package cz.cvut.zuul.oaas.dao.memory;

import cz.cvut.zuul.oaas.api.resources.exceptions.NoSuchResourceException;
import cz.cvut.zuul.oaas.dao.ResourceDAO;
import cz.cvut.zuul.oaas.generators.IdentifierGenerator;
import cz.cvut.zuul.oaas.models.Auth;
import cz.cvut.zuul.oaas.models.Resource;
import cz.cvut.zuul.oaas.models.Scope;
import cz.cvut.zuul.oaas.models.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Deprecated
public class ResourceInMemoryDAO implements ResourceDAO {

    private static List<Resource> resources;
    
    @Autowired
    private IdentifierGenerator identifierGenerator;

    static {
        resources = new ArrayList<Resource>();
        populateResources();
    }

    public boolean exists(String id) {
        for (Resource resource : resources) {
            if (resource.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    public <S extends Resource> S save(S resource) {
        resource.setId(identifierGenerator.generateArgBasedIdentifier(resource.getTitle()));
        resources.add(resource);
        return resource;
    }

    public void update(String id, Resource resource) throws NoSuchResourceException{
        Resource update = findOne(id);
        resources.remove(update);
        resources.add(resource);
    }

    public List<Resource> findAllPublic() {
        List<Resource> publicResources = new ArrayList<Resource>();
        for (Resource resource : resources) {
            if (resource.getVisibility().equals(Visibility.PUBLIC.toString())) {
                publicResources.add(resource);
            }
        }
        return publicResources;
    }
    
    public List<Resource> findAll() {
        return resources;
    }

    public Resource findOne(String id) throws NoSuchResourceException{
        for (Resource resource : resources) {
            if (resource.getId().equals(id)) {
                return resource;
            }
        }
        throw new NoSuchResourceException(String.format("No resource exists with given id [%s]", id));
    }

    public void delete(String id) throws NoSuchResourceException{
        Resource deleted = findOne(id);
        resources.remove(deleted);
    }

    private static void populateResources() {
        Auth auth1 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.readonly", "Read only scope", false));
        resources.add(createResource(auth1, "kos-api-basic-880693", "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Basic"));
        Auth auth2 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.write", "Write scope", true));
        resources.add(createResource(auth2, "kos-api-master-646521", "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Master"));
        Auth auth3 = createAuth(createScope("https://www.cvutapis.cz/auth/kosapi.teacher", "Teacher scope", true));
        resources.add(createResource(auth3, "kos-api-teacher-296880", "https://www.cvutapis.cz/kosapi/v3", "API for access to the data within KOS db.", "kosapi", "v3", "KOS API Teacher"));
        Auth auth4 = createAuth(createScope("https://www.cvutapis.cz/auth/erasmus.readonly", "Read only scope", false));
        resources.add(createResource(auth4, "erasmus-api-429817", "https://www.cvutapis.cz/erasmusapi/v1", "API for access to the data within Erasmus db.", "erasmusapi", "v1", "Erasmus API"));
        Auth auth5 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.student", "Student scope", true));
        resources.add(createResource(auth5, "edux-api-student-716690", "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Student"));
        Auth auth6 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.write", "Write scope", true));
        resources.add(createResource(auth6, "edux-api-pro-927661", "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Pro"));
        Auth auth7 = createAuth(createScope("https://www.cvutapis.cz/auth/edux.readonly", "Read only scope", false));
        resources.add(createResource(auth7, "edux-api-simple-8876512", "https://www.cvutapis.cz/eduxapi/v2", "API for access to the data within Edux db.", "eduxapi", "v2", "Edux API Simple"));
    }

    private static Resource createResource(Auth auth, String code, String url, String desc, String name, String version, String title) {
        return new Resource(auth, code, URI.create(url), desc, name, version, title, Visibility.PUBLIC);
    }

    private static Auth createAuth(Scope... scopes) {
        Auth auth = new Auth();
        auth.setScope(Arrays.asList(scopes));
        return auth;
    }

    private static Scope createScope(String name, String description, boolean secured) {
        return new Scope(name, description, secured);
    }



    public Iterable<Resource> findAll(Iterable<String> strings) {
        throw new IllegalStateException("Not implemented");
    }

    public long count() {
        throw new IllegalStateException("Not implemented");
    }


    public <S extends Resource> Iterable<S> save(Iterable<S> entities) {
        throw new IllegalStateException("Not implemented");
    }

    public void delete(Resource entity) {
        throw new IllegalStateException("Not implemented");
    }

    public void delete(Iterable<? extends Resource> entities) {
        throw new IllegalStateException("Not implemented");
    }

    public void deleteAll() {
        throw new IllegalStateException("Not implemented");
    }

    
    //////////  Getters / Setters  //////////

    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }

    public void setIdentifierGenerator(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }
    
    
}
