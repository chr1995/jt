package com.jt.service;

import java.util.List;

import com.jt.vo.EasyUITree;

public interface ItemCatService {

	String findItemCatById(Long itemCatId);

	List<EasyUITree> findItemCatByParentId(Long parentId);

	//List<EasyUITree> findItemCatByCache(Long parentId);
	
}
