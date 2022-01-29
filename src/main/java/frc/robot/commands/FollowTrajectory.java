package frc.robot.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import frc.robot.Constants;
import frc.robot.Constants.AutonomousConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class FollowTrajectory extends RamseteCommand {
    private final DriveSubsystem driveSubsystem;
    private static Pose2d zeroPose = new Pose2d();
    private static DifferentialDriveVoltageConstraint voltageConstraint;
    private static TrajectoryConfig config = new TrajectoryConfig(AutonomousConstants.maxVelocityMetersPerSecond,
            AutonomousConstants.maxAccelerationMetersPerSecondSq);
    private final SimpleMotorFeedforward simpleMotorFeedforward;
    private Trajectory trajectory;

    /**
     * Create a RamseteCommand to follow a given trajectory
     * 
     * @param driveSubsystem Drive subsystem for sensors & dependency injection
     * @param trajectory     Trajectory to follow. See {@link #getAutoTrajectory}
     *                       for autonomous trajectory
     */
    public FollowTrajectory(DriveSubsystem driveSubsystem, Trajectory trajectory) {
        super(trajectory,
                driveSubsystem::getPose, new RamseteController(DriveConstants.KB, DriveConstants.ZETA),
                driveSubsystem.getFeedforward(), DriveConstants.DRIVE_KINEMATICS,
                driveSubsystem::getWheelSpeeds,
                driveSubsystem.getLeftPid(), driveSubsystem.getRightPid(),
                driveSubsystem::tankDriveVolts,
                driveSubsystem);
        this.driveSubsystem = driveSubsystem;

        addRequirements(driveSubsystem);
        simpleMotorFeedforward = new SimpleMotorFeedforward(DriveConstants.KS, DriveConstants.KV, DriveConstants.KA);
        // Constrain the max voltage to 10
        voltageConstraint = new DifferentialDriveVoltageConstraint(simpleMotorFeedforward,
                DriveConstants.DRIVE_KINEMATICS, 10);
        config.setKinematics(DriveConstants.DRIVE_KINEMATICS).addConstraint(voltageConstraint);

        this.trajectory = trajectory;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public static FollowTrajectory followTrajectoryFromJSON(DriveSubsystem driveSubsystem, String fileName) {
        return new FollowTrajectory(driveSubsystem, getTrajectoryFromJSON(fileName));
    }

    public static RamseteCommand TestFollowTrjectory(DriveSubsystem driveSubsystem, String fileName) {

        var leftController = new PIDController(DriveConstants.LEFT_KP + 0.5, 0, 0);
        var rightController = new PIDController(DriveConstants.RIGHT_KP + 0.5, 0, 0);

        RamseteController m_disabledRamsete = new RamseteController();
        m_disabledRamsete.setEnabled(true);
        RamseteCommand ramseteCommand = new RamseteCommand(
                getTrajectoryFromJSON(fileName),
                driveSubsystem::getPose,
                m_disabledRamsete, // Pass inm disabledRamsete here
                driveSubsystem.getFeedforward(),
                Constants.DriveConstants.DRIVE_KINEMATICS,
                driveSubsystem::getWheelSpeeds,
                leftController,
                rightController,
                // RamseteCommand passes volts to the callback
                (leftVolts, rightVolts) -> {
                    driveSubsystem.tankDriveVolts(leftVolts, rightVolts);
                },
                driveSubsystem);

        ShuffleboardTab tab = Shuffleboard.getTab("Test");

        tab.addNumber("LeftActual", () -> driveSubsystem.getWheelSpeeds().leftMetersPerSecond);
        tab.addNumber("RightActual", () -> driveSubsystem.getWheelSpeeds().rightMetersPerSecond);
        tab.addNumber("LeftIdeal", () -> leftController.getSetpoint());
        tab.addNumber("RightIdeal", () -> rightController.getSetpoint());

        return ramseteCommand;

    }

    private static Trajectory getTrajectoryFromJSON(String pathWeeverFileName) {

        Trajectory trajectory = new Trajectory();
        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath()
                    .resolve("output/" + pathWeeverFileName + ".wpilib.json");
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + pathWeeverFileName, ex.getStackTrace());
        }
        return trajectory;
    }

    public static Trajectory getAutoTrajectory1() {
        System.out.println("Called getAutoTrajectory");
        // Draw an 's' curve
        Trajectory trajectory = TrajectoryGenerator
                .generateTrajectory(List.of(zeroPose, new Pose2d(2, 2, Rotation2d.fromDegrees(0))), config);
        return trajectory;
    }

    public static Trajectory getAutoTrajectory2() {
        System.out.println("Called getAutoTrajectory");
        // Draw an 's' curve
        Trajectory trajectory = TrajectoryGenerator
                .generateTrajectory(List.of(new Pose2d(2, 2, Rotation2d.fromDegrees(0)),
                        new Pose2d(0, 4, Rotation2d.fromDegrees(0))), config);
        return trajectory;
    }

    public Pose2d getStartPose2d() {
        return trajectory.getInitialPose();
    }

}
