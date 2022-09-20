package com.redhat.bobbycar;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/api/carmetrics-aggregated")
@ApplicationScoped
public class CarMetricsAggregatedSocket {

    private static final Logger LOGGER = Logger.getLogger(CarMetricsAggregatedSocket.class.getName());
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Opening CarMetricsAggregatedSocket session "+session.getId());
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("Closing CarMetricsAggregatedSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("ERROR! Closing CarMetricsAggregatedSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Received CarMetricsAggregatedSocket message: "+message);
    }

    public void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    LOGGER.error("Unable to send message: " + result.getException());
                }
            });
        });
    }

}