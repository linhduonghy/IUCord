
package httpserver.handle;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: linhdv
 * @created: Dec 4, 2020 8:50:48 PM 
 */

/**
 * problem: map the path browser to correspond file html
*/

public class HomeHandle implements HttpHandler {

    private String filename = "home.html";
    
    @Override
    public void handle(HttpExchange he) throws IOException {
          
        System.out.println(he.getRequestMethod() + he.getRequestURI());                
        
        // read file index.html
        File file = new File("src/resources/" + filename);
        
        byte[] bytes = new byte[(int)file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(bytes, 0, bytes.length);
        bis.close();
        
        //        
        Headers resHeaders = he.getResponseHeaders();
        resHeaders.set("Access-Control-Allow-Origin", "*");
        
        he.sendResponseHeaders(200, file.length());
        OutputStream os = he.getResponseBody();
        os.write(bytes);
        os.close();
        he.close();
    }

}