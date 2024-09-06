/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.zephon.scimserver;

import com.unboundid.scim2.common.messages.ErrorResponse;
import com.unboundid.scim2.common.utils.StaticUtils;
import com.unboundid.scim2.server.utils.ServerUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author Owner
 */
//implements ContainerRequestFilter 
public class TestRequestFilter  {

    private MultivaluedMap<String, String> expectedHeaders = new MultivaluedHashMap<String, String>();
    private MultivaluedMap<String, String> expectedQueryParams = new MultivaluedHashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        validateRequest(requestContext, headers, expectedHeaders, true);

        MultivaluedMap<String, String> queryParams = requestContext.getUriInfo().getQueryParameters();
        validateRequest(requestContext, queryParams, expectedQueryParams, false);
    }

    /**
     * Adds an expected header and value.
     *
     * @param header The expected header name.
     * @param value The expected header value.
     */
    void addExpectedHeader(String header, String value) {
        expectedHeaders.add(header, value);
    }

    /**
     * Adds an expected query parameter and value.
     *
     * @param queryParameter The expected query parameter name.
     * @param value The expected query parameter value.
     */
    void addExpectedQueryParam(String queryParameter, String value) {
        expectedQueryParams.add(queryParameter, value);
    }

    /**
     * Resets the expected headers and query parameters.
     */
    void reset() {
        expectedHeaders.clear();
        expectedQueryParams.clear();
    }

    /**
     * Validates that a request contains the given set of expected headers or query parameters and values.
     *
     * @param requestContext The request context.
     * @param actual The actual set of headers or query parameters.
     * @param expected The expected set of headers or query parameters.
     * @param isCommaDelimited Whether a value may be comma-delimited.
     */
    private void validateRequest(ContainerRequestContext requestContext,
            MultivaluedMap<String, String> actual,
            MultivaluedMap<String, String> expected,
            boolean isCommaDelimited) {
        System.out.println("validateRequest:");
        System.out.println("requestContext:" + requestContext);
        System.out.println("actual:" + actual);
        System.out.println("expected:" + expected);
        if (!expected.isEmpty()) {
            for (Map.Entry<String, List<String>> expectedEntry : expected.entrySet()) {
                List<String> actualEntry = actual.get(expectedEntry.getKey());
                if (actualEntry != null && !actualEntry.isEmpty()) {
                    List<String> actualValues;
                    // Checking a header.
                    if (isCommaDelimited) {
                        actualValues
                                = Arrays.asList(StaticUtils.splitCommaSeparatedString(
                                        actual.get(expectedEntry.getKey()).get(0)));
                    } // Checking a query parameter.
                    else {
                        actualValues = actual.get(expectedEntry.getKey());
                    }
                    if (actualValues.containsAll(expectedEntry.getValue())) {
                        continue;
                    }
                }
                /*
                ErrorResponse errorResponse = errorResponse();
                requestContext.abortWith(ServerUtils.setAcceptableType(
                        Response.status(errorResponse.getStatus())
                                .entity(errorResponse),
                        requestContext.getAcceptableMediaTypes()).build()); */
            }
        }
    }

    private ErrorResponse errorResponse() {
        return new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode());
    }

}
