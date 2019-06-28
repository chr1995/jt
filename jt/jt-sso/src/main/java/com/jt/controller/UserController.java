package com.jt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.pojo.User;
import com.jt.service.UserService;
import com.jt.util.ObjectMapperUtil;
import com.jt.vo.SysResult;

import redis.clients.jedis.JedisCluster;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	/**
	 * 业务说明
	 * 校验用户是否存在
	 * http://sso.jt.com/user/check/{param}/{type}
	 * 返回值:SysResult
	 * 	由于是跨域请求,所以返回值必须特殊处理callback(json)
	 */
	@RequestMapping("/check/{param}/{type}")
	public JSONPObject checkUser(@PathVariable String param,
								 @PathVariable Integer type,
								 String callback) {
		JSONPObject object=null;
		try {
			boolean flag=userService.checkUser(param,type);
			object=new JSONPObject(callback, SysResult.ok(flag));
		} catch (Exception e) {
			e.printStackTrace();
			object=new JSONPObject(callback, SysResult.fail());
		}
		return object;
	}
	
		//http://sso.jt.com/user/register
		@RequestMapping("/register")
		public SysResult saveUser(String userJSON) {
			try {
				User user = ObjectMapperUtil.toObject(userJSON, User.class);
				userService.saveUser(user);
				return SysResult.ok();
			} catch (Exception e) {
				e.printStackTrace();
				return SysResult.fail();
			}
		}
	
		/**
		 *利用跨域实现用户信息回显
		 http://sso.jt.com/user/query/9001489aef44572b6a2cd4dd76ee946f?callback=jsonp1560762673777&_=1560762673810
		 */
		@RequestMapping("/query/{ticket}")
		public JSONPObject findUserByTicket(@PathVariable String ticket,String callback) {
			String userJSON=jedisCluster.get(ticket);
			if(StringUtils.isEmpty(userJSON)) 
				return new JSONPObject(callback,SysResult.fail());
			else
				//回传数据需要经过200判断SysResult对象
				return new JSONPObject(callback,SysResult.ok(userJSON));
			
		}
}
