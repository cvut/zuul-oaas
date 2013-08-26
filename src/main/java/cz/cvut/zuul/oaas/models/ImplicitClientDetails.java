package cz.cvut.zuul.oaas.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 */
@Data
@AllArgsConstructor @NoArgsConstructor
public class ImplicitClientDetails {
    
    private String type;
}
