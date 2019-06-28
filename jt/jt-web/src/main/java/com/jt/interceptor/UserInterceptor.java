package com.jt.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.jt.pojo.User;
import com.jt.util.ObjectMapperUtil;
import com.jt.util.UserThreadLocal;

import redis.clients.jedis.JedisCluster;

@Component //将拦截器交给spring容器管理
public class UserInterceptor implements HandlerInterceptor{
	
	@Autowired
	private JedisCluster jedisCluster;
	
	/**
	 * 在spring4版本中要求必须重写3个方法,不管是否需要
	 * 在spring5版本中在接口中添加default属性,则省略
	*	
	 */
	/**
	 * 返回值结果:
	 * true:拦截放行
	 * false:请求拦截 重定向到登录页面
	 * 业务逻辑:
	 * 1.获取Cookie数据
	 * 2.从cookie中获取token(TICKET)
	 * 3.判断redis缓存服务器中是否有数据
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//1.获取Cookie信息
		String token=null;
		Cookie[] cookies=request.getCookies();
		for (Cookie cookie : cookies) {
			if("JT_TICKET".equals(cookie.getName())) {
				token=cookie.getValue();
				break;
			}
		}
		//2.判断token是否有效
		if(!StringUtils.isEmpty(token)) {
			//4.判断redis中是否有数据
			String userJSON=jedisCluster.get(token);
			if(!StringUtils.isEmpty(userJSON)) {
				//redis中有用户数据,可以拦截放行
				//将userJSON转化成user对象
				User user=ObjectMapperUtil.toObject(userJSON, User.class);
				
				//将user数据保存到request域中
				//request.setAttribute("JT_USER",user);
				//用户每次请求都将数据保存到session中,切记用完关闭
				//request.getSession().setAttribute("JT_USER", user);
				
				UserThreadLocal.set(user);
				return true;
			}
		}
		//3.重定向到用户的登录页面
			response.sendRedirect("/user/login.html");
			return false;//表示拦截
		
		//return HandlerInterceptor.super.preHandle(request, response, handler);
		
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	//视图渲染之后
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		
		//request.getSession().removeAttribute("JT_USER");
		
		//调用移除操作
		UserThreadLocal.remove();
		
	}
}
