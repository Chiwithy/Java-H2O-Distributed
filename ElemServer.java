import java.net.*;
import java.util.*;

public class ElemServer implements Runnable {
    private Socket clientSocket;
    private int element = 0;
    private ArrayList<String> ackMsgs = new ArrayList<> ();
    private final Object ackLock = new Object ();

    public ElemServer (Socket clientSocket, int element) {
        try {
            this.clientSocket = clientSocket;
            this.element = element;
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    public void close () {
        try {
            this.clientSocket.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }
    }

    @Override
    public void run () {
        ElemServerListener elemListener = new ElemServerListener (clientSocket, element, ackMsgs, ackLock);
        ElemServerWriter elemWriter = new ElemServerWriter (clientSocket, ackMsgs, ackLock);
        Thread elemListenerThread = new Thread (elemListener);
        Thread elemWriterThread = new Thread (elemWriter);

        elemListenerThread.start ();
        elemWriterThread.start ();

        try {
            elemListenerThread.join ();
            elemWriterThread.join ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());;
        }

        close ();
    }
}