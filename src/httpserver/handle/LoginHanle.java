
package httpserver.handle;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDAO;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import bean.User;
import httpserver.utils.RequestQuery;

/**
 * @author: linhdv
 * @created: Dec 5, 2020 12:42:14 AM 
 */

/**
 * problem: handle post login
*/

public class LoginHanle implements HttpHandler {
    
    @Override
    public void handle(HttpExchange he) throws IOException {
        
        // just handle post method
        if (he.getRequestMethod().equalsIgnoreCase("post")) {            
            // Request Headers
            Headers reqHeaders = he.getRequestHeaders();
            Set<Map.Entry<String, List<String>>> entries = reqHeaders.entrySet();
            int conentLength = Integer.parseInt(reqHeaders.getFirst("Content-length"));
            // check request headers 
            entries.forEach(k -> {
                Map.Entry<String, List<String>> e = k;
//                System.out.println(e.getKey() + "->" + e.getValue());                
            });
            // Request Body
            InputStream is = he.getRequestBody();
            byte[] data = new byte[conentLength];            
            // get parameter form
            is.read(data);
            
            Map<String, String> query = RequestQuery.queryToMap(new String(data));
            // get username, password 
            String username = query.get("username");
            String password = query.get("password");
            
            // check user
            UserDAO userDAO = new UserDAO();
            User user = null;
            try {
                user = userDAO.get(username, password);               
            } catch (SQLException ex) {
                Logger.getLogger(LoginHanle.class.getName()).log(Level.SEVERE, null, ex);
            }            
            
            String redirectPath;                    
            // login fail
            if (user == null) {
                redirectPath = "login.html";
            } else { // success
                redirectPath = "home?user_id=" + user.getId();
            }                        
            
            // redirect page
            Headers resHeaders = he.getResponseHeaders();
            resHeaders.set("Location", redirectPath);
            resHeaders.set("Access-Control-Allow-Origin", "*");
            
            he.sendResponseHeaders(302, 0);
            
            he.close();
        }
    }
    
}
