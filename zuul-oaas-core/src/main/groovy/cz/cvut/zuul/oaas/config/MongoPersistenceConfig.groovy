/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.config

import com.mongodb.*
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
import cz.cvut.zuul.oaas.repos.mongo.converters.GrantedAuthorityReaderConverter
import cz.cvut.zuul.oaas.repos.mongo.converters.GrantedAuthorityWriteConverter
import cz.cvut.zuul.oaas.repos.mongo.converters.OAuth2AuthenticationReadConverter
import cz.cvut.zuul.oaas.repos.mongo.converters.OAuth2AuthenticationWriteConverter
import cz.cvut.zuul.oaas.repos.mongo.support.MongoSeedLoader
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.authentication.UserCredentials
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.util.Assert

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
        def servers = $('persistence.mongo.servers').split(',')

        def opts = MongoClientOptions.builder()
        opts.writeConcern(WriteConcern.valueOf( $('persistence.mongo.write_concern')) )

        // replica set
        if (servers.length > 1) {
            opts.readPreference(ReadPreference.valueOf( $('persistence.mongo.read_preference')) )

            new MongoClient( servers.collect { parseServerAddress(it) }, opts.build() )

        // single node
        } else {
            new MongoClient( parseServerAddress(servers[0]), opts.build() )
        }
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
            new OAuth2AuthenticationWriteConverter()
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


    def parseServerAddress(String value) {
        Assert.hasText(value, "server address must not be empty")

        def tokens = value.split(':', 2)
        def host = tokens[0].trim()
        def port = tokens.length > 1 ? tokens[1].trim().toInteger() : ServerAddress.defaultPort()

        new ServerAddress(host, port)
    }
}
