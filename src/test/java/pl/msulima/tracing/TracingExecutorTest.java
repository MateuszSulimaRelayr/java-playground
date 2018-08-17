package pl.msulima.tracing;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class TracingExecutorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void shouldCreateScope() {
        // given
        TracingScope.create(1);

        // when
        int scopeId = TracingScope.getCurrent().id;

        //then
        assertThat(scopeId).isEqualTo(1);
    }

    @Test
    public void shouldPreserveScope() {
        // given
        TracingScope.create(1);
        TracingExecutor tracingExecutor = new TracingExecutor(Executors.newFixedThreadPool(10));

        CompletableFuture<Integer> scopeId = CompletableFuture.supplyAsync(() -> {
            log.info("Hello");
            return TracingScope.getCurrent().id;
        }, tracingExecutor);

        // when
        await().until(scopeId::isDone);
        assertThat(scopeId).isCompletedWithValue(1);
    }
}
