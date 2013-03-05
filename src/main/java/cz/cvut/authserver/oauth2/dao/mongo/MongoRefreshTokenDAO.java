package cz.cvut.authserver.oauth2.dao.mongo;

import cz.cvut.authserver.oauth2.dao.RefreshTokenDAO;
import cz.cvut.authserver.oauth2.models.PersistableRefreshToken;

/**
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MongoRefreshTokenDAO
        extends AbstractMongoGenericDAO<PersistableRefreshToken, String> implements RefreshTokenDAO {

}
