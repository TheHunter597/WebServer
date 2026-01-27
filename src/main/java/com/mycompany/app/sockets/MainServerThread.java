package com.mycompany.app.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Handlers.HandlerThread;
import com.mycompany.app.Handlers.HttpDriver;

public class MainServerThread {
    ServerSocket server;
    ConfigurationManager manager;
    ExecutorService exectuor;
    HttpDriver driver;

    public MainServerThread(ConfigurationManager manager, ExecutorService executor, HttpDriver driver)
            throws IOException {
        this.manager = manager;
        this.exectuor = executor;
        this.server = new ServerSocket(manager.getConfig().getPort());
        this.driver = driver;
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = server.accept();
            socket.setSoTimeout(20);
            Future<?> future = exectuor.submit(new HandlerThread(socket, driver));

            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.err.println("Task failed: " + e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
