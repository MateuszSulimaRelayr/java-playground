package pl.msulima.actors.multilingual;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TranslatorService {

    public CompletableFuture<String> translate(Locale locale) {
        return CompletableFuture.completedFuture("Signor");
    }
}
