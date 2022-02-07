package com.redhat.bobbycar;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/api/carmetrics")
@ApplicationScoped
public class CarMetricsSocket {

    private static final Logger LOGGER = Logger.getLogger(CarMetricsSocket.class.getName());
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Opening CarMetricsSocket session "+session.getId());
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("Closing CarMetricsSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("ERROR! Closing CarMetricsSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Received CarMetricsSocket message: "+message);
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