package com.ozdemo.sqlSession;

import com.ozdemo.config.BoundSql;
import com.ozdemo.pojo.Configuration;
import com.ozdemo.pojo.MappedStatement;
import com.ozdemo.utils.DBconnection;
import com.ozdemo.utils.GenericTokenParser;
import com.ozdemo.utils.ParameterMapping;
import com.ozdemo.utils.ParameterMappingTokenHandler;
import org.apache.log4j.helpers.ThreadLocalMap;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor {

    /**
     * 查询数据
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        //1、注册驱动，获取连接
        //2、获取Sql语句 然后转换sql语句，并对#{}里面的值进行解析存储
        //3、获取预处理对象： preparedStatement
        //4、设置参数
        PreparedStatement preparedStatement = prepareStatement(configuration, mappedStatement, params);

        //5、执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();

        //6、封装返回结果集
        while (resultSet.next()) {
            //获取元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            Object o = resultTypeClass.newInstance();
            for (int j = 1; j <= metaData.getColumnCount(); j++) {
                //字段名
                String columnName = metaData.getColumnName(j);
                //字段值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }

    /**
     * 更新数据（添加、删除、更新）
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @return
     */
    public int update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //1、注册驱动，获取连接
        //2、获取Sql语句 然后转换sql语句，并对#{}里面的值进行解析存储
        //3、获取预处理对象： preparedStatement
        //4、设置参数
        PreparedStatement preparedStatement = prepareStatement(configuration, mappedStatement, params);
        //5、执行sql
        preparedStatement.executeUpdate();
        //6、返回更新的条数
        return preparedStatement.getUpdateCount();
    }

    /**
     * 准备预处理对象
     * <p>
     * 1、注册驱动，获取连接
     * 2、获取Sql语句 然后转换sql语句，并对#{}里面的值进行解析存储
     * 3、获取预处理对象： preparedStatement
     * 4、设置参数
     *
     * @param configuration
     * @param mappedStatement
     * @param params
     * @return
     * @throws Exception
     */
    private PreparedStatement prepareStatement(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        //1、注册驱动，获取连接
        Connection connection = DBconnection.getCurrConnection(configuration);

        //2、获取Sql语句 然后转换sql语句，并对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        //3、获取预处理对象： preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        //4、设置参数
        String paramterType = mappedStatement.getParamsType();
        Class<?> paramterTypeClass = getClassType(paramterType);

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        int i = 0;
        List<String> parameterValues = new ArrayList<>();
        for (ParameterMapping parameterMapping : parameterMappings) {
            String content = parameterMapping.getContent();
            Object value;
            //根据类 类型判断参数，可定义一个非自定义类的类 Type 来做判断，Mybatis中使用TypeHandlerRegistry
            if (paramterTypeClass == Integer.class) {
                value = params[0];
            } else {
                //反射
                Field field = paramterTypeClass.getDeclaredField(content);
                //暴力访问
                field.setAccessible(true);
                value = field.get(params[0]);
            }
            parameterValues.add(i,String.valueOf(value));
            preparedStatement.setObject(i + 1, value);
            i++;
        }
        System.out.println("执行的StatementId："+ mappedStatement.getId());
        System.out.println("执行的Sql："+ getQueryString(parameterValues,boundSql.getSqlText()));
        return preparedStatement;
    }

    /**
     * 打印Sql语句
     *
     * @param parameterValues
     * @param sql
     * @return
     */
    private String getQueryString(List<String> parameterValues, String sql) {
        int len = sql.length();
        StringBuffer t = new StringBuffer(len * 2);

        if (parameterValues != null) {
            int i = 0, limit = 0, base = 0;

            while ((limit = sql.indexOf('?', limit)) != -1) {
                t.append(sql.substring(base, limit));
                t.append(parameterValues.get(i));
                i++;
                limit++;
                base = limit;
            }
            if (base < len) {
                t.append(sql.substring(base));
            }
        }
        return t.toString();
    }

    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if (paramterType != null) {
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
        return null;
    }

    /**
     * 解析sql
     * 完成对#{}的解析工作： 1、将#{}使用？代替，2、解析出#{}中的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析过后的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql();
        boundSql.setSqlText(parseSql);
        boundSql.setParameterMappings(parameterMappings);
        return boundSql;
    }
}
