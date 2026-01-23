package org.firstinspires.ftc.teamcode.testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Test Shooter PID", group = "test")
@Config
public class TestShooterPID extends LinearOpMode {
  private final Telemetry telemetry_M =
      new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
  public static boolean isPIDControl = true;
  public static double setP = 30;
  public static double setI = 0;
  public static double setD = 0;
  public static double setF = 15;
  public static double setShooterPower = 1;
  public static boolean isPowerMode = false;
  public static double setPreShooterPower = 1;
//  public static double shooterMinVelocity = 1400.0;
  public static double shooterVelocity = 1100;
  public static double hoodPosition = 0.05;

  @Override
  public void runOpMode() throws InterruptedException {
    DcMotorEx shooterLeft = hardwareMap.get(DcMotorEx.class, "shooterLeft");
    DcMotorEx shooterRight = hardwareMap.get(DcMotorEx.class, "shooterRight");
    DcMotorEx preShooter = hardwareMap.get(DcMotorEx.class, "preShooter");
    DcMotorEx intake = hardwareMap.get(DcMotorEx.class, "intake");
    Servo hood = hardwareMap.get(Servo.class, "hood");
    Servo preLimit = hardwareMap.get(Servo.class, "preLimit");

    shooterLeft.setDirection(DcMotorSimple.Direction.FORWARD);
    shooterRight.setDirection(DcMotorSimple.Direction.REVERSE);
    preShooter.setDirection(DcMotorSimple.Direction.REVERSE);

    shooterLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    shooterRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

    shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    if (isPIDControl) {
      shooterLeft.setVelocityPIDFCoefficients(setP, setI, setD, setF);
      shooterRight.setVelocityPIDFCoefficients(setP, setI, setD, setF);
    }

    waitForStart();

    while (opModeIsActive()) {
      if(isPowerMode){
        shooterLeft.setPower(setShooterPower);
        shooterRight.setPower(setShooterPower);
      }
      else{
        shooterLeft.setVelocity(shooterVelocity);
        shooterRight.setVelocity(shooterVelocity);
      }

      hood.setPosition(hoodPosition);

//      if (frontShooter.getVelocity() > shooterMinVelocity) {

      if(gamepad1.dpad_up){
        preLimit.setPosition(0.74);
      }
      else{
        preLimit.setPosition(0.42);
      }
      if(gamepad1.a){
        preShooter.setPower(setPreShooterPower);
        intake.setPower(1);
      }
      else{
        preShooter.setPower(0);
        intake.setPower(0);
      }

//      if (frontShooter.getVelocity() < shooterMinVelocity) {
//        //            if(gamepad1.b){
//        preShooter.setPower(0);
//        blender.setPower(0);
//        intake.setPower(0);
//      }

      telemetry_M.addData("Shooter Velocity", shooterLeft.getVelocity());
      telemetry_M.addData("PreShooter Velocity", preShooter.getVelocity());
      telemetry_M.update();
    }
  }
}