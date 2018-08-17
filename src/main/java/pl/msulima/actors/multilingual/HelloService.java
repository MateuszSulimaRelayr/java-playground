package pl.msulima.actors.multilingual;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class HelloService {

    private final TranslatorService translatorService;

    public HelloService(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    public CompletableFuture<String> respond(String name, Locale locale) {
        return translatorService.translate(locale)
                .thenApply(translation -> translation + " " + name.toUpperCase(locale));
    }
}
