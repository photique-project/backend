package com.benchpress200.photique;

import java.util.Arrays;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//@Component
public class BeanLogger implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("=== 현재까지 등록된 빈 목록 ===");
        Arrays.stream(applicationContext.getBeanDefinitionNames())
                .forEach(System.out::println);
        System.out.println("===========================");
    }
}
