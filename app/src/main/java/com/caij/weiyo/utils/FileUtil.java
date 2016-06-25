package com.caij.weiyo.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Caij on 2016/6/24.
 */
public class FileUtil {

    public static String readAssetsFile(String file, Context context) {
        StringBuffer sb = new StringBuffer();

        try {
            InputStream e = context.getResources().getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(e, "UTF-8"));
            String readLine = null;

            while((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return sb.toString();
    }
}
