package util;

public class NumberParser {
    public static int parseInt(String str) {
        try {
            int num = Integer.parseInt(str);
            return num > 0 ? num : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
