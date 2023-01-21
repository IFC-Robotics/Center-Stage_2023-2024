package org.firstinspires.ftc.teamcode.powerPlay.robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class Camera {

    LinearOpMode linearOpMode;
    HardwareMap hardwareMap;
    Telemetry telemetry;

    static int cameraMonitorViewId;
    public static OpenCvCamera camera;
    static AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static double fx = 578.272;
    static double fy = 578.272;
    static double cx = 402.145;
    static double cy = 221.506;

    static double tagsize = 0.166; // measured in meters

    static int LEFT = 1; // tag ID from the 36h11 family
    static int MIDDLE = 2;
    static int RIGHT = 3;

    public Camera() {}

    public void init(LinearOpMode opModeParam) {

        linearOpMode = opModeParam;
        hardwareMap = opModeParam.hardwareMap;
        telemetry = opModeParam.telemetry;

        telemetry.addLine("checkpoint 1 ...");
        telemetry.update();

        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        telemetry.addLine("checkpoint 2 ...");
        telemetry.update();

        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        telemetry.addLine("checkpoint 3 ...");
        telemetry.update();

        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        telemetry.addLine("checkpoint 4 ...");
        telemetry.update();

        camera.setPipeline(aprilTagDetectionPipeline);

        telemetry.addLine("checkpoint 5 ...");
        telemetry.update();

        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() { camera.startStreaming(800, 448, OpenCvCameraRotation.UPRIGHT); }
            @Override
            public void onError(int errorCode) {}
        });

        telemetry.addLine("checkpoint 6 ...");
        telemetry.update();

        telemetry.setMsTransmissionInterval(50);

    }

    public int getTag() {

        int numAttempts = 250;
        int tagID = 0;

        for (int i = 0; i < numAttempts; i++) {

            telemetry.addData("numAttempts", i);

            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            for (AprilTagDetection tag : currentDetections) {
                if (tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT) {
                    tagID = tag.id;
                    telemetry.addData("Tag ID", tagID);
                    telemetry.addData("numAttemptsNeeded", i);
                    telemetry.update();
                    i = numAttempts;
                    break;
                }
            }

            telemetry.update();

            linearOpMode.sleep(20);

        }

        return tagID;

    }

}