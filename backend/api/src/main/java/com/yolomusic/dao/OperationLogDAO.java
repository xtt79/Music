package com.yolomusic.dao;

import com.yolomusic.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志数据访问对象
 * 处理操作日志相关的数据库操作
 * 
 * @author YOLOMusic Team
 */
public class OperationLogDAO {
    
    /**
     * 添加操作日志
     * 
     * @param userId 操作用户ID
     * @param operationType 操作类型
     * @param operationTarget 操作对象
     * @return 是否添加成功
     */
    public boolean insert(Integer userId, String operationType, String operationTarget) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO operation_log (user_id, operation_type, operation_target) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, operationType);
            pstmt.setString(3, operationTarget);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("添加操作日志失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 查询操作日志（分页、搜索、筛选）
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param operationType 操作类型筛选
     * @param userId 用户ID筛选
     * @return 日志列表
     */
    public List<Map<String, Object>> findAll(int page, int pageSize, String operationType, Integer userId) {
        List<Map<String, Object>> logs = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT ol.*, u.username, u.nickname " +
                "FROM operation_log ol " +
                "LEFT JOIN user u ON ol.user_id = u.id " +
                "WHERE 1=1"
            );
            
            // 添加筛选条件
            if (operationType != null && !operationType.isEmpty()) {
                sql.append(" AND ol.operation_type = ?");
            }
            
            if (userId != null) {
                sql.append(" AND ol.user_id = ?");
            }
            
            sql.append(" ORDER BY ol.created_at DESC LIMIT ? OFFSET ?");
            
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (operationType != null && !operationType.isEmpty()) {
                pstmt.setString(paramIndex++, operationType);
            }
            
            if (userId != null) {
                pstmt.setInt(paramIndex++, userId);
            }
            
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (page - 1) * pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("id", rs.getInt("id"));
                log.put("userId", rs.getInt("user_id"));
                log.put("username", rs.getString("username"));
                log.put("nickname", rs.getString("nickname"));
                log.put("operationType", rs.getString("operation_type"));
                log.put("operationTarget", rs.getString("operation_target"));
                log.put("createdAt", rs.getString("created_at"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("查询操作日志失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return logs;
    }
    
    /**
     * 查询日志总数
     * 
     * @param operationType 操作类型筛选
     * @param userId 用户ID筛选
     * @return 日志总数
     */
    public int count(String operationType, Integer userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM operation_log WHERE 1=1");
            
            if (operationType != null && !operationType.isEmpty()) {
                sql.append(" AND operation_type = ?");
            }
            
            if (userId != null) {
                sql.append(" AND user_id = ?");
            }
            
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (operationType != null && !operationType.isEmpty()) {
                pstmt.setString(paramIndex++, operationType);
            }
            
            if (userId != null) {
                pstmt.setInt(paramIndex++, userId);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("查询日志总数失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return 0;
    }
}

