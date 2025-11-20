package image;

import java.awt.*;

/**
 * A class that creates a new image with padding.
 */
public class PaddingImage {
    private final Image paddingImage;

    /**
     * Constructor for the PaddingImage class. this function finds the closest power of 2 for the width and
     * height of the image and pads the new image with white pixels around.
     * @param image the image we want to pad.
     */
    public PaddingImage(Image image) {
        int closestPowerOfWidth = getClosesPowerOf2(image.getWidth());
        int closestPowerOfHeight = getClosesPowerOf2(image.getHeight());

        if (closestPowerOfWidth == image.getWidth() && closestPowerOfHeight == image.getHeight()) {
            this.paddingImage = image;
            return;
        }

        Color[][] pixelArray = new Color[closestPowerOfHeight][closestPowerOfWidth];
        int paddingWidth = (closestPowerOfWidth - image.getWidth()) / 2;
        int paddingHeight = (closestPowerOfHeight - image.getHeight()) / 2;

        // Initialize the padded array with white pixels
        for (int row = 0; row < closestPowerOfHeight; row++) {
            for (int col = 0; col < closestPowerOfWidth; col++) {
                pixelArray[row][col] = Color.WHITE;
            }
        }

        // Copy the pixels of the original image into the padded array
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                pixelArray[row + paddingHeight][col + paddingWidth] = image.getPixel(row, col);
            }
        }
        this.paddingImage = new Image(pixelArray, closestPowerOfWidth, closestPowerOfHeight);
    }

    /**
     * this function returns the closest power of 2 for a number.
     * @param num int of the number we want to find the closest power of 2.
     * @return the closest power of 2.
     */
    private int getClosesPowerOf2(int num) {
        int power = 1;
        while (power < num) {
            power *= 2;
        }
        return power;
    }

    /**
     * This function returns the padded image.
     * @return the padding image.
     */
    public Image getPaddingImage() {
        return this.paddingImage;
    }
}
