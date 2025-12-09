package com.yolomusic.servlet;

import com.yolomusic.util.FileUtil;
import com.yolomusic.util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传Servlet
 * 处理音乐文件、封面图片、歌单封面的上传
 *
 * @author YOLOMusic Team
 */
@WebServlet("/api/upload")
@MultipartConfig(
        maxFileSize = 52428800,      // 50MB
        maxRequestSize = 52428800    // 50MB
)
public class FileUploadServlet extends HttpServlet {

    // CORS头由CORSFilter统一处理

    /**
     * 处理文件上传请求
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 检查登录状态
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            ResponseUtil.error(response, 401, "请先登录");
            return;
        }

        // 获取上传类型
        String uploadType = request.getParameter("type");
        if (uploadType == null || uploadType.isEmpty()) {
            ResponseUtil.error(response, 400, "请指定上传类型（music/cover/playlist）");
            return;
        }

        // 获取Servlet上下文路径
        String servletContextPath = getServletContext().getRealPath("/");

        try {
            Part filePart = request.getPart("file");

            if (filePart == null || filePart.getSize() == 0) {
                ResponseUtil.error(response, 400, "请选择要上传的文件");
                return;
            }

            String fileUrl = null;

            // 根据类型处理文件上传
            switch (uploadType) {
                case "music":
                    // 上传音乐文件
                    fileUrl = FileUtil.uploadMusicFile(filePart, servletContextPath);
                    break;
                case "cover":
                    // 上传封面图片
                    fileUrl = FileUtil.uploadCoverImage(filePart, servletContextPath);
                    break;
                case "playlist":
                    // 上传歌单封面
                    fileUrl = FileUtil.uploadPlaylistCover(filePart, servletContextPath);
                    break;
                default:
                    ResponseUtil.error(response, 400, "不支持的上传类型");
                    return;
            }

            // 返回文件URL
            Map<String, Object> data = new HashMap<>();
            data.put("fileUrl", fileUrl);
            data.put("fileName", getFileName(filePart));
            data.put("fileSize", filePart.getSize());

            ResponseUtil.success(response, "文件上传成功", data);

        } catch (IllegalArgumentException e) {
            ResponseUtil.error(response, 400, e.getMessage());
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            ResponseUtil.error(response, 500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 从Part中获取文件名
     *
     * @param part Part对象
     * @return 文件名
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "unknown";
    }
}
