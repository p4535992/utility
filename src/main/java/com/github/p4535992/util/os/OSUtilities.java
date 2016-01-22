package com.github.p4535992.util.os;

/**
 * Created by tenti on 18/01/2016.
 */
public class OSUtilities {
    private static OSUtilities ourInstance = new OSUtilities();

    public static OSUtilities getInstance() {
        return ourInstance;
    }

    private OSUtilities() {
    }

    private static String OS = null;

    public static String getOsName() {
        if(OS == null) { OS = System.getProperty("os.name"); }
        //System.out.println("OS:" + OS);
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }


}
