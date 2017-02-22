package jmsoft.pushnotificationcontacts.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.widget.Toast;

import jmsoft.pushnotificationcontacts.PermissionActivity;
import jmsoft.pushnotificationcontacts.R;

/**
 * Created by joaom on 21/02/2017.
 */

public class ContactsService{

    private Context context;
    private String phoneNumberToSearch;

    public ContactsService(Context context){
        this.context = context;
    }

    private SharedPreferences prefs;

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

                    storeContactId(phoneContactID);

                    openContactDetails(phoneContactID);
                }

            }
            phones.close();

            if(!contactFound){
                clearContactIdFromPreferences();

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
                }
            });
            Intent i = new Intent(context, PermissionActivity.class);
            i.putExtra("phoneNumberToSearch", phoneNumberToSearch);
            context.startActivity(i);
        }
    }

    public int getContactId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt("contactId", 0);
    }

    public void clearContactIdFromPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove("contactId").commit();
    }

    public void openContactDetails(int phoneContactID) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(phoneContactID));
        intent.setData(uri);
        context.startActivity(intent);
    }

    public void storeContactId(int phoneContactID) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt("contactId", phoneContactID).commit();
    }

    public void setPhoneNumberToSearch(String phoneNumberToSearch) {
        this.phoneNumberToSearch = phoneNumberToSearch;
    }
}
