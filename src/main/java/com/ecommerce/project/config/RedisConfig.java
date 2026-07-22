package com.ecommerce.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public Jedis jedis() {

        HostAndPort hostAndPort = new HostAndPort(host, port);

        DefaultJedisClientConfig config =
                DefaultJedisClientConfig.builder()
                        .user(username)
                        .password(password)
                        .build();

        return new Jedis(hostAndPort, config);
    }
}
