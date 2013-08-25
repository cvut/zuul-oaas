package cz.cvut.zuul.oaas.test.factories

import net.java.quickcheck.Generator

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class CustomGenerators {

    static Generator<String> emails() {
        return {
            (anyLetterString(1, 8) + '@' + anyLetterString(1, 8) + '.' + anyLetterString(2, 5)).toLowerCase()
        } as Generator
    }
}
