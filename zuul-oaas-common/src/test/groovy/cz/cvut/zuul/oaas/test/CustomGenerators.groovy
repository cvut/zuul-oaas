package cz.cvut.zuul.oaas.test

import net.java.quickcheck.Generator

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyDate
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyLetterString

class CustomGenerators {

    static Generator<String> emails() {
        return {
            (anyLetterString(1, 8) + '@' + anyLetterString(1, 8) + '.' + anyLetterString(2, 5)).toLowerCase()
        } as Generator
    }

    static Generator<Date> futureDates() {
        return {
            anyDate(new Date() + 1, new Date() + 365)
        } as Generator
    }

    static Generator<Date> pastDates() {
        return {
            anyDate(new Date() - 365, new Date() - 1)
        } as Generator
    }
}
