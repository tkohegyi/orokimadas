package website.magyar.adoration.web.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@org.springframework.context.annotation.PropertySource(value = {
        "classpath:i18n/messages_hu.properties",
        "classpath:i18n/messages_en.properties",
        "classpath:i18n/web_hu.properties",
        "classpath:i18n/web_en.properties"
    }, encoding="UTF-8")
public class LangProperties {

    @Autowired
    Environment environment;
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Get language specific strings to be used at java-scripts.
     * Available languages files:
     *         "classpath:i18n/web_hu.properties",
     *         "classpath:i18n/web_en.properties"
     * @param langCode is the 2 char language code
     * @return with the loaded properties as map
     */
    public HashMap<String, String> getWebStrings(String langCode) {
        Map<String, Object> map = getAllPropertiesAsMap();
        HashMap<String, String> retMap = new HashMap<>();
        String keyPrefix = "web." + langCode;
        int startingPos = keyPrefix.length() + 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(keyPrefix)) {
                retMap.put(key.substring(startingPos), String.valueOf(entry.getValue())); //no need to have lang code anymore
            }
        }
        return retMap;
    }

    /**
     * Convert Environment properties to HashMap.
     * @return with the Map.
     */
    private Map<String, Object> getAllPropertiesAsMap() {
        Map<String, Object> map = new HashMap();
        for (PropertySource<?> source : ((AbstractEnvironment) environment).getPropertySources()) {
            if (source instanceof MapPropertySource) {
                map.putAll(((MapPropertySource) source).getSource());
            }
        }
        return map;
    }

}
