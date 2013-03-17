package cz.cvut.authserver.oauth2.controllers;

import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 *
 * @author Tomas Mano <tomasmano@gmail.com>
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Controller
@SessionAttributes("authorizationRequest")
public class AccessConfirmationController {

	private ClientDAO clientDAO;

	@RequestMapping("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model) throws OAuth2Exception {
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");

		Client client = clientDAO.findOne(clientAuth.getClientId());
		model.put("auth_request", clientAuth);
		model.put("client", client);

		return new ModelAndView("confirm_access", model);
	}

	@RequestMapping("/oauth/error")
	public String handleError(Map<String,Object> model) {
		return "oauth_error";
	}

	@Required
	public void setClientDAO(ClientDAO clientDAO) {
		this.clientDAO = clientDAO;
	}
}
