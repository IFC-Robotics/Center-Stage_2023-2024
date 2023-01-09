package org.firstinspires.ftc.teamcode.powerPlay.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.powerPlay.robot.Robot;

@Autonomous(name="Reset Robot")
public class ResetRobot extends LinearOpMode {

    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Robot.init(hardwareMap);
        waitForStart();

        Robot.reset();

    }

}