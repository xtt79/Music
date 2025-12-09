package com.yolomusic.servlet;

import com.google.gson.Gson;
import com.yolomusic.dao.MusicDAO;
import com.yolomusic.dao.OperationLogDAO;
import com.yolomusic.entity.Music;
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
 * 音乐管理Servlet
 * 处理音乐的增删改查操作
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/music/*")
public class MusicServlet extends HttpServlet {

    private MusicDAO musicDAO = new MusicDAO();
    private OperationLogDAO logDAO = new OperationLogDAO();
    private Gson gson = new Gson();

    // CORS头由CORSFilter统一处理

    /**
     * 处理GET请求：查询音乐列表或单个音乐
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
            // 查询音乐列表
            listMusic(request, response);
        } else if (pathInfo.startsWith("/genres")) {
            // 获取所有流派
            getGenres(response);
        } else {
            // 查询单个音乐
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.parseInt(idStr);
                getMusicById(id, response);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的音乐ID");
            }
        }
    }

    /**
     * 处理POST请求：添加音乐
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        // 从 JSON请求体中读取参数
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        Music music = gson.fromJson(requestBody, Music.class);

        // 参数验证
        if (music == null || music.getTitle() == null || music.getTitle().isEmpty() ||
                music.getArtist() == null || music.getArtist().isEmpty()) {
            ResponseUtil.error(response, 400, "歌曲标题和歌手不能为空");
            return;
        }

        // 设置默认值
        if (music.getStatus() == null || music.getStatus().isEmpty()) {
            music.setStatus("published");
        }
        music.setPlayCount(0);

        // 添加音乐
        boolean success = musicDAO.insert(music);

        if (success) {
            // 记录操作日志
            logDAO.insert(userId, "ADD_MUSIC", "添加音乐: " + music.getTitle());
            ResponseUtil.success(response, "添加音乐成功", music);
        } else {
            ResponseUtil.error(response, 500, "添加音乐失败");
        }
    }

    /**
     * 处理PUT请求：更新音乐
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

        Integer userId = (Integer) session.getAttribute("userId");

        // 获取路径参数
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.error(response, 400, "请指定要更新的音乐ID");
            return;
        }

        String idStr = pathInfo.substring(1);
        Integer id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            ResponseUtil.error(response, 400, "无效的音乐ID");
            return;
        }

        // 从 JSON请求体中读取参数
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }

        String requestBody = sb.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> params = gson.fromJson(requestBody, Map.class);

        // 查询原音乐信息
        Music music = musicDAO.findById(id);
        if (music == null) {
            ResponseUtil.error(response, 404, "音乐不存在");
            return;
        }

        // 更新音乐信息
        if (params != null) {
            if (params.containsKey("title")) music.setTitle((String) params.get("title"));
            if (params.containsKey("artist")) music.setArtist((String) params.get("artist"));
            if (params.containsKey("album")) music.setAlbum((String) params.get("album"));
            if (params.containsKey("genre")) music.setGenre((String) params.get("genre"));
            if (params.containsKey("fileUrl")) music.setFileUrl((String) params.get("fileUrl"));
            if (params.containsKey("coverImage")) music.setCoverImage((String) params.get("coverImage"));
            if (params.containsKey("status")) music.setStatus((String) params.get("status"));
        }

        // 更新数据库
        boolean success = musicDAO.update(music);

        if (success) {
            // 记录操作日志
            logDAO.insert(userId, "UPDATE_MUSIC", "更新音乐ID: " + id);
            ResponseUtil.success(response, "更新音乐成功", music);
        } else {
            ResponseUtil.error(response, 500, "更新音乐失败");
        }
    }

    /**
     * 处理DELETE请求：删除音乐
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        // 获取路径参数
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            ResponseUtil.error(response, 400, "请指定要删除的音乐ID");
            return;
        }

        String idStr = pathInfo.substring(1);

        // 判断是单个删除还是批量删除
        if (idStr.contains(",")) {
            // 批量删除
            String[] idStrs = idStr.split(",");
            Integer[] ids = new Integer[idStrs.length];
            try {
                for (int i = 0; i < idStrs.length; i++) {
                    ids[i] = Integer.parseInt(idStrs[i].trim());
                }
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的音乐ID");
                return;
            }

            // 查询要删除的音乐文件URL，用于删除文件
            for (Integer id : ids) {
                Music music = musicDAO.findById(id);
                if (music != null) {
                    // 删除音乐文件
                    if (music.getFileUrl() != null) {
                        FileUtil.deleteFile(music.getFileUrl(), getServletContext().getRealPath("/"));
                    }
                    // 删除封面图片
                    if (music.getCoverImage() != null) {
                        FileUtil.deleteFile(music.getCoverImage(), getServletContext().getRealPath("/"));
                    }
                }
            }

            int deletedCount = musicDAO.batchDelete(ids);

            if (deletedCount > 0) {
                logDAO.insert(userId, "BATCH_DELETE_MUSIC", "批量删除音乐，数量: " + deletedCount);
                ResponseUtil.success(response, "批量删除成功，共删除 " + deletedCount + " 条记录");
            } else {
                ResponseUtil.error(response, 500, "批量删除失败");
            }
        } else {
            // 单个删除
            Integer id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                ResponseUtil.error(response, 400, "无效的音乐ID");
                return;
            }

            // 查询音乐信息，用于删除文件
            Music music = musicDAO.findById(id);
            if (music == null) {
                ResponseUtil.error(response, 404, "音乐不存在");
                return;
            }

            // 删除音乐文件
            if (music.getFileUrl() != null) {
                FileUtil.deleteFile(music.getFileUrl(), getServletContext().getRealPath("/"));
            }
            // 删除封面图片
            if (music.getCoverImage() != null) {
                FileUtil.deleteFile(music.getCoverImage(), getServletContext().getRealPath("/"));
            }

            // 删除数据库记录
            boolean success = musicDAO.delete(id);

            if (success) {
                logDAO.insert(userId, "DELETE_MUSIC", "删除音乐ID: " + id);
                ResponseUtil.success(response, "删除音乐成功");
            } else {
                ResponseUtil.error(response, 500, "删除音乐失败");
            }
        }
    }

    /**
     * 查询音乐列表
     */
    private void listMusic(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取查询参数
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        String keyword = request.getParameter("keyword");
        String genre = request.getParameter("genre");

        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = pageSizeStr != null ? Integer.parseInt(pageSizeStr) : 10;

        // 查询数据
        List<Music> musics = musicDAO.findAll(page, pageSize, keyword, genre);
        int total = musicDAO.count(keyword, genre);

        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", musics);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);

        ResponseUtil.success(response, "查询成功", data);
    }

    /**
     * 根据ID查询音乐
     */
    private void getMusicById(Integer id, HttpServletResponse response) throws IOException {
        Music music = musicDAO.findById(id);

        if (music != null) {
            ResponseUtil.success(response, "查询成功", music);
        } else {
            ResponseUtil.error(response, 404, "音乐不存在");
        }
    }

    /**
     * 获取所有音乐流派
     */
    private void getGenres(HttpServletResponse response) throws IOException {
        List<String> genres = musicDAO.findAllGenres();
        ResponseUtil.success(response, "查询成功", genres);
    }
}
