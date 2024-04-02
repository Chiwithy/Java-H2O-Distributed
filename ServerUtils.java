import java.util.*;

public class ServerUtils {
    private static final Object hLock = new Object ();
    private static final Object oLock = new Object ();
    private static final Object completedLock = new Object ();
    private static ArrayList<Integer> hs = new ArrayList<> ();
    private static ArrayList<Integer> os = new ArrayList<> ();
    private static ArrayList<String> completedBonds = new ArrayList<> ();
    private static boolean mainDone = false;

    public static void bond () {
        int h1, h2, o1;
        h1 = h2 = o1 = 0;

        synchronized (hLock) {
            h1 = hs.get (0);
            hs.remove (0);
            h2 = hs.get (0);
            hs.remove (0);
        }
        synchronized (oLock) {
            o1 = os.get (0);
            os.remove (0);
        }

        TimeUtils.printTimeMsg ("H" + h1 + " and H" + h2 + " bonded with O" + o1 + "");

        addBondedString (h1, h2, o1);
    }

    public static void addE (int eId, int element) {
        if (element == 0) {
            ServerUtils.addH (eId);

            if (eId == -1)
                TimeUtils.printTimeMsg("All Hydrogen bond requests have been received");
            else
                TimeUtils.printTimeMsg("Received bond request for H" + eId);
        }
        else if (element == 1) {
            ServerUtils.addO (eId);

            if (eId == -1)
                TimeUtils.printTimeMsg("All Oxygen bond requests have been received");
            else
                TimeUtils.printTimeMsg("Received bond request for O" + eId);
        }
    }

    private static void addH (int hId) {
        synchronized (hLock) { hs.add (hId); }
    }

    private static void addO (int oId) {
        synchronized (oLock) { os.add (oId); }
    }

    public static boolean hasNewBond (int printIndex) {
        synchronized (completedLock) {
            if (printIndex < completedBonds.size ())
                return true;
            else return false;
        }
    }

    public static void removeTerminateIds () {
        synchronized (hLock) { hs.remove (Integer.valueOf (-1)); }
        synchronized (oLock) { os.remove (Integer.valueOf (-1)); }
    }

    public static String getBondAt (int index) {
        synchronized (completedLock) {
            return completedBonds.get (index);
        }
    }

    public static int getHCount () {
        synchronized (hLock) { return hs.size (); }
    }

    public static int getOCount () {
        synchronized (oLock) { return os.size (); }
    }

    public static boolean canBond () {
        synchronized (hLock) {
            synchronized (oLock) {
                checkIfDone ();

                return ( hs.size () >= 2 && hs.get (0) != -1 && hs.get (1) != -1 && os.size () >= 1 && os.get (0) != -1 );
            }
        }
    }

    public static boolean isMainDone () {
            return mainDone;
    }

    private static void checkIfDone () {
        if ((hs.size () > 0 && hs.size () <= 2 && hs.contains (-1)) || (os.size () == 1 && os.get (0) == -1)) {
                mainDone = true;
                addBondedString ("fin");
        }
    }

    private static void addBondedString (int h1, int h2, int o1) {
        String bondedString = "" + h1 + "," + h2 + "," + o1;
        synchronized (completedLock) {
            completedBonds.add (bondedString);
        }
    }

    private static void addBondedString (String finMsg) {
        synchronized (completedLock) {
            completedBonds.add (finMsg);
        }
    }
}
