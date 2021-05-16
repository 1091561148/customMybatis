package com.ozdemo;

import com.ozdemo.io.Resources;
import com.ozdemo.sqlSession.SqlSession;
import com.ozdemo.sqlSession.SqlSessionFactory;
import com.ozdemo.sqlSession.SqlSessionFactoryBuilder;
import com.ozdemo.test.IUserDao;
import com.ozdemo.test.User;
import org.junit.Test;

import java.io.InputStream;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream inputStream = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //查询
        System.out.println("------------------ 查询 -------------------");
        User user = new User();
        user.setUid(1);
        user.setUsername("张三");

        IUserDao iUserDao = sqlSession.getMapper(IUserDao.class);
        User resUser1 = iUserDao.findOne(user);
        if(resUser1 == null){
            System.out.println("查询结果：null" );
        }else {
            System.out.println("查询结果：" + resUser1.toString());
        }
        //新增
        System.out.println();
        System.out.println("------------------ 新增 -------------------");
        User user1 = new User();
        user1.setUsername("XX7");
        int res1 = iUserDao.insertOne(user1);
        System.out.println("添加成功条数："+ res1);

        //修改
        System.out.println();
        System.out.println("------------------ 修改 -------------------");
        User user2 = new User();
        user2.setUid(1);
        user2.setUsername("XX-update");
        int res2 = iUserDao.updateOne(user2);
        System.out.println("更新成功条数："+ res2);

        //删除
        System.out.println();
        System.out.println("------------------ 删除 -------------------");
        int res3 = iUserDao.deleteById(6);
        System.out.println("删除成功条数："+ res3);
    }
}
