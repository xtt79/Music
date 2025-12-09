package com.yolomusic.dao;

import com.yolomusic.entity.Music;
import com.yolomusic.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 音乐数据访问对象
 * 处理音乐相关的数据库操作
 * 
 * @author YOLOMusic Team
 */
public class MusicDAO {
    
    /**
     * 查询所有音乐（分页、搜索、筛选）
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param keyword 搜索关键词（标题、歌手、专辑）
     * @param genre 音乐流派筛选
     * @return 音乐列表
     */
    public List<Music> findAll(int page, int pageSize, String keyword, String genre) {
        List<Music> musics = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM music WHERE 1=1");
            
            // 添加搜索条件
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND (title LIKE ? OR artist LIKE ? OR album LIKE ?)");
            }
            
            // 添加流派筛选
            if (genre != null && !genre.isEmpty()) {
                sql.append(" AND genre = ?");
            }
            
            sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
            
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (keyword != null && !keyword.isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }
            
            if (genre != null && !genre.isEmpty()) {
                pstmt.setString(paramIndex++, genre);
            }
            
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (page - 1) * pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Music music = new Music();
                music.setId(rs.getInt("id"));
                music.setTitle(rs.getString("title"));
                music.setArtist(rs.getString("artist"));
                music.setAlbum(rs.getString("album"));
                music.setGenre(rs.getString("genre"));
                music.setFileUrl(rs.getString("file_url"));
                music.setCoverImage(rs.getString("cover_image"));
                music.setPlayCount(rs.getInt("play_count"));
                music.setStatus(rs.getString("status"));
                music.setCreatedAt(rs.getString("created_at"));
                musics.add(music);
            }
        } catch (SQLException e) {
            System.err.println("查询音乐列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return musics;
    }
    
    /**
     * 查询音乐总数
     * 
     * @param keyword 搜索关键词
     * @param genre 音乐流派
     * @return 音乐总数
     */
    public int count(String keyword, String genre) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM music WHERE 1=1");
            
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND (title LIKE ? OR artist LIKE ? OR album LIKE ?)");
            }
            
            if (genre != null && !genre.isEmpty()) {
                sql.append(" AND genre = ?");
            }
            
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (keyword != null && !keyword.isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
                pstmt.setString(paramIndex++, likeKeyword);
            }
            
            if (genre != null && !genre.isEmpty()) {
                pstmt.setString(paramIndex, genre);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("查询音乐总数失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    /**
     * 根据ID查询音乐
     * 
     * @param id 音乐ID
     * @return 音乐对象
     */
    public Music findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM music WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Music music = new Music();
                music.setId(rs.getInt("id"));
                music.setTitle(rs.getString("title"));
                music.setArtist(rs.getString("artist"));
                music.setAlbum(rs.getString("album"));
                music.setGenre(rs.getString("genre"));
                music.setFileUrl(rs.getString("file_url"));
                music.setCoverImage(rs.getString("cover_image"));
                music.setPlayCount(rs.getInt("play_count"));
                music.setStatus(rs.getString("status"));
                music.setCreatedAt(rs.getString("created_at"));
                return music;
            }
        } catch (SQLException e) {
            System.err.println("查询音乐失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * 添加音乐
     * 
     * @param music 音乐对象
     * @return 是否添加成功
     */
    public boolean insert(Music music) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO music (title, artist, album, genre, file_url, cover_image, play_count, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, music.getTitle());
            pstmt.setString(2, music.getArtist());
            pstmt.setString(3, music.getAlbum());
            pstmt.setString(4, music.getGenre());
            pstmt.setString(5, music.getFileUrl());
            pstmt.setString(6, music.getCoverImage());
            pstmt.setInt(7, music.getPlayCount() != null ? music.getPlayCount() : 0);
            pstmt.setString(8, music.getStatus() != null ? music.getStatus() : "published");
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("添加音乐失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 更新音乐信息
     * 
     * @param music 音乐对象
     * @return 是否更新成功
     */
    public boolean update(Music music) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE music SET title = ?, artist = ?, album = ?, genre = ?, file_url = ?, cover_image = ?, status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, music.getTitle());
            pstmt.setString(2, music.getArtist());
            pstmt.setString(3, music.getAlbum());
            pstmt.setString(4, music.getGenre());
            pstmt.setString(5, music.getFileUrl());
            pstmt.setString(6, music.getCoverImage());
            pstmt.setString(7, music.getStatus());
            pstmt.setInt(8, music.getId());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("更新音乐失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 删除音乐
     * 
     * @param id 音乐ID
     * @return 是否删除成功
     */
    public boolean delete(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM music WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("删除音乐失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 批量删除音乐
     * 
     * @param ids 音乐ID数组
     * @return 删除成功的数量
     */
    public int batchDelete(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            // 构建IN子句
            StringBuilder sql = new StringBuilder("DELETE FROM music WHERE id IN (");
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");
            
            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < ids.length; i++) {
                pstmt.setInt(i + 1, ids[i]);
            }
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("批量删除音乐失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 获取所有音乐流派（用于筛选）
     * 
     * @return 流派列表
     */
    public List<String> findAllGenres() {
        List<String> genres = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DISTINCT genre FROM music WHERE genre IS NOT NULL AND genre != '' ORDER BY genre";
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            System.err.println("查询音乐流派失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return genres;
    }
}

