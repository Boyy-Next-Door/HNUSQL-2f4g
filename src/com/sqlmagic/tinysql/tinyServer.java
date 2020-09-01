package com.sqlmagic.tinysql;

import java.net.ServerSocket;

public class tinyServer {
    public final static int PORT=6666;
    private ServerSocket serverSocket;

    public void start()throws Exception{
        serverSocket=new ServerSocket(PORT);
        while(true){
            new clientHandler(serverSocket.accept()).start();
        }
    }

    public void stop()throws Exception{
        serverSocket.close();
    }

    public static void main(String[] args) throws Exception {
        tinyServer server=new tinyServer();
        server.start();
        server.stop();
    }

}


