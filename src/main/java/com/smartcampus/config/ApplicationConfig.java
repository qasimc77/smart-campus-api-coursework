package com.smartcampus.config;

import com.smartcampus.filter.ApiLoggingFilter;
import com.smartcampus.mapper.GlobalExceptionMapper;
import com.smartcampus.mapper.InputValidationMapper;
import com.smartcampus.mapper.LinkedResourceNotFoundMapper;
import com.smartcampus.mapper.RoomNotEmptyMapper;
import com.smartcampus.mapper.SensorUnavailableMapper;
import com.smartcampus.mapper.WebApplicationExceptionMapper;
import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        register(DiscoveryResource.class);
        register(RoomResource.class);
        register(SensorResource.class);
        register(RoomNotEmptyMapper.class);
        register(LinkedResourceNotFoundMapper.class);
        register(SensorUnavailableMapper.class);
        register(InputValidationMapper.class);
        register(WebApplicationExceptionMapper.class);
        register(GlobalExceptionMapper.class);
        register(ApiLoggingFilter.class);
        register(JacksonFeature.class);
    }
}
