# Java WebSocket Chat Application

A real-time chat application built with Java WebSocket that supports multiple clients, private messaging, and connection monitoring.

## Learning Objectives

This project was built to understand and demonstrate the following concepts:

- WebSocket Protocol Implementation
  - How WebSocket maintains persistent connections
  - Real-time bidirectional communication
  - Connection lifecycle management
  - Message handling and routing

- Real-time Communication Patterns
  - Server-client architecture
  - Message broadcasting
  - Private messaging
  - Connection state management

- Java WebSocket Features
  - WebSocket server implementation
  - Client connection handling
  - Message encoding/decoding
  - Connection event handling


## Features

- Real-time messaging between multiple clients
- Private messaging between users
- Connection status monitoring
- Automatic connection tracking
- Graceful server shutdown
- Port configuration support

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Project Structure

```
src/main/java/com/chatapp/
├── server/
│   └── ChatServer.java
└── client/
    └── ChatClient.java
```

## Building the Project

1. Clone the repository
2. Navigate to the project directory
3. Build the project using Maven:

```bash
mvn clean compile
```

## Running the Application

### Starting the Server

You can start the server in two ways:

1. Using default port (8887):
```bash
mvn exec:java -Dexec.mainClass="com.chatapp.server.ChatServer"
```

2. Using a custom port:
```bash
mvn exec:java -Dexec.mainClass="com.chatapp.server.ChatServer" -Dexec.args="8888"
```

The server will:
- Start on the specified port
- Print active connections every 30 seconds
- Handle client connections and disconnections
- Manage message broadcasting

### Running Multiple Clients

1. Open multiple terminal windows
2. In each terminal, run:
```bash
mvn exec:java -Dexec.mainClass="com.chatapp.client.ChatClient"
```

3. For each client:
   - Enter a username when prompted
   - The client will automatically connect to the server
   - You can now send and receive messages

## Using the Chat

### Commands

- Regular messages: Just type your message and press Enter
- Private messages: Use `@username message` format
  Example: `@John Hello, how are you?`
- Quit: Type `quit` to exit the chat

### Message Types

1. Broadcast Messages:
   - Sent to all connected users
   - Format: `username: message`

2. Private Messages:
   - Sent to a specific user
   - Format: `@username message`
   - Only the sender and recipient can see these messages

## Server Features

- Connection Monitoring:
  - Prints active connections every 30 seconds
  - Shows total number of connections
  - Lists all connected users with their IP addresses

- Error Handling:
  - Automatic retry on port conflicts
  - Graceful shutdown
  - Connection error recovery

## Troubleshooting

1. "Address already in use" error:
   - The server will automatically retry up to 3 times
   - Wait for the retry attempts to complete
   - If the error persists, try using a different port

2. Connection issues:
   - Ensure the server is running
   - Check if you're using the correct port
   - Verify network connectivity

3. Client connection problems:
   - Make sure the server is running
   - Check if the username is unique
   - Verify the server port matches the client configuration

## Shutting Down

1. Server:
   - Press Ctrl+C to gracefully shut down the server
   - The server will clean up resources and close connections

2. Clients:
   - Type `quit` to exit the chat client
   - The client will properly close the connection

## Notes

- The server supports multiple simultaneous connections
- Messages are delivered in real-time
- Private messages are only visible to the sender and recipient
- The server automatically tracks and displays active connections
- Usernames must be unique within the chat session 

## Current Limitations

1. Message Persistence
   - No message history storage
   - Messages are lost when server restarts
   - No message delivery confirmation

2. Security
   - No authentication system
   - No message encryption
   - No SSL/TLS support
   - No rate limiting

3. Scalability
   - Single server instance
   - No load balancing
   - No clustering support
   - Limited concurrent connections

4. User Experience
   - Basic command-line interface
   - No message formatting
   - No file sharing
   - No user status indicators

5. Features
   - No group chat support
   - No message editing/deletion
   - No read receipts
   - No typing indicators

## Future Enhancements

1. Message Management
   - Implement message persistence using a database
   - Add message delivery status
   - Support message editing and deletion
   - Add message search functionality

2. Security Improvements
   - Add user authentication and authorization
   - Implement SSL/TLS encryption
   - Add rate limiting and DDoS protection
   - Implement message encryption

3. Scalability Features
   - Add support for multiple server instances
   - Implement load balancing
   - Add clustering support
   - Optimize connection handling

4. User Interface
   - Develop a web-based UI
   - Add real-time typing indicators
   - Implement read receipts
   - Add user status indicators

5. Advanced Features
   - Group chat functionality
   - File sharing capabilities
   - Message formatting support
   - User presence system
   - Message reactions
   - Chat rooms/channels

6. Monitoring and Administration
   - Add detailed connection logging
   - Implement user management
   - Add performance monitoring
   - Create admin dashboard

7. Integration Capabilities
   - Add REST API endpoints
   - Support for external services
   - Webhook notifications
   - Third-party integrations