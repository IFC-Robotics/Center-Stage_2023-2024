package org.firstinspires.ftc.teamcode.powerPlay.oldCode.teleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.powerPlay.oldCode.robot.RobotClass;

import java.lang.Math;

@TeleOp(name="new teleOp (AS)")
@Disabled
public class NewTeleOp extends LinearOpMode {

    RobotClass robot = new RobotClass();

    double servoClawPosition = 0;
    double servoRotateClawPosition = 0;
    double servoHookPosition = 1;
    double servoRotateHookPosition = 0;

    boolean servoClawIsMoving = false;
    boolean servoRotateClawIsMoving = false;
    boolean servoHookIsMoving = false;
    boolean servoRotateHookIsMoving = false;

    boolean motorHorizontalLiftIsMoving = false;
    boolean motorVerticalLiftIsMoving = false;

    @Override
    public void runOpMode() {

        robot.init(hardwareMap);
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad2.start) robot.assistMode = !robot.assistMode;

            controlMovement();

            // make sure this code actually works, aka if you pass a variable into a method and change the variable inside the method, the variable also changes outside the method.

            controlServo(robot.servoClaw, servoClawPosition, servoClawIsMoving, gamepad2.dpad_left, gamepad2.dpad_right);
            controlServo(robot.servoRotateClaw, servoRotateClawPosition, servoRotateClawIsMoving, gamepad2.dpad_up, gamepad2.dpad_down);
            controlServo(robot.servoHook, servoHookPosition, servoHookIsMoving, gamepad2.y, gamepad2.a);
            controlServo(robot.servoRotateHook, servoRotateHookPosition, servoRotateHookIsMoving, gamepad2.x, gamepad2.b);

            controlMotor(robot.motorHorizontalLift, motorHorizontalLiftIsMoving, -gamepad2.left_stick_y, robot.HORIZONTAL_LIFT_MIN_DIST, robot.HORIZONTAL_LIFT_MAX_DIST, robot.HORIZONTAL_LIFT_SPEED, gamepad2.left_bumper, gamepad2.right_bumper, false, false, false, 0.0, 18.0, 0.0, 0.0, 0.0); // change values (18 -> distance to auto collect)
            controlMotor(robot.motorVerticalLift, motorVerticalLiftIsMoving, -gamepad2.right_stick_y, robot.VERTICAL_LIFT_MIN_DIST, robot.VERTICAL_LIFT_MAX_DIST, robot.VERTICAL_LIFT_SPEED, gamepad1.a, gamepad1.x, gamepad1.b, gamepad1.y, gamepad1.right_bumper, 0.0, 2.0, 15.0, 25.0, 35.0); // change values (2, 15, 25, 35 -> height to score on ground, low, middle, and high junctions, respectively)

        }

    }

    public void controlMovement() {

        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(drive) + Math.abs(strafe) + Math.abs(turn), 1);

        double frontLeftPower  = Range.clip((drive + turn + strafe) / denominator, -robot.MAX_TELEOP_DRIVE_SPEED, robot.MAX_TELEOP_DRIVE_SPEED);
        double backLeftPower   = Range.clip((drive + turn - strafe) / denominator, -robot.MAX_TELEOP_DRIVE_SPEED, robot.MAX_TELEOP_DRIVE_SPEED);
        double frontRightPower = Range.clip((drive - turn - strafe) / denominator, -robot.MAX_TELEOP_DRIVE_SPEED, robot.MAX_TELEOP_DRIVE_SPEED);
        double backRightPower  = Range.clip((drive - turn + strafe) / denominator, -robot.MAX_TELEOP_DRIVE_SPEED, robot.MAX_TELEOP_DRIVE_SPEED);

        // if this doesn't work, get rid of the variable "denominator"

        robot.motorFrontLeft.setPower(frontLeftPower);
        robot.motorBackLeft.setPower(backLeftPower);
        robot.motorFrontRight.setPower(frontRightPower);
        robot.motorBackRight.setPower(backRightPower);

        // replace .setPower() with .setVelocity() throughout the code, including here
        // explanation: https://docs.revrobotics.com/duo-control/programming/using-encoder-feedback

    }

    public void controlServo(Servo servo, double servoPosition, boolean servoIsMoving, boolean button1, boolean button2) {

        if (robot.assistMode) {

            if (button1 || button2) {
                if (button1) servo.setPosition(0);
                if (button2) servo.setPosition(1);
                servoIsMoving = true;
            }

        } else {

            if (!servoIsMoving) {
                if (button1 && servoPosition > 0) servoPosition -= 0.001; // test this value
                if (button2 && servoPosition < 1) servoPosition += 0.001; // test this value
            }

            servo.setPosition(servoPosition);

        }

        // remember that servo.getPosition() returns the position that the servo is currently moving to (target position)

        if (servoIsMoving && servo.getPosition() == servoPosition) {
            servoIsMoving = false;
        }

    }

    public void controlMotor(DcMotor motor, boolean motorIsMoving, double joystick, int motorMinDist, int motorMaxDist, double motorMaxSpeed, boolean button1, boolean button2, boolean button3, boolean button4, boolean button5, double button1Dist, double button2Dist, double button3Dist, double button4Dist, double button5Dist) {

        if (!motorIsMoving) {

            double motorSpeed = Range.clip(joystick, -motorMaxSpeed, motorMaxSpeed);
            int motorCurrentPosition = motor.getCurrentPosition();

            if ((motorCurrentPosition > motorMaxDist && motorSpeed > 0) || (motorCurrentPosition < motorMinDist && motorSpeed < 0)) {
                motorSpeed = 0;
            }

            motor.setPower(motorSpeed);

        }

        if (button1 || button2 || button3 || button4 || button5) {

            if (button1) motor.setTargetPosition((int) button1Dist * robot.LIFT_COUNTS_PER_INCH);
            if (button2) motor.setTargetPosition((int) button2Dist * robot.LIFT_COUNTS_PER_INCH);
            if (button3) motor.setTargetPosition((int) button3Dist * robot.LIFT_COUNTS_PER_INCH);
            if (button4) motor.setTargetPosition((int) button4Dist * robot.LIFT_COUNTS_PER_INCH);
            if (button5) motor.setTargetPosition((int) button5Dist * robot.LIFT_COUNTS_PER_INCH);

            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(motorMaxSpeed);
            motorIsMoving = true;

        }

        if (motorIsMoving && !motor.isBusy()) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorIsMoving = false;
        }

    }

}

