package p4535992.util.file;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 4535992 on 08/06/2015.
 * Exposing the spring properties bean in java
 To allow our Java classes to access the properties from the same object as spring, we’ll need to
 extend the PropertyPlaceholderConfigurer so that we can provide a more convenient method for
 retrieving the properties (there is no direct method of retrieving properties!).
 We can extend the spring provided class to allow us to reuse spring’s property resolver in our Java classes:
 */
public class PropertiesKit extends PropertyPlaceholderConfigurer {
    private static Map<String,String> propertiesMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {
        super.processProperties(beanFactory, props);

        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            propertiesMap.put(keyStr, resolvePlaceholder(props.getProperty(keyStr), props));
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name);
    }
}
