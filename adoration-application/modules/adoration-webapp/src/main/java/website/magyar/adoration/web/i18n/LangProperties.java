package website.magyar.adoration.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {
        "classpath:i18n/messages_hu.properties",
        "classpath:i18n/messages_en.properties"
    }, encoding="UTF-8")
public class LangProperties {

    @Autowired
    Environment environment;
    public Environment getEnvironment() {
        return environment;
    }
}
