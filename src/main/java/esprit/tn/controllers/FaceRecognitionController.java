package esprit.tn.controllers;

import esprit.tn.entities.SessionManager;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.authentificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FaceRecognitionController {

    static {
        try {
            System.loadLibrary("opencv_java490"); // Load OpenCV
            System.out.println("✅ OpenCV loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Failed to load OpenCV: " + e.getMessage());
        }
    }

    @FXML
    private ImageView imageView;
    @FXML
    private Label statusLabel; // For user feedback

    private final authentificationService authService = new authentificationService();
    private final VideoCapture videoCapture = new VideoCapture(0); // Open webcam

    private final Integer userId = SessionManager.getUserIdFromToken(); // ✅ Extract User ID from Token

    @FXML
    private void captureImage() {
        if (!videoCapture.isOpened()) {
            System.out.println("❌ Camera not opened!");
            statusLabel.setText("❌ Camera not opened!");
            return;
        }

        Mat frame = new Mat();
        if (videoCapture.read(frame)) {
            System.out.println("✅ Image captured!");

            // ✅ Define image save path
            String saveDir = "C:/Users/messa/Pictures/user_images/";
            File directory = new File(saveDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Ensure the directory exists
            }

            String imagePath = saveDir + "user_" + userId + ".png";

            // ✅ Save image using OpenCV
            if (Imgcodecs.imwrite(imagePath, frame)) {
                System.out.println("✅ Image saved successfully: " + imagePath);
                statusLabel.setText("✅ Image saved successfully!");

                // ✅ Update ImageView with saved image
                imageView.setImage(new Image(new File(imagePath).toURI().toString()));

                // ✅ Save image path to database
                saveImageToDatabase(imagePath, userId);
            } else {
                System.out.println("❌ Failed to save image!");
                statusLabel.setText("❌ Failed to save image!");
            }
        } else {
            System.out.println("❌ Failed to capture image (empty frame)!");
            statusLabel.setText("❌ Failed to capture image!");
        }
    }

    private void saveImageToDatabase(String imagePath, int userId) {
        String query = "UPDATE user SET profile_image = ? WHERE Id_user = ?";

        try (Connection cnx = DatabaseConnection.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, userId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Image path updated in database successfully!");
            } else {
                System.out.println("⚠ No user found with ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saving image path to database: " + e.getMessage());
        }
    }

    public void stopCamera() {
        if (videoCapture.isOpened()) {
            videoCapture.release();
            System.out.println("📷 Camera closed.");
        }
    }
}
