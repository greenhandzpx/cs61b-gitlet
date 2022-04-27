package gitlet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
        File stage = new File(".gitlet/stage");
        if (!objects.mkdir() || !refs.mkdir() || !logs.mkdir() || !stage.mkdir()) {
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
        String hashCode = Utils.sha1((Object) Utils.serialize(commit)) ;
        Utils.writeObject(new File(".gitlet/objects/"+hashCode), commit);

        // save the first commit into master branch
        Utils.writeContents(new File(".gitlet/refs/heads/master"), hashCode);
        // save the head pointer(aka the master commit's hashCode)
        Utils.writeContents(new File(".gitlet/HEAD"), hashCode);

        return true;
    }

    public static boolean add(String... files) {
        if (files.length <= 1) {
            return true;
        }
        // get all the files that the head commit controls
        Commit head = getHead();
        TreeMap<String, String> oldFiles = head.getFiles();
        boolean first = true;
        for (String file: files) {
            if (first) {
                first = false;
                continue;
            }
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
                // write the new staging Blob object to the staging area
                Blob blob = new Blob(file, contents);
                String hashCode = Utils.sha1((Object) Utils.serialize(blob));
                Utils.writeObject(new File(".gitlet/stage/"+hashCode), blob);
            }
        }
        return true;
    }

    public static void rm() {

    }

    public static void commit(String message) {
        Commit head = getHead();
        Commit currentCommit = new Commit(message, head);
        // get all the staging files
        List<Blob> blobs = getStagingFiles(".gitlet/stage");
        for (Blob blob: blobs) {
            // modify or add fileName-hashCode kv
            currentCommit.getFiles().put(blob.getName(), Utils.sha1((Object) blob.getContent()));
            // persist the blob to the objects dir
            String hashCode = Utils.sha1((Object) Utils.serialize(blob));
            Utils.writeObject(new File(".gitlet/objects/"+hashCode), blob);
        }
        // persist the new commit
        String hashCode = Utils.sha1((Object) Utils.serialize(currentCommit));
        Utils.writeObject(new File(".gitlet/objects/"+hashCode), currentCommit);
    }

    public static Commit getHead() {
        String headHash = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        return Utils.readObject(new File(".gitlet/objects/"+headHash), Commit.class);
    }


    /**
     * get all the staging files
     */
    public static List<Blob> getStagingFiles(String dir) {
        List<String> fileNames = Utils.plainFilenamesIn(dir);
        List<Blob> res = new ArrayList<>();
        for (String fileName: fileNames) {
            res.add(Utils.readObject(new File(fileName), Blob.class));
        }
        return res;
    }
}
