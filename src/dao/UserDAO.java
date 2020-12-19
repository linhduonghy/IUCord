package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import bean.User;

/**
 * @author: linhdv
 * @created: Nov 8, 2020 2:48:56 PM
 */
/**
 * problem:
 */
public class UserDAO extends DAO<User> {

    public UserDAO() {
        super();
    }

    @Override
    public List<User> getAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User get(int id) throws SQLException {
        String sql = "select * from user where id=?";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setInt(1, id);
        ResultSet rs = pstm.executeQuery();
        if (rs.next()) {
            return mapRowToUser(rs);
        }
        return null;
    }

    public User get(String username, String password) throws SQLException {

        String sql = "select * from user where username=? and password=?";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, username);
        pstm.setString(2, password);
        ResultSet rs = pstm.executeQuery();
        if (rs.next()) {
            return mapRowToUser(rs);
        }
        return null;

    }

    public List<User> getUsersByRoom(int room_id) throws SQLException {

        String sql = "SELECT *"
                + "FROM user "
                + "INNER JOIN room_user "
                + "ON user.id = room_user.user_id "
                + "where room_user.room_id = ?";
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setInt(1, room_id);
        ResultSet rs = pstm.executeQuery();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(mapRowToUser(rs));
        }
        return users;
    }

    @Override
    public void insert(User t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(User t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(User t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {

        return new User(rs.getInt("id"), rs.getString("name"), rs.getString("username"), rs.getString("password"));
    }
}
