package com.smartcampus.resource;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    @GET
    public Map<String, Object> discover() {
        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        links.put("sensorReadings", "/api/v1/sensors/{sensorId}/readings");
        links.put("filteredSensors", "/api/v1/sensors?type=CO2");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "Smart Campus Sensor and Room Management API");
        response.put("version", "1.0");
        response.put("adminContact", "smart-campus-admin@westminster.ac.uk");
        response.put("description", "JAX-RS REST API for managing rooms, sensors and sensor readings.");
        response.put("links", links);
        return response;
    }
}
