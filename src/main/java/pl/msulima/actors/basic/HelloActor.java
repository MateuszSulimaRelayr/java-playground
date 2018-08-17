package pl.msulima.actors.basic;

import akka.actor.AbstractActor;
import akka.actor.Props;
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

    private void handle(Greetings message) {
        sender().tell(new Response("Hello " + message.name.toUpperCase(Locale.ENGLISH)), self());
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
}
