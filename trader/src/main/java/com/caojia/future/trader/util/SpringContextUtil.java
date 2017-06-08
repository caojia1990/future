package com.caojia.future.trader.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class SpringContextUtil implements ApplicationContextAware {

	
    private static ApplicationContext applicationContext;
    
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
		
	}
	
	public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
    
    public static Object getBean(String name) throws BeansException{
        return applicationContext.getBean(name);
    }
    
    public static Boolean containsBean(String name){
    	return applicationContext.containsBean(name);
    }
    
}
