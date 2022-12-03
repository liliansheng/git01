package com.yun.note.web;

import com.yun.note.dao.UserDao;
import com.yun.note.po.User;
import com.yun.note.service.UserService;
import com.yun.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
@WebServlet("/user")
@MultipartConfig//文件上传的注解
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置个人中心（项目栏）高亮
        request.setAttribute("menu_page","user");
        //接收用户行为
        String actionName = request.getParameter("actionName");
        //判断用户行为，调用对应的方法 (如果是登陆行为)
        if ("login".equals(actionName)){
            //用户登陆
            userLogin(request,response);
        }else if ("logout".equals(actionName)){
            //退出登陆
          userLogout(request,response);
        }else if ("userCenter".equals(actionName)){
           //进入个人中心
            userCenter(request,response);
        }else if ("userHead".equals(actionName)){
            userHead(request,response);
        }else if ("checkNick".equals(actionName)){
            checkNick(request,response);
        }else if ("updateUser".equals(actionName)){
            updateUser(request,response);
        }



    }


    /**
     * 修改用户信息
     注：文件上传必须在Servlet类上提那家注解！！！ @MultipartConfig
     1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
     2. 将resultInfo对象存到request作用域中
     3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
     * @param request
     * @param response
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo<User> resultInfo = userService.updateUser(request);
        request.setAttribute("resultInfo",resultInfo);
        request.getRequestDispatcher("user?actionName=userCenter").forward(request,response);

    }


    /**
     *昵称唯一性校验
            1. 获取参数（昵称）
            2. 从session作用域获取用户对象，得到用户ID
            3. 调用Service层的方法，得到返回的结果
            4. 通过字符输出流将结果响应给前台的ajax的回调函数
            5. 关闭资
     * @param request
     * @param response
     */
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取参数（昵称）
        String nick = request.getParameter("nick");
        // 2. 从session作用域获取用户对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
        Integer code = userService.checkNick(nick,user.getUserId());
       // 4. 通过字符输出流将结果响应给前台的ajax的回调函数
        response.getWriter().write(code + "");
       // 5. 关闭资源
        response.getWriter().close();

    }

    /**
     * 加载头像
          前台：
              设置img标签的src属性，请求后台加载头像
                  src="user?actionName=userHead&imageName=图片名称" （通过el表达式熊session中获取）

          后台：
              1. 获取参数 （图片名称）
              2. 得到图片的存放路径 （request.getServletContext().getealPathR("/")）
              3. 通过图片的完整路径，得到file对象
              4. 通过截取，得到图片的后缀
              5. 通过不同的图片后缀，设置不同的响应的类型
              6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
     * @param request
     * @param response
     */
    private void userHead(HttpServletRequest request, HttpServletResponse response) {
        // 1. 获取参数 （图片名称）
        String headName = request.getParameter("headName");
        // 2. 得到图片的存放路径 （request.getServletContext().getRealPath("/")）
        String realPath = request.getServletContext().getRealPath("/WEB-INF/upload/");
        // 3. 通过图片的完整路径，得到file对象
        File file = new File(realPath + "/" + headName);
        // 4. 通过截取，得到图片的后缀
        String pic = realPath.substring(realPath.lastIndexOf(".") + 1);
        // 5. 通过不同的图片后缀，设置不同的响应的类型
        if ("PNG".equalsIgnoreCase(pic)) {
            response.setContentType("image/png");
        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            response.setContentType("image/jpeg");
        } else if ("GIF".equalsIgnoreCase(pic)) {
            response.setContentType("image/gif");
        }
        // 6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
        try {
            FileUtils.copyFile(file,response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     进入个人中心
     *  1. 设置首页动态包含的页面值
     *  2. 请求转发跳转到index.jsp
     * @param request
     * @param response
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse response) {
        //1. 设置首页动态包含的页面值
        request.setAttribute("changePage","user/info.jsp");
        //2. 请求转发跳转到index.jsp
        try {
            request.getRequestDispatcher("index.jsp").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 退出登陆（方法）
     *  前端：
     *         设置超链接的请求地址  user?actionName=logout
     *     后台：
     *         1. 销毁Session对象
     *         2. 删除Cookie对象
     *         3. 重定向跳转到登录页面
     * @param request
     * @param response
     */
    private void userLogout(HttpServletRequest request, HttpServletResponse response) {
        // 1. 销毁Session对象
        request.getSession().invalidate();

        // 2. 删除Cookie对象
        Cookie cookie = new Cookie("user",null);
        //设置为0，表示删除cookie
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 3. 重定向跳转到登录页面
        try {
            response.sendRedirect("login.jsp");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *登陆
     Web层：（控制层：接收参数、响应数据）
     1. 获取参数 （姓名、密码）
     2. 调用Service层的方法，返回ResultInfo对象
     3. 判断是否登录成功
         如果失败
            将resultInfo对象设置到request作用域中
            请求转发跳转到登录页面
         如果成功
            将用户信息设置到session作用域中
            判断用户是否选择记住密码（rem的值是1）
                如果是,将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
                如果否,清空原有的cookie对象
            重定向跳转到index页面
     * @param request
     * @param response
     */
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 获取参数 （姓名、密码）
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");
        // 2. 调用Service层的方法，返回ResultInfo对象
        ResultInfo<User> resultInfo = userService.userLogin(userName,userPwd);
        //3. 判断是否登录成功
        if (resultInfo.getCode() == 1){//如果成功
        // 将用户信息(姓名、密码)设置到session作用域中
            request.getSession().setAttribute("user",resultInfo.getResult());
        // 判断用户是否选择记住密码（rem的值是1）
           String rem = request.getParameter("rem");
        // 如果是,将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
            if ("1".equals(rem)){
                //得到Cookie对象
                Cookie cookie = new Cookie("user",userName + "-" + userPwd);
                // 设置失效时间
                cookie.setMaxAge(3*24*60*60);
                //响应给客户端
                response.addCookie(cookie);
            }else {
                // 如果否,清空原有的cookie对象(上面的user在session里面，下面的user在cookie里面，所以不冲突)
                 Cookie cookie = new Cookie("user",null);
                // 删除cookie，设置maxage为0
                cookie.setMaxAge(0);
            }
            // 重定向跳转到index页面
            response.sendRedirect("index");

        }else {//如果失败
            // 将resultInfo对象设置到request作用域中
            // (如果把前台的校验关掉，这里存了返回信息，页面也不显示，所以前台也要把信息取出来)
            request.setAttribute("resultInfo",resultInfo);
            // 请求转发跳转到登录页面
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }
}
