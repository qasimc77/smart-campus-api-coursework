package com.smartcampus.mapper;

import com.smartcampus.dto.ErrorResponse;
import com.smartcampus.exception.InputValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InputValidationMapper implements ExceptionMapper<InputValidationException> {
    @Override
    public Response toResponse(InputValidationException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(400, "Bad Request", ex.getMessage()))
                .build();
    }
}
