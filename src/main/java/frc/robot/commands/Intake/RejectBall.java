// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Intake;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.shooter.ReverseShoot;
import frc.robot.commands.shooter.ShootTimed;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LoaderSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

import static frc.robot.Constants.LoaderConstants.*;
import static frc.robot.Constants.ShooterConstants.*;

public class RejectBall extends CommandBase {

  private final LoaderSubsystem loaderSubsystem;
  private final IntakeSubsystem intakeSubsystem;
  private final ShooterSubsystem shooterSubsystem;
  private final BooleanSupplier isTeamRed;
  private ShootTimed shootCommand;

  /**
   * Checks if a ball is at the color sensor, what color it is, and what team the
   * robot is on, then runs the loader to eject any enemy balls
   * 
   * @param isTeamRed False if the team is Blue, True if it is Red
   */
  public RejectBall(LoaderSubsystem loaderSubsystem, IntakeSubsystem intakeSubsystem, ShooterSubsystem shooterSubsystem, BooleanSupplier isTeamRed, BooleanSupplier rejectEnabled) {
    this.loaderSubsystem = loaderSubsystem;
    this.intakeSubsystem = intakeSubsystem;
    this.shooterSubsystem = shooterSubsystem;
    this.isTeamRed = isTeamRed;
    addRequirements(loaderSubsystem);
    this.shootCommand = new ShootTimed(intakeSubsystem, loaderSubsystem, shooterSubsystem, SHOOTER_PROFILE_LOW, REJECT_TIME);
    shootCommand.cancel();
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // Check ball color
    if (intakeSubsystem.isBallLow() && !shootCommand.isScheduled()
        // If ball is blue and we are red
        && (BLUE.colorMatch(intakeSubsystem.getColorSensorRaw()) && isTeamRed.getAsBoolean()) 
        // If ball is red and we are blue
        || (RED.colorMatch(intakeSubsystem.getColorSensorRaw()) && !isTeamRed.getAsBoolean())) { 
        // If ball is opponent color, reject it
      CommandScheduler.getInstance().schedule(createShootTimed()
        // Then push next ball back down to color sensor
        .andThen(
        new ParallelDeadlineGroup(
          new WaitCommand(REVERSE_TIME),
          new EjectBalls(intakeSubsystem, loaderSubsystem),
          new ReverseShoot(shooterSubsystem))
      ));
    } else if (shootCommand.isScheduled() // Trying to eject wrong ball
    // If ball is blue and we are blue
    && (BLUE.colorMatch(intakeSubsystem.getColorSensorRaw()) && !isTeamRed.getAsBoolean()) 
    // If ball is red and we are red
    || (RED.colorMatch(intakeSubsystem.getColorSensorRaw()) && isTeamRed.getAsBoolean())) { 
      shootCommand.cancel();
    }
  }

  /**
   * Creates a new shoot timed command instance
   */
  private ShootTimed createShootTimed() {
    this.shootCommand = new ShootTimed(intakeSubsystem, loaderSubsystem, shooterSubsystem, SHOOTER_PROFILE_REJECT, REJECT_TIME);
    return this.shootCommand;
  }



  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
