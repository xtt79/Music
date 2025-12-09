package com.yolomusic.dao;

import com.yolomusic.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据访问对象
 * 处理仪表盘统计数据的查询
 * 
 * @author YOLOMusic Team
 */
public class DashboardDAO {
    
    /**
     * 获取仪表盘统计数据
     * 
     * @return 统计数据Map
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            
            // 总用户数
            String userSql = "SELECT COUNT(*) as count FROM user";
            pstmt = conn.prepareStatement(userSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("totalUsers", rs.getInt("count"));
            }
            
            // 活跃用户数
            String activeUserSql = "SELECT COUNT(*) as count FROM user WHERE status = 'active'";
            pstmt = conn.prepareStatement(activeUserSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("activeUsers", rs.getInt("count"));
            }
            
            // 总音乐数
            String musicSql = "SELECT COUNT(*) as count FROM music";
            pstmt = conn.prepareStatement(musicSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("totalMusic", rs.getInt("count"));
            }
            
            // 总歌单数
            String playlistSql = "SELECT COUNT(*) as count FROM playlist";
            pstmt = conn.prepareStatement(playlistSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("totalPlaylists", rs.getInt("count"));
            }
            
            // 总播放量
            String playCountSql = "SELECT COALESCE(SUM(play_count), 0) as total FROM music";
            pstmt = conn.prepareStatement(playCountSql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("totalPlayCount", rs.getLong("total"));
            } else {
                stats.put("totalPlayCount", 0L);
            }
            
        } catch (SQLException e) {
            System.err.println("获取统计数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return stats;
    }
    
    /**
     * 获取音乐流派分布数据
     * 
     * @return 流派分布列表
     */
    public List<Map<String, Object>> getGenreDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT genre, COUNT(*) as count FROM music WHERE genre IS NOT NULL AND genre != '' GROUP BY genre ORDER BY count DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("genre", rs.getString("genre"));
                item.put("count", rs.getInt("count"));
                distribution.add(item);
            }
        } catch (SQLException e) {
            System.err.println("获取流派分布失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return distribution;
    }
    
    /**
     * 获取用户增长趋势数据（最近7天）
     * 
     * @return 用户增长数据列表
     */
    public List<Map<String, Object>> getUserGrowthTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                        "FROM user " +
                        "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                        "GROUP BY DATE(created_at) " +
                        "ORDER BY date ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("date", rs.getString("date"));
                item.put("count", rs.getInt("count"));
                trend.add(item);
            }
        } catch (SQLException e) {
            System.err.println("获取用户增长趋势失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return trend;
    }
}

