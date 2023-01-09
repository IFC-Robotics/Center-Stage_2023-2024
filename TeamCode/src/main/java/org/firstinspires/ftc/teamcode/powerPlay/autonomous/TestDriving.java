package org.firstinspires.ftc.teamcode.powerPlay.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.powerPlay.robot.Robot;

@Autonomous(name="Test Driving", group="Test")
public class TestDriving extends LinearOpMode {

    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Robot.init(hardwareMap);
        waitForStart();

        telemetry.addData("Driving", "");
        telemetry.update();

        Robot.drivetrain.drive(12.0);
        sleep(1000);

        telemetry.addData("Strafing", "");
        telemetry.update();

        Robot.drivetrain.strafe(12.0);
        sleep(1000);

        telemetry.addData("Turning", "");
        telemetry.update();

        Robot.drivetrain.turn(90.0);

    }

}