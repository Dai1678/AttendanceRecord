package com.robop.attendancerecord;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SettingClassTimeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Switch alarmSwitch[] = new Switch[5];
    int alarmSwitchIds[] = new int[]{R.id.alarm1_switch, R.id.alarm2_switch, R.id.alarm3_switch, R.id.alarm4_switch, R.id.alarm5_switch};

    TextView endClassTimeText[] = new TextView[5];
    int classTimeIds[] = new int[]{R.id.end_class_time1, R.id.end_class_time2, R.id.end_class_time3, R.id.end_class_time4, R.id.end_class_time5};

    Button setAlarmButton[] = new Button[5];
    int alarmButtonIds[] = new int[]{R.id.set_time_button1, R.id.set_time_button2, R.id.set_time_button3, R.id.set_time_button4, R.id.set_time_button5};

    Calendar alarmCalendar[] = new Calendar[5];

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_time);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(realmConfiguration);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //初期化処理
        for (int i = 0; i< endClassTimeText.length; i++){
            endClassTimeText[i] = findViewById(classTimeIds[i]);
        }

        for (int i=0; i<alarmCalendar.length; i++){
            alarmCalendar[i] = Calendar.getInstance();
        }

        long defaultAlarmTimeInMillis[] = getDefaultAlarmTimeInMillis();

        // 授業終了時刻を表示するTextViewの設定
        RealmResults<EndClassTimeRealmModel> endClassTimeRealmResults = realm.where(EndClassTimeRealmModel.class).findAll();
        if (endClassTimeRealmResults.size() > 0){   //Realmデータあり
            for (int i = 0; i< endClassTimeText.length; i++){
                EndClassTimeRealmModel realmModel = endClassTimeRealmResults.get(i);

                if (realmModel != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(realmModel.getEndClassTimeInMillis());
                    calendar = getCalendarCompareNowTime(calendar);

                    String timeFormatted = dateFormat.format(calendar.getTime());
                    endClassTimeText[i].setText(timeFormatted);

                    alarmCalendar[i] = calendar;
                }else{
                    String timeFormatted = dateFormat.format(defaultAlarmTimeInMillis[i]);
                    endClassTimeText[i].setText(timeFormatted);

                    alarmCalendar[i].setTimeInMillis(defaultAlarmTimeInMillis[i]);
                }
            }
        }else{  //Realmデータなし
            for (int i = 0; i< endClassTimeText.length; i++){
                String timeFormatted = dateFormat.format(defaultAlarmTimeInMillis[i]);
                endClassTimeText[i].setText(timeFormatted);
                initEndClassTimeRealm(i, defaultAlarmTimeInMillis[i]);

                alarmCalendar[i].setTimeInMillis(defaultAlarmTimeInMillis[i]);
                initAlarmTimeInMillisRealm(i, defaultAlarmTimeInMillis[i]);
            }
        }

        for (int i=0; i<setAlarmButton.length; i++){
            setAlarmButton[i] = findViewById(alarmButtonIds[i]);
            setAlarmButton[i].setOnClickListener(this);
        }

        //Switchの初期化処理
        for (int i=0; i<alarmSwitch.length; i++){
            alarmSwitch[i] = findViewById(alarmSwitchIds[i]);
            alarmSwitch[i].setOnCheckedChangeListener(this);
        }

        //Realm参照してSwitchのOn/Offチェック
        RealmResults<AlarmSwitchRealmModel> alarmSwitchRealmResults = realm.where(AlarmSwitchRealmModel.class).findAll();
        if (alarmSwitchRealmResults.size() > 0){
            for (int i=0; i<alarmSwitch.length; i++){
                AlarmSwitchRealmModel realmModel = alarmSwitchRealmResults.get(i);

                if (realmModel != null){
                    alarmSwitch[i].setChecked(realmModel.isAlarmSwitch());
                }else{
                    alarmSwitch[i].setChecked(false);
                }
            }
        }else{
            for (int i=0; i<alarmSwitch.length; i++){
                alarmSwitch[i].setChecked(false);
                initAlarmSwitchRealm(i);
            }
        }
    }

    //Realmデータなし時のSwitch on/off の初期データ挿入
    private void initAlarmSwitchRealm(int id){
        realm.beginTransaction();

        AlarmSwitchRealmModel realmModel = realm.createObject(AlarmSwitchRealmModel.class);
        realmModel.setId(id);
        realmModel.setAlarmSwitch(false);

        realm.commitTransaction();
    }

    //Realmデータなし時の、TextViewに表示する授業終了時刻の初期データ挿入
    private void initEndClassTimeRealm(int id, long defaultTime){
        realm.beginTransaction();

        EndClassTimeRealmModel realmModel = realm.createObject(EndClassTimeRealmModel.class);
        realmModel.setId(id);
        realmModel.setEndClassTimeInMillis(defaultTime);

        realm.commitTransaction();
    }

    //初期の授業終了時間の設定
    private long[] getDefaultAlarmTimeInMillis(){
        int defaultHourOfDay[] = new int[]{10, 12, 15, 17, 18};
        int defaultMinute[] = new int[]{50, 40, 10, 0, 50};
        long defaultAlarmTimeInMillis[] = new long[5];

        Calendar calendar[] = new Calendar[5];
        for (int i=0; i<calendar.length; i++){
            calendar[i] = Calendar.getInstance();
            calendar[i].set(Calendar.HOUR_OF_DAY, defaultHourOfDay[i]);
            calendar[i].set(Calendar.MINUTE, defaultMinute[i]);

            calendar[i] = getCalendarCompareNowTime(calendar[i]); //現在時刻との比較

            defaultAlarmTimeInMillis[i] = calendar[i].getTimeInMillis();
        }

        return defaultAlarmTimeInMillis;
    }

    // Realmデータなし時のアラーム時間の初期データ挿入
    private void initAlarmTimeInMillisRealm(int id, long time){
        realm.beginTransaction();

        AlarmCalendarRealmModel realmModel = realm.createObject(AlarmCalendarRealmModel.class);
        realmModel.setId(id);
        realmModel.setAlarmTimeInMillis(time);

        realm.commitTransaction();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_time_button1:
                setAlarmTimeDialog(0);
                break;

            case R.id.set_time_button2:
                setAlarmTimeDialog(1);
                break;

            case R.id.set_time_button3:
                setAlarmTimeDialog(2);
                break;

            case R.id.set_time_button4:
                setAlarmTimeDialog(3);
                break;

            case R.id.set_time_button5:
                setAlarmTimeDialog(4);
                break;
        }
    }

    // アラーム時間を変更するダイアログを表示 & Realmに保存
    private void setAlarmTimeDialog(int element){
        final Calendar calendarTarget = Calendar.getInstance();     //TimePickerDialogで設定した時刻
        final int hour = calendarTarget.get(Calendar.HOUR_OF_DAY);
        final int minute = calendarTarget.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingClassTimeActivity.this, (TimePicker timePicker, int hourOfDay, int minute1) -> {
            calendarTarget.set(Calendar.DAY_OF_MONTH, calendarTarget.get(Calendar.DAY_OF_MONTH) );
            calendarTarget.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarTarget.set(Calendar.MINUTE, minute1);
            calendarTarget.set(Calendar.SECOND, 0);

            alarmCalendar[element] = getCalendarCompareNowTime(calendarTarget);   //アラーム時間を決定
            long targetMillis = alarmCalendar[element].getTimeInMillis();    //ミリ秒の方も更新

            //TextView更新
            //String alarmTime = String.format("%02d:%02d", hourOfDay, minute1);
            String alarmTime = dateFormat.format(alarmCalendar[element].getTime());
            endClassTimeText[element].setText(alarmTime);

            //TODO registerメソッド呼ぶ
            register(targetMillis, element+1);  //id + 1 = classNum

            //Realmに時間書き込み
            RealmResults<EndClassTimeRealmModel> results = realm.where(EndClassTimeRealmModel.class).equalTo("id", element).findAll();
            EndClassTimeRealmModel realmModel = results.get(0);

            realm.beginTransaction();
            if (realmModel != null){
                realmModel.setEndClassTimeInMillis(targetMillis);
            }
            realm.commitTransaction();

        }, hour, minute, true);
        timePickerDialog.show();
    }

    //現在時刻を過ぎているか確認
    private Calendar getCalendarCompareNowTime(Calendar calendarTarget){

        Calendar calendarNow = Calendar.getInstance();  //現在時刻
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
        calendarNow.setTimeZone(timeZone);

        long targetMillis = calendarTarget.getTimeInMillis();   //TimerPickerDialogで設定した時刻のミリ秒
        long nowMillis = calendarNow.getTimeInMillis();     //現在時刻のミリ秒

        if (targetMillis < nowMillis){
            calendarTarget.add(Calendar.DAY_OF_MONTH, 1);   //過ぎてたら一日増やす
        }

        return calendarTarget;
    }

    //Switchの切り替わり時の処理
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        long alarmTimeInMillis;

        switch (compoundButton.getId()){
            case R.id.alarm1_switch:
                if (isChecked){
                    alarmTimeInMillis = getCalendarCompareNowTime(alarmCalendar[0]).getTimeInMillis();
                    endClassTimeText[0].setText(dateFormat.format(alarmTimeInMillis));
                    register(alarmTimeInMillis, 1);
                }else{
                    unregister(1);
                }
                break;

            case R.id.alarm2_switch:
                if (isChecked){
                    alarmTimeInMillis = getCalendarCompareNowTime(alarmCalendar[1]).getTimeInMillis();
                    endClassTimeText[1].setText(dateFormat.format(alarmTimeInMillis));
                    register(alarmTimeInMillis, 2);
                }else{
                    unregister(2);
                }
                break;

            case R.id.alarm3_switch:
                if (isChecked){
                    alarmTimeInMillis = getCalendarCompareNowTime(alarmCalendar[2]).getTimeInMillis();
                    endClassTimeText[2].setText(dateFormat.format(alarmTimeInMillis));
                    register(alarmTimeInMillis, 3);
                }else{
                    unregister(3);
                }
                break;

            case R.id.alarm4_switch:
                if (isChecked){
                    alarmTimeInMillis = getCalendarCompareNowTime(alarmCalendar[3]).getTimeInMillis();
                    endClassTimeText[3].setText(dateFormat.format(alarmTimeInMillis));
                    register(alarmTimeInMillis, 4);
                }else{
                    unregister(4);
                }
                break;

            case R.id.alarm5_switch:
                if (isChecked){
                    alarmTimeInMillis = getCalendarCompareNowTime(alarmCalendar[4]).getTimeInMillis();
                    endClassTimeText[4].setText(dateFormat.format(alarmTimeInMillis));
                    register(alarmTimeInMillis, 5);
                }else{
                    unregister(5);
                }
                break;
        }
    }

    //アラーム登録
    private void register(long alarmTimeMillis, int classNum){
        AlarmManager alarmManager = (AlarmManager) SettingClassTimeActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(classNum);

        if (alarmManager != null){
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            Log.d("alarmTime", dateFormat.format(alarmTimeMillis));
        }

        updateRealmAlarmSwitch(classNum,true);
    }

    //アラーム削除
    private void unregister(int classNum){
        AlarmManager alarmManager = (AlarmManager) SettingClassTimeActivity.this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getPendingIntent(classNum));
        }

        updateRealmAlarmSwitch(classNum,false);
    }

    //Switchが切り替わった際の状態保存
    private void updateRealmAlarmSwitch(int classNum, boolean flag){
        RealmResults<AlarmSwitchRealmModel> results = realm.where(AlarmSwitchRealmModel.class).equalTo("id", classNum - 1).findAll();
        AlarmSwitchRealmModel realmModel = results.get(0);

        realm.beginTransaction();
        if (realmModel != null){
            realmModel.setAlarmSwitch(flag);
        }
        realm.commitTransaction();
    }

    private PendingIntent getPendingIntent(int classNum){
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("classNum", classNum);
        intent.setClass(this, AlarmReceiver.class);

        return PendingIntent.getBroadcast(this, classNum, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
