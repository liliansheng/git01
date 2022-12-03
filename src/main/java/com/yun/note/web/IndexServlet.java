package com.yun.note.web;

import com.yun.note.po.Note;
import com.yun.note.po.User;
import com.yun.note.service.NoteService;
import com.yun.note.util.Page;
import com.yun.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置首页导航高亮
        request.setAttribute("menu_page","index");

        //得到用户行为（判断用户是什么条件查询，标题查询、日期查询、类型查询）
        String actionName = request.getParameter("actionName");
        //将用户行为设置到request设置到作用域中（分布导航中需要获取，用于分页查询的区分）
        request.setAttribute("action",actionName);
        //判断用户行为
        if ("searchTitle".equals(actionName)){
            //得到条件查询：标题
            String title = request.getParameter("title");
            //设置（搜索框）回显数据
            request.setAttribute("title",title);
            //标题搜索
            noteList(request,response,title,null,null);
        }else if ("searchDate".equals(actionName)){
            //得到条件查询：日期
            String date = request.getParameter("date");
            //设置回显数据
            request.setAttribute("date",date);
            //日期搜索
            noteList(request,response,null,date,null);

        }else if ("searchType".equals(actionName)){
            //得到条件查询：类型
            String typeId = request.getParameter("typeId");
            //设置（搜索框）回显数据
            request.setAttribute("typeId",typeId);
            //类型搜索
            noteList(request,response,null,null,typeId);
        }

        else {
            //不做条件查询
            //分页查询云记列表(设置分页)
            noteList(request,response,null,null,null);
        }


       //设置首页动态包含的页面
       request.setAttribute("changePage","note/list.jsp");
       //请求转发到index.jsp
       request.getRequestDispatcher("index.jsp").forward(request,response);
    }

    /**
      分页查询云记列表
               1. 接收参数 （当前页、每页显示的数量）
               2. 获取Session作用域中的user对象
               3. 调用Service层查询方法，返回Page对象
               4. 将page对象设置到request作用域中
     * @param request
     * @param response
     * @param title 条件查询的参数：标题（查询）
     */
    private void noteList(HttpServletRequest request, HttpServletResponse response,String title,String date,String typeId) {

        // 1. 接收参数 （当前页、每页显示的数量）
         String pageNum = request.getParameter("pageNum");
         String pageSize = request.getParameter("pageSize");
        // 2. 获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
        // 3. 调用Service层查询方法，返回Page对象
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
        // 4. 将page对象设置到request作用域中
        request.setAttribute("page",page);

        // 通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
        // 设置集合存放在request作用域中(每跳转一个页面都是一次新的请求，所以信息放入session中保存)
        request.getSession().setAttribute("dateInfo",dateInfo);

        // 通过类型分组查询当前登录用户下的云记数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        // 设置集合存放在request作用域中
        request.getSession().setAttribute("typeInfo",typeInfo);
        // 设置集合存放在request作用域中


    }
}
