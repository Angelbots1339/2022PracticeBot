// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public final static class JoystickConstants{

         public final static int mainJoystick = 0;
         //public final static int secondaryJoystick = 0; //This is optional


        // TODO all of these are incorrect
        public final static int leftJoystickY = 1;
        public final static int leftJoystickX = 2;
        public final static int rightJoystickX = 4;
        public final static int rightJoystickY = 4;
        public final static int dpadHorizontal = 5;
        public final static int dpadVertical = 6;



        public final static int buttonX = 1;
        public final static int buttonA = 2;
        public final static int buttonB = 3;
        public final static int buttonY = 4;
        public final static int leftBumper = 5;
        public final static int rightBumper = 6;
        public final static int leftTrigger = 7;
        public final static int rightTrigger = 8;
        public final static int leftJoystickButton = 0;
        public final static int rightJoystickButton = 0;
        
        

    }
    /**
     * All length units in meters 
     */
    public final static class DriveConstants{
        //general 
        public final static double maxDriveOutput = 0.25;
        public final static boolean LOG_DATA = true;

        //Motor ports
        public final static int LEFT_MOTOR_TOP_PORT = 2; 
        public final static int LEFT_MOTOR_FRONT_PORT = 1; 
        public final static int LEFT_MOTOR_BACK_PORT = 3; 
        public final static int RIGHT_MOTOR_TOP_PORT = 5; 
        public final static int RIGHT_MOTOR_FRONT_PORT = 4; 
        public final static int RIGHT_MOTOR_BACK_PORT = 6; 
        
        //Drive base values
        public final static double TRACK_WIDTH = Units.inchesToMeters(21.5); // Center of left wheel to center of right wheel
        public final static double WHEEL_DIAMETER = Units.inchesToMeters(3.875);
        public final static double WHEEL_ROT_PER_MOTOR_ROT = 1/6.67;
        public final static double CLICKS_PER_ROT = 2048;
        public final static DifferentialDriveKinematics DRIVE_KINEMATICS = new DifferentialDriveKinematics(TRACK_WIDTH);
        public final static boolean RIGHT_INVERTED = true;
        public final static boolean LEFT_INVERTED = false;
        public final static double CLICKS_TO_METERS = 1 / CLICKS_PER_ROT
                * WHEEL_ROT_PER_MOTOR_ROT * WHEEL_DIAMETER * Math.PI;
        public final static boolean GYRO_INVERTED = true;

        /* Checking robot kinematics:
        Origin of the robot is the center of rotation when leftVelocity = -rightVelocity
        */

        //PID 
        public final static double LEFT_KP = 2.1258;
        public final static double RIGHT_KP = 2.1258;

        /* Checking kP:
        Graph the input motor values from the desired motor values over time
        */

        //Motion profiling
        public final static double KS = 0.5221; // Volts
        public final static double KV = 2.1103;  // Volts * Seconds / Meters 
        public final static double KA = 0.11835; // Volts * Seconds^2 / Meters

        

        //Ramsete
        // TODO The practice bot does better without a ramsete than with one. Need to tune
        public final static double KB = 2;
        public final static double ZETA = 0.7;
        
        /* Checking kV:
        kV = voltage / free speed (meters per second)
        free speed = free speed of the motor times the wheel circumference divided by the gear reduction
        kV = 12v / (5380rpm / 60s * 0.1016m * pi / 6.67)
        kV (theoretical) ~ 2.797
        */

    }
    public final static class AutonomousConstants{ // TODO ensure these match pathweaver inputs
        public final static double maxVelocityMetersPerSecond = 1;
        public final static double maxAccelerationMetersPerSecondSq = 0.25;
        /* Tuning constraints:
        - DifferentialDriveVoltageConstraint: If your robot accelerates very slowly then
        it’s possible that the max voltage for this constraint is too low.
        - DifferentialDriveKinematicsConstraint: If your robot ends up at the wrong heading then
        it’s possible that the max drivetrain side speed is too low,
        or that it’s too high. The only way to tell is to tune the max speed and to see what happens.
        - CentripetalAccelerationConstraint: If your robot ends up at the wrong heading then this could be the culprit.
        If your robot doesn’t seem to turn enough then you should increase the max centripetal acceleration,
        but if it seems to go around tight turns to quickly then you should decrease the maximum centripetal acceleration.
        */
    }
    
    public final static class ShooterConstants {
        public final static int LEFT_POWER_WHEEL = 0;
        public final static int RIGHT_POWER_WHEEL = 1;
        public final static int AIM_WHEEL = 2;

        public final static double POWER_WHEEL_KP = 0;
        public final static double POWER_WHEEL_KI = 0;
        public final static double POWER_WHEEL_KD = 0;
        public final static double AIM_WHEEL_KP = 0;
        public final static double AIM_WHEEL_KI = 0;
        public final static double AIM_WHEEL_KD = 0;

        public final static boolean LEFT_POWER_WHEEL_INVERTED = false;
        public final static boolean RIGHT_POWER_WHEEL_INVERTED = false;

        public final static double POWER_KS = 0;
        public final static double POWER_KV = 0;
        public final static double POWER_KA = 0;

        public final static double AIM_KS = 0;
        public final static double AIM_KV = 0;
        public final static double AIM_KA = 0;

    }
    public final static class LimelightConstants{
        public static enum entryType{
            VALID_TARGETS, HORIZONTAL_OFFSET, VERTICAL_OFFSET, TARGET_AREA, SKEW, LATENCY, SHORTEST_SIDE, LONGEST_SIDE, HORIZONTAL_BOUNDS, VERTICAL_BOUNDS, ACTIVE_PIPELINE, POSE_3D, LED_MODE, CAM_MODE, PIPELINE, STREAM, SNAPSHOT;

            @Override
            public String toString() {
                switch (this) {
                    case VALID_TARGETS:
                        return "tv";
                    case HORIZONTAL_OFFSET:
                        return "tx";
                    case VERTICAL_OFFSET:
                        return "ty";
                    case TARGET_AREA:
                        return "ta";
                    case SKEW:
                        return "ts";
                    case LATENCY:
                        return "tl";
                    case SHORTEST_SIDE:
                        return "tshort";
                    case LONGEST_SIDE:
                        return "tlong";
                    case HORIZONTAL_BOUNDS:
                        return "thor";
                    case VERTICAL_BOUNDS:
                        return "tvert";
                    case ACTIVE_PIPELINE:
                        return "getpipe";
                    case POSE_3D:
                        return "camtran";
                    case LED_MODE:
                        return "ledMode";
                    case CAM_MODE:
                        return "camMode";
                    case PIPELINE:
                        return "pipeline";
                    case STREAM:
                        return "stream";
                    case SNAPSHOT:
                        return "snapshot";
                
                    default:
                        return "";
                }
            }
        }
    }

    
}
