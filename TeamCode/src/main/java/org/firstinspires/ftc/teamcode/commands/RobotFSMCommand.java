package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

import java.util.function.BooleanSupplier;

public class RobotFSMCommand extends CommandBase {

    private final Shooter shooter;
    private final Intake intake;

    public RobotFSMCommand(
            Shooter shooter,
            Intake intake
    ) {
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    public void execute() {

        switch (shooter.state) {
            case SLOW:
                shooter.accelerate_slow();
                break;
            case MID:
                shooter.accelerate_mid();
                break;
            case FAST:
                shooter.accelerate_fast();
                break;
        }
    }
}
