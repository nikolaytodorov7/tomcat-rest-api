package util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.*;

public class FileLogger {
    private static final Logger logger = Logger.getLogger("Logger");
    private static FileHandler fh = null;

    static {
        try {
            fh = new FileHandler("log.log", true);
        } catch (IOException ignored) { // FH path can't be wrong
        }
    }

    public static void log(Level level, String msg) {
        fh.setFormatter(createFormatter(msg));
        logger.addHandler(fh);
        if (level.equals(Level.INFO))
            logger.log(Level.INFO, msg);
        else if (level.equals(Level.WARNING))
            logger.log(Level.WARNING, msg);
    }

    private static Formatter createFormatter(String msg) {
        return new Formatter() {
            public String format(LogRecord record) {
                return String.format("%s %s %s\n", LocalDateTime.now(), record.getLevel().toString(), msg);
            }
        };
    }
}
