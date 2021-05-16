package com.ozdemo.test;

import java.util.List;

public interface IUserDao {

    public List<User> findAll() throws Exception;

    public User findOne(User user) throws Exception;

    public int insertOne(User user) throws Exception;

    public int updateOne(User user) throws Exception;

    public int deleteById(int uid) throws Exception;
}
