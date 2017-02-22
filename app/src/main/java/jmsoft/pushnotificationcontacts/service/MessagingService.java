package jmsoft.pushnotificationcontacts.service;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import jmsoft.pushnotificationcontacts.utils.Util;

public class MessagingService extends FirebaseMessagingService {

    public MessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(Util.getAppTag(), "From: " + remoteMessage.getFrom());
        Toast.makeText(getApplicationContext(), remoteMessage.getFrom(), Toast.LENGTH_LONG).show();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(Util.getAppTag(), "Message data payload: " + remoteMessage.getData());
            Toast.makeText(getApplicationContext(), "Message data payload: " + remoteMessage.getData(), Toast.LENGTH_LONG).show();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(Util.getAppTag(), "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Toast.makeText(getApplicationContext(), "Message Notification Body: " + remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
