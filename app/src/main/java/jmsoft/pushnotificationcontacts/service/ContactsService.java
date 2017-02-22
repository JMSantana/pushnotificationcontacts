package jmsoft.pushnotificationcontacts.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.widget.Toast;

import jmsoft.pushnotificationcontacts.PermissionActivity;
import jmsoft.pushnotificationcontacts.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by joaom on 21/02/2017.
 */

public class ContactsService{

    private Context context;
    private String phoneNumberToSearch;
    private InternalStorageService iss;

    public ContactsService(Context context){
        this.context = context;
    }

    public void searchContact() {
        boolean contactFound = false;

        try{
            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
            while (phones.moveToNext())
            {
                //String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                int phoneContactID = phones.getInt(phones.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(phoneNumberToSearch.equals(phoneNumber)){
                    contactFound = true;

                    storeContactIdToInternalStorage(phoneContactID);

                    openContactDetails(phoneContactID);
                }

            }
            phones.close();

            if(!contactFound){
                clearContactIdFromInternalStorage();

                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.no_contact_found),Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException securityException){
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                Toast.makeText(context, context.getResources().getString(R.string.permission_denied),Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, PermissionActivity.class);
                i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("phoneNumberToSearch", phoneNumberToSearch);
                context.startActivity(i);
                }
            });
        }
    }

    public int getContactIdFromInternalStorage() {
        iss = new InternalStorageService(context);
        String contactId = iss.readFromInternalStorage();

        if(contactId != null && !contactId.isEmpty()){
            return Integer.parseInt(contactId);
        }

        return 0;
    }

    public void clearContactIdFromInternalStorage() {
        iss = new InternalStorageService(context);
        iss.clearFile();
    }

    public void openContactDetails(int phoneContactID) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(phoneContactID));
        i.setData(uri);
        context.startActivity(i);
    }

    public void storeContactIdToInternalStorage(int phoneContactID) {
        iss = new InternalStorageService(context);
        iss.storeToInternalStorage(String.valueOf(phoneContactID));
    }

    public void setPhoneNumberToSearch(String phoneNumberToSearch) {
        this.phoneNumberToSearch = phoneNumberToSearch;
    }
}
