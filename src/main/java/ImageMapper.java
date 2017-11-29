import java.io.FileInputStream;
import java.util.Properties;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

public class ImageMapper extends RichMapFunction<Thumbnail, Thumbnail> {
	private static final long serialVersionUID = -6242385993242907395L;

	@Override
	public Thumbnail map(Thumbnail data) throws Exception {
		Mat image = data.getImage();
		CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_alt.xml");
		MatOfRect face_Detections = new MatOfRect();
		faceDetector.detectMultiScale(image, face_Detections);
		System.out.println(face_Detections.toArray().length);
		if (face_Detections.toArray().length == 0) {
			image = data.correctedImage(faceDetector, face_Detections);
		}
		System.out.println(String.format("Detected %s faces", face_Detections.toArray().length));
		Rect rect_Crop = null;
		Properties pr = new Properties();
		pr.load(new FileInputStream("borderInput.properties"));
		int height_img = Integer.parseInt(pr.getProperty("IMAGE_HEIGHT"));
		int width_img = Integer.parseInt(pr.getProperty("IMAGE_WIDTH"));
		for (Rect rect : face_Detections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + width_img, rect.y + height_img),
					new Scalar(0, 255, 0));
			rect_Crop = new Rect(rect.x, rect.y, rect.width, rect.height);
		}
		// For local file Output
		Mat image_roi = new Mat(image, rect_Crop);
		data.setCroppedImage(image_roi);
		
		return data;
		}

}
