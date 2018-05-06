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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SettingClassTimeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Switch alarmSwitch[] = new Switch[5];
    int alarmSwitchIds[] = new int[]{R.id.alarm1_switch, R.id.alarm2_switch, R.id.alarm3_switch, R.id.alarm4_switch, R.id.alarm5_switch};

    TextView endClassTime[] = new TextView[5];
    int classTimeIds[] = new int[]{R.id.end_class_time1, R.id.end_class_time2, R.id.end_class_time3, R.id.end_class_time4, R.id.end_class_time5};

    Button setAlarmButton[] = new Button[5];
    int alarmButtonIds[] = new int[]{R.id.set_time_button1, R.id.set_time_button2, R.id.set_time_button3, R.id.set_time_button4, R.id.set_time_button5};

    Calendar alarmCalendar[] = new Calendar[5];

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

        //授業終わる時間の初期値
        String defaultEndClassTime[] = new String[]{"10:50", "12:40", "15:10", "17:00", "18:50"};

        //初期化処理
        for (int i=0; i<endClassTime.length; i++){
            endClassTime[i] = findViewById(classTimeIds[i]);
        }

        //Realm参照して授業終了時間をTextViewにセット  //TODO Realmから参照するデータをlong型にする
        RealmResults<EndClassTimeRealmModel> endClassTimeRealmResults = realm.where(EndClassTimeRealmModel.class).findAll();
        if (endClassTimeRealmResults.size() > 0){
            for (int i=0; i<endClassTime.length; i++){
                EndClassTimeRealmModel realmModel = endClassTimeRealmResults.get(i);

                if (realmModel != null){
                    endClassTime[i].setText(realmModel.getEndClassTime());

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String timeFormatted = format.format(realmModel.getEndClassTimeInMillis());

                    //alarmCalendar[i].setTimeInMillis(realmModel.getEndClassTimeInMillis()); //TODO これでいけるとおもう

                }else{
                    endClassTime[i].setText(defaultEndClassTime[i]);
                }
            }

        }else{
            for (int i=0; i<endClassTime.length; i++){
                endClassTime[i].setText(defaultEndClassTime[i]);
                initEndClassTimeRealm(i, defaultEndClassTime[i]);
            }
        }

        for (int i=0; i<setAlarmButton.length; i++){
            setAlarmButton[i] = findViewById(alarmButtonIds[i]);
            setAlarmButton[i].setOnClickListener(this);
        }

        /*
        long defaultAlarmTimeInMillis[] = new long[] {};
        //TODO Realmチェック処理
        RealmResults<AlarmCalendarRealmModel> alarmCalendarRealmResults = realm.where(AlarmCalendarRealmModel.class).findAll();
        if (alarmCalendarRealmResults.size() > 0){
            for (int i=0; i<alarmCalendar.length; i++){
                AlarmCalendarRealmModel realmModel = alarmCalendarRealmResults.get(i);

                if (realmModel != null){
                    alarmCalendar[i].setTimeInMillis(realmModel.getAlarmTimeInMillis());
                }else{

                }
            }
        }
        */

        for (int i=0; i<alarmCalendar.length; i++){
            alarmCalendar[i] = Calendar.getInstance();
        }
    }

    //TODO Realmに保存するデータをlong型にする
    private void initEndClassTimeRealm(int id, String defaultTime){
        realm.beginTransaction();

        EndClassTimeRealmModel realmModel = realm.createObject(EndClassTimeRealmModel.class);
        realmModel.setId(id);
        realmModel.setEndClassTime(defaultTime);

        realm.commitTransaction();
    }

    private void initAlarmSwitchRealm(int id){
        realm.beginTransaction();

        AlarmSwitchRealmModel realmModel = realm.createObject(AlarmSwitchRealmModel.class);
        realmModel.setId(id);
        realmModel.setAlarmSwitch(false);

        realm.commitTransaction();
    }

    private void initAlarmCalendarRealm(int id, long time){
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
                setAlarmTime(0);
                break;

            case R.id.set_time_button2:
                setAlarmTime(1);
                break;

            case R.id.set_time_button3:
                setAlarmTime(2);
                break;

            case R.id.set_time_button4:
                setAlarmTime(3);
                break;

            case R.id.set_time_button5:
                setAlarmTime(4);
                break;
        }
    }

    private void setAlarmTime(int element){
        final Calendar calendarTarget = Calendar.getInstance();
        final int hour = calendarTarget.get(Calendar.HOUR_OF_DAY);
        final int minute = calendarTarget.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingClassTimeActivity.this, (TimePicker timePicker, int hourOfDay, int minute1) -> {
            //alarmCalendar[element].set(Calendar.YEAR, calendarTarget.get(Calendar.YEAR));
            //alarmCalendar[element].set(Calendar.MONTH, calendarTarget.get(Calendar.MONTH));
            calendarTarget.set(Calendar.DAY_OF_MONTH, calendarTarget.get(Calendar.DAY_OF_MONTH) -1 );
            calendarTarget.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarTarget.set(Calendar.MINUTE, minute1);
            //alarmCalendar[element].set(Calendar.SECOND, 0);

            //現在時刻を過ぎているかどうか確認
            Calendar calendarNow = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
            calendarNow.setTimeZone(timeZone);

            long targetMillis = calendarTarget.getTimeInMillis();
            long nowMillis = calendarNow.getTimeInMillis();

            if (targetMillis < nowMillis){
                calendarTarget.add(Calendar.DAY_OF_MONTH, 1);
                targetMillis = calendarTarget.getTimeInMillis();
            }

            alarmCalendar[element] = calendarTarget;

            //TextView更新
            String alarmTime = String.format("%02d:%02d", hourOfDay, minute1);
            endClassTime[element].setText(alarmTime);

            //Realmに時間書き込み (TextView表示用) //TODO Realmに書き込むデータをlong型で
            RealmResults<EndClassTimeRealmModel> results = realm.where(EndClassTimeRealmModel.class).equalTo("id", element).findAll();
            EndClassTimeRealmModel realmModel = results.get(0);

            realm.beginTransaction();
            if (realmModel != null){
                realmModel.setEndClassTime(alarmTime);
                realmModel.setEndClassTimeInMillis(targetMillis);
            }
            realm.commitTransaction();

        }, hour, minute, true);
        timePickerDialog.show();
    }

    //Switchの切り替わり時の処理
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.alarm1_switch:
                if (isChecked){
                    register(alarmCalendar[0].getTimeInMillis(), 1);
                }else{
                    unregister(1);
                }
                break;

            case R.id.alarm2_switch:
                if (isChecked){
                    register(alarmCalendar[1].getTimeInMillis(), 2);
                }else{
                    unregister(2);
                }
                break;

            case R.id.alarm3_switch:
                if (isChecked){
                    register(alarmCalendar[2].getTimeInMillis(), 3);
                }else{
                    unregister(3);
                }
                break;

            case R.id.alarm4_switch:
                if (isChecked){
                    register(alarmCalendar[3].getTimeInMillis(), 4);
                }else{
                    unregister(4);
                }
                break;

            case R.id.alarm5_switch:
                if (isChecked){
                    register(alarmCalendar[4].getTimeInMillis(), 5);
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
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
            Log.d("setAlarmTime", dateFormat.format(alarmTimeMillis));
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
