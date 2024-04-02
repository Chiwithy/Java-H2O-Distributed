import java.io.*;
import java.util.*;

public class ClientListener implements Runnable {
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<String> receivedMsgs = new ArrayList<> ();

    public ClientListener (BufferedReader in, PrintWriter out) {
        try {
            this.in = in;
            this.out = out;
            receivedMsgs.add ("asd");
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());
        }
    }

    @Override
    public void run () {
        boolean isDone = false;

        while (!isDone) {
            try {
                String serverMsg = in.readLine ();
                if (serverMsg != null && !receivedMsgs.contains (serverMsg)) {
                    receivedMsgs.remove (0);
                    serverMsg = serverMsg.toLowerCase ();

                    if (serverMsg.equals ("fin")) {
                        isDone = true;
                        TimeUtils.printTimeMsg ("Server has finished processing all bond requests");
                    }
                    else {
                        serverMsg = serverMsg.toUpperCase ();
                        String elements[] = serverMsg.split (",");
                        String updateString = "H" + elements[0] + ", H" + elements[1] + ", and O" + elements[2] + " have successfully bonded";
                        
                        TimeUtils.printTimeMsg (updateString);
                    }

                out.println (serverMsg);
                receivedMsgs.add (serverMsg);
                }
            } catch (Exception e) {
                TimeUtils.printTimeMsg (e.toString ());
            }
        }

        try  {
            this.in.close ();
        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());
        }
    }
}