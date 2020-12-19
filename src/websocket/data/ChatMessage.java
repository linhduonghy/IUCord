
package websocket.data;

/**
 * @author: linhdv
 * @created: Dec 5, 2020 5:14:29 PM 
 */

/**
 * problem: room x have a message of user y
*/

public class ChatMessage{
    
    private int room_id;
    private int user_id;
    private String text;

    public ChatMessage() {
    }

    public ChatMessage(int room_id, int user_id, String message) {
        this.room_id = room_id;
        this.user_id = user_id;
        this.text = message;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" + "room_id=" + room_id + ", user_id=" + user_id + ", text=" + text + '}';
    }
}
