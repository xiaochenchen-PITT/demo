package dewei.cglibProxy;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class Demo {

    /**
     * 动态代理
     * @param clazz 需要代理的方法
     */
    public Class<?> demo(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        Callback[] callbacks = new Callback[] {
            new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                    // TODO: 2022/5/25 代理逻辑
                    if (method.getName().equals("method name")) {
                        return null;
                    }

                    return method.invoke(o, args);
                }
            }
        };
        enhancer.setSuperclass(clazz);
        enhancer.setCallbacks(callbacks);

        return (Class<?>) enhancer.create();
    }
}
