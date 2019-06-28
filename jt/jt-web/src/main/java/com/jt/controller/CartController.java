package com.jt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;

import com.jt.pojo.Cart;
import com.jt.pojo.User;
import com.jt.service.DubboCartService;
import com.jt.util.UserThreadLocal;
import com.jt.vo.SysResult;

@Controller
@RequestMapping("/cart")
public class CartController {
	@Reference(timeout=3000,check = false)
	private DubboCartService cartService;
	/**
	 * 1.实现商品列表信息展现
	 * 2.页面取值:${cartList}
	 */
	@RequestMapping("/show")
	public String findCartList(Model model,HttpServletRequest request) {
		//Long userId=7L;//暂时写死
		
		//动态获取用户信息
		/*
		 * User user=(User)request.getAttribute("JT_USER"); Long userId = user.getId();
		 */
		
		Long userId = UserThreadLocal.get().getId();
		
		List<Cart> cartList=cartService.findCartListByUserId(userId);
		model.addAttribute("cartList", cartList);
		return "cart";//返回页面逻辑名称
	}
	
	/**
	 * 实现购物车数量的修改
	 * http://www.jt.com/cart/update/num/562379/8 
	 * 规定:如果url参数中使用restFul风格获取数据时,
	 * 介绍参数是对象并且属性匹配,则可以使用对象接收
	 */
	@RequestMapping("/update/num/{itemId}/{num}")
	@ResponseBody
	public SysResult updateCartNum(Cart cart) {
		try {
			//Long userId=7L;
			Long userId = UserThreadLocal.get().getId();
			cart.setUserId(userId);
			cartService.updateCartNum(cart);
			return SysResult.ok();
		} catch (Exception e) {
			e.printStackTrace();
			return SysResult.fail();
		}
	}
	
	/**
	 * 实现购物车删除操作
	 */
	@RequestMapping("/delete/{itemId}")
	public String deleteCart(Cart cart) {
		//Long userId=7L;
		Long userId = UserThreadLocal.get().getId();
		cart.setUserId(userId);
		cartService.deleteCart(cart);
		//重定向到购物车列表页面
		return "redirect:/cart/show.html";
	}
	
	/**
	 * 新增购物车
	 * 页面表单提交 发起post请求
	 * 携带购物车参数
	 */
	@RequestMapping("/add/{itemId}")
	public String insertCart(Cart cart) {
		//Long userId=7L;
		Long userId = UserThreadLocal.get().getId();
		cart.setUserId(userId);
		cartService.insertCart(cart);
		
		//新增数据之后,展现购物车列表信息
		return "redirect:/cart/show.html";
	}
	
}
