/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.persistence.config

import com.zaxxer.hikari.HikariDataSource
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.config.PersistenceBeans
import cz.cvut.zuul.oaas.persistence.JdbcAccessTokensRepo
import cz.cvut.zuul.oaas.persistence.JdbcApprovalsRepo
import cz.cvut.zuul.oaas.persistence.JdbcAuthorizationCodesRepo
import cz.cvut.zuul.oaas.persistence.JdbcClientsRepo
import cz.cvut.zuul.oaas.persistence.JdbcRefreshTokensRepo
import cz.cvut.zuul.oaas.persistence.JdbcRepositoriesCleaner
import cz.cvut.zuul.oaas.persistence.JdbcResourcesRepo
import cz.cvut.zuul.oaas.repos.AccessTokensRepo
import cz.cvut.zuul.oaas.repos.ApprovalsRepo
import cz.cvut.zuul.oaas.repos.AuthorizationCodesRepo
import cz.cvut.zuul.oaas.repos.ClientsRepo
import cz.cvut.zuul.oaas.repos.RefreshTokensRepo
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
class JdbcPersistenceConfig implements ConfigurationSupport, PersistenceBeans {

    @Autowired ApplicationContext context


    @Bean(destroyMethod = 'shutdown')
    def dataSource() {
        new HikariDataSource (
            autoCommit:           true,  // TODO should be disabled
            connectionTimeout:    10_000,  // 10 seconds
            maximumPoolSize:      p('persistence.jdbc.pool.max_size') as int,
            minimumIdle:          p('persistence.jdbc.pool.min_idle') as int,
            maxLifetime:          (p('persistence.jdbc.pool.max_lifetime') as int) * 1000,
            idleTimeout:          (p('persistence.jdbc.pool.idle_timeout') as int) * 1000,
            dataSourceClassName:  p('persistence.jdbc.data_source_class'),
            dataSourceProperties: [
                host:             p('persistence.jdbc.host'),
                port:             p('persistence.jdbc.port') as int,
                database:         p('persistence.jdbc.dbname'),
                user:             p('persistence.jdbc.username'),
                password:         p('persistence.jdbc.password'),
                networkTimeout:   10_000,  // 10 seconds
                applicationName:  'zuul-oaas',
                housekeeper:      false  // see https://github.com/impossibl/pgjdbc-ng/issues/129
            ] as Properties
        )
    }

    @Bean DataSourceTransactionManager transactionManager() {
        new DataSourceTransactionManager( dataSource() )
    }

    @Bean(initMethod='migrate')
    def flyway() {
        new Flyway (
            dataSource: dataSource(),
            locations: ['classpath:db/migration'],
            sqlMigrationSuffix: '.pg.sql',
            initOnMigrate: true,
            cleanOnValidationError: isActive('test')
        )
    }

    @Bean @DependsOn('flyway')
    def jdbcTemplate() {
        new JdbcTemplate( dataSource() )
    }

    @Bean @Profile('test')
    def repositoriesCleaner() {
        new JdbcRepositoriesCleaner( context, jdbcTemplate() )
    }


    //////// Repositories ////////

    @Bean ClientsRepo clientsRepo() {
        new JdbcClientsRepo( jdbcTemplate() )
    }

    @Bean AccessTokensRepo accessTokensRepo() {
        new JdbcAccessTokensRepo( jdbcTemplate() )
    }

    @Bean RefreshTokensRepo refreshTokensRepo() {
        new JdbcRefreshTokensRepo( jdbcTemplate() )
    }

    @Bean ResourcesRepo resourcesRepo() {
        new JdbcResourcesRepo( jdbcTemplate() )
    }

    @Bean AuthorizationCodesRepo authorizationCodesRepo() {
        new JdbcAuthorizationCodesRepo( jdbcTemplate() )
    }

    @Bean ApprovalsRepo approvalsRepo() {
        new JdbcApprovalsRepo( jdbcTemplate() )
    }
}
