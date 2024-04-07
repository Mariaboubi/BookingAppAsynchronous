package org.aueb.util;

public class DateProcessing {

    public static int[] formatDate(String[] date){
        int[] firstDate = new int[date.length];
        for (int i = 0; i < date.length; i++) {
            firstDate[i] = Integer.parseInt(date[i]);
        }

        return firstDate;


    }
    public static boolean compareDates(int[] firstDate,int[] lastDate){
        if (firstDate[0] >= lastDate[0]) {
            if (firstDate[1] >= lastDate[1]) {
                if (firstDate[2] >= lastDate[2]) {
                    return false;
                }
            }
        }
        return true;
    }
}
