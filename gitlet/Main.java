package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author greenhandzpx
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        // FILL THIS IN
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        switch (args[0]) {
            case "init":
                Functions.init();
                break;
            case "add":
                break;
            case "commit":
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }

}
