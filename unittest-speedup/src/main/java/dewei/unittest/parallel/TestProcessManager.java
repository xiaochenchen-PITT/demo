package dewei.unittest.parallel;

import dewei.unittest.FileUtils;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TestProcessManager {

    /**
     * 通过一个临时文件，实现多进程之间的通信 fixme
     * 第一行：进度文件前缀
     * 第二行：total case总数
     * 第三行：当前success count
     * 第四行：当前fail count
     * 第五行：当前ignore count
     * 第六行：当前正在执行的case
     * 第七行：当前重试是否已开始（1：开始 0：未开始）
     */
    @Getter
    private static final String tempFile = "tempFile";

    @Getter
    private static ReentrantLock lock = new ReentrantLock();

    private static List<String> getTempFileContent() throws Exception {
        File file = new File(tempFile);
        if (!file.exists()) {
            file.createNewFile();
        }

        return FileUtils.readFile(tempFile);
    }

    public static synchronized String getFileNamePrefix() {
        try {
            return getTempFileContent().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static synchronized void setFileNamePrefix(String prefix) {
        try {
            List<String> contents = getTempFileContent();
            if (CollectionUtils.isEmpty(contents)) {
                contents.add(prefix);
            } else {
                contents.set(0, prefix);
            }

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized int getTotalCase() {
        try {
            return Integer.valueOf(getTempFileContent().get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static synchronized void setTotalCase(int totalCase) {
        try {
            List<String> contents = getTempFileContent();
            contents.set(1, String.valueOf(totalCase));

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized int getSuccessCount() {
        try {
            return Integer.valueOf(getTempFileContent().get(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static synchronized int getFailedCount() {
        try {
            return Integer.valueOf(getTempFileContent().get(3));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static synchronized int getIgnoreCount() {
        try {
            return Integer.valueOf(getTempFileContent().get(4));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getFileName() {
        return getFileNamePrefix() + "_test_process.txt";
    }

    public static synchronized String getLastFinishedTest() {
        try {
            return getTempFileContent().get(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static synchronized void setLastFinishedTest(String testcase) {
        try {
            List<String> contents = getTempFileContent();
            contents.set(5, testcase);

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean isRetryStarted() {
        try {
            return Integer.valueOf(getTempFileContent().get(6)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static synchronized void setRetryStarted() {
        try {
            List<String> contents = getTempFileContent();
            contents.set(6, "1");

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void getAndAdjustSuccessCount(int offset) {
        try {
            List<String> contents = getTempFileContent();
            int successCount = Integer.valueOf(contents.get(2));
            contents.set(2, String.valueOf(successCount + offset));

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void getAndAdjustFailedCount(int offset) {
        try {
            List<String> contents = getTempFileContent();
            int failedCount = Integer.valueOf(contents.get(3));
            contents.set(3, String.valueOf(failedCount + offset));

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void getAndAdjustIgnoreCount(int offset) {
        try {
            List<String> contents = getTempFileContent();
            int ignoreCount = Integer.valueOf(contents.get(4));
            contents.set(4, String.valueOf(ignoreCount + offset));

            StringBuffer sb = new StringBuffer();
            for (String content : contents) {
                sb.append(content).append("\n");
            }
            FileUtils.writeFile(tempFile, sb.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String buildProcessContent() {
        StringBuffer sb = new StringBuffer();
        sb.append("test started, total test case: ").append(getTotalCase()).append("\n").append("\n");

        int successCount = getSuccessCount();
        int failedCount = getFailedCount();
        int ignoreCount = getIgnoreCount();
        int finishedCount = successCount + failedCount + ignoreCount;

        if (finishedCount > 0) {
            sb.append("====================================== Process ======================================").append("\n")
                .append("finished: ").append(finishedCount).append("/").append(getTotalCase())
                .append(TestProcessManager.isRetryStarted() ? " retrying...": "").append("\n")
                .append("last finished test: ").append(getLastFinishedTest()).append("\n")
                .append("\n")
                .append("success: ").append(successCount).append("\n")
                .append("failed: ").append(failedCount).append("\n")
                .append("ignored: ").append(ignoreCount).append("\n")
                .append("\n");
        }

        return sb.toString();
    }

    public static String buildResultContent(List<Failure> failures) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = dateFormat.format(new Date());

        StringBuffer sb = new StringBuffer();
        sb.append("====================================== Result ======================================").append("\n")
            .append(String.format("%s, Paralled Test Finished. count: %d, Success: %d, Failures: %s, IgnoresL %d, total rt: % ms",
                dateNow, getTotalCase(), getSuccessCount(), getFailedCount(), getIgnoreCount(),
                System.currentTimeMillis() - ParallelRun.getStartTimeMillis())).append("\n")
            .append("\n")
            .append("================================ Failure Result ======================================").append("\n");

        if (CollectionUtils.isNotEmpty(failures)) {
            for (Failure failure : failures) {
                sb.append(failure.getTestHeader() + "\n" + failure.getTrace()).append("\n\n\n");
            }
        } else {
            sb.append("NA");
        }

        return sb.toString();
    }

    public static void writeIgnore(String displayName) {
        TestProcessManager.getAndAdjustIgnoreCount(1);
        TestProcessManager.setLastFinishedTest(displayName);

        String content = TestProcessManager.buildProcessContent();
        FileUtils.writeFile(TestProcessManager.getFileName(), content, false);
    }

}
