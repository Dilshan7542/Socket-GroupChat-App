package sever;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :Dilshan
 **/
public class ClientManager implements Runnable {
    private static List<ClientManager> clientManagerList=new ArrayList<>();
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStreamImage;
    private DataInputStream dataInputStreamImage;
    private Socket socket;
    private Socket socketImage;
    private  String clientName;


    public ClientManager(Socket socket,Socket socketImage) {
        try {
            this.socket=socket;
            this.socketImage=socketImage;
            this.dataInputStream=new DataInputStream(socket.getInputStream());
            this.dataOutputStream=new DataOutputStream(socket.getOutputStream());

            this.dataInputStreamImage=new DataInputStream(socketImage.getInputStream());
            this.dataOutputStreamImage=new DataOutputStream(socketImage.getOutputStream());

           clientName=dataInputStream.readUTF();
            setMassageToClient(this," Enter to Chat..!");
            clientManagerList.add(this);

        } catch (IOException e) {
            closeAllInstance(socket,dataInputStream,dataOutputStream,dataInputStreamImage,dataOutputStreamImage);
            e.printStackTrace();
        }
    }

    private void setMassageToClient(ClientManager client, String msg)  {
        for (ClientManager clientManager : clientManagerList) {
                if(client!=clientManager){
                    try {
                        clientManager.dataOutputStream.writeUTF(clientName+" "+msg);

                    } catch (IOException e) {
                        closeAllInstance(socket,dataInputStream,dataOutputStream,dataInputStreamImage,dataOutputStreamImage);
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void run() {
            sendMassage();
            sendImage();
    }

    private void sendImage() {
        new Thread(()->{
            while (socket.isConnected()){
                try {
                    final int readInt = dataInputStreamImage.readInt();
                    final byte[] bytes = new byte[readInt];
                    dataInputStreamImage.readFully(bytes,0,readInt);
                    sendImageToClient(this,readInt,bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    closeAllInstance(socket,dataInputStream,dataOutputStream,dataInputStreamImage,dataOutputStreamImage);
                    break;
                }
            }
        }).start();

    }

    private void sendImageToClient(ClientManager client, int readInt, byte[] bytes) {
        for (ClientManager clientManager : clientManagerList) {
            if(client !=clientManager){
                try {
                    clientManager.dataOutputStreamImage.writeInt(readInt);
                    clientManager.dataOutputStreamImage.write(bytes);
                    clientManager.dataOutputStreamImage.writeUTF(clientName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMassage()  {
        new Thread(()->{
        while (socket.isConnected()){
            try {
                setMassageToClient(this,dataInputStream.readUTF());
            } catch (IOException e) {
                closeAllInstance(socket,dataInputStream,dataOutputStream,dataInputStreamImage,dataOutputStreamImage);
                e.printStackTrace();
                break;
            }
        }

        }).start();
    }

    private void closeAllInstance(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream,DataInputStream dataInputStreamImage,DataOutputStream dataOutputStreamImage) {
            removeClient();
        try {
            if(dataInputStream!=null)dataInputStream.close();
            if(dataOutputStream!=null)dataOutputStream.close();
            if(dataInputStreamImage!=null)dataInputStreamImage.close();
            if(dataOutputStreamImage!=null)dataOutputStreamImage.close();
            if (socket!=null){
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient() {
        clientManagerList.remove(this);
        setMassageToClient(this," Left");
    }
}
