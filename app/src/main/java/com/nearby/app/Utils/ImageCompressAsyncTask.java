package com.nearby.app.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.nearby.app.ChatRoomActivity;
import com.nearby.app.MessageObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompressAsyncTask extends AsyncTask<String, Void, String> {

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";

    private static final int MAX_IMAGE_SIZE = 70000;
    private String mUsername;
    private boolean mFromUser;

    @Override
    protected String doInBackground(String... strings) {
        if (strings.length == 4) {
            int streamLength = MAX_IMAGE_SIZE;
            int compressionQuality = 55;

            String mFilePath = strings[0];
            mUsername = strings[1];
            mFromUser = !strings[2].equals(FALSE);

            Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
            String bitmapString = "";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while (streamLength >= MAX_IMAGE_SIZE && compressionQuality > 5) {
                try {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compressionQuality -= 5;
                Log.d("compressionQuality", "cQ = " + compressionQuality);
                bitmap.compress(Bitmap.CompressFormat.WEBP, compressionQuality, byteArrayOutputStream);
                streamLength = byteArrayOutputStream.size();
                Log.d("streamLength", "streamLength = " + streamLength);
            }

            if (streamLength < MAX_IMAGE_SIZE) {
                bitmapString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);
            }

            bitmap.recycle();

            return bitmapString;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (!s.isEmpty()) {
            ChatRoomActivity.publishMessage(new MessageObject(mUsername, s, MessageObject.MESSAGE_CONTENT_IMAGE, mFromUser));
        } else {
            KeyboardUtils.showSnackbar("Image too large");
        }
    }
}
