package pl.msulima.actors.errors;

import java.util.Locale;

public class HelloService {

    public String respond(String name) {
        return "Hello " + name.toUpperCase(Locale.ENGLISH);
    }
}
