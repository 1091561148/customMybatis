package com.ozdemo.pojo;

import com.ozdemo.type.SqlCmdType;

/**
 * mapper.xml 属性对象
 */
public class MappedStatement {

    /* sql命令类型 */
    private SqlCmdType sqlCmdType;

    /* id标识 */
    private String id;

    //返回类型
    private String resultType;

    //参数类型
    private String paramsType;

    //SQL语句
    private String sql;

    public SqlCmdType getSqlCmdType() {
        return sqlCmdType;
    }

    public void setSqlCmdType(SqlCmdType sqlCmdType) {
        this.sqlCmdType = sqlCmdType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParamsType() {
        return paramsType;
    }

    public void setParamsType(String paramsType) {
        this.paramsType = paramsType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
