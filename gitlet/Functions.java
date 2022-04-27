package gitlet;

import java.awt.*;
import java.io.*;
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

        // save the first commit as head into master branch
        Utils.writeContents(new File(".gitlet/refs/heads/master"), hashCode);
        // add the first commit to the log of the master branch
        Utils.writeContents(new File(".gitlet/logs/refs/heads/master"), hashCode);
        // save the head pointer(aka the master commit's hashCode)
        String head = "master";
        Utils.writeContents(new File(".gitlet/HEAD"), head);

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
        if (blobs.size() == 0) {
            System.out.println("No file at staging area");
            return;
        }
        for (Blob blob: blobs) {
            // modify or add fileName-hashCode kv
            currentCommit.getFiles().put(blob.getName(), Utils.sha1((Object) blob.getContent()));
            // persist the blob to the objects dir
            String hashCode = Utils.sha1((Object) Utils.serialize(blob));
            Utils.writeObject(new File(".gitlet/objects/"+hashCode), blob);
            // after copying this file, delete it from staging area
            if (!new File(".gitlet/stage/"+hashCode).delete()) {
                System.out.println("deleting file failed");
                return;
            }
        }
        // persist the new commit
        String hashCode = Utils.sha1((Object) Utils.serialize(currentCommit));
        Utils.writeObject(new File(".gitlet/objects/"+hashCode), currentCommit);
        // add this commit to the log
        String branch = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        Utils.writeContents(new File(".gitlet/logs/refs/heads/"+branch), "\n"+hashCode);

    }

    /**
     *  Starting at the current head commit, display information about each commit
     *  backwards along the commit tree until the initial commit, following the
     *  first parent commit links, ignoring any second parents found in merge commits.
     *  (In regular Git, this is what you get with git log --first-parent). This set
     *  of commit nodes is called the commit's history. For every node in this history,
     *  the information it should display is the commit id, the time the commit was made,
     *  and the commit message.
     */
    public static void log() {
        // get the branch of the head pointer
        String branch = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        // get the logs of the branch of the head
        List<String> logs = getLogsOfBranch(branch);
        for (int i = logs.size() - 1; i >= 0; i--) {
            // reverse print
            String log = logs.get(i);
            Commit commit = Utils.readObject(new File(".gitlet/objects/"+log), Commit.class);
            System.out.println("===");
            System.out.println("commit "+log);
            System.out.println("Date: "+commit.getTimestamp());
            System.out.println(commit.getMessage());
            System.out.println();
        }
    }

    public static Commit getHead() {
        // get the branch of the head pointer
        String branch = Utils.readContentsAsString(new File(".gitlet/HEAD"));
        String headHash = Utils.readContentsAsString(new File(".gitlet/refs/heads/"+branch));
        return Utils.readObject(new File(".gitlet/objects/"+headHash), Commit.class);
    }


    /**
     * get all the staging files
     */
    public static List<Blob> getStagingFiles(String dir) {
        List<String> fileNames = Utils.plainFilenamesIn(dir);
        List<Blob> res = new ArrayList<>();
        for (String fileName: fileNames) {
            res.add(Utils.readObject(new File(".gitlet/stage/"+fileName), Blob.class));
        }
        return res;
    }

    /**
     * get all the logs of one branch
     */
    public static List<String> getLogsOfBranch(String branch) {
        List<String> logs = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(".gitlet/logs/refs/heads/"+branch);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }
}
