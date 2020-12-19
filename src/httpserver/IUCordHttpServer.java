package httpserver;

import httpserver.handle.UserHandle;
import httpserver.handle.LoginHanle;
import com.sun.net.httpserver.HttpServer;
import httpserver.handle.IndexHandle;
import httpserver.handle.HomeHandle;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author: linhdv
 * @created: Dec 4, 2020 8:03:05 PM
 */
/**
 * problem:
 */
public class IUCordHttpServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);     
        // show login
        server.createContext("/", new IndexHandle());
        
        // 
        server.createContext("/home", new HomeHandle());

        // post login
        server.createContext("/login", new LoginHanle());
                
        // get user 
        server.createContext("/user", new UserHandle());
         
        server.setExecutor(null); // creates a default executor
        server.start(); // start server        
        System.out.println("Server start at localhost:" + server.getAddress().getPort());
    }
}


