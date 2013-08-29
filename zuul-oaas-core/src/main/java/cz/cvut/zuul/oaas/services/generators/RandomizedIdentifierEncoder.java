package cz.cvut.zuul.oaas.services.generators;

import lombok.Setter;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.stripAccents;

/**
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class RandomizedIdentifierEncoder implements StringEncoder {
    
    private @Setter int numericSuffixLength = 4;


    public String encode(String value) {
        if (value == null) return null;

        String encoded = stripAccents(value)
                .replace("\\s+", "-")            //replace whitespaces with '-'
                .replaceAll("[^a-zA-Z0-9]", "")  //remove non-alphanumeric characters
                .toLowerCase();

        String suffix = randomNumeric(numericSuffixLength);

        return encoded + "-" + suffix;
    }
}
