import java.net.*;
import java.io.*;
import java.util.*;

public class ElemServerListener implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private int element = 0;
    private boolean isDone = false; 
    private ArrayList<String> ackMsgs;
    private Object ackLock;

    public ElemServerListener (Socket clientSocket, int element, ArrayList<String> ackMsgs, Object ackLock) {
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader (new InputStreamReader (this.clientSocket.getInputStream ()));
            this.element = element;
            this.ackMsgs = ackMsgs;
            this.ackLock = ackLock;
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void getNewE () {
        try {
            String newE = in.readLine ();
            if (newE.contains (",") || newE.equals ("fin")) {
                synchronized (ackLock) {
                    ackMsgs.add (newE);
                }
            }
            else {
                int eId = Integer.parseInt (newE);
                ServerUtils.addE (eId, element);
            }

                if (newE.equals ("fin"))
                    isDone = true;
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void close () {
        try {
            in.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    @Override
    public void run () {
        while (!isDone) {
            getNewE ();
        }

        close ();
    }
}
