
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


 // Grundlegende Server logic

public class Main {
    static final int port = 10001;
    private Socket socket;


    public static void main(String[] args) {
        if (!new DatenBankVerbindung().init()) {
            System.err.println("DB init failed");
            System.exit(0);
        }
        System.out.println("\n\nServer wird jetzt gestartet ->>>> auf Port " + port + "\n\n");
        new Main(port);
    }


    public Main(int port){
        ServerSocket serSocket;
        try {
            serSocket = new ServerSocket(port);
            //noinspection InfiniteLoopStatement
            while (true){
                this.socket = serSocket.accept();
                requestResponding();
            }
        } catch (IOException e) {
            System.out.println("Exception!!!!!");
            e.printStackTrace();
        }
    }


    public void requestResponding(){
        try{
            System.out.println("wurde gestartet !");
            Request request = new Request(this.socket);
            new Response(request.getUrlAdresse(), request.getCmdHttpRequest(), request.getOutput(), request.getauthorizationString(), request.getPayload());
            this.socket.close();
            System.out.println("Socket was closed!");
            System.out.println("BYE!!");
        }catch (IOException e){
            System.out.println("Exception!!!!!");
            e.printStackTrace();

        }
    }
}
