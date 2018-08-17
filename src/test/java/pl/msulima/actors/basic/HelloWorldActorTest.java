package pl.msulima.actors.basic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.After;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class HelloWorldActorTest {

    private ActorSystem system = ActorSystem.apply();

    @After
    public void setUp() throws Exception {
        FiniteDuration shutdownTimeout = new FiniteDuration(10, TimeUnit.SECONDS);
        system.terminate();
        Await.result(system.whenTerminated(), shutdownTimeout);
    }

    @Test
    public void shouldRespondHelloWorld() {
        ActorRef actorRef = system.actorOf(HelloActor.props());
        TestKit testKit = new TestKit(system);
        actorRef.tell(new HelloActor.Greetings("world"), testKit.getRef());

        HelloActor.Response response = testKit.expectMsgClass(HelloActor.Response.class);
        assertEquals("Hello WORLD", response.message);
    }
}
