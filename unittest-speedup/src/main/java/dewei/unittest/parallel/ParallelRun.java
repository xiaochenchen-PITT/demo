package dewei.unittest.parallel;

import dewei.unittest.FileUtils;
import dewei.unittest.Utils;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;

public class ParallelRun {

    private static JUnitCore jUnitCore;

    @Getter
    private static Long startTimeMillis;

    public static void run(Class[] cls, int retryTimes, String indicator) throws Exception {
        try {
            Utils.initFiles(indicator);

            startTimeMillis = System.currentTimeMillis();

            jUnitCore = new JUnitCore();
            jUnitCore.addListener(new FileRunnerListener());

            // 以类和方法维度，并发执行用例
            Result result = jUnitCore.run(new ParallelComputer(true, true), cls);

            // 执行完成，开始失败重试
            TestProcessManager.setRetryStarted();

            List<Failure> failures = result.getFailures();
            for (int i = 0; i < retryTimes; i++) {
                List<Failure> newFailures = new ArrayList<>();
                for (Failure failure : failures) {
                    Result retryRet = new Result();
                    try {
                        // AssumptionViolatedException算作是ignore的
                        if (failure.getException() instanceof org.junit.internal.AssumptionViolatedException) {
                            continue;
                        }

                        retryRet = runTest(failure.getDescription().getClassName(), failure.getDescription().getMethodName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newFailures.addAll(retryRet.getFailures());
                }

                failures = newFailures;
                if (CollectionUtils.isEmpty(failures)) {
                    break;
                }
            }

            String content = TestProcessManager.buildResultContent(failures);
            FileUtils.writeFile(TestProcessManager.getFileName(), content, true);
        } finally {
            Utils.deleteInitFiles();
        }
    }

    /**
     * run单个test
     *
     * @param testClassName case的全名
     * @param testMethodName case的方法名
     * @return
     * @throws Exception
     */
    private static Result runTest(String testClassName, String testMethodName) throws Exception {
        Class testClazz = Class.forName(testClassName);
        Request request = Request.method(testClazz, testMethodName);

        return jUnitCore.run(request);
    }
}
