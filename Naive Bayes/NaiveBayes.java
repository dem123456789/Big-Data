package com.NaiveBayes.Mushroom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

/**
 * Hello world!
 *
 */
public class NaiveBayes 
{
    static String s = null;
    static String key = null;
    static String colvalues = "";
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );
        String inputfiledata = "./data/mushroom.test";
        String outputfile = "./out/test";
        FileSystem fs = null;
        SequenceFile.Writer writer;
        Configuration conf = new Configuration();
        fs = FileSystem.get(conf);
        Path path = new Path(outputfile);
        writer = new SequenceFile.Writer(fs, conf, path, Text.class, Text.class);
        try {
            FileReader fr = new FileReader(inputfiledata);
            BufferedReader br = new BufferedReader(fr);
            Integer count = 0;
            while((s=br.readLine())!=null){

                // My columns are split by tabs with each entry in a new line as rows
                String spl[] = s.split("\\t");
                key = spl[0];

                for(int k=1;k<spl.length;k++){
                		if(colvalues=="") {
                            colvalues = spl[k];
                		} else {
                            colvalues = colvalues + "," + spl[k];                		
                		}
                            
                    }
                writer.append(new Text("/" + key + "/" + count++), new Text(colvalues));
                colvalues = "";
            }
                writer.close();
                br.close();
        } catch (Exception e) {
            System.out.println("ERROR: "+e);
        }

        SequenceFile.Reader reader = new SequenceFile.Reader(fs, new Path("./out/training"), conf);

        Text key = new Text();
        Text value = new Text();
        while(reader.next(key, value)){
            System.out.println(key.toString() + ":" + value.toString());
        }
        reader.close();
        
    }
}
