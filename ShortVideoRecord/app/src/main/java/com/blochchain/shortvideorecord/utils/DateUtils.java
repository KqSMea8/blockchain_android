package com.blochchain.shortvideorecord.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtils {

    public static long simpleDateToMilli(String simpleDate) {

        if (simpleDate == null) {
            simpleDate = "2014-12-01 10:00:05.932Z";
        }
        // 2014-10-24T02:58:05.932Z
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long time = 0;
        try {
            time = format.parse(simpleDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long simpleDate(String simpleDate) {
        // 2014-10-24T02:58:05.932Z
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = 0;
        try {
            time = format.parse(simpleDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String milliToSimpleDate(long time) {
        SimpleDateFormat format0 = new SimpleDateFormat("MMdd");
        SimpleDateFormat format = new SimpleDateFormat("HH");
        SimpleDateFormat format1 = new SimpleDateFormat("mm");
        SimpleDateFormat format2 = new SimpleDateFormat("ss");
        int hour = Integer.parseInt(format.format(System.currentTimeMillis()))
                - Integer.parseInt(format.format(time));
        int minute = Integer
                .parseInt(format1.format(System.currentTimeMillis()))
                - Integer.parseInt(format1.format(time));
        int s = Integer.parseInt(format2.format(System.currentTimeMillis()))
                - Integer.parseInt(format2.format(time));
        // 不是当天日期
        if (!format0.format(time).equals(
                format0.format(System.currentTimeMillis()))) {
            SimpleDateFormat format3 = new SimpleDateFormat("MM月 dd日");
            String simpleDate = format3.format(time);
            return simpleDate;
        }
        // 当前日期内
        else {
            if (hour == 1) {
                if (minute >= -59 && minute < 0) {
                    return (minute + 60) + "分钟前";
                } else {
                    return hour + "小时前";
                }
            } else if (hour == 0) {
                if (minute == 1) {
                    if (s >= -59 && s < 0) {
                        return (s + 60) + "秒前";
                    } else {
                        return minute + "分钟前";
                    }
                } else if (minute == 0) {
                    if (s < 0) {
                        return 0 + "秒前";
                    } else {
                        return s + "秒前";
                    }
                }
                if(minute<0){
                    return 0 + "秒前";
                }
                return minute + "分钟前";
            }
            return hour + "小时前";
        }
    }

    public static String milliToSimpleDateHour(long time) {

        SimpleDateFormat format = new SimpleDateFormat("HH");
        String simpleDate = format.format(time);

        return simpleDate;
    }

    public static String milliToSimpleDateTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String simpleDate = format.format(time);
        return simpleDate;
    }

    public static String milliToSimpleDateTime1(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String simpleDate = format.format(time);
        return simpleDate;
    }

    public static String milliToDayAndMonthAndYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String simpleDate = format.format(time);
        return simpleDate;
    }
	/*
     * NSDate *createTime = [NSDate dateWithTimeIntervalSince1970:[[info
	 * objectForKey:@"createTime"] longLongValue]/1000.0]; long offset =
	 * [[NSDate date] timeIntervalSinceDate:createTime]; NSString *createDate =
	 * [TDUtility dateToString:createTime formatter:@"yyyy年MM月dd日"]; NSString
	 * *today = [TDUtility dateToString:[NSDate date] formatter:@"yyyy年MM月dd日"];
	 * if ([createDate isEqualToString:today]) { int hour = (int)offset/3600; if
	 * (hour) { _timeLabel.text = [NSString stringWithFormat:@"%d小时前",hour];
	 * }else { int min = (offset%3600)/60; _timeLabel.text = [NSString
	 * stringWithFormat:@"%d分钟前",min]; } }else { _timeLabel.text = [TDUtility
	 * dateToString:createTime formatter:@"MM月dd日"]; }
	 */
}