/* 

Controls being used:

gamepad1.left_stick_x:  strafing
gamepad1.left_stick_y:  driving
gamepad1.right_stick_x: turning
gamepad1.a:             move vertical lift to transfer
gamepad1.x:             move vertical lift to ground junction
gamepad1.b:             move vertical lift to low junction
gamepad1.y:             move vertical lift to middle junction
gamepad1.right_bumper:  move vertical lift to high junction

gamepad2.left_stick_y:  moving horizontal lift
gamepad2.right_stick_y: moving vertical lift
gamepad2.a:             retract hook
gamepad2.x:             move rotate hook to transfer
gamepad2.b:             move rotate hook to score
gamepad2.y:             extend hook
gamepad2.dpad_up:       rotate claw up
gamepad2.dpad_right:    close claw
gamepad2.dpad_down:     rotate claw down
gamepad2.dpad_left:     open claw
gamepad2.left_bumper:   move horizontal lift to collect
gamepad2.right_bumper:  move horizontal lift to transfer
gamepad2.back:          turn assist mode on/off

Controls not being used:

- gamepad1.left_stick_button
- gamepad1.right_stick_button
- gamepad1.right_stick_y
- gamepad1.dpad_up
- gamepad1.dpad_right
- gamepad1.dpad_down
- gamepad1.dpad_left
- gamepad1.left_bumper
- gamepad1.left_trigger
- gamepad1.right_trigger
- gamepad1.back
- gamepad1.start
- gamepad1.guide

- gamepad2.left_stick_button
- gamepad2.right_stick_button
- gamepad2.left_stick_x
- gamepad2.right_stick_x
- gamepad2.left_bumper
- gamepad2.left_trigger
- gamepad2.right_bumper
- gamepad2.right_trigger
- gamepad2.start
- gamepad2.guide

*/