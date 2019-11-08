package com.example.visiblevoice.Data;

public class FileInfo {
    private String filename;
    private boolean isDir;
    private int capacity;
    private static final int SIZE = 1024;
    private int children;

    public FileInfo(String filename, boolean isDir, int capacity) {
        this.filename = filename;
        this.isDir = isDir;
        this.capacity = capacity;
        this.children = 0;
    }

    public void setFilename(String filename) { this.filename = filename; }
    public void setIsDir(boolean isDir) { this.isDir = isDir; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getCapacity() { return capacity; }
    public String getFilename() { return filename; }
    public boolean getisDir() { return isDir; }
    public String getCapacityAsString() {
        double c = capacity;
        if(c < SIZE)
            return c + "B";
        c /= SIZE;
        if(c < SIZE)
            return String.format("%.1fKB", c);
        c /= SIZE;
        if(c < SIZE)
            return String.format("%.1fMB", c);
        c /= SIZE;
        if(c < SIZE)
            return String.format("%.1fGB", c);
        return c+"";
    }
    public void setChildren(int children) {
        this.children = children;
    }
    public int getChildren() { return children; }

}
