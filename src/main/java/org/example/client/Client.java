package org.example.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client{
    private Socket socket;
    public static void main(String[] args){
        Client klient = new Client();
        klient.polaczzserwerem("localhost", 6767);

    }

    public void polaczzserwerem(String host, int port){
        try{
            System.out.println("probojemy polaczyc sie z serwerem " + host + " : " + port);
            socket=new Socket(host, port);
            System.out.println("polaczono z serwerem :)");
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Serwer: " + msg);
            }

            System.out.println("Serwer zamknal polaczenie.");

        } catch (IOException e){
            System.out.println("blad poloczenia: " + e.getMessage());
    
    }
}