package com.ozdemo.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     *
     * @param statementId
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 查询单个
     *
     * @param statementId
     * @param params
     * @param <T>
     * @return
     */
    public <T> T selectOne(String statementId, Object... params) throws Exception;

    /**
     * 插入数据
     *
     * @param statementId
     * @return
     */
    int insert(String statementId) throws Exception;

    /**
     * 插入数据
     *
     * @param statementId
     * @param params
     * @return
     */
    int insert(String statementId, Object... params) throws Exception;

    /**
     * 更新数据
     *
     * @param statementId
     * @return
     */
    int update(String statementId) throws Exception;

    /**
     * 更新数据
     *
     * @param statementId
     * @param params
     * @return
     */
    int update(String statementId, Object... params) throws Exception;

    /**
     * 删除数据
     *
     * @param statementId
     * @return
     */
    int delete(String statementId)throws Exception;

    /**
     * 删除数据
     *
     * @param statementId
     * @param params
     * @return
     */
    int delete(String statementId, Object... params) throws Exception;

    /**
     * Dao动态代理
     *
     * @param mapperClass
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<?> mapperClass) throws Exception;

    /**
     * 提交
     */
    public void commit();

    /**
     * 回滚
     */
    public void rollback();

    /**
     * 关闭splSession连接
     */
    public void close();
}
