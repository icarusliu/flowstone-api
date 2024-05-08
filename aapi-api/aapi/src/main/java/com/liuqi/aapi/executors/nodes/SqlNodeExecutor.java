package com.liuqi.aapi.executors.nodes;

import cn.hutool.db.ds.druid.DruidDSFactory;
import com.github.freakchick.orange.engine.DynamicSqlEngine;
import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiNode;
import com.liuqi.aapi.bean.dto.DsDTO;
import com.liuqi.aapi.executors.ConnectionPool;
import com.liuqi.aapi.executors.NodeExecutor;
import com.liuqi.aapi.service.DsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.jdbc.datasource.ConnectionHolder;

import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.util.Map;

/**
 * SQL节点执行器
 *
 * @author LiuQi 16:16
 **/
@Slf4j
public class SqlNodeExecutor implements NodeExecutor {
    @Override
    public Object execute(ApiContext context, ApiNode node) {
        Map<String, Object> config = node.getConfig();
        String dsId = MapUtils.getString(config, "dsId");
        String sql = MapUtils.getString(config, "sql");

        // 获取数据源配置
        DsService dsService = context.getApplicationContext().getBean(DsService.class);
        DsDTO ds = dsService.findById(dsId).orElseThrow(() -> new RuntimeException("数据源配置不存在"));

        // 获取SqlSession
        Connection connection = ConnectionPool.getConnection(ds);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(new Configuration());
        SqlSession sqlSession = sqlSessionFactory.openSession(connection);


        // 执行动态SQL，支持MyBatis语法
        DynamicSqlEngine engine = new DynamicSqlEngine();
        String finalSql = engine.parse(sql, node.getParams()).getSql();
        if (log.isDebugEnabled()) {
            log.debug("Final sql: {}", finalSql);
        }

        return null;
    }
}
