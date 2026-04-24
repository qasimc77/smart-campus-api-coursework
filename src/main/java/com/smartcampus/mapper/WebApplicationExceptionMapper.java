package com.smartcampus.mapper;

import com.smartcampus.dto.ErrorResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException ex) {
        int status = ex.getResponse().getStatus();
        String message = ex.getMessage() == null ? "Request could not be processed." : ex.getMessage();
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(status, "HTTP Error", message))
                .build();
    }
}
