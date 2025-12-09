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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理Servlet
 * 处理用户的查询、修改、重置密码等操作
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();
    private OperationLogDAO logDAO = new OperationLogDAO();

    // CORS头由CORSFilter统一处理

    /**
     * 处理GET请求：查询用户列表或单个用户
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // 查询用户列表
            listUsers(request, response);
        } else {
            // 查询单个用户
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.parseInt(idStr);
                getUserById(id, response);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的用户ID");
            }
        }
    }

    /**
     * 处理PUT请求：更新用户信息
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        Integer currentUserId = (Integer) session.getAttribute("userId");

        // 获取路径参数
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.error(response, 400, "请指定要更新的用户ID");
            return;
        }

        // 判断是否是重置密码请求
        if (pathInfo.contains("/reset-password")) {
            // 从路径中提取用户ID: /2/reset-password -> 2
            String idStr = pathInfo.substring(1, pathInfo.indexOf("/reset-password"));
            try {
                Integer id = Integer.parseInt(idStr);
                resetPassword(id, request, response, currentUserId);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的用户ID");
            }
            return;
        }

        String action = request.getParameter("action");

        if ("batchUpdateStatus".equals(action)) {
            // 批量更新状态
            batchUpdateStatus(request, response, currentUserId);
        } else {
            // 更新用户信息
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.parseInt(idStr);
                updateUser(id, request, response, currentUserId);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的用户ID");
            }
        }
    }

    /**
     * 查询用户列表
     */
    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取查询参数
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        String keyword = request.getParameter("keyword");

        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 10;

        // 查询数据
        List<User> users = userDAO.findAll(page, pageSize, keyword);
        int total = userDAO.count(keyword);

        // 移除密码信息
        for (User user : users) {
            user.setPassword(null);
        }

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);

        ResponseUtil.success(response, "查询成功", data);
    }

    /**
     * 根据ID查询用户
     */
    private void getUserById(Integer id, HttpServletResponse response) throws IOException {
        User user = userDAO.findById(id);

        if (user != null) {
            user.setPassword(null);
            ResponseUtil.success(response, "查询成功", user);
        } else {
            ResponseUtil.error(response, 404, "用户不存在");
        }
    }

    /**
     * 更新用户信息
     */
    private void updateUser(Integer id, HttpServletRequest request, HttpServletResponse response, Integer currentUserId) throws IOException {
        // 从 JSON请求体中读取参数
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        @SuppressWarnings("unchecked")
        Map<String, String> params = gson.fromJson(requestBody, Map.class);

        String role = params != null ? params.get("role") : null;
        String status = params != null ? params.get("status") : null;

        // 参数验证
        if (role == null && status == null) {
            ResponseUtil.error(response, 400, "请至少指定一个要更新的字段");
            return;
        }

        // 查询原用户信息
        User user = userDAO.findById(id);
        if (user == null) {
            ResponseUtil.error(response, 404, "用户不存在");
            return;
        }

        // 更新用户信息
        String newRole = role != null ? role : user.getRole();
        String newStatus = status != null ? status : user.getStatus();

        boolean success = userDAO.updateRoleAndStatus(id, newRole, newStatus);

        if (success) {
            logDAO.insert(currentUserId, "UPDATE_USER", "更新用户ID: " + id);
            ResponseUtil.success(response, "更新用户成功");
        } else {
            ResponseUtil.error(response, 500, "更新用户失败");
        }
    }

    /**
     * 重置用户密码
     */
    private void resetPassword(Integer id, HttpServletRequest request, HttpServletResponse response, Integer currentUserId) throws IOException {
        // 从 JSON请求体中读取密码
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        @SuppressWarnings("unchecked")
        Map<String, String> params = gson.fromJson(requestBody, Map.class);

        String newPassword = params != null ? params.get("newPassword") : null;

        if (newPassword == null || newPassword.isEmpty()) {
            ResponseUtil.error(response, 400, "新密码不能为空");
            return;
        }

        // 验证用户是否存在
        User user = userDAO.findById(id);
        if (user == null) {
            ResponseUtil.error(response, 404, "用户不存在");
            return;
        }

        // 重置密码（实际应用中应该加密）
        boolean success = userDAO.resetPassword(id, newPassword);

        if (success) {
            logDAO.insert(currentUserId, "RESET_PASSWORD", "重置用户ID: " + id + " 的密码");
            ResponseUtil.success(response, "重置密码成功");
        } else {
            ResponseUtil.error(response, 500, "重置密码失败");
        }
    }

    /**
     * 批量更新用户状态
     */
    private void batchUpdateStatus(HttpServletRequest request, HttpServletResponse response, Integer currentUserId) throws IOException {
        String idsStr = request.getParameter("ids");
        String status = request.getParameter("status");

        if (idsStr == null || idsStr.isEmpty() || status == null || status.isEmpty()) {
            ResponseUtil.error(response, 400, "请指定用户ID和状态");
            return;
        }

        // 解析ID数组
        String[] idStrs = idsStr.split(",");
        Integer[] ids = new Integer[idStrs.length];
        try {
            for (int i = 0; i < idStrs.length; i++) {
                ids[i] = Integer.parseInt(idStrs[i].trim());
            }
        } catch (NumberFormatException e) {
            ResponseUtil.error(response, 400, "无效的用户ID");
            return;
        }

        // 批量更新
        int updatedCount = userDAO.batchUpdateStatus(ids, status);

        if (updatedCount > 0) {
            logDAO.insert(currentUserId, "BATCH_UPDATE_USER_STATUS", "批量更新用户状态，数量: " + updatedCount);
            ResponseUtil.success(response, "批量更新成功，共更新 " + updatedCount + " 条记录");
        } else {
            ResponseUtil.error(response, 500, "批量更新失败");
        }
    }
}
