package message;

public class Message {
    public static String getMessageString(String cmd, String msg) {
        return "<code>" + cmd + "</code>" + "<message>" + msg + "</message>";
    }
}
