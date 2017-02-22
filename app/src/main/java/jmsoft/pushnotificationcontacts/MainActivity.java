package jmsoft.pushnotificationcontacts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import jmsoft.pushnotificationcontacts.utils.Util;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String phoneNumberToSearch;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Token button declaration and listener setup
        Button btn = (Button) findViewById(R.id.buttonToken);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tkn = FirebaseInstanceId.getInstance().getToken();
                Toast.makeText(MainActivity.this, "Current token ["+tkn+"]",
                        Toast.LENGTH_LONG).show();
                Log.d(Util.getAppTag(), "Token ["+tkn+"]");
            }
        });

        //Get the extras from the intent, if there is a phoneNumber, then, a new push has come
        phoneNumberToSearch = getExtrasFromNotification();

        if(phoneNumberToSearch != null && !phoneNumberToSearch.isEmpty()){
            searchContact();
        } else {
            //Get the last contactId if it exists
            int contactId = getContactId();

            //If there is a last contact, show the details
            if(contactId > 0){
                openContactDetails(contactId);
            }
        }
    }

    private String getExtrasFromNotification() {
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            return extras.getString("phoneNumber");
        }

        return null;
    }

    private void searchContact() {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            boolean contactFound = false;

            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
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
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private int getContactId() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt("contactId", 0);
    }

    private void clearContactIdFromPreferences() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("contactId").commit();
    }

    private void openContactDetails(int phoneContactID) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(phoneContactID));
        intent.setData(uri);
        startActivity(intent);
    }

    private void storeContactId(int phoneContactID) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt("contactId", phoneContactID).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if(phoneNumberToSearch != null && !phoneNumberToSearch.isEmpty()){
                    searchContact();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
