import java.io.*;

public class ClientWriter implements Runnable {
    private PrintWriter out;
    private int reqCount = 0;
    private String element = "X";

    public ClientWriter (PrintWriter out, int reqCount, int element) {
        try {
            this.out = out;
            this.reqCount = reqCount;
            this.element = (element == 0) ? "Hydrogen" : "Oxygen";
            } catch (Exception e) {
                TimeUtils.printTimeMsg (e.toString ());
            }
    }

    @Override
    public void run () {
        try {
            int i;

            for (i = 1; i <= reqCount; i++) {
                out.println ("" + i);
                TimeUtils.printTimeMsg ("Sent bond request for " + element.charAt (0) + i);
            }

            out.println ("-1");
            TimeUtils.printTimeMsg ("All bond requests for " + element + " have been sent");

        } catch (Exception e) {
            TimeUtils.printTimeMsg (e.toString ());
        }
    }
}
