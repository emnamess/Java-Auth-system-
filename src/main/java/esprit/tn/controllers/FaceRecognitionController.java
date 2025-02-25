package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.ByteArrayInputStream;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class FaceRecognitionController {

    static {
        try {
            System.load("C:\\opencv\\opencv\\build\\java\\x64\\opencv_java490.dll");
            System.out.println("✅ OpenCV loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Failed to load OpenCV: " + e.getMessage());
        }
    }


    @FXML
    private ImageView imageView;

    @FXML
    private Button captureButton, recognizeButton;

    @FXML
    private Label statusLabel;

    private VideoCapture camera;
    private Mat frame;

    @FXML
    public void initialize() {
        camera = new VideoCapture(0);
        frame = new Mat();

        if (!camera.isOpened()) {
            statusLabel.setText("❌ Camera not detected!");
            return;
        }

        captureButton.setOnAction(event -> captureImage());
        recognizeButton.setOnAction(event -> recognizeFace());
    }


    private void captureImage() {
        if (camera.isOpened()) {
            camera.read(frame);
            String filename = "captured_face.jpg";
            Imgcodecs.imwrite(filename, frame);
            updateImageView(filename);
            statusLabel.setText("📸 Face captured successfully!");
        } else {
            statusLabel.setText("❌ Camera not detected!");
        }
    }

    private void recognizeFace() {
        statusLabel.setText("🔍 Recognizing face...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "face_recognition.py", "captured_face.jpg");
            pb.redirectErrorStream(true); // Capture errors

            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String output;
            StringBuilder result = new StringBuilder();
            while ((output = reader.readLine()) != null) {
                result.append(output).append("\n");
            }
            process.waitFor(); // Wait for the Python script to finish

            statusLabel.setText("✅ Face recognized!\n" + result.toString());
        } catch (Exception e) {
            statusLabel.setText("❌ Error in face recognition!");
            e.printStackTrace();
        }
    }


    private void updateImageView(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                statusLabel.setText("❌ Image file not found!");
                return;
            }

            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading image!");
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        if (camera != null && camera.isOpened()) {
            camera.release();
            System.out.println("📷 Camera closed.");
        }
    }

}
