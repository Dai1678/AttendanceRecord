package com.robop.attendancerecord;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_SEND.equals(intent.getAction())){

            int attendResult = intent.getIntExtra("attendResult", -1);
            int attendClassNum = intent.getIntExtra("classNumResult", -1);

            //現在の曜日を取得
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2; //デフォルト仕様では1 : 日曜 ~ 7:土曜 → 0 : 月 〜 にしてる

            Log.d("attendResult", String.valueOf(attendResult));
            Log.d("classNumResult", String.valueOf(attendClassNum));
            Log.d("dayOfWeek", String.valueOf(dayOfWeek));

            //現在の曜日が日曜以外の時
            if (dayOfWeek != -1){

                Realm.init(context);

                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm realm = Realm.getInstance(realmConfiguration);

                RealmResults<ListRealmModel> results = realm.where(ListRealmModel.class).equalTo("dayOfWeekId", dayOfWeek).and().equalTo("classId", attendClassNum).findAll();
                ListRealmModel realmModel = results.get(0);

                realm.beginTransaction();
                if (realmModel != null){
                    switch (attendResult){
                        case 0:
                            //出席
                            int attendNum = realmModel.getAttendResult() + 1;
                            realmModel.setAttendResult(attendNum);
                            break;

                        case 1:
                            //欠席
                            int absentNum = realmModel.getAbsentResult() + 1;
                            realmModel.setAbsentResult(absentNum);
                            break;

                        case 2:
                            //遅刻
                            int lateNum = realmModel.getLateResult() + 1;
                            realmModel.setLateResult(lateNum);
                            break;
                    }

                    realm.commitTransaction();

                    replyNotification(context, "出欠情報を更新しました");

                }
            }

        } else {
            replyNotification(context, "出欠情報の取得に失敗しました");
        }

    }

    //結果通知
    private void replyNotification(Context context, String description){
        String channelId = "default";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
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
                        .setContentText(description)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .build();

                notificationManager.notify(R.string.app_name, notification);
            }

        }else{
            Notification replyNotification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(description)
                    .build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(1, replyNotification);
        }
    }

}
