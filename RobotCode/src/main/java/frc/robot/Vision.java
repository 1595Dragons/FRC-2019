package frc.robot;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;

public class Vision {

    public boolean isRunning = false;

    private GRIPVision grip = new GRIPVision();

    private int cameraWidth;

    private double targetWidth, targetHeight;

    // This is the full FOV of the camera (Or 2 theta)
    private final double FOV = 68.5d;

    /**
     * Only run this once!
     */
    public void generateTargetImage(VideoSource camera) {
        this.isRunning = true;
        new Thread(() -> {

            CvSink cvSink = CameraServer.getInstance().getVideo(camera);
            this.cameraWidth = camera.getVideoMode().width;
            System.out.println("Camera width: " + this.cameraWidth);
            CvSource outputStream = CameraServer.getInstance().putVideo("GRIP", this.cameraWidth,
                    camera.getVideoMode().height);

            Mat source = new Mat();

            while (!Thread.interrupted()) {
                if (cvSink.grabFrame(source) != 0) {
                    grip.process(source);
                    outputStream.putFrame(grip.cvErodeOutput());
                }
            }
        }).start();
    }

    public double findCenterX() {

        double centerX = 0.0d;

        // Check if there are contours to go off of
        if (grip.filterContoursOutput.isEmpty()) {
            return 0.0d;
        }

        // Get the number of contours, and find their center X value
        for (MatOfPoint matpoint : grip.filterContoursOutput) {
            Rect r = Imgproc.boundingRect(matpoint);
            this.targetWidth = r.width;
            this.targetHeight = r.height;
            centerX = ((r.x + r.width) - (r.width / 2) - (this.cameraWidth / 2));
        }

        return centerX;

    }
	
	public double getDegree(double centerX) {
		return (0.2*centerX)-1;
    }
    
    public double getTargetWidth() {
        return this.targetWidth;
    }

    public double getTargetHeight() {
        return this.targetHeight;
    }

    public int numberOfTargets() {
        return grip.filterContoursOutput.size();
    }

    /**
     * @param center center of object (in pixels)
     */
    public double getDistance(double center) {
        double x = this.cameraWidth - center;

        double pixelsPerInch = this.targetWidth/2; // The target is a known 2 inches

        return (x/Math.tan(Math.toRadians(this.FOV/2))) * pixelsPerInch;
    }

}