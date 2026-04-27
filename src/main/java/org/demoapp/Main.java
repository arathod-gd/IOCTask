package org.demoapp;


import org.demoapp.beans.Bar;
import org.demoapp.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // Get Bar bean (which depends on Foo)
        Bar bar = context.getBean(Bar.class);
        bar.show();
    }
}