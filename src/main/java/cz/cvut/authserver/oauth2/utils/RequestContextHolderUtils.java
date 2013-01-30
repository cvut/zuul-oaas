package cz.cvut.authserver.oauth2.utils;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class RequestContextHolderUtils {

    public static HttpServletRequest getHttpRequest() {
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return currentRequest;
    }

    public static String getRequestURI() {
        HttpServletRequest currentRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return currentRequest.getRequestURI();
    }
}
