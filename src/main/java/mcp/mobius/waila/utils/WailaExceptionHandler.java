package mcp.mobius.waila.utils;

import mcp.mobius.waila.Waila;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class WailaExceptionHandler {

    public WailaExceptionHandler() {
    }

    private static ArrayList<String> errs = new ArrayList<String>();

    public static List<String> handleErr(final Throwable e, final String className, final List<String> currenttip) {
        if (!errs.contains(className)) {
            errs.add(className);

            for (final StackTraceElement elem : e.getStackTrace()) {
                Waila.log.log(Level.WARN, String.format("%s.%s:%s", elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
                if (elem.getClassName().contains("waila")) break;
            }

            Waila.log.log(Level.WARN, String.format("Catched unhandled exception : [%s] %s", className, e));
        }
        if (currenttip != null)
            currenttip.add("<ERROR>");

        return currenttip;
    }

}
