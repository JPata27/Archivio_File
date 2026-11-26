package dpdns.org.lake;

//Input & Output
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
//FileReading
import java.nio.file.Files;
import java.nio.file.Path;


public class FileSender implements Runnable {

    private final String PARENT_FOLDER = "documenti";
    private Socket socket;


    public FileSender(Socket socket) {
        this.socket = socket;
    }

    
    
    @Override
    public void run() {
        String clientID =  Server.getInfoClientSocket(socket);
        System.out.printf("[%s] Sessione Aperta\n", clientID);



        try (
                Socket s = this.socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
        ) {
            String msg;
            if((msg = in.readLine()) == null) return;



            String req = msg.trim();
            

            String[] data = req.split("\\s+");


            //more or less params instead of 2
            if(data.length != 2){
                out.println("ERR SYNTAX");
                return;
            }

            String command = data[0];
            String fileName = data[1];



            if(!command.equals("LEGGI")){
                out.println("ERR UNKNOWNCMD");
                return;
            }


            Path folder = null;
            Path file = null;

            try{
                folder = Path.of(PARENT_FOLDER).toRealPath();
                file = folder.resolve(fileName).normalize();
            }
            //parent_folder for admin
            catch(IOException ioe){
                out.println("ERR PARENTFOLDER");
                return;
            }



            //don't check if is null bc -> return if PARENT_FOLDER is 404



            //if PARENT_FOLDER == "documenti" && is a file => OK
            if (!file.getParent().equals(folder) || !Files.isRegularFile(file)) {
                out.println("ERR NOTFOUND");
                return;
            }


            //Trasferiamo solo testo, non immagini, PDF o altri file binari. => (error code NON nella traccia)
            if(!fileName.endsWith(".txt")){
                out.println("ERR INVALIDXT");
                return;
            }
            

            //File Reading
            try{
                out.println(String.format("OK FILE\n%s", Files.readString(file, StandardCharsets.UTF_8)));
            }

            //No Permissions
            catch(AccessDeniedException ade){
                out.println("ERR READ");
            }

            System.out.printf("[%s] Sessione Chiusa\n", clientID);

        } catch (Exception e) {}
    }
}