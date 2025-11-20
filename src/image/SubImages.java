package image;

import java.awt.*;

/**
 * A class that creates sub images of an image.
 */
public class SubImages {
    private Image[][] subImages;
    private final Image originalImage;
    private final int resolution;

    private static final int MAX_RGB = 255;

    /**
     * Constructor that creates a new SubImages object.
     * @param resolution the resolution of the sub image.
     * @param image the image to create the sub images of.
     */
    public SubImages(int resolution, Image image) {
        this.subImages = null;
        this.originalImage = image;
        this.resolution = resolution;
    }

    /**
     * This function sets the sub images of the image.
     */
    public void setSubImage() {
        int subImageSize = this.originalImage.getWidth() / resolution;
        int rows = this.originalImage.getHeight() / subImageSize;
        int cols = resolution;

        this.subImages = new Image[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                this.subImages[row][col] = extractSubImage(row, col, subImageSize);
            }
        }
    }

    /**
     * This function extracts a sub image from the original image.
     * @param row the row of the sub image.
     * @param col the column of the sub image.
     * @param subImageSize the size of the sub image.
     * @return the sub image.
     */
    private Image extractSubImage(int row, int col, int subImageSize) {
        Color[][] pixelsArray = new Color[subImageSize][subImageSize];

        int startX = row * subImageSize;
        int startY = col * subImageSize;

        for (int i = 0; i < subImageSize; i++) {
            for (int j = 0; j < subImageSize; j++) {
                pixelsArray[i][j] = this.originalImage.getPixel(startX + i, startY + j);
            }
        }

        return new Image(pixelsArray, subImageSize, subImageSize);
    }

    /**
     * This function calculates the brightness of the image.
     * @param image the image to calculate the brightness of.
     * @return the brightness of the image.
     */
    public double calculateBrightnessImage (Image image){
        double sumGrey = 0.0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                double greyPixel = image.getPixel(i, j).getRed() * 0.2126 +
                        image.getPixel(i, j).getGreen() * 0.7152 + image.getPixel(i, j).getBlue() * 0.0722;
                sumGrey += greyPixel;
            }
        }
        return sumGrey / (image.getWidth() * image.getHeight()) / MAX_RGB;
    }

    /**
     * This function returns 2 array list of images.
     * @return the sub images.
     */
    public Image[][] getSubImages() {
        return this.subImages;
    }
}

