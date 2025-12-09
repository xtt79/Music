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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘Servlet
 * 提供仪表盘统计数据
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/dashboard/*")
public class DashboardServlet extends HttpServlet {

    private DashboardDAO dashboardDAO = new DashboardDAO();

    // CORS头由CORSFilter统一处理

    /**
     * 处理GET请求：获取仪表盘数据
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("DashboardServlet: Session检查失败");
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        String pathInfo = request.getPathInfo();
        System.out.println("DashboardServlet 收到请求，pathInfo='" + pathInfo + "'");

        // 简单匹配，只要不是其他路径就返回统计数据
        if (pathInfo == null || pathInfo.equals("/") || 
            pathInfo.equals("/statistics") || 
            pathInfo.equalsIgnoreCase("/statistics")) {
            // 获取统计数据
            System.out.println("✅ 处理统计数据请求");
            getStatistics(response);
        } else if (pathInfo.contains("genre-distribution")) {
            // 获取流派分布
            System.out.println("✅ 处理流派分布请求");
            getGenreDistribution(response);
        } else if (pathInfo.contains("user-growth")) {
            // 获取用户增长趋势
            System.out.println("✅ 处理用户增长请求");
            getUserGrowthTrend(response);
        } else {
            System.out.println("❌ 未知路径: " + pathInfo);
            ResponseUtil.error(response, 404, "接口不存在: " + pathInfo);
        }
    }

    /**
     * 获取统计数据
     */
    private void getStatistics(HttpServletResponse response) throws IOException {
        try {
            Map<String, Object> stats = dashboardDAO.getStatistics();
            System.out.println("DashboardServlet: 获取到统计数据 - " + stats);
            ResponseUtil.success(response, "查询成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.error(response, 500, "获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取流派分布数据
     */
    private void getGenreDistribution(HttpServletResponse response) throws IOException {
        try {
            List<Map<String, Object>> distribution = dashboardDAO.getGenreDistribution();
            System.out.println("DashboardServlet: 获取到流派分布数据 - 数量: " + distribution.size());
            ResponseUtil.success(response, "查询成功", distribution);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.error(response, 500, "获取流派分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户增长趋势
     */
    private void getUserGrowthTrend(HttpServletResponse response) throws IOException {
        try {
            List<Map<String, Object>> trend = dashboardDAO.getUserGrowthTrend();
            System.out.println("DashboardServlet: 获取到用户增长趋势 - 数量: " + trend.size());
            ResponseUtil.success(response, "查询成功", trend);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.error(response, 500, "获取用户增长趋势失败: " + e.getMessage());
        }
    }
}