package com.github.p4535992.util.string;

/**
 * Created by 4535992 on 31/12/2015.
 * @author 4535992.
 * WARNING: This is just a local copy of the Timer of the
 *          JENA Project i don't own any right on this.
 */
@SuppressWarnings("unused")
public class Timer
{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Timer.class);

    protected long timeFinish = -1 ;
    protected boolean inTimer = false ;
    protected long timeStart  = 0 ;

    public Timer() { }

    public void startTimer() throws Exception {
        if ( inTimer )
            throw new Exception("Already in timer") ;

        timeStart = System.currentTimeMillis() ;
        timeFinish = -1 ;
        inTimer = true ;
    }

    /** 
     * Return time in millisecods.
     * @return the Long value of the Timer when stopped.
     * @throws java.lang.Exception throw if any error is occurred.
     */
    public long endTimer() throws Exception {
        if ( ! inTimer )
            throw new Exception("Not in timer") ;
        timeFinish = System.currentTimeMillis() ;
        inTimer = false ;
        return getTimeInterval() ;
    }

    public long readTimer() throws Exception {
        if ( ! inTimer )
            throw new Exception("Not in timer") ;
        return System.currentTimeMillis()-timeStart  ;
    }

    public long getTimeInterval() throws Exception {
        if ( inTimer )
            throw new Exception("Still timing") ;
        if ( timeFinish == -1 )
            throw new Exception("No valid interval") ;

        return  timeFinish-timeStart ;
    }

    static public String timeStr(long timeInterval)
    {
//        DecimalFormat f = new DecimalFormat("#0.###") ;
//        String s = f.format(timeInterval/1000.0) ;
//        return s ;
        //Java5
        return String.format("%.3f", timeInterval/1000.0) ;
    }

    protected String timeStr(long timePoint, long startTimePoint)
    {
        return timeStr(timePoint-startTimePoint) ;
    }
}
