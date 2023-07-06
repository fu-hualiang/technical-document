package com.example.nestle.utils.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 发送http请求
 *
 * @author fuhualiang
 * @author Varian
 */
public class Request {
    public static String doGet(String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        return doRequest(RequestEnum.GET, url, parameterMap, headerMap);
    }

    public static String doPost(String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        return doRequest(RequestEnum.POST, url, parameterMap, headerMap);
    }

    public static String doPut(String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        return doRequest(RequestEnum.PUT, url, parameterMap, headerMap);
    }

    public static String doDelete(String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        return doRequest(RequestEnum.DELETE, url, parameterMap, headerMap);
    }

    private static String doRequest(RequestEnum method, String url, Map<String, String> parameterMap, Map<String, String> headerMap) {
        //创建httpClient实例
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            //创建一个uri对象
            URIBuilder uriBuilder = new URIBuilder(url);
            //塞入form参数
            if (parameterMap != null)
                for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue());
                }
            //创建远程连接实例,这里传入目标的网络地址
            HttpRequestBase httpRequest;
            if (method == RequestEnum.GET) {
                httpRequest = new HttpGet(uriBuilder.build());
            } else if (method == RequestEnum.POST) {
                httpRequest = new HttpPost(uriBuilder.build());
            } else if (method == RequestEnum.PUT) {
                httpRequest = new HttpPut(uriBuilder.build());
            } else if (method == RequestEnum.DELETE) {
                httpRequest = new HttpDelete(uriBuilder.build());
            } else {
                throw new RuntimeException("无请求方法！");
            }
            // 设置请求头信息，鉴权(没有可忽略)
            if (headerMap != null)
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            // 设置配置请求参数(没有可忽略)
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpRequest.setConfig(requestConfig);
            //执行请求
            CloseableHttpResponse response = client.execute(httpRequest);
            //获取Response状态码
//            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println(statusCode);
            //获取响应实体, 响应内容
            HttpEntity entity = response.getEntity();
            //通过EntityUtils中的toString方法将结果转换为字符串
            String str = EntityUtils.toString(entity);
            response.close();
            client.close();
            return str;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
