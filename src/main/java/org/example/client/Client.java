package org.example.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
    private Socket socket;
    private volatile boolean mojatura=false;
    public static void main(String[] args){
        Client klient = new Client();
        klient.polaczzserwerem("localhost", 6767);

    }

    public void polaczzserwerem(String host, int port){
        try{
            System.out.println("probójemy połączyc się z serwerem " + host + " : " + port);
            socket=new Socket(host, port);
            System.out.println("połączono z serwerem :)");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Thread odbior = new Thread(() -> {
                try{
                    String msg;

                    while ((msg = in.readLine()) != null) {
                        if (msg.equals("TWOJ_RUCH")) {
                            mojatura = true;
                            System.out.println("Twój ruch:");
                        }
                        else if (msg.equals("SERVER_PELNY")) {
                            System.out.println("Serwer jest pelny :( Rozłączono");
                            socket.close();
                            break;
                        } else if(msg.equals("PLANSZA")){
                            for (int i = 0; i < 20; i++) {
                                String liniaplanszy = in.readLine();
                                System.out.println(liniaplanszy);
                            }

                            System.out.println();
                        }

                    }
                }catch (IOException e){
                    System.out.println("rozłączono z serwerem");

                }
            });
            odbior.start();

            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            String linia;
            while ((linia = console.readLine()) != null) {
                if (mojatura) {
                    out.println(linia);
                    mojatura = false;
                } else {
                    System.out.println("czekaj na swoją kolej...");
                }

            }

        } catch (IOException e){
            System.out.println("błąd połączenia: " + e.getMessage());
        }

    }
}




