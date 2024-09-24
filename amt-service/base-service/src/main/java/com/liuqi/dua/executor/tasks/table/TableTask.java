package com.liuqi.dua.executor.tasks.table;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.base.bean.query.FilterOp;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.executor.AbstractDagTask;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.bean.NodeParam;
import com.liuqi.dua.service.DsService;
import liquibase.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表节点类型
 * 用于执行对应的ETL任务
 *
 * @author LiuQi 2024/8/6-12:35
 * @version V1.0
 **/
@Slf4j
public class TableTask extends AbstractDagTask<TableNodeConfig> {
    public TableTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    @Override
    public Object executeInternal() {
        TableNodeConfig config = this.nodeConfig;
        String table = config.getTable();
        String ds = config.getDs();

        // 生成待执行的SQL语句
        String sql = generateSql(table, config);

        // 切换数据源
        DynamicDataSourceContextHolder.push(ds);

        try {
            String key = "dagTableTask-" + this.executorContext.getApiId()
                    + "-" + this.getId().getNodeInfo().getId();

            // 处理分页参数
            Map<String, Object> params = new HashMap<>(16);
            if (null != config.getPageable() && config.getPageable()) {
                NodeParam pageNoParam = config.getPageNo();
                Object pageNo = this.getNodeParamValue(pageNoParam).getKey();
                Object pageSize = this.getNodeParamValue(config.getPageSize()).getKey();
                if (null != pageNo) {
                    params.put("pageNo", pageNo);
                    params.put("pageSize", pageSize);
                }
            }

            log.debug("待执行SQL：{}", sql);
            this.info("待执行SQL", sql);
            this.info("执行参数", params);

            return DynamicSqlHelper.executeSql(key, sql, params);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 根据表名及查询条件等生成SQL语句
     * 暂仅支持生成查询语句
     *
     * @param table  表名
     * @param config 配置信息
     * @return 待执行的SQL语句
     */
    private String generateSql(String table, TableNodeConfig config) {
        // 组装SQL语句
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ")
                .append(table)
                .append(" where 1 = 1 ");
        List<NodeParam> filters = config.getParams();

        // 获取字段信息
        List<Map<String, Object>> fields = this.getTableFields(this.nodeConfig.getDs(), table);
        Map<String, Map<String, Object>> fieldMap = fields.stream()
                .collect(Collectors.toMap(map -> MapUtils.getString(map, "columnName"),s -> s));

        if (!CollectionUtils.isEmpty(filters)) {
            filters.forEach(filter -> {
                // key存储字段
                String column = filter.getKey();
                column = StringUtil.toKabobCase(column).replaceAll("-", "_");
                FilterOp op = filter.getOp();

                // 查询条件
                if (op == FilterOp.NULL) {
                    sb.append(" and ")
                            .append(column)
                            .append(" is null ");
                    return;
                } else if (op == FilterOp.NOT_NULL) {
                    sb.append(" and ")
                            .append(column)
                            .append(" is not null ");
                    return;
                } else if (op == FilterOp.AND || op.equals(FilterOp.OR)) {
                    // 只处理一级查询条件
                    return;
                }

                Pair<Object, Object> filterValue = getNodeParamValue(filter);
                Object value = filterValue.getKey();
                Object value1 = filterValue.getValue();

                if (null == value || (null == value1 && op.equals(FilterOp.BETWEEN))) {
                    return;
                }

                // 获取字段是否是字符串类型
                Map<String, Object> fieldInfo = fieldMap.get(column);
                String columnType = MapUtils.getString(fieldInfo, "columnType");
                boolean isChar = columnType.startsWith("char") || columnType.startsWith("varchar")
                        || columnType.contains("text") || columnType.contains("date") || columnType.contains("time");

                sb.append(" and ")
                        .append(column);

                // 字符串需要转换
                if (isChar && !(value instanceof Map) && !(value instanceof Collection<?>) && op != FilterOp.IN) {
                    value = "'" + value + "'";
                }

                if (isChar && null != value1 && !(value1 instanceof Number)) {
                    value1 = "'" + value1 + "'";
                }

                switch (op) {
                    case IN -> {
                        Collection<?> list;
                        if (value instanceof String) {
                            // 如果值是字符串时，需要进行处理
                            list = Arrays.stream(((String) value).split(","))
                                    .toList();
                        } else {
                            list = (Collection<?>) value;
                        }

                        if (CollectionUtils.isEmpty(list)) {
                            return;
                        }

                        sb.append(" in (");
                        String str = list.stream()
                                .map(Object::toString)
                                .map(item -> {
                                    if (!isChar) {
                                        return item;
                                    }

                                    return "'" + item + "'";
                                })
                                .reduce((s1, s2) -> s1 + "," + s2)
                                .orElse("");
                        sb.append(str)
                                .append(")");
                    }
                    case EQ -> sb.append("=").append(value);
                    case LIKE -> sb.append(" like %'")
                            .append(value)
                            .append("%");
                    case NEQ -> sb.append(" &lt;&gt; ").append(value);
                    case BETWEEN -> sb.append(" between ")
                            .append(value)
                            .append(" and ")
                            .append(value1);
                    case LT -> sb.append(" &lt; ").append(value);
                    case LE -> sb.append(" &lt;= ").append(value);
                    case GT -> sb.append(" &gt; ").append(value);
                    case GE -> sb.append(" &gt;= ").append(value);
                }
                sb.append(" ");
            });
        }

        return sb.toString();
    }

    /**
     * 查找表字段列表
     *
     * @param ds    数据源
     * @param table 表名
     * @return 表对应的字段列表
     */
    public List<Map<String, Object>> getTableFields(String ds, String table) {
        DsService dsService = this.executorContext.getApplicationContext().getBean(DsService.class);
        return dsService.getTableFieldsFull(ds, table);
    }
}
