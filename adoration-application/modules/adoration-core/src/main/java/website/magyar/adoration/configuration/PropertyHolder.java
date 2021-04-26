package website.magyar.adoration.configuration;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the application configuration properties in a key-value store.
 */
@Component
public class PropertyHolder {
    private final Map<String, String> properties = new HashMap<>();

    /**
     * Adds a new property to the property holding map.
     *
     * @param key   the key of the property
     * @param value the value of the property
     */
    public void addProperty(final String key, final String value) {
        properties.put(key, value);
    }

    /**
     * Returns a property with a String key.
     *
     * @param key the key of the property
     * @return the value matching the key
     */
    public String get(final String key) {
        return properties.get(key);
    }

    /**
     * Returns a property with an Integer key.
     *
     * @param key the key of the property
     * @return the value matching the key
     */
    public Integer getInt(final String key) {
        return Integer.valueOf(properties.get(key));
    }

    /**
     * Returns a property with a Long key.
     *
     * @param key the key of the property
     * @return the value matching the key
     */
    public Long getLong(final String key) {
        return Long.valueOf(properties.get(key));
    }
}
