package com.yolomusic.servlet;

import com.yolomusic.dao.DashboardDAO;
import com.yolomusic.util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * 简单的统计数据接口
 * 映射到 /api/stats
 */
@WebServlet("/api/stats")
public class StatsServlet extends HttpServlet {

    private DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("===== StatsServlet 收到请求 =====");
        
        // 检查登录
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("未登录，返回401");
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        try {
            // 获取统计数据
            Map<String, Object> stats = dashboardDAO.getStatistics();
            System.out.println("统计数据获取成功: " + stats);
            ResponseUtil.success(response, "查询成功", stats);
        } catch (Exception e) {
            System.err.println("获取统计数据失败: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.error(response, 500, "获取统计数据失败");
        }
    }
}
