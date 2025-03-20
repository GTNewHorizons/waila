package mcp.mobius.waila.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;

public class WailaExceptionHandler {

    public WailaExceptionHandler() {}

    private static ArrayList<String> errs = new ArrayList<String>();

    public static List<String> handleErr(Throwable e, String className, List<String> currenttip) {
        if (!errs.contains(className)) {
            errs.add(className);

            Throwable working = e;

            while (working != null) {
                if (working != e) {
                    Waila.log.log(Level.WARN, String.format("Caused by: %s", working));
                }
                for (StackTraceElement elem : working.getStackTrace()) {
                    Waila.log.log(
                            Level.WARN,
                            String.format("%s.%s:%s", elem.getClassName(), elem.getMethodName(), elem.getLineNumber()));
                    if (working == e && elem.getClassName().contains("waila")) break;
                }

                working = working.getCause();
            }

            Waila.log.log(Level.WARN, String.format("Catched unhandled exception : [%s] %s", className, e));
        }
        if (currenttip != null) currenttip.add("<ERROR>");

        return currenttip;
    }

}
