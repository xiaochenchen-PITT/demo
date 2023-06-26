package dewei.toolcenter;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceManager {

    /**
     * java方法的获取入参
     *
     * @param serviceName
     * @param methodName
     * @return
     */
    public Map<String, String> getMethodDetail(String serviceName, String methodName) {
        Class<?> beanType;
        try {
            beanType = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            return null;
        }

        if (SpringUtils.getBean(beanType) == null) {
            System.out.println("null");
        }

        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        Method[] methods;
        if (beanType.getName().contains("CGLIB$$")) {
            methods = beanType.getSuperclass().getDeclaredMethods();
        } else {
            methods = beanType.getDeclaredMethods();
        }

        Method theMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                theMethod = method;
                break;
            }
        }

        if (theMethod == null) {
            System.out.println("null 2");
            return null;
        }

        List<String> paramNames = Arrays.asList(u.getParameterNames(theMethod));
        List<String> paramTypes = Arrays.stream(theMethod.getParameters()).map(parameter -> parameter.getType().getTypeName()).collect(Collectors.toList());
        Map<String, String> paramMap = new LinkedHashMap<>();
        for (int i = 0; i < paramNames.size(); i++) {
            paramMap.put(paramNames.get(i), paramTypes.get(i));
        }

        return paramMap;
    }
}
