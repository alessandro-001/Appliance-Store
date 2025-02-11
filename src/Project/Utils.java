package Project;

import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.sun.net.httpserver.HttpExchange;

/**
 * ---- UTILS ----
 * @author Alessandro Frondini
 * Simple utility class containing helper methods to parse data
 */

public class Utils {
	/**
     * Converts a URL queries strings into a HashMaps.
     * This method split the query string by the "&" sign to separate the key-value pairs,
     * then it decodes both of the keys and values with the UTF-8 encoder.
     * If the request string is null or empty, HashMap returns an empty map.
     * 
     * @param request the URL-encoded query string to be converted
     * @return HashMap containing decoded key-value pairs from the query strings
     */

  public static HashMap<String, String> requestStringToMap(String request) {
    HashMap<String, String> map = new HashMap<String, String>();
    
    if (request == null || request.isEmpty()) {
    	return new HashMap<>();
    }
    
    String[] pairs = request.split("&");
    
    
    for (int i = 0; i < pairs.length; i++) {
      String pair = pairs[i];

      try {
        String key = pair.split("=")[0];
        key = URLDecoder.decode(key, "UTF-8");

        String value = pair.split("=")[1];
        value = URLDecoder.decode(value, "UTF-8");

        map.put(key, value);
      } catch (UnsupportedEncodingException e) {
        System.err.println(e.getMessage());
      }

    }
    return map;
  }
  
  /**
   * Sets a cookie in the HTTP response, later will be used to store the user role.
   *
   * @param he the HttpExchange object
   * @param name the name of the cookie
   * @param value the value of the cookie
   */
  public static void setTheCookies(HttpExchange he, String name, String value) {
      String cookie = name + "=" + value + "; Path=/; HttpOnly";
      he.getResponseHeaders().add("Set-Cookie", cookie);
  }
}
