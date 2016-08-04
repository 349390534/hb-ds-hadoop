/**
 * 
 */
package com.howbuy.hadoop.mr.test;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author qiankun.li
 *
 */
public class TestPageIdString {

	public static void testPageid(){


		String pageid = "31612670080804007603";
		//3161267+008+0804+00760+3
		int len = pageid.length();
		// N浣峣d
		long zid = Long.valueOf(StringUtils.left(pageid, len-13));
		//3浣峮ewstype
		String newstype = StringUtils.substring(StringUtils.right(pageid, 13), 0,3);
		//4浣峴ubtype 
		String subtype = StringUtils.substring(StringUtils.right(pageid, 13), 3,7);
		//5浣嶄綔鑰�
		String authorId = StringUtils.substring(StringUtils.right(pageid, 13), 7,12);
		System.out.println("zid:"+zid);
		System.out.println("newstype:"+newstype);
		System.out.println("subtype:"+subtype);
		System.out.println("authorId:"+authorId);
	
	}
	
	public static void testPath(){
		String WEBPV_HOUR_FILTER_REGEX_PATTERN_PRE = "\\.";
		Calendar now = Calendar.getInstance();
		//now.set(Calendar.HOUR_OF_DAY, 1);
		
		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY)-1);
		
		int hour = now.get(Calendar.HOUR_OF_DAY);
		System.out.println(hour);
		String input ="hdfs://Hadoop22.howbuy.local:9000/hive/uac/dt=2016-05-16/channel=web/pv/web_pv.27.2016-05-16.10";
		String patern = WEBPV_HOUR_FILTER_REGEX_PATTERN_PRE+hour+"$";
		//String patern = "\\."+hour;
		System.out.println(patern);
		Pattern p = Pattern.compile(patern);
		Matcher pidmatcher = p.matcher(input);
		System.out.println(pidmatcher.matches());
	}
	
	static void testLong (){
		System.out.println(Long.MAX_VALUE);
					 System.out.println("9223372036854775807");
		System.out.println(Long.valueOf("31612670080804007603"));
	}
	
	static void testPagePattern(){
		String p = "http://www.howbuy.com/fund/163412/";
		Pattern howbuy_fund_pattern = Pattern.compile("(?:http|https)://www.howbuy.com/fund/\\d{6}/");
		Matcher m = howbuy_fund_pattern.matcher(p);
		System.out.println(m.matches());

	}
	static void testDt(){
		String s ="1462032133383";
		Date dt = new Date(Long.valueOf(s));
		System.out.println(dt.toLocaleString());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testPath();
		//testLong();
		//testPagePattern();
		testDt();
	}
}
