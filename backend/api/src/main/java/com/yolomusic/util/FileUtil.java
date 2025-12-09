package com.yolomusic.util;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件上传工具类
 * 处理文件上传、保存、验证等功能
 * 
 * @author YOLOMusic Team
 */
public class FileUtil {
    
    // 上传文件保存路径
    private static final String UPLOAD_BASE_PATH = "uploads";
    private static final String MUSIC_UPLOAD_PATH = UPLOAD_BASE_PATH + "/music";
    private static final String COVER_UPLOAD_PATH = UPLOAD_BASE_PATH + "/covers";
    private static final String PLAYLIST_COVER_PATH = UPLOAD_BASE_PATH + "/playlists";
    
    // 允许的音乐文件类型
    private static final String[] ALLOWED_MUSIC_TYPES = {".mp3", ".wav", ".flac"};
    
    // 允许的图片文件类型
    private static final String[] ALLOWED_IMAGE_TYPES = {".jpg", ".jpeg", ".png", ".gif"};
    
    // 文件大小限制（50MB）
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    
    /**
     * 获取上传文件的真实路径
     * 
     * @param servletContextPath Servlet上下文路径
     * @return 上传文件的基础路径
     */
    public static String getUploadBasePath(String servletContextPath) {
        return servletContextPath + File.separator + UPLOAD_BASE_PATH;
    }
    
    /**
     * 上传音乐文件
     * 
     * @param part 文件Part对象
     * @param servletContextPath Servlet上下文路径
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public static String uploadMusicFile(Part part, String servletContextPath) throws IOException {
        // 验证文件类型
        String fileName = getFileName(part);
        if (!isValidMusicFile(fileName)) {
            throw new IllegalArgumentException("不支持的音乐文件格式，仅支持: MP3, WAV, FLAC");
        }
        
        // 验证文件大小
        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制（最大50MB）");
        }
        
        // 创建保存目录
        String saveDir = servletContextPath + File.separator + MUSIC_UPLOAD_PATH;
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成唯一文件名
        String extension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        String filePath = saveDir + File.separator + uniqueFileName;
        
        // 保存文件
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 返回访问URL
        return "/" + MUSIC_UPLOAD_PATH + "/" + uniqueFileName;
    }
    
    /**
     * 上传封面图片
     * 
     * @param part 文件Part对象
     * @param servletContextPath Servlet上下文路径
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public static String uploadCoverImage(Part part, String servletContextPath) throws IOException {
        // 验证文件类型
        String fileName = getFileName(part);
        if (!isValidImageFile(fileName)) {
            throw new IllegalArgumentException("不支持的图片文件格式，仅支持: JPG, JPEG, PNG, GIF");
        }
        
        // 验证文件大小
        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制（最大50MB）");
        }
        
        // 创建保存目录
        String saveDir = servletContextPath + File.separator + COVER_UPLOAD_PATH;
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成唯一文件名
        String extension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        String filePath = saveDir + File.separator + uniqueFileName;
        
        // 保存文件
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 返回访问URL
        return "/" + COVER_UPLOAD_PATH + "/" + uniqueFileName;
    }
    
    /**
     * 上传歌单封面图片
     * 
     * @param part 文件Part对象
     * @param servletContextPath Servlet上下文路径
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public static String uploadPlaylistCover(Part part, String servletContextPath) throws IOException {
        // 验证文件类型
        String fileName = getFileName(part);
        if (!isValidImageFile(fileName)) {
            throw new IllegalArgumentException("不支持的图片文件格式，仅支持: JPG, JPEG, PNG, GIF");
        }
        
        // 验证文件大小
        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制（最大50MB）");
        }
        
        // 创建保存目录
        String saveDir = servletContextPath + File.separator + PLAYLIST_COVER_PATH;
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 生成唯一文件名
        String extension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        String filePath = saveDir + File.separator + uniqueFileName;
        
        // 保存文件
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // 返回访问URL
        return "/" + PLAYLIST_COVER_PATH + "/" + uniqueFileName;
    }
    
    /**
     * 从Part中获取文件名
     * 
     * @param part Part对象
     * @return 文件名
     */
    private static String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }
    
    /**
     * 验证是否为有效的音乐文件
     * 
     * @param fileName 文件名
     * @return 是否有效
     */
    private static boolean isValidMusicFile(String fileName) {
        String extension = getFileExtension(fileName);
        for (String type : ALLOWED_MUSIC_TYPES) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证是否为有效的图片文件
     * 
     * @param fileName 文件名
     * @return 是否有效
     */
    private static boolean isValidImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        for (String type : ALLOWED_IMAGE_TYPES) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 删除文件
     * 
     * @param fileUrl 文件URL
     * @param servletContextPath Servlet上下文路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String fileUrl, String servletContextPath) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        String filePath = servletContextPath + fileUrl;
        File file = new File(filePath);
        
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        
        return false;
    }
}

