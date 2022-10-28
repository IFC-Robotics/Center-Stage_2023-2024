package org.firstinspires.ftc.teamcode.powerPlay;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="PowerPlay autonomous", group="powerPlay")
public class autonomous extends LinearOpMode {

    robotClass robot = new robotClass();
    double DRIVE_SPEED = 0.5;

    @Override
    public void runOpMode() {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.init(hardwareMap);
        waitForStart();

        robot.moveMotorOne(DRIVE_SPEED, 200);

    }

}