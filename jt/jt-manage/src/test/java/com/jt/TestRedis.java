package com.jt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.pojo.Item;
import com.jt.pojo.ItemDesc;
import com.jt.pojo.User;

import redis.clients.jedis.Jedis;

public class TestRedis {
	//Mybatis~~~对象/集合~~~Redis
	//String类型操作方式 : 配置文件3处	防火墙
	//IP:端口号
	@Test
	public void testString() {
		Jedis jedis = new  Jedis("192.168.58.129",6379);
		jedis.set("1902", "1902班");
		jedis.expire("1902", 10);
		System.out.println(jedis.get("1902"));
		System.out.println(jedis.type("1902"));
		System.out.println(jedis.ttl("1902"));
	}
	
	//设定数据的超时方法2种
	//分布式锁!!!
	@Test
	public void testTimeOut() throws Exception {
		Jedis jedis = new Jedis("192.168.58.129",6379);
		jedis.setex("aa", 2, "aa");//设定超时时间
		System.out.println(jedis.get("aa"));
		
		Thread.sleep(3000);
		//当key不存在时操作正常.当key存在时.则操作失败
		Long result = jedis.setnx("aa", "bb");
		System.out.println("获取输出数据"+result+":"+jedis.get("aa"));
	}
	
	//实现对象转化JSON
	@Test
	public void objectToJSON() throws IOException {
		ItemDesc itemDesc = new ItemDesc();
		itemDesc.setItemId(1000L).setItemDesc("测试方法");
		ObjectMapper mapper=new ObjectMapper();
		//转化JSON时必须get/set方法
		String json=mapper.writeValueAsString(itemDesc);
		System.out.println(json);
		
	//将json串转化成对象
		ItemDesc desc2 = mapper.readValue(json, ItemDesc.class);
		System.out.println("测试对象"+desc2);
	}
	
	//实现List集合与JSON转化
	@Test
	public void listToJSON() throws IOException {
		ItemDesc itemDesc1 = new ItemDesc();
		itemDesc1.setItemId(1000L).setItemDesc("测试方法");
		ItemDesc itemDesc2 = new ItemDesc();
		itemDesc2.setItemId(1000L).setItemDesc("测试方法");
		List<ItemDesc> list = new ArrayList<ItemDesc>();
		list.add(itemDesc1);
		list.add(itemDesc2);
		
		ObjectMapper mapper=new ObjectMapper();
		String json=mapper.writeValueAsString(list);
		System.out.println("集合转化为JSON"+json);
		//将数据保存到redis中
		Jedis jedis=new Jedis("192.168.58.129",6379);
		jedis.set("itemDescList", json);
		//从redis中获取数据
		String result=jedis.get("itemDescList");
		@SuppressWarnings("unused")
		List<ItemDesc> descList = mapper.readValue(result, list.getClass());
		System.out.println(descList);
	}
	
	
	/**
	 * 3.利用Redis保存业务数据  数据库
	 * 数据路数据:对象Object
	 * String类型要求只能存储字符串类型
	 * item~~ JSON ~~~字符串
	 */
	public void testSetObject() {
		Item item = new Item();
		item.setId(100L).setTitle("测试数据");
		//Jedis jedis = new  Jedis("192.168.58.129",6379);
		//jedis.set("item", );
		
	}
	
	
	//研究转化关系
	/*
	 * class User{ private Integer id; private String name; private Integer age;
	 * private String sex;
	 * 
	 * public Integer getId() { return id; }
	 * 
	 * public void setId(Integer id) { this.id = id; }
	 * 
	 * public String getName() { return name; }
	 * 
	 * public void setName(String name) { this.name = name; }
	 * 
	 * public Integer getAge() { return age; }
	 * 
	 * public void setAge(Integer age) { this.age = age; }
	 * 
	 * public String getSex() { return sex; }
	 * 
	 * public void setSex(String sex) { this.sex = sex; }
	 * 
	 * }
	 */
	
	//user转换成JSON串
	/**
	 * 1.首先获取对象的getXXX方法
	 * 2.将get去掉,之后首字母小写获取属性的名称
	 * 3.之后将属性名称:属性的值进行拼接
	 * 4.形成json串(字符串)
	 * @throws IOException 
	 */
	@Test
	public void userToJSON() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
//		user.setId(1000);
//		user.setName("json测试");
//		user.setAge(18);
//		user.setSex("男");
		String json = mapper.writeValueAsString(user);
		System.out.println(json);
		
		//以下方法实现了数据的转化  jsonToUser
		/**
		 * 1.获取userJSON串
		 * 通过json串获取json中key
		 * 2.根据class类型的反射机制实例化对象
		 * 根据key调用setKey方法为对象赋值
		 * 最终生成对象
		 */
		User user2 = mapper.readValue(json, User.class);
		System.out.println(user2);
	}
	
	
}
