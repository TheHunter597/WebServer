package com.mycompany.app.Handlers;

import java.io.IOException;
import java.net.Socket;

import com.mycompany.app.Request.Request;
import com.mycompany.app.Response.Route;

public class HandlerThread implements Runnable {
    Socket socket;
    HttpDriver driver;

    public HandlerThread(Socket socket, HttpDriver driver) {
        this.socket = socket;
        this.driver = driver;
    }

    @Override
    public void run() {
        try {
            System.err.println("Request handled by" + Thread.currentThread());
            Request bodyProcessor = new Request(socket.getInputStream());
            Thread.sleep(200);
            if (bodyProcessor.getRequest().length() == 0 || bodyProcessor.getMethod().equals("OPTIONS")) {
                // just made it return this untill I implement proper OPTIONS
                // handling
                String headers = "HTTP/1.1 204 No Content\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type\r\n" +
                        "\r\n";
                byte[] bytes = headers.getBytes();
                socket.getOutputStream().write(bytes);
                socket.close();
                return;
            }

            if (bodyProcessor.getPath().equals("/favicon.ico")) {
                // this too
                String headers = "HTTP/1.1 204 No Content\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "Access-Control-Allow-Methods: POST, GET, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type\r\n" +
                        "\r\n";
                byte[] bytes = headers.getBytes();
                socket.getOutputStream().write(bytes);
                socket.close();
                return;
            }
            Route route = driver.findRoute(
                    new Route(bodyProcessor.getCoreData().get("method"), bodyProcessor.getCoreData().get("path")));

            route.executeRoute(socket.getOutputStream(), bodyProcessor);

            socket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
