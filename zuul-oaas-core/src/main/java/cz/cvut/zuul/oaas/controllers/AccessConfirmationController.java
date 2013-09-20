package cz.cvut.zuul.oaas.controllers;

import cz.cvut.zuul.oaas.api.models.ClientDTO;
import cz.cvut.zuul.oaas.api.services.ClientsService;
import lombok.Setter;
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

	private @Setter ClientsService clientsService;


	@RequestMapping("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model) {
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");

		ClientDTO client = clientsService.findClientById(clientAuth.getClientId());
		model.put("auth_request", clientAuth);
		model.put("client", client);

		return new ModelAndView("confirm_access", model);
	}

	@RequestMapping("/oauth/error")
	public String handleError() {
		return "oauth_error";
	}
}
