package com.nz.rpc.client.beanlife;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
//@Component
public class BeanLife
        implements
        //bean 级生命周期
        BeanNameAware,
        BeanFactoryAware,
        InitializingBean,
        DisposableBean,
        BeanFactoryPostProcessor
        , BeanPostProcessor {

    //bean 级生命周期

    @Override
    public void setBeanName(String s) {
        log.debug("++++++++++++++bean 级生命周期 BeanNameAware  setBeanName +++++++++++++++");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.debug("++++++++++++++bean 级生命周期 BeanNameAware  setBeanFactory +++++++++++++++");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("++++++++++++++bean 级生命周期 BeanNameAware  afterPropertiesSet +++++++++++++++");
    }

    @Override
    public void destroy() throws Exception {
        log.debug("++++++++++++++bean 级生命周期 BeanNameAware  destroy +++++++++++++++");
    }


    @PostConstruct
    public void init() {
        log.debug("++++++++++++++Bean 自身的方法 initMethod+++++++++++++++");
    }

    @PreDestroy
    public void destroyMethod() {
        log.debug("++++++++++++++Bean 自身的方法 destroyMethod+++++++++++++++");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        log.debug("BeanFactoryPostProcessor  postProcessBeanFactory ...............................  ");
    }

    //
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //log.debug("BeanPostProcessor  postProcessBeforeInitialization ...............................  ");
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // log.debug("BeanPostProcessor  postProcessAfterInitialization ...............................  ");
        return null;
    }


}
