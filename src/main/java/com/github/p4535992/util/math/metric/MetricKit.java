package com.github.p4535992.util.math.metric;

/**
 * Created by 4535992 on 06/05/2015.
 * @author 4535992.
 * @version 2015-07-13.
 */
@SuppressWarnings("unused")
public class MetricKit {

    /**
     * The calculateKilometers method displays the kilometers that are equivalent to
     * a specified number of meters.
     *
     * @param meters the number of meters.
     * @return The number of kilometers.
     */
    static double convertMeterToKilometers(double meters) {
        return meters * 0.001;
    }

    /**
     * This method should calculate inches that are equivalent to a specified
     * number of meters.
     *
     * @param meters the number of meters.
     * @return the number of inches.
     */
    static double convertMetersToInches(double meters) {
        return meters * 39.37;
    }

    /**
     * This method should calculate the feet that are equivalent to a specified
     * number of meters.
     *
     * @param meters the number of meters.
     * @return The number of feet.
     */
    static double convertMetersToFeet(double meters) {
        return meters * 3.281;
    }


    /**
     * Method to convert Miles to Kilometers.
     * @param miles the number of miles.
     * @return The number of Kilometers.
     */
    static double convertMilesToKilometers(double miles) {
        return miles * 1.609344;
    }

    /**
     * Method to convert to Miles to NauticMiles.
     * @param miles the number of miles.
     * @return The number of NauticMiles.
     */
    static double convertMilesToNauticMiles(double miles) {
        return miles * 0.8684;
    }

    /**
     * Method to convert to KiloMeters to Miles.
     * @param kilometers the number of kilometers.
     * @return The number of Miles.
     */
    static double convertKilometersToMiles(double kilometers) {
        return kilometers / 1.609344;
    }

    /**
     * Method to convert to KiloMeters to NauticMiles.
     * @param kilometers the number of kilometers.
     * @return The number of NauticMiles.
     */
    static double convertKilometersToNauticMiles(double kilometers) {
        return convertKilometersToMiles(kilometers) / 0.8684;
    }

}
