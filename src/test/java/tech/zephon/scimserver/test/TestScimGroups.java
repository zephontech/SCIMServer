/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver.test;

import com.unboundid.scim2.client.ScimService;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.filters.Filter;
import com.unboundid.scim2.common.messages.ListResponse;
import com.unboundid.scim2.common.types.GroupResource;
import com.unboundid.scim2.common.types.Member;
import com.unboundid.scim2.common.types.UserResource;
import java.net.URI;
import java.util.ArrayList;
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
public class TestScimGroups extends BaseTestClass {
    
    ScimService scimService = null;
    @Test
    public void mainTest()
    {
        /*
        Client client = ClientBuilder.newClient();
        String userName = "spadmin";
        String password = "admin";
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName,password);
        client.register(feature);
        client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        WebTarget target = client.target("http://sailpoint.testserver.org:8080/SCIMServer/scim/v2");
        scimService = new ScimService(target);
*/
        this.scimService = this.getScimService();
        
        if (scimService == null)
        {
            System.out.println("ERROR:null client");
            return;
        }

        
        GroupResource group = null;
        
        try
        {
            group = this.createGroup();
            System.out.println("Group:" + group.getId() + ":" + group.toString());
            GroupResource newGroup = this.getGroup(group.getId());
            System.out.println("newGroup:" + newGroup.getId() + ":" + newGroup.toString());
            //newGroup = this.addUsersToGroup("d1978d98-849d-4b2a-baef-6dedf5214584", newGroup.getId());
            //System.out.println("newGroup Users:" + newGroup.getId() + ":" + newGroup.toString());
            
        }
        catch(Exception e)
        {
            System.out.println("ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        
        List<UserResource> allUsers = new ArrayList();
        
        try
        {
            allUsers = this.getUsers();
        }
        catch(Exception e)
        {
            System.out.println("ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        
        if (allUsers.isEmpty())
        {
            System.out.println("No Users");
            return;
        }
        try
        {
            for(UserResource ur : allUsers)
            {
                this.addUsersToGroup(ur, group);
            }
        }
        catch(Exception e)
        {
            System.out.println("ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    public GroupResource createGroup() throws ScimException {
        // Create a user
        GroupResource group = new GroupResource();
        String id = UUID.randomUUID().toString();
        group.setId(id);
        group.setDisplayName("TESTGROUP-" + id);
        System.out.println("Group Created");
        group = scimService.create("Groups", group);
        return group;

    }
    
    public GroupResource addUsersToGroup(UserResource user,GroupResource group) throws Exception
    {
        
        List<Member> members = group.getMembers();
        if (members == null)
        {
            members = new ArrayList();
        }
        if (members.contains(user.getId()))
        {
            return group;
        }
        
        Member m1 = new Member();
        m1.setValue(user.getId());
        m1.setRef(user.getProfileUrl());
        members.add(m1);
        group.setMembers(members);
        System.out.println("User added to group");
        group = scimService.replace(group);
        return group;
    }
    
    public GroupResource getGroup(String id) throws ScimException
    {
        // Retrieve the user as a UserResource and replace with a modified instance using PUT
        GroupResource group = scimService.retrieve("Groups", id, GroupResource.class);
        return group;
    }
    
    public List<UserResource> getUsers() throws Exception
    {
        // Search for users with the same last name as our user
        ListResponse<UserResource> searchResponse = scimService.searchRequest("Users")
        /*
                        .filter(Filter.eq("name.familyName", user.getName().getFamilyName()).toString())
                        .page(1, 5)
                        .attributes("name") */
                        .invoke(UserResource.class);
        if (searchResponse != null && searchResponse.getResources().size() > 0)
        {
            List<UserResource> users = searchResponse.getResources();
            for(UserResource u : users)
            {
                System.out.println("U:" + u.getId());
            }
            return users;
        }
        return null;
               
    }
    
}
