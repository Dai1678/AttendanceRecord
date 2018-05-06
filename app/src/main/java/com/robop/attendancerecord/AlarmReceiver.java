package com.robop.attendancerecord;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int classNum = intent.getIntExtra("classNum", -1);
        Log.d("classNum", String.valueOf(classNum));

        //現在の曜日を取得
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2; //デフォルト仕様では1 : 日曜 ~ 7:土曜 → 0 : 月 〜 にしてる

        //日曜以外に発行   //TODO 教科がないときに通知発行しないようにする
        if (dayOfWeek != -1){

            final int ATTEND = 0, ABSENT = 1, LATE = 2;
            String channelId = "default";
            String title = classNum + "限の授業に出席しましたか？";

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, classNum, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent actionIntent[] = new Intent[3];
            for (int i=0; i<actionIntent.length; i++){
                actionIntent[i] = new Intent(context, NotificationReceiver.class);
                actionIntent[i].setAction(Intent.ACTION_SEND);

                actionIntent[i].putExtra("classNumResult", classNum);
                if (i==0) actionIntent[i].putExtra("attendResult", ATTEND);
                if (i==1) actionIntent[i].putExtra("attendResult", ABSENT);
                if (i==2) actionIntent[i].putExtra("attendResult", LATE);
            }

            PendingIntent actionPendingIntent[] = new PendingIntent[3];
            int pendingRequestCode = 100;   //アラームのほうで1~5まで使っちゃってるので別途定義
            for (int i=0; i<actionPendingIntent.length; i++){
                actionPendingIntent[i] = PendingIntent.getBroadcast(context, pendingRequestCode, actionIntent[i], PendingIntent.FLAG_ONE_SHOT);
            }

            Notification.Action action[] = new Notification.Action[3];
            for (int i=0; i<action.length; i++){
                if (i==0) action[i] = new Notification.Action(0, "出席", actionPendingIntent[i]);
                if (i==1) action[i] = new Notification.Action(0, "欠席", actionPendingIntent[i]);
                if (i==2) action[i] = new Notification.Action(0, "遅刻", actionPendingIntent[i]);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(title);
                channel.enableVibration(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);

                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.setShowBadge(true);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);

                    Notification notification = new Notification.Builder(context, channelId)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText(title)
                            .setAutoCancel(true)
                            .addAction(action[0])
                            .addAction(action[1])
                            .addAction(action[2])
                            .setContentIntent(resultPendingIntent)
                            .setWhen(System.currentTimeMillis())
                            .build();

                    notificationManager.notify(R.string.app_name, notification);
                }
            } else {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle(title);
                builder.setAutoCancel(true);
                builder.addAction(action[0]);
                builder.addAction(action[1]);
                builder.addAction(action[2]);
                builder.setContentIntent(resultPendingIntent);
                builder.setWhen(System.currentTimeMillis());

                builder.build().flags |= Notification.FLAG_AUTO_CANCEL;

                assert notificationManager != null;
                notificationManager.notify(1, builder.build());
            }
        }

    }
}
