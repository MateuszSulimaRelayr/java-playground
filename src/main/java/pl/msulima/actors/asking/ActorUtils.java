package pl.msulima.actors.asking;

import akka.actor.ActorContext;
import akka.actor.Cancellable;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.FiniteDuration;

public class ActorUtils {

    public static Cancellable sendSelfAfter(Object msg, FiniteDuration duration, ActorContext ctx) {
        ExecutionContext ec = ctx.system().dispatcher();
        return ctx.system().scheduler().scheduleOnce(duration, ctx.self(), msg, ec, ctx.self());
    }
}
