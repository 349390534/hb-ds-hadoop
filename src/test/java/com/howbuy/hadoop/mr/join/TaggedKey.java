package com.howbuy.hadoop.mr.join;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class TaggedKey implements Writable, WritableComparable<TaggedKey> {

    private Text joinKey = new Text();
    private IntWritable tag;

    public Text getJoinKey() {
		return joinKey;
	}

	public IntWritable getTag() {
		return tag;
	}



	@Override
    public int compareTo(TaggedKey taggedKey) {
        int compareValue = this.joinKey.compareTo(taggedKey.getJoinKey());
        if(compareValue == 0 ){
            compareValue = this.tag.compareTo(taggedKey.getTag());
        }
       return compareValue;
    }
   //Details left out for clarity



	@Override
	public void write(DataOutput out) throws IOException {
		
		Text.writeString(out, joinKey.toString());
		
		out.writeInt(tag.get());
	}



	@Override
	public void readFields(DataInput in) throws IOException {
		
		joinKey = new Text(Text.readString(in));
		
		tag = new IntWritable(in.readInt());
	}



	public void set(String joinKey, int joinOrder) {
		this.joinKey = new Text(joinKey);
		
		this.tag = new IntWritable(joinOrder);
		
	}

	@Override
	public String toString() {
		return "TaggedKey [joinKey=" + joinKey + ", tag=" + tag + "]";
	}
 }
