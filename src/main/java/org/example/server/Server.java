package org.example.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket gracz1;
    private Socket gracz2;

    public static void main(String[] args){
        Server server = new Server();
        server.start(6767);

    }

    public void start(int port){
        System.out.println("uruchamiam serwer na porcie " + port);
        try(ServerSocket serverSocket=new ServerSocket(port)){
            System.out.println("serwer działa! oczekiwanie na graczy...");

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("nowe połączenie: " + clientSocket.getInetAddress());

                if(gracz1==null){
                    gracz1=clientSocket;
                    System.out.println("gracz 1 (czarny) połączony :)");

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("POŁĄCZONO");

                } else if(gracz2 == null){
                    gracz2 = clientSocket;
                    System.out.println("gracz 2 (biały) połączony :)");
                    System.out.println("dwóch graczy połączonych - zaczynamy gre! powodzenia!!");

                    ClientHandler handler1 = new ClientHandler(gracz1);
                    ClientHandler handler2 = new ClientHandler(gracz2);
                    new Thread(handler1).start();
                    new Thread(handler2).start();

                    handler1.wyslij("START CZARNY");
                    handler2.wyslij("START BIALY");
                    break;
                }else{
                    System.out.println("niestety ten serwer jeest pełny :( odrzucam połączenie...");
                    PrintWriter tempOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    tempOut.println("SERVER_PELNY");
                    clientSocket.close();
                }

            }
            System.out.println("Serwer działa dalej...");

            while (true) {
                Thread.sleep(1000);
            }

            
        }
        catch (IOException | InterruptedException e){
                System.out.println("błąd serwera: " + e.getMessage());
            }
        }
}
