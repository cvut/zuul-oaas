package cz.cvut.authserver.oauth2.dao;

import cz.cvut.authserver.oauth2.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.authserver.oauth2.generators.IdentificatorGenerator;
import cz.cvut.authserver.oauth2.models.resource.Auth;
import cz.cvut.authserver.oauth2.models.resource.Resource;
import cz.cvut.authserver.oauth2.models.resource.Scope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceInMemoryDAO implements ResourceDAO {

    private static List<Resource> resources;
    
    @Autowired
    IdentificatorGenerator identificatorGenerator;

    static {
        resources = new ArrayList<Resource>();
        populateResources();
    }

    @Override
    public Resource createResource(Resource resource) {
        resource.setId(identificatorGenerator.generateIdentificator());
        resources.add(resource);
        return resource;
    }

    @Override
    public void updateResource(Long id, Resource resource) throws NoSuchResourceException{
        Resource update = findResourceById(id);
        resources.remove(update);
        resources.add(resource);
    }

    @Override
    public List<Resource> getAllResources() {
        return resources;
    }

    @Override
    public Resource findResourceById(Long id) throws NoSuchResourceException{
        for (Resource resource : resources) {
            if (resource.getId().compareTo(id)==0) {
                return resource;
            }
        }
        throw new NoSuchResourceException(String.format("No resource exists with given id [%s]", id));
    }
    
    @Override
    public boolean deleteResourceById(Long id) throws NoSuchResourceException{
        Resource deleted = findResourceById(id);
        return resources.remove(deleted);
    }

    private static void populateResources() {
        Auth auth1 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.readonly", "Read only scope", false));
        resources.add(createResource(auth1, 7549153485L, "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Basic"));
        Auth auth2 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.write", "Write scope", true));
        resources.add(createResource(auth2, 98804085L, "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Master"));
        Auth auth3 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.teacher", "Teacher scope", true));
        resources.add(createResource(auth3, 1936153677L, "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Teacher"));
        Auth auth4 = createAuth(createScope("https://www.cvutapis.cz/auth/erasmus.readonly", "Read only scope", false));
        resources.add(createResource(auth4, 9872934331L, "https://www.cvutapis.cz/erasmusapi/v1", "API for access to the data within Erasmus db.", "erasmusapi", "v1", "Erasmus API"));
        Auth auth5 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.student", "Student scope", true));
        resources.add(createResource(auth5, 9949111737L, "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Student"));
        Auth auth6 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.write", "Write scope", true));
        resources.add(createResource(auth6, 187545534710L, "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Pro"));
        Auth auth7 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.readonly", "Read only scope", false));
        resources.add(createResource(auth7, 29981500157L, "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Simple"));
    }

    private static Resource createResource(Auth auth, Long code, String url, String desc, String name, String version, String title) {
        Resource resource = new Resource(auth, code, url, desc, name, version, title);
        return resource;
    }

    private static Auth createAuth(Scope... scopes) {
        Auth auth = new Auth();
        auth.setScope(Arrays.asList(scopes));
        return auth;
    }

    private static Scope createScope(String name, String description, boolean secured) {
        return new Scope(name, description, secured);
    }
    
    //////////  Getters / Setters  //////////

    public IdentificatorGenerator getIdentificatorGenerator() {
        return identificatorGenerator;
    }

    public void setIdentificatorGenerator(IdentificatorGenerator identificatorGenerator) {
        this.identificatorGenerator = identificatorGenerator;
    }
    
}