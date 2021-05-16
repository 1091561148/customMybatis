package com.ozdemo.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.ozdemo.io.Resources;
import com.ozdemo.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {
    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 该方法就是使用dom4j对配置文件进行解析，封装Configuration
     * @param in
     * @return
     */
    public Configuration parseConfig(InputStream in) throws DocumentException, PropertyVetoException {
        Document document = new SAXReader().read(in);
        //Configuration 解析
        Element rootElement = document.getRootElement();
        List<Element> porpetyList = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for (Element element : porpetyList){
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name,value);
        }
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("user"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(comboPooledDataSource);

        //mapper.xml 解析: 拿到路径，加载成字节输入流，在通过dom4j解析
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (Element element : mapperList){
            String mapperPath = element.attributeValue("resource");
            InputStream resourceAsSteam = Resources.getResourceAsSteam(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAsSteam);
        }

        return configuration;
    }
}
