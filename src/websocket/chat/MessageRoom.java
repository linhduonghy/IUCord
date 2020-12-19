package websocket.chat;

import websocket.data.Pair;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import websocket.utils.WSMessageEncrypt;

/**
 * @author: linhdv
 * @created: Nov 8, 2020 10:22:16 AM
 */
/**
 * problem: 
 */
public class MessageRoom {
    
    private int room_id;
    // list client socket in room
    private Set<Pair<Integer, Socket>> clients = new HashSet<>();

    public MessageRoom(int room_id) {
        this.room_id = room_id;
    }

    public Set<Pair<Integer, Socket>> getClients() {
        return clients;
    }
    public void join(Pair<Integer, Socket> client) {
        clients.add(client);
    }

    public void leave(Pair<Integer, Socket> client) {        
        clients.remove(client);
    }

    public void sendMessage(String message) {
        for (Pair<Integer, Socket> client : clients) {
            try {
                send(client.getSecond(), message);
            } catch (IOException ex) {
                Logger.getLogger(MessageRoom.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }

    private void send(Socket client, String message) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(WSMessageEncrypt.encode(message));
        out.flush();
    }
    
    public Pair<Integer, Socket> getClientBySocket(Socket c) {
        for (Pair<Integer, Socket> client : clients) { 
            if (client.getSecond().equals(c))
                return client;
        }
        return null;
    }
}
