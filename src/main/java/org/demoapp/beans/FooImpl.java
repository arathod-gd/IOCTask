package org.demoapp.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class FooImpl implements Foo{

    @Override
    public void print(){
        System.out.println("Foo Implementation!");
    }

    @PostConstruct
    public void init() {
        System.out.println("\nFooImpl bean initialized!");
    }

     @PreDestroy
    public void destroy() {
         System.out.println("\nFooImpl bean destroyed!");
     }
}
