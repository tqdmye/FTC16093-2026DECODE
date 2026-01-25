package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Subsystems.Constants.MotorConstants;

public class Shooter {
    public DcMotorEx shooterLeft, shooterRight, preShooter;
    public Servo preLimit, hood;

    public Shooter(HardwareMap hardwareMap) {
        this.shooterLeft = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        this.shooterRight = hardwareMap.get(DcMotorEx.class, "shooterRight");
        this.preShooter = hardwareMap.get(DcMotorEx.class, "preShooter");
        this.preLimit = hardwareMap.get(Servo.class, "preLimit");
        this.hood = hardwareMap.get(Servo.class, "hood");

        shooterLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterRight.setDirection(DcMotorSimple.Direction.REVERSE);
        preShooter.setDirection(DcMotorSimple.Direction.REVERSE);

        shooterLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        preShooter.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        shooterLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shooterRight.setVelocityPIDFCoefficients(MotorConstants.SHOOTER_P.value, MotorConstants.SHOOTER_I.value, MotorConstants.SHOOTER_D.value, MotorConstants.SHOOTER_F.value);
        shooterLeft.setVelocityPIDFCoefficients(MotorConstants.SHOOTER_P.value, MotorConstants.SHOOTER_I.value, MotorConstants.SHOOTER_D.value, MotorConstants.SHOOTER_F.value);
    }

    public void accelerate_mid(){
        shooterLeft.setVelocity(MotorConstants.SHOOTER_MID_VELOCITY.value);
        shooterRight.setVelocity(MotorConstants.SHOOTER_MID_VELOCITY.value);
        hood.setPosition(0.85);
    }
    public void accelerate_slow(){
        shooterLeft.setVelocity(MotorConstants.SHOOTER_SLOW_VELOCITY.value);
        shooterRight.setVelocity(MotorConstants.SHOOTER_SLOW_VELOCITY.value);
        hood.setPosition(0.85);
    }
    public void accelerate_fast(){
        shooterLeft.setVelocity(MotorConstants.SHOOTER_FAST_VELOCITY.value);
        shooterRight.setVelocity(MotorConstants.SHOOTER_FAST_VELOCITY.value);
        if(MotorConstants.SHOOTER_FAST_VELOCITY.value-shooterLeft.getVelocity()>200){
            hood.setPosition(0.05);
        }
        else{
            hood.setPosition(0.04);
        }
    }
    public void shoot(){
        preLimit.setPosition(0.76);
//        new WaitCommand(5000);
        preShooter.setPower(1);
    }

    public void outtake(){
        preShooter.setPower(-0.8);
    }

    public void emergency(){
        shooterLeft.setPower(-1);
        shooterRight.setPower(-1);
    }

    public void init(){
        preLimit.setPosition(0.42);
        new WaitCommand(5000);
        preShooter.setPower(0);
    }

    public void stopAccelerate(){
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
    }

    public void intakeBall(){
        preShooter.setPower(0.7);
    }
}