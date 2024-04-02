import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int element;
    private double dPower;
    private int reqCounts = 2;

    public Client (int element, int nPower) {
        this.element = element;
        dPower = (double) nPower;

        reqCounts = (int) (Math.pow (2.00, dPower));
        if (element == 0)
            reqCounts *= 2;
    }

    public boolean startConnection (String ip, int port) {
        boolean success = false;
        try {
            clientSocket = new Socket (ip, port);
            out = new PrintWriter (clientSocket.getOutputStream (), true);
            in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));

            out.println (this.getElementType ());

            String response = in.readLine ();
            response = response.toLowerCase ();

            success = (response.equals (this.getElementType ().toLowerCase ()));
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());
        }

        return success;
    }

    public void beginProcessing () {
        long timeStart = 0;
        long timeFinish, timeElapsed;

        try {
            ClientListener cListen = new ClientListener (in, out);
            ClientWriter cWriter = new ClientWriter (out, reqCounts, element);
            Thread cListenThread = new Thread (cListen);
            Thread cWriterThread = new Thread (cWriter);
        
            timeStart = System.nanoTime ();

            cListenThread.start ();
            cWriterThread.start ();

            cListenThread.join ();
            cWriterThread.join ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }

        timeFinish = System.nanoTime ();
        timeElapsed = timeFinish - timeStart;
        timeElapsed /= 1000000;

        System.out.println ("\nElapsed time (ms): " + timeElapsed);
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

    public int getReqCount () {
        return reqCounts;
    }

    public String getElementType () {
        return (element == 0 ? "Hydrogen" : "Oxygen");
    }

    public static void main (String args[]) {
        Scanner scanny = new Scanner(System.in);

        System.out.println ("Element choices: ");
        System.out.println ("  0 - Hydrogen");
        System.out.println ("  1 - Oxygen");
        System.out.print ("Element of client: ");
        int element = Integer.parseInt (scanny.nextLine ());

        System.out.println ("");
        System.out.println ("Note: Hydrogen clients automatically double the value (N is 2M)");
        System.out.print ("Number of elements to send (power of 2): ");
        int nPower = Integer.parseInt (scanny.nextLine ());

        Client client = new Client (element, nPower);
        TimeUtils.printTimeMsg ("Successfully created client of type: " + client.getElementType ());
        System.out.println ("which will send requests: " + client.getReqCount ());
        System.out.println ("");

        boolean success = client.startConnection( "192.168.1.5", 6666);

        if (success) {
            System.out.print (TimeUtils.getNowString () + "Successfully connected to bonding Server. Press enter to begin processing requests...");
            scanny.nextLine ();



            client.beginProcessing ();
            client.stopConnection ();
        }
        else
            TimeUtils.printTimeMsg ("Error occurred in trying to connect to server. Please try again");

        scanny.close ();
    }
}
