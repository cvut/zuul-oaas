/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.authserver.oauth2.controllers;

import java.util.Map;

import cz.cvut.authserver.oauth2.dao.ClientDAO;
import cz.cvut.authserver.oauth2.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for retrieving the model for and displaying the confirmation page for access to a protected resource.
 * 
 * @author Ryan Heaton
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
		return new ModelAndView("access_confirmation", model);
	}

	@RequestMapping("/oauth/error")
	public String handleError(Map<String,Object> model) {
		// We can add more stuff to the model here for JSP rendering.  If the client was a machine then
		// the JSON will already have been rendered.
		model.put("message", "Nastala chyba v protokole OAuth 2.0.");
		return "oauth_error";
	}

	@Required
	public void setClientDAO(ClientDAO clientDAO) {
		this.clientDAO = clientDAO;
	}
}
