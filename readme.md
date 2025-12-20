# FRC CANdle Animation Library

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![FRC](https://img.shields.io/badge/FRC-2025-blue.svg)](https://www.firstinspires.org/robotics/frc)

A simple, elegant LED animation library for CTRE CANdle devices with built-in simulation support. Developed by FRC Team 1403 - Cougar Robotics.

## ✨ Features

- 🎨 **5 Animation Types** - Breathing, countdown, state indicator, boolean indicator, and range display
- 🖥️ **Simulation GUI** - Develop and test LED code without hardware
- 📦 **Strip Segmentation** - Control multiple independent sections
- 🎮 **WPILib Command Integration** - Works seamlessly with command-based robot code
- 📚 **Comprehensive JavaDocs** - Full documentation for every method
- 🔧 **Type-Safe Color System** - Predefined colors plus custom RGB support

## 🚀 Quick Start

### Installation

1. Copy `CANdleLib.java` into your robot project at `src/main/java/frc/robot/CANdle/`
2. Add CTRE Phoenix dependency to your `build.gradle` if not already present

### Basic Usage

```java
import frc.robot.CANdle.CANdleLib;
import frc.robot.CANdle.CANdleLib.*;
import com.ctre.phoenix.led.CANdle;

public class RobotContainer {
    // Initialize the library
    private final CANdleLib candleLib = new CANdleLib(1, CANdle.LEDStripType.RGB);
    private final CANdle candle = candleLib.createCANdle();
    
    // Create LED strip segments
    private final LEDStrip fullStrip = candleLib.createLEDStrip(0, 60);
    
    // Set a solid color
    candleLib.setStripColor(candle, fullStrip, Colors.RED).schedule();
    
    // Create a breathing animation
    Animations breathe = candleLib.createAnimation(
        candle, fullStrip, Colors.BLUE, 0.5, 0.2, 0
    );
    breathe.run();
}
```

## 🎯 Animation Types

### 1. Breathing Effect
Smooth pulsing animation with configurable frequency and minimum brightness.

```java
Animations breathe = candleLib.createAnimation(
    candle,           // CANdle device
    strip,            // LED strip segment
    Colors.BLUE,      // Color
    0.5,              // Frequency (Hz)
    0.2,              // Minimum brightness (0.0-1.0)
    0                 // Phase shift (radians)
);
breathe.run();
```

### 2. Countdown Timer
Visual countdown that progressively turns off LEDs.

```java
Animations countdown = candleLib.createAnimation(
    candle,
    strip,
    10.0,             // Duration in seconds
    Colors.ORANGE
);
countdown.run();
```

### 3. State Indicator
Maps enum states to different colors.

```java
private enum RobotState { IDLE, INTAKING, SHOOTING }
private RobotState currentState = RobotState.IDLE;

Animations stateIndicator = candleLib.createAnimation(
    candle,
    strip,
    () -> currentState,           // State supplier
    Colors.WHITE,                 // IDLE color
    Colors.GREEN,                 // INTAKING color
    Colors.RED                    // SHOOTING color
);
stateIndicator.run();
```

### 4. Boolean Indicator
Shows one of two colors based on a boolean condition.

```java
private boolean hasGamePiece = false;

Animations boolIndicator = candleLib.createAnimation(
    candle,
    strip,
    () -> hasGamePiece,          // Boolean supplier
    Colors.GREEN,                // True color
    Colors.OFF                   // False color
);
boolIndicator.run();
```

### 5. Range Display (Progress Bar)
Visualizes a numeric value as a progress bar.

```java
private double batteryVoltage = 12.0;

Animations powerBar = candleLib.createAnimation(
    candle,
    strip,
    10.0,                        // Minimum value
    13.0,                        // Maximum value
    () -> batteryVoltage,        // Value supplier
    Colors.CYAN,                 // Fill color
    Colors.OFF                   // Empty color
);
powerBar.run();
```

## 🎮 Demo Button Mapping

Our included demo showcases all features:

| Button/Input | Action |
|--------------|--------|
| Button 1 | Blue breathing effect |
| Button 2 | Red breathing effect |
| Button 3 | Green breathing effect |
| Button 4 | Purple breathing effect |
| Button 5 | RGB segmented strips |
| Button 6 | 10-second countdown |
| Button 7 | Cycle robot states |
| Button 8 | Toggle system on/off |
| Button 9 | Show power bar |
| Button 10 | Clear all animations |
| POV ↑/↓ | Adjust power level ±10% |
| POV ← | Phase-shifted wave effect |
| POV → | Fast strobe effect |

## 🖥️ Simulation Support

The library automatically detects simulation mode and displays a GUI visualization of your LED strip. Perfect for:
- Remote development
- Testing animations without hardware
- Debugging LED logic
- Teaching students

No configuration needed - just run your robot code in simulation mode!

## 📦 Strip Segmentation

Divide your LED strip into independent controllable sections:

```java
LEDStrip topSection = candleLib.createLEDStrip(0, 20);
LEDStrip middleSection = candleLib.createLEDStrip(20, 40);
LEDStrip bottomSection = candleLib.createLEDStrip(40, 60);

// Control each section independently
candleLib.setStripColor(candle, topSection, Colors.RED).schedule();
candleLib.setStripColor(candle, middleSection, Colors.WHITE).schedule();
candleLib.setStripColor(candle, bottomSection, Colors.BLUE).schedule();
```

## 🎨 Color System

### Predefined Colors
`RED`, `GREEN`, `BLUE`, `YELLOW`, `PURPLE`, `ORANGE`, `WHITE`, `CYAN`, `MAGENTA`, `OFF`

### Custom Colors
```java
LEDColor custom = Colors.custom(100, 150, 200);
candleLib.setStripColor(candle, strip, custom).schedule();
```

## 🔄 Animation Lifecycle

All animations support three lifecycle methods:

```java
Animations anim = candleLib.createAnimation(...);

anim.run();   // Start/resume the animation
anim.stop();  // Pause (keeps current LED state)
anim.end();   // Stop and turn off LEDs
```

## 🏗️ Building Your Own Priority System

The library intentionally leaves orchestration to you:

```java
private void stopAll() {
    breatheBlue.end();
    countdown.end();
    stateIndicator.end();
    // ... stop all other animations
}

private void showCriticalAlert() {
    stopAll();  // Clear everything
    criticalAlert.run();  // Show high-priority animation
}
```

## 📋 Requirements

- WPILib 2024+
- CTRE Phoenix Framework
- Java 17+
- CTRE CANdle hardware (or simulation mode)

## 🤝 Contributing

We welcome contributions! If you find a bug or have a feature request:

1. Open an issue on GitHub
2. Fork the repository
3. Create a pull request

## 📄 License

MIT License

Copyright (c) 2025 FRC Team 1403 - Cougar Robotics

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 🏆 About Team 1403

**Cougar Robotics** - FRC Team 1403

## 📞 Contact

For questions or support:
- Open an issue on GitHub
- Find us on Chief Delphi
- Visit us at FRC competitions!

---

**Built with ❤️ by FRC Team 1403**

*Making LED control simple and powerful for all FRC teams*