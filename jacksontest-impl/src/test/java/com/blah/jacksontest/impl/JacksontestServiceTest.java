package com.blah.jacksontest.impl;

import com.blah.jacksontest.api.JacksontestService;
import org.junit.Test;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;

public class JacksontestServiceTest {
    @Test
    public void shouldStorePersonalizedGreeting() {
        withServer(defaultSetup().withCassandra(), server -> {

            JacksontestService service = server.client(JacksontestService.class);
            String msg1 = service.hello("Alice").invoke().toCompletableFuture().get(5, SECONDS);
            assert msg1.equals("success");

        });
    }
}
