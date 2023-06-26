package dewei.unittest;

import dewei.unittest.parallel.TestProcessManager;
import org.junit.Assert;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Utils {

    public static final String TEST_PATH = "unit-test";

    public static final String WEB_APP_PATH = "src/main/webapp";

    public static void filter(Runner runner, Filter filter) throws NoTestsRemainException {
        try {
            runner.getClass().getMethod("filter", Filter.class).invoke(runner, filter);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof NoTestsRemainException) {
                throw (NoTestsRemainException) e.getCause();
            } else {
                throw new RuntimeException(e.getTargetException());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void sort(Runner runner, Sorter sorter) {
        try {
            runner.getClass().getMethod("sort", Sorter.class).invoke(runner, sorter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[16384];

        int read;
        do {
            read = in.read(b);
            if (read > 0) {
                out.write(b, 0, read);
            }
        } while (read != -1);

        return out.toString();
    }

    public static Runner createRunner(Class<? extends Runner> runnerClass, Class<?> testClass) {
        Constructor<? extends Runner> c;
        try {
            c = runnerClass.getDeclaredConstructor(Class.class);
            return c.newInstance(testClass);
        } catch (NoSuchMethodException e) {
            try {
                return runnerClass.newInstance();
            } catch (Exception e1) {
                throw new RuntimeException("Unable to create runner instanceof " + runnerClass, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create runner instanceof " + runnerClass, e);
        }
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        A annotation = clazz.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }

        for (Class<?> ifc : clazz.getInterfaces()) {
            annotation = findAnnotation(ifc, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        if (!Annotation.class.isAssignableFrom(clazz)) {
            for (Annotation ann : clazz.getAnnotations()) {
                annotation = findAnnotation(ann.annotationType(), annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)) {
            return null;
        }

        return findAnnotation(superClass, annotationType);
    }

    public static void initFiles(String indicator) throws Exception {
        FileUtils.writeFile(TestProcessManager.getTempFile(), indicator + "\n0\n0\n0\n0\n0\n0\n", false);

        File directory = new File("");
        String courseFile = directory.getCanonicalPath();
        if (courseFile.contains(TEST_PATH)) {
            courseFile = courseFile.replace(TEST_PATH, "");
        }

        String targetPath = FileUtils.pathJoin(Arrays.asList(courseFile, WEB_APP_PATH));
        String sourcePath = FileUtils.pathJoin(Arrays.asList(courseFile, TEST_PATH, WEB_APP_PATH));
        boolean ret = FileUtils.copyFolder(new File(sourcePath), new File(targetPath));
        Assert.assertTrue(ret);
    }

    public static void onThreadClosed() {
        Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                deleteInitFiles();
            }
        };

        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    public static void deleteInitFiles() {
        try {
            File directory = new File("");
            String courseFile = directory.getCanonicalPath();
            String targetPath = FileUtils.pathJoin(Arrays.asList(courseFile, WEB_APP_PATH));

            FileUtils.deleteDir(new File(targetPath));
            FileUtils.deleteDir(new File(TestProcessManager.getTempFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
