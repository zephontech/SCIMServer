/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver.test;

import com.fasterxml.jackson.databind.node.TextNode;
import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.GenericScimResource;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.Email;
import com.unboundid.scim2.common.types.UserResource;
import com.unboundid.scim2.common.types.Name;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;

/**
 *
 * @author Owner
 */
public class TestScimUsers {

    ScimService scimService = null;

    @Test
    public void mainTest() {
        // Create a ScimService
        //Client client = ClientBuilder.newClient().register(OAuth2ClientSupport.feature("..bearerToken.."));
        Client client = ClientBuilder.newClient();
        String userName = "spadmin";
        String password = "admin";
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName,password);
        client.register(feature);
        client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        WebTarget target = client.target("http://sailpoint.testserver.org:8080/SCIMServer/scim/v2");
        scimService = new ScimService(target);
        if (scimService == null)
        {
            System.out.println("ERROR:null client");
            return;
        }
        
        try
        {
            UserResource user = this.createUser();
            System.out.println("User:" + user.getId() + ":" + user.toString());
            this.updateUser(user);
            
        }
        catch(Exception e)
        {
            System.out.println("ERROR:" + e.getMessage());
            e.printStackTrace();
        }

    }

    public UserResource createUser() throws ScimException {
        // Create a user
        UserResource user = new UserResource();
        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setUserName("babs-" + id);
        user.setPassword("secret");
        Name name = new Name()
                .setGivenName("Barbara")
                .setFamilyName("Jensen");
        user.setName(name);
        Email email = new Email()
                .setType("home")
                .setPrimary(true)
                .setValue("babs@example.com");
        user.setEmails(Collections.singletonList(email));
        user = scimService.create("Users", user);
        System.out.println("User Created");
        return user;

    }

    public void updateUser(UserResource user) throws ScimException {
        // Retrieve the user as a UserResource and replace with a modified instance using PUT
        user = scimService.retrieve("Users", user.getId(), UserResource.class);
        user.setDisplayName("Babs");
        user = scimService.replace(user);

        // Retrieve the user as a GenericScimResource and replace with a modified instance using PUT
        GenericScimResource genericUser = scimService.retrieve("Users", user.getId(), GenericScimResource.class);
        genericUser.replaceValue("displayName", TextNode.valueOf("Babs Jensen"));
        genericUser = scimService.replaceRequest(genericUser).invoke();

        // Perform a partial modification of the user using PATCH
        scimService.modifyRequest("Users", user.getId()).replaceValue("displayName", "Babs").invoke(GenericScimResource.class);

        // Perform a password change using PATCH
        scimService.modifyRequest("Users", user.getId()).replaceValue("password", "new-password").invoke(GenericScimResource.class);

        // Search for users with the same last name as our user
        ListResponse<UserResource> searchResponse = scimService.searchRequest("Users")
                        //.filter(Filter.eq("name.familyName", user.getName().getFamilyName()).toString())
                        //.page(1, 5)
                        //.attributes("name")
                        .invoke(UserResource.class);
        if (searchResponse != null && searchResponse.getResources().size() > 0)
        {
            List<UserResource> users = searchResponse.getResources();
            for(UserResource u : users)
            {
                System.out.println("U:" + u.getId());
            }
        }
    }

}
