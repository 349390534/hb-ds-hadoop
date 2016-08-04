package com.howbuy.hadoop.mr.test;

import java.util.Calendar;
import java.util.Date;

public class TestTime {

	public static void main(String[] args) {
		Calendar ctc = Calendar.getInstance();
		ctc.setTimeInMillis(Long.valueOf("1457145257731"));
		ctc.set(Calendar.HOUR_OF_DAY, 0);
		ctc.set(Calendar.MINUTE, 0);
		ctc.set(Calendar.SECOND, 0);
		ctc.set(Calendar.MILLISECOND, 0);
		System.out.println(ctc.getTimeInMillis());
		System.out.println(new Date(ctc.getTimeInMillis()).toLocaleString());
		String s = "http://www.howbuy.com/fund/163412/";
		
	}
}
