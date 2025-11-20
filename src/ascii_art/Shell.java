package ascii_art;

import ascii_art.exceptions.OutOfImageSizeException;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.PaddingImage;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the shell of the program. It is responsible for
 * handling user input and running the program.
 */
public class Shell {
    private final SubImgCharMatcher subImgCharMatcher;
    private int resolution;
    private final String defaultOutput;
    private String round;
    private AsciiOutput output;

    private static final int MIN_ASCII = 32;
    private static final int MAX_ASCII = 126;
    private static final int DEFAULT_RES = 2;


    /**
     * This is the constructor for the Shell class.
     */
    public Shell() {
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        this.subImgCharMatcher = new SubImgCharMatcher(chars);
        this.resolution = DEFAULT_RES;
        this.defaultOutput = "console";
        this.round = "abs";
        this.output = new ConsoleAsciiOutput();
    }

    /**
     * This function runs the shell. It takes in user input and accordingly runs the program. It also handles
     * exceptions caused by incorrect user input.
     * @param imageName the name of the image.
     * @throws IOException if the user input is incorrect / the charset is too small / the character is not
     * valid / the resolution is not valid / the rounding method is not valid / the output method is not
     * valid / request is not one of the above.
     * @throws OutOfImageSizeException if the resolution exceeds the boundaries.
     */
    public void run(String imageName) throws IOException, OutOfImageSizeException {
        // Create a new AsciiArtAlgorithm object
        String inputFromU;
        do {
            System.out.print(">>> ");
            inputFromU = KeyboardInput.readLine();
            Image orgImage = null;
            Image image = null;
            try {
                orgImage = new Image(imageName);
                PaddingImage paddingImage = new PaddingImage(orgImage);
                image = paddingImage.getPaddingImage();
            } catch (IOException e) {
                throw new IOException(e);
            }
            if (inputFromU.toLowerCase().startsWith("chars")) {
                printChars();
            } else if (inputFromU.toLowerCase().startsWith("add")) {
                try { addCharToSet(inputFromU.split(" "));
                } catch (IOException e) {
                    System.out.println("Did not add due to incorrect format.");
                }
            } else if (inputFromU.toLowerCase().startsWith("remove")) {
                try { removeCharFromSet(inputFromU.split(" "));
                } catch (IOException e) {
                    System.out.println("Did not remove due to incorrect format.");
                }
            } else if (inputFromU.toLowerCase().startsWith("res")) {
                try { setResolution((inputFromU.split(" ")), image);
                    System.out.println("Resolution set to " + this.resolution + ".");
                } catch (OutOfImageSizeException e) {
                    System.out.println("Did not change resolution due to exceeding boundaries.");
                } catch (IOException e) {
                    System.out.println("Did not change resolution due to incorrect format.");
                }
            } else if (inputFromU.toLowerCase().startsWith("round")){
                try {
                    setRound(inputFromU.split(" "));
                } catch (IOException e) {
                    System.out.println("Did not change rounding method due to incorrect format.");
                }
            } else if (inputFromU.toLowerCase().startsWith("output")) {
                try {
                    setOutput(inputFromU.split(" "));
                } catch (IOException e) {
                    System.out.println("Did not change output method due to incorrect format.");
                }
            }
            else if(inputFromU.startsWith("asciiArt")){

                try {
                    runAlgorithmAscii(image);
                } catch (IOException e) {
                    System.out.println("Did not execute. Charset is too small.");
                }
            } else if (inputFromU.toLowerCase().startsWith("exit")) {
                System.exit(0);

            } else throw new IOException();

        }
        while (!inputFromU.toLowerCase().startsWith("exit"));
    }

    /**
     * This function runs the ASCII art algorithm according to the image, resolution, charset and rounding
     * method that the user decided.
     * @param image the image.
     * @throws IOException if the charset is too small.
     */
    private void runAlgorithmAscii(Image image) throws IOException {
        if (this.subImgCharMatcher.getCharset().size() < 2) {
            throw new IOException();
        }
        AsciiArtAlgorithm asciiArtAlgorithm = new
                AsciiArtAlgorithm(image, this.resolution, this.subImgCharMatcher, this.round);
        char[][] asciiArt = asciiArtAlgorithm.run();
        this.output.out(asciiArt);
    }

