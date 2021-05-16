package com.ozdemo.sqlSession;

import com.ozdemo.pojo.Configuration;
import com.ozdemo.pojo.MappedStatement;
import com.ozdemo.type.SqlCmdType;
import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    private boolean autoCommit;

    public DefaultSqlSession() {
        autoCommit = true;
    }

    public DefaultSqlSession(boolean autoCommit){
        this.autoCommit = autoCommit;
    }

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        //将要去完成对SimpleExecutor里的query方法调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.query(configuration, mappedStatement, params);
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects == null || objects.size() == 0) {
            return null;
        }
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("Too many result sets!size:"+ objects.size());
        }
    }

    /**
     * 插入数据
     *
     * @param statementId
     * @return int
     */
    @Override
    public int insert(String statementId) throws Exception {
        return update(statementId, (Object)null);
    }

    /**
     * 插入数据
     *
     * @param statementId
     * @param params
     * @return
     */
    @Override
    public int insert(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    /**
     * 更新数据
     *
     * @param statementId
     * @return
     */
    @Override
    public int update(String statementId) throws Exception {
        return update(statementId, (Object)null);
    }

    /**
     * 更新数据
     *
     * @param statementId
     * @param params
     * @return
     */
    @Override
    public int update(String statementId, Object... params) throws Exception {
        //将要去完成对SimpleExecutor里的query方法调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(configuration, mappedStatement, params);
    }

    /**
     * 删除数据
     *
     * @param statementId
     * @return
     */
    @Override
    public int delete(String statementId) throws Exception {
        return update(statementId, (Object)null);
    }

    /**
     * 删除数据
     *
     * @param statementId
     * @param params
     * @return
     */
    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //使用JDK动态代理来为Dao接口生产代理对象，并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //底层都是去执行JDBC代码，根据不同情况来调用不同的方法
                //准备参数1：statementId sql语句的唯一标识：namespace.id = 接口全限定名.方法名
                //方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                //准备参数2 ： params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();

                //判断sqlCmdType
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                SqlCmdType sqlCmdType = mappedStatement.getSqlCmdType();
                switch (sqlCmdType) {
                    case SELECT:
                        // 判断是否进行了 泛型类型参数化
                        if (genericReturnType instanceof ParameterizedType) {
                            return selectList(statementId, args);
                        }
                        return selectOne(statementId, args);
                    case INSERT:
                        if (args != null && args.length > 0) {
                            return insert(statementId, args);
                        }
                        return insert(statementId);
                    case UPDATE:
                        if (args != null && args.length > 0) {
                            return update(statementId, args);
                        }
                        return update(statementId);
                    case DELETE:
                        if (args != null && args.length > 0) {
                            return delete(statementId, args);
                        }
                        return delete(statementId);
                    default:
                        throw new RuntimeException("SqlCmdType unknown!sqlCmdType:" + sqlCmdType);
                }
            }
        });

        return (T) proxyInstance;
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }
}
