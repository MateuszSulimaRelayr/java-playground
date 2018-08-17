package pl.msulima.actors.concurrency;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Ignore
public class LockTest {

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int ACTORS_COUNT = PROCESSORS * 4;
    private static final int PER_PROCESSOR = 10_000;

    @Test
    public void count() throws InterruptedException {
        List<SumLock> actors = IntStream.range(0, ACTORS_COUNT)
                .mapToObj(i -> new SumLock())
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(PROCESSORS);

        long start = System.nanoTime();
        for (int i = 0; i < PROCESSORS; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < PER_PROCESSOR; j++) {
                    for (int k = 0; k < ACTORS_COUNT; k++) {
                        try {
                            actors.get(k).add(1);
                        } catch (Throwable x) {
                            x.printStackTrace();
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(300, TimeUnit.SECONDS);

        actors.forEach(actorRef -> {
            Assertions.assertThat(actorRef.get()).isEqualTo(PROCESSORS * PER_PROCESSOR);
        });

        System.out.println(ACTORS_COUNT * PROCESSORS * PER_PROCESSOR / (double) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) * 1000);
    }
}
