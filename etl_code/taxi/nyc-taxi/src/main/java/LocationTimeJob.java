import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class LocationTimeJob {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage LocationTimeJob <input_path> <output_path>");
            System.exit(0);
        }

        Configuration conf = new Configuration();
        conf.set("mapred.textoutputformat.separator", ",");
        if (args[0].contains("yellow")) {
            conf.set("data_type", "yellow");
        } else if (args[0].contains("green")) {
            conf.set("data_type", "green");
        }

        Job job = Job.getInstance(conf, "analyzing nyc taxi data");
        job.setJarByClass(LocationTimeJob.class);

        if (args[0].contains("yellow")) {
            job.setMapperClass(LocationTimeMapper.YellowMapper.class);
        } else if (args[0].contains("green")) {
            job.setMapperClass(LocationTimeMapper.GreenMapper.class);
        } else {
            job.setMapperClass(LocationTimeMapper.FHVMapper.class);
        }

//        job.setCombinerClass(LocationTimeReducer.class);
        job.setReducerClass(LocationTimeReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        Path inputPath = new Path(args[0]);
        Path outputPath = new Path(args[1]);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
