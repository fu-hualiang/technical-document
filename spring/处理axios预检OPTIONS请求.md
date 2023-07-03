# 处理 axios 预检 OPTIONS 请求

## 1.创建自定义 CorsFilter

```java
@Component
public class CorsFilter implements Filter {
    public static final String OPTION_METHOD = "OPTIONS";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Allow-Headers", "*");
        resp.setHeader("Access-Control-Max-Age", "3600");
        // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
        // 例如说，vue axios 请求时，会自带该逻辑的
        HttpServletRequest req = (HttpServletRequest) request;
        if (OPTION_METHOD.equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        // 如果是其它请求方法，则继续过滤器。
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
```

## 2.注册 CorsFilter

```java
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<CorsFilter> CorsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("CorsFilter");
        return registration;
    }
}
```

