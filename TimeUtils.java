import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static String getNowString () {
        LocalDateTime currentDateTime = LocalDateTime.now ();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss");
        String formattedDate = currentDateTime.format (formatter) + ": ";

        return formattedDate;
    }

    public static void printTimeMsg (String msg) {
        System.out.println (TimeUtils.getNowString () + msg + ".");
    }
}



