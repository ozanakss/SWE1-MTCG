
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Processes a request
public class Request {

    private final PrintStream output;
    private final String       CmdHttpRequest;
    private final String       UrlAdresse;
    private final String payload;
    private final String authorizationString;


    public Request(Socket socket) throws IOException {
        StringBuilder reqsBuilder = new StringBuilder();
        this.output = new PrintStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String lines = bufferedReader.readLine();
        while (!lines.isBlank()) {
            reqsBuilder.append(lines).append("\r\n");
            lines = bufferedReader.readLine();
            System.out.println(lines);
        }
        String request = reqsBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        //code to read the post payload data
        StringBuilder payload = new StringBuilder();
        while(bufferedReader.ready()){
            payload.append((char) bufferedReader.read());
        }
        System.out.println("Payload: " + payload.toString());
        this.payload = payload.toString();

        this.UrlAdresse = path;
        this.CmdHttpRequest = method;

        List<String> headers = new ArrayList<>(Arrays.asList(requestsLines).subList(2, requestsLines.length));



        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                socket.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);
        if(headers.toString().contains("Authorization: Basic")) {
            int authIndex = headers.toString().indexOf("Authorization: Basic");
            String authorizationString = headers.toString().substring(authIndex + 21);
            int authIndexEnd = authorizationString.indexOf(',');
            if(authIndexEnd == -1){
                authIndexEnd = authorizationString.indexOf(']');
            }
            authorizationString = authorizationString.substring(0, authIndexEnd);

            this.authorizationString = authorizationString;
        }else{
            this.authorizationString = null;
        }
    }
 // GETTER UND SETTER METHODEN****************************

    public String getauthorizationString() {
        return authorizationString;
    }


    public PrintStream getOutput() {
        return this.output;
    }


    public String getCmdHttpRequest() {
        return this.CmdHttpRequest;
    }


    public String getUrlAdresse() {
        return this.UrlAdresse;
    }


    public String getPayload() {
        return this.payload;
    }

}
