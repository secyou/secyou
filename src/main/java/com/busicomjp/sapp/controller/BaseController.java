package com.busicomjp.sapp.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.busicomjp.sapp.common.item.CommonAlert;
import com.busicomjp.sapp.common.item.CommonDialog;
import com.busicomjp.sapp.common.item.CommonItems;
import com.busicomjp.sapp.common.item.CommonTask;

public class BaseController {

	@Autowired
	public CommonAlert commonAlert;
	@Autowired
	public CommonDialog commonDialog;
	@Autowired
	public CommonItems commonItems;
	@Autowired
	public CommonTask commonTask;
	
}
