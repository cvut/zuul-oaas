package cz.cvut.zuul.oaas.config

import com.mongodb.Mongo
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.authentication.UserCredentials

@Configuration
class TestMongoPersistenceConfig extends MongoPersistenceConfig {

    String databaseName = 'oaas_test'

    UserCredentials userCredentials = null

    def loadDatabaseSeed() {}

    /**
     * Embedded MongoDB
     *
     * It's not truly embedded 'cause there's no Java implementation of the MongoDB. This factory
     * initializes embedmongo.flapdoodle.de that is able to download original MongoDB binary for
     * your platform and run it for your integration tests.
     */
    @Bean EmbeddedMongoFactoryBean getMongoFactoryBean() {
        new EmbeddedMongoFactoryBean(version: '2.2.6')
    }

    Mongo mongo() { mongoFactoryBean.object }
}
