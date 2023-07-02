# JAVA发送http请求

```java
        //创建httpClient实例
        CloseableHttpClient client = HttpClients.createDefault();
        //创建一个uri对象
        URIBuilder uriBuilder = new URIBuilder(url);
        //塞入form参数
        uriBuilder.addParameter("X-Auth-Token", Token);
        uriBuilder.addParameter("User-Agent", Agent);
        //创建httpGet远程连接实例,这里传入目标的网络地址
        HttpGet httpRequest = new HttpGet(uriBuilder.build());
        HttpRequestBase httpGet=httpRequest;
        HttpPost httpPost =new HttpPost(uriBuilder.build());
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        HttpPut httpPut = new HttpPut(uriBuilder.build());
        // 设置请求头信息，鉴权(没有可忽略)
        httpGet.setHeader("X-Auth-Token", Token);
        httpGet.setHeader("User-Agent", Agent);
        // 设置配置请求参数(没有可忽略)
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 请求超时时间
                .setSocketTimeout(60000)// 数据读取超时时间
                .build();
        // 为httpGet实例设置配置
        httpGet.setConfig(requestConfig);
        //执行请求
        CloseableHttpResponse response = client.execute(httpGet);
        //获取Response状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
        //获取响应实体, 响应内容
        HttpEntity entity = response.getEntity();
        //通过EntityUtils中的toString方法将结果转换为字符串
        String str = EntityUtils.toString(entity);
//        System.out.println(str);
        response.close();
        client.close();

        //解析json

        ObjectMapper objectMapper = new ObjectMapper();
//        Data data= objectMapper.readValue(str, Data.class);
//        System.out.println(data.getData());
//        return data.getData();
        JsonNode jsonNode = objectMapper.readTree(str);
        JsonNode userNode = jsonNode.get("data");
        System.out.println("userNode.toString()"+userNode.toString());
        User user = objectMapper.readValue(userNode.toString(), User.class);
        return user;
```

