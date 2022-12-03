package com.yun.note.dao;

import com.mysql.cj.xdevapi.Result;
import com.yun.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础的JDBC操作类
 *      更新操作 （添加、修改、删除）
 *      查询操作
 *          1. 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
 *          2. 查询集合
 *          3. 查询某个对象
 */
public class BaseDao {
    /**
     更新操作
       (添加、修改、删除)
           1. 得到数据库连接
           2. 定义sql语句 （添加语句、修改语句、删除语句）
           3. 预编译
           4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
           5. 执行更新，返回受影响的行数
           6. 关闭资源

     *  注：需要两个参数:sql语句、所需参数的集合
     * @param sql
     * @param params
     * @return
     */
    public static int executeUpdate(String sql, List<Object> params){
        //更新操作没有返回结果（result），只有返回的受影响行数
        int row = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            //1. 得到数据库连接
            connection = DBUtil.getConnection();
            // 定义sql语句，这里的sql语句是传过来的参数，就不用在这里编写sql语句了
            //3. 预编译
            preparedStatement = connection.prepareStatement(sql);
            //4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            //sql点位符从第一个开始设置，放在集合里的参数从第0位开始取
            if (params != null && params.size()>0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
          //  5. 执行更新，返回受影响的行数
            row = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //调用方法，关闭资源
            DBUtil.close(null,preparedStatement,connection);
        }
        return row;
    }

    /**
     查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
         1. 得到数据库连接
         2. 定义sql语句
         3. 预编译
         4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
         5. 执行查询，返回结果集
         6. 判断并分析结果集
         7. 关闭资源

     注：需要两个参数:sql语句、所需参数的集合
     * @return
     */
    public static Object findSingleValue(String sql,List<Object> params){
        Object object = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
           //  1. 得到数据库连接
            connection = DBUtil.getConnection();
           // 3. 预编译
            preparedStatement = connection.prepareStatement(sql);
           // 4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            if (params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
           // 5. 执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();
            //6. 判断并分析结果集(因为只有一个结果，所以取下标1就好了)
            if (resultSet.next()){
                object = resultSet.getObject(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //7. 关闭资源
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        //7、返回查询结果
        return object;
    }

    /**
     查询集合 （JavaBean中的字段与数据库中表的字段对应）
        1. 获取数据库连接
        2. 定义SQL语句
        3. 预编译
        4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
        5. 执行查询，得到结果集
        6. 得到结果集的元数据对象（查询到的字段数量以及查询了哪些字段）
        7. 判断并分析结果集
            8. 实例化对象
            9. 遍历查询的字段数量，得到数据库中查询到的每一个列名
            10. 通过反射，使用列名得到对应的field对象
            11. 拼接set方法，得到字符串
            12. 通过反射，将set方法的字符串反射成类中的指定set方法
            13. 通过invoke调用set方法
            14. 将对应的JavaBean设置到集合中
        15. 关闭资源
     */
    public static List queryRows(String sql,List<Object> params,Class cls){
        List list = new ArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            //  1. 得到数据库连接
            connection = DBUtil.getConnection();
            // 3. 预编译
            preparedStatement = connection.prepareStatement(sql);
            // 4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
            if (params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            // 5. 执行查询，返回结果集
            resultSet = preparedStatement.executeQuery();
            // 6. 得到结果集的元数据对象（查询到的字段数量以及查询了哪些字段）
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            // 得到查询的字段数量
            int filedNum = resultSetMetaData.getColumnCount();

            //7. 判断并分析结果集
            while (resultSet.next()){
                // 8. 实例化对象
                Object object = cls.newInstance();
                // 9. 遍历查询的字段数量，得到数据库中查询到的每一个列名
                for (int i = 1; i <= filedNum; i++) {
                    // 得到查询的每一个列名
                    // getColumnLabel()：获取列名或别名
                    // getColumnName()：获取列名
                    String columnName = resultSetMetaData.getColumnLabel(i);
                    // 10. 通过反射，使用列名得到对应的field对象
                    Field field = cls.getDeclaredField(columnName);
                    // 11. 拼接set方法，得到字符串
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);
                    // 12. 通过反射，将set方法的字符串反射成类中的指定set方法 (要有方法名和参数类型)
                    Method method = cls.getDeclaredMethod(setMethod,field.getType());
                    //13得到查询的每一个对应的值
                    Object value = resultSet.getObject(columnName);
                    // 14. 通过invoke调用set方法
                    method.invoke(object, value);
                }
                // 15. 将对应的JavaBean设置到集合中
                list.add(object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //15. 关闭资源
        DBUtil.close(resultSet,preparedStatement,connection);
        }
        //
        return list;
    }

    /**
     *查询某一个对象，查询的结果也是集合，只不过查询的是集合的第一条记录而已（调用上面的方法，拿第一条数据就好）
     */
    public static Object queryRow(String sql,List<Object> paras,Class cls){
        List list = BaseDao.queryRows(sql, paras, cls);
        Object object = null;
        // 如果集合不为空，则获取查询的第一条数据
        if (list !=null && list.size() > 0){
            object = list.get(0);
        }

        return  object;
    }
}
