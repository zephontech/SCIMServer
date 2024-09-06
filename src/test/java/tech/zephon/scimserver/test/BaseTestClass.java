/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver.test;

import com.unboundid.scim2.client.ScimService;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author Owner
 */
public class BaseTestClass {
    
    private String userName = "spadmin";
    private String password = "admin";
    private String scimTarget = "http://sailpoint.testserver.org:8080/SCIMServer/scim/v2";
    
    
    public ScimService getScimService()
    {
        Client client = ClientBuilder.newClient();
        String userName = this.userName;
        String password = this.password;
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName,password);
        client.register(feature);
        // allow the 
        client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        WebTarget target = client.target(this.scimTarget);
        ScimService scimService = new ScimService(target);
        if (scimService == null)
        {
            System.out.println("ERROR:null group service");
            return null;
        }
        return scimService;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScimTarget(String scimTarget) {
        this.scimTarget = scimTarget;
    }

    
    
    
    
}
