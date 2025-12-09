package com.yolomusic.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 字符编码过滤器
 * 统一设置请求和响应的字符编码为UTF-8
 * 
 * @author YOLOMusic Team
 */
//配置拦截路径 什么路径都要拦截
@WebFilter(
        filterName = "CharacterEncodingFilter",
        urlPatterns = "/*"
)
public class CharacterEncodingFilter implements Filter {
    
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodingParam = filterConfig.getInitParameter("encoding");
        if (encodingParam != null && !encodingParam.isEmpty()) {
            this.encoding = encodingParam;
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求编码
        request.setCharacterEncoding(encoding);
        // 设置响应编码
        response.setCharacterEncoding(encoding);
        
        // 继续执行过滤器链
        //拦截完之后需要放行
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // 清理资源
    }
}