    /** This function prints the characters that the algorithm uses to create the ASCII art. */
    private void printChars() {
        List<Character> sortedChars = new ArrayList<>(this.subImgCharMatcher.getCharset());
        for (Character c : sortedChars) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    /** This function adds a character to the set of characters that the algorithm
     *  uses to create the ASCII art.
     * @param words the character to add to the set.
     * @throws IOException if the character is not valid.
     */
    private void addCharToSet (String[] words) throws IOException{
        String option = "";
        if (words.length != 1) {
            option = words[1];
        }
        if (option.length() == 1) {
            if (option.charAt(0) < MIN_ASCII || option.charAt(0) > MAX_ASCII) {
                throw new IOException();
            }
            this.subImgCharMatcher.addChar(option.charAt(0));
        } else if (option.equalsIgnoreCase("all")) {
            for (int i = MIN_ASCII; i <= MAX_ASCII; i++) {
                char currentChar = (char) i;
                this.subImgCharMatcher.addChar(currentChar);
            }
        } else if (option.equalsIgnoreCase("space")) {
            this.subImgCharMatcher.addChar(' ');
        } else if (option.contains("-")) {
            String[] range = option.split("-");
            char firstChar = range[0].charAt(0);
            char lastChar = range[1].charAt(0);
            while (firstChar != lastChar) {
                if (firstChar < lastChar) {
                    this.subImgCharMatcher.addChar(firstChar);
                    firstChar++;
                } else {
                    this.subImgCharMatcher.addChar(firstChar);
                    firstChar--;
                }
            }
            this.subImgCharMatcher.addChar(lastChar);
        } else {
            throw new IOException();
        }
    }

    /** This function removes a character from the set of characters that the algorithm
     *  uses to create the ASCII art.
     * @param words the character to remove from the set.
     * @throws IOException if the character is not valid.
     */
    private void removeCharFromSet (String[] words) throws IOException{
        String option = "";
        if (words.length != 1) {
            option = words[1];
        }
        if (option.length() == 1) {
            if (option.charAt(0) < MIN_ASCII || option.charAt(0) > MAX_ASCII) {
                throw new IOException();
            }
            this.subImgCharMatcher.removeChar(option.charAt(0));
        } else if (option.equalsIgnoreCase("all")) {
            for (int i = MIN_ASCII; i <= MAX_ASCII; i++) {
                char currentChar = (char) i;
                this.subImgCharMatcher.removeChar((char) (currentChar + '0'));
            }
        } else if (option.equalsIgnoreCase("space")) {
            this.subImgCharMatcher.removeChar(' ');
        } else if (option.contains("-")) {
            String[] range = option.split("-");
            char firstChar = range[0].charAt(0);
            char lastChar = range[1].charAt(0);
            while (firstChar != lastChar) {
                if (firstChar < lastChar) {
                    this.subImgCharMatcher.removeChar(firstChar);
                    firstChar++;
                } else {
                    this.subImgCharMatcher.removeChar(firstChar);
                    firstChar--;
                }
            }
            this.subImgCharMatcher.removeChar(lastChar);
        } else {
            throw new IOException();
        }
    }

    /**
     * This function sets the resolution if possible.
     * @param words the new resolution.
     * @param image the image.
     * @throws OutOfImageSizeException if the rise of lower the resolution is out of image bounds.
     * @throws IOException if the change of resolution is not valid.
     */
    private void setResolution(String[] words, Image image) throws IOException, OutOfImageSizeException {
        int minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
        int maxCharsInRow = image.getWidth();
        String newRes = "";
        if (words.length != 1) {
            newRes = words[1];
        }
        if (newRes.equalsIgnoreCase("up")) {
            if(this.resolution * 2 > maxCharsInRow) {
                throw new OutOfImageSizeException();
            }
            this.resolution *= 2;
        } else if (newRes.equalsIgnoreCase("down")) {
            if(this.resolution / 2 < minCharsInRow) {
                throw new OutOfImageSizeException();
            }
            this.resolution /= 2;
        } else if (!newRes.equals("")) {
            throw new IOException();
        }
    }

    /**
     * This function sets the rounding method.
     * @param words the new rounding method.
     * @throws IOException if the rounding method is not valid.
     */
    private void setRound(String[] words) throws IOException{
        String newRound = "";
        if (words.length != 1) {
            newRound = words[1];
        }
        if (newRound.equalsIgnoreCase("abs")) {
            this.subImgCharMatcher.setRoundSubMatcher("abs");
            this.round = "abs";
        } else if (newRound.equalsIgnoreCase("up")) {
            this.subImgCharMatcher.setRoundSubMatcher("up");
            this.round = "up";
        } else if (newRound.equalsIgnoreCase("down")) {
            this.subImgCharMatcher.setRoundSubMatcher("down");
            this.round = "down";
        } else {
            throw new IOException();
        }
    }

    /**
     * This function sets the output method.
     * @param words the new output method.
     * @throws IOException if the output method is not valid.
     */
    private void setOutput(String[] words) throws IOException{
        String output = "";
        if (words.length != 1) {
            output = words[1];
        }
        if (output.equalsIgnoreCase("console")) {
            ConsoleAsciiOutput consoleOut = new ConsoleAsciiOutput();
            this.output = consoleOut;
        } else if (output.equalsIgnoreCase("html")) {
            HtmlAsciiOutput htmlOut = new HtmlAsciiOutput("out.html", "Courier New");
            this.output = htmlOut;
        } else {
            throw new IOException();
        }
    }

    /**
     * This function is the main function of the program. It creates a new shell and runs it.
     * @param args the arguments.
     */
    public static void main(String[] args){
        Shell shell = new Shell();
        try {
            shell.run(args[0]);
        } catch (IOException | OutOfImageSizeException e) {
            System.out.println("Did not execute due to incorrect command.");
        }
    }
}
