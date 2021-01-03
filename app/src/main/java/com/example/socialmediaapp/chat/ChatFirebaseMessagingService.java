package com.example.socialmediaapp.chat;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatFirebaseMessagingService extends FirebaseMessagingService {

  public ChatFirebaseMessagingService() {
    super();
  }

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    super.onMessageReceived(remoteMessage);
    System.out.println("From: " + remoteMessage.getFrom());

    // Check if message contains a data payload.
    if (remoteMessage.getData().size() > 0) {
      System.out.println("Message data payload: " + remoteMessage.getData());

      Intent local = new Intent();
      local.setAction("com.example.socialmediaapp.fcmAction");
      this.sendBroadcast(local);
    }

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      System.out.println("Message Notification Body: " + remoteMessage.getNotification().getBody());
    }
  }
}