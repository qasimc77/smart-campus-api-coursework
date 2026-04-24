package com.smartcampus.resource;

import com.smartcampus.exception.InputValidationException;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type,
                                   @QueryParam("status") String status,
                                   @QueryParam("roomId") String roomId) {
        return new ArrayList<>(DataStore.SENSORS.values()).stream()
                .filter(sensor -> isBlank(type) || (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)))
                .filter(sensor -> isBlank(status) || (sensor.getStatus() != null && sensor.getStatus().equalsIgnoreCase(status)))
                .filter(sensor -> isBlank(roomId) || (sensor.getRoomId() != null && sensor.getRoomId().equalsIgnoreCase(roomId)))
                .collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        validateSensor(sensor);
        Room linkedRoom = DataStore.ROOMS.get(sensor.getRoomId());
        if (linkedRoom == null) {
            throw new LinkedResourceNotFoundException("Sensor cannot be created because roomId " + sensor.getRoomId() + " does not exist.");
        }
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }
        Sensor existing = DataStore.SENSORS.putIfAbsent(sensor.getId(), sensor);
        if (existing != null) {
            throw new WebApplicationException("Sensor already exists: " + sensor.getId(), Response.Status.CONFLICT);
        }
        if (!linkedRoom.getSensorIds().contains(sensor.getId())) {
            linkedRoom.getSensorIds().add(sensor.getId());
        }
        DataStore.READINGS.putIfAbsent(sensor.getId(), new CopyOnWriteArrayList<>());
        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        return sensor;
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.remove(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        Room room = DataStore.ROOMS.get(sensor.getRoomId());
        if (room != null && room.getSensorIds() != null) {
            room.getSensorIds().remove(sensorId);
        }
        DataStore.READINGS.remove(sensorId);
        return Response.noContent().build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        if (!DataStore.SENSORS.containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        return new SensorReadingResource(sensorId);
    }

    private void validateSensor(Sensor sensor) {
        if (sensor == null) {
            throw new InputValidationException("Sensor JSON body is required.");
        }
        if (isBlank(sensor.getId())) {
            throw new InputValidationException("Sensor id is required.");
        }
        if (isBlank(sensor.getType())) {
            throw new InputValidationException("Sensor type is required.");
        }
        if (isBlank(sensor.getRoomId())) {
            throw new InputValidationException("Sensor roomId is required.");
        }
        if (!isBlank(sensor.getStatus()) && !sensor.getStatus().equalsIgnoreCase("ACTIVE")
                && !sensor.getStatus().equalsIgnoreCase("MAINTENANCE")
                && !sensor.getStatus().equalsIgnoreCase("OFFLINE")) {
            throw new InputValidationException("Sensor status must be ACTIVE, MAINTENANCE or OFFLINE.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
