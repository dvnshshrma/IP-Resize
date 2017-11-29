import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Thumbnail {
	private Mat image;
	private Mat croppedImage;
	private Integer id;

	public Mat getImage() {
		return image;
	}

	public void setImage(Mat image) {
		this.image = image;
	}
	
	public Mat getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(Mat croppedImage) {
		this.croppedImage = croppedImage;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Mat correctedImage(CascadeClassifier faceDetector, MatOfRect face_Detections) {
		double degree = 0.0;
		boolean checkFace = isFaceDetected(faceDetector, face_Detections);
		if (checkFace == true) {
			return image;
		}
		while (checkFace == false && degree <= 180) {
			image = rotateImage(10.0);
			checkFace = isFaceDetected(faceDetector, face_Detections);
			degree += 10.0;
		}
		return image;
	}

	private boolean isFaceDetected(CascadeClassifier faceDetector, MatOfRect face_Detections) {
		faceDetector.detectMultiScale(image, face_Detections);
		if (face_Detections.toArray().length == 0) {
			return false;
		}
		return true;
	}

	private Mat rotateImage(double angle) {
		Point center = new Point(image.rows() / 2, image.cols() / 2);
		Mat dest = Mat.zeros(image.height(), image.width(), image.type());
		Mat rot_mat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
		Imgproc.warpAffine(image, dest, rot_mat, dest.size());
		return dest;
	}

}
