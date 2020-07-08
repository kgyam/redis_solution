package com.kgyam.redis_solution.configuration;

import com.kgyam.redis_solution.component.ClusterConfigurationProperties;
import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


/**
 * redis配置
 * 参考文档：https://docs.spring.io/spring-data/redis/docs/2.3.1.RELEASE/reference/html/#reference
 */
@Configuration
public class RedisConfig {


    @Autowired
    public ClusterConfigurationProperties properties;

    /**
     * 单体redis实例连接
     * <p>
     * For environments reporting non-public addresses through the INFO command (for example, when using AWS),
     * use RedisStaticMasterReplicaConfiguration instead of RedisStandaloneConfiguration.
     * Please note that RedisStaticMasterReplicaConfiguration does not support Pub/Sub because of missing Pub/Sub message propagation across individual servers.
     *
     * @return
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        /*
        读写分离
         */
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA).build();


        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("server", 6379);
        return new LettuceConnectionFactory(serverConfig, clientConfig);


    }


    /**
     * 哨兵模式
     * Configuration Properties
     * spring.redis.sentinel.master: name of the master node.
     * <p>
     * spring.redis.sentinel.nodes: Comma delimited list of host:port pairs.
     * <p>
     * spring.redis.sentinel.password: The password to apply when authenticating with Redis Sentinel
     *
     * @return
     */
//    @Bean
//    public RedisConnectionFactory sentinelConnectionFactory() {
//        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
//                .master("mymaster")
//                .sentinel("127.0.0.1", 26379)
//                .sentinel("127.0.0.1", 26380);
//
//        return new LettuceConnectionFactory(sentinelConfig);
//
//    }


    /**
     * redis集群
     *
     * RedisClusterConfiguration can also be defined through PropertySource and has the following properties:
     *
     * Configuration Properties
     * spring.redis.cluster.nodes: Comma-delimited list of host:port pairs.
     *
     * spring.redis.cluster.max-redirects: Number of allowed cluster redirections.
     * @return
     */
//    @Bean
//    public RedisConnectionFactory clusterConnectionFactory() {
//        return new LettuceConnectionFactory(
//                new RedisClusterConfiguration(properties.getNodes()));
//
//    }


}
