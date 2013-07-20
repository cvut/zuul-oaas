package cz.cvut.zuul.oaas.api.resources.aspect;

import cz.cvut.zuul.oaas.api.resources.ClientsController;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging aspect for logging update and delete operation in ClientsController defined in aspect.xml.
 * 
 * @author Tomas Mano <tomasmano@gmail.com>
 */
public class LoggingAspect {
    
    public static final Logger LOG = LoggerFactory.getLogger(ClientsController.class);
    
    public void logUpdate(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        LOG.info("Property was updated for client with id [{}] to value [{}] by calling {}", new Object[]{args[0], args[1], joinPoint.getSignature().getName()});
    }
    
    public void logDelete(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        LOG.info("Value [{}] was deleted for client with id [{}] by calling {}", new Object[]{args[1], args[0], joinPoint.getSignature().getName()});
    }
    
}
