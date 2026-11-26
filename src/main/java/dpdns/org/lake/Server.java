package dpdns.org.lake;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {

        // port number
        int porta = 3000;
        String homeString = String.format("Server in ascolto sulla porta %d...\n", porta);
        

       // Creazione Socket sulla porta
        try (ServerSocket serverSocket = new ServerSocket(porta)) {

            System.out.print(homeString);

            while (true) {
                Socket socket = serverSocket.accept();
                
                new FileSender(socket).run();
                //new Thread(() -> File(socket)).start();
            }

        } catch (Exception e) {
            System.out.println("[ERRORE SERVER] " + e.getMessage());
        }

    }

    public static String getInfoClientSocket(Socket s) {
        return String.format("%s:%d", s.getInetAddress().getHostAddress(), s.getPort());
    }
}