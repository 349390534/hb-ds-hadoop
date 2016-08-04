package com.howbuy.hadoop.mr.test;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.howbuy.hadoop.mr.online.GenPartitioner;
import com.howbuy.hadoop.mr.online.GuidGroupingComparator;
import com.howbuy.hadoop.mr.online.TaggedKey;


/**
 * @author qiankun.li
 */
public class WebFundPagePvCount extends Configured implements Tool{

	
	private static String FIELD_SEPARATOR = "\u0001";
	
	private static final String GROUP_NAME = "HOW_BUY_FUND_PAGE";

	//公募基金档案页匹配
	private static Pattern HOWBUY_FUND_PATTERN = Pattern.compile("(?:http|https)://www.howbuy.com/fund/\\d{6}/");

	//private static Pattern HOWBUY_SIMU_FUND_PATTERN = Pattern.compile("(?:http|https)://simu.howbuy.com.com//\\d+{6}");
	
	public static  class PVMapper extends  
	    Mapper<LongWritable, Text, TaggedKey, Text>{
		
			private Splitter splitter = Splitter.on(FIELD_SEPARATOR).trimResults();
			
		    public void map(LongWritable key, Text value, Context context)throws IOException, InterruptedException{
		    	context.getCounter(GROUP_NAME,"HOW_BUY_FUND_PAGE").increment(1);

				String line = value.toString();

				List<String> values = Lists.newArrayList(splitter.split(line));

				if (values.size() == 27) {

					String guid = values.get(0);
					String timestamp = values.get(1);

					if (isEmpty(guid) || isEmpty(timestamp) || !guid.startsWith("0x")) {
						
						context.getCounter(GROUP_NAME,GROUP_NAME+"_INVALID_DATA").increment(1);
						return;
					}
					String proid = values.get(22);
					if(!"1001".equals(proid)){
						context.getCounter(GROUP_NAME,GROUP_NAME+"_INVALID_DATA_PROID").increment(1);
						return;
					}
					String desturl = values.get(12);
					
					///公募
					Matcher destmatcher = HOWBUY_FUND_PATTERN.matcher(desturl);
					//私募
					//Matcher simuDestmatcher = HOWBUY_SIMU_FUND_PATTERN.matcher(desturl);
					
					if (!destmatcher.matches()){
						context.getCounter(GROUP_NAME,GROUP_NAME+"_NOT_MATCH_URL").increment(1);
						return;
					}
					
					String fundCode = "";
					TaggedKey tag = new TaggedKey();
					
					Calendar ctc = Calendar.getInstance();
					ctc.setTimeInMillis(Long.valueOf(timestamp));
					ctc.set(Calendar.HOUR, 0);
					ctc.set(Calendar.MINUTE, 0);
					ctc.set(Calendar.SECOND, 0);
					ctc.set(Calendar.MILLISECOND, 0);
					
					tag.setTimestamp(ctc.getTimeInMillis()+"");
					tag.setGuid(fundCode);
					context.write(tag, value);
				}else
					context.getCounter(GROUP_NAME,GROUP_NAME+ "NO_COMPLETE").increment(1);
			
		    }

	   	}
	
	public static  class PVReducer extends  Reducer<TaggedKey, Text, Text, NullWritable>{
		
		//private Splitter splitter = Splitter.on(FIELD_SEPARATOR).trimResults();
		
		private NullWritable nullValue = NullWritable.get();
		
		Text word = new Text();
		
		
	    public void reduce(TaggedKey key, Iterable<Text> values, Context context)
	    											throws IOException, InterruptedException{
	    	String fundCode = key.getGuid();
	    	String dateMil = key.getTimestamp();
	    	int count = 0;
			for (Text str : values) {
				if(!isEmpty(str.toString()))
				count++;
			}
			int pv = count;
			
			Calendar ctc = Calendar.getInstance();
			ctc.setTimeInMillis(Long.valueOf(dateMil));
			StringBuffer sb = new StringBuffer();
			sb.append(DateUtils.getFormatedDate(ctc.getTime(), "yyyy-MM-dd"));
			sb.append("\t");
			sb.append(fundCode);
			sb.append(pv);
			sb.append("\n");
			Text tx = new Text();
			tx.set(sb.toString());
			context.write(tx, nullValue);
	    }

	}
	
	public static boolean isEmpty(String val) {

		if (StringUtils.isEmpty(val) || "null".equals(val))
			return true;
		return false;
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf =getConf();
		Job job = Job.getInstance(conf, "howbuy_fund_page_count");
		job.setJarByClass(WebFundPagePvCount.class);
        job.setInputFormatClass(TextInputFormat.class);
        
        job.setMapOutputKeyClass(TaggedKey.class);
        job.setMapOutputValueClass(Text.class);
          
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class); 
        
        job.setMapperClass(PVMapper.class);  
        job.setReducerClass(PVReducer.class);  
        
        job.setPartitionerClass(GenPartitioner.class);
        job.setGroupingComparatorClass(GuidGroupingComparator.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        //FileInputFormat.setInputPaths(job, new Path("/wordcount"));  
        FileOutputFormat.setOutputPath(job, new Path("/pvcount/"+args[1]));
        //开始运行  
        job.waitForCompletion(true);
        
		return 0;
	}  
	
	public static  void main(String args[])throws Exception{ 
		
		ToolRunner.run(new Configuration(), new WebFundPagePvCount(), args);
		
	}
}
