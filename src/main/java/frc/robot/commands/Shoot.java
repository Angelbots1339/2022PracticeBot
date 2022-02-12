// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.LoaderSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.utils.ShooterProfiles;
import static frc.robot.Constants.LoaderConstants.*;

public class Shoot extends CommandBase {

  private ShooterSubsystem shooterSubsystem;
  private LoaderSubsystem loaderSubsystem;
  private ShooterProfiles shooterProfiles;

  /**
   * Revs the flywheels, and when they are at setpoint, it will feed the balls and
   * shoot
   * 
   * @param loaderSubsystem  pass in the intake subsystem
   * @param shooterSubsystem pass in the shooter subsystem
   * @param shooterProfile   pass in a shooter profile 
   */
  public Shoot(LoaderSubsystem loaderSubsystem, ShooterSubsystem shooterSubsystem, ShooterProfiles shooterProfile) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.loaderSubsystem = loaderSubsystem;
    this.shooterSubsystem = shooterSubsystem;
    this.shooterProfiles = shooterProfile;
    addRequirements(loaderSubsystem, shooterSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    shooterSubsystem.setPowerWheelSpeed(shooterProfiles.getPowerRPM());
    shooterSubsystem.setPowerWheelRPM(shooterProfiles.getPowerRPM());
    
    shooterSubsystem.setAimWheelSpeed(shooterProfiles.getAimRPM());

    if (shooterSubsystem.getAtSetpoint()) {
      loaderSubsystem.runLoader(MAX_LOADER_SPEED);

    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    loaderSubsystem.runLoader(0);
    shooterSubsystem.setPowerWheelRPM(0);
    shooterSubsystem.setAimWheelRPM(0);

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
