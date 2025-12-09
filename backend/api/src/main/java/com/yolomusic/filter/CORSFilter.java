package com.yolomusic.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName = "CORSFilter",
        urlPatterns = "/*",
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
)
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("✅ CORSFilter初始化完成 - 通过注解配置");
        System.out.println("✅ 过滤器名称: " + filterConfig.getFilterName());
        System.out.println("✅ 过滤器路径: /*");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        System.out.println("\n🌐 CORSFilter处理请求 =============");
        System.out.println("请求方法: " + method);
        System.out.println("请求URI: " + requestURI);
        System.out.println("请求头Origin: " + httpRequest.getHeader("Origin"));

        // 设置CORS头
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // 如果是OPTIONS请求，直接返回200
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("🔄 处理OPTIONS预检请求，返回200");
            System.out.println("✅ 设置CORS头完成");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        System.out.println("➡️ 继续处理请求...");
        chain.doFilter(request, response);
        System.out.println("✅ 请求处理完成 =============\n");
    }

    @Override
    public void destroy() {
        System.out.println("🛑 CORSFilter销毁");
    }
}