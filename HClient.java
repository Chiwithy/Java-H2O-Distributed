import java.net.*;
import java.io.*;

public class HClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection (String ip, int port) {
        try {
            clientSocket = new Socket (ip, port);
            out = new PrintWriter (clientSocket.getOutputStream (), true);
            in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));

            sendMessage ("hydrogen");
            String response = in.readLine (), clientResponse = "";
            response = response.toLowerCase ();

            if (response.equals ("hydrogen"))
                clientResponse = "Successfully connected to bonding Server";
            else
                clientResponse = "Unknown Server response";

            TimeUtils.printTimeMsg (clientResponse);
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void sendMessage (String msg) {
        try {
            out.println (msg);
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void stopConnection () {
        try {
            in.close ();
            out.close ();
            clientSocket.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public static void main (String args[]) {
        HClient client = new HClient ();
        client.startConnection( "127.0.0.1", 6666);
    }
}
