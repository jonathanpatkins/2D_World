package byow.Core;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 * @author Jonathan Atkins Jake Webster CS61B Staff 11/12/20.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Engine engine = new Engine();
            engine.interactWithInputString(args[0], false);
            System.out.println(engine.toString());

            // once done with all of that quit the program
            System.exit(0);
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
            // once done with all of that quit the program
            System.exit(0);
        }
    }
}
