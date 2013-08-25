package cz.cvut.zuul.oaas.test.factories

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class CustomGeneratorSamples {

    static String anyEmail() { CustomGenerators.emails().next() }
}
