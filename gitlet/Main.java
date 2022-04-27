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
                Functions.add(args);
                break;
            case "commit":
                if (args.length <= 1) {
                    System.out.println("Please add a message.");
                    break;
                }
                Functions.commit(args[1]);
                break;
            case "log":
                Functions.log();
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }

}
