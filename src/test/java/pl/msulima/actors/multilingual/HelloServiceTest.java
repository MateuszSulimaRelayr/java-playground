package pl.msulima.actors.multilingual;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class HelloServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private TranslatorService translatorService;
    @InjectMocks
    private HelloService helloService;

    @Test
    public void shouldRespondGeneralKenobi() {
        when(translatorService.translate(Locale.forLanguageTag("pl-PL"))).thenReturn(completedFuture("Witaj"));

        CompletableFuture<String> response = helloService.respond("świecie", Locale.forLanguageTag("pl-PL"));

        assertThat(response).isCompletedWithValue("Witaj ŚWIECIE");
    }
}
