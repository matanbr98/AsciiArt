package ascii_art;

import image.*;
import image_char_matching.SubImgCharMatcher;

/**
 * this class runs the ascii art algorithm on some image, resolution, with type of round.
 */
public class AsciiArtAlgorithm {
    private final Image originalImage;
    private final int resolution;
    private final SubImgCharMatcher subImgCharMatcher;
    private String round;


    /**
     * constructor for the class that set the values.
     * @param image the image we want to run the algorithm on.
     * @param resolution the wanted resolution.
     * @param charset the set of chars.
     * @param round the wanted ran method.
     */
    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher charset, String round){
            this.originalImage = image;
            this.resolution = resolution;
            this.subImgCharMatcher = charset;
            this.round = round;
        }

    /**
     * This function runs the algorithm to create the ascii art. find the closet char to the brightness of
     * the image.
     * @return the ascii art.
     */
    public char[][] run() {
        SubImages subImages = new SubImages(resolution, originalImage);
        subImages.setSubImage();
        Image[][] subImageArray = subImages.getSubImages();

        int rows = subImageArray.length;
        int cols = subImageArray[0].length;
        char[][] asciiArt = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double brightness = subImages.calculateBrightnessImage(subImageArray[i][j]);
                asciiArt[i][j] = subImgCharMatcher.getCharByImageBrightness(brightness);
            }
        }
        return asciiArt;
    }
}
