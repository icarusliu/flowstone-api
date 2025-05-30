package com.liuqi.common.utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态SQL实现
 *
 * @author LiuQi 2024/8/8-11:44
 * @version V1.0
 **/
@Slf4j
public class DynamicSqlHelper {
    private static final XMLLanguageDriver xmlLangDriver = new XMLLanguageDriver();
    private static final Hashtable<String, String> sqlCache = new Hashtable<>();

    /**
     * 批量插入
     * @param key 关键字
     * @param sql 执行的SQL
     * @param dataList 数据列表
     * @return 插入记录数
     */
    public static Object batchInsert(String key, String sql, List<Map<String, Object>> dataList) {
        if (StringUtils.isBlank(sql) || CollectionUtils.isEmpty(dataList)) {
            return null;
        }

        // 添加执行脚本到上下文中
        SqlSessionFactory sqlSessionFactory = SqlHelper.FACTORY;
        MybatisConfiguration configuration = (MybatisConfiguration) sqlSessionFactory.getConfiguration();
        synchronized (configuration.getMappedStatements()) {
            // 后续可以考虑做成自动刷新，而不是每次执行的时候刷新
            configuration.getMappedStatementNames().removeIf(s -> s.equals(key));
            configuration.addMappedStatement(createMappedStatement(configuration, sql, key));
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            dataList.forEach(data -> {
                sqlSession.insert(key, data);
            });
            sqlSession.flushStatements();
        }

        return dataList.size();
    }

    /**
     * 执行SQL语句
     * 支持分号分隔的多条语句
     * @param key mybatis 语句key
     * @param sql SQL语句
     * @param params 参数
     * @return 执行结果
     */
    public static Object executeSql(String key, String sql, Map<String, Object> params) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }

        // 执行SQL语句，按;进行分隔分别执行，需要防止出现 &lt;&gt;&quot;&apos;&amp;这种情况；
        String[] sqls = sql.split("(?<!&lt|&gt|&quot|&apos|&amp);");
        Object result = null;
        for (int i = 0; i < sqls.length; i++) {
            String subSql = sqls[i].trim();
            if (StringUtils.isBlank(subSql)) {
                continue;
            }

            result = executeSqlInternal(key + "_" + i, subSql, params);
        }

        return result;
    }

    /**
     * 执行SQL语句
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 执行结果
     */
    private static Object executeSqlInternal(String key, String sql, Map<String, Object> params) {
        // 添加执行脚本到上下文中
        SqlSessionFactory sqlSessionFactory = SqlHelper.FACTORY;
        MybatisConfiguration configuration = (MybatisConfiguration) sqlSessionFactory.getConfiguration();
        if (configuration.hasStatement(key)) {
            synchronized (configuration.getMappedStatements()) {
                configuration.addMappedStatement(createMappedStatement(configuration, sql, key));
                sqlCache.put(key, sql);
            }
        } else {
            String cachedSql = sqlCache.get(key);
            if (!StringUtils.equals(cachedSql, sql)) {
                synchronized (configuration.getMappedStatements()) {
                    configuration.getMappedStatementNames().removeIf(key::equals);
                    configuration.addMappedStatement(createMappedStatement(configuration, sql, key));
                    sqlCache.put(key, sql);
                }
            }
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            if (sql.matches(".*update .* set .*")) {
                // 更新
                return sqlSession.update(key, params);
            } else if (sql.contains("insert into ")) {
                // 插入，如果是批量插入时，需要进行分批处理，防止数据量过大
                // 每次最多处理500条，并且只处理只有一个参数的场景
                if (null != params && params.size() == 1) {
                    int batchSize = 500;
                    for (String s : params.keySet()) {
                        Object obj = params.get(s);
                        if (obj instanceof List) {
                            List list = (List) obj;

                            // 如果list为空，不需要执行
                            if (list.isEmpty()) {
                                log.warn("待插入数据为空");
                                return 0;
                            }

                            if (list.size() < batchSize) {
                                // 小于500时，不用进行分批处理
                                break;
                            } else {
                                // 需要进行分批处理
                                int processedSize = 0;
                                Map<String, Object> subParams = new HashMap<>(16);
                                int result = 0;
                                while (processedSize < list.size()) {
                                    int endSize = Math.min(processedSize + batchSize, list.size());
                                    subParams.put(s, list.subList(processedSize, endSize));
                                    result += sqlSession.insert(key, subParams);
                                    processedSize += (endSize - processedSize);
                                }
                                return result;
                            }
                        }
                    }
                }

                return sqlSession.insert(key, params);
            } else if (sql.contains("delete from")) {
                return sqlSession.delete(key, params);
            }

            // 如果包含有分页参数，进行分页查询
            if (params.containsKey("pageNo") && params.containsKey("pageSize")) {
                int page = MapUtils.getIntValue(params, "pageNo", 1);
                int size = MapUtils.getIntValue(params, "pageSize", 10);

                PageHelper.startPage(page, size);

                List<Map<String, Object>> list = sqlSession.selectList(key, params);
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
                IPage<Map<String, Object>> result = new Page<>((long) page * size, size);
                result.setTotal(pageInfo.getTotal());
                result.setRecords(pageInfo.getList());
                if (log.isDebugEnabled()) {
                    log.debug("查询结果：{}", result);
                }

                list = result.getRecords().stream().map(item -> {
                    // key转换
                    Map<String, Object> map = new HashMap<>(16);
                    item.forEach((k, v) -> {
                        k = StringUtils.underlineToCamel(k);
                        map.put(k, v);
                    });
                    return map;
                }).collect(Collectors.toList());
                result.setRecords(list);

                return result;
            }

            // 查询
            List<Map<String, Object>> map = sqlSession.selectList(key, params);

            return map.stream().map(item -> {
                Map<String, Object> newMap = new HashMap<>(16);
                item.forEach((k, v) -> {
                    k = StringUtils.underlineToCamel(k);
                    newMap.put(k, v);
                });
                return newMap;
            }).collect(Collectors.toList());
        }
    }

    private static Map<String, Object> sqlMap(String sql, IPage page, Object... args) {
        Map<String, Object> sqlMap = CollectionUtils.newHashMapWithExpectedSize(2);
        sqlMap.put("page", page);
        sqlMap.put("sql", StringUtils.sqlArgsFill(sql, args));
        return sqlMap;
    }


    /**
     * 动态创建SQL表达式
     *
     * @param configuration MyBatis配置
     * @param sql           SQL脚本
     * @param statementId   脚本id
     * @return SQL执行表达式
     */
    private static MappedStatement createMappedStatement(Configuration configuration, String sql, String statementId) {
        Class<?> parameterType = Map.class;
        String commandStatement = "<script>" + sql + "</script>";
        SqlSource sqlSource = xmlLangDriver.createSqlSource(configuration, commandStatement, parameterType);

        ResultMap resultMap = new ResultMap.Builder(configuration, statementId + "-Inline", Map.class, Collections.emptyList()).build();
        ParameterMap parameterMap = new ParameterMap.Builder(configuration, statementId + "-Inline", Map.class, Collections.emptyList()).build();
        SqlCommandType commandType = SqlCommandType.SELECT;
        return new MappedStatement.Builder(configuration, statementId, sqlSource, commandType)
                .resultMaps(Collections.singletonList(resultMap))
                .parameterMap(parameterMap).build();
    }
}
