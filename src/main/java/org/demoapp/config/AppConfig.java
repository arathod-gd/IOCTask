package org.demoapp.config;

import org.demoapp.beans.Foo;
import org.demoapp.beans.FooImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@ComponentScan("org.demoapp")
//public class AppConfig {
//}


@Configuration()
@ComponentScan("org.demoapp")
public class AppConfig {
    @Bean
    public Foo fooBean(){
        return new FooImpl();
    }
}