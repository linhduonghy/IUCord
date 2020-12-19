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
 * @created: Dec 7, 2020 11:55:48 PM
 */
/**
 * problem:
 */
public class AudioRoom {

    private int room_id;
    // list client socket in room
    private Set<Pair<Integer, Socket>> clients = new HashSet<>();

    public AudioRoom(int room_id) {
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

    public void sendMessage(int fromUser, String message) {
        for (Pair<Integer, Socket> client : clients) {
            try {
                if (client.getFirst() != fromUser)
                    send(client.getSecond(), message);
            } catch (IOException ex) {
                Logger.getLogger(AudioRoom.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendMessage(int fromId, int toId, String message) throws IOException {
        System.out.println(fromId + "->" + toId + ": " +message);
        Pair<Integer, Socket> toClient = getClientByUserId(toId);
        send(toClient.getSecond(), message);
        
    }

    private void send(Socket client, String message) throws IOException {
        OutputStream out = client.getOutputStream();
        out.write(WSMessageEncrypt.encode(message));
        out.flush();
    }

    public Pair<Integer, Socket> getClientBySocket(Socket c) {
        for (Pair<Integer, Socket> client : clients) {
            if (client.getSecond().equals(c)) {
                return client;
            }
        }
        return null;
    }

    public Set<Integer> getUsers() {
        Set<Integer> s = new HashSet<>();
        for (Pair<Integer, Socket> p : clients) {
            s.add(p.getFirst());
        }
        return s;
    }

    private Pair<Integer, Socket> getClientByUserId(int toId) {
        for (Pair<Integer, Socket> client : clients) {
            if (client.getFirst().equals(toId)) {
                return client;
            }
        }
        return null;
    }
}
