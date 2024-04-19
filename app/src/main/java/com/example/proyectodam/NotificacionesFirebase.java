package com.example.proyectodam;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificacionesFirebase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        //Creo una variabe para recoger el titulo de la notificación recibida:
        String titulo = message.getNotification().getTitle();
        //Creo una variable para recoger el cuerpo de la notificación recibida:
        String cuerpo = message.getNotification().getBody();

        //Ahora creo una constante con el nombre del canal a crear:
        final String ID_CHANNEL = "ID CHANNEL";
        //Creo el NotificationManager:
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Configuro el canal:
        NotificationChannel channel = new NotificationChannel(ID_CHANNEL, "Id Channel", NotificationManager.IMPORTANCE_HIGH);
        channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        //Creo el canal:
        notificationManager.createNotificationChannel(channel);

        //Creo el intent que va a tener lugar al pulsar la notificación, en este caso nos llevará a la MainActivity:
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = getActivity(this, 0, intent, FLAG_MUTABLE);

        //Creo el Notification.Builder:
        Notification.Builder notificacion = new Notification.Builder(getApplicationContext(), ID_CHANNEL)
                .setContentText(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        //Se implementa método debido a que obliga al usar NotificationManagerCompat:
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG","Permiso denegado.");
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notificacion.build());

        super.onMessageReceived(message);
    }

}
