package com.liuqi.common.utils;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQL辅助类
 *
 * @author LiuQi 2025/3/11-15:26
 * @version V1.0
 **/
@Slf4j
public class SqlParserUtils {
    /**
     * 解析SQL语句中使用或影响的表清单
     * @param sql sql语句，支持；分隔的多个语句
     * @return 解析结果
     */
    public static Pair<Set<String>, Set<String>> parseTableNames(String sql) {
        Set<String> querySourceTables = new HashSet<>();
        Set<String> tableNames = new HashSet<>(16);
        Pair<Set<String>, Set<String>> result = new Pair<>(querySourceTables, tableNames);
        if (StringUtils.isBlank(sql)) {
            return result;
        }

        String[] sqlStatements = sql.split(";");
        for (String singleSql : sqlStatements) {
            singleSql = singleSql.trim()
                    .replaceAll("^--.*$", "")
                    .replaceAll("/\\*.*?\\*/", "");
            if (singleSql.isEmpty()) {
                continue;
            }

            try {
                Statement statement = CCJSqlParserUtil.parse(singleSql);
                if (statement instanceof Select selectStatement) {
                    Select selectBody = selectStatement.getPlainSelect();
                    collectQuerySourceTablesFromSelectBody(selectBody, querySourceTables);
                    // 处理 WITH 子句
                    if (selectStatement.getWithItemsList() != null) {
                        processWithItems(selectStatement.getWithItemsList(), querySourceTables);
                    }
                } else if (statement instanceof Update updateStatement) {
                    tableNames.add(updateStatement.getTable().getName());
                    FromItem fromItem = updateStatement.getFromItem();
                    if (fromItem != null) {
                        collectQuerySourceTablesFromFromItem(fromItem, querySourceTables);
                    }
                } else if (statement instanceof Insert insertStatement) {
                    tableNames.add(insertStatement.getTable().getName());
                    if (insertStatement.getSelect() != null) {
                        collectQuerySourceTablesFromSelectBody(insertStatement.getSelect(), querySourceTables);
                    }
                    List<WithItem> withItemsList = insertStatement.getWithItemsList();
                    processWithItems(withItemsList, querySourceTables);
                } else if (statement instanceof Delete deleteStatement) {
                    tableNames.add(deleteStatement.getTable().getName());
                }
            } catch (JSQLParserException e) {
                log.error("解析SQL表信息异常", e);
            }
        }
        return result;
    }

    /**
     * 处理with的情况
     * @param withItems with列表清单
     * @param querySourceTables 源表名清单
     */
    private static void processWithItems(List<WithItem> withItems, Set<String> querySourceTables) {
        if (null == withItems) {
            return;
        }

        for (WithItem withItem : withItems) {
            if (withItem.getSelect() != null) {
                collectQuerySourceTablesFromSelectBody(withItem.getSelect().getPlainSelect(), querySourceTables);
            }
            // 移除 CTE 别名
            Alias alias = withItem.getAlias();
            if (null != alias) {
                querySourceTables.remove(alias.getName());
            }
        }
    }

    private static void collectQuerySourceTablesFromSelectBody(Select selectBody, Set<String> querySourceTables) {
        if (selectBody instanceof PlainSelect plainSelect) {
            FromItem fromItem = plainSelect.getFromItem();
            if (fromItem != null) {
                collectQuerySourceTablesFromFromItem(fromItem, querySourceTables);
            }
            List<Join> joins = plainSelect.getJoins();
            if (joins != null) {
                for (Join join : joins) {
                    collectQuerySourceTablesFromFromItem(join.getRightItem(), querySourceTables);
                }
            }
        } else if (selectBody instanceof SetOperationList setOperationList) {
            List<Select> selectBodies = setOperationList.getSelects();
            for (Select subSelectBody : selectBodies) {
                collectQuerySourceTablesFromSelectBody(subSelectBody, querySourceTables);
            }
        } else if (selectBody instanceof ParenthesedSelect select) {
            FromItem fromItem = select.getPlainSelect().getFromItem();
            if (null != fromItem) {
                collectQuerySourceTablesFromFromItem(fromItem, querySourceTables);
            }

            List<Join> joins = select.getPlainSelect().getJoins();
            if (null != joins) {
                for (Join join : joins) {
                    collectQuerySourceTablesFromFromItem(join.getRightItem(), querySourceTables);
                }
            }
        }
    }

    private static void collectQuerySourceTablesFromFromItem(FromItem fromItem, Set<String> querySourceTables) {
        if (fromItem instanceof Table table) {
            querySourceTables.add(table.getName());
        } else if (fromItem instanceof Select subSelect) {
            collectQuerySourceTablesFromSelectBody(subSelect.getPlainSelect(), querySourceTables);
        }
    }

    public static void main(String[] args) {
        String complexSql = "SELECT * FROM employees e JOIN (SELECT * FROM departments) d ON e.department_id = d.department_id " +
                "WHERE e.salary > 5000; " +
                "-- select * from a;" +
                "UPDATE products SET price = price * 1.1 WHERE category = 'electronics'; " +
                "INSERT INTO orders (order_id, customer_id) VALUES (1, 101); " +
                "DELETE FROM customers WHERE customer_id = 1;" +
                "with t as (select * from test) select * from t; " +
                "insert into test_insert select * from (select * from test_select) tmp; ";
        complexSql = """
                with t as (select * from test1 left join test2 on a = b left join (select * from test3) on a = c), 
                b as (select * from (select * from test5) t) insert into result select * from t left join (select a from test4) on a = b
                """;
        Pair<Set<String>, Set<String>> tableNames = parseTableNames(complexSql);
        System.out.println("查询来源表: " + tableNames.getKey());
        System.out.println("写入/更新/删除表: " + tableNames.getValue());
    }
}
