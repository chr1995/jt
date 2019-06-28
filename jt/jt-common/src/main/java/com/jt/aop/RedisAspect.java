package com.jt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.jt.anno.Cache_Find;
import com.jt.enu.KEY_ENUM;
import com.jt.util.ObjectMapperUtil;
import com.jt.util.RedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ShardedJedis;

@Component//将对象交给spring容器管理
@Aspect   //标识切面	
public class RedisAspect {
	/*
	 * required = false 容器初始化时不需要实例化该对象, 
	 * 只有用户使用时才使用, 一般工具类中添加该注解
	 */
	@Autowired(required = false)
	private JedisCluster jedis;
	
	//注入redis哨兵工具API
	//@Autowired(required = false)
	//private RedisService jedis;
	//private Jedis jedis;
	//private ShardedJedis jedis;
	//引入哨兵机制 1000个链接
	//private JedisSentinelPool pool;
	
	
	//使用该方法可以直接获取注解的对象
	//execution(返回值类型    包名.类名.方法名(参数列表))
	/**
	 * key:value
	 * @param cache_find
	 * @return
	 */
					//切入点表达式       首先获取全部注解,其中类型满足
	@Around("@annotation(cache_find)")//环绕通知必须要返回值
	public Object around(ProceedingJoinPoint joinPoint,Cache_Find cache_find) {
		//1.获取key值
		@SuppressWarnings("unused")
		String key=getKey(joinPoint,cache_find);
		//2.根据key查询缓存
		String result=jedis.get(key);
		Object data=null;
		try {
			if(StringUtils.isEmpty(result)) {
			//如果结果为null,表示缓存中没有数据
			//查询数据库
			data=joinPoint.proceed();//表示业务方法执行
			//将数据转化为JSON串
			String json = ObjectMapperUtil.toJSON(data);
			//判断用户是否设定了超时时间
			if(cache_find.secondes()==0) 
				//表示不要超时
				jedis.set(key, json);
			else 	
				jedis.setex(key, cache_find.secondes(), json);
			System.out.println("第一次查询数据库!!!");
		   }else {//如果缓存中有数据,则将json串转换成对象类型返回
			   Class targetClass=getClass(joinPoint);
			   data=ObjectMapperUtil.toObject(result, targetClass);
			   System.out.println("AOP查询缓存");
		   }
		}catch(Throwable e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return data;
		
	}
	//获取返回值类型
	private Class getClass(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getReturnType();
	}
	/**
	 * 1.判断用户key类型 auto empty
	 * @param joinPoint
	 * @param cache_find
	 * @return
	 */
	private String getKey(ProceedingJoinPoint joinPoint, Cache_Find cache_find) {
		//1.获取key类型
		KEY_ENUM key_ENUM = cache_find.keyType();
		//2.判断key类型
		if(key_ENUM.equals(KEY_ENUM.EMPTY)) {
			//表示使用用户自己的的key
			return cache_find.key();
		}
		//表示用户的key需要拼接 key+"_"+第一个参数
		String strArgs=String.valueOf(joinPoint.getArgs()[0]);
		String key=cache_find.key()+"_"+strArgs;
		return key;
	}
	
}
