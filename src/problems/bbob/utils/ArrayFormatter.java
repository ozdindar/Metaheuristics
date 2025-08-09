package problems.bbob.utils;

import java.util.Locale;

public class ArrayFormatter {

    /**
     * Converts a double array to a formatted string with a specified number of decimal places.
     *
     * @param array the input double array
     * @param decimals the number of decimal places to format
     * @return a string representation of the array with formatted decimal values
     */
    public static String formatDoubleArray(double[] array, int decimals) {
        if (decimals < 0) throw new IllegalArgumentException("Decimal places must be non-negative.");

        String formatString = "%." + decimals + "f";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(String.format(Locale.US, formatString, array[i]));
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        double[] values = {1.23456, 2.34567, 3.45678};
        System.out.println(formatDoubleArray(values, 2));  // Output: [1.23, 2.35, 3.46]
        System.out.println(formatDoubleArray(values, 3));  // Output: [1.235, 2.346, 3.457]
    }
}
