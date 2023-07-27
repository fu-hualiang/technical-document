# Spring Security Architecture

[Getting Started | Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture/)

本指南是Spring Security的入门读物，提供了对框架的设计和基本构建块的深入了解。我们只介绍应用程序安全性的基本知识。然而，通过这样做，我们可以澄清使用Spring Security的开发人员所遇到的一些困惑。为了做到这一点，我们来看看安全性在web应用程序中的应用方式，通过使用过滤器，更一般地，通过使用方法注释。当您需要深入了解安全应用程序的工作方式、如何自定义应用程序，或者需要了解如何考虑应用程序安全性时，可以使用本指南。

本指南不打算作为解决最基本问题的手册或配方(还有其他来源)，但它可能对初学者和专家都很有用。Spring Boot也经常被引用，因为它为安全应用程序提供了一些默认行为，了解如何将其与整体体系结构相适应是很有用的。

> 注意
>
> 所有这些原则同样适用于不使用Spring Boot的应用程序。



## 身份验证和访问控制

应用程序安全性可以归结为两个或多或少独立的问题:身份验证(您是谁?)和授权(允许您做什么?)。有时人们说“访问控制”而不是“授权”，这可能会让人感到困惑，但这样想可能会有所帮助，因为“授权”在其他地方是重载的。Spring Security的体系结构旨在将身份验证与授权分离，并为两者提供策略和扩展点。

### 身份验证

认证的主要策略接口是`AuthenticationManager`，它只有一个方法:

```java
public interface AuthenticationManager {

  Authentication authenticate(Authentication authentication)
    throws AuthenticationException;
}
```

`AuthenticationManager`可以在其`authenticate()`方法中执行以下三种操作之一:

- 如果它可以验证输入代表一个有效的主体，则返回一个`Authentication`(通常`authenticated=true`)。
- 如果它认为输入代表无效主体，则抛出`AuthenticationException`。
- 如果无法判断，则返回`null`。

`AuthenticationException`是一个运行时异常。它通常由应用程序以通用方式处理，具体取决于应用程序的样式或目的。换句话说，通常不期望用户代码捕获和处理它。例如，web UI可能呈现一个显示身份验证失败的页面，后端HTTP服务可能发送一个401响应，根据上下文有或没有`WWW-Authenticate`报头。

`AuthenticationManager`最常用的实现是`ProviderManager`，它委托给`AuthenticationProvider`实例链。`AuthenticationProvider`有点像`AuthenticationManager`，但它有一个额外的方法来允许调用者查询它是否支持给定的`Authentication`类型:

```java
public interface AuthenticationProvider {

	Authentication authenticate(Authentication authentication)
			throws AuthenticationException;

	boolean supports(Class<?> authentication);
}
```

`Class<?>`参数在`supports()`方法中实际上是`Class<?extends Authentication>`(它只会被询问是否支持传递给`authenticate()`方法的内容)。通过委托给`AuthenticationProviders`链，`ProviderManager`可以在同一个应用程序中支持多种不同的身份验证机制。如果`ProviderManager`不能识别特定的`Authentication`实例类型，则跳过它。

`ProviderManager`有一个可选的父节点，如果所有的提供程序都返回`null`，它可以咨询父节点。如果父节点不可用，则`null` `Authentication`将导致`AuthenticationException`。

有时，应用程序具有受保护资源的逻辑组(例如，匹配路径模式的所有web资源，例如`/api/**`)，并且每个组都可以拥有自己专用的`AuthenticationManager`。通常，它们中的每一个都是一个`ProviderManager`，它们共享一个父节点。父类是一种“全局”资源，充当所有提供者的后备。

