package pl.msulima.actors.asking;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import scala.concurrent.duration.FiniteDuration;

import java.util.Locale;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class HelloActor extends AbstractActor {

    private static final Timeout TIMEOUT = Timeout.durationToTimeout(new FiniteDuration(1, TimeUnit.SECONDS));

    private final ActorRef translatorActor;

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
                .build();
    }

    private void handle(Greetings message) {
        TranslatorActor.Request request = new TranslatorActor.Request(message.locale);

        CompletionStage<String> future = PatternsCS.ask(translatorActor, request, TIMEOUT)
                .thenApply(response -> {
                    TranslatorActor.Response translation = (TranslatorActor.Response) response;
                    return translation.message + " " + message.name.toUpperCase(message.locale);
                });

        PatternsCS.pipe(future.thenApply(HelloActor.Response::new), context().dispatcher())
                .pipeTo(sender(), self());
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
