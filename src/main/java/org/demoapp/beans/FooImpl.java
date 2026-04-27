package org.demoapp.beans;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class FooImpl implements Foo{
    @Override
    public void print(){
        System.out.println("Foo Implementation!");
    }
}
