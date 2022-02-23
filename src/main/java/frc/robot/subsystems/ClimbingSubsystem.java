package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import static frc.robot.Constants.ClimberConstants.*;

public class ClimbingSubsystem extends SubsystemBase {

    // Motors
    private WPI_TalonFX extenderLeftMotor = new WPI_TalonFX(EXTENDER_LEFT_PORT, Constants.CANIVORE_NAME);
    private WPI_TalonFX extenderRightMotor = new WPI_TalonFX(EXTENDER_RIGHT_PORT, Constants.CANIVORE_NAME);
    private WPI_TalonFX rotatorLeftMotor = new WPI_TalonFX(ROTATOR_LEFT_PORT, Constants.CANIVORE_NAME);
    private WPI_TalonFX rotatorRightMotor = new WPI_TalonFX(ROTATOR_RIGHT_PORT, Constants.CANIVORE_NAME);
    private ShuffleboardTab tab = Shuffleboard.getTab("Climber Subsystem");

    // Limit Switches
    private DigitalInput rotatorLeftFrontLimit = new DigitalInput(ROTATOR_LEFT_FRONT_LIMIT_PORT);
    private DigitalInput rotatorRightFrontLimit = new DigitalInput(ROTATOR_RIGHT_FRONT_LIMIT_PORT);
    private DigitalInput rotatorRightBackLimit = new DigitalInput(ROTATOR_LEFT_BACK_LIMIT_PORT);
    private DigitalInput rotatorLeftBackLimit = new DigitalInput(ROTATOR_RIGHT_BACK_LIMIT_PORT);

    private Debouncer debouncerFrontLeft = new Debouncer(LIMIT_SWITCH_DEBOUNCE_SECONDS, Debouncer.DebounceType.kBoth);
    private Debouncer debouncerBackLeft = new Debouncer(LIMIT_SWITCH_DEBOUNCE_SECONDS, Debouncer.DebounceType.kBoth);
    private Debouncer debouncerFrontRight = new Debouncer(LIMIT_SWITCH_DEBOUNCE_SECONDS, Debouncer.DebounceType.kBoth);
    private Debouncer debounceBackRight = new Debouncer(LIMIT_SWITCH_DEBOUNCE_SECONDS, Debouncer.DebounceType.kBoth);
    
    //Through bore encoders
    private DutyCycleEncoder leftEncoder = new DutyCycleEncoder(LEFT_ENCODER_PORT);
    private DutyCycleEncoder rightEncoder = new DutyCycleEncoder(RIGHT_ENCODER_PORT);


    public ClimbingSubsystem() {

        
        extenderLeftMotor.setInverted(EXTENDER_LEFT_INVERTED);
        extenderRightMotor.setInverted(EXTENDER_RIGHT_INVERTED);
        rotatorLeftMotor.setInverted(ROTATOR_LEFT_INVERTED);
        rotatorRightMotor.setInverted(ROTATOR_RIGHT_INVERTED);

        extenderRightMotor.setNeutralMode(NeutralMode.Brake);
        extenderLeftMotor.setNeutralMode(NeutralMode.Brake);
        rotatorLeftMotor.setNeutralMode(NeutralMode.Brake);
        rotatorRightMotor.setNeutralMode(NeutralMode.Brake);

        extenderRightMotor.clearStickyFaults();
        extenderLeftMotor.clearStickyFaults();
        rotatorLeftMotor.clearStickyFaults();
        rotatorRightMotor.clearStickyFaults();
        
       reset(true);
        log();

    }

    @Override
    public void periodic() {
    }

    public void log() {

        tab.addNumber("left Angle", () -> getLeftAngle());
        tab.addNumber("right Angle", () -> getRightAngle());

        tab.addNumber("right Length", () -> getRightLength());
        tab.addNumber("left Length", () -> getLeftLength());
     
        tab.addBoolean("Arms stalling?", () -> false);
        tab.add(this);
    }

    

    // Setters

    /**
     * Resets rotation/extension to 0
     */
    public void reset(boolean resetAngle) {
        extenderLeftMotor.setSelectedSensorPosition(0);
        extenderRightMotor.setSelectedSensorPosition(0);
        if(resetAngle) {
            leftEncoder.reset();;
            rightEncoder.reset();
        }
    }
    
    /**
     * Sets both extension to the same voltage.
     * @param volts Input voltage (will be clamped)
     */
    public void setExtensionVolts(double volts){
        setLeftExtensionVolts(volts);
        setRightExtensionVolts(volts);
    }

