package pl.msulima.tracing;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class TracingExecutor implements Executor {

    private final ExecutorService delegate;

    public TracingExecutor(ExecutorService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(Runnable command) {
        TracingScope current = TracingScope.getCurrent();
        delegate.execute(() -> {
            TracingScope.setCurrent(current);
            command.run();
        });
    }
}
