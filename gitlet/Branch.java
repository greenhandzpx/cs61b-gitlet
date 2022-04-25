package gitlet;

import java.util.HashMap;
import java.util.TreeMap;

public class Branch {
    private final String branchName;
    private Commit commit;

    public Branch(String branchName, Commit commit) {
        this.branchName = branchName;
        this.commit = commit;
    }

    public String getBranchName() {
        return branchName;
    }

    public Commit getCommit() {
        return commit;
    }


    /**
     * store all the branches
     */
    public static TreeMap<String, Branch> branches;

    /**
     * HEAD pointer
     */
    public static Branch HEAD;

    public static void changeBranch(String branchName) {

    }
}
