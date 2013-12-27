package cz.cvut.zuul.oaas.common.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Properties;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ConfigurationSupport implements ApplicationContextAware {

    private ConfigurableApplicationContext applicationContext;

    private ResourceLoader resourceLoader;


    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        this.resourceLoader = new DefaultResourceLoader(applicationContext.getClassLoader());
        loadProperties();
    }

    public void _initSupport(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }


    public Resource resource(String location) {
        return resourceLoader.getResource(location);
    }

    public ClassPathResource classpath(String path) {
        return new ClassPathResource(path);
    }

    public <T> T initialize(FactoryBean<T> factoryBean) throws Exception {
        applicationContext.getBeanFactory().initializeBean(factoryBean, null);

        return factoryBean.getObject();
    }

    public String $(String propertyKey) {
        Environment env = applicationContext.getEnvironment();

        if (env.containsProperty(propertyKey)) {
            return env.getProperty(propertyKey);

        } else {
            return applicationContext.getBeanFactory()
                    .resolveEmbeddedValue(String.format("${%s}", propertyKey));
        }
    }

    public boolean isActive(String... profile) {
        return applicationContext.getEnvironment().acceptsProfiles(profile);
    }

    public boolean isProfileDev() {
        return isActive("dev");
    }


    protected Object getLocalProperties() {
        return new Properties();
    }

    private void loadProperties() {
        Properties props = new Properties();

        Object input = getLocalProperties();
        if (input instanceof Map) {
            props.putAll((Map) input);
        } else {
            throw new IllegalArgumentException("localProperties() must return Map or Properties");
        }

        if (! props.isEmpty()) {
            PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setProperties(props);
            configurer.postProcessBeanFactory(applicationContext.getBeanFactory());
        }
    }
}
