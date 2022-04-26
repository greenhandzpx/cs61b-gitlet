package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

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

    public Commit(String message, Commit parent) {
        this.message = message;
        if (parent == null) {
            // it means that this is to create an init commit
            this.parent = "";
            this.timestamp = " 00:00:00 UTC, Thursday, 1 January 1970";
            this.files = new TreeMap<>();
        } else {
            this.parent = Utils.sha1(parent);
            Date date = new Date();
            String strDateFormat = "HH:mm:ss yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            this.timestamp = sdf.format(date);
            // gets all the files its parent controls
            this.files = parent.getFiles();
        }
    }

}
