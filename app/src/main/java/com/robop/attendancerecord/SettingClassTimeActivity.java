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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class SettingClassTimeActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Switch alarmSwitch[] = new Switch[5];
    int alarmSwitchIds[] = new int[]{R.id.alarm1_switch, R.id.alarm2_switch, R.id.alarm3_switch, R.id.alarm4_switch, R.id.alarm5_switch};

    TextView endClassTime[] = new TextView[5];
    int classTimeIds[] = new int[]{R.id.end_class_time1, R.id.end_class_time2, R.id.end_class_time3, R.id.end_class_time4, R.id.end_class_time5};

    Button setAlarmButton[] = new Button[5];
    int alarmButtonIds[] = new int[]{R.id.set_time_button1, R.id.set_time_button2, R.id.set_time_button3, R.id.set_time_button4, R.id.set_time_button5};

    Calendar alarmCalendar[] = new Calendar[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_time);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        for (int i=0; i<alarmSwitch.length; i++){
            alarmSwitch[i] = findViewById(alarmSwitchIds[i]);
            alarmSwitch[i].setOnCheckedChangeListener(this);
        }

        for (int i=0; i<endClassTime.length; i++){
            endClassTime[i] = findViewById(classTimeIds[i]);
        }

        for (int i=0; i<setAlarmButton.length; i++){
            setAlarmButton[i] = findViewById(alarmButtonIds[i]);
            setAlarmButton[i].setOnClickListener(this);
        }

        for (int i=0; i<alarmCalendar.length; i++){
            alarmCalendar[i] = Calendar.getInstance();
        }

        //TODO prefチェック

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
        final int year = calendarTarget.get(Calendar.YEAR);
        final int monthOfYear = calendarTarget.get(Calendar.MONTH);
        final int dayOfMonth = calendarTarget.get(Calendar.DAY_OF_MONTH);
        final int hour = calendarTarget.get(Calendar.HOUR_OF_DAY);
        final int minute = calendarTarget.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingClassTimeActivity.this, (timePicker, hourOfDay, minute1) -> {
            alarmCalendar[element].set(Calendar.YEAR, year);
            alarmCalendar[element].set(Calendar.MONTH, monthOfYear);
            alarmCalendar[element].set(Calendar.DAY_OF_MONTH, dayOfMonth-1);
            alarmCalendar[element].set(Calendar.HOUR_OF_DAY, hourOfDay);
            alarmCalendar[element].set(Calendar.MINUTE, minute1);
            alarmCalendar[element].set(Calendar.SECOND, 0);

            //現在時刻を過ぎているかどうか確認
            Calendar calendarNow = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
            calendarNow.setTimeZone(timeZone);

            long targetMillis = calendarTarget.getTimeInMillis();
            long nowMillis = calendarNow.getTimeInMillis();

            if (targetMillis < nowMillis){
                alarmCalendar[element].add(Calendar.DAY_OF_MONTH, 1);
            }

            endClassTime[element].setText(String.format("%02d:%02d", hourOfDay, minute1));
        }, hour, minute, true);
        timePickerDialog.show();
    }

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

    private void register(long alarmTimeMillis, int classNum){
        AlarmManager alarmManager = (AlarmManager) SettingClassTimeActivity.this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(classNum);

        if (alarmManager != null){
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
            Log.d("setAlarmTime", dateFormat.format(alarmTimeMillis));
        }

        //TODO pref保存
    }

    private void unregister(int classNum){
        AlarmManager alarmManager = (AlarmManager) SettingClassTimeActivity.this.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getPendingIntent(classNum));
        }

        //TODO pref削除
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
}
