package com.mobzheng.utils;

import org.joda.time.DateTime;

import java.util.Date;

public class DateUtil {


    public static Date timestampToDate(long timestamp) {
        DateTime dateTime = new DateTime(timestamp);
        return dateTime.toDate();
    }



    public static long dateToTimestamp(Date date) {
        return date.getTime();
    }


}
