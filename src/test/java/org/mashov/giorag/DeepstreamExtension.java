package org.mashov.giorag;

import com.google.gson.JsonObject;
import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamFactory;
import io.deepstream.EventListener;
import io.deepstream.LoginResult;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DeepstreamExtension {
    private static final DeepstreamClient DEEPSTREAM = getDeepstreamClient();

    private static class EventSubscription {
        private final String eventName;
        private final EventListener eventListener;

        private EventSubscription(String eventName, EventListener eventListener) {
            this.eventName = eventName;
            this.eventListener = eventListener;

            DEEPSTREAM.event.subscribe(eventName, eventListener);
        }

        private void dispose() {
            DEEPSTREAM.event.unsubscribe(eventName, eventListener);
        }
    }

    private static final List<EventSubscription> EVENT_SUBSCRIPTIONS = new ArrayList<>();

    static void emit(String eventName, Object data) {
        System.out.println("Emitting to " + eventName + " - " + data);
        DEEPSTREAM.event.emit(eventName, data);
    }

    static void subscribeToEvent(String eventName, EventListener eventListener) {
        System.out.println("Subscribing to " + eventName);
        EVENT_SUBSCRIPTIONS.add(new EventSubscription(eventName, eventListener));
    }

    public static void tearDown() {
        System.out.println("Clearing " + EVENT_SUBSCRIPTIONS.size() + " subscriptions");
        EVENT_SUBSCRIPTIONS.forEach(EventSubscription::dispose);
        EVENT_SUBSCRIPTIONS.clear();
        System.out.println("Done");
    }

    private static DeepstreamClient getDeepstreamClient() {
        try {
            System.out.println("Logging in to deepstream");
            return DeepstreamFactory.getInstance().getClient("0.0.0.0:6020");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
