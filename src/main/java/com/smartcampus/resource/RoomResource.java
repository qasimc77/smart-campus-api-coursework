package com.smartcampus.resource;

import com.smartcampus.exception.InputValidationException;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {
    @GET
    public List<Room> getRooms() {
        return new ArrayList<>(DataStore.ROOMS.values());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        validateRoom(room);
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        Room existing = DataStore.ROOMS.putIfAbsent(room.getId(), room);
        if (existing != null) {
            throw new WebApplicationException("Room already exists: " + room.getId(), Response.Status.CONFLICT);
        }
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because it still has active sensors assigned to it.");
        }
        DataStore.ROOMS.remove(roomId);
        return Response.noContent().build();
    }

    private void validateRoom(Room room) {
        if (room == null) {
            throw new InputValidationException("Room JSON body is required.");
        }
        if (isBlank(room.getId())) {
            throw new InputValidationException("Room id is required.");
        }
        if (isBlank(room.getName())) {
            throw new InputValidationException("Room name is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new InputValidationException("Room capacity must be greater than zero.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
