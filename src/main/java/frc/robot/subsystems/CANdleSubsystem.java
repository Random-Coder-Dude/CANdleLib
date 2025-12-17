package frc.robot.subsystems;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.ColorFlowAnimation;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.LarsonAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.RgbFadeAnimation;
import com.ctre.phoenix.led.SingleFadeAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.ctre.phoenix.led.TwinkleAnimation;
import com.ctre.phoenix.led.TwinkleOffAnimation;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

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
public class CANdleSubsystem extends SubsystemBase{
    private int busId;
    private CANdle.LEDStripType m_rgbOrder;

    /**
     * Constructs a new CANdleSubsystem with the specified CAN ID and LED strip type.
     * 
     * @param id the CAN bus ID of the CANdle device
     * @param rgbOrder the LED strip type defining the RGB byte order (e.g., RGB, GRB, BRG)
     */
    public CANdleSubsystem(int id, CANdle.LEDStripType rgbOrder) {
        busId = id;
        m_rgbOrder = rgbOrder;
    }

    /**
     * Creation Stuff
     */
    
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
        CANdle candle = new CANdle(busId);
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
        return new LEDStrip(startIndex, endIndex);
    }

    /**
     * Creates a CANdle animation with default configuration.
     * 
     * <p>This is a convenience method that calls {@link #createCANdleAnimation(CANdle, LEDStrip, LEDColor, AnimationTypes, AnimationConfig)}
     * with {@link AnimationConfig#defaults()}.
     * 
     * @param candle the CANdle device to animate
     * @param strip the LED strip segment to apply the animation to
     * @param color the color to use for the animation (may be ignored by some animation types)
     * @param animation the type of animation to create
     * @return the configured Animation object
     * @see #createCANdleAnimation(CANdle, LEDStrip, LEDColor, AnimationTypes, AnimationConfig)
     */
    public Animation createCANdleAnimation(CANdle candle, LEDStrip strip, LEDColor color, AnimationTypes animation) {
        return createCANdleAnimation(candle, strip, color, animation, AnimationConfig.defaults());
    }

    /**
     * Creates a CANdle animation with custom configuration.
     * 
     * <p>This method constructs the appropriate CTRE Animation object based on
     * the specified animation type and configuration. Different animation types
     * use different parameters from the config:
     * 
     * <ul>
     *   <li><b>ColorFlow:</b> Uses color, speed, and direction</li>
     *   <li><b>Fire:</b> Uses brightness, speed, sparking, cooling, and direction</li>
     *   <li><b>Larson:</b> Uses color, speed, size, and direction (bounce mode)</li>
     *   <li><b>Rainbow:</b> Uses brightness, speed, and direction</li>
     *   <li><b>RgbFade:</b> Uses brightness and speed</li>
     *   <li><b>SingleFade:</b> Uses color and speed</li>
     *   <li><b>Strobe:</b> Uses color and speed</li>
     *   <li><b>Twinkle:</b> Uses color, speed, and twinkle percent</li>
     *   <li><b>TwinkleOff:</b> Uses color, speed, and twinkle off percent</li>
     * </ul>
     * 
     * @param candle the CANdle device to animate (must not be null)
     * @param strip the LED strip segment to apply the animation to (must not be null)
     * @param color the color to use for color-based animations (must not be null)
     * @param animation the type of animation to create (must not be null)
     * @param config the animation configuration parameters (must not be null)
     * @return the configured Animation object ready to be passed to candle.animate()
     * @throws IllegalArgumentException if any parameter is null
     */
    public Animation createCANdleAnimation(CANdle candle, LEDStrip strip, LEDColor color, AnimationTypes animation, AnimationConfig config) {
        if (candle == null || strip == null || color == null || animation == null || config == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        
        boolean reverse = (config.direction() == Direction.BACKWARD);
        
        switch(animation) {
            case ColorFlow:
                ColorFlowAnimation.Direction flowDir = (config.direction() == Direction.FORWARD) 
                    ? ColorFlowAnimation.Direction.Forward 
                    : ColorFlowAnimation.Direction.Backward;
                return new ColorFlowAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    flowDir, 
                    strip.start
                );
                
            case Fire:
                return new FireAnimation(
                    config.brightness(), 
                    config.speed(), 
                    strip.length(), 
                    config.sparking(), 
                    config.cooling(), 
                    reverse, 
                    strip.start
                );
                
            case Larson:
                LarsonAnimation.BounceMode bounceMode = (config.direction() == Direction.FORWARD) 
                    ? LarsonAnimation.BounceMode.Front 
                    : LarsonAnimation.BounceMode.Back;
                return new LarsonAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    bounceMode, 
                    config.size(), 
                    strip.start
                );
                
            case Rainbow:
                return new RainbowAnimation(
                    config.brightness(), 
                    config.speed(), 
                    strip.length(), 
                    reverse, 
                    strip.start
                );
                
            case RgbFade:
                return new RgbFadeAnimation(
                    config.brightness(), 
                    config.speed(), 
                    strip.length(), 
                    strip.start
                );
                
            case SingleFade:
                return new SingleFadeAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    strip.start
                );
                
            case Strobe:
                return new StrobeAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    strip.start
                );
                
            case Twinkle:
                return new TwinkleAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    config.twinklePercent(), 
                    strip.start
                );
                
            case TwinkleOff:
                return new TwinkleOffAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    config.speed(), 
                    strip.length(), 
                    config.twinkleOffPercent(), 
                    strip.start
                );
                
            default:
                return new StrobeAnimation(
                    color.getRed(), 
                    color.getGreen(), 
                    color.getBlue(), 
                    0, 
                    1.0, 
                    strip.length(), 
                    strip.start
                );
        }
    }

    /**
     * Setting Stuff
     */
    
    /**
     * Creates a command to set the entire LED strip to a solid color.
     * 
     * <p>This command sets all LEDs on the CANdle to the specified color.
     * The command completes instantly.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param color the color to set (must not be null)
     * @return an InstantCommand that sets the strip color
     * @throws IllegalArgumentException if any parameter is null
     */
    public Command setColor(CANdle candle, LEDColor color) {
        if (candle == null || color == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return new InstantCommand(() -> candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue()), this);
    }

    /**
     * Creates a command to set the entire LED strip to a solid color using RGB values.
     * 
     * <p>This is a convenience overload that accepts raw RGB integer values
     * instead of an LEDColor object.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param red the red channel value (0-255)
     * @param green the green channel value (0-255)
     * @param blue the blue channel value (0-255)
     * @return an InstantCommand that sets the strip color
     * @throws IllegalArgumentException if candle is null
     */
    public Command setColor(CANdle candle, int red, int green, int blue) {
        if (candle == null) {
            throw new IllegalArgumentException("CANdle cannot be null");
        }
        return new InstantCommand(() -> candle.setLEDs(red, green, blue), this);
    }

    /**
     * Creates a command to set a specific LED strip segment to a solid color.
     * 
     * <p>This command sets only the LEDs in the specified strip segment,
     * leaving other LEDs unchanged. The command completes instantly.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param strip the LED strip segment to set (must not be null)
     * @param color the color to set (must not be null)
     * @return an InstantCommand that sets the strip segment color
     * @throws IllegalArgumentException if any parameter is null
     */
    public Command setStripColor(CANdle candle, LEDStrip strip, LEDColor color) {
        if (candle == null || strip == null || color == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return new InstantCommand(() -> candle.setLEDs(color.getRed(), color.getGreen(), color.getBlue(), 0, strip.start, strip.length()), this);
    }

    /**
     * Creates a command to set a specific LED strip segment to a solid color using RGB values.
     * 
     * <p>This is a convenience overload that accepts raw RGB integer values
     * instead of an LEDColor object.
     * 
     * @param candle the CANdle device to control (must not be null)
     * @param strip the LED strip segment to set (must not be null)
     * @param red the red channel value (0-255)
     * @param green the green channel value (0-255)
     * @param blue the blue channel value (0-255)
     * @return an InstantCommand that sets the strip segment color
     * @throws IllegalArgumentException if candle or strip is null
     */
    public Command setStripColor(CANdle candle, LEDStrip strip, int red, int green, int blue) {
        if (candle == null || strip == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return new InstantCommand(() -> candle.setLEDs(red, green, blue, 0, strip.start, strip.length()), this);
    }

    /**
     * Animation Stuff
     */
    
    /**
     * Creates a command to animate an LED strip segment with custom configuration.
     * 
     * <p>This command creates and starts an animation on the specified LED strip.
     * The animation will continue running until replaced by another animation
     * or until a solid color is set. The command completes instantly after
     * starting the animation.
     * 
     * <p><b>Note:</b> CANdle devices typically support 2 animation slots. Starting
     * a new animation may replace an existing one depending on slot availability.
     * 
     * @param candle the CANdle device to animate (must not be null)
     * @param strip the LED strip segment to apply the animation to (must not be null)
     * @param color the color to use for color-based animations (must not be null)
     * @param animation the type of animation to run (must not be null)
     * @param config the animation configuration parameters (must not be null)
     * @return an InstantCommand that starts the animation
     * @throws IllegalArgumentException if any parameter is null
     * @see AnimationConfig
     * @see AnimationTypes
     */
    public Command animateStrip(CANdle candle, LEDStrip strip, LEDColor color, AnimationTypes animation, AnimationConfig config) {
        if (candle == null || strip == null || color == null || animation == null || config == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return new InstantCommand(() -> {
            Animation anim = createCANdleAnimation(candle, strip, color, animation, config);
            candle.animate(anim);
        }, this);
    }
    
    /**
     * Creates a command to animate an LED strip segment with default configuration.
     * 
     * <p>This is a convenience method that uses {@link AnimationConfig#defaults()}
     * for animation parameters.
     * 
     * @param candle the CANdle device to animate (must not be null)
     * @param strip the LED strip segment to apply the animation to (must not be null)
     * @param color the color to use for color-based animations (must not be null)
     * @param animation the type of animation to run (must not be null)
     * @return an InstantCommand that starts the animation with default settings
     * @see #animateStrip(CANdle, LEDStrip, LEDColor, AnimationTypes, AnimationConfig)
     */
    public Command animateStrip(CANdle candle, LEDStrip strip, LEDColor color, AnimationTypes animation) {
        return animateStrip(candle, strip, color, animation, AnimationConfig.defaults());
    }

    /**
     * LEDStrip Data Struct
     */
    
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
     * AnimationConfig Data Struct
     */
    
    /**
     * Configuration parameters for LED animations.
     * 
     * <p>This record provides a fluent builder-style API for configuring animation
     * parameters. Different animation types use different subsets of these parameters:
     * 
     * <ul>
     *   <li><b>speed:</b> Animation speed (0.0 to 1.0, higher is faster)</li>
     *   <li><b>direction:</b> Animation direction (FORWARD or BACKWARD)</li>
     *   <li><b>brightness:</b> Overall brightness (0.0 to 1.0)</li>
     *   <li><b>size:</b> Size parameter for Larson animation</li>
     *   <li><b>sparking:</b> Spark generation rate for Fire animation (0.0 to 1.0)</li>
     *   <li><b>cooling:</b> Cooling rate for Fire animation (0.0 to 1.0)</li>
     *   <li><b>twinklePercent:</b> Percentage of LEDs twinkling for Twinkle animation</li>
     *   <li><b>twinkleOffPercent:</b> Percentage of LEDs off for TwinkleOff animation</li>
     * </ul>
     * 
     * <p>Example usage:
     * <pre>
     * AnimationConfig config = AnimationConfig.defaults()
     *     .withSpeed(0.8)
     *     .withBrightness(0.5)
     *     .withDirection(Direction.BACKWARD);
     * </pre>
     * 
     * @param speed animation speed (0.0 to 1.0)
     * @param direction animation direction
     * @param brightness overall brightness (0.0 to 1.0)
     * @param size size parameter for certain animations
     * @param sparking spark generation rate for fire animation (0.0 to 1.0)
     * @param cooling cooling rate for fire animation (0.0 to 1.0)
     * @param twinklePercent percentage of LEDs twinkling
     * @param twinkleOffPercent percentage of LEDs off in twinkle-off animation
     */
    public record AnimationConfig(
        double speed,
        Direction direction,
        double brightness,
        int size,
        double sparking,
        double cooling,
        TwinkleAnimation.TwinklePercent twinklePercent,
        TwinkleOffAnimation.TwinkleOffPercent twinkleOffPercent
    ) {
        /**
         * Creates an AnimationConfig with default values suitable for most animations.
         * 
         * <p>Default values:
         * <ul>
         *   <li>speed: 0.5</li>
         *   <li>direction: FORWARD</li>
         *   <li>brightness: 1.0</li>
         *   <li>size: 3</li>
         *   <li>sparking: 0.7</li>
         *   <li>cooling: 0.5</li>
         *   <li>twinklePercent: Percent42</li>
         *   <li>twinkleOffPercent: Percent100</li>
         * </ul>
         * 
         * @return an AnimationConfig with balanced default parameters
         */
        public static AnimationConfig defaults() {
            return new AnimationConfig(
                0.5,
                Direction.FORWARD,
                1.0,
                3,
                0.7,
                0.5,
                TwinkleAnimation.TwinklePercent.Percent42,
                TwinkleOffAnimation.TwinkleOffPercent.Percent100
            );
        }
        
        /**
         * Creates a new config with the specified speed.
         * 
         * @param speed animation speed (0.0 to 1.0, higher is faster)
         * @return a new AnimationConfig with updated speed
         */
        public AnimationConfig withSpeed(double speed) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified direction.
         * 
         * @param direction animation direction (FORWARD or BACKWARD)
         * @return a new AnimationConfig with updated direction
         */
        public AnimationConfig withDirection(Direction direction) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified brightness.
         * 
         * @param brightness overall brightness (0.0 to 1.0)
         * @return a new AnimationConfig with updated brightness
         */
        public AnimationConfig withBrightness(double brightness) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified size.
         * 
         * @param size size parameter (used by Larson animation)
         * @return a new AnimationConfig with updated size
         */
        public AnimationConfig withSize(int size) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified sparking rate.
         * 
         * @param sparking spark generation rate for fire animation (0.0 to 1.0, higher creates more sparks)
         * @return a new AnimationConfig with updated sparking
         */
        public AnimationConfig withSparking(double sparking) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified cooling rate.
         * 
         * @param cooling cooling rate for fire animation (0.0 to 1.0, higher cools faster)
         * @return a new AnimationConfig with updated cooling
         */
        public AnimationConfig withCooling(double cooling) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified twinkle percentage.
         * 
         * @param percent percentage of LEDs that should twinkle
         * @return a new AnimationConfig with updated twinkle percent
         */
        public AnimationConfig withTwinklePercent(TwinkleAnimation.TwinklePercent percent) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, percent, twinkleOffPercent);
        }
        
        /**
         * Creates a new config with the specified twinkle-off percentage.
         * 
         * @param percent percentage of LEDs that should be off in twinkle-off animation
         * @return a new AnimationConfig with updated twinkle-off percent
         */
        public AnimationConfig withTwinkleOffPercent(TwinkleOffAnimation.TwinkleOffPercent percent) {
            return new AnimationConfig(speed, direction, brightness, size, sparking, cooling, twinklePercent, percent);
        }
        
        /**
         * Creates a preset config for fast animations.
         * 
         * @return an AnimationConfig with speed set to 1.0
         */
        public static AnimationConfig fast() {
            return defaults().withSpeed(1.0);
        }
        
        /**
         * Creates a preset config for slow animations.
         * 
         * @return an AnimationConfig with speed set to 0.2
         */
        public static AnimationConfig slow() {
            return defaults().withSpeed(0.2);
        }
        
        /**
         * Creates a preset config for dim animations.
         * 
         * @return an AnimationConfig with brightness set to 0.3
         */
        public static AnimationConfig dim() {
            return defaults().withBrightness(0.3);
        }
        
        /**
         * Creates a preset config for bright animations.
         * 
         * @return an AnimationConfig with brightness set to 1.0
         */
        public static AnimationConfig bright() {
            return defaults().withBrightness(1.0);
        }
        
        /**
         * Creates a preset config for an intense fire animation.
         * 
         * <p>This preset creates a bright, active fire with lots of sparking
         * and slow cooling for dramatic effect.
         * 
         * @return an AnimationConfig optimized for intense fire effects
         */
        public static AnimationConfig intenseFire() {
            return defaults()
                .withSpeed(0.8)
                .withSparking(0.9)
                .withCooling(0.2)
                .withBrightness(0.7);
        }
        
        /**
         * Creates a preset config for a calm fire animation.
         * 
         * <p>This preset creates a gentle, flickering fire with less sparking
         * and faster cooling for a subtle ambient effect.
         * 
         * @return an AnimationConfig optimized for calm fire effects
         */
        public static AnimationConfig calmFire() {
            return defaults()
                .withSpeed(0.3)
                .withSparking(0.4)
                .withCooling(0.7)
                .withBrightness(0.5);
        }
    }

    /**
     * Colors data Struct
     */
    
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

        /**
         * Private implementation of LEDColor for custom RGB colors.
         */
        private static class CustomColor implements LEDColor {
            private final int red;
            private final int green;
            private final int blue;
        
            private CustomColor(int red, int green, int blue) {
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

    /**
     * AnimationTypes Data Struct
     */
    
    /**
     * Enumeration of available LED animation types.
     * 
     * <p>Each animation type produces a different visual effect:
     * <ul>
     *   <li><b>ColorFlow:</b> A wave of color that flows along the strip</li>
     *   <li><b>Fire:</b> Simulates flickering fire with adjustable intensity</li>
     *   <li><b>Larson:</b> A scanning effect (like KITT from Knight Rider)</li>
     *   <li><b>Rainbow:</b> Displays a moving rainbow pattern</li>
     *   <li><b>RgbFade:</b> Smoothly fades through all RGB colors</li>
     *   <li><b>SingleFade:</b> Fades a single color in and out</li>
     *   <li><b>Strobe:</b> Creates a strobe/flashing effect</li>
     *   <li><b>Twinkle:</b> Random LEDs twinkle with the specified color</li>
     *   <li><b>TwinkleOff:</b> LEDs randomly turn off, creating a twinkling effect</li>
     * </ul>
     * 
     * @see AnimationConfig
     */
    public enum AnimationTypes {
        ColorFlow,
        Fire,
        Larson,
        Rainbow,
        RgbFade,
        SingleFade,
        Strobe,
        Twinkle,
        TwinkleOff
    }

    /**
     * Direction Data Struct
     */
    
    /**
     * Enumeration of animation direction options.
     * 
     * <p>Determines the direction in which animations play:
     * <ul>
     *   <li><b>FORWARD:</b> Animation moves from start to end of the strip</li>
     *   <li><b>BACKWARD:</b> Animation moves from end to start of the strip</li>
     * </ul>
     * 
     * <p>Not all animations support direction. Direction-aware animations include:
     * ColorFlow, Fire, Larson, and Rainbow.
     */
    public enum Direction {
        FORWARD,
        BACKWARD
    }
}