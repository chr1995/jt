package com.jt.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ImageVO implements Serializable{
	
	private static final long serialVersionUID = -8019017074323385599L;
	
	private Integer error; //0.表示成功  1.表示失败
	private String url;
	private Integer width;
	private Integer height;
}
