package com.liuqi.base.service.data;

import liquibase.util.StringUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 动态查询
 *
 * @author 不空军 15:34
 **/
@Data
public class DynamicQuery {
    public static DynamicQuery create() {
        return new DynamicQuery();
    }

    private Long pageNo;

    private Long pageSize;

    private List<Filter> filters = new ArrayList<>(16);

    public DynamicQuery addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public DynamicQuery addFilter(String key, String value, FilterOp op) {
        Filter filter = new Filter();
        filter.setOp(op);
        filter.setKey(key);
        filter.setValue(value);
        return this.addFilter(filter);
    }

    public DynamicQuery eq(String key, Object value) {
        if (null == value || "".equals(value)) {
            return this;
        }

        Filter filter = new Filter();
        filter.setOp(FilterOp.EQ);
        filter.setKey(key);
        filter.setValue(value);
        return this.addFilter(filter);
    }

    public DynamicQuery neq(String key, Object value) {
        if (null == value || "".equals(value)) {
            return this;
        }

        Filter filter = new Filter();
        filter.setOp(FilterOp.NEQ);
        filter.setKey(key);
        filter.setValue(value);
        return this.addFilter(filter);
    }

    public DynamicQuery in(String key, Collection<?> collection) {
        if (null == collection || collection.isEmpty()) {
            return this;
        }

        Filter filter = new Filter();
        filter.setOp(FilterOp.IN);
        filter.setKey(key);
        filter.setValue(collection);
        return this.addFilter(filter);
    }

    private String filterToSql(Filter filter,
                               Map<String, Object> params) {
        FilterOp op = Optional.ofNullable(filter.getOp()).orElse(FilterOp.EQ);
        String key = filter.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        List<Filter> subFilters = filter.getFilters();
        if (!CollectionUtils.isEmpty(subFilters)) {
            //存在子查询
            Stream<String> stream = subFilters.stream()
                    .map(subFilter -> filterToSql(subFilter, params))
                    .filter(StringUtils::isNotBlank);
            String subSqls = null;
            if (op == FilterOp.EQ || op == FilterOp.AND) {
                subSqls = stream.collect(Collectors.joining(" and "));
            } else if (op == FilterOp.OR) {
                subSqls = stream.collect(Collectors.joining(" or "));
            }

            if (StringUtils.isNotBlank(subSqls)) {
                sb.append("(").append(subSqls).append(")");
            }
            return sb.toString();
        }

        key = StringUtil.toKabobCase(key).replaceAll("-", "_");
        if (op == FilterOp.NULL) {
            sb.append(key).append(" is null");
            return sb.toString();
        } else if (op == FilterOp.NOT_NULL) {
            sb.append(key).append(" is not null");
            return sb.toString();
        }

        Object value = filter.getValue();
        Object value1 = filter.getValue1();
        if (null == value ) {
            return null;
        }

        String paramKey = "param-" + params.size();
        params.put(paramKey, value);
        sb.append(key);
        switch (op) {
            case IN -> sb.append(" in #{").append(paramKey).append("}");
            case EQ -> sb.append(" = #{").append(paramKey).append("}");
            case LIKE -> sb.append(" like concat('%', #{").append(paramKey).append("}, '%')");
            case NEQ -> sb.append(" &lt;&gt; #{").append(paramKey).append("}");
            case BETWEEN -> {
                String param1Key = "param-" + params.size();
                params.put(param1Key, value1);
                sb.append(" between #{").append(paramKey).append("} and #{").append(param1Key).append("}");
            }
            case LT -> sb.append(" &lt; #{").append(paramKey).append("}");
            case LE -> sb.append(" &le; #{").append(paramKey).append("}");
            case GT -> sb.append(" &gt; #{").append(paramKey).append("}");
            case GE -> sb.append(" &ge; #{").append(paramKey).append("}");
        }

        return sb.toString();
    }

    public Pair<String, Map<String, Object>> toWhereSql() {
        Map<String, Object> params = new HashMap<>(16);
        List<Filter> filters = this.getFilters();
        if (CollectionUtils.isEmpty(filters)) {
            return Pair.of("", params);
        }

        String sql = filters.stream()
                .map(filter -> filterToSql(filter, params))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" and "));
        if (StringUtils.isBlank(sql)) {
            return Pair.of("", params);
        }

        return Pair.of("(" + sql + ")", params);
    }
}
