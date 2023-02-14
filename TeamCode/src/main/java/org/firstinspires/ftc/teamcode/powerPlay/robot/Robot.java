package org.firstinspires.ftc.teamcode.powerPlay.robot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Robot {

    public static Telemetry telemetry;

    public static Drivetrain drivetrain;
    public static ServoClass servoClaw;
    public static ServoClass servoHook;
    public static ServoClass servoRotateClaw;
    public static ServoClass servoRotateHook;
    public static LiftClass horizontalLift;
    public static LiftClass verticalLift;
    //public static SensorClass clawSensor;
    public static SensorClass hookSensor;
    public static Camera camera;

    public static double MAX_LIFT_SPEED = 0.8;
    public static double SERVO_SPEED = 0.01;
    public static int SERVO_TIME = 1000;
    public static int SLEEP_TIME = 50;

    public static String mode = "assist";
    public static String side = "";
    public static int numCycles = 0;
    public static int tag = 0;

    // initialize

    public static void init(LinearOpMode opMode) {

        telemetry = opMode.telemetry;

        telemetry.addLine("initializing robot class...");
        telemetry.update();

        /*

            FABRICATORS TO-DO:

                1. fix servo_rotate_claw
                2. fix servo_claw

            PROGRAMMERS TO-DO ASAP:

                3. test the horizontal_lift predetermined distances
                4. make sure servo_claw actually holds onto the cone
                5. make sure servo_rotate_claw aligns with servo_rotate_hook
                6. test FSM w/ everything

                7. work on auton
                    a. make sure everything is up-to-date
                    b. test all values
                    c. generalize to other auton situations (see globalAuton.java and Robot.configureAuton())
                    d. consider what to do w/ the extra time

            PROGRAMMERS OPTIONAL TO-DO

                7. add color/range sensor to claw (again)
                8. create helper methods for scoring
                    a. Robot.transferToCollect(): extends horizontal lift, rotates claw down, opens claw
                    b. Robot.collectToTransfer(): closes claw, rotates claw up, retracts horizontal lift, opens claw
                    c. Robot.transferToDeposit(): closes hook, raises lift to max height, rotates hook to score, opens hook
                    d. Robot.depositToTransfer(): rotates hook to transfer, lowers lift to zero
                9. test inchesToTicks() and degreesToTicks()

        */

        drivetrain      = new Drivetrain("cone", SLEEP_TIME);
        servoClaw       = new ServoClass("servo_claw", "release", 0.30, "hold", 0.64, SERVO_SPEED, SERVO_TIME, false);
        servoHook       = new ServoClass("servo_hook", "release", 0.51, "hold", 0.57, SERVO_SPEED, SERVO_TIME, false);
        servoRotateClaw = new ServoClass("servo_rotate_claw", "collect",  0.00, "transfer", 1.00, SERVO_SPEED, SERVO_TIME, false);
        servoRotateHook = new ServoClass("servo_rotate_hook", "transfer", 0.10, "score",    0.84, SERVO_SPEED, SERVO_TIME, false);
        horizontalLift  = new LiftClass("motor_horizontal_lift", MAX_LIFT_SPEED, 0,      SLEEP_TIME, false);
        verticalLift    = new LiftClass("motor_vertical_lift",   MAX_LIFT_SPEED, 0.0005, SLEEP_TIME, false);
        hookSensor      = new SensorClass("hook_sensor");
        camera          = new Camera();

        drivetrain     .init(opMode);
        servoClaw      .init(opMode);
        servoHook      .init(opMode);
        servoRotateClaw.init(opMode);
        servoRotateHook.init(opMode);
        horizontalLift .init(opMode);
        verticalLift   .init(opMode);
        hookSensor     .init(opMode);
        camera         .init(opMode);

        resetRandomization();

        telemetry.addLine("done initializing robot class");
        telemetry.update();

    }

    public static double inchesToTicks(double inches) {

        double TICKS_PER_REV = 1120;
        double WHEEL_RADIUS = 2;
        double GEAR_RATIO = 20;

        double wheelCircumference = 2 * Math.PI * WHEEL_RADIUS;
        double ticksPerInch = TICKS_PER_REV / (wheelCircumference * GEAR_RATIO);
        return inches * ticksPerInch;

    }

    public static double degreesToTicks(double degrees) {

        double ROBOT_RADIUS = 9;

        double robotCircumference = 2 * Math.PI * ROBOT_RADIUS;
        double arc = robotCircumference * degrees / 360;
        return inchesToTicks(arc);

    }

    public static void closeHookUsingColorSensor() {

        String currentColor = Robot.hookSensor.getDominantColor();
        double currentDistance = Robot.hookSensor.getDistance("mm");

        double desiredDistance = 55;
        double error = 20;

        boolean correctColor = (currentColor == "red" || currentColor == "blue");
        boolean correctDistance = (desiredDistance - error < currentDistance && currentDistance < desiredDistance + error);

        if (correctColor && correctDistance) Robot.servoHook.runToPosition("hold");

    }

    public static void resetRandomization() {
        mode = "assist";
        side = "";
        numCycles = 0;
        tag = 0;
    }

    public static void resetScoring() {
        servoClaw.runToPosition("hold");
        servoHook.runToPosition("release");
        servoRotateClaw.runToPosition("transfer");
        servoRotateHook.runToPosition("transfer");
        horizontalLift.runToPosition("zero");
        verticalLift.runToPosition("zero");
    }

    public static void configureAuton(LinearOpMode opMode) {

        while (opMode.opModeInInit()) {

            telemetry.addLine("Configuring auton...");

            if (opMode.gamepad1.a) side = "left";
            if (opMode.gamepad1.y) side = "right";

            if (opMode.gamepad2.a) numCycles++;
            if (opMode.gamepad2.y) numCycles = 0;

            telemetry.addData("side", side);
            telemetry.addData("numCycles", numCycles);
            telemetry.update();

        }

    }

}