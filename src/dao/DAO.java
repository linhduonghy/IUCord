package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: linhdv
 * @created: Nov 8, 2020 2:46:22 PM
 */
public abstract class DAO<T> {

    public static Connection conn;

    public DAO() {
        if (conn == null) {
            String user = "root";
            String pass = "linhdv";
            String dbName = "ltm_iucord";
            String dbClass = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/" + dbName
                    + "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
            try {
                Class.forName(dbClass);
                conn = DriverManager.getConnection(url, user, pass);
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public abstract List<T> getAll() throws SQLException;

    public abstract T get(int id) throws SQLException;

    public abstract void insert(T t) throws SQLException;

    public abstract void update(T t) throws SQLException;

    public abstract void delete(T t) throws SQLException;

    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
