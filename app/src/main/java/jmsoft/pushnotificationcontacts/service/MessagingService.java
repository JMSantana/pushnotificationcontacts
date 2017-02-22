package jmsoft.pushnotificationcontacts.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import jmsoft.pushnotificationcontacts.utils.Util;

public class MessagingService extends FirebaseMessagingService {

    public MessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(Util.getAppTag(), "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload. The phone number should be in the payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(Util.getAppTag(), "Message data payload: " + remoteMessage.getData());
            String phoneNumberToSearch = remoteMessage.getData().get("phoneNumber");

            if(phoneNumberToSearch != null){
                ContactsService contactsService = new ContactsService(getApplicationContext());
                contactsService.setPhoneNumberToSearch(phoneNumberToSearch);
                contactsService.searchContact();
            }
        }

        // Check if message contains a notification payload. Not using the payload this time
        if (remoteMessage.getNotification() != null) {
            Log.d(Util.getAppTag(), "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
