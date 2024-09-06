/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver;

import com.unboundid.scim2.common.exceptions.ScimException;
import com.unboundid.scim2.common.types.AuthenticationScheme;
import com.unboundid.scim2.common.types.BulkConfig;
import com.unboundid.scim2.common.types.ChangePasswordConfig;
import com.unboundid.scim2.common.types.ETagConfig;
import com.unboundid.scim2.common.types.FilterConfig;
import com.unboundid.scim2.common.types.PatchConfig;
import com.unboundid.scim2.common.types.ServiceProviderConfigResource;
import com.unboundid.scim2.common.types.SortConfig;
import com.unboundid.scim2.server.resources.AbstractServiceProviderConfigEndpoint;
import java.util.Collections;

/**
 *
 * @author Owner
 */
public class TestServiceProviderConfigEndpoint extends AbstractServiceProviderConfigEndpoint {
    
    /**
   * {@inheritDoc}
   */
  @Override
  public ServiceProviderConfigResource getServiceProviderConfig()
      throws ScimException
  {
    return create();
  }

  /**
   * Create a test config resource.
   *
   * @return The created resource.
   */
  public static ServiceProviderConfigResource create()
  {
    return new ServiceProviderConfigResource("https://doc",
        new PatchConfig(true),
        new BulkConfig(true, 100, 1000),
        new FilterConfig(true, 200),
        new ChangePasswordConfig(true),
        new SortConfig(true),
        new ETagConfig(false),
        Collections.singletonList(
            new AuthenticationScheme(
                "Basic", "HTTP BASIC", null, null, "httpbasic", true)));
  }
    
}
