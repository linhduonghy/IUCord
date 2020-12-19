package websocket.data;

import java.util.Map;

/**
 * @author: linhdv
 * @created: Dec 7, 2020 11:09:11 PM
 */
/**
 * problem:
 */
public class AudioMessage {

    private int room_id;
    private int fromUserId;
    private String fromUserName;
    private int toUserId;
    private Map<String, String> signal;

    public AudioMessage() {
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public Map<String, String> getSignal() {
        return signal;
    }

    public void setSignal(Map<String, String> signal) {
        this.signal = signal;
    }

}
