
package bean;

/**
 * @author: linhdv
 * @created: Nov 8, 2020 2:42:02 PM 
 */


public class Room {
    
    private int id;
    private String name;
    private User roomManager;

    public Room() {
    }

    public Room(int id, String name, User roomManager) {
        this.id = id;
        this.name = name;
        this.roomManager = roomManager;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getRoomManager() {
        return roomManager;
    }

    public void setRoomManager(User roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", name=" + name + ", roomManager=" + roomManager + '}';
    }
}
