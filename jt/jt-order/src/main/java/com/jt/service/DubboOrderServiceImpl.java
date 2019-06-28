package com.jt.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.jt.mapper.OrderItemMapper;
import com.jt.mapper.OrderMapper;
import com.jt.mapper.OrderShippingMapper;
import com.jt.pojo.Order;
import com.jt.pojo.OrderItem;
import com.jt.pojo.OrderShipping;

@Service
public class DubboOrderServiceImpl implements DubboOrderService {
	
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private OrderShippingMapper orderShippingMapper;
	/**
	 * 一个业务逻辑,需要操作三张表
	 * 1.添加事务控制
	 * 2.表数据分析  order orderItems orderShipping
	 * 3.准备orderId '订单号': 登录用户id+"当前时间戳"
	 * 4.操作3个mapper分别入库
	 */
	@Transactional
	@Override
	public String insertOrder(Order order) {
		//1.获取orderId
		String orderId=""+order.getUserId()+System.currentTimeMillis();
		Date date=new Date();
		
		//2.入库订单
		order.setOrderId(orderId)
			 .setStatus(1) //状态：1、未付款2、已付款3、未发货4、已发货5、交易成功6、交易关闭',
			 .setCreated(date)
			 .setUpdated(date);
		orderMapper.insert(order);
		System.out.println("订单入库成功");
		
		//3.入库订单物流
		OrderShipping shipping = order.getOrderShipping();
		shipping.setOrderId(orderId)
				.setCreated(date)
				.setUpdated(date);
		orderShippingMapper.insert(shipping);
		System.out.println("订单物流入库成功");
		
		//4.入库订单商品 1.自己遍历集合分别入库 2.自己编辑sql语句批量入库
		//insert into order_items(xxx,xxx,xxx) values(xxx,xxx,...),(xxx,xxx,xxx),(xxxxxxx)
		
		List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			orderItem.setOrderId(orderId)
					.setCreated(date)
					.setUpdated(date);
			orderItemMapper.insert(orderItem);
		}
		System.out.println("订单商品入库成功");
		return orderId;
	}
	@Override
	public Order findOrderById(String id) {
		Order order = orderMapper.selectById(id);
		OrderShipping shipping = orderShippingMapper.selectById(id);
		QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("order_id", id);
		List<OrderItem> list = orderItemMapper.selectList(queryWrapper);
		order.setOrderShipping(shipping)
			 .setOrderItems(list);
		return order;
	}
	
	
}
