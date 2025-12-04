package com.shanzhu.hospital.entity.vo;

import lombok.Data;

@Data
public class ChatMessage {
    private String role; // "system", "user", "assistant"
    private String content;
    private Long timestamp;
    
    public ChatMessage() {}
    
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
}