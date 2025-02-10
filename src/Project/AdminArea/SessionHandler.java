package Project.AdminArea;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ----- SESSION HANDLER -----
 * @author Alessandro Frondini 14501682
 * SessionHandler handles the user sessions through the application. It provides methods to 
 * create, get, update and destroy
 */
public class SessionHandler {
	/**
	 * @param he (the HttpExchange object represent the requests and responses).
	 * @throws IOException (if an I/O error occurs during the process).
	 */
    private static final Map<String, Map<String, Object>> sessions = new HashMap<>();

    
    /**
     * Create a session
     * @return a unique session ID
     */
    public static String createSession() {
        String sessionId = java.util.UUID.randomUUID().toString();
        sessions.put(sessionId, new HashMap<>());
        return sessionId;
    }

    /**
     * Get the session id
     * @param sessionId (is the ID for the session).
     * @return the session data as a map, or return null if the session don't exists.
     */
    public static Map<String, Object> getSession(String sessionId) {
        return sessions.getOrDefault(sessionId, null);
    }

    /**
     * Add id to session 
     * @param sessionId (is the ID to update to the session).
     * @param key (key to add to the session).
     * @param value (is the value associated to the key).
     */
    public static void addToSession(String sessionId, String key, Object value) {
        sessions.computeIfPresent(sessionId, (sId, session) -> {
            session.put(key, value);
            return session;
        });
    }

    /**
     * Disable the session
     * @param sessionId (is the ID of the session to be destroyed)
     */
    public static void destroySession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    
    /**
     * Add to session's BASKET
     * @param he (HttpExchange object representing the HTTP request and response).
     * @param product (product to add in the basket).
     */
    public static void addToBasket(HttpExchange he, Object product) {
        String sessionId = extractSessionIdFromCookie(he);
        if (sessionId == null) return;

        Map<String, Object> session = getSession(sessionId);

        //Get or initialise a basket
        List<Object> basket = (List<Object>) session.computeIfAbsent("basket", b -> new ArrayList<>());

        //ADD the product to the basket
        basket.add(product);
    }
    
    /**
     * Get the session's BASKET
     * @param he (HttpExchange object representing the HTTP request and response).
     * @return (return the list of products in the basket if exists, or an list if basket is not created).
     */
    public static List<Object> getBasket(HttpExchange he) {
        String sessionId = extractSessionIdFromCookie(he);
        if (sessionId == null) return new ArrayList<>();

        Map<String, Object> session = getSession(sessionId);

        //return basket or empty
        return (List<Object>) session.getOrDefault("basket", new ArrayList<>());
    }
    
    
    /**
     * Extract session ID from the cookies in the header
     * @param he (HttpExchange object representing the HTTP request and response).
     * @return (returns the session ID if found, or return null if not exist).
     */
    private static String extractSessionIdFromCookie(HttpExchange he) {
        String cookieHeader = he.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null && cookieHeader.contains("sessionId=")) {
            for (String cookie : cookieHeader.split(";")) {
                String[] keyValue = cookie.trim().split("=");
                if (keyValue.length == 2 && "sessionId".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

}

