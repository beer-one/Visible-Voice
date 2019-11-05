package com.example.visiblevoice.Data;

import android.os.Environment;

public class AppDataInfo {
    public  static final class File {
        public static final String key = "downloadfile";
        public static final String music_path = "music_path";
        public static final String json = "json";
        public static final String png = "png";
    }
    public static final class CurrentFile{
        public static final String key = "currentfile";
        public static final String filename = "filename";
        public static final String music = "musicfile";
        public static final String json = "jsonfile";
        public static final String png = "pngfile";
    }

    public  static final class Path{
        public static final String VisibleVoiceFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+"VisibleVoice";
    }
    public static final class Login{
        public static final String key = "logininfo";
        public static final String checkbox = "checkbox";
        public static final String userID = "userID";
        public static final String userPwd = "userPwd";
    }
    public static final class Color{
        public static final int selected_lyric = 0xFF2C3C79;
        public static final int lyric = 0xFFFFFFFF;
        public static final int white = 0xFFFFFFFF;
        public static final String gray_string = "0x3F3F3F";
    }

}
