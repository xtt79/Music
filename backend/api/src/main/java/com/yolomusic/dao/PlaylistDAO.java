package com.yolomusic.dao;

import com.yolomusic.entity.Playlist;
import com.yolomusic.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌单数据访问对象
 * 处理歌单相关的数据库操作
 * 
 * @author YOLOMusic Team
 */
public class PlaylistDAO {
    
    /**
     * 查询所有歌单（分页、搜索）
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param keyword 搜索关键词（歌单名称）
     * @return 歌单列表
     */
    public List<Playlist> findAll(int page, int pageSize, String keyword) {
        List<Playlist> playlists = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM playlist WHERE 1=1");
            
            // 添加搜索条件
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND name LIKE ?");
            }
            
            sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
            
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (keyword != null && !keyword.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + keyword + "%");
            }
            
            pstmt.setInt(paramIndex++, pageSize);
            pstmt.setInt(paramIndex, (page - 1) * pageSize);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Playlist playlist = new Playlist();
                playlist.setId(rs.getInt("id"));
                playlist.setName(rs.getString("name"));
                playlist.setDescription(rs.getString("description"));
                playlist.setCoverImage(rs.getString("cover_image"));
                playlist.setCreatorId(rs.getInt("creator_id"));
                playlist.setMusicCount(rs.getInt("music_count"));
                playlist.setCreatedAt(rs.getString("created_at"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            System.err.println("查询歌单列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return playlists;
    }
    
    /**
     * 查询歌单总数
     * 
     * @param keyword 搜索关键词
     * @return 歌单总数
     */
    public int count(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM playlist WHERE 1=1");
            
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND name LIKE ?");
            }
            
            pstmt = conn.prepareStatement(sql.toString());
            
            if (keyword != null && !keyword.isEmpty()) {
                pstmt.setString(1, "%" + keyword + "%");
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("查询歌单总数失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    /**
     * 根据ID查询歌单
     * 
     * @param id 歌单ID
     * @return 歌单对象
     */
    public Playlist findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM playlist WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Playlist playlist = new Playlist();
                playlist.setId(rs.getInt("id"));
                playlist.setName(rs.getString("name"));
                playlist.setDescription(rs.getString("description"));
                playlist.setCoverImage(rs.getString("cover_image"));
                playlist.setCreatorId(rs.getInt("creator_id"));
                playlist.setMusicCount(rs.getInt("music_count"));
                playlist.setCreatedAt(rs.getString("created_at"));
                return playlist;
            }
        } catch (SQLException e) {
            System.err.println("查询歌单失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * 添加歌单
     * 
     * @param playlist 歌单对象
     * @return 是否添加成功
     */
    public boolean insert(Playlist playlist) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO playlist (name, description, cover_image, creator_id, music_count) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playlist.getName());
            pstmt.setString(2, playlist.getDescription());
            pstmt.setString(3, playlist.getCoverImage());
            pstmt.setInt(4, playlist.getCreatorId());
            pstmt.setInt(5, playlist.getMusicCount() != null ? playlist.getMusicCount() : 0);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("添加歌单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 更新歌单信息
     * 
     * @param playlist 歌单对象
     * @return 是否更新成功
     */
    public boolean update(Playlist playlist) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE playlist SET name = ?, description = ?, cover_image = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playlist.getName());
            pstmt.setString(2, playlist.getDescription());
            pstmt.setString(3, playlist.getCoverImage());
            pstmt.setInt(4, playlist.getId());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("更新歌单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 删除歌单
     * 
     * @param id 歌单ID
     * @return 是否删除成功
     */
    public boolean delete(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM playlist WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("删除歌单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 向歌单添加音乐
     * 
     * @param playlistId 歌单ID
     * @param musicId 音乐ID
     * @return 是否添加成功
     */
    public boolean addMusicToPlaylist(Integer playlistId, Integer musicId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement checkPstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            // 检查是否已存在
            String checkSql = "SELECT COUNT(*) as count FROM playlist_music WHERE playlist_id = ? AND music_id = ?";
            checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setInt(1, playlistId);
            checkPstmt.setInt(2, musicId);
            rs = checkPstmt.executeQuery();
            
            if (rs.next() && rs.getInt("count") > 0) {
                // 已存在，不重复添加
                return false;
            }
            
            // 添加关联
            String sql = "INSERT INTO playlist_music (playlist_id, music_id) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, musicId);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                // 更新歌单音乐数量
                updateMusicCount(playlistId);
            }
            
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("添加音乐到歌单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(checkPstmt);
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 从歌单移除音乐
     * 
     * @param playlistId 歌单ID
     * @param musicId 音乐ID
     * @return 是否移除成功
     */
    public boolean removeMusicFromPlaylist(Integer playlistId, Integer musicId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM playlist_music WHERE playlist_id = ? AND music_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, musicId);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                // 更新歌单音乐数量
                updateMusicCount(playlistId);
            }
            
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("从歌单移除音乐失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeAll(conn, pstmt, null);
        }
    }
    
    /**
     * 获取歌单中的音乐列表
     * 
     * @param playlistId 歌单ID
     * @return 音乐ID列表
     */
    public List<Integer> getMusicIdsByPlaylistId(Integer playlistId) {
        List<Integer> musicIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT music_id FROM playlist_music WHERE playlist_id = ? ORDER BY added_at";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                musicIds.add(rs.getInt("music_id"));
            }
        } catch (SQLException e) {
            System.err.println("查询歌单音乐列表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeAll(conn, pstmt, rs);
        }
        
        return musicIds;
    }
    
    /**
     * 更新歌单音乐数量
     * 
     * @param playlistId 歌单ID
     */
    private void updateMusicCount(Integer playlistId) {
        Connection conn = null;
        PreparedStatement countPstmt = null;
        PreparedStatement updatePstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            // 统计音乐数量
            String countSql = "SELECT COUNT(*) as count FROM playlist_music WHERE playlist_id = ?";
            countPstmt = conn.prepareStatement(countSql);
            countPstmt.setInt(1, playlistId);
            rs = countPstmt.executeQuery();
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            
            // 更新歌单音乐数量
            String updateSql = "UPDATE playlist SET music_count = ? WHERE id = ?";
            updatePstmt = conn.prepareStatement(updateSql);
            updatePstmt.setInt(1, count);
            updatePstmt.setInt(2, playlistId);
            updatePstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新歌单音乐数量失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.closeResultSet(rs);
            DBUtil.closePreparedStatement(countPstmt);
            DBUtil.closePreparedStatement(updatePstmt);
            DBUtil.closeConnection(conn);
        }
    }
}

