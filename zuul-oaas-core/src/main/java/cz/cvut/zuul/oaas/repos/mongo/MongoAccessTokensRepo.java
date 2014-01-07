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
package cz.cvut.zuul.oaas.repos.mongo;

import cz.cvut.zuul.oaas.repos.AccessTokensRepo;
import cz.cvut.zuul.oaas.models.PersistableAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.annotation.PostConstruct;
import java.util.Collection;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
public class MongoAccessTokensRepo
        extends AbstractMongoRepository<PersistableAccessToken, String> implements AccessTokensRepo {

    private static final String
            CLIENT_ID = "authentication.authorization_request.client_id",
            USER_NAME = "authentication.user_authentication.user_name";

    private static final IndexDefinition[] INDEXES = {
        new Index().on("authenticationKey", Order.ASCENDING),
        new Index().on("refreshToken", Order.ASCENDING),
        new Index().on(CLIENT_ID, Order.ASCENDING),
        new Index().on(USER_NAME, Order.ASCENDING)
    };


    @PostConstruct
    protected void ensureIndexes() {
        for (IndexDefinition index : INDEXES) {
            mongo().indexOps(entityClass()).ensureIndex(index);
        }
    }


    public PersistableAccessToken findOneByAuthentication(OAuth2Authentication authentication) {
        String authKey = PersistableAccessToken.extractAuthenticationKey(authentication);

        PersistableAccessToken accessToken = mongo().findOne(query(
                where("authenticationKey").is(authKey)),
                entityClass());

        if (accessToken == null) {
            log.debug("Failed to find access token for authentication: [{}] with key: [{}]", authentication, authKey);
        }

        if (accessToken != null && !authentication.equals(accessToken.getAuthentication())) {
            log.debug("Stored authentication details differs from given one, updating to keep the store consistent");
            delete(accessToken); //TODO not needed?
            // keep the store consistent (maybe the same user is represented by this auth. but the details have changed)
            save(new PersistableAccessToken(accessToken, authentication));
        }
        return accessToken;
    }

    public Collection<OAuth2AccessToken> findByClientId(String clientId) {
        return findTokensBy(CLIENT_ID, clientId);
    }

    public Collection<OAuth2AccessToken> findByUserName(String userName) {
        return findTokensBy(USER_NAME, userName);
    }

    public void deleteByRefreshToken(OAuth2RefreshToken refreshToken) {
        mongo().remove(query(
                where("refreshToken").is(refreshToken.getValue())),
                entityClass());
    }

    public void deleteByClientId(String clientId) {
        mongo().remove(query(
                where(CLIENT_ID).is(clientId)),
                entityClass());
    }


    private Collection<OAuth2AccessToken> findTokensBy(String field, Object value) {
        Query query = query(where(field).is(value));
        query.fields().exclude("authentication");

        return (Collection) mongo().find(query, entityClass());
    }
}
