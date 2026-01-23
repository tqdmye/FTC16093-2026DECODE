package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.driving.NewMecanumDrive;

@TeleOp (group = "Testing")
@Config
public class TestLocalizer extends LinearOpMode {
    //    NewMecanumDrive drive = new NewMecanumDrive();
    NewMecanumDrive drive;

    private final Telemetry telemetry_M = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    public static  double x = -15, y = 62.3, heading = 90;
    public static double targetX = -15, targetY = 62.3, targetHeading = 90;
    private static Pose2d startPos;
    @Override
    public void runOpMode(){
        drive = new NewMecanumDrive(hardwareMap);

//        drive.setUp(hardwareMap);
        startPos = new Pose2d(x,y,Math.toRadians(heading));
        drive.setPoseEstimate(startPos);
        drive.update();
        telemetry.addData("Pos Estimate: ",drive.getPoseEstimate());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()){
            double standard_xPos = drive.getPoseEstimate().getX();
            double standard_yPos = drive.getPoseEstimate().getY();

            if(gamepad1.a){
                drive.initSimpleMove(new Pose2d(targetX,targetY,Math.toRadians(targetHeading)));
            }
            if(gamepad1.b){
                drive.stopTrajectory();
                drive.setMotorPowers(
                        0,0,0,0
                );
            }

            telemetry_M.addData("Current X Position (in): ", "%.3f", standard_xPos);
            telemetry_M.addData("Current Y Position (in): ", "%.3f", standard_yPos);
            telemetry_M.addData("Current Heading: ", drive.getPoseEstimate().getHeading());

            telemetry.update();
        }
    }
}
