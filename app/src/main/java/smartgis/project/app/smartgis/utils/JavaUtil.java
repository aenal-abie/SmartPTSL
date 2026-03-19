package smartgis.project.app.smartgis.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUtil {
    public static int getOnlyNumber(String data) {
        return Integer.valueOf(data.replaceAll("[a-zA-Z]", "").trim());
    }

    public static String cleanUpMessage(String message) {
        Pattern xx = Pattern.compile("\\$([^*$]*)\\*([0-9A-F][0-9A-F])?\r\n");
        Matcher m = xx.matcher(message);
        return m.group(1);
    }

    public static void openPathInFileManager(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        context.startActivity(intent);
    }

    public static String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("FILE", "File not found :", e);
        } catch (IOException e) {
            Log.e("FILE", "Can not read file :", e);
        }

        return ret;
    }

}