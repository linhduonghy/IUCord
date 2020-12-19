
package httpserver.handle;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.RoomDAO;
import dao.UserDAO;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import bean.Room;
import bean.User;
import com.sun.net.httpserver.Headers;
import httpserver.utils.RequestQuery;

/**
 * @author: linhdv
 * @created: Dec 6, 2020 1:34:28 AM 
 */

/**
 * problem:
*/

public class RoomUserHandle implements HttpHandler{

    @Override
    public void handle(HttpExchange he) throws IOException {
        //
        System.out.println(he.getRequestMethod() + he.getRequestURI());   
                        
        // get user_id from request uri
        int user_id = Integer.parseInt(RequestQuery.queryToMap(he.getRequestURI().getQuery()).get("id"));   
        // get user, rooms of user
        UserDAO userDAO = new UserDAO();
        RoomDAO roomDAO = new RoomDAO();
        User user = null;
        List<Room> rooms = null;
        try {
            user = userDAO.get(user_id);
            rooms = roomDAO.getRoomUser(user_id);
        } catch (SQLException ex) {
            Logger.getLogger(UserHandle.class.getName()).log(Level.SEVERE, null, ex);
        }        
        Object[] wrap = new Object[] {user, rooms};
        // parse user -> json
        String json = new Gson().toJson(wrap);
                
        // set response headers
        Headers resHeaders = he.getResponseHeaders();
        resHeaders.set("Content-Type", "application/json");        
        resHeaders.set("Access-Control-Allow-Origin", "*");
        
        he.sendResponseHeaders(200, json.getBytes().length);        
        // write data to to response stream
        OutputStream os = he.getResponseBody();
        os.write(json.getBytes());
        os.flush();
        os.close();
        he.close();
    }
    
}
