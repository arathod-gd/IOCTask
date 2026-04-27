package org.demoapp.beans;

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
}
