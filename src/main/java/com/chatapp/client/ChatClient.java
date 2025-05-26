package com.chatapp.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Scanner;

public class ChatClient extends WebSocketClient {
    private String username;
    private boolean isRegistered = false;

    public ChatClient(String serverUri, String username) throws Exception {
        super(new URI(serverUri));
        this.username = username;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server!");
        // Register the user
        send("register:" + username);
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server!");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    public void sendPrivateMessage(String targetUsername, String message) {
        send("private:" + targetUsername + ":" + message);
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            ChatClient client = new ChatClient("ws://localhost:8887", username);
            client.connect();

            // Wait for connection to establish
            Thread.sleep(1000);

            System.out.println("\nCommands:");
            System.out.println("  @username message - Send private message to user");
            System.out.println("  quit - Exit the chat");
            System.out.println("  Any other message will be broadcast to all users\n");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();
                
                if (message.equalsIgnoreCase("quit")) {
                    break;
                }
                
                if (message.startsWith("@")) {
                    // Handle private message
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex > 1) {
                        String targetUsername = message.substring(1, spaceIndex);
                        String privateMessage = message.substring(spaceIndex + 1);
                        client.sendPrivateMessage(targetUsername, privateMessage);
                    } else {
                        System.out.println("Invalid private message format. Use: @username message");
                    }
                } else {
                    // Broadcast message
                    client.send(username + ": " + message);
                }
            }

            client.close();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 