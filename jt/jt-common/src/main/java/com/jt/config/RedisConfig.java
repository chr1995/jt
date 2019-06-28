package com.jt.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

//表示redis配置类
@Configuration
@PropertySource("classpath:/properties/redis.properties")
public class RedisConfig {
	/*
	 * @Value("${jedis.host}") private String host;
	 * 
	 * @Value("${jedis.port}") private Integer port;
	 * 
	 * @Bean public Jedis jedis() {
	 * 
	 * return new Jedis(host,port);
	 * 
	 * }
	 */
	@Value("${redis.nodes}")
	public String redisNodes;
	
	@Bean
	public JedisCluster jedisCluster() {
		//1.  按照,号拆分为多个redisNode      redisNode再根据:拆分 获取IP和端口
		Set<HostAndPort> nodes=new HashSet<>();
		String[] redisNode=redisNodes.split(",");
		for (String redisNodeArgs : redisNode) {
			//{ip,端口}
			String host=redisNodeArgs.split(":")[0];
			int port=Integer.parseInt(redisNodeArgs.split(":")[1]);
			HostAndPort hostAndPort=new HostAndPort(host, port);
			nodes.add(hostAndPort);
			
//			String[] args=redisNodeArgs.split(":");
//			String redisNodeIP=args[0];
//			int redisNodePort = Integer.parseInt(args[1]);
//			nodes.add(new HostAndPort(redisNodeIP,redisNodePort));
		}
	return new JedisCluster(nodes);
}	
	/*
	 * @Value("${redis.sentinels}") private String JedisSentinelNodes;
	 * 
	 * @Value("${redis.sentinel.masterName}") private String masterName;
	 * 
	 * @Bean public JedisSentinelPool jedisSentinelPool() { Set<String> sentinels =
	 * new HashSet<>(); sentinels.add(JedisSentinelNodes); return new
	 * JedisSentinelPool(masterName, sentinels); }
	 */
	
//	@Value("${redis.nodes}")
//	private String nodes;//ip:端口,ip:端口...redis.nodes=192.168.58.129:6379,192.168.58.129:6380,192.168.58.129:6381	
//	@Bean
//	public ShardedJedis getShards() {
//		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//		//将nodes中的数据进行分组{ip:端口}
//		String[] node=nodes.split(",");
//		for (String nodeArgs : node) {
//			//{ip,端口}
//			String[] args=nodeArgs.split(":");
//			String nodeIP=args[0];
//			int nodePort = Integer.parseInt(args[1]);
//			JedisShardInfo info = new JedisShardInfo(nodeIP,nodePort);
//			shards.add(info);
//		}
//		return new ShardedJedis(shards);
//	}
}
