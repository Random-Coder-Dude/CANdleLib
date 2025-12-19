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

  private final Animations animation =
      candleLib.createAnimation(
          candle,
          ledStrip,
          Colors.BLUE,
          0.2,
          0.5
      );

  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {

    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    m_driverController.x().onTrue(new InstantCommand(() -> animation.run()));
    m_driverController.button(1).whileTrue(new InstantCommand()).onFalse(new InstantCommand());
}


  public Command getAutonomousCommand() {
    return null;
  }
}
