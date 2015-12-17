package com.github.p4535992.util.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 14/07/2015.
 * @author 4535992,
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class ExceptionKit extends  RuntimeException{



    private static final long serialVersionUID = 3L;
    public ExceptionKit() {}

    public ExceptionKit(String message) {
        super(message);
    }

    public ExceptionKit(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionKit(Throwable e) {
        super(e);
    }

    public ExceptionKit(Exception e){ super(new Throwable(e));}


    private static void printStackTrace(Exception e){
        StackTraceElement elements[] = e.getStackTrace();
        for (StackTraceElement element : elements) {
            System.err.println(element.getFileName()
                    + ":" + element.getLineNumber()
                    + ">> "
                    + element.getMethodName() + "()");
        }
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintStream s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }

    /**
     * Overridden so we can print the enclosed exception's stacktrace too.
     */
    public void printStackTrace(java.io.PrintWriter s) {
        s.flush();
        super.printStackTrace(s);
        Throwable cause = getCause();
        if (cause != null) {
            s.print("Caused by:\n");
            cause.printStackTrace(s);
        }
    }

    /**
     * Method to generate a Out of Memory Exception.
     */
    public static void generateOutOfMemory(){
        //System.out.println("Initial freeMemory: " + Runtime.getRuntime().freeMemory() / (1024 * 1024));
        List<String> myList = new ArrayList<>();
        for(long i=0; ; i++) {
            myList.add(i + " - " + System.currentTimeMillis());
            if (i%100000==0) {
                System.gc();
                System.out.println("i: "+i+", freeMemory: "+Runtime.getRuntime().freeMemory()/(1024*1024));
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //Exception managed
    //------------------------------------------------------------------------------------------------------------------
    /**
     * Handle the given reflection exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with an appropriate message else.
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Handle the given invocation target exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such a root
     * cause. Throws an IllegalStateException else.
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>Rethrows the underlying exception cast to an {@link RuntimeException} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */
    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the
     * target method.
     * <p>Rethrows the underlying exception cast to an {@link Exception} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     * @param ex the exception to rethrow
     * @throws Exception the rethrown exception (in case of a checked exception)
     */
    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Determine whether the given method explicitly declares the given
     * exception or one of its superclasses, which means that an exception of
     * that type can be propagated as-is within a reflective invocation.
     * @param method the declaring method
     * @param exceptionType the exception to throw
     * @return {@code true} if the exception can be thrown as-is;
     * {@code false} if it needs to be wrapped
     */
    public static boolean declaresException(Method method, Class<?> exceptionType) {
        if(method==null) return false;
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtains the entire stracktrace of an exception and converts it into a
     * string.
     *
     * @param exception the exception whose stacktrace has to be converted
     * @return the stracktrace, converted into a string
     * @since 1.0
     */
    public static String getExceptionStackTrace(Throwable exception) {
        if (null == exception)  throw new IllegalArgumentException("exception can't be null;");
        String stack_trace;
        StringWriter string_writer = new StringWriter();
        PrintWriter print_writer = new PrintWriter(string_writer);
        exception.printStackTrace(print_writer);
        stack_trace = string_writer.getBuffer().toString();
        print_writer.close();
        try {
            string_writer.close();}
        // JDK 1.2.2 compatibility
        catch (Throwable ignored) {}

        return stack_trace;
    }

      /*public static void logStackTrace(Exception e, org.slf4j.Logger logger) {
        logger.debug(e.getMessage());
        for (StackTraceElement stackTrace : e.getStackTrace()) {
            logger.error(stackTrace.toString());
        }
    }

    public static void logException(Exception e, org.slf4j.Logger logger) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error(sw.toString());
    }*/


}
