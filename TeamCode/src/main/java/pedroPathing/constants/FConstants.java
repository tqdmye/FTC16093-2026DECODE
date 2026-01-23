package pedroPathing.constants;

import com.pedropathing.localization.Localizers;
import com.pedropathing.follower.FollowerConstants;
import com.qualcomm.robotcore.hardware.DcMotorSimple;



public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = "leftFront";
        FollowerConstants.leftRearMotorName = "leftRear";
        FollowerConstants.rightFrontMotorName = "rightFront";
        FollowerConstants.rightRearMotorName = "rightRear";

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.FORWARD;

        FollowerConstants.mass = 10;

        FollowerConstants.xMovement = 80;
        FollowerConstants.yMovement = 64;

        FollowerConstants.forwardZeroPowerAcceleration = -33;
        FollowerConstants.lateralZeroPowerAcceleration = -63;


        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.08, 0, 0.0005, 0.02);
        FollowerConstants.useSecondaryTranslationalPID = false;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.15, 0, 0.03, 0.1);

        FollowerConstants.headingPIDFCoefficients.setCoefficients(0.8, 0.02, 0.001, 0.01);
        FollowerConstants.useSecondaryHeadingPID = false;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2.5, 0, 0, 0.5);

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.5,0,0.001,0.6,0.019);
        FollowerConstants.useSecondaryDrivePID = true;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.001,0,0.00001,0,0);

        FollowerConstants.zeroPowerAccelerationMultiplier = 4;

        FollowerConstants.centripetalScaling = 0.0006;

        FollowerConstants.pathEndTimeoutConstraint = 100;
        FollowerConstants.pathEndTValueConstraint = 0.99;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;

        FollowerConstants.automaticHoldEnd = true;
        FollowerConstants.useVoltageCompensationInAuto = true;
    }
}