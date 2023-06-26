package dewei.cglibProxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ProxyInterceptor implements MethodInterceptor {

    private Object target;

    private String className;

    public ProxyInterceptor(Object target, String className) {
        this.target = target;
        this.className = className;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // TODO: 2022/5/30 代理逻辑

        return method.invoke(target, args);
    }
}
