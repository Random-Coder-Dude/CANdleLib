// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.led.CANdle;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.CANdleSubsystem;
import frc.robot.subsystems.CANdleSubsystem.AnimationConfig;
import frc.robot.subsystems.CANdleSubsystem.AnimationTypes;
import frc.robot.subsystems.CANdleSubsystem.Colors;
import frc.robot.subsystems.CANdleSubsystem.Direction;
import frc.robot.subsystems.CANdleSubsystem.LEDStrip;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  
  // CANdle subsystem and components
  private final CANdleSubsystem m_candleSubsystem = new CANdleSubsystem(Constants.candle.id, CANdle.LEDStripType.RGB);
  private final CANdle m_candle = m_candleSubsystem.createCANdle();
  private final LEDStrip m_fullStrip = m_candleSubsystem.createLEDStrip(0, 60);
  private final LEDStrip m_leftHalf = m_candleSubsystem.createLEDStrip(0, 30);
  private final LEDStrip m_rightHalf = m_candleSubsystem.createLEDStrip(30, 60);

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /** 
   * The container for the robot. Contains subsystems, OI devices, and commands.
   * 
   * <p>This demo showcases various CANdle LED features:
   * <ul>
   *   <li>D-Pad: Different solid colors</li>
   *   <li>Face Buttons: Various animations</li>
   *   <li>Bumpers: Split strip control</li>
   *   <li>Triggers: Fire animations</li>
   *   <li>Start/Back: Special effects</li>
   * </ul>
   */
  public RobotContainer() {
    // Set default color
    m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.OFF).schedule();
    
    // Configure the trigger bindings
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // ========== SOLID COLORS (D-Pad) ==========
    
    // D-Pad Up: Red
    m_driverController.povUp()
        .onTrue(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.RED));
    
    // D-Pad Down: Blue
    m_driverController.povDown()
        .onTrue(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.BLUE));
    
    // D-Pad Left: Green
    m_driverController.povLeft()
        .onTrue(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.GREEN));
    
    // D-Pad Right: Yellow
    m_driverController.povRight()
        .onTrue(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.YELLOW));
    
    
    // ========== ANIMATIONS (Face Buttons) ==========
    
    // A Button: Rainbow animation
    m_driverController.a()
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.OFF, 
            AnimationTypes.Rainbow,
            AnimationConfig.defaults().withSpeed(0.7)
        ));
    
    // B Button: Strobe red
    m_driverController.b()
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.RED, 
            AnimationTypes.Strobe,
            AnimationConfig.fast()
        ));
    
    // X Button: RGB Fade
    m_driverController.x()
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.OFF, 
            AnimationTypes.RgbFade,
            AnimationConfig.defaults().withSpeed(0.5)
        ));
    
    // Y Button: Larson Scanner (KITT effect) with custom color
    m_driverController.y()
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.custom(255, 0, 0), // Custom red
            AnimationTypes.Larson,
            AnimationConfig.defaults()
                .withSpeed(0.8)
                .withSize(5)
        ));
    
    
    // ========== FIRE ANIMATIONS (Triggers) ==========
    
    // Left Trigger: Intense fire
    m_driverController.leftTrigger(0.5)
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.ORANGE, 
            AnimationTypes.Fire,
            AnimationConfig.intenseFire()
        ));
    
    // Right Trigger: Calm fire
    m_driverController.rightTrigger(0.5)
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.ORANGE, 
            AnimationTypes.Fire,
            AnimationConfig.calmFire()
        ));
    
    
    // ========== SPLIT STRIP CONTROL (Bumpers) ==========
    
    // Left Bumper: Left half cyan, right half magenta
    m_driverController.leftBumper()
        .onTrue(
            m_candleSubsystem.setStripColor(m_candle, m_leftHalf, Colors.CYAN)
                .andThen(m_candleSubsystem.setStripColor(m_candle, m_rightHalf, Colors.MAGENTA))
        );
    
    // Right Bumper: Dual color flow (left goes forward, right goes backward)
    m_driverController.rightBumper()
        .onTrue(
            m_candleSubsystem.animateStrip(
                m_candle, 
                m_leftHalf, 
                Colors.PURPLE, 
                AnimationTypes.ColorFlow,
                AnimationConfig.defaults().withDirection(Direction.FORWARD).withSpeed(0.6)
            )
            .andThen(m_candleSubsystem.animateStrip(
                m_candle, 
                m_rightHalf, 
                Colors.ORANGE, 
                AnimationTypes.ColorFlow,
                AnimationConfig.defaults().withDirection(Direction.BACKWARD).withSpeed(0.6)
            ))
        );
    
    
    // ========== SPECIAL EFFECTS (Start/Back) ==========
    
    // Start Button: Party mode (twinkle with multiple colors)
    m_driverController.start()
        .onTrue(m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.WHITE, 
            AnimationTypes.Twinkle,
            AnimationConfig.defaults()
                .withSpeed(0.9)
                .withBrightness(1.0)
        ));
    
    // Back Button: Turn off all LEDs
    m_driverController.back()
        .onTrue(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.OFF));
    
    
    // ========== EXAMPLE SUBSYSTEM BINDINGS ==========
    
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example autonomous routine with LED feedback
    return Autos.exampleAuto(m_exampleSubsystem)
        .beforeStarting(m_candleSubsystem.setStripColor(m_candle, m_fullStrip, Colors.GREEN))
        .finallyDo(() -> m_candleSubsystem.animateStrip(
            m_candle, 
            m_fullStrip, 
            Colors.BLUE, 
            AnimationTypes.Strobe
        ).schedule());
  }
}