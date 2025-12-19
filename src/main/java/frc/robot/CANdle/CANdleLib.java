package frc.robot.CANdle;

import frc.robot.MockCANdle;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

/**
 * Subsystem for controlling CTRE CANdle LED strips with animations and color control.
 * 
 * <p>This subsystem provides a high-level interface for:
 * <ul>
 *   <li>Creating and configuring CANdle devices</li>
 *   <li>Defining LED strip segments</li>
 *   <li>Setting solid colors on strips</li>
 *   <li>Running various animations (rainbow, fire, strobe, etc.)</li>
 *   <li>Configuring animation parameters</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>
 * CANdleSubsystem ledSubsystem = new CANdleSubsystem(1, CANdle.LEDStripType.RGB);
 * CANdle candle = ledSubsystem.createCANdle();
 * LEDStrip strip = ledSubsystem.createLEDStrip(0, 60);
 * 
 * // Set solid color
 * ledSubsystem.setStripColor(candle, strip, Colors.RED).schedule();
 * 
 * // Run animation
 * ledSubsystem.animateStrip(candle, strip, Colors.BLUE, AnimationTypes.Rainbow).schedule();
 * </pre>
 */
public class CANdleLib{
    private int busId;
    private CANdle.LEDStripType m_rgbOrder;

    /**
     * Constructs a new CANdleSubsystem with the specified CAN ID and LED strip type.
     * 
     * @param id the CAN bus ID of the CANdle device
     * @param rgbOrder the LED strip type defining the RGB byte order (e.g., RGB, GRB, BRG)
     */
    public CANdleLib(int id, CANdle.LEDStripType rgbOrder) {
        busId = id;
        m_rgbOrder = rgbOrder;
    }
    
    /**
     * Creates and configures a new CANdle device instance.
     * 
     * <p>This method initializes a CANdle with the CAN ID and LED strip type
     * specified in the constructor. The configuration is automatically applied
     * to the device.
     * 
     * @return a configured CANdle instance ready for use
     */
    public CANdle createCANdle() {
        CANdle candle;

        if (RobotBase.isReal()) {
            candle = new CANdle(busId);
        }
        else {
            candle = new MockCANdle(busId);
        }
        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = m_rgbOrder;
        candle.configAllSettings(config);
        return candle;
    }

