package com.yun.note;

import com.yun.note.util.DBUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDB {

    // 使用日志工厂类，记入日志
    private static Logger logger = LoggerFactory.getLogger(DBUtil.class);
    /**
     * 单元测试类
     * 1、方法的返回值建议用void，一般没有返回值
     * 2、参数列表建议空参，一般没有参数
     * 3、方法上需设置@Test
     * 4、每个方法都可独立运行
     */
    @Test
    public void  testConnection(){
        System.out.println(DBUtil.getConnection());
        /*使用日志，有不同级别的日志debug,info*/
        logger.info("获取数据库连接" + DBUtil.getConnection());
        logger.info("获取数据库连接{},大括号在哪里，后面的参数就显示在哪里" ,DBUtil.getConnection());
        logger.info("在{}时，获取数据库连接", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