    /**
     * Sets both rotation to the same voltage.
     * @param volts Input voltage (will be clamped)
     */
    public void setRotationVolts(double volts) {
        setLeftRotationVolts(volts);
        setRightRotationVolts(volts);
    }

    /**
     * @param volts Input voltage (will be clamped)
     */
    public void setLeftRotationVolts(double volts) {
        volts = MathUtil.clamp(volts, -MAX_ROTATOR_VOLTS, MAX_ROTATOR_VOLTS);
        rotatorLeftMotor.setVoltage(checkBoundsRotations(volts, getLeftAngle(), isLeftFrontAtLimit() && isLeftBackAtLimit()));
    }

    /**
     * @param volts Input voltage (will be clamped)
     */
    public void setRightRotationVolts(double volts) {
        volts = MathUtil.clamp(volts, -MAX_ROTATOR_VOLTS, MAX_ROTATOR_VOLTS);
        rotatorRightMotor.setVoltage(checkBoundsRotations(volts, getRightAngle(), isRightFrontAtLimit() && isRightBackAtLimit()));
    }

    /**
     * @param volts Input voltage (will be clamped)
     */
    public void setRightExtensionVolts(double volts) {
        volts = MathUtil.clamp(volts, -MAX_EXTENDER_VOLTS, MAX_EXTENDER_VOLTS);
        extenderRightMotor.setVoltage(checkBoundsExtensions(volts, getRightLength()));
    }

    /** 
     * @param volts Input voltage (will be clamped)
     */
    public void setLeftExtensionVolts(double volts) {
        volts = MathUtil.clamp(volts, -MAX_EXTENDER_VOLTS, MAX_EXTENDER_VOLTS);
        extenderLeftMotor.setVoltage(checkBoundsExtensions(volts, getLeftLength()));
    }

    public void setTestExtenderPercent(double left, double right){
        extenderRightMotor.set(right);
        extenderLeftMotor.set(left);
    }

    public void setTestRotatorPercent(double left, double right) {
        rotatorLeftMotor.set(left);
        rotatorRightMotor.set(right);
    }
    /**
     * Checks if the extender is at max positions, and which direction is is trying
     * to move
     * 
     * @param voltage
     * @param currentPos
     * @return limited voltage output to not hit end stops
     */
    private double checkBoundsExtensions(double voltage, double currentPos) {
        // Positive voltage is extend out, negative voltage is reel in
        
        if ((currentPos <= EXTENDER_TOP_LIMIT && voltage > 0) || // Current position below top 
                (currentPos >= EXTENDER_BOTTOM_LIMIT && voltage < 0)) {
            return voltage;

        }
        return 0;
    }
    
    // Getters

    /**
     * Checks if the left rotator is at max positions, and which direction it is
     * trying to move
     * 
     * @param voltage
     * @param angle   angle of target motor
     * @param limit   limit switch of given motor
     * @return limited voltage output to not hit end stops
     */

    private double checkBoundsRotations(double voltage, double angle, boolean limit) {
        // Negative voltage is rotate towards hard stop, positive voltage is rotate towards intake
        if ( // check rotate forward (not at limit & forward) or (below front limit & forward)
            ((!limit && voltage > 0) && (angle <= ROTATOR_FRONT_LIMIT_DEG && voltage > 0)) ||
            // check rotate back (above back limit & forward)
            (angle >= ROTATOR_BACK_LIMIT_DEG && voltage < 0)
            ) {
            return voltage;
        }
        return 0;
    }

    public double getRightLength() {
        return extenderRightMotor.getSelectedSensorPosition() * LENGTH_PER_CLICK;
    }

    public double getLeftLength() {
        return extenderLeftMotor.getSelectedSensorPosition() * LENGTH_PER_CLICK;
    }

    public double getRightAngle() {
        return rightEncoder.get() * 360;

    }

    public double getLeftAngle() {
        return -leftEncoder.get() * 360;
    }

    public boolean isLeftFrontAtLimit() {
        return !debouncerFrontLeft.calculate(rotatorLeftFrontLimit.get());
    }

    public boolean isRightFrontAtLimit() {
        return !debouncerFrontRight.calculate(rotatorRightFrontLimit.get());
    }
    public boolean isRightBackAtLimit() {
        return !debounceBackRight.calculate(rotatorRightBackLimit.get());
    }
    public boolean isLeftBackAtLimit() {
        return !debouncerBackLeft.calculate(rotatorLeftBackLimit.get());
    }
    
}
