package cz.cvut.zuul.oaas.test.factories

import net.java.quickcheck.Generator
import net.java.quickcheck.srcgenerator.Samples

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Samples
class CustomGenerators {

    static Generator<String> emails() {
        return {
            (anyLetterString(1, 8) + '@' + anyLetterString(1, 8) + '.' + anyLetterString(2, 5)).toLowerCase()
        } as Generator
    }
}
