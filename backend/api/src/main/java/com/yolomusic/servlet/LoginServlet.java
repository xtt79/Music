package com.yolomusic.servlet;

import com.yolomusic.dao.OperationLogDAO;
import com.yolomusic.dao.UserDAO;
import com.yolomusic.entity.User;
import com.yolomusic.util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录Servlet
 * 处理用户登录和退出功能
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private OperationLogDAO logDAO = new OperationLogDAO();

    // CORS头由CORSFilter统一处理

    /**
     * 处理登录请求
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置请求编码
        request.setCharacterEncoding("UTF-8");

        // 获取请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("收到登录请求 - 用户名: " + username + ", 密码: " + (password != null ? "[已设置]" : "null"));

        // 参数验证
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            ResponseUtil.error(response, 400, "用户名和密码不能为空");
            return;
        }

        // 查询用户
        User user = userDAO.findByUsernameAndPassword(username, password);

        if (user != null) {
            // 登录成功，创建Session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            // 记录操作日志
            logDAO.insert(user.getId(), "LOGIN", "系统登录");

            // 返回用户信息（不包含密码）
            user.setPassword(null);
            ResponseUtil.success(response, "登录成功", user);

            System.out.println("登录成功 - 用户ID: " + user.getId() + ", 用户名: " + user.getUsername());
        } else {
            // 登录失败
            ResponseUtil.error(response, 401, "用户名或密码错误");
            System.out.println("登录失败 - 用户名或密码错误");
        }
    }

    /**
     * 处理退出登录请求
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            Integer userId = (Integer) session.getAttribute("userId");

            // 记录操作日志
            if (userId != null) {
                logDAO.insert(userId, "LOGOUT", "系统退出");
            }

            // 销毁Session
            session.invalidate();
        }

        ResponseUtil.success(response, "退出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                user.setPassword(null);
                ResponseUtil.success(response, "获取成功", user);
                return;
            }
        }

        ResponseUtil.error(response, 401, "未登录");
    }
}