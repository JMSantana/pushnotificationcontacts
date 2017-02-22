package jmsoft.pushnotificationcontacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import jmsoft.pushnotificationcontacts.service.ContactsService;
import jmsoft.pushnotificationcontacts.util.Util;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String phoneNumberToSearch;
    private ContactsService contactsService;

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

        //Setup ContactsService
        contactsService = new ContactsService(this);

        //Get the extras from the intent, if there is a phoneNumber, then, a new push has come
        phoneNumberToSearch = getExtrasFromNotification();

        if(phoneNumberToSearch != null && !phoneNumberToSearch.isEmpty()){
            contactsService.setPhoneNumberToSearch(phoneNumberToSearch);
            if(checkContactPermission()){
                contactsService.searchContact();
            }
        } else {
            //Get the last contactId if it exists
            int contactId = contactsService.getContactIdFromInternalStorage();

            //If there is a last contact, show the details
            if(contactId > 0){
                contactsService.openContactDetails(contactId);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if(phoneNumberToSearch != null && !phoneNumberToSearch.isEmpty() && grantResults[0] == 0){
                    contactsService.searchContact();
                } else {
                    finish();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public boolean checkContactPermission(){
        boolean permissionGranted = true;
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            permissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
        return permissionGranted;
    }
}
