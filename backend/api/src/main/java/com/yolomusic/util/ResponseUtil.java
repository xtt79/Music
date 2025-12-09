package com.yolomusic.util;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 响应工具类
 * 用于统一处理HTTP响应，返回JSON格式数据
 * 
 * @author YOLOMusic Team
 */
public class ResponseUtil {
    
    private static final Gson gson = new Gson();
    
    /**
     * 统一响应格式
     */
    public static class Response {
        private int code;        // 响应码：200-成功，其他-失败
        private String message;   // 响应消息
        private Object data;       // 响应数据
        
        public Response(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
        
        // Getters and Setters
        public int getCode() {
            return code;
        }
        
        public void setCode(int code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
    }
    
    /**
     * 成功响应
     * 
     * @param response HttpServletResponse对象
     * @param message 响应消息
     * @param data 响应数据
     * @throws IOException IO异常
     */
    public static void success(HttpServletResponse response, String message, Object data) throws IOException {
        writeResponse(response, 200, message, data);
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @param response HttpServletResponse对象
     * @param message 响应消息
     * @throws IOException IO异常
     */
    public static void success(HttpServletResponse response, String message) throws IOException {
        writeResponse(response, 200, message, null);
    }
    
    /**
     * 失败响应
     * 
     * @param response HttpServletResponse对象
     * @param code 错误码
     * @param message 错误消息
     * @throws IOException IO异常
     */
    public static void error(HttpServletResponse response, int code, String message) throws IOException {
        writeResponse(response, code, message, null);
    }
    
    /**
     * 写入响应数据
     * 
     * @param response HttpServletResponse对象
     * @param code 响应码
     * @param message 响应消息
     * @param data 响应数据
     * @throws IOException IO异常
     */
    private static void writeResponse(HttpServletResponse response, int code, String message, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // CORS头由CORSFilter统一处理，这里不再重复设置
        
        PrintWriter out = response.getWriter();
        Response resp = new Response(code, message, data);
        out.print(gson.toJson(resp));
        out.flush();
        out.close();
    }
}

