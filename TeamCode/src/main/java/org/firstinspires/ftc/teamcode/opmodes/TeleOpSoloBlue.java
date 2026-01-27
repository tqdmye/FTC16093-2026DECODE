package org.firstinspires.ftc.teamcode.opmodes;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.ConditionalCommand;
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

import org.firstinspires.ftc.teamcode.Subsystems.Constants.ServoConstants;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.commands.RobotFSMCommand;
import org.firstinspires.ftc.teamcode.commands.TeleOpDriveCommandPP;
import org.firstinspires.ftc.teamcode.utils.ButtonEx;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;


@TeleOp(group = "0-competition", name = "TeleOp Solo Blue")
public class TeleOpSoloBlue extends CommandOpModeEx {
    GamepadEx gamepadEx1, gamepadEx2;
    Follower follower;
    RobotFSMCommand robotFSMCommand;
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
        follower.setStartingPose(new Pose(-64,64, Math.toRadians(270)));
        follower.update();
        follower.startTeleopDrive();

        TeleOpDriveCommandPP driveCommand = new TeleOpDriveCommandPP(follower,
                ()->gamepadEx1.getLeftX(),
                ()->gamepadEx1.getLeftY(),
                ()->gamepadEx1.getRightX(),
                ()->(gamepadEx1.getButton(GamepadKeys.Button.START) && !gamepad1.touchpad),
                ()->isFieldCentric);

        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        turret = new Turret(hardwareMap);

        robotFSMCommand = new RobotFSMCommand(
                shooter,
                intake
        );

        /* ---------- Schedule ---------- */
        CommandScheduler.getInstance().schedule(driveCommand);
        CommandScheduler.getInstance().schedule(robotFSMCommand);

        //timers
        new ButtonEx(()->getRuntime()>30).whenPressed(()->gamepad1.rumble(500));
        new ButtonEx(()->getRuntime()>60).whenPressed(()->gamepad1.rumble(500));
        new ButtonEx(()->getRuntime()>110).whenPressed(()->gamepad1.rumble(1000));

    }

    @Override
    public void onStart() {
        resetRuntime();
        shooter.accelerate_slow();
        shooter.preLimit.setPosition(ServoConstants.PRELIMIT_DNT_SHOOT.value);
    }

    @Override
    public void functionalButtons() {

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.BACK))
                .whenPressed(new InstantCommand(()->isFieldCentric=!isFieldCentric));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.X))
                .whenPressed(new InstantCommand(()->shooter.state = Shooter.State.SLOW));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.LEFT_BUMPER))
                .whenPressed(new ConditionalCommand(
                        new InstantCommand(()->shooter.preLimit.setPosition(ServoConstants.PRELIMIT_SHOOT.value)),
                        new InstantCommand(()->shooter.preLimit.setPosition(ServoConstants.PRELIMIT_DNT_SHOOT.value)),
                        ()->shooter.preLimit.getPosition()==ServoConstants.PRELIMIT_DNT_SHOOT.value));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.LEFT_STICK_BUTTON))
                .whenPressed(new InstantCommand(()->shooter.state = Shooter.State.MID));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.RIGHT_STICK_BUTTON))
                .whenPressed(new InstantCommand(()->shooter.state = Shooter.State.FAST));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.RIGHT_BUMPER))
                .whenPressed(new SequentialCommandGroup(
                                new InstantCommand(()->intake.intake()),
                                new InstantCommand(()->shooter.intakeBall()),
//                        new ConditionalCommand(
//                                new WaitCommand(0),
//                                new SequentialCommandGroup(
//                                        new InstantCommand(()->shooter.preLimit.setPosition(ServoConstants.PRELIMIT_SHOOT.value)),
//                                        new WaitCommand(400)
//                                ),
//                                ()->shooter.preLimit.getPosition()==ServoConstants.PRELIMIT_SHOOT.value
//                        ),
                        new InstantCommand(()->shooter.preShooter.setPower(1)),
                        new InstantCommand(()->intake.intake())))

                .whenReleased(new SequentialCommandGroup(
                        new InstantCommand(()->intake.init()),
                        new InstantCommand(()->shooter.init()),
                        new WaitCommand(150),
                        new InstantCommand(()->shooter.preShooter.setPower(0)),
                        new WaitCommand(200),
                        new InstantCommand(()->shooter.preLimit.setPosition(ServoConstants.PRELIMIT_DNT_SHOOT.value)),
                        new InstantCommand(()->intake.init())));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.A))
                .whenPressed(new ParallelCommandGroup(
                        new InstantCommand(()->intake.outtake()),
                        new InstantCommand(()->shooter.outtake())))
                .whenReleased(new ParallelCommandGroup(
                        new InstantCommand(()->intake.init()),
                        new InstantCommand(()->shooter.init())));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_RIGHT)) //远射复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(0,50, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_LEFT)) //开门复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(58,0, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_DOWN)) //己方loading zone复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(-60,60, follower.getPose().getHeading()))));

        new ButtonEx(()->gamepadEx1.getButton(GamepadKeys.Button.DPAD_UP)) //敌方loading zone复位
                .whenPressed(new InstantCommand(()->follower.setPose(new Pose(60,60, follower.getPose().getHeading()))));

    }

    @Override
    public void run(){
        CommandScheduler.getInstance().run();

        follower.update();
        turret.lockBlue(follower.getPose());

        telemetry.addData("shooter velocity", shooter.shooterLeft.getVelocity());
        if(isFieldCentric) telemetry.addData("Field Centric", isFieldCentric);
        else telemetry.addData("Robot Centric", isFieldCentric);

        telemetry.addData("Current Pose X", follower.getPose().getX());
        telemetry.addData("Current Pose Y", follower.getPose().getY());
        telemetry.addData("Current Pose Heading", follower.getPose().getHeading());

        telemetry.update();
    }
}