
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class DataSource {
	public static String url;
	{
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	static DataSource DB;

	public static DataSource retrieveDB() {
		if (DB == null) {
			DB = new DataSource();
		}
		return DB;
	}

	Connection conn;
	Statement sqlStatement;

	private DataSource() {
		{
			try {
				url = "jdbc:sqlserver://Q3GN0570;databaseName=AdventureWorksDW2014;integratedSecurity=true;";
				conn = DriverManager.getConnection(url);
				sqlStatement = conn.createStatement();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Code to retrieve Data-- Two Columns -- Data, Id
	public ArrayList<Thumbnail> retrieveImageData() throws IOException {
		ArrayList<Thumbnail> ImageList = new ArrayList<Thumbnail>();
		{
			try {
				sqlStatement = conn.createStatement();
				ResultSet imageResultSet = sqlStatement
						.executeQuery("SELECT [Id],[Data] FROM [AdventureWorksDW2014].[dbo].[Thumbnail]");
				int i = 0;
				while (imageResultSet.next()) {
					Blob blob = imageResultSet.getBlob("Data");
//					int pos = 1;
//					int len = 10;
					//byte[] ImageBytes = blob.getBytes(pos, len);
					InputStream is = blob.getBinaryStream();
					Thumbnail data = new Thumbnail();
					data.setImage(readInputStreamIntoMat(is));
					data.setId(imageResultSet.getInt("Id"));
					i = i + 1;
					System.out.println(i);
					ImageList.add(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ImageList;
	}

	private static Mat readInputStreamIntoMat(InputStream inputStream) throws IOException {
		// Read into byte-array
		byte[] temporaryImageInMemory = readStream(inputStream);

		// Decode into mat. Use any IMREAD_ option that describes your image
		// appropriately
		Mat outputImage = Highgui.imdecode(new MatOfByte(temporaryImageInMemory), Highgui.CV_LOAD_IMAGE_UNCHANGED);

		System.out.println("Rows and cols of original image");
		System.out.println(outputImage.rows());
		System.out.println(outputImage.cols());
		
		return outputImage;
	}

	private static byte[] readStream(InputStream stream) throws IOException {
		// Copy content of the image to byte-array
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = stream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		byte[] temporaryImageInMemory = buffer.toByteArray();
		buffer.close();
		stream.close();
		return temporaryImageInMemory;
	}
}
