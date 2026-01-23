package org.firstinspires.ftc.teamcode.Subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.util.Angle;
import com.pedropathing.localization.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class Turret {

    private final Servo turret;

    /* ================= 机械标定参数（实测） ================= */

    // 舵机正中（云台正前，必须实测）
    private static final double SERVO_CENTER = 0.21;

    // 实测标定：云台旋转 +45° → 舵机 position +0.1
    private static final double SERVO_PER_DEGREE = 0.1 / 45.0; // ≈ 0.002222...

    // 云台机械最大旋转角（deg）
    private static final double MAX_TURRET_ANGLE_DEG = 90.0;

    /* ================= 目标参数（世界坐标，inch / deg） ================= */

    // 目标中心位置（inch）
    private static final double TARGET_X = 29.8859;
    private static final double TARGET_Y = -27.9487;

    // 目标朝向（deg）
    private static final double TARGET_HEADING_DEG = 300.0;

    // 目标“面”法向参考距离（inch）
    private static final double FACE_OFFSET = 0.0;

    /* ======================================================= */

    public Turret(@NonNull HardwareMap hardwareMap) {
        this.turret = hardwareMap.get(Servo.class, "turret");
    }

    /**
     * 根据机器人位姿锁定目标“面”的法向方向
     *
     * Pose:
     *   x, y      -> inch
     *   heading   -> rad （Pedro 规范）
     */
    public void lock(Pose pose) {

        double xr = pose.getX();
        double yr = pose.getY();
        double robotHeadingDeg = Math.toDegrees(pose.getHeading());

        double targetFaceX = TARGET_X + FACE_OFFSET * Math.cos(Math.toRadians(TARGET_HEADING_DEG));
        double targetFaceY = TARGET_Y + FACE_OFFSET * Math.sin(Math.toRadians(TARGET_HEADING_DEG));

        double dx = targetFaceX - xr;
        double dy = targetFaceY - yr;
        double worldTargetAngleDeg = Math.toDegrees(Math.atan2(dy, dx));

        // 度数归一化 [-180, 180]
        double turretAngleDeg = worldTargetAngleDeg - robotHeadingDeg;
        turretAngleDeg = ((turretAngleDeg + 180) % 360 + 360) % 360 - 180;

        // 限幅
        turretAngleDeg = Range.clip(turretAngleDeg, -MAX_TURRET_ANGLE_DEG, MAX_TURRET_ANGLE_DEG);

        // 映射到舵机
        double servoPos = SERVO_CENTER + turretAngleDeg * SERVO_PER_DEGREE;
        turret.setPosition(Range.clip(servoPos, 0.0, 1.0));
    }

}
