
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import bean.Room;
import java.sql.Statement;

/**
 * @author: linhdv
 * @created: Nov 8, 2020 2:49:09 PM 
 */

/**
 * problem:
*/

public class RoomDAO extends DAO<Room>{

    public RoomDAO() {
        super();
    }
    
    @Override
    public List<Room> getAll() throws SQLException {
        String sql = "select * from room";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        List<Room> rooms = new ArrayList<>();
        while (rs.next()) {
            rooms.add(mapRowToRoom(rs));
        }
        return rooms;
    }

    @Override
    public Room get(int id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Room> getRoomUser(int user_id) throws SQLException{
        
        String sql = "select room.id, room.name, room.room_manager from room "
                + "inner join room_user "
                + "on room.id=room_user.room_id "
                + "where room_user.user_id = ?";
        
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setInt(1, user_id);
        ResultSet rs = pstm.executeQuery();
        List<Room> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRowToRoom(rs));            
        }
        return result;
    }
    @Override
    public void insert(Room t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Room t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Room t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Room mapRowToRoom(ResultSet rs) throws SQLException {
        return new Room(rs.getInt("id"), rs.getString("name"), null);        
    }
    
    
}
