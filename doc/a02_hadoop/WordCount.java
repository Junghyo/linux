package hadoopexp;


import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCount {

	// Map Ŭ���� ����
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
		// �ܾ ������ �ϴ� 1���� ����ó��.
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			// �Է��� ������ �ش� ������ ���ڿ��� �ڸ��� ó��..(����)
			StringTokenizer itr = new StringTokenizer(value.toString());
			// ���еǴ� ������ ���� ó��..  context��..
			while(itr.hasMoreTokens()){
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}
	
	// Reduce Ŭ���� ����
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result =new IntWritable();
		// ���ڿ��� 1 �� ���ε� �����͸� ���� ���ڴ� count up ó�� - �Ѱ� ó��..
		public void reduce(Text key, Iterable<IntWritable> values, Context context) 
					throws IOException, InterruptedException{
			int sum =0;
			for(IntWritable val : values){
				sum+=val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
		
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException  {
		// hdfs �� ó��(����)
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		// job�� setting!!
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		// �Է��� ���� ��������.. 
		FileInputFormat.addInputPath(job, new Path("/home/vagrant/hadoop/input"));
		// ����� ���� ���� ����..
		FileOutputFormat.setOutputPath(job, new Path("/home/vagrant/hadoop/output100"));
		// ������ ó��, �ڿ�����.
		job.waitForCompletion(true);
		
		
		
		
		
		
		
		
	}

}
