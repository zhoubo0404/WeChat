package com.zhoubo.listener;

import com.zhoubo.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * web项目启动动态加载log配置文件
 *
 * @author qizhen
 * @date 2015-6-10
 */
public class LogListener implements ServletContextListener {
    private static Logger log = LoggerFactory.getLogger(LogListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        MDC.put("projectName", event.getServletContext().getServletContextName());
        LogUtil.init(event.getServletContext().getServletContextName());
        log.info("Log 配置已加载");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
