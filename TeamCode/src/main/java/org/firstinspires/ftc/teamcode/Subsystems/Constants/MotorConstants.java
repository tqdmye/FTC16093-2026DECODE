package org.firstinspires.ftc.teamcode.Subsystems.Constants;

public enum MotorConstants {
    SHOOTER_SLOW_VELOCITY(0),
    SHOOTER_MID_VELOCITY(1300),
    SHOOTER_FAST_VELOCITY(2100),

    SHOOTER_P(30),
    SHOOTER_I(0),
    SHOOTER_D(0),
    SHOOTER_F(12.5)
    ;

    public final double value;

    MotorConstants(double value) {
        this.value = value;
    }
}