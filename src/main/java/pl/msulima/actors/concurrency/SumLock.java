package pl.msulima.actors.concurrency;

import java.util.concurrent.locks.LockSupport;

public class SumLock {

    private Holder holder = new Holder();

    public synchronized void add(int x) {
        LockSupport.parkNanos(1000);
        holder.x += x;
    }

    public synchronized int get() {
        return holder.x;
    }

    private static class Holder {
        volatile int x;
    }
}
