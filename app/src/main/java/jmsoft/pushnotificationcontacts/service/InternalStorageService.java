package jmsoft.pushnotificationcontacts.service;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jmsoft.pushnotificationcontacts.entity.Contact;
import jmsoft.pushnotificationcontacts.util.Util;

/**
 * Created by joao.marco on 22/02/2017.
 */

public class InternalStorageService {

    private String FILENAME = "contactFile";
    private Context mContext;

    public InternalStorageService(Context context) {
        this.mContext = context;
    }

    public void storeToInternalStorage(Contact contact){
        try{
            String string = String.valueOf(contact.getId()) + ":" + contact.getName() + ":" + contact.getPhone();
            FileOutputStream fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }catch(FileNotFoundException e){
            Log.e(Util.getAppTag(), "File contactFile not found");
        }catch(IOException e){
            Log.e(Util.getAppTag(), "Error opening the internal storage file");
        }
    }

    public String readFromInternalStorage(){
        try{
            FileInputStream fis = mContext.openFileInput(FILENAME);
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];

            int n;

            while ((n = fis.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            fis.close();

            return fileContent.toString();
        }catch(FileNotFoundException e){
            Log.e(Util.getAppTag(), "File contactFile not found");
        }catch(IOException e){
            Log.e(Util.getAppTag(), "Error opening the internal storage file");
        }

        return "";
    }

    public boolean clearFile() {
        File dir = mContext.getFilesDir();
        File file = new File(dir, FILENAME);
        return file.delete();
    }
}
