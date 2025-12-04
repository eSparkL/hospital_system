# 大语言模型(LLM)相关API接口文档

## 1. 概述

本文档详细描述了医院管理系统中大语言模型相关API接口的使用方法，包括普通请求、流式请求以及带上下文的会话管理等功能。

## 2. 接口列表

### 2.1 普通请求接口
- 请求地址：`/llm/ask`
- 请求方法：POST
- 功能描述：向大语言模型发送问题并获得完整的响应结果

### 2.2 流式请求接口
- 请求地址：`/llm/ask/stream`
- 请求方法：GET
- 功能描述：向大语言模型发送问题并获得流式响应结果

### 2.3 会话管理接口
- 请求地址：`/llm/session/create`
- 请求方法：POST
- 功能描述：创建新的聊天会话

### 2.4 带上下文请求接口
- 请求地址：`/llm/ask/context`
- 请求方法：POST
- 功能描述：向大语言模型发送问题并获得带有上下文的完整响应结果

### 2.5 流式带上下文请求接口
- 请求地址：`/llm/ask/context/stream`
- 请求方法：GET
- 功能描述：向大语言模型发送问题并获得带有上下文的流式响应结果

## 3. 接口详情

### 3.1 普通请求接口

#### 基本信息
- 接口地址：`POST /llm/ask`
- 请求协议：HTTP/1.1
- 字符编码：UTF-8
- 返回格式：JSON

#### 请求参数
| 参数名称 | 类型   | 是否必填 | 描述         |
| -------- | ------ | -------- | ------------ |
| prompt   | String | 是       | 用户提问内容 |

#### 请求示例
```http
POST /llm/ask?prompt=什么是人工智能 HTTP/1.1
Host: localhost:9281
Content-Type: application/x-www-form-urlencoded
```

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": "人工智能是计算机科学的一个分支，它企图了解智能的实质，并生产出一种新的能以人类智能相似的方式做出反应的智能机器。"
}
```

#### 错误响应示例
```json
{
  "code": 500,
  "msg": "调用大语言模型失败: Network error",
  "data": null
}
```

### 3.2 流式请求接口

#### 基本信息
- 接口地址：`GET /llm/ask/stream`
- 请求协议：HTTP/1.1
- 字符编码：UTF-8
- 返回格式：text/event-stream (SSE)

#### 请求参数
| 参数名称 | 类型   | 是否必填 | 描述         |
| -------- | ------ | -------- | ------------ |
| prompt   | String | 是       | 用户提问内容 |

#### 请求示例
```http
GET /llm/ask/stream?prompt=什么是人工智能 HTTP/1.1
Host: localhost:9281
Accept: text/event-stream
```

#### 响应格式
该接口使用 Server-Sent Events (SSE) 协议返回数据，每条消息包含一部分响应内容：

```
data: 人工
data: 智能
data: 是
data: 计算机
...
data: [DONE]
```

当收到 `[DONE]` 表示响应结束。

#### 前端使用示例
```javascript
const eventSource = new EventSource('/llm/ask/stream?prompt=什么是人工智能');
let responseText = '';

eventSource.onmessage = function(event) {
  if (event.data === '[DONE]') {
    eventSource.close();
    console.log('最终响应:', responseText);
  } else {
    responseText += event.data;
    // 实时更新UI
    document.getElementById('response').innerText = responseText;
  }
};

eventSource.onerror = function(err) {
  console.error('EventSource failed:', err);
  eventSource.close();
};
```

### 3.3 会话管理接口

#### 基本信息
- 接口地址：`POST /llm/session/create`
- 请求协议：HTTP/1.1
- 字符编码：UTF-8
- 返回格式：JSON

#### 请求示例
```http
POST /llm/session/create HTTP/1.1
Host: localhost:9281
Content-Type: application/json
```

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": "session_1701234567890"
}
```

#### 错误响应示例
```json
{
  "code": 500,
  "msg": "创建会话失败: Session creation error",
  "data": null
}
```

### 3.4 带上下文请求接口

#### 基本信息
- 接口地址：`POST /llm/ask/context`
- 请求协议：HTTP/1.1
- 字符编码：UTF-8
- 返回格式：JSON

#### 请求参数
| 参数名称 | 类型   | 是否必填 | 描述         |
| -------- | ------ | -------- | ------------ |
| sessionId| String | 是       | 会话ID       |
| prompt   | String | 是       | 用户提问内容 |

#### 请求示例
```http
POST /llm/ask/context?sessionId=session_1701234567890&prompt=我头疼发热 HTTP/1.1
Host: localhost:9281
Content-Type: application/x-www-form-urlencoded
```

#### 成功响应示例
```json
{
  "code": 200,
  "msg": "success",
  "data": "根据您的症状，建议您就诊于内科或感染科。头疼发热可能是感冒或其他病毒感染引起的。"
}
```

#### 错误响应示例
```json
{
  "code": 500,
  "msg": "调用大语言模型失败: Network error",
  "data": null
}
```

### 3.5 流式带上下文请求接口

#### 基本信息
- 接口地址：`GET /llm/ask/context/stream`
- 请求协议：HTTP/1.1
- 字符编码：UTF-8
- 返回格式：text/event-stream (SSE)

#### 请求参数
| 参数名称 | 类型   | 是否必填 | 描述         |
| -------- | ------ | -------- | ------------ |
| sessionId| String | 是       | 会话ID       |
| prompt   | String | 是       | 用户提问内容 |

#### 请求示例
```http
GET /llm/ask/context/stream?sessionId=session_1701234567890&prompt=我头疼发热 HTTP/1.1
Host: localhost:9281
Accept: text/event-stream
```

#### 响应格式
该接口使用 Server-Sent Events (SSE) 协议返回数据，每条消息包含一部分响应内容：

```
data: 根据
data: 您的
data: 症状
...
data: [DONE]
```

当收到 `[DONE]` 表示响应结束。

#### 前端使用示例
```javascript
const eventSource = new EventSource('/llm/ask/context/stream?sessionId=session_1701234567890&prompt=我头疼发热');
let responseText = '';

eventSource.onmessage = function(event) {
  if (event.data === '[DONE]') {
    eventSource.close();
    console.log('最终响应:', responseText);
  } else {
    responseText += event.data;
    // 实时更新UI
    document.getElementById('response').innerText = responseText;
  }
};

eventSource.onerror = function(err) {
  console.error('EventSource failed:', err);
  eventSource.close();
};
```

## 4. 错误码说明

| 错误码 | 描述                   |
| ------ | ---------------------- |
| 200    | 请求成功               |
| 500    | 服务器内部错误         |
| 400    | 请求参数错误           |
| 404    | 接口不存在             |

## 5. 注意事项

1. 流式接口使用GET方法，请确保prompt参数长度不超过URL限制
2. 普通接口使用POST方法，适用于较长的输入内容
3. 流式接口返回的是纯文本流，不是JSON格式
4. 使用流式接口时，前端需要正确处理SSE协议
5. 带上下文的接口需要先创建会话获取sessionId
6. 上下文接口会保留最近10条对话历史供模型参考