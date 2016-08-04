package com.howbuy.hadoop.mr.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public class ReduceSideJoinDriver extends Configured implements Tool{



	@Override
	public int run(String[] args) throws Exception {
		
		
		
		Splitter splitter = Splitter.on('/');
        StringBuilder filePaths = new StringBuilder();

        Configuration config = new Configuration();
        
        config.set("keyIndex", "0");
        config.set("separator", ",");

        for(int i = 0; i< args.length - 1; i++) {
            String fileName = Iterables.getLast(splitter.split(args[i]));
            config.set(fileName, Integer.toString(i+1));
            filePaths.append(args[i]).append(",");
        }

        filePaths.setLength(filePaths.length() - 1);
        Job job = new Job(config, "ReduceSideJoin");
        job.setJarByClass(ReduceSideJoinDriver.class);

        FileInputFormat.addInputPaths(job, filePaths.toString());
        FileOutputFormat.setOutputPath(job, new Path(args[args.length-1]));

        job.setMapperClass(JoiningMapper.class);
        job.setReducerClass(JoiningReducer.class);
        job.setPartitionerClass(TaggedJoiningPartitioner.class);
        job.setGroupingComparatorClass(TaggedJoiningGroupingComparator.class);
        job.setOutputKeyClass(TaggedKey.class);
        job.setOutputValueClass(Text.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
        
		return 0;
	}
	
	public static void main(String args[]) throws Exception {

		ToolRunner.run(new Configuration(), new ReduceSideJoinDriver(), args);
	}


}
