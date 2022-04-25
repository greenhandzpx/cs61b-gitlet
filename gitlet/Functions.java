package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class Functions {

    public static boolean init() {
        File folder = new File(".gitlet");
        if (folder.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return false;
        }
        if (!folder.mkdir()) {
            System.out.println("Creating .gitlet folder failed.");
            return false;
        }

        // create necessary dirs
        File objects = new File(".gitlet/objects");
        File refs = new File(".gitlet/refs");
        File logs = new File(".gitlet/logs");
        if (!objects.mkdir() || !refs.mkdir() || !logs.mkdir()) {
            System.out.println("create dir error");
            return false;
        }
        File heads = new File(".gitlet/refs/heads");
        refs = new File(".gitlet/logs/refs");
        if (!heads.mkdir() || !refs.mkdir()) {
            System.out.println("create dir error");
            return false;
        }
        heads = new File(".gitlet/logs/refs/heads");
        if (!heads.mkdir()) {
            System.out.println("create dir error");
            return false;
        }

        // create the initial commit
        Commit commit = new Commit("initial commit", null);
        // tmp is just an inter file to get the bytes of the commit
        File tmp = new File(".gitlet/objects/tmp");
        Utils.writeObject(tmp, commit);
        byte[] bytes = Utils.readContents(tmp);
        String hashCode = Utils.sha1((Object) bytes);
        if (!tmp.delete()) {
            System.out.println("Delete failed.");
            return false;
        }
        Utils.writeObject(new File(".gitlet/objects/"+hashCode), commit);

        // save the first commit into master branch
        Utils.writeContents(new File(".gitlet/refs/heads/master"), hashCode);
        // save the head pointer message
        String headMessage = "refs/heads/master";
        Utils.writeContents(new File(".gitlet/HEAD"), headMessage);

        return true;
    }

    public static boolean add(String... files) {
        // get all the files that the head commit controls
        TreeMap<String, String> oldFiles = Branch.HEAD.getCommit().getFiles();
        // This file is used for saving the staging files.
        File fileStage = new File("index");
        if (!fileStage.exists()) {
            try {
                if (!fileStage.createNewFile()) {
                    System.out.println("Creating stage file failed. Try adding again.");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (String file: files) {
            File f = new File(file);
            if (!f.exists()) {
                System.out.println("File does not exist.");
                return false;
            }
            byte[] contents = Utils.readContents(f);
            // calculate the newly added file's hash code
            String newHash = Utils.sha1((Object) contents);
            // calculate the old file's hash code
            String oldHash = oldFiles.get(file);
            if (!newHash.equals(oldHash)) {
                // It means this file is exactly modified.
                // write the new staging file to "index"
                Utils.writeContents(fileStage, (Object) contents);
            }
        }
        return true;
    }

    public static void rm() {

    }

    public static void commit(String message) {
//        Commit commit = new Commit(message, )
    }
}
