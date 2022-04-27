package gitlet;

import java.io.Serializable;

/**
 * The content of one file
 */
public class Blob implements Serializable {
    /**
     * file name
     */
    private final String name;
    private final byte[] content;

    public Blob(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }
    public byte[] getContent() {
        return content;
    }
}
