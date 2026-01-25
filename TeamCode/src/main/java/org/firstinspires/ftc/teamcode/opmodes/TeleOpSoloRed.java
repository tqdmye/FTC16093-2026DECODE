package org.firstinspires.ftc.teamcode.opmodes;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.RepeatCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.localization.PoseUpdater;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.commands.TeleOpDriveCommandPP;
import org.firstinspires.ftc.teamcode.utils.ButtonEx;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;


@TeleOp(group = "0-competition", name = "TeleOp Solo Red")
public class TeleOpSoloRed extends CommandOpModeEx {
    GamepadEx gamepadEx1, gamepadEx2;
//    NewMecanumDrive driveCore;
    Follower follower;
    PoseUpdater poseUpdater;
    Shooter shooter;
    Intake intake;
    Turret turret;
    private boolean isFieldCentric=false;


    @Override
    public void initialize() {
        CommandScheduler.getInstance().cancelAll();
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        gamepadEx1 = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        follower.setStartingPose(new Pose(64,-64, Math.toRadians(90)));
        follower.update();
        follower.startTeleopDrive();

//        driveCore = new NewMecanumDrive(hardwareMap);
//        driveCore.resetOdo();
//        driveCore.init();
//        driveCore.setPoseEstimate(new Pose2d(0, 0, Math.toRadians(0)));
//        driveCore.resetHeading();
//        driveCore.yawHeading += 90; //如果specimen自动接solo手动就把这行去掉
//        driveCore.yawHeading %= 360;    //如果specimen自动接solo手动就把这行去掉
//        driveCore.resetPose(new Pose2d(0,0));
//        driveCore.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        driveCore.initialUpdate();

//        Constants.setConstants(FConstants.class, LConstants.class);
//        poseUpdater = new PoseUpdater(hardwareMap, FConstants.class, LConstants.class);
//        poseUpdater.setStartingPose(new Pose(0,0, Math.toRadians(90)));


        TeleOpDriveCommandPP driveCommand = new TeleOpDriveCommandPP(follower,
                ()->gamepadEx1.getLeftX(),
                ()->gamepadEx1.getLeftY(),
                ()->gamepadEx1.getRightX(),
                ()->(gamepadEx1.getButton(GamepadKeys.Button.START) && !gamepad1.touchpad),
                ()->isFieldCentric);

        intake = new Intake(hardwareMap);
//        frontArm.setLED(false);
        shooter = new Shooter(hardwareMap);
        turret = new Turret(hardwareMap);


        CommandScheduler.getInstance().schedule(driveCommand);

        //timers
        new ButtonEx(()->getRuntime()>30).whenPressed(()->gamepad1.rumble(500));
        new ButtonEx(()->getRuntime()>60).whenPressed(()->gamepad1.rumble(500));
        new ButtonEx(()->getRuntime()>110).whenPressed(()->gamepad1.rumble(1000));

    }

    @Override
    public void onStart() {
        resetRuntime();
        shooter.accelerate_slow();
        shooter.preLimit.setPosition(0.42);
//        follower.startTeleopDrive();
    }

    @Override
    public void functionalButtons() {

        //leftBumper -- intake
        //rightTrigger -- Shooter
        //leftTrigger -- preShooter
        //a -- preShooter & intake 反转

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.BACK))
                .whenPressed(new InstantCommand(()->isFieldCentric=!isFieldCentric));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.LEFT_BUMPER))
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(()->intake.intake()),
                        new InstantCommand(()->shooter.intakeBall()),
                        new InstantCommand(()->shooter.accelerate_slow())
                ))
                .whenReleased(new SequentialCommandGroup(
                        new InstantCommand(()->intake.init()),
                        new InstantCommand(()->shooter.init())));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.LEFT_STICK_BUTTON))
                .whenPressed(new InstantCommand(()->shooter.accelerate_mid()));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.RIGHT_STICK_BUTTON))
                .whenReleased(new RepeatCommand(new InstantCommand(()->shooter.accelerate_fast())),true);

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenPressed(new SequentialCommandGroup(
                        new InstantCommand(()->shooter.preLimit.setPosition(0.75)),
                        new WaitCommand(500),
                        new InstantCommand(()->shooter.preShooter.setPower(1)),
                        new InstantCommand(()->intake.intake())))
                .whenReleased(new SequentialCommandGroup(
                        new InstantCommand(()->shooter.preLimit.setPosition(0.42)),
                        new WaitCommand(500),
                        new InstantCommand(()->shooter.preShooter.setPower(0)),
                        new InstantCommand(()->intake.init())));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.A))
                .whenPressed(new ParallelCommandGroup(
                        new InstantCommand(()->intake.outtake()),
                        new InstantCommand(()->shooter.outtake())))
                .whenReleased(new ParallelCommandGroup(
                        new InstantCommand(()->intake.init()),
                        new InstantCommand(()->shooter.init())));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_DOWN))
                .whenPressed(new InstantCommand(()->shooter.emergency()))
                .whenReleased(new InstantCommand(()->shooter.stopAccelerate()));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_UP))
                .whenPressed(new InstantCommand(()->shooter.stopAccelerate()));

        new ButtonEx(()->gamepadEx2.getButton(GamepadKeys.Button.DPAD_RIGHT)) //远射复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(0,-50, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx2.getButton(GamepadKeys.Button.X)) //开门复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(58,0, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx2.getButton(GamepadKeys.Button.DPAD_DOWN)) //己方loading zone复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(-60,-60, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx2.getButton(GamepadKeys.Button.DPAD_UP)) //敌方loading zone复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(60,60, follower.getPose().getHeading()))));

    }

    @Override
    public void run(){
        CommandScheduler.getInstance().run();
//        driveCore.update();
//        turret.lock(new Pose(driveCore.getPoseEstimate().getX(), driveCore.getPoseEstimate().getY(), driveCore.getHeading()));

        follower.update();
        turret.lockRed(follower.getPose());

//        poseUpdater.update();
//        turret.lock(new Pose(poseUpdater.getPose().getX(), poseUpdater.getPose().getY(), poseUpdater.getPose().getHeading()));


        telemetry.addData("shooter velocity", shooter.shooterLeft.getVelocity());
        if(isFieldCentric) telemetry.addData("Field Centric", isFieldCentric);
        else telemetry.addData("Robot Centric", isFieldCentric);
//        telemetry.addData("Current Pose X", driveCore.getPoseEstimate().getX());
//        telemetry.addData("Current Pose Y", driveCore.getPoseEstimate().getY());
//        telemetry.addData("Current Pose Heading", driveCore.getHeading());

        telemetry.addData("Current Pose X", follower.getPose().getX());
        telemetry.addData("Current Pose Y", follower.getPose().getY());
        telemetry.addData("Current Pose Heading", follower.getPose().getHeading());

//        telemetry.addData("Current Pose X", poseUpdater.getPose().getX());
//        telemetry.addData("Current Pose Y", poseUpdater.getPose().getY());
//        telemetry.addData("Current Pose Heading", Math.toDegrees(poseUpdater.getPose().getHeading()));


        telemetry.update();
    }
}