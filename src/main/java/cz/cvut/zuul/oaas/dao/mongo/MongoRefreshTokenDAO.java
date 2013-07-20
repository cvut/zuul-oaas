package cz.cvut.zuul.oaas.dao.mongo;

import cz.cvut.zuul.oaas.dao.RefreshTokenDAO;
import cz.cvut.zuul.oaas.models.PersistableRefreshToken;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokenDAO
        extends AbstractMongoGenericDAO<PersistableRefreshToken, String> implements RefreshTokenDAO {

}
