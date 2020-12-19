package websocket;

import websocket.data.Pair;
import websocket.chat.MessageRoom;
import websocket.utils.WSMessageEncrypt;
import websocket.data.ChatMessage;
import com.google.gson.Gson;
import dao.RoomDAO;
import dao.UserDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import bean.Room;
import bean.User;

public class ChatRoomServer {

    private ServerSocket server;
    private final int port = 8086;
        // store sockets in a room
    private static Map<Integer, MessageRoom> rooms = new HashMap<>();
    private static Map<Integer, Set<User>> onlineStatus = new HashMap<>();
    private static Map<Integer, Set<User>> offlineStatus = new HashMap<>();
    

    public ChatRoomServer() throws SQLException {

        // initialize all user in rooms offline
        List<Room> rs = new RoomDAO().getAll();
        for (Room room : rs) {
            Set<User> usersInRoom = new HashSet<>(new UserDAO().getUsersByRoom(room.getId()));
            offlineStatus.put(room.getId(), usersInRoom);
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, SQLException {

        ChatRoomServer chatServer = new ChatRoomServer();
        chatServer.runServer();

    }

    public void runServer() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server Websocket has started on localhost:8086\r\nWaiting for a connection...");
        while (true) {
            Socket client = server.accept();
            // each client request is handled by a thread
            new Thread(new ClientHandle(client)).start();
        }
    }

    private static class ClientHandle implements Runnable {

        private final Socket client;

        public ClientHandle(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                // hanshake websocket connection
                doHandShakeToInitializeWebSocketConnection(in, out);

                // client is connecting
                while (client.isConnected()) {
                    // receive message     
                    byte[] r_data = new byte[1024];
                    int len = in.read(r_data);
                    if (len == -1) { // client socket closed
                        break;
                    }
                    // decode data
                    String r_message = WSMessageEncrypt.decode(r_data, len);

                    if (r_message.startsWith("{")) {
                        System.out.println(r_message);
                        // parse json to Object
                        
                        ChatMessage messageInp = new Gson().fromJson(r_message, ChatMessage.class);
                        // user login
                        if (messageInp.getRoom_id() < 0) {
                            addUserToRooms(messageInp.getUser_id());
                        } else {
                            // broadcast message to clients in room                            
                            broadcastMessageInRoom(rooms.get(messageInp.getRoom_id()), messageInp);
                        }

                    }
                }
                // client closed socket
                removeUserFromRooms();

            } catch (IOException | NoSuchAlgorithmException | SQLException ex) {
                Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // handle request upgrade the connection from http request
        // server handshake to upgrade connection to the websocket
        private void doHandShakeToInitializeWebSocketConnection(InputStream in, OutputStream out) throws IOException, NoSuchAlgorithmException {
            //translate bytes of request to string
            String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);

            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + DatatypeConverter
                                .printBase64Binary(
                                        MessageDigest
                                                .getInstance("SHA-1")
                                                .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                                        .getBytes("UTF-8")))
                        + "\r\n\r\n")
                        .getBytes("UTF-8");
                out.write(response, 0, response.length);
            }
        }

        // broadcast message to all user in room when user send message
        private void broadcastMessageInRoom(MessageRoom roomMessage, ChatMessage chatMessage) throws SQLException {            
            // send message to client in room
            Map<String, Map<String, Object>> message = new HashMap<>();
            Map<String, Object> chatM = new HashMap<>();
            chatM.put("user", new UserDAO().get(chatMessage.getUser_id()));
            chatM.put("room_id", chatMessage.getRoom_id());
            chatM.put("text", chatMessage.getText());
            message.put("message", chatM);
            roomMessage.sendMessage(new Gson().toJson(message));            
        }

        private void broadCastOnlineStatus(Room room) {
            MessageRoom rm = rooms.get(room.getId());
            // users online in room
            Map<String, Map<String, Set<User>>> onlSts = new HashMap<>();
            Map<String, Set<User>> onInRoom = new HashMap<>();
            onInRoom.put("" + room.getId(), onlineStatus.get(room.getId()));
            onlSts.put("onlineStatus", onInRoom);

            // users offline in room
            Map<String, Map<String, Set<User>>> offSts = new HashMap<>();
            Map<String, Set<User>> offInRoom = new HashMap<>();
            offInRoom.put("" + room.getId(), offlineStatus.get(room.getId()));
            offSts.put("offlineStatus", offInRoom);

            rm.sendMessage(new Gson().toJson(onlSts));
            rm.sendMessage(new Gson().toJson(offSts));
        }
        
        // when user login, add user to rooms and update online status
        private void addUserToRooms(int user_id) throws SQLException {
            // get rooms of user
            List<Room> roomsOfUser = new RoomDAO().getRoomUser(user_id);
            // add user to rooms and update online status
            System.out.println("client: " + client.getInetAddress().getHostAddress());
            for (Room room : roomsOfUser) {
                MessageRoom rm = rooms.get(room.getId());
                Set<User> usersInRoom = onlineStatus.get(room.getId());
                Set<User> usersOutRoom = offlineStatus.get(room.getId());

                if (rm == null) {
                    rm = new MessageRoom(room.getId());
                }
                if (usersInRoom == null) {
                    usersInRoom = new HashSet<>();
                }
                rm.join(new Pair<>(user_id, client));
                usersInRoom.add(new UserDAO().get(user_id));
                usersOutRoom.remove(new UserDAO().get(user_id));

                rooms.put(room.getId(), rm);
                onlineStatus.put(room.getId(), usersInRoom);
                offlineStatus.put(room.getId(), usersOutRoom);
            }
            // broadcast online status to all user in rooms
            for (Room room : roomsOfUser) {

                broadCastOnlineStatus(room);

            }
        }

        // remove client from rooms
        private void removeUserFromRooms() throws SQLException {
            System.out.println("client closed: " + client.getInetAddress().getHostAddress());

            int user_id = -1;
            // get User info when client socket closed
            for (Map.Entry<Integer, MessageRoom> e : rooms.entrySet()) {
                MessageRoom rm = e.getValue();
                Pair<Integer, Socket> c = rm.getClientBySocket(client);
                if (c != null) {
                    user_id = c.getFirst();
                }
            }
            // get rooms of user
            List<Room> roomsOfUser = new RoomDAO().getRoomUser(user_id);
            // remove user from rooms, update online status
            for (Room room : roomsOfUser) {
                MessageRoom rm = rooms.get(room.getId());
                Set<User> usersInRoom = onlineStatus.get(room.getId());
                Set<User> usersOutRoom = offlineStatus.get(room.getId());

                rm.leave(new Pair<>(user_id, client));
                usersInRoom.remove(new UserDAO().get(user_id));
                usersOutRoom.add(new UserDAO().get(user_id));

                rooms.put(room.getId(), rm);
                onlineStatus.put(room.getId(), usersInRoom);
                offlineStatus.put(room.getId(), usersOutRoom);
            }
            // broadcast online status to all user in rooms
            for (Room room : roomsOfUser) {
                broadCastOnlineStatus(room);
            }
        }                
    }
}
