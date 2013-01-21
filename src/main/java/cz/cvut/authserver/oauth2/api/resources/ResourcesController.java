package cz.cvut.authserver.oauth2.api.resources;

import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import cz.cvut.authserver.oauth2.services.ResourceService;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import static org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * API for autorization server resource's management.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Controller
@RequestMapping(value = "/v1/resources")
public class ResourcesController {

    private static final Logger LOG = LoggerFactory.getLogger(ResourcesController.class);
    private String apiVersion;
    
    private MessageSource messageSource;
    
    private ResourceService resourceService;

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }

    @ResponseBody
    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public List<Resource> getAllPublicResources() {
        return resourceService.getAllPublicResources();
    }
    
    @ResponseStatus(CREATED)
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Resource createResource(@Valid @RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        LOG.info("Creating new resource [{}]", resource);
        return created;
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteResource(@PathVariable String id) throws Exception{
        resourceService.deleteResourceById(id);
    }
    
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateResource(@PathVariable String id, @Valid @RequestBody Resource resource) throws Exception{
        resourceService.updateResource(id, resource);
    }
  
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Resource findResourceById(@PathVariable String id) {
        return resourceService.findResourceById(id);
    }
    
    //////////  Exceptions Handling  //////////
    
    @ExceptionHandler(NoSuchResourceException.class)
    public ResponseEntity<Void> handleNoSuchClient(NoSuchResourceException ex) {
        return new ResponseEntity<>(NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    JsonExceptionMapping handleMethodArgumentNotValidException(MethodArgumentNotValidException error) throws Exception {
        BindingResult bindingResult = error.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        String errorMessage = constructErrorMessage(errors);

        return new JsonExceptionMapping(bindingResult, HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    private String constructErrorMessage(List<ObjectError> errors) throws Exception {
        String errorMessage = "";
        for (ObjectError objectError : errors) {
            try {
                errorMessage = errorMessage.concat(messageSource.getMessage(objectError.getCode(), objectError.getArguments(), null)).concat(" ");
            } catch (Exception e) {
                LOG.error("Error during parsing properties file with validation errors: {}", e.getMessage());
                throw new Exception(e);
            }
        }
        return errorMessage;
    }

    //////////  Getters / Setters  //////////

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
}
