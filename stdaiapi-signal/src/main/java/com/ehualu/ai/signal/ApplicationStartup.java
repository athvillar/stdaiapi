package com.ehualu.ai.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.bean.PropertyConfig;
import cn.standardai.api.dao.base.LocalSqlSessionFactory;
import cn.standardai.api.dao.exception.DaoException;

@Component("ApplicationStartup") 
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

	@Autowired
	private PropertyConfig propertyConfig;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		init();
	}

	private void init() {
		if (propertyConfig == null) return;
		logger.info("开始初始化");
		Context.setProp(propertyConfig);
		try {
			if (propertyConfig.getMysql() != null) {
				initMysql();
			}
			logger.info("结束初始化");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("结束初始化(" + e.getMessage() + ")");
		}
	}

	private void initMysql() throws DaoException {
		LocalSqlSessionFactory.init(propertyConfig.getMysql());
	}
}
