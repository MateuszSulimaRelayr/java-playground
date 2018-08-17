package pl.msulima.actors.basic;

import org.junit.Assert;
import org.junit.Test;

public class HelloServiceTest {

    @Test
    public void shouldRespondHelloWorld() {
        HelloService helloService = new HelloService();
        String response = helloService.respond("world");
        Assert.assertEquals("Hello WORLD", response);
    }
}
