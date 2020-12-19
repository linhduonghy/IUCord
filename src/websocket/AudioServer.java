package websocket;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import java.util.stream.Stream;
import javax.xml.bind.DatatypeConverter;
import websocket.data.AudioMessage;
import websocket.chat.AudioRoom;
import websocket.chat.MessageRoom;
import websocket.data.Pair;

import websocket.utils.WSMessageEncrypt;

/**
 * @author: linhdv
 * @created: Dec 6, 2020 5:58:14 PM
 */
/**
 * problem:
 */
public class AudioServer {

    private ServerSocket server;
    private final int port = 8088;

    private static Map<Integer, AudioRoom> audioRooms = new HashMap<>();

    public static void main(String[] args) {
        new AudioServer().runServer();
    }

    private void runServer() {
        try {
            server = new ServerSocket(port);
            System.out.println("Audio Websocket Server running at localhost:8088. Waiting for connection...");

            while (true) {
                Socket client = server.accept();

                // each client request is handled by a thread
                new Thread(new ClientHandle(client)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(AudioServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class ClientHandle implements Runnable {

        private Socket client;

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

                    byte[] r_data = new byte[(int) Math.pow(2, 16)];
                    int len = in.read(r_data);
                    if (len == -1) { // client socket closed
                        break;
                    }
                    // decode data
                    String r_message = WSMessageEncrypt.decode(r_data, len);                    
                    if (r_message.startsWith("{")) {
                        
//                        System.out.println("Mes: " + r_message);

                        // parse message to audio message (... Json can't parse sdp)
                        AudioMessage audioMessage = mapMessageToAudioMessage(r_message);
                        
                        if (audioMessage.getSignal() == null) {
                            removeUserFromAudioRoom();
                            continue;
                        }
                        // add user to audio room
                        addUserToRoomAudio(audioMessage);
                        
                        // send message to other user 
                        broadcastAudioMessageInRoom(audioMessage.getRoom_id(), audioMessage);                        
                    }
                }
                // client closed socket
                removeUserFromAudioRoom();

            } catch (IOException ex) {
                Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AudioServer.class.getName()).log(Level.SEVERE, null, ex);
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

//        private void broadcastNewUserMessageInRoom(int room_id, AudioMessage audioMessage) {
//            AudioRoom room = audioRooms.get(room_id);
//            Map<String, Object> m = new HashMap<>();
//            m.put("type", "user-joined");
//            m.put("user_id", audioMessage.getUser_id());
//            m.put("user_name", audioMessage.getUser_name());
//            m.put("signal", audioMessage.getSdp());
//            m.put("users", room.getUsers());
//            room.sendMessage(audioMessage.getUser_id(), new Gson().toJson(m));
//        }
        private void broadcastAudioMessageInRoom(int room_id, AudioMessage audioMessage) throws IOException {
            AudioRoom room = audioRooms.get(room_id);    
            System.out.println(new Gson().toJson(audioMessage));
            room.sendMessage(audioMessage.getFromUserId(), new Gson().toJson(audioMessage));
        }

        private void addUserToRoomAudio(AudioMessage audioMessage) {
            AudioRoom ar = audioRooms.get(audioMessage.getRoom_id());

            if (ar == null) {
                ar = new AudioRoom(audioMessage.getRoom_id());
            }
            ar.join(new Pair<>(audioMessage.getFromUserId(), client));

            audioRooms.put(audioMessage.getRoom_id(), ar);
        }

        private void removeUserFromAudioRoom() {
            for (Map.Entry<Integer, AudioRoom> e : audioRooms.entrySet()) {
                AudioRoom ar = e.getValue();
                Pair<Integer, Socket> c = ar.getClientBySocket(client);
                if (c != null) {
                    ar.leave(c); 
                    Map<String, Integer> m = new HashMap<>();
                    m.put("disconnect", c.getFirst());
                    ar.sendMessage(c.getFirst(), new Gson().toJson(m));
                }
            }
        }

        private AudioMessage mapMessageToAudioMessage(String message) {
            //System.out.println("MES: " + message);            
            if (message.contains("null")) {
                
            } else if (!message.endsWith("}}")) {
                int p = message.indexOf("}}");
                message = message.substring(0, p + 2);
            }         
            
            AudioMessage audioMessage = new Gson().fromJson(message, AudioMessage.class);

            return audioMessage;
        }

        
    }
}
