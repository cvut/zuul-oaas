package cz.cvut.authserver.oauth2.controllers;

/*
 * Cloud Foundry 2012.02.03 Beta
 * Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 */
import cz.cvut.authserver.oauth2.api.models.JsonExceptionMapping;
import cz.cvut.authserver.oauth2.converter.AccessTokenConverter;
import cz.cvut.authserver.oauth2.converter.DefaultTokenConverter;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import static org.springframework.http.HttpStatus.*;

/**
 * Controller which decodes access tokens for clients who are not able to do so
 * (or where opaque token values are used).
 *
 * @author Luke Taylor
 * @author Tomas Mano
 */
@Controller
public class CheckTokenEndpoint implements InitializingBean {

    private AccessTokenConverter tokenConverter = new DefaultTokenConverter();
    private ResourceServerTokenServices resourceServerTokenServices;

    public void setTokenConverter(AccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
    }

    public void setTokenServices(ResourceServerTokenServices resourceServerTokenServices) {
        this.resourceServerTokenServices = resourceServerTokenServices;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(resourceServerTokenServices, "tokenServices must be set");
    }

    @RequestMapping(value = "/check_token")
    @ResponseBody
    public Map<String, ?> checkToken(@RequestParam("token") String value) {
        
        // first check if token is recognized and if it is not expired
        
        OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(value);
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }
        
        // now load authentication and add all required details necessary for resource provider to response

        OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(value);
        Map<String, ?> response = tokenConverter.convertAccessToken(token, authentication);

        return response;
    }
    
    //////////  Exceptions Handling  //////////
 
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseBody
    public JsonExceptionMapping handleTokenProblem(InvalidTokenException ex) {
        // TODO Should we really return 409 CONFLICT ? Status message from exception is 401
        return new JsonExceptionMapping(null, CONFLICT.value(), ex.getOAuth2ErrorCode().toString(), ex.getMessage());
    }
}
