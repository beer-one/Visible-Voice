package com.example.visiblevoice.Data;

public class CurrentDownload {
    private String file_name;
    private String music_path;
    private String json_path;
    private String png_path;
    private boolean checked;

    public CurrentDownload(String file_name,String music_path, String json_path, String png_path) {
        this.file_name = file_name;
        this.music_path = music_path;
        this.json_path = json_path;
        this.png_path = png_path;
        this.checked = false;
    }

    public boolean ischecked() {
        return checked;
    }

    public void setchecked(boolean checked) {
        this.checked = checked;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getMusic_path() {
        return music_path;
    }

    public String getJson_path() {
        return json_path;
    }

    public String getPng_path() {
        return png_path;
    }
    public boolean getChecked() { return checked; }
}
