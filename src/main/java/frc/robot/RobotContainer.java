package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.CANdle.CANdleLib;
import frc.robot.CANdle.CANdleLib.Animations;
import frc.robot.CANdle.CANdleLib.Colors;
import frc.robot.CANdle.CANdleLib.LEDStrip;

import com.ctre.phoenix.led.CANdle;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {

  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

  private final CANdleLib candleLib =
      new CANdleLib(Constants.candle.id, CANdle.LEDStripType.RGB);

  private final CANdle candle = candleLib.createCANdle();
  private final LEDStrip ledStrip = new LEDStrip(0, 100);

  private double value = 50.0;

  private final Animations animation =
      candleLib.createAnimation(
          candle,
          ledStrip,
          0.0,
          100.0,
          () -> value,
          Colors.RED,
          Colors.OFF
      );

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {

    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    m_driverController.x().onTrue(new InstantCommand(() -> animation.run())).onFalse(new InstantCommand(() -> animation.stop()));
    m_driverController.button(1).whileTrue(new InstantCommand(() -> value = 75)).onFalse(new InstantCommand(() -> value = 25));
}


  public Command getAutonomousCommand() {
    return null;
  }
}
