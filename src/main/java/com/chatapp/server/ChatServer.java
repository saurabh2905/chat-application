package com.chatapp.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ChatServer extends WebSocketServer {
    private Set<WebSocket> connections = new HashSet<>();
    private Map<String, WebSocket> userConnections = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final int DEFAULT_PORT = 8887;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        startConnectionPrinter();
    }

    private void startConnectionPrinter() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n=== Active Connections ===");
            System.out.println("Total connections: " + connections.size());
            System.out.println("Connected users:");
            userConnections.forEach((username, conn) -> 
                System.out.println("- " + username + " (" + conn.getRemoteSocketAddress() + ")")
            );
            System.out.println("=======================\n");
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        System.out.println("New connection from: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        // Remove user from userConnections
        userConnections.entrySet().removeIf(entry -> entry.getValue().equals(conn));
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
        broadcast("A user has disconnected!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received message: " + message);
        
        // Check if this is a registration message (format: "register:username")
        if (message.startsWith("register:")) {
            String username = message.substring("register:".length());
            userConnections.put(username, conn);
            broadcast(username + " has joined the chat!");
            return;
        }
        
        // Check if this is a private message (format: "private:username:message")
        if (message.startsWith("private:")) {
            String[] parts = message.substring("private:".length()).split(":", 2);
            if (parts.length == 2) {
                String targetUsername = parts[0];
                String privateMessage = parts[1];
                WebSocket targetConn = userConnections.get(targetUsername);
                if (targetConn != null) {
                    // Find sender's username
                    String sender = userConnections.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(conn))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("Unknown");
                    
                    targetConn.send("Private message from " + sender + ": " + privateMessage);
                    conn.send("Message sent to " + targetUsername);
                } else {
                    conn.send("User " + targetUsername + " not found!");
                }
                return;
            }
        }
        
        // Regular broadcast message
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error occurred: " + ex.getMessage());
        if (conn != null) {
            connections.remove(conn);
            userConnections.entrySet().removeIf(entry -> entry.getValue().equals(conn));
        }
    }

    @Override
    public void onStart() {
        System.out.println("Chat server started on port " + getPort());
    }

    public void broadcast(String message) {
        for (WebSocket conn : connections) {
            conn.send(message);
        }
    }

    public void stopServer() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            stop();
        } catch (Exception e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }

        final AtomicReference<ChatServer> serverRef = new AtomicReference<>();
        int retries = 0;

        while (retries < MAX_RETRIES) {
            try {
                ChatServer server = new ChatServer(port);
                serverRef.set(server);
                server.start();
                System.out.println("Server started successfully on port " + port);
                break;
            } catch (Exception e) {
                retries++;
                if (retries < MAX_RETRIES) {
                    System.err.println("Failed to start server on port " + port + ". Retrying in " + (RETRY_DELAY_MS/1000) + " seconds...");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println("Failed to start server after " + MAX_RETRIES + " attempts. Exiting.");
                    System.exit(1);
                }
            }
        }

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ChatServer server = serverRef.get();
            if (server != null) {
                System.out.println("Shutting down server...");
                server.stopServer();
            }
        }));

        // Keep the main thread alive
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 