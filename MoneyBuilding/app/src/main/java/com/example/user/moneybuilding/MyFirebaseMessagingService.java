package com.example.user.moneybuilding;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    private static final String FCM = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Intent intent = new Intent();
            switch (remoteMessage.getData().get("tag")) {
                case "addMember": {
                    intent.setClass(this, MainTallyBook.class);
                    intent.putExtra("back","Yes");
                    intent.putExtra("tallyBookID",remoteMessage.getData().get("id"));
                }break;
                case "goalAchieved": {
                    intent.setClass(this, MainTallyBook.class);
                    intent.putExtra("back","Yes");
                    intent.putExtra("tallyBookID",remoteMessage.getData().get("id"));
                }break;
                default: {
                    Log.v("switch", "default");
                    intent.setClass(this, HomePage.class);
                }
            }
            startActivity(intent);

        }

    }

}