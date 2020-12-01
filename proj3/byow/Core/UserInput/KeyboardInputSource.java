package byow.Core.UserInput;

/**
 * @author hug.
 */
import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = true;

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            } else {
                StdDraw.pause(50);
                return '0';
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
