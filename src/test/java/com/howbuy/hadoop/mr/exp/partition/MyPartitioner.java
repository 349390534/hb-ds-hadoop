
package com.howbuy.hadoop.mr.exp.partition;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class MyPartitioner implements Partitioner<IntWritable,Text> {
	@Override
	public int getPartition(IntWritable key, Text value, int numPartitions) {
		/* Pretty ugly hard coded partitioning function. Don't do that in practice, it is just for the sake of understanding. */
		int nbOccurences = key.get();

		if( nbOccurences < 3 )
			return 0;
		else
			return 1;
	}

	@Override
	public void configure(JobConf arg0) {

	}

}
