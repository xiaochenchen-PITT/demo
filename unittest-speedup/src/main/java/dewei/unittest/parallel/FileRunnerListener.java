package dewei.unittest.parallel;

import dewei.unittest.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

public class FileRunnerListener extends RunListener {

    @Override
    public synchronized void testRunStarted(Description description) {
        if (!TestProcessManager.isRetryStarted()) {
            int size = getChildrenSize(description, 0);
            TestProcessManager.setTotalCase(size);

            String content = TestProcessManager.buildProcessContent();
            FileUtils.writeFile(TestProcessManager.getFileName(), content, false);
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        try {
            TestProcessManager.getLock().lock();

            TestProcessManager.writeIgnore(description.getDisplayName());
        } finally {
            TestProcessManager.getLock().unlock();
        }
    }

    private int getChildrenSize(Description description, int currentSize) {
        if (CollectionUtils.isEmpty(description.getChildren())) {
            return currentSize + 1;
        }

        int ret = currentSize;
        for (Description desc : description.getChildren()) {
            ret += getChildrenSize(desc, currentSize);
        }

        return ret;
    }
}
