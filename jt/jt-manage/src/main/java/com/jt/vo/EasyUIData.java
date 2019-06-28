package com.jt.vo;

import java.io.Serializable;
import java.util.List;

import com.jt.pojo.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

//该类是展现表格数据
@Data
@Accessors			//链式加载
@NoArgsConstructor	//无参构造
@AllArgsConstructor	//有参构造
public class EasyUIData implements Serializable{
	private Integer total;		//记录总数
	private List<Item> rows;	//展现数据集合
	
}
