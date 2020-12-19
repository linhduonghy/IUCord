package httpserver.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: linhdv
 * @created: Dec 5, 2020 1:35:34 AM
 */
/**
 * problem: 
 */
public class RequestQuery {

    /**
     * convert request query to map
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
