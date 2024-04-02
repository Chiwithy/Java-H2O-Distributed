import java.net.*;
import java.io.*;
import java.util.*;

public class ElemServerWriter implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private boolean isDone = false;
    private int nextPrintIndex = 0;
    private static final Object printLock = new Object ();
    private ArrayList<String> ackMsgs;
    private Object ackLock;
    private boolean ackReceived = false;

    public ElemServerWriter (Socket clientSocket, ArrayList<String> ackMsgs, Object ackLock) {
        try {
            this.clientSocket = clientSocket;
            out = new PrintWriter (this.clientSocket.getOutputStream (), true);
            this.ackMsgs = ackMsgs;
            this.ackLock = ackLock;
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());
        }
    }

    public void sendNewBond () {
        synchronized (printLock) {
            try {
                if (ServerUtils.hasNewBond (nextPrintIndex)) {
                    String bondMsg = ServerUtils.getBondAt (nextPrintIndex);
                while (!ackReceived) {
                    out.println (bondMsg);
                    synchronized  (ackLock) {
                        if (ackMsgs.contains (bondMsg)) {
                            ackMsgs.removeIf (msg -> msg.equals (bondMsg));
                            ackReceived = true;
                        }
                    }
                }

                    ackReceived = false;
                    nextPrintIndex++;
                    // TimeUtils.printTimeMsg ("Sent " + bondMsg + " to " + clientSocket.getInetAddress () + " at port: " + clientSocket.getPort ());

                    if (bondMsg.equals ("fin"))
                        isDone = true;
                }
            } catch (Exception e) {
                TimeUtils.printTimeMsg (e.toString ());;
            }
        }
    }

    public void close () {
        try {
            out.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    @Override
    public void run () {
        while (!isDone) {
            sendNewBond ();
        }

        close ();
    }
}
