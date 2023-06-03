package sever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :Dilshan
 **/
public class SeverRun {
   private ServerSocket serverSocket;
   private ServerSocket serverSocketImage;

    public SeverRun(ServerSocket serverSocket,ServerSocket serverSocketImage) {
        this.serverSocket = serverSocket;
        this.serverSocketImage=serverSocketImage;
    }

    public void startSever() {
        while (!serverSocket.isClosed()){
            try {
                new Thread(new ClientManager(serverSocket.accept(),serverSocketImage.accept())).start();
            } catch (IOException e) {
                closeSever();
                e.printStackTrace();
            }
        }
    }
    public void closeSever() {
        if (serverSocket!=null) {
            try {
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        final int PORT = 40000;
        final int PORT_2 = 40001;
        SeverRun severRun=new SeverRun(new ServerSocket(PORT),new ServerSocket(PORT_2));
        System.out.println("sever is Start..");
        severRun.startSever();

    }
}
