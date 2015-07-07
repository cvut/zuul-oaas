/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.models

import groovy.transform.EqualsAndHashCode
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.oauth2.provider.approval.Approval
import org.springframework.util.Base64Utils

import java.security.MessageDigest

import static org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.APPROVED
import static org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.DENIED

@CompoundIndexes([
    @CompoundIndex(name = 'compositeId', def = '{ userId: 1, clientId: 1, scope: 1 }', unique = true),
    @CompoundIndex(name = 'userIdAndClientId', def = '{ userId: 1, clientId: 1 }')
])
@TypeAlias('Approval')
@Document(collection = 'approvals')
@EqualsAndHashCode(includes = ['userId', 'clientId', 'scope'])
class PersistableApproval implements Serializable {

    private static final long serialVersionUID = 1L
    private static final SHA1 = MessageDigest.getInstance('SHA-1')

    @Id
    private final String id

    final String userId

    final String clientId

    final String scope

    boolean approved

    @Field('exp')
    Date expiresAt

    @LastModifiedDate
    Date lastUpdatedAt


    @PersistenceConstructor
    PersistableApproval(String userId, String clientId, String scope) {
        this.id = compositeId(userId, clientId, scope)
        this.userId = userId
        this.clientId = clientId
        this.scope = scope
    }

    PersistableApproval(Approval approval) {
        this(approval.userId, approval.clientId, approval.scope)
        approved = approval.status != DENIED
        expiresAt = approval.expiresAt
        lastUpdatedAt = approval.lastUpdatedAt
    }

    static Collection<PersistableApproval> createFrom(Iterable<Approval> oauthApprovals) {
        oauthApprovals.collect { new PersistableApproval(it) }
    }


    private static compositeId(userId, clientId, scope) {
        def digest = SHA1.digest([userId, clientId, scope].join(':').bytes)
        Base64Utils.encodeToString(digest)
    }

    Approval toOAuthApproval() {
        new Approval(userId, clientId, scope, expiresAt, approved ? APPROVED : DENIED, lastUpdatedAt)
    }

    String toString() {
        "{ userId: ${userId}, clientId: ${clientId}, scope: ${scope} }"
    }
}
