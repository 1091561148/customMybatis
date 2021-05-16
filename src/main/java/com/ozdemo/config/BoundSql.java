package com.ozdemo.config;

import com.ozdemo.utils.ParameterMapping;

import java.util.List;

/**
 * 一次可执行的sql封装
 */
public class BoundSql {

    private String sqlText;
    private List<ParameterMapping> parameterMappings;

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }
}
