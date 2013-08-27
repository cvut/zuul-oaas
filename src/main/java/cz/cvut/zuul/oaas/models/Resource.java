package cz.cvut.zuul.oaas.models;

import cz.cvut.zuul.oaas.models.enums.Visibility;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Data
@EqualsAndHashCode(of="id")
@ToString(of={"id", "name", "version"})

@TypeAlias("Resource")
@Document(collection = "resources")

public class Resource implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    private String id;

    private String baseUrl;
    
    private String description;
    
    private String name;
    
    private String version;

    private List<Scope> scopes;

    @Indexed
    private Visibility visibility = Visibility.PUBLIC;

}
