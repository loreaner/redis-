package com.example.demo;

import redis.clients.jedis.Jedis;

public class RedisNativeExample {
    public static void main(String[] args) {
        // 配置参数（根据你的Redis实际配置修改）
        String host = "192.168.176.131"; // Redis服务器IP
        int port = 6379;                // Redis端口
        String password = null;         // 密码（如果没有设为null）

        try (Jedis jedis = new Jedis(host, port)) {
            // 认证（如果有密码）
            if (password != null) {
                jedis.auth(password);
            }

            // 测试连接
            System.out.println("Ping: " + jedis.ping());

            // 实际业务操作（示例）
            jedis.set("testKey", "Hello Redis");
            System.out.println("Get: " + jedis.get("testKey"));

        } catch (Exception e) {
            System.err.println("Redis连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}