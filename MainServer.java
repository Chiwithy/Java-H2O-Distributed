import java.net.*;
import java.io.*;

public class MainServer implements Runnable {
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean hConnected = false;
    private boolean oConnected = false;
    private Thread hThread, oThread;

    public MainServer (int port) {
        try {
            serverSocket = new ServerSocket (port);
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void stop () {
        try {
            in.close ();
            out.close ();
            serverSocket.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    @Override
    public void run () {
        long timeStart = 0;
        long timeFinish, timeElapsed;
        
        try {
            while (!hConnected || !oConnected) {
                Socket clientSocket = serverSocket.accept ();
                PrintWriter out = new PrintWriter (clientSocket.getOutputStream (), true);
                BufferedReader in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream ()));
                String connectionType = in.readLine ();
                connectionType = connectionType.toLowerCase ();

                if (connectionType.equals ("hydrogen")) {
                    out.println ("hydrogen");
                    ElemServer hMan = new ElemServer (clientSocket, 0);
                    hThread = new Thread (hMan);
                    hThread.start ();

                    TimeUtils.printTimeMsg ("Hydrogen client connected");
                    hConnected = true;
                }
                else if (connectionType.equals ("oxygen")) {
                    out.println ("oxygen");
                    ElemServer oMan = new ElemServer (clientSocket, 1);
                    oThread = new Thread (oMan);
                    oThread.start ();

                    TimeUtils.printTimeMsg ("Oxygen client connected");
                    oConnected = true;
                }
                else {
                    System.out.println (TimeUtils.getNowString () + "Unknown client attempted to connect");
                }
            }

            while (!ServerUtils.isMainDone ()) {
                if (ServerUtils.canBond ())
                    ServerUtils.bond ();
            }

            hThread.join ();
            oThread.join ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }

        timeStart = ServerUtils.getTimeStart ();
        timeFinish = System.nanoTime ();
        timeElapsed = timeFinish - timeStart;
        timeElapsed /= 1000000;

        System.out.println ("\nElapsed time (ms): " + timeElapsed);
    }

    public static void main (String[] args) {
        MainServer server = new MainServer (6666);
        Thread serverThread = new Thread (server);
        serverThread.start ();

        try {
            serverThread.join ();
            ServerUtils.removeTerminateIds ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }

        TimeUtils.printTimeMsg ("All elements have been received and bonded. Remaining Hydrogen: " + ServerUtils.getHCount () + " | Remaining Oxygen: " + ServerUtils.getOCount ());
    }
}