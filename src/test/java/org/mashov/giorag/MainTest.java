package org.mashov.giorag;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class MainTest {
    @Test
    public void testWithFutureAndNoAwaitility() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<String> future = expectEventFuture();

        String event = future.get(Durations.FIVE_SECONDS.toMillis(), TimeUnit.MILLISECONDS);

        Assert.assertEquals("KAPALA", event);
    }

    @Test
    public void testWithFutureAndAwaitility() {
        CompletableFuture<String> future = expectEventFuture();

        Awaitility.await()
                .atMost(Durations.FIVE_SECONDS)
                .pollDelay(Duration.ZERO)
                .until(future::isDone);

        String event = future.getNow(null);
        Assert.assertEquals("KAPALA", event);
    }

    private CompletableFuture<String> expectEventFuture() {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                future.complete("KAPALA");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return future;
    }

    @Test
    public void testWithAtomicReference() {
        AtomicReference<String> future = expectEventAtomicRef();

        String event = Awaitility
                .await()
                .atMost(Durations.FIVE_SECONDS)
                .pollDelay(Duration.ZERO)
                .until(future::get, Objects::nonNull);

        Assert.assertEquals("KAPALA", event);
    }

    private AtomicReference<String> expectEventAtomicRef() {
        AtomicReference<String> ref = new AtomicReference<>(null);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                ref.set("KAPALA");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return ref;
    }
}