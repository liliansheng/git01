package com.yun.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 *  将resultInfo对象转成JSON格式字符串，响应给ajax的回调函数
 */
public class JsonUtil {
    public static void toJson(HttpServletResponse response,Object result){

        try {
            //将resultInfo对象转成JSON格式字符串，响应给ajax的回调函数
            //设置响应类型及编码格式（JSON类型的）
            response.setContentType("application/json;charset=UTF-8");
            //获得字符输出流
            PrintWriter out = response.getWriter();
            //通过fast json方法，将resultInfo对象转成JSON格式字符串
            String json = JSON.toJSONString(result);
            //通过输出流将json类型的字符串输出
            out.write(json);
            //关流资源
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
