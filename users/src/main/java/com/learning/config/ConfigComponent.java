package com.learning.config;

import com.learning.core.query.FetchUserPaymentDetailsQuery;
import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigComponent {

    @Bean
    public XStream mySecuredXStream() {
        XStream xStream = new XStream();
        xStream.allowTypesByWildcard(new String[]{"com.learning.**", "com.learning.core.query.**"});
        //xStream.allowTypes(new Class[]{FetchUserPaymentDetailsQuery.class});
        return xStream;
    }
}
