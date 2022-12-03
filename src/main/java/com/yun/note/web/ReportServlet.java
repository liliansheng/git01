package com.yun.note.web;

import com.yun.note.po.User;
import com.yun.note.service.NoteService;
import com.yun.note.util.JsonUtil;
import com.yun.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置导航高亮
        request.setAttribute("menu_page","report");
        //判断用户行为（如果是info则进入到数据报表页面）
        String actionName = request.getParameter("actionName");
        if ("info".equals(actionName)){
            //进入报表页面 (执行reportInfo方法，跳转到首页并设置页面包含数据报表页面)
            reportInfo(request,response);
        }else if ("month".equals(actionName)){
            // 通过月份查询对应的云记数量
            queryNoteContByMonth(request,response);
        }



    }

    /**
     * 通过月份查询对应的云记数量
     * @param request
     * @param response
     */
    private void queryNoteContByMonth(HttpServletRequest request, HttpServletResponse response) {
        // 从session作用域中获取user对象
        User user = (User) request.getSession().getAttribute("user");
        // 调用service层的方法，得到resultInfo（集合）对象
        ResultInfo<Map<String,Object>> resultInfo = noteService.queryNoteContByMonth(user.getUserId());
        // 将ResultInfo对象转换成JSON格式的字符串，响应给AJAX的回调函数
        JsonUtil.toJson(response,resultInfo);

    }

    /**
     * 进入数据报表页面（首页包含数据报表页面）
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置（首页）包含的页面
        request.setAttribute("changePage","report/info.jsp");
        //跳转到首页
        request.getRequestDispatcher("index.jsp").forward(request,response);


    }
}
