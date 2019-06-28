package com.jt.util;

import com.jt.pojo.User;

//ThreadLocal是线程安全的
public class UserThreadLocal {
	/**
	 * 如何获取多个数据? Map集合
	 * ThreadLocal<Map<k,v>>
	 */
	private static ThreadLocal<User> thread=new ThreadLocal<>(); 
	
	//新增数据
	public static void set(User user) {
		thread.set(user);
	}
	//获取数据
	public static User get() {
		return thread.get();
	}
	
	//使用threadlocal切记内存泄漏
	public static void remove() {
		thread.remove();
	}
}
