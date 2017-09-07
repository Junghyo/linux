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

	// Map 클래스 선언
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable>{
		// 단어가 들어오면 일단 1개로 매핑처리.
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException{
			// 입력이 들어오면 해당 단위로 문자열을 자르는 처리..(공백)
			StringTokenizer itr = new StringTokenizer(value.toString());
			// 구분되는 내용을 저장 처리..  context에..
			while(itr.hasMoreTokens()){
				word.set(itr.nextToken());
				context.write(word, one);
			}
		}
	}
	
	// Reduce 클래스 선언
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result =new IntWritable();
		// 문자열과 1 로 맵핑된 데이터를 같은 문자는 count up 처리 - 총계 처리..
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
		// hdfs 에 처리(추후)
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);
		// job의 setting!!
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		// 입력할 파일 폴드지정.. 
		FileInputFormat.addInputPath(job, new Path("/home/vagrant/hadoop/input"));
		// 출력할 파일 폴드 지정..
		FileOutputFormat.setOutputPath(job, new Path("/home/vagrant/hadoop/output100"));
		// 마무리 처리, 자원해제.
		job.waitForCompletion(true);
		
		
		
		
		
		
		
		
	}

}
