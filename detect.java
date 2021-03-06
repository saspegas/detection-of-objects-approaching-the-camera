package arkaplan;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

public class arkaplan {

	static {
		try {
			System.load("C:\\Users\\Lenovo\\Desktop\\opencv\\build\\x64\\vc14\\bin\\opencv_ffmpeg341_64.dll");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static JFrame frame1, frame2;
	private static JLabel label1, label2;
	private static ImageIcon icon1, icon2;

	public static int erteleme = 0;

	private static BufferedImage ConvertMat2Image(Mat kameraVerisi) {
		MatOfByte byteMatVerisi = new MatOfByte();
		Imgcodecs.imencode(".jpg", kameraVerisi, byteMatVerisi);
		byte[] byteArray = byteMatVerisi.toArray();
		BufferedImage goruntu = null;

		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			goruntu = ImageIO.read(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return goruntu;
	}

	public static void PencereHazirla() {
		frame1 = new JFrame();
		frame1.setLayout(new FlowLayout());
		frame1.setSize(800, 1000);
		frame1.setVisible(true);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void Detect(Image detect) {

		Button buton = new Button("TAMAM");

		frame2 = new JFrame();
		frame2.setLayout(new FlowLayout());
		frame2.setSize(800, 1000);
		frame2.setVisible(true);
		frame2.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		icon2 = new ImageIcon(detect);
		label2 = new JLabel();
		label2.setIcon(icon2);
		frame2.add(label2);
		frame2.add(buton);
		frame2.revalidate();

		buton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame2.dispose();
				frame2 = null;
			}
		});
	}

	public static void PushImage(Image img2) {
		if (frame1 == null) {
			PencereHazirla();
		}
		if (label1 != null) {
			frame1.remove(label1);
		}
		icon1 = new ImageIcon(img2);
		label1 = new JLabel();
		label1.setIcon(icon1);
		frame1.add(label1);
		frame1.revalidate();
	}

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture capture = new VideoCapture(0);
		Mat fgmask = new Mat();
		Mat frame = new Mat();
		BackgroundSubtractorMOG2 bgsMOG2 = Video.createBackgroundSubtractorMOG2();

		while (capture.isOpened()) {

			capture.read(frame);
			bgsMOG2.apply(frame, fgmask);

			List<MatOfPoint> contours = new ArrayList<>();
			Imgproc.findContours(fgmask, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

			for (MatOfPoint matOfPoint : contours) {

				if (Imgproc.contourArea(matOfPoint) > 1000) {

					Rect rect = Imgproc.boundingRect(matOfPoint);
					Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

					if (rect.height + rect.width > 850 && erteleme > 0) {

						Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);

						if (frame2 == null)
							Detect(ConvertMat2Image(frame));

					} else {

						erteleme = 1;

					}
				}
			}
			PushImage(ConvertMat2Image(frame));
		}
	}
}