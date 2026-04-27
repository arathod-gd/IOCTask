package org.demoapp.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class Bar {
    private final Foo foo;

    public Bar(Foo foo) {
        this.foo = foo;
    }

    public void show(){
        foo.print();
    }


    @PostConstruct
    public void init() {
        System.out.println("\nBar bean initialized!");
    }


    @PreDestroy
    public void destroy() {
        System.out.println("\nBar bean destroyed!");
    }

}
