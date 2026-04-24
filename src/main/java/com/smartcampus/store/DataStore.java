package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {
    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    static {
        Room library = new Room("LIB-301", "Library Quiet Study", 80);
        Room lab = new Room("LAB-201", "Computer Lab", 35);
        ROOMS.put(library.getId(), library);
        ROOMS.put(lab.getId(), lab);

        Sensor co2 = new Sensor("CO2-001", "CO2", "ACTIVE", 410.5, "LIB-301");
        SENSORS.put(co2.getId(), co2);
        library.getSensorIds().add(co2.getId());
        READINGS.put(co2.getId(), new CopyOnWriteArrayList<>());
    }

    private DataStore() {}
}
