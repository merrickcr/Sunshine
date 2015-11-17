package com.example.cmerrick.andevconnotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Button button;

    int mId = 42;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = (Button) view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
                buildAndPostNotifications();
            }
        });
    }

    private void buildAndPostNotifications() {

        NotificationCompat.BigTextStyle bigTextStyle= new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText
            ("loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooool");

        Intent followIntent = new Intent(getActivity(), SecondActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, followIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity())
            .setContentTitle("Notified!")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentText("NOTIFIIIIIIED")
            .setAutoCancel(true)
            .setStyle(bigTextStyle)
            .addAction(android.R.drawable.stat_notify_sync,
                "git it",
                pendingIntent)
            .setVibrate(new long[0])
            //.setPriority(NotificationCompat.PRIORITY_HIGH)
        ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        int notificationID = new Random().nextInt();

        notificationBuilder.setContentIntent(pendingIntent);

        Intent deleteIntent = new Intent(getActivity(), DeleteIntentService.class);
        PendingIntent pendingDeleteService = PendingIntent.getService(getActivity(), 0, deleteIntent ,0);

        notificationBuilder.setDeleteIntent(pendingDeleteService);

        getActivity().findViewById(R.id.hello).postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context
                    .NOTIFICATION_SERVICE);
                mNotificationManager.notify(mId, notificationBuilder.build());
            }
        }, 10000);


//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
//
//        stackBuilder.addParentStack(MainActivity.class);
//
//        stackBuilder.addNextIntent(followIntent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    public PendingIntent getSyncPendingIntent(){
        return null;
    }
}
