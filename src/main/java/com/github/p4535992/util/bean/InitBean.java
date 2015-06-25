package com.github.p4535992.util.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * This is very basic example of implementing BeanPostProcessor,
 * which prints a bean name before and after initialization of any bean.
 * You can implement more complex logic before and after instantiating a
 * bean because you have access on bean object inside both the post processor methods.
 */
public class InitBean implements BeanPostProcessor{
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeforeInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("AfterInitialization : " + beanName);
        return bean;
        // you can return any other object as well
    }
}
