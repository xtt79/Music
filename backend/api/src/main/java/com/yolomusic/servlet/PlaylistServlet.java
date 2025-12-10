package com.yolomusic.servlet;

import com.yolomusic.dao.OperationLogDAO;
import com.yolomusic.dao.PlaylistDAO;
import com.yolomusic.entity.Playlist;
import com.yolomusic.entity.User;
import com.yolomusic.util.FileUtil;
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
 * 歌单管理Servlet
 * 处理歌单的增删改查和音乐关联操作
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/playlist/*")
public class PlaylistServlet extends HttpServlet {

    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private OperationLogDAO logDAO = new OperationLogDAO();

    // CORS头由CORSFilter统一处理

    /**
     * 处理GET请求：查询歌单列表或单个歌单
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
            // 查询歌单列表
            listPlaylists(request, response);
        } else if (pathInfo.startsWith("/music/")) {
            // 获取歌单中的音乐列表
            String playlistIdStr = pathInfo.substring(7);
            try {
                Integer playlistId = Integer.parseInt(playlistIdStr);
                getPlaylistMusic(playlistId, response);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的歌单ID");
            }
        } else {
            // 查询单个歌单
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.parseInt(idStr);
                getPlaylistById(id, response);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的歌单ID");
            }
        }
    }

    /**
     * 处理POST请求：添加歌单或添加音乐到歌单
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        User currentUser = getSessionUser(session);
        if (currentUser == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        Integer userId = currentUser.getId();
        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());
        String action = request.getParameter("action");

        if ("addMusic".equals(action)) {
            addMusicToPlaylist(request, response, userId, isAdmin);
        } else {
            createPlaylist(request, response, currentUser);
        }
    }

    /**
     * 处理PUT请求：更新歌单
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        User currentUser = getSessionUser(session);
        if (currentUser == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        Integer userId = currentUser.getId();
        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

        // 获取路径参数
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.error(response, 400, "请指定要更新的歌单ID");
            return;
        }

        String idStr = pathInfo.substring(1);
        Integer id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            ResponseUtil.error(response, 400, "无效的歌单ID");
            return;
        }

        // 从 JSON请求体中读取参数
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> params = gson.fromJson(requestBody, java.util.Map.class);

        Playlist playlist = playlistDAO.findById(id);
        if (playlist == null) {
            ResponseUtil.error(response, 404, "歌单不存在");
            return;
        }
        if (!canEdit(playlist, userId, isAdmin)) {
            ResponseUtil.error(response, 403, "无权限编辑该歌单");
            return;
        }

        // 更新歌单信息
        if (params != null) {
            if (params.containsKey("name")) playlist.setName((String) params.get("name"));
            if (params.containsKey("description")) playlist.setDescription((String) params.get("description"));
            if (params.containsKey("coverImage")) playlist.setCoverImage((String) params.get("coverImage"));
        }

        // 更新数据库
        boolean success = playlistDAO.update(playlist);

        if (success) {
            logDAO.insert(userId, "UPDATE_PLAYLIST", "更新歌单ID: " + id);
            ResponseUtil.success(response, "更新歌单成功", playlist);
        } else {
            ResponseUtil.error(response, 500, "更新歌单失败");
        }
    }

    /**
     * 处理DELETE请求：删除歌单或从歌单移除音乐
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        User currentUser = getSessionUser(session);
        if (currentUser == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }
        Integer userId = currentUser.getId();
        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

        String action = request.getParameter("action");

        if ("removeMusic".equals(action)) {
            // 从歌单移除音乐
            removeMusicFromPlaylist(request, response, userId, isAdmin);
        } else {
            // 删除歌单
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                ResponseUtil.error(response, 400, "请指定要删除的歌单ID");
                return;
            }

            String idStr = pathInfo.substring(1);
            Integer id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的歌单ID");
                return;
            }

            Playlist playlist = playlistDAO.findById(id);
            if (playlist == null) {
                ResponseUtil.error(response, 404, "歌单不存在");
                return;
            }
            if (!canEdit(playlist, userId, isAdmin)) {
                ResponseUtil.error(response, 403, "无权限删除该歌单");
                return;
            }

            // 删除封面图片
            if (playlist.getCoverImage() != null) {
                FileUtil.deleteFile(playlist.getCoverImage(), getServletContext().getRealPath("/"));
            }

            // 删除数据库记录
            boolean success = playlistDAO.delete(id);

            if (success) {
                logDAO.insert(userId, "DELETE_PLAYLIST", "删除歌单ID: " + id);
                ResponseUtil.success(response, "删除歌单成功");
            } else {
                ResponseUtil.error(response, 500, "删除歌单失败");
            }
        }
    }

    /**
     * 查询歌单列表
     */
    private void listPlaylists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取查询参数
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        String keyword = request.getParameter("keyword");

        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 10;

        List<Playlist> playlists = playlistDAO.findAll(page, pageSize, keyword);
        int total = playlistDAO.count(keyword);

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", playlists);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);

        ResponseUtil.success(response, "查询成功", data);
    }

    /**
     * 根据ID查询歌单
     */
    private void getPlaylistById(Integer id, HttpServletResponse response) throws IOException {
        Playlist playlist = playlistDAO.findById(id);

        if (playlist != null) {
            ResponseUtil.success(response, "查询成功", playlist);
        } else {
            ResponseUtil.error(response, 404, "歌单不存在");
        }
    }

    /**
     * 创建歌单
     */
    private void createPlaylist(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        // 从 JSON请求体中读取参数
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        Playlist playlist = gson.fromJson(requestBody, Playlist.class);

        // 参数验证
        if (playlist == null || playlist.getName() == null || playlist.getName().isEmpty()) {
            ResponseUtil.error(response, 400, "歌单名称不能为空");
            return;
        }

        // 设置创建者和音乐数量
        playlist.setCreatorId(user.getId());
        playlist.setCreatorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
        playlist.setMusicCount(0);

        // 添加歌单
        boolean success = playlistDAO.insert(playlist);

        if (success) {
            logDAO.insert(user.getId(), "CREATE_PLAYLIST", "创建歌单: " + playlist.getName());
            ResponseUtil.success(response, "创建歌单成功", playlist);
        } else {
            ResponseUtil.error(response, 500, "创建歌单失败");
        }
    }

    /**
     * 添加音乐到歌单
     */
    private void addMusicToPlaylist(HttpServletRequest request, HttpServletResponse response, Integer userId, boolean isAdmin) throws IOException {
        String playlistIdStr = request.getParameter("playlistId");
        String musicIdStr = request.getParameter("musicId");

        if (playlistIdStr == null || musicIdStr == null) {
            ResponseUtil.error(response, 400, "请指定歌单ID和音乐ID");
            return;
        }

        try {
            Integer playlistId = Integer.parseInt(playlistIdStr);
            Integer musicId = Integer.parseInt(musicIdStr);

            Playlist playlist = playlistDAO.findById(playlistId);
            if (playlist == null) {
                ResponseUtil.error(response, 404, "歌单不存在");
                return;
            }
            if (!canEdit(playlist, userId, isAdmin)) {
                ResponseUtil.error(response, 403, "无权限编辑该歌单");
                return;
            }

            boolean success = playlistDAO.addMusicToPlaylist(playlistId, musicId);

            if (success) {
                logDAO.insert(userId, "ADD_MUSIC_TO_PLAYLIST", "向歌单ID: " + playlistId + " 添加音乐ID: " + musicId);
                ResponseUtil.success(response, "添加音乐到歌单成功");
            } else {
                ResponseUtil.error(response, 500, "添加音乐到歌单失败（可能已存在）");
            }
        } catch (NumberFormatException e) {
            ResponseUtil.error(response, 400, "无效的歌单ID或音乐ID");
        }
    }

    /**
     * 从歌单移除音乐
     */
    private void removeMusicFromPlaylist(HttpServletRequest request, HttpServletResponse response, Integer userId, boolean isAdmin) throws IOException {
        String playlistIdStr = request.getParameter("playlistId");
        String musicIdStr = request.getParameter("musicId");

        if (playlistIdStr == null || musicIdStr == null) {
            ResponseUtil.error(response, 400, "请指定歌单ID和音乐ID");
            return;
        }

        try {
            Integer playlistId = Integer.parseInt(playlistIdStr);
            Integer musicId = Integer.parseInt(musicIdStr);

            Playlist playlist = playlistDAO.findById(playlistId);
            if (playlist == null) {
                ResponseUtil.error(response, 404, "歌单不存在");
                return;
            }
            if (!canEdit(playlist, userId, isAdmin)) {
                ResponseUtil.error(response, 403, "无权限编辑该歌单");
                return;
            }

            boolean success = playlistDAO.removeMusicFromPlaylist(playlistId, musicId);

            if (success) {
                logDAO.insert(userId, "REMOVE_MUSIC_FROM_PLAYLIST", "从歌单ID: " + playlistId + " 移除音乐ID: " + musicId);
                ResponseUtil.success(response, "从歌单移除音乐成功");
            } else {
                ResponseUtil.error(response, 500, "从歌单移除音乐失败");
            }
        } catch (NumberFormatException e) {
            ResponseUtil.error(response, 400, "无效的歌单ID或音乐ID");
        }
    }

    /**
     * 获取歌单中的音乐列表
     */
    private void getPlaylistMusic(Integer playlistId, HttpServletResponse response) throws IOException {
        List<com.yolomusic.entity.Music> musicList = playlistDAO.getMusicDetailsByPlaylistId(playlistId);
        ResponseUtil.success(response, "查询成功", musicList);
    }

    private User getSessionUser(HttpSession session) {
        Object obj = session.getAttribute("user");
        if (obj instanceof User) {
            return (User) obj;
        }
        // 兼容只存 userId/role/nickname 的情况
        User user = new User();
        Object uid = session.getAttribute("userId");
        if (uid instanceof Integer) {
            user.setId((Integer) uid);
        }
        Object role = session.getAttribute("role");
        if (role instanceof String) {
            user.setRole((String) role);
        }
        Object nickname = session.getAttribute("nickname");
        if (nickname instanceof String) {
            user.setNickname((String) nickname);
        }
        Object username = session.getAttribute("username");
        if (username instanceof String) {
            user.setUsername((String) username);
        }
        if (user.getId() == null) {
            return null;
        }
        return user;
    }

    private boolean canEdit(Playlist playlist, Integer userId, boolean isAdmin) {
        if (isAdmin) return true;
        return playlist != null && playlist.getCreatorId() != null && playlist.getCreatorId().equals(userId);
    }
}
