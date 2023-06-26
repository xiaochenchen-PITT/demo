package dewei.cglibProxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProxyBeanPostProcessor implements BeanPostProcessor {

    /**
     * 需要代理的beanName和对应的接口
     */
    private static final Map<String, Class> beanNameAndClassNameMap = new HashMap<>();

    static {
        beanNameAndClassNameMap.put("beanName1", Demo.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class className = beanNameAndClassNameMap.get(beanName);
        if (className != null) {
            ProxyInterceptor interceptor = new ProxyInterceptor(bean, className.getName());
            Enhancer enhancer = new Enhancer();
            Callback[] callbacks = new Callback[]{interceptor};
            enhancer.setSuperclass(className);
            enhancer.setCallbacks(callbacks);
            return enhancer.create();
        }

        return bean;

    }

}
