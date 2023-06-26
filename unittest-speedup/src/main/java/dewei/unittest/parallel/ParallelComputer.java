package dewei.unittest.parallel;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelComputer extends Computer {

    private final boolean classes;
    private final boolean methods;

    public ParallelComputer(boolean classes, boolean methods) {
        this.classes = classes;
        this.methods = methods;
    }

    private static Runner parallelize(Runner runner) {
        if (runner instanceof ParentRunner) {
            ((ParentRunner)runner).setScheduler(new RunnerScheduler() {
                private final ExecutorService executorService = Executors.newFixedThreadPool(8);

                public void schedule(Runnable childStatement) {
                    executorService.submit(childStatement);
                }

                public void finished() {
                    try {
                        executorService.shutdown();
                        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }

                }
            });
        }

        return runner;
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        Runner suite = super.getSuite(builder, classes);
        return this.classes ? parallelize(suite) : suite;
    }

    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        return this.methods ? parallelize(runner) : runner;
    }
}
