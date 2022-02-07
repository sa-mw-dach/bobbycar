package com.redhat.bobbycar;

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
import org.jboss.logging.Logger;

@ServerEndpoint("/api/carevents")
@ApplicationScoped
public class CarEventSocket {

    private static final Logger LOGGER = Logger.getLogger(CarEventSocket.class.getName());
    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Opening CarEventSocket session "+session.getId());
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("Closing CarEventSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("ERROR! Closing CarEventSocket session "+session.getId());
        sessions.remove(session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOGGER.info("Received CarEventSocket message: "+message);
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