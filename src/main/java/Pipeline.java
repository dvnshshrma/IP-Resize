import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.opencv.core.Core;

public class Pipeline {

	public Pipeline() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		//System.out.println(new File(".").getAbsolutePath());
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		env.fromCollection(DataSource.retrieveDB().retrieveImageData())
		.map(new ImageMapper()).setParallelism(env.getParallelism())
		.filter(new FilterFunction<Thumbnail>() {
					private static final long serialVersionUID = 1051512855757329267L;
					public boolean filter(Thumbnail data) throws Exception {
						return data.getCroppedImage() != null;
					}
				}).setParallelism(env.getParallelism())
		.flatMap(new FileOutputMapper()).setParallelism(env.getParallelism()).print();
		env.execute();

	}

}
