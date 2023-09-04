# Token 的简易实现

此方法将 token 信息附加于请求头中，会引发跨域问题，参考[处理 axios 预检 OPTIONS 请求](处理axios预检OPTIONS请求.md)。

## 1.利用 JWT 编写工具类

```java
    /** 生成 token */
    public static String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * expire);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
```

```java
    /** 解析 token */
    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
```

## 2.编写自定义拦截器

```java
@Component
public class TokenInterceptor implements HandlerInterceptor {
    /** 目标方法执行之前 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws MyException {
        System.out.println("拦截请求信息 (" + request.getMethod() + ") " + request.getRequestURI());
        // 取得请求头中的数据
        String token = request.getHeader("Authorization");
        try {
            // 提取 token 中的 userId 信息
            String userId = TokenUtils.validateToken(token).getSubject();
            String loginUserId = request.getHeader("LoginUserId");
            if (!userId.equals(loginUserId)) throw new MyException(40000, "请登录");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(40000, "请登录");
        }
    }
}
```

## 3.注册拦截器

```java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    
    @Resource
    private TokenInterceptor tokenInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
            // 设置拦截路径的规则
                .addPathPatterns("/**")
                .excludePathPatterns("/users/login")
                .excludePathPatterns("/static/**");
    }
}
```

