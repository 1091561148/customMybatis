package com.ozdemo.config;

import com.ozdemo.pojo.Configuration;
import com.ozdemo.pojo.MappedStatement;
import com.ozdemo.type.SqlCmdType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * @author 今昔何夕
 */
class XMLMapperBuilder {

    private Configuration configuration;

    XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 解析mapper.xml
     * 每条sql都会生成MappedStatement对象，并存入Configuration
     *
     * @param inputStream
     * @throws DocumentException
     */
    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");
        List<Element> contexts = rootElement.elements();
        if(null != contexts && contexts.size() > 0){
            for (Element element : contexts){
                String nodeName = element.getName();
                String id = element.attributeValue("id");
                String resultType = element.attributeValue("resultType");
                String paramterType = element.attributeValue("paramterType");
                String sqlText = element.getTextTrim();
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setSqlCmdType(SqlCmdType.valueOf(nodeName.toUpperCase(Locale.ENGLISH)));
                mappedStatement.setId(id);
                mappedStatement.setResultType(resultType);
                mappedStatement.setParamsType(paramterType);
                mappedStatement.setSql(sqlText);
                String key = namespace + "." + id;
                configuration.getMappedStatementMap().put(key,mappedStatement);
            }
        }
    }
}
