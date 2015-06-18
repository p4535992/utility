package com.github.p4535992.util.metric;

/**
 * Created by 4535992 on 06/05/2015.
 */
public class MetricKit {

    /**
     * The calculateKilometers method displays the kilometers that are equivalent to
     * a specified number of meters.
     *
     * @param meters
     * @return the number of kilometers
     */
    static double convertMeterToKilometers(double meters) {
        double kilometers = meters * 0.001;
        return kilometers;
    }

    /**
     * This method should calculate inches that are equivalent to a specified
     * number of meters.
     *
     * @param meters
     * @return the number of inches
     */
    static double convertMetersToInches(double meters) {
        double inches = meters * 39.37;
        return inches;
    }

    /**
     * This method should calculate the feet that are equivalent to a specified
     * number of meters.
     *
     * @param meters
     * @return The number of feet.
     */
    static double convertMetersToFeet(double meters) {
        double feet = meters * 3.281;
        return feet;
    }


    static double convertMilesToKilometers(double miles) {
        double kilometers = miles * 1.609344;
        return kilometers;
    }

    static double convertMilesToNauticMiles(double miles) {
        double nauticMiles = miles * 0.8684;
        return nauticMiles;
    }

    static double convertKilometersToMiles(double kilometers) {
        double miles = kilometers / 1.609344;
        return miles;
    }

    static double convertKilometersToNauticMiles(double kilometers) {
        double nauticMiles = convertKilometersToMiles(kilometers) / 0.8684;
        return nauticMiles;
    }

}
