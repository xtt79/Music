package com.yolomusic.dao;

import com.yolomusic.entity.User;
import com.yolomusic.util.DBUtil;
import org.mindrot.jbcrypt.BCrypt;  // 添加BCrypt导入

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 * 处理用户相关的数据库操作
 *
 * @author YOLOMusic Team
 */
public class UserDAO {

    /**
     * 根据用户名和密码查询用户（登录验证）- 修改为BCrypt验证
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @return 用户对象，如果不存在返回null
     */
    public User findByUsernameAndPassword(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            // 修改：只根据用户名查询，不验证密码
            String sql = "SELECT * FROM user WHERE username = ? AND status = 'active'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));

                // 获取数据库中的密码哈希
                String storedHash = user.getPassword();

                // 验证密码
                if (BCrypt.checkpw(password, storedHash)) {
                    return user;  // 密码正确
                }
                // 如果BCrypt验证失败，尝试明文验证（兼容旧数据）
                if (password.equals(storedHash)) {
                    // 如果是旧明文密码，可以在这里升级为BCrypt
                    String newHash = BCrypt.hashpw(password, BCrypt.gensalt());
                    updatePassword(user.getId(), newHash);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("密码验证失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * 根据用户名查询用户（新增方法）
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * 查询所有用户（分页） - 保持不变
     */
    public List<User> findAll(int page, int pageSize, String keyword) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE 1=1";

            if (keyword != null && !keyword.isEmpty()) {
                sql += " AND (username LIKE ? OR nickname LIKE ?)";
            }

            sql += " ORDER BY created_at DESC LIMIT ? OFFSET ?";

            pstmt = conn.prepareStatement(sql);
            int paramIndex = 1;

            if (keyword != null && !keyword.isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }

            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (page - 1) * pageSize);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("查询用户列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }

        return users;
    }

    /**
     * 更新用户密码（新增方法）
     *
     * @param id 用户ID
     * @param passwordHash 密码哈希（BCrypt加密后的）
     * @return 是否更新成功
     */
    public boolean updatePassword(Integer id, String passwordHash) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE user SET password = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, passwordHash);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("更新密码失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    /**
     * 重置用户密码 - 修改为生成BCrypt哈希
     *
     * @param id 用户ID
     * @param newPassword 新密码（明文）
     * @return 是否重置成功
     */
    public boolean resetPassword(Integer id, String newPassword) {
        // 对新密码进行BCrypt加密
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE user SET password = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("重置密码失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    /**
     * 查询用户总数 - 保持不变
     */
    public int count(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) as total FROM user WHERE 1=1";

            if (keyword != null && !keyword.isEmpty()) {
                sql += " AND (username LIKE ? OR nickname LIKE ?)";
            }

            pstmt = conn.prepareStatement(sql);

            if (keyword != null && !keyword.isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(1, likeKeyword);
                pstmt.setString(2, likeKeyword);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("查询用户总数失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }

        return 0;
    }

    /**
     * 根据ID查询用户 - 保持不变
     */
    public User findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }

        return null;
    }

    /**
     * 更新用户角色和状态 - 保持不变
     */
    public boolean updateRoleAndStatus(Integer id, String role, String status) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE user SET role = ?, status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            pstmt.setString(2, status);
            pstmt.setInt(3, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("更新用户信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }

    /**
     * 批量更新用户状态 - 保持不变
     */
    public int batchUpdateStatus(Integer[] ids, String status) {
        if (ids == null || ids.length == 0) {
            return 0;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("UPDATE user SET status = ? WHERE id IN (");
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, status);
            for (int i = 0; i < ids.length; i++) {
                pstmt.setInt(i + 2, ids[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("批量更新用户状态失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
}