package pl.msulima.actors.asking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status;
import akka.testkit.javadsl.TestKit;
import org.junit.After;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.duration.FiniteDuration;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class HelloActorTest {

    private ActorSystem system = ActorSystem.apply();

    @After
    public void setUp() throws Exception {
        FiniteDuration shutdownTimeout = new FiniteDuration(10, TimeUnit.SECONDS);
        system.terminate();
        Await.result(system.whenTerminated(), shutdownTimeout);
    }

    @Test
    public void shouldRespondGeneralKenobi() {
        TestKit testKit = new TestKit(system);
        TestKit translatorActor = new TestKit(system);
        ActorRef actorRef = system.actorOf(HelloActor.props(translatorActor.getRef()));
        actorRef.tell(new HelloActor.Greetings("świecie", Locale.forLanguageTag("pl-PL")), testKit.getRef());

        translatorActor.expectMsgClass(TranslatorActor.Request.class);
        translatorActor.reply(new TranslatorActor.Response("Witaj"));

        HelloActor.Response response = testKit.expectMsgClass(HelloActor.Response.class);
        assertEquals("Witaj ŚWIECIE", response.message);
    }

    @Test
    public void shouldTimeout() {
        TestKit testKit = new TestKit(system);
        TestKit translatorActor = new TestKit(system);
        ActorRef actorRef = system.actorOf(HelloActor.props(translatorActor.getRef()));
        actorRef.tell(new HelloActor.Greetings("świecie", Locale.forLanguageTag("pl-PL")), testKit.getRef());

        testKit.expectMsgClass(Status.Failure.class);
    }
}

