package com.smartcampus;

import com.smartcampus.config.ApplicationConfig;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) {
        try {
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                    URI.create(BASE_URI),
                    new ApplicationConfig(),
                    false
            );

            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();

            System.out.println("-------------------------------------------------");
            System.out.println(" Smart Campus JAX-RS REST API is running");
            System.out.println("-------------------------------------------------");
            System.out.println("Discovery endpoint: " + BASE_URI);
            System.out.println("Rooms endpoint:     " + BASE_URI + "rooms");
            System.out.println("Sensors endpoint:   " + BASE_URI + "sensors");
            System.out.println("Press CTRL + C to stop the server.");
            Thread.currentThread().join();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "The Smart Campus API could not start", ex);
        }
    }
}
