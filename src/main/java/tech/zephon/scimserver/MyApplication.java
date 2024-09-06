/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver;

import com.unboundid.scim2.common.utils.JsonUtils;
import com.unboundid.scim2.server.providers.JsonProcessingExceptionMapper;
import com.unboundid.scim2.server.providers.RuntimeExceptionMapper;
import com.unboundid.scim2.server.providers.ScimExceptionMapper;
import com.unboundid.scim2.server.providers.DotSearchFilter;
import com.unboundid.scim2.server.providers.DefaultContentTypeFilter;
import com.unboundid.scim2.server.resources.ResourceTypesEndpoint;
import com.unboundid.scim2.server.resources.SchemasEndpoint;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.JaxRSFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author Owner
 */
@ApplicationPath("/")
public class MyApplication extends ResourceConfig {

    public MyApplication() {
        
        register(ScimExceptionMapper.class);
        register(RuntimeExceptionMapper.class);
        register(JsonProcessingExceptionMapper.class);

        JacksonJsonProvider provider = new JacksonJsonProvider(JsonUtils.createObjectMapper());
        provider.configure(JaxRSFeature.ALLOW_EMPTY_INPUT, false);
        register(provider);

        // Filters
        register(DotSearchFilter.class);
        register(AuthenticationFilter.class);
        register(DefaultContentTypeFilter.class);
        //TestRequestFilter requestFilter = new TestRequestFilter();
        //register(requestFilter);

        // Standard endpoints
        register(ResourceTypesEndpoint.class);
        register(CustomContentEndpoint.class);
        register(SchemasEndpoint.class);
        register(TestServiceProviderConfigEndpoint.class);

        // create a singleton for an in memory DB otherwise pass in the .class like above
        register(new UsersServiceEndpoint());
        register(new GroupsServiceEndpoint());
        //register(new TestResourceEndpoint());
        //register(new TestSingletonResourceEndpoint());

    }

}
