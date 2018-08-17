package pl.msulima.actors.concurrency;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Ignore
public class VolatileActorTest {

    private ActorSystem system = ActorSystem.apply();
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int ACTORS_COUNT = PROCESSORS * 4;
    private static final int PER_PROCESSOR = 100_000;

    @After
    public void setUp() throws Exception {
        FiniteDuration shutdownTimeout = new FiniteDuration(10, TimeUnit.SECONDS);
        system.terminate();
        Await.result(system.whenTerminated(), shutdownTimeout);
    }

    @Test
    public void count() throws InterruptedException {
        List<ActorRef> actors = IntStream.range(0, ACTORS_COUNT)
                .mapToObj(i -> system.actorOf(VolatileActor.props()))
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(PROCESSORS);

        long start = System.nanoTime();
        for (int i = 0; i < PROCESSORS; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < PER_PROCESSOR; j++) {
                    for (int k = 0; k < ACTORS_COUNT; k++) {
                        try {
                            actors.get(k).tell(1, ActorRef.noSender());
                        } catch (Throwable x) {
                            x.printStackTrace();
                        }
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        actors.forEach(actorRef -> {
            TestKit probe = new TestKit(system);
            actorRef.tell("", probe.getRef());
            FiniteDuration apply = FiniteDuration.apply(60L, TimeUnit.SECONDS);
            probe.expectMsg(apply, PROCESSORS * PER_PROCESSOR);
        });

        System.out.println(ACTORS_COUNT * PROCESSORS * PER_PROCESSOR / (double) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) * 1000);
    }
}
