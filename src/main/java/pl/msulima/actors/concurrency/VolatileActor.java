package pl.msulima.actors.concurrency;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.concurrent.locks.LockSupport;

public class VolatileActor extends AbstractActor {

    private Holder holder = new Holder();

    public VolatileActor() {
    }

    public static Props props() {
        return Props.create(VolatileActor.class, VolatileActor::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Integer.class, this::handle)
                .match(String.class, this::handle)
                .build();
    }

    private void handle(Integer x) {
        LockSupport.parkNanos(1000);
        holder.x += x;
    }

    private void handle(String x) {
        sender().tell(holder.x, self());
    }

    private static class Holder {
        int x;
    }
}