![ProviderManagers with a common parent](https://github.com/spring-guides/top-spring-security-architecture/raw/main/images/authentication.png)



Figure 1. An `AuthenticationManager` hierarchy using `ProviderManager`

### Customizing Authentication Managers

Spring Security provides some configuration helpers to quickly get common authentication manager features set up in your application. The most commonly used helper is the `AuthenticationManagerBuilder`, which is great for setting up in-memory, JDBC, or LDAP user details or for adding a custom `UserDetailsService`. The following example shows an application that configures the global (parent) `AuthenticationManager`:

```java
COPY@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

   ... // web stuff here

  @Autowired
  public void initialize(AuthenticationManagerBuilder builder, DataSource dataSource) {
    builder.jdbcAuthentication().dataSource(dataSource).withUser("dave")
      .password("secret").roles("USER");
  }

}
```

This example relates to a web application, but the usage of `AuthenticationManagerBuilder` is more widely applicable (see [Web Security](https://spring.io/guides/topicals/spring-security-architecture/#web-security) for more detail on how web application security is implemented). Note that the `AuthenticationManagerBuilder` is `@Autowired` into a method in a `@Bean` — that is what makes it build the global (parent) `AuthenticationManager`. In contrast, consider the following example:

```java
COPY@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

  @Autowired
  DataSource dataSource;

   ... // web stuff here

  @Override
  public void configure(AuthenticationManagerBuilder builder) {
    builder.jdbcAuthentication().dataSource(dataSource).withUser("dave")
      .password("secret").roles("USER");
  }

}
```

If we had used an `@Override` of a method in the configurer, the `AuthenticationManagerBuilder` would be used only to build a “local” `AuthenticationManager`, which would be a child of the global one. In a Spring Boot application, you can `@Autowired` the global one into another bean, but you cannot do that with the local one unless you explicitly expose it yourself.

Spring Boot provides a default global `AuthenticationManager` (with only one user) unless you pre-empt it by providing your own bean of type `AuthenticationManager`. The default is secure enough on its own for you not to have to worry about it much, unless you actively need a custom global `AuthenticationManager`. If you do any configuration that builds an `AuthenticationManager`, you can often do it locally to the resources that you are protecting and not worry about the global default.

### Authorization or Access Control

Once authentication is successful, we can move on to authorization, and the core strategy here is `AccessDecisionManager`. There are three implementations provided by the framework and all three delegate to a chain of `AccessDecisionVoter` instances, a bit like the `ProviderManager` delegates to `AuthenticationProviders`.

An `AccessDecisionVoter` considers an `Authentication` (representing a principal) and a secure `Object`, which has been decorated with `ConfigAttributes`:

```java
COPYboolean supports(ConfigAttribute attribute);

boolean supports(Class<?> clazz);

int vote(Authentication authentication, S object,
        Collection<ConfigAttribute> attributes);
```

The `Object` is completely generic in the signatures of the `AccessDecisionManager` and `AccessDecisionVoter`. It represents anything that a user might want to access (a web resource or a method in a Java class are the two most common cases). The `ConfigAttributes` are also fairly generic, representing a decoration of the secure `Object` with some metadata that determines the level of permission required to access it. `ConfigAttribute` is an interface. It has only one method (which is quite generic and returns a `String`), so these strings encode in some way the intention of the owner of the resource, expressing rules about who is allowed to access it. A typical `ConfigAttribute` is the name of a user role (like `ROLE_ADMIN` or `ROLE_AUDIT`), and they often have special formats (like the `ROLE_` prefix) or represent expressions that need to be evaluated.

Most people use the default `AccessDecisionManager`, which is `AffirmativeBased` (if any voters return affirmatively, access is granted). Any customization tends to happen in the voters, either by adding new ones or modifying the way that the existing ones work.

It is very common to use `ConfigAttributes` that are Spring Expression Language (SpEL) expressions — for example, `isFullyAuthenticated() && hasRole('user')`. This is supported by an `AccessDecisionVoter` that can handle the expressions and create a context for them. To extend the range of expressions that can be handled requires a custom implementation of `SecurityExpressionRoot` and sometimes also `SecurityExpressionHandler`.

## Web Security

Spring Security in the web tier (for UIs and HTTP back ends) is based on Servlet `Filters`, so it is helpful to first look at the role of `Filters` generally. The following picture shows the typical layering of the handlers for a single HTTP request.

![Filter chain delegating to a Servlet](Spring Security Architecture.assets/filters.png)

The client sends a request to the application, and the container decides which filters and which servlet apply to it based on the path of the request URI. At most, one servlet can handle a single request, but filters form a chain, so they are ordered. In fact, a filter can veto the rest of the chain if it wants to handle the request itself. A filter can also modify the request or the response used in the downstream filters and servlet. The order of the filter chain is very important, and Spring Boot manages it through two mechanisms: `@Beans` of type `Filter` can have an `@Order` or implement `Ordered`, and they can be part of a `FilterRegistrationBean` that itself has an order as part of its API. Some off-the-shelf filters define their own constants to help signal what order they like to be in relative to each other (for example, the `SessionRepositoryFilter` from Spring Session has a `DEFAULT_ORDER` of `Integer.MIN_VALUE + 50`, which tells us it likes to be early in the chain, but it does not rule out other filters coming before it).

Spring Security is installed as a single `Filter` in the chain, and its concrete type is `FilterChainProxy`, for reasons that we cover soon. In a Spring Boot application, the security filter is a `@Bean` in the `ApplicationContext`, and it is installed by default so that it is applied to every request. It is installed at a position defined by `SecurityProperties.DEFAULT_FILTER_ORDER`, which in turn is anchored by `FilterRegistrationBean.REQUEST_WRAPPER_FILTER_MAX_ORDER` (the maximum order that a Spring Boot application expects filters to have if they wrap the request, modifying its behavior). There is more to it than that, though: From the point of view of the container, Spring Security is a single filter, but, inside of it, there are additional filters, each playing a special role. The following image shows this relationship:

![Spring Security Filter](Spring Security Architecture.assets/security-filters.png)

Figure 2. Spring Security is a single physical `Filter` but delegates processing to a chain of internal filters

In fact, there is even one more layer of indirection in the security filter: It is usually installed in the container as a `DelegatingFilterProxy`, which does not have to be a Spring `@Bean`. The proxy delegates to a `FilterChainProxy`, which is always a `@Bean`, usually with a fixed name of `springSecurityFilterChain`. It is the `FilterChainProxy` that contains all the security logic arranged internally as a chain (or chains) of filters. All the filters have the same API (they all implement the `Filter` interface from the Servlet specification), and they all have the opportunity to veto the rest of the chain.

There can be multiple filter chains all managed by Spring Security in the same top level `FilterChainProxy` and all are unknown to the container. The Spring Security filter contains a list of filter chains and dispatches a request to the first chain that matches it. The following picture shows the dispatch happening based on matching the request path (`/foo/**` matches before `/**`). This is very common but not the only way to match a request. The most important feature of this dispatch process is that only one chain ever handles a request.

![Security Filter Dispatch](Spring Security Architecture.assets/security-filters-dispatch.png)

Figure 3. The Spring Security `FilterChainProxy` dispatches requests to the first chain that matches.

A vanilla Spring Boot application with no custom security configuration has a several (call it n) filter chains, where usually n=6. The first (n-1) chains are there just to ignore static resource patterns, like `/css/**` and `/images/**`, and the error view: `/error`. (The paths can be controlled by the user with `security.ignored` from the `SecurityProperties` configuration bean.) The last chain matches the catch-all path (`/**`) and is more active, containing logic for authentication, authorization, exception handling, session handling, header writing, and so on. There are a total of 11 filters in this chain by default, but normally it is not necessary for users to concern themselves with which filters are used and when.

| Note | The fact that all filters internal to Spring Security are unknown to the container is important, especially in a Spring Boot application, where, by default, all `@Beans` of type `Filter` are registered automatically with the container. So if you want to add a custom filter to the security chain, you need to either not make it be a `@Bean` or wrap it in a `FilterRegistrationBean` that explicitly disables the container registration. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### Creating and Customizing Filter Chains

The default fallback filter chain in a Spring Boot application (the one with the `/**` request matcher) has a predefined order of `SecurityProperties.BASIC_AUTH_ORDER`. You can switch it off completely by setting `security.basic.enabled=false`, or you can use it as a fallback and define other rules with a lower order. To do the latter, add a `@Bean` of type `WebSecurityConfigurerAdapter` (or `WebSecurityConfigurer`) and decorate the class with `@Order`, as follows:

```java
COPY@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/match1/**")
     ...;
  }
}
```

This bean causes Spring Security to add a new filter chain and order it before the fallback.

Many applications have completely different access rules for one set of resources compared to another. For example, an application that hosts a UI and a backing API might support cookie-based authentication with a redirect to a login page for the UI parts and token-based authentication with a 401 response to unauthenticated requests for the API parts. Each set of resources has its own `WebSecurityConfigurerAdapter` with a unique order and its own request matcher. If the matching rules overlap, the earliest ordered filter chain wins.

### Request Matching for Dispatch and Authorization

A security filter chain (or, equivalently, a `WebSecurityConfigurerAdapter`) has a request matcher that is used to decide whether to apply it to an HTTP request. Once the decision is made to apply a particular filter chain, no others are applied. However, within a filter chain, you can have more fine-grained control of authorization by setting additional matchers in the `HttpSecurity` configurer, as follows:

```java
COPY@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/match1/**")
      .authorizeRequests()
        .antMatchers("/match1/user").hasRole("USER")
        .antMatchers("/match1/spam").hasRole("SPAM")
        .anyRequest().isAuthenticated();
  }
}
```

One of the easiest mistakes to make when configuring Spring Security is to forget that these matchers apply to different processes. One is a request matcher for the whole filter chain, and the other is only to choose the access rule to apply.

### Combining Application Security Rules with Actuator Rules

If you use the Spring Boot Actuator for management endpoints, you probably want them to be secure, and, by default, they are. In fact, as soon as you add the Actuator to a secure application, you get an additional filter chain that applies only to the actuator endpoints. It is defined with a request matcher that matches only actuator endpoints and it has an order of `ManagementServerProperties.BASIC_AUTH_ORDER`, which is 5 fewer than the default `SecurityProperties` fallback filter, so it is consulted before the fallback.

If you want your application security rules to apply to the actuator endpoints, you can add a filter chain that is ordered earlier than the actuator one and that has a request matcher that includes all actuator endpoints. If you prefer the default security settings for the actuator endpoints, the easiest thing is to add your own filter later than the actuator one, but earlier than the fallback (for example, `ManagementServerProperties.BASIC_AUTH_ORDER + 1`), as follows:

```java
COPY@Configuration
@Order(ManagementServerProperties.BASIC_AUTH_ORDER + 1)
public class ApplicationConfigurerAdapter extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher("/foo/**")
     ...;
  }
}
```

| Note | Spring Security in the web tier is currently tied to the Servlet API, so it is only really applicable when running an application in a servlet container, either embedded or otherwise. It is not, however, tied to Spring MVC or the rest of the Spring web stack, so it can be used in any servlet application — for instance, one using JAX-RS. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## Method Security

As well as support for securing web applications, Spring Security offers support for applying access rules to Java method executions. For Spring Security, this is just a different type of “protected resource”. For users, it means the access rules are declared using the same format of `ConfigAttribute` strings (for example, roles or expressions) but in a different place in your code. The first step is to enable method security — for example, in the top level configuration for our application:

```java
COPY@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SampleSecureApplication {
}
```

Then we can decorate the method resources directly:

```java
COPY@Service
public class MyService {

  @Secured("ROLE_USER")
  public String secure() {
    return "Hello Security";
  }

}
```

This example is a service with a secure method. If Spring creates a `@Bean` of this type, it is proxied and callers have to go through a security interceptor before the method is actually executed. If access is denied, the caller gets an `AccessDeniedException` instead of the actual method result.

There are other annotations that you can use on methods to enforce security constraints, notably `@PreAuthorize` and `@PostAuthorize`, which let you write expressions containing references to method parameters and return values, respectively.

| Tip  | It is not uncommon to combine Web security and method security. The filter chain provides the user experience features, such as authentication and redirect to login pages and so on, and the method security provides protection at a more granular level. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## Working with Threads

Spring Security is fundamentally thread-bound, because it needs to make the current authenticated principal available to a wide variety of downstream consumers. The basic building block is the `SecurityContext`, which may contain an `Authentication` (and when a user is logged in it is an `Authentication` that is explicitly `authenticated`). You can always access and manipulate the `SecurityContext` through static convenience methods in `SecurityContextHolder`, which, in turn, manipulate a `ThreadLocal`. The following example shows such an arrangement:

```java
COPYSecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
assert(authentication.isAuthenticated);
```

It is **not** common for user application code to do this, but it can be useful if you, for instance, need to write a custom authentication filter (although, even then, there are base classes in Spring Security that you can use so that you could avoid needing to use the `SecurityContextHolder`).

If you need access to the currently authenticated user in a web endpoint, you can use a method parameter in a `@RequestMapping`, as follows:

```java
COPY@RequestMapping("/foo")
public String foo(@AuthenticationPrincipal User user) {
  ... // do stuff with user
}
```

This annotation pulls the current `Authentication` out of the `SecurityContext` and calls the `getPrincipal()` method on it to yield the method parameter. The type of the `Principal` in an `Authentication` is dependent on the `AuthenticationManager` used to validate the authentication, so this can be a useful little trick to get a type-safe reference to your user data.

If Spring Security is in use, the `Principal` from the `HttpServletRequest` is of type `Authentication`, so you can also use that directly:

```java
COPY@RequestMapping("/foo")
public String foo(Principal principal) {
  Authentication authentication = (Authentication) principal;
  User = (User) authentication.getPrincipal();
  ... // do stuff with user
}
```

This can sometimes be useful if you need to write code that works when Spring Security is not in use (you would need to be more defensive about loading the `Authentication` class).

### Processing Secure Methods Asynchronously

Since the `SecurityContext` is thread-bound, if you want to do any background processing that calls secure methods (for example, with `@Async`), you need to ensure that the context is propagated. This boils down to wrapping the `SecurityContext` with the task (`Runnable`, `Callable`, and so on) that is executed in the background. Spring Security provides some helpers to make this easier, such as wrappers for `Runnable` and `Callable`. To propagate the `SecurityContext` to `@Async` methods, you need to supply an `AsyncConfigurer` and ensure the `Executor` is of the correct type:

```java
COPY@Configuration
public class ApplicationConfiguration extends AsyncConfigurerSupport {

  @Override
  public Executor getAsyncExecutor() {
    return new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(5));
  }

}
```

## Get the Code

[Go To Repo](https://github.com/spring-guides/top-spring-security-architecture)

## Projects

[Spring Security](https://spring.io/projects/spring-security)

[Why Spring](https://spring.io/why-spring)

[Microservices](https://spring.io/microservices)

[Reactive](https://spring.io/reactive)

[Event Driven](https://spring.io/event-driven)

[Cloud](https://spring.io/cloud)

[Web Applications](https://spring.io/web-applications)

[Serverless](https://spring.io/serverless)

[Batch](https://spring.io/batch)

[Learn](https://spring.io/learn)

[Quickstart](https://spring.io/quickstart)

[Guides](https://spring.io/guides)

[Blog](https://spring.io/blog)

[Community](https://spring.io/community)

[Events](https://spring.io/events)

[Team](https://spring.io/team)

[Support](https://spring.io/support)

[Security Advisories](https://spring.io/security)

[Projects](https://spring.io/projects)

[Training](https://spring.io/training)

[Thank You](https://spring.io/thank-you)

## Get the Spring newsletter

Please enter your email address

Yes, I would like to be contacted by The Spring Team and VMware for newsletters, promotions and events



SUBSCRIBE



© 2023 VMware, Inc. or its affiliates. [Terms of Use](https://www.vmware.com/help/legal.html) • [Privacy](https://www.vmware.com/help/privacy.html)• [Trademark Guidelines](https://spring.io/trademarks) • [Your California Privacy Rights](https://www.vmware.com/help/privacy/california-privacy-rights.html) • Cookie Settings

Apache®, Apache Tomcat®, Apache Kafka®, Apache Cassandra™, and Apache Geode™ are trademarks or registered trademarks of the Apache Software Foundation in the United States and/or other countries. Java™, Java™ SE, Java™ EE, and OpenJDK™ are trademarks of Oracle and/or its affiliates. Kubernetes® is a registered trademark of the Linux Foundation in the United States and other countries. Linux® is the registered trademark of Linus Torvalds in the United States and other countries. Windows® and Microsoft® Azure are registered trademarks of Microsoft Corporation. “AWS” and “Amazon Web Services” are trademarks or registered trademarks of Amazon.com Inc. or its affiliates. All other trademarks and copyrights are property of their respective owners and are only mentioned for informative purposes. Other names may be trademarks of their respective owners.