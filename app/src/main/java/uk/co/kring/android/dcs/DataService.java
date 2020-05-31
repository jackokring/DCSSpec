package uk.co.kring.android.dcs;

import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class DataService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {

        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    public void subscribe(String s) {
        FirebaseMessaging.getInstance().subscribeToTopic(s)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {

                }
            });
    }

    public void unsubscribe(String s) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(s)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {

                }
            });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bundle data = UtilStatic.bundleFromMap(remoteMessage.getData());
        RemoteMessage.Notification n = remoteMessage.getNotification();
        if(n != null) {//foreground notification intercept
            UtilStatic.notify(n.getTitle(), data, n.getBody(), this);
        } else {//not notification but data packet
            onDataPacket(data);
        }
    }

    public void onDataPacket(Bundle data) {

    }

    @Override
    public void onDeletedMessages() {//sync state as offline long time
        super.onDeletedMessages();
    }
}