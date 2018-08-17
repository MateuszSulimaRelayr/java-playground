package pl.msulima.actors.multilingual;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.Locale;

public class HelloActor extends AbstractActor {

    private final ActorRef translatorActor;
    private ActorRef replyTo; // what if more than one sender?
    private Greetings message;

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
                .build();
    }

    private void handle(Greetings message) {
        this.message = message;
        replyTo = sender();
        translatorActor.tell(new TranslatorActor.Request(message.locale), self());
    }

    private void handle(TranslatorActor.Response translation) {
        replyTo.tell(new Response(translation.message + " " + this.message.name.toUpperCase(message.locale)), self());
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
}