    /**
     * Creates a logical LED strip segment definition.
     * 
     * <p>LED strips can be divided into multiple segments, each controllable
     * independently. This method creates a segment spanning from startIndex
     * (inclusive) to endIndex (exclusive).
     * 
     * @param startIndex the first LED index in the strip segment (inclusive)
     * @param endIndex the LED index after the last LED in the segment (exclusive)
     * @return an LEDStrip record representing the specified segment
     */
    public LEDStrip createLEDStrip(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex - startIndex <= 0) {
            throw new IllegalArgumentException("Check Parameters");
        }
        return new LEDStrip(startIndex, endIndex);
    }

    // public Animations createAnimation(CANdle candle, LEDStrip strip, Enum possibleStates, Enum colors) {
    //     return new stateIndicator(candle, strip, possibleStates, colors); 
    // }

    public Animations createAnimation(CANdle candle, LEDStrip strip, double min, double max, DoubleSupplier value, Colors fillColor, Colors emptyColor) {
        return new rangeValue(candle, strip, min, max, value, fillColor, emptyColor); 
    }
    
    /**
     * Creates a command to set the entire LED strip to a solid color.
     * 
     * <p>This command first turns off all LEDs, then sets them to the specified color.
     * This two-step process prevents certain LEDs from sticking in their previous state.
     * The command completes instantly.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param color the color to set (must not be null)
     * @return a SequentialCommandGroup that clears then sets the strip color
     * @throws IllegalArgumentException if any parameter is null
     */
    public Command setColor(CANdle candle, LEDColor color) {
        if (candle == null || color == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        return new InstantCommand(() -> candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * Creates a command to set the entire LED strip to a solid color using RGB values.
     * 
     * <p>This command first turns off all LEDs, then sets them to the specified color.
     * This two-step process prevents certain LEDs from sticking in their previous state.
     * This is a convenience overload that accepts raw RGB integer values
     * instead of an LEDColor object.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param red the red channel value (0-255)
     * @param green the green channel value (0-255)
     * @param blue the blue channel value (0-255)
     * @return a SequentialCommandGroup that clears then sets the strip color
     * @throws IllegalArgumentException if candle is null
     */
    public Command setColor(CANdle candle, int red, int green, int blue) {
        if (candle == null) {
            throw new IllegalArgumentException("CANdle cannot be null");
        }

        return new InstantCommand(() -> candle.setLEDs(red, green, blue));
    }

    /**
     * Creates a command to set a specific LED strip segment to a solid color.
     * 
     * <p>This command first turns off the specified segment, then sets it to the color.
     * This two-step process prevents certain LEDs from sticking in their previous state.
     * This command sets only the LEDs in the specified strip segment,
     * leaving other LEDs unchanged. The command completes instantly.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param strip the LED strip segment to set (must not be null)
     * @param color the color to set (must not be null)
     * @return a SequentialCommandGroup that clears then sets the strip segment color
     * @throws IllegalArgumentException if any parameter is null
     */
    public Command setStripColor(CANdle candle, LEDStrip strip, LEDColor color) {
        if (candle == null || strip == null || color == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        return new InstantCommand(() -> candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue(), 0, strip.start, strip.length()));
    }

    /**
     * Creates a command to set a specific LED strip segment to a solid color using RGB values.
     * 
     * <p>This command first turns off the specified segment, then sets it to the color.
     * This two-step process prevents certain LEDs from sticking in their previous state.
     * This is a convenience overload that accepts raw RGB integer values
     * instead of an LEDColor object.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param strip the LED strip segment to set (must not be null)
     * @param red the red channel value (0-255)
     * @param green the green channel value (0-255)
     * @param blue the blue channel value (0-255)
     * @return a SequentialCommandGroup that clears then sets the strip segment color
     * @throws IllegalArgumentException if candle or strip is null
     */
    public Command setStripColor(CANdle candle, LEDStrip strip, int red, int green, int blue) {
        if (candle == null || strip == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        return new InstantCommand(() -> candle.setLEDs(red, green, blue, 0, strip.start, strip.length()));
    }
    
    /**
     * Represents a contiguous segment of LEDs on a CANdle strip.
     * 
     * <p>LED strips can be logically divided into multiple segments for
     * independent control. Each segment is defined by a start index (inclusive)
     * and an end index (exclusive).
     * 
     * <p>Example:
     * <pre>
     * LEDStrip fullStrip = new LEDStrip(0, 60);     // 60 LEDs total
     * LEDStrip leftHalf = new LEDStrip(0, 30);      // First 30 LEDs
     * LEDStrip rightHalf = new LEDStrip(30, 60);    // Last 30 LEDs
     * </pre>
     * 
     * @param start the first LED index in the segment (inclusive)
     * @param end the LED index after the last LED in the segment (exclusive)
     */
    public record LEDStrip(
        int start,
        int end
    ){
        /**
         * Calculates the number of LEDs in this strip segment.
         * 
         * @return the length of the strip (end - start)
         */
        public int length() {
            return end - start;
        }
    }
    
    /**
     * Interface representing an LED color with red, green, and blue components.
     * 
     * <p>This interface allows for both predefined colors (via the Colors enum)
     * and custom RGB colors (via Colors.custom()).
     * 
     * @see Colors
     */
    public interface LEDColor {
        /**
         * Gets the red component of the color.
         * 
         * @return red value (0-255)
         */
        int getRed();
        
        /**
         * Gets the green component of the color.
         * 
         * @return green value (0-255)
         */
        int getGreen();
        
        /**
         * Gets the blue component of the color.
         * 
         * @return blue value (0-255)
         */
        int getBlue();
    }

    /**
     * Enumeration of predefined LED colors.
     * 
     * <p>Provides common colors for LED control. For custom colors, use
     * {@link #custom(int, int, int)}.
     * 
     * <p>Available colors:
     * <ul>
     *   <li>RED (255, 0, 0)</li>
     *   <li>GREEN (0, 255, 0)</li>
     *   <li>BLUE (0, 0, 255)</li>
     *   <li>YELLOW (255, 255, 0)</li>
     *   <li>PURPLE (128, 0, 128)</li>
     *   <li>ORANGE (255, 165, 0)</li>
     *   <li>WHITE (255, 255, 255)</li>
     *   <li>CYAN (0, 255, 255)</li>
     *   <li>MAGENTA (255, 0, 255)</li>
     *   <li>OFF (0, 0, 0) - turns LEDs off</li>
     * </ul>
     * 
     * <p>Example usage:
     * <pre>
     * LEDColor red = Colors.RED;
     * LEDColor custom = Colors.custom(100, 150, 200);
     * </pre>
     */
    public enum Colors implements LEDColor{
        RED(255, 0, 0),
        GREEN(0, 255, 0),
        BLUE(0, 0, 255),
        YELLOW(255, 255, 0),
        PURPLE(128, 0, 128),
        ORANGE(255, 165, 0),
        WHITE(255, 255, 255),
        CYAN(0, 255, 255),
        MAGENTA(255, 0, 255),
        OFF(0, 0, 0);

        private final int redChannel;
        private final int greenChannel;
        private final int blueChannel;

        Colors(int red, int green, int blue) {
            redChannel = red;
            greenChannel = green;
            blueChannel = blue;
        }

        /**
         * Creates a custom LED color with the specified RGB values.
         * 
         * <p>This method allows creating colors that aren't in the predefined
         * Colors enum. The returned LEDColor can be used anywhere a color
         * is required.
         * 
         * @param red red component (0-255)
         * @param green green component (0-255)
         * @param blue blue component (0-255)
         * @return an LEDColor with the specified RGB values
         */
        public static LEDColor custom(int red, int green, int blue) {
            return new CustomColor(red, green, blue);
        }

        public static class CustomColor implements LEDColor {
            public final int green;
            public final int blue;
            public final int red;
        
            public CustomColor(int red, int green, int blue) {
                this.red = red;
                this.green = green;
                this.blue = blue;
            }

            @Override
            public int getRed() {
                return red;
            }
            
            @Override
            public int getGreen() {
                return green;
            }
            
            @Override
            public int getBlue() {
                return blue;
            }
        }

        @Override
        public int getRed() {
            return redChannel;
        }
        
        @Override
        public int getGreen() {
            return greenChannel;
        }
        
        @Override
        public int getBlue() {
            return blueChannel;
        }
    }

    public enum AnimationType{
        STATEINDICATOR,
        RANGEVALUE,
        BOOLEAN,
        STATUS,
        COUNTDOWN,
        COUNTER
    }

    public interface Animations {
        void run();
        void stop();
        void end();
    }

    // public static class stateIndicator implements Animations{

    //     public stateIndicator(CANdle candle, LEDStrip strip, Enum states, Enum colors) {

    //     }

    //     @Override
    //     public void run() {

    //     }

    //     @Override
    //     public void stop() {

    //     }

    //     @Override
    //     public void end() {

    //     }
    // }

    public static class rangeValue implements Animations {
        private final CANdle candle;
        private final CANdleLib.LEDStrip strip;
        private final double min;
        private final double max;
        private final DoubleSupplier valueSupplier;
        private final CANdleLib.LEDColor fillColor;
        private final CANdleLib.LEDColor emptyColor;
    
        public rangeValue(CANdle candle, CANdleLib.LEDStrip strip, double min, double max, DoubleSupplier valueSupplier, Colors fillColor, Colors emptyColor) {
            this.candle = candle;
            this.strip = strip;
            this.min = min;
            this.max = max;
            this.valueSupplier = valueSupplier;
            this.fillColor = fillColor;
            this.emptyColor = emptyColor;
        }
    
        private void draw() {
            double value = MathUtil.clamp(valueSupplier.getAsDouble(), min, max);
            int totalLEDs = strip.length();
            double fraction = (value - min) / (max - min);
            int litLEDs = (int) Math.round(fraction * totalLEDs);
    
            candle.setLEDs(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), 0, strip.start, litLEDs);
    
            int remaining = totalLEDs - litLEDs;
            if (remaining > 0) {
                candle.setLEDs(emptyColor.getRed(), emptyColor.getGreen(), emptyColor.getBlue(), 0, strip.start + litLEDs, remaining);
            }
        }
    
        private Command updateCommand = new Command() {
            @Override
            public void initialize() {
            }
    
            @Override
            public void execute() {
                draw();
            }
    
            @Override
            public boolean isFinished() {
                return false;
            }
    
            @Override
            public void end(boolean interrupted) {}
        };
    
        @Override
        public void run() {
            if (!updateCommand.isScheduled()) {
                updateCommand.schedule();
            }   
        }
    
        @Override
        public void stop() {
            if (updateCommand.isScheduled()) {
                updateCommand.cancel();
            }
        }
    
        @Override
        public void end() {
            if (updateCommand.isScheduled()) {
                updateCommand.cancel();
            }
            candle.setLEDs(0, 0, 0, 0, strip.start, strip.length());
        }
    }    
}