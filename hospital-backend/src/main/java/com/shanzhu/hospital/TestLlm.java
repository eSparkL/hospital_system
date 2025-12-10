package com.shanzhu.hospital;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLlm {

    // API 密钥和URL
    private static final String API_KEY = "sk-TMe6dYqpxvJgQXfK82mhCpqPcOcxzMVjr3EylpcfowuKipR1";
    private static final String API_URL = "https://api.chatanywhere.tech/v1/chat/completions";

    public static void main(String[] args) {
        try {
            // 创建 HTTP 客户端
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 创建 HTTP 请求对象
            HttpPost httpPost = new HttpPost(API_URL);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);

            // 构造请求体
            String jsonBody = "{"
                    + "\"model\": \"gpt-3.5-turbo\","
                    + "\"messages\": ["
                    + "{\"role\": \"user\", \"content\": \"我头晕恶心，应该挂什么科比较好\"}"
                    + "]"
                    + "}";

            // 设置请求体
            StringEntity entity = new StringEntity(jsonBody, "UTF-8");
            httpPost.setEntity(entity);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseString.append(line);
            }

            // 输出完整响应
            System.out.println("Full API Response:");
            System.out.println(responseString.toString());

            // 使用正则表达式提取 content 内容
            String responseText = responseString.toString();
            Pattern pattern = Pattern.compile("\"content\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(responseText);
            
            if (matcher.find()) {
                String modelResponse = matcher.group(1);
                // 处理转义字符
                modelResponse = modelResponse.replace("\\n", "\n").replace("\\\"", "\"");
                System.out.println("\nModel Response: " + modelResponse);
            } else {
                System.out.println("Could not extract model response from JSON");
            }

            // 关闭客户端
            httpClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}