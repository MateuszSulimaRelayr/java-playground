package pl.msulima.actors.multilingual;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.Locale;

public class TranslatorActor extends AbstractActor {

    public static Props props() {
        return Props.create(TranslatorActor.class, TranslatorActor::new);
    }

    private TranslatorActor() {
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Request.class, this::handle)
                .build();
    }

    private void handle(Request message) {
        sender().tell(new Response("Hello " + message), self());
    }

    public static class Request {

        private final Locale country;

        public Request(Locale country) {
            this.country = country;
        }
    }

    public static class Response {

        final String message;

        public Response(String message) {
            this.message = message;
        }
    }
}
