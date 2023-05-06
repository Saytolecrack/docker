import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    public static ArrayList<ClientThread> clients;

    public ChatServer() {
        clients = new ArrayList<ClientThread>();
    }

    public void start() {
        ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(9000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 9000.");
            System.exit(-1);
        }
        


        System.out.println("Serveur lance en attente de connexion...");
        

        while (listening) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Erreur lors de l'acceptation de la connexion.");
                System.exit(-1);
            }

            System.out.println("Client connecte: " + clientSocket.getInetAddress().getHostName());

            ClientThread clientThread = new ClientThread(clientSocket, this);
            clients.add(clientThread);
            clientThread.start();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message) {
        for (ClientThread client : clients) {
            client.sendMessage(message);
        }
    }

    public static void executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("La commande s'est terminée avec le code de sortie : " + exitCode);
    }
    

    public static void main(String[] args) {
        try {
            String command = "ipconfig";
            executeCommand(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        new ChatServer().start();
    }
}

class ClientThread extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private ChatServer server;

    public ClientThread(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    public void run() {
        
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Client says: " + inputLine);
                System.out.println(server.clients.size());
                server.broadcast(inputLine);
            }

            System.out.println("Client deconnecte.");
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }

}

