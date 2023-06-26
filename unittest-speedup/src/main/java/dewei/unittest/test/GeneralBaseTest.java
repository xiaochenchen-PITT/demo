package dewei.unittest.test;

import dewei.unittest.FileUtils;
import dewei.unittest.parallel.TestProcessManager;
import dewei.unittest.test.annotation.Except;
import dewei.unittest.test.annotation.Only;
import org.junit.Rule;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.Arrays;
import java.util.List;

public abstract class GeneralBaseTest {

    protected Description currentDescription;

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            currentDescription = description;
        }

        @Override
        protected void succeeded(Description description) {
            try {
                TestProcessManager.getLock().lock();

                TestProcessManager.getAndAdjustSuccessCount(1);
                if (TestProcessManager.isRetryStarted()) {
                    TestProcessManager.getAndAdjustFailedCount(-1);
                }

                TestProcessManager.setLastFinishedTest(description.getDisplayName());

                String content = TestProcessManager.buildProcessContent();
                FileUtils.writeFile(TestProcessManager.getFileName(), content, false);
            } finally {
                TestProcessManager.getLock().unlock();
            }
        }

        @Override
        protected void failed(Throwable e, Description description) {
            try {
                TestProcessManager.getLock().lock();

                if (!TestProcessManager.isRetryStarted()) {
                    TestProcessManager.getAndAdjustFailedCount(1);
                }

                TestProcessManager.setLastFinishedTest(description.getDisplayName());

                String content = TestProcessManager.buildProcessContent();
                FileUtils.writeFile(TestProcessManager.getFileName(), content, false);
            } finally {
                TestProcessManager.getLock().unlock();
            }
        }

        // 因为@Only范围不满足导致的忽略
        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
           skipInner(description);
        }

        private void skipInner(Description description) {
            try {
                TestProcessManager.getLock().lock();

                TestProcessManager.writeIgnore(description.getDisplayName());
            } finally {
                TestProcessManager.getLock().unlock();
            }
        }
    };

    public void checkTestScope() {
        Only only = currentDescription.getAnnotation(Only.class);
        Except except = currentDescription.getAnnotation(Except.class);

        boolean notOnOnlyScope = only == null || only.scope().length < 1;
        boolean notOnExcepScope = except == null || except.scope().length < 1;

        if (notOnOnlyScope && notOnExcepScope) {
            return;
        }

        if (except != null) {
            List<String> exceptScopes = Arrays.asList(except.scope());
            // TODO: 2022/5/25 作判断
            if (exceptScopes.contains("1")) {
                throw new AssumptionViolatedException("case is in excluded scope");
            }
        }

        if (only != null) {
            List<String> onlyScopes = Arrays.asList(only.scope());
            // TODO: 2022/5/25 作判断
            if (!onlyScopes.contains("1")) {
                throw new AssumptionViolatedException("case not in only scope");
            }
        }
    }

}
