package com.jt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	//根据用户请求,跳转页面 
	
	/**
	 * 1.要求参数必须使用/分隔
	 * 2.参数位置必须固定
	 * 3.接受参数时必须使用 { }标识参数  使用特定的注解,并且名称最好一致
	 * @param moduleName
	 * @return
	 */
	@RequestMapping("/page/{moduleName}")
	public String module(@PathVariable String moduleName) {
		
		return moduleName;
	}
}
