package pl.msulima.tracing;

import org.slf4j.MDC;

import java.util.Objects;

public class TracingScope {
    final int id;

    private static ThreadLocal<TracingScope> context = new ThreadLocal<>();

    public static TracingScope create(int id) {
        TracingScope value = new TracingScope(id);
        setCurrent(value);
        return value;
    }

    public static TracingScope getCurrent() {
        return context.get();
    }

    private TracingScope(int id) {
        this.id = id;
    }

    public static void setCurrent(TracingScope current) {
        context.set(current);
        MDC.put("traceId", Integer.toString(current.id));
    }

    @Override
    public String toString() {
        return "TracingScope{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TracingScope that = (TracingScope) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
