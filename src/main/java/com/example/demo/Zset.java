package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Zset {
    private Jedis jedis;

    public Zset() {
        // 连接到本地Redis服务
        try {
            jedis = new Jedis("192.168.176.131",6379,5000);
            jedis.ping(); // 检查连接是否成功
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
            jedis = null;
        }
    }

    // 添加用户及其分数到排行榜
    public void addUserToLeaderboard(String user, double score) {
        if (jedis != null) {
            jedis.zadd("leaderboard", score, user);
        } else {
            System.err.println("Jedis connection is not available.");
        }
    }

    // 获取排行榜前10名
    // 获取排行榜前10名
    public Set<Tuple> getTop10Users() {
        if (jedis != null) {
            List<Tuple> topUsersList = jedis.zrevrangeWithScores("leaderboard", 0, 9);
            return new HashSet<>(topUsersList); // 转换为 Set
        } else {
            System.err.println("Jedis connection is not available.");
            return null;
        }
    }


    // 获取用户的排名
    public long getUserRank(String user) {
        if (jedis != null) {
            Long rank = jedis.zrevrank("leaderboard", user);
            return rank != null ? rank + 1 : -1; // +1 因为排名从0开始，如果用户不存在返回-1
        } else {
            System.err.println("Jedis connection is not available.");
            return -1;
        }
    }

    // 删除用户
    public void removeUser(String user) {
        if (jedis != null) {
            jedis.zrem("leaderboard", user);
        } else {
            System.err.println("Jedis connection is not available.");
        }
    }

    // 关闭Jedis连接
    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static void main(String[] args) {
        Zset leaderboard = new Zset();

        if (leaderboard.jedis == null) {
            System.err.println("Exiting due to Jedis connection failure.");
            return;
        }

        // 添加用户及其分数
        leaderboard.addUserToLeaderboard("Alice", 95.5);
        leaderboard.addUserToLeaderboard("Bob", 88.0);
        leaderboard.addUserToLeaderboard("Charlie", 92.3);
        leaderboard.addUserToLeaderboard("David", 85.0);

        // 获取并打印前10名用户
        System.out.println("Top 10 Users:");
        Set<Tuple> topUsers = leaderboard.getTop10Users();
        if (topUsers != null) {
            for (Tuple tuple : topUsers) {
                System.out.println(tuple.getElement() + ": " + tuple.getScore());
            }
        }

        // 获取并打印某个用户的排名
        long aliceRank = leaderboard.getUserRank("Alice");
        if (aliceRank != -1) {
            System.out.println("Alice's Rank: " + aliceRank);
        } else {
            System.out.println("Alice is not in the leaderboard.");
        }

        // 删除某个用户
        leaderboard.removeUser("Bob");

        // 再次获取并打印前10名用户
        System.out.println("Top 10 Users after removing Bob:");
        topUsers = leaderboard.getTop10Users();
        if (topUsers != null) {
            for (Tuple tuple : topUsers) {
                System.out.println(tuple.getElement() + ": " + tuple.getScore());
            }
        }

        // 关闭Jedis连接
        leaderboard.close();
    }
}