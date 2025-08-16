package airhacks.ebank.logging.control;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

public record EBLog(Logger systemLogger) {

    public void info(String message){
        systemLogger.log(Level.INFO, message);
    }

    public void error(String message){
        systemLogger.log(Level.ERROR, message);
    }

    public void error(String message, Throwable exception){
        systemLogger.log(Level.ERROR, message, exception);
    }
}
