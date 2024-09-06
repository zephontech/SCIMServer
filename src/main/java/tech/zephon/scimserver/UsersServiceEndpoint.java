/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.exceptions.ResourceNotFoundException;
import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.exceptions.ServerErrorException;
import com.unboundid.scim2.common.messages.PatchOperation;
import com.unboundid.scim2.common.messages.PatchRequest;
import com.unboundid.scim2.common.types.EnterpriseUserExtension;
import com.unboundid.scim2.common.types.UserResource;
import static com.unboundid.scim2.common.utils.ApiConstants.MEDIA_TYPE_SCIM;
import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.server.annotations.ResourceType;
import com.unboundid.scim2.server.utils.ResourcePreparer;
import com.unboundid.scim2.server.utils.ResourceTypeDefinition;
import com.unboundid.scim2.server.utils.SimpleSearchResults;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Owner
 */
@ResourceType(
        description = "User Account",
        name = "User",
        schema = UserResource.class, requiredSchemaExtensions = EnterpriseUserExtension.class)
@Path("/Users")
public class UsersServiceEndpoint {
    
    // this is a in mem store. you have to add code to hadle your repository
    private final Map<String, UserResource> users = new HashMap<String, UserResource>();
    /**
     * A per resource life cycle Resource Endpoint implementation.
     */
    private static final ResourceTypeDefinition RESOURCE_TYPE_DEFINITION = ResourceTypeDefinition.fromJaxRsResource(UsersServiceEndpoint.class);

    /**
     * This method will return a JAX-RS response with status 'Unauthorized' and media type 'application/octet-stream' and an entity body that cannot be deserialized as JSON.
     *
     * @return The JAX-RS response.
     */
    @GET
    @Path("responseWithStatusUnauthorizedAndTypeOctetStreamAndBadEntity")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response getResponseWithStatusUnauthorizedAndTypeOctetStreamAndBadEntity() {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .entity("WhateverDude")
                .build();
    }

    /**
     * Test SCIM search.
     *
     * @param uriInfo The UriInfo.
     * @return The results.
     * @throws ScimException if an error occurs.
     */
    @GET
    @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    public SimpleSearchResults<UserResource> search(@Context final UriInfo uriInfo) throws ScimException {
        SimpleSearchResults<UserResource> results = new SimpleSearchResults<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
        results.addAll(users.values());
        return results;
    }

    /**
     * Test SCIM retrieve by ID.
     *
     * @param id The ID of the resource to retrieve.
     * @param uriInfo The UriInfo.
     * @return The result.
     * @throws ScimException if an error occurs.
     */
    @Path("{id}")
    @GET
    @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    public ScimResource retrieve(@PathParam("id") final String id, @Context final UriInfo uriInfo) throws ScimException {
        System.out.println("Find User:" + id);
        System.out.println("Current User:" + users);
        UserResource found = users.get(id);
        if (found == null) {
            throw new ResourceNotFoundException("No resource with ID " + id);
        }

        ResourcePreparer<UserResource> resourcePreparer = new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
        return resourcePreparer.trimRetrievedResource(found);
    }

    /**
     * Test SCIM create.
     *
     * @param resource The resource to create.
     * @param uriInfo The UriInfo.
     * @return The result.
     * @throws ScimException if an error occurs.
     */
    @POST
    @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    public ScimResource create(final UserResource resource, @Context final UriInfo uriInfo) throws ScimException {
        //resource.setId(String.valueOf(resource.hashCode()));
        System.out.println("User Created:" + resource.getId());
        users.put(resource.getId(), resource);
        System.out.println("Users Created:" + users);
        ResourcePreparer<UserResource> resourcePreparer = new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
        
        return resourcePreparer.trimCreatedResource(resource, resource);
    }

    /**
     * Test SCIM delete.
     *
     * @param id The ID of the resource to delete.
     * @throws ScimException if an error occurs.
     */
    @Path("{id}")
    @DELETE
    public void delete(@PathParam("id") final String id) throws ScimException {
        System.out.println("Delete User:" + id);
        UserResource found = users.remove(id);
        if (found == null) {
            throw new ResourceNotFoundException("No resource with ID " + id);
        }
    }

    /**
     * Test SCIM replace.
     *
     * @param id the ID of the resource to replace.
     * @param resource The resource to create.
     * @param uriInfo The UriInfo.
     * @return The result.
     * @throws ScimException if an error occurs.
     */
    @Path("{id}")
    @PUT
    @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    public ScimResource replace(@PathParam("id") final String id, final UserResource resource, @Context final UriInfo uriInfo) throws ScimException {
        System.out.println("Replace User:" + id);
        if (!users.containsKey(id)) {
            throw new ResourceNotFoundException("No resource with ID " + id);
        }
        users.put(id, resource);
        ResourcePreparer<UserResource> resourcePreparer
                = new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
        return resourcePreparer.trimReplacedResource(resource, resource);
    }

    /**
     * Test SCIM modify.
     *
     * @param id The ID of the resource to modify.
     * @param patchRequest The patch request.
     * @param uriInfo The UriInfo.
     * @return The result.
     * @throws ScimException if an error occurs.
     */
    @Path("{id}")
    @PATCH
    @Consumes({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    @Produces({MEDIA_TYPE_SCIM, MediaType.APPLICATION_JSON})
    public ScimResource modify(@PathParam("id") final String id, final PatchRequest patchRequest, @Context final UriInfo uriInfo) throws ScimException {
        System.out.println("Modify User:" + id);
        UserResource found = users.get(id);
        if (found == null) {
            throw new ResourceNotFoundException("No resource with ID " + id);
        }
        ObjectNode node = JsonUtils.valueToNode(found);
        for (PatchOperation operation : patchRequest) {
            operation.apply(node);
        }
        UserResource patchedFound = null;
        try {
            patchedFound = JsonUtils.getObjectReader().treeToValue(node, UserResource.class);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getMessage(), null, e);
        }
        users.put(id, patchedFound);
        ResourcePreparer<UserResource> resourcePreparer = new ResourcePreparer<UserResource>(RESOURCE_TYPE_DEFINITION, uriInfo);
        return resourcePreparer.trimModifiedResource(patchedFound, patchRequest);
    }
    
}
