/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver;

import com.unboundid.scim2.server.providers.AuthenticatedSubjectAliasFilter;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Owner
 */
// extends AuthenticatedSubjectAliasFilter
public class TestAuthenticatedSubjectAliasFilter  {

    //@Override
    protected String getAuthenticatedSubjectPath(final SecurityContext securityContext) {
        
        System.out.println("getAuthenticatedSubjectPath");
        String prince = securityContext.getUserPrincipal().getName();
        System.out.println("getUserPrincipal:" + prince);
        return "Users/123";
    }

}
