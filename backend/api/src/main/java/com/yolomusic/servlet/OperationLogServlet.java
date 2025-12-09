package com.yolomusic.servlet;

import com.yolomusic.dao.OperationLogDAO;
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
 * 操作日志Servlet
 * 处理操作日志的查询
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/log/*")
public class OperationLogServlet extends HttpServlet {

    private OperationLogDAO logDAO = new OperationLogDAO();

    // CORS头由CORSFilter统一处理

    /**
     * 处理GET请求：查询操作日志
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        // 获取查询参数
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        String operationType = request.getParameter("operationType");
        String userIdStr = request.getParameter("userId");

        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 10;
        Integer userId = userIdStr != null ? Integer.parseInt(userIdStr) : null;

        // 查询数据
        List<Map<String, Object>> logs = logDAO.findAll(page, pageSize, operationType, userId);
        int total = logDAO.count(operationType, userId);

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", logs);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);

        ResponseUtil.success(response, "查询成功", data);
    }
}
