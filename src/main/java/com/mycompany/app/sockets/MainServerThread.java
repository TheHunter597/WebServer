package com.mycompany.app.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.mycompany.app.Config.ConfigurationManager;
import com.mycompany.app.Handlers.HandlerThread;
import com.mycompany.app.Handlers.HttpDriver;

public class MainServerThread {

    ServerSocket serverSocket;
    ConfigurationManager manager;
    ExecutorService exectuor;
    HttpDriver driver;
    Server server;

    public MainServerThread(ConfigurationManager manager, ExecutorService executor, HttpDriver driver, Server server)
            throws IOException {
        this.manager = manager;
        this.exectuor = executor;
        this.serverSocket = new ServerSocket(manager.getConfig().getPort());
        this.driver = driver;
        this.server = server;
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            socket.setSoTimeout(20);
            exectuor.submit(new HandlerThread(socket, driver, server));
        }
    }

}
