package pl.msulima.actors.errors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;

import java.util.Locale;

public class HelloActor extends AbstractActor {

    public static Props props() {
        return Props.create(HelloActor.class, HelloActor::new);
    }

    private HelloActor() {
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Greetings.class, this::handle)
                .build();
    }

    private void handleAkkaStatusFailure(Greetings message) {
        try {
            sender().tell(new Response("Hello " + message.name.toUpperCase(Locale.ENGLISH)), self());
        } catch (Exception e) {
            // If sender() actor made several calls at once, how will he know which one failed?
            sender().tell(new Status.Failure(e), self());
        }
    }

    private void handle(Greetings message) {
        try {
            sender().tell(new Response("Hello " + message.name.toUpperCase(Locale.ENGLISH)), self());
        } catch (Exception e) {
            sender().tell(new Status.Failure(new HelloActorException(e)), self());
        }
    }

    public static class Greetings {

        private final String name;

        public Greetings(String name) {
            this.name = name;
        }
    }

    public static class Response {

        final String message;

        public Response(String message) {
            this.message = message;
        }
    }

    public static class HelloActorException extends RuntimeException {

        private HelloActorException(Throwable cause) {
            super(cause);
        }
    }
}
