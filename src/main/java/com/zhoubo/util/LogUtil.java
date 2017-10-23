package com.zhoubo.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 日志工具类
 *
 * @author qizhen
 * @date 2015-6-09
 */
public class LogUtil {

    /**
     * 工程名称标志,用于日志中传递工程名称
     */
    public static final String PROJECT_NAME = "projectName";

    public static Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static String getProjectName() {
        String dir = System.getProperty("user.dir");
        return dir.substring(dir.lastIndexOf(File.separator) + 1);
    }

    public static void init() {
        init(null);
    }

    public static void init(String projectName) {
        System.err.println("logConfig loading ..");
        MDC.put("projectName", projectName == null ? getProjectName() : projectName);
        InputStream is = null;
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            is = loadConfigXml();
            configurator.doConfigure(is);
        } catch (JoranException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        log.info("logConfig init OK ");
    }

    /**
     * 创建log配置文件流
     *
     * @return
     * @throws IOException
     */
    private static InputStream loadConfigXml() throws IOException {
        InputStream in = null;
        URL url = LogUtil.class.getProtectionDomain().getCodeSource().getLocation();
        String homePath = "";
        try {
            homePath = new File(url.toURI()).getParent();
        } catch (URISyntaxException ignored) {
        }
        File temp = new File(homePath + File.separator + "log" + File.separator + "logback.xml");
        if (temp.exists()) {
            in = new FileInputStream(temp);
            System.err.println("Load log Config from file logback.xml");
        } else {
            temp = new File(homePath + File.separator + "log" + File.separator + "base_logback.xml");
            if (temp.exists()) {
                in = new FileInputStream(temp);
                System.err.println("Load log Config from file base_logback.xml");
            }
        }

        if (in == null) {
            try {
                in = LogUtil.class.getResourceAsStream("/log/logback.xml");
                System.err.println("Load log Config from jar logback.xml");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 如果本地没有logback.xml才启用默认配置，如果本地存在启动本地配置
            if (in == null) {
                // 通过基础log配置实现基本配置项，并实现不同环境设置相关参数
                in = LogUtil.class.getResourceAsStream("/log/base_logback.xml");
                System.err.println("Load log Config from jar base_logback.xml");
            }
        }
        return in;
    }

    public static void main(String[] args) {
        init();
        log.info("sss");
        log.error("///////////////////");
    }

}
