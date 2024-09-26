package com.liuqi.dua.executor;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.druid.DruidDataSourceCreator;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.service.db.DbMetadataHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 动态数据源配置
 *
 * @author LiuQi 2024/8/9-11:46
 * @version V1.0
 **/
@Component
@Slf4j
public class DynamicDsConfigService {
    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private DruidDataSourceCreator dataSourceCreator;

    @Autowired
    private DbMetadataHelper dbMetadataHelper;

    /**
     * 加载单个数据源
     *
     * @param ds 数据源配置
     */
    public void loadDs(DsDTO ds) {
        String url = ds.getUrl();
        if (ds.getType().equals("sqlserver")) {
            if (!url.contains("encrypt")) {
                url += ";encrypt=false";
            }
        }

        String code = ds.getCode();
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setUrl(url);
        dataSourceProperty.setUsername(ds.getUsername());
        dataSourceProperty.setPassword(ds.getPassword());
        dataSourceProperty.setDriverClassName(dbMetadataHelper.getDriverClass(ds.getType()));
        try {
            dynamicRoutingDataSource.addDataSource(code, dataSourceCreator.createDataSource(dataSourceProperty));
        } catch (Exception ex) {
            log.error("加载数据源失败", ex);
        }
    }

    public void removeDs(String ds) {
        dynamicRoutingDataSource.removeDataSource(ds);
    }
}
