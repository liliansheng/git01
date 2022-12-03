package com.yun.note;

import com.yun.note.dao.BaseDao;
import com.yun.note.dao.UserDao;
import com.yun.note.po.User;
import com.yun.note.util.DBUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUser {
    @Test
    public void testQueryUserByName(){
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());
    }

    @Test
    public void testAdd(){
        //定义sql语句
        String sql = "insert into tb_user (uname,upwd,nick,head,mood) values(?,?,?,?,?)";
        //设置参数集合
        List<Object> params = new ArrayList();
        params.add("lisi");
        params.add("e10adc3949ba59abbe56e057f20f883e");
        params.add("lisi");
        params.add("404.jpg");
        params.add("Hello");
        int row = BaseDao.executeUpdate(sql,params);
        System.out.println(row);



    }
}
