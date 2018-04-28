/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secant.match.main;

import com.secant.match.utility.BaseUtility;
import com.secant.match.service.Matcher;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Manjut
 */
public class Start {

    public static void main(String[] args) throws IOException {
        final Log log = LogFactory.getLog(Start.class);
        final ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        log.info("Application Started...");
        Matcher matcher = (Matcher) ctx.getBean("getMatcher");
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:application.properties");
        Properties properties = new Properties();
        log.debug("after properties :");
        properties.load(resource.getInputStream());
        BaseUtility.prop = properties;
        log.debug("properties :" + properties);
        //Matcher matcher = new Matcher();
        matcher.startMatching();
    }
}
