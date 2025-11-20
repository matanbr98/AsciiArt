package image_char_matching;

import java.util.HashMap;
import java.util.TreeSet;
/**
 * A class that matches a character to a brightness value.
 */
public class SubImgCharMatcher {
    //attributes
    private HashMap<Character, Double> charBrightnessMap;
    private final TreeSet<Character> charset;
    private final HashMap<Character, Double> brightnessCache;
    private String round;

    //constants
    private static final int DEFAULT_PIXEL_RESOLUTION = 16;
    private static final int NUM_OF_PIXELS = 256;

    /**
     * Constructor for SubImgCharMatcher. Calls the initialize function.Saves the characters from the array
     * to a set and creates a map to
     *      * save characters with their brightness values.
     * @param charset the charset to use.
     */
    public SubImgCharMatcher(char[] charset) {
        this.round = "abs"; //default value
        this.charset = new TreeSet<>();
        this.brightnessCache = new HashMap<>(); // Initialize the cache
        initializeBrightnessMap(charset);
    }

    /**
     * Initialize the brightness map and find the min and max values of brightness, and try to find the
     * brightness val if there is.
     * @param charset the charset to use.
     */
    private void initializeBrightnessMap(char[] charset) {
        double[] charsBrightness = new double[charset.length];
        double minBrightness = 1;
        double maxBrightness = 0;

        // Update the character set and calculate brightness values
        for (int i = 0; i < charset.length; i++) {
            this.charset.add(charset[i]);
            charsBrightness[i] = getCachedBrightness(charset[i]);
            if (charsBrightness[i] < minBrightness) {
                minBrightness = charsBrightness[i];
            }
            if (charsBrightness[i] > maxBrightness) {
                maxBrightness = charsBrightness[i];
            }
        }
        this.charBrightnessMap = createBrightnessMap(this.charset.toArray(new Character[0]), charsBrightness,
                minBrightness, maxBrightness);
    }


    /**
     * Update the brightness map and find the min and max values of brightness.
     */
    private void updateBrightnessMap() {
        double[] charsBrightness = new double[this.charset.size()];
        double minBrightness = 1;
        double maxBrightness = 0;
        int index = 0;

        // Calculate brightness for each character and find min/max values
        for (char c : this.charset) {
//            double brightness = getBrightness(c);
            double brightness = getCachedBrightness(c);
            charsBrightness[index] = brightness;
            if (brightness < minBrightness) {
                minBrightness = brightness;
            }
            if (brightness > maxBrightness) {
                maxBrightness = brightness;
            }
            index++;
        }

        // Update the brightness map with normalized values
        this.charBrightnessMap = createBrightnessMap(this.charset.toArray(new Character[0]), charsBrightness,
                minBrightness, maxBrightness);
    }


    /**
     * Create a map of characters and their brightness values.
     *
     * @param charset the charset to use.
     * @param charsBrightness the brightness values of the characters.
     * @param minBrightness the minimum brightness value.
     * @param maxBrightness the maximum brightness value.
     * @return the map of characters and their brightness values.
     */
    private HashMap<Character, Double> createBrightnessMap(Character[] charset, double[] charsBrightness,
                                                           double minBrightness, double maxBrightness) {
        this.charBrightnessMap = new HashMap<>();
        for (int i = 0; i < charsBrightness.length; i++) {
            double newCharBrightness = (charsBrightness[i] - minBrightness) / (maxBrightness - minBrightness);
            charBrightnessMap.put(charset[i], newCharBrightness);
        }
        return charBrightnessMap;
    }

    /**
     * Get the brightness of a character from the cache in order to save
     * @param c the character to get the brightness of.
     * @return the brightness value of the character.
     */
    private double getCachedBrightness(char c) {
        if (!this.brightnessCache.containsKey(c)) {
            double brightness = getBrightness(c);
            this.brightnessCache.put(c, brightness);
        }
        return this.brightnessCache.get(c);
    }

    /**
     * Get the brightness of a character from the charSet.
     *
     * @param cFromCharSet the character to get the brightness of.
     * @return the normalized brightness value of the character.
     */
    private double getBrightness(char cFromCharSet) {
        boolean[][] array = CharConverter.convertToBoolArray(cFromCharSet);
        int sumTrue = 0;
        for (int i = 0; i < DEFAULT_PIXEL_RESOLUTION; i++) {
            for (int j = 0; j < DEFAULT_PIXEL_RESOLUTION; j++) {
                if (array[i][j]) {
                    sumTrue++;
                }
            }
        }
        return (double) sumTrue / NUM_OF_PIXELS;
    }

    /**
     * Get the character that is closest to the brightness value, if there is a two similar value
     * the character with the lower ASCII value will be returned.
     *
     * @param brightness the brightness value to get the character for.
     * @return the character that is closest to the brightness value.
     */
    public char getCharByImageBrightness(double brightness) {
        // get the first element of the tree.
        char minChar = this.charset.first();
        double minValue = calculateRoundVal(minChar, brightness);
        for (Character c : charBrightnessMap.keySet()) {
            double absoluteVal = calculateRoundVal(c, brightness);
            if (absoluteVal < minValue) {
                minValue = absoluteVal;
                minChar = c;
            } else if (absoluteVal == minValue) {
                if ((int) c < (int) minChar) {
                    minChar = c;
                }
            }
        }
        return minChar;
    }

    /**
     * Add a character to the charset.
     *
     * @param c the character to add.
     */
    public void addChar(char c) {
        this.charset.add(c);
        updateBrightnessMap();
    }

    /**
     * Remove a character from the charset.
     *
     * @param c the character to remove.
     */
    public void removeChar(char c) {
        this.charset.remove(c);
        updateBrightnessMap();
    }

    /**
     * Get the charset.
     *
     * @return the charset.
     */
    public TreeSet<Character> getCharset() {
        return this.charset;
    }

    /**
     * Set the round method according to user input in the shell.
     *
     * @param round the round method.
     */
    public void setRoundSubMatcher(String round) {
        this.round = round;
    }

    /**
     * Calculate the round value according to the round method.
     *
     * @param c the character to calculate the round value for.
     * @param brightness the brightness value to calculate the round value for.
     * @return the round value.
     */
    private double calculateRoundVal(char c, double brightness) {
        if (this.round.equals("abs")) {
            return Math.abs(charBrightnessMap.get(c) - brightness);
        } else if (this.round.equals("up")) {
            Double charBrightness = charBrightnessMap.get(c);
            return charBrightness >= brightness ? charBrightness - brightness : Double.MAX_VALUE;
        } else //this.round.equals("down")
        {
            Double charBrightness = charBrightnessMap.get(c);
            return charBrightness <= brightness ? brightness - charBrightness : Double.MAX_VALUE;
        }
    }
}
