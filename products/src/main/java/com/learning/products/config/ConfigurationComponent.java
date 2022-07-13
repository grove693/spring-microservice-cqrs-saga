package com.learning.products.config;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationComponent {

    @Bean
    public XStream mySecuredXStream() {
        XStream xStream = new XStream();
        xStream.allowTypesByWildcard(new String[]{"com.learning.**"});
        return xStream;
    }
}
