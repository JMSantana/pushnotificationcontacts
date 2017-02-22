package jmsoft.pushnotificationcontacts.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import jmsoft.pushnotificationcontacts.ContactCardActivity;
import jmsoft.pushnotificationcontacts.PermissionActivity;
import jmsoft.pushnotificationcontacts.R;
import jmsoft.pushnotificationcontacts.entity.Contact;
import jmsoft.pushnotificationcontacts.util.Util;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by joaom on 21/02/2017.
 */

public class ContactsService{

    private Context mContext;
    private String mPhoneNumberToSearch;
    private InternalStorageService mIss;

    public ContactsService(Context context){
        this.mContext = context;
    }

    public void searchContact() {
        boolean contactFound = false;

        try{
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
            while (phones.moveToNext())
            {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if(mPhoneNumberToSearch != null && mPhoneNumberToSearch.trim().equals(phoneNumber.trim())){
                    contactFound = true;

                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    int phoneContactID = phones.getInt(phones.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

                    Contact contact = new Contact();
                    contact.setId(phoneContactID);
                    contact.setName(name);
                    contact.setPhone(phoneNumber);

                    storeContactToInternalStorage(contact);

                    openContactDetails(contact);
                }

            }
            phones.close();

            if(!contactFound){
                clearContactFromInternalStorage();

                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_contact_found),Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException securityException){
            Handler handler = new Handler(Looper.getMainLooper());

            //If the user did not allow the app to read the contacts yet, ask for the permission
            handler.post(new Runnable() {

                @Override
                public void run() {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_denied),Toast.LENGTH_LONG).show();
                Intent i = new Intent(mContext, PermissionActivity.class);
                i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("phoneNumberToSearch", mPhoneNumberToSearch);
                mContext.startActivity(i);
                }
            });
        }
    }

    public String getContactFromInternalStorage() {
        mIss = new InternalStorageService(mContext);
        String contact = mIss.readFromInternalStorage();

        if(contact != null && !contact.isEmpty()){
            return contact;
        }

        return "";
    }

    public void clearContactFromInternalStorage() {
        mIss = new InternalStorageService(mContext);
        if(mIss.clearFile()){
            Log.i(Util.getAppTag(), "The contact info has been removed from Internal Storage");
        } else {
            Log.d(Util.getAppTag(), "Some error occurred when trying to remove contact info from Internal Storage");
        }

    }

    public void openContactDetails(Contact contact) {
        Intent i = new Intent(mContext, ContactCardActivity.class);
        i.putExtra("contactId", contact.getId());
        i.putExtra("contactName", contact.getName());
        i.putExtra("contactPhone", contact.getPhone());
        i.setFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public void storeContactToInternalStorage(Contact contact) {
        mIss = new InternalStorageService(mContext);
        mIss.storeToInternalStorage(contact);
    }

    public void setPhoneNumberToSearch(String phoneNumberToSearch) {
        this.mPhoneNumberToSearch = phoneNumberToSearch;
    }
}
