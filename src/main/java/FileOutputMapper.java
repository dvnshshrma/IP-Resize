import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class FileOutputMapper extends RichFlatMapFunction<Thumbnail, Thumbnail> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileOutputMapper() {
		
	}

	@Override
	public void flatMap(Thumbnail data, Collector<Thumbnail> list) throws Exception {
		{
			try{
			String hdfsPath = "hdfs://localhost:50071";
 		FileSystem fs = FileSystem.get(new URI(hdfsPath), new Configuration());
			IOUtils.copyBytes(
					(InputStream) new BufferedInputStream(createOutputStream(data.getCroppedImage(), data.getId())),
					(OutputStream) fs.create(new Path(hdfsPath + "/cropped-images/" + data.getId() + ".jpg"), (short) 1), 4096,
					true);
			//createOutputStream(data.getCroppedImage(), data.getId());
			list.collect(data);
			}catch(Exception e) {
				System.out.println("Error Storing in Hadoop !! ");
				e.printStackTrace();
			}
		}
		}
	
	private InputStream createOutputStream(Mat picture, int Id) {	
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", picture, bytemat);
		byte[] bytes = bytemat.toArray();
		return new ByteArrayInputStream(bytes);
		
		/*byte[] outputArray = new byte[(int) (picture.total()*picture.elemSize())];
		picture.get(0, 0, outputArray);
		return new ByteArrayInputStream(outputArray);*/
	}

}
