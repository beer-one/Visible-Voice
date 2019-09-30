package com.example.visiblevoice.Data;

import android.os.Environment;

public class AppDataInfo {
    public  static final class File{
        public static final String key ="downloadfile";
        public static final String json = "json";
        public static final String png = "png";
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

}
