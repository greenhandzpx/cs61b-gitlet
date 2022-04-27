package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author greenhandzpx
 */
public class Commit implements Serializable {
    /**
     * metadata
     */
    private final String message;
    private String timestamp;

    /**
     * just save the hash code of the parent commit
     */
    private String parent;
    /**
     * used for merge
     */
    private String secondParent;

    /**
     * All the files it controls(filename->hashCode)
     */
    private TreeMap<String, String> files;

    public TreeMap<String, String> getFiles() {
        return files;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Commit(String message, Commit parent) {
        this.message = message;
        if (parent == null) {
            // it means that this is to create an init commit
            this.parent = "";
            this.timestamp = " 00:00:00 UTC, Thursday, 1 January 1970";
            this.files = new TreeMap<>();
        } else {
            this.parent = Utils.sha1((Object) Utils.serialize(parent));
            Date date = new Date();
            this.timestamp = String.format(Locale.US, "%ta %tb %te %tT %tY +0800",
                    date, date, date, date, date);
            // gets all the files its parent controls
            this.files = parent.getFiles();
        }
    }

}
