package com.liuqi.ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置
 *
 * @author  LiuQi 2024/8/1-16:13
 * @version V1.0
 **/
@Configuration
public class WebsocketConfiguration implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Bean
    public ServerEndpointExporter getExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(WebSocketServer.class);
        return exporter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebsocketConfiguration.applicationContext = applicationContext;
    }
}
