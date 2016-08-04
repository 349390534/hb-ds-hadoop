package com.howbuy.hadoop.mr.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * <pre>
 *  时间帮助类
 * </pre>
 *
 * @author ji.ma
 * @create 13-3-15 上午10:23
 * @modify
 * @since JDK1.6
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils{
    
    public static final String FORMAT_YYYYMMDD="yyyyMMdd";
    
    public static final String FORMAT_YYYYMMDD_HHMMSS="yyyyMMddHHmmss";
    
    public static final String FORMAT_D_YYYYMMDD="yyyy-MM-dd";
    

    /**
     * 如果日期为空，返回昨天
     * @param date   昨天
     * @return   .
     */
    public static String getDefaultDateIfEmpty(String date) {
        if (StringUtils.isEmpty(date)) {
            date =  DateUtils.getFormatedDate(getYesterdayDate());
        }

        return date;
    }

    public static String getDefaultMonthIfEmpty(String month) {
        if (StringUtils.isEmpty(month)) {
            month =  DateUtils.getFormatedDate(getYesterdayDate(),"yyyy-MM");
        }

        return month;
    }

    public static String getDateFromTime(String time) {
        String date = time;
        if (StringUtils.isEmpty(time) || date.length() < 10) {
            date = DateUtils.getFormatedDate(getYesterdayDate());
        } else if (date.length() > 10) {
            date = date.substring(0, 10);
        }

        return date;
    }

    public static Date getYesterdayDate() {
        return DateUtils.addDays(new Date(), -1);
    }

    public static String currentDate() {
        Date currentDate = new Date();

        return getFormatedDate(currentDate);
    }

    public static String getFormatedDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date parseDate(String date, String format) {
        try {
            return parseDate(date,new String[] {format});
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date parseDate(String s) {
        return parseDate(s,"yyyy-MM-dd");
    }

    public static String getFormatedDate(Date date) {
        return getFormatedDate(date, "yyyy-MM-dd");
    }
    
    
    /**
     * 获取 fromDay 前后interval的日期
     * @param fromDay
     * @return
     */
    public static Date getDate(String fromDay,int interval) {
        return DateUtils.addDays(parseDate(fromDay), interval);
    }

    /**
     * 计算两个日期之间相差的天数，不足1天的时间将舍去
     *
     * @param d1 被减数
     * @param d2 减数
     * @return d1-d2 时间相差的天数
     */
    public static int minus(Date d1, Date d2) {
        return (int) Math.floor((d1.getTime() - d2.getTime()) / 1000 / 60 / 60 / 24);
    }
}
