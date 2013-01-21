package cz.cvut.authserver.oauth2.utils;

import java.io.Serializable;
import java.net.URI;

/**
 * Utilities for entities identifiers.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class IdUtils {

    private IdUtils() {
        // Prevent instantiation.
    }
    
    /**
     * Tells if the given ids are equal.
     * 
     * @param id1
     * @param id2
     * @return true if ids are equal
     */
    public static boolean areEqual(Long id1, Long id2){
        return id1.compareTo(id2) == 0;
    }

    /**
     * Tells if the given ids are equal.
     * 
     * @param id1
     * @param id2
     * @return true if ids are equal
     */
    public static boolean areEqual(Serializable id1, Serializable id2) {
        Long idLong1 = (Long) id1;
        Long idLong2 = (Long) id2;
        return idLong1.compareTo(idLong2) == 0;
    }
    
    /**
     * Extract identifier from http location response header in URI.
     * 
     * @param uri of location header from http response header
     * @return client identifier
     */
    public static String extractIdentifier(URI uri){
        String location = uri.getPath();
        String clientId = location.substring(location.lastIndexOf("/") + 1);
        return clientId;
    }
}
