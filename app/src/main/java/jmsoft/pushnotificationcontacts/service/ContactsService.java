package jmsoft.pushnotificationcontacts.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.widget.Toast;

import jmsoft.pushnotificationcontacts.ContactCardActivity;
import jmsoft.pushnotificationcontacts.PermissionActivity;
import jmsoft.pushnotificationcontacts.R;
import jmsoft.pushnotificationcontacts.entity.Contact;

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
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(phoneNumberToSearch.trim().equals(phoneNumber.trim())){
                    contactFound = true;

                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    int phoneContactID = phones.getInt(phones.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

                    Contact contact = new Contact();
                    contact.setId(phoneContactID);
                    contact.setName(name);
                    contact.setPhone(phoneNumber);

                    storeContactIdToInternalStorage(contact);

                    openContactDetails(contact);
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

    public String getContactIdFromInternalStorage() {
        iss = new InternalStorageService(context);
        String contact = iss.readFromInternalStorage();

        if(contact != null && !contact.isEmpty()){
            return contact;
        }

        return "";
    }

    public void clearContactIdFromInternalStorage() {
        iss = new InternalStorageService(context);
        iss.clearFile();
    }

    public void openContactDetails(Contact contact) {
        Intent i = new Intent(context, ContactCardActivity.class);
        i.putExtra("contactId", contact.getId());
        i.putExtra("contactName", contact.getName());
        i.putExtra("contactPhone", contact.getPhone());
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void storeContactIdToInternalStorage(Contact contact) {
        iss = new InternalStorageService(context);
        iss.storeToInternalStorage(contact);
    }

    public void setPhoneNumberToSearch(String phoneNumberToSearch) {
        this.phoneNumberToSearch = phoneNumberToSearch;
    }
}
