package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Smart Campus API - Main Entry Point
 * Student: M A Rushen Kavindu
 * Student ID: w2153204
 * Module: 5COSC022W - Client-Server Architectures
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8080/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.smartcampus");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        System.out.println("=============================================================");
        System.out.println("  Smart Campus Sensor & Room Management API");
        System.out.println("  Student: M A Rushen Kavindu | ID: w2153204");
        System.out.println("  Module: 5COSC022W - Client-Server Architectures");
        System.out.println("=============================================================");
        System.out.println("  Server started at: " + BASE_URI);
        System.out.println("  API Base URL:       " + BASE_URI + "api/v1");
        System.out.println("  Discovery:          GET " + BASE_URI + "api/v1");
        System.out.println("  Rooms:              GET " + BASE_URI + "api/v1/rooms");
        System.out.println("  Sensors:            GET " + BASE_URI + "api/v1/sensors");
        System.out.println("  Health:             GET " + BASE_URI + "api/v1/health");
        System.out.println("  Stats:              GET " + BASE_URI + "api/v1/stats");
        System.out.println("=============================================================");
        System.out.println("  Press ENTER to stop the server...");
        System.out.println("=============================================================");

        System.in.read();
        server.stop();
        LOGGER.info("Server stopped.");
    }
}
