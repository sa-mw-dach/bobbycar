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

@ServerEndpoint("/api/zonechange")
@ApplicationScoped
public class ZoneChangeSocket {

    private static final Logger LOGGER = Logger.getLogger(CarMetricsSocket.class.getName());
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Opening ZoneChangeSocket session "+session.getId());
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("Closing ZoneChangeSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("ERROR! Closing ZoneChangeSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Received ZoneChangeSocket message: "+message);
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
