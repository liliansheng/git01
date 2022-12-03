package com.yun.note.dao;

import com.yun.note.po.User;
import com.yun.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 Dao层：（数据访问层：数据库中的增删改查操作）
 通过用户名查询用户对象， 返回用户对象
     1. 获取数据库连接
     2. 定义sql语句
     3. 预编译
     4. 设置参数
     5. 执行查询，返回结果集
     6. 判断并分析结果集
     7. 关闭资源
 */
public class UserDao {

    /*根据姓名查询所有信息（使用封装的方法）*/
    public static User queryUserByName(String uname){
        //定义sql语句
        String sql = "select * from tb_user where uname = ?";
        //设置参数集合
        List<Object> list = new ArrayList<>();
        list.add(uname);
        User user = (User) BaseDao.queryRow(sql, list, User.class);

        return user;
    }

    /**
     * 校验昵称唯一
         1. 定义SQL语句
             通过用户ID查询除了当前登录用户之外是否有其他用户使用了该昵称
             指定昵称  nick （前台传递的参数）
             当前用户  userId （session作用域中的user对象）
                String sql = "select * from tb_user where nick = ? and userId != ?";
         2. 设置参数集合
         3. 调用BaseDao的查询方法
     * @param nick
     * @param userId
     * @return
     */
    public static User queryUserByNickAndUserId(String nick, Integer userId) {

            String sql = "select * from tb_user where nick = ? and userId != ?";
            List<Object> params = new ArrayList<>();
            params.add(nick);
            params.add(userId);
            User user = (User) BaseDao.queryRow(sql,params,User.class);
            return user;
    }


    /*根据姓名查询所有信息*/
    public User queryUserByName2(String uname){
        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            //  1. 获取数据库连接 (是用的工具类的一个方法来获取连接)
            connection = DBUtil.getConnection();
            //  2. 定义sql语句（一个问号是一个点位符）
            String sql = "select * from tb_user where uname = ?";
            //  3. 预编译
            preparedStatement = connection.prepareStatement(sql);
            //  4. 设置参数  (给第一个占位符传一个userName参数)
            preparedStatement.setString(1,uname);
            //  5. 执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();

            //  6. 判断并分析结果集
            if (resultSet.next()){
                user = new User();
                user.setUserId(resultSet.getInt("userId"));
                /*因为是用名字来查询的，返回的也必然是这个（无论与数据库中的是否一样）*/
                user.setUname(uname);
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
                user.setNick(resultSet.getString("nick"));
                user.setUpwd(resultSet.getString("upwd"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //  7. 关闭资源
         DBUtil.close(resultSet,preparedStatement,connection);
        }



        return user;
    }


    /**
     * 通过用户ID修改用户信息
     1. 定义SQL语句
     String sql = "update tb_user set nick = ?, mood = ?, head = ? where userId = ? ";
     2. 设置参数集合
     3. 调用BaseDao的更新方法，返回受影响的行数
     4. 返回受影响的行数
     * @param user
     * @return
     */
    public int updateUser(User user) {
        // 1. 定义SQL语句
        String sql = "update tb_user set nick = ?, mood = ?, head = ? where userId = ? ";
        // 2. 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        // 3. 调用BaseDao的更新方法，返回受影响的行数
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }
}
