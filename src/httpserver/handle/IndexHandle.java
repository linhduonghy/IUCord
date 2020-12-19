
package httpserver.handle;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: linhdv
 * @created: Dec 4, 2020 8:50:48 PM 
 */

/**
 * problem: map the path browser to correspond file html
*/

public class IndexHandle implements HttpHandler {

    private String filename = "index.html";
    
    @Override
    public void handle(HttpExchange he) throws IOException {
                  
        System.out.println(he.getRequestMethod() + he.getRequestURI());   
        
//        filename = he.getHttpContext().getPath();
        
        // read file index.html
        File index = new File("src/resources/" + filename);          
        byte[] bytes = new byte[(int)index.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(index));
        bis.read(bytes, 0, bytes.length);
        bis.close();
        
        // set response headers
        he.getResponseHeaders().set("Content-Type", "text/html;charset=utf-8");
        he.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        he.sendResponseHeaders(200, index.length());
        // write data to response stream
        OutputStream os = he.getResponseBody();
        os.write(bytes);
        os.close();
        
    }

}