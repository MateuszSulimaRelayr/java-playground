package pl.msulima.actors.impatient;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.FiniteDuration;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HelloActor extends AbstractActor {

    private static final FiniteDuration TIMEOUT = new FiniteDuration(1, TimeUnit.SECONDS);

    private final ActorRef translatorActor;
    private ActorRef replyTo;
    private Greetings message;
    private Cancellable timeoutSchedule;

    public static Props props(ActorRef translatorActor) {
        return Props.create(HelloActor.class, () -> new HelloActor(translatorActor));
    }

    private HelloActor(ActorRef translatorActor) {
        this.translatorActor = translatorActor;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Greetings.class, this::handle)
                .match(TranslatorActor.Response.class, this::handle)
                .match(Timeout.class, t -> handleTimeout())
                .build();
    }

    private void handle(Greetings message) {
        this.message = message;
        replyTo = sender();
        translatorActor.tell(new TranslatorActor.Request(message.locale), self());
        timeoutSchedule = ActorUtils.sendSelfAfter(new Timeout(), TIMEOUT, context());
    }

    private void handleTimeout() {
        replyTo.tell(new Status.Failure(new HelloActorException("Timeout")), self());
    }

    private void handle(TranslatorActor.Response translation) {
        timeoutSchedule.cancel();
        replyTo.tell(new Response(translation.message + " " + this.message.name.toUpperCase(message.locale)), self());
    }

    private static class Timeout {
    }

    public static class Greetings {

        private final String name;

        private final Locale locale;

        public Greetings(String name, Locale locale) {
            this.name = name;
            this.locale = locale;
        }

    }

    public static class Response {


        final String message;

        public Response(String message) {
            this.message = message;
        }

    }

    static class HelloActorException extends RuntimeException {

        private HelloActorException(String message) {
            super(message);
        }
    }
}
