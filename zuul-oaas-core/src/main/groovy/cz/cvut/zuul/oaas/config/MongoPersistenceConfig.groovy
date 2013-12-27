package cz.cvut.zuul.oaas.config

import com.mongodb.Mongo
import com.mongodb.MongoClient
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import cz.cvut.zuul.oaas.repos.mongo.MongoAccessTokensRepo
import cz.cvut.zuul.oaas.repos.mongo.MongoClientsRepo
import cz.cvut.zuul.oaas.repos.mongo.MongoRefreshTokensRepo
import cz.cvut.zuul.oaas.repos.mongo.MongoResourcesRepo
import cz.cvut.zuul.oaas.repos.mongo.converters.*
import cz.cvut.zuul.oaas.repos.mongo.support.MongoSeedLoader
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.authentication.UserCredentials
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.CustomConversions

import javax.annotation.PostConstruct
import javax.inject.Inject

import static org.springframework.data.mongodb.core.WriteResultChecking.EXCEPTION

//TODO MongoDB JMX
@Configuration
@Mixin(ConfigurationSupport)
class MongoPersistenceConfig extends AbstractMongoConfiguration implements PersistenceBeans {

    // Initialize mixed in ConfigurationSupport
    @Inject initSupport(ApplicationContext ctx) { _initSupport(ctx) }


    @PostConstruct loadDatabaseSeed() {
        if (profileDev) {
            new MongoSeedLoader(
                mongoTemplate:  mongoTemplate(),
                location:       classpath('/config/seeds.json')
            ).seed()
        }
    }

    @Lazy String mappingBasePackage = Resource.package.name

    @Lazy String databaseName = $('persistence.mongo.dbname')

    @Lazy UserCredentials userCredentials = new UserCredentials (
        $('persistence.mongo.username'),
        $('persistence.mongo.password')
    )


    @Bean Mongo mongo() {
        new MongoClient(
            $('persistence.mongo.host'),
            $('persistence.mongo.port') as int
        )
    }

    @Bean MongoTemplate mongoTemplate() {
        super.mongoTemplate().with {
            writeResultChecking = EXCEPTION
            return it
        }
    }

    @Bean CustomConversions customConversions() {
        new CustomConversions([
            new GrantedAuthorityReaderConverter(),
            new GrantedAuthorityWriteConverter(),
            new OAuth2AuthenticationReadConverter(),
            new OAuth2AuthenticationWriteConverter(),
            new RefreshTokenReaderConverter(),
            new RefreshTokenWriterConverter()
        ])
    }


    @Bean ClientsRepo clientsRepo() {
        new MongoClientsRepo (
            mongoOperations: mongoTemplate()
        )
    }

    @Bean AccessTokensRepo accessTokensRepo() {
        new MongoAccessTokensRepo (
            mongoOperations: mongoTemplate()
        )
    }

    @Bean RefreshTokensRepo refreshTokensRepo() {
        new MongoRefreshTokensRepo (
            mongoOperations: mongoTemplate()
        )
    }

    @Bean ResourcesRepo resourcesRepo() {
        new MongoResourcesRepo (
            mongoOperations: mongoTemplate()
        )
    }
}
