package com.robop.attendancerecord;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity  {

    Toolbar toolbar;

    ViewPager viewPager;
    TabLayout tabLayout;

    CustomFragmentPagerAdapter customFragmentAdapter;

    int[] endTimeHourGroup;     //通知時間の時間部分をまとめた配列
    int[] endTimeMinuteGroup;   //通知時間の分部分をまとめた配列

    final int INTENT_REQUEST_CODE = 1;  //通知設定(EndTimeActivity)へStartActivityForResultする際のCODE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        String[] tabNames = getResources().getStringArray(R.array.tabNames);    //TabLayoutに表示する文字を管理する配列

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pager);

        toolbar.setTitle("曜日");
        setSupportActionBar(toolbar);

        initEndTimeArray();     //通知時間管理配列の初期化

        //曜日の数だけFragment生成
        customFragmentAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), tabNames);
        for(int i=0; i<6; i++){
            customFragmentAdapter.addFragment(ScheduleFragment.newInstance());
        }

        viewPager.setAdapter(customFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        setNotificationTime();     //通知設定処理
    }

    private void initEndTimeArray(){
        endTimeHourGroup = new int[5];
        endTimeHourGroup[0] = 10;
        endTimeHourGroup[1] = 12;
        endTimeHourGroup[2] = 15;
        endTimeHourGroup[3] = 17;
        endTimeHourGroup[4] = 18;

        endTimeMinuteGroup = new int[5];
        endTimeMinuteGroup[0] = 50;
        endTimeMinuteGroup[1] = 40;
        endTimeMinuteGroup[2] = 10;
        endTimeMinuteGroup[3] = 00;
        endTimeMinuteGroup[4] = 50;
    }

    @Override
    public void onRestart(){
        super.onRestart();

        setNotificationTime();
        //reloadFragmentData();     //Fragment内のListデータ更新
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.alertSetting:     //通知設定画面へ
                Intent intent = new Intent(this, EndTimeActivity.class);
                startActivityForResult(intent, INTENT_REQUEST_CODE);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == INTENT_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                //通知設定時間の数字配列を取得
                endTimeHourGroup = data.getIntArrayExtra("EndTimeHour");
                endTimeMinuteGroup = data.getIntArrayExtra("EndTimeMinute");
            }
        }
    }

    private void reloadFragmentData(){

        for (int i=0; i<6; i++){
            Fragment fragment = customFragmentAdapter.getItem(i);

            if (fragment != null && fragment instanceof ScheduleFragment){
                ((ScheduleFragment)fragment).reloadList();
            }
        }
    }

    private void setNotificationTime(){
        //TODO 授業が無ければ通知は鳴らさない

        int classExistFlag;
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");

        //現在時刻取得
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTimeZone(timeZone);


        for (int i=0; i<5; i++){

            Calendar calendarTarget = Calendar.getInstance();
            //通知が鳴る時間の設定
            calendarTarget.setTimeInMillis(System.currentTimeMillis());
            calendarTarget.set(Calendar.HOUR_OF_DAY, endTimeHourGroup[i]);
            calendarTarget.set(Calendar.MINUTE, endTimeMinuteGroup[i]);
            calendarTarget.set(Calendar.SECOND, 0);

            //ミリ秒取得
            long targetMs = calendarTarget.getTimeInMillis();
            long nowMs = calendarNow.getTimeInMillis();

            //現時刻とターゲット時刻と比較して、ターゲット時刻が未来なら通知設定
            if(targetMs >= nowMs){
                Toast.makeText(this, calendarTarget.getTime().toString() + "にアラームが設定されています", Toast.LENGTH_LONG).show();
                Log.i("alarm", calendarTarget.getTime().toString());

                Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
                intent.putExtra("ClassNumCode", i);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intent, 0);

                AlarmManager alarmmanager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                if (alarmmanager != null) {
                    alarmmanager.set(AlarmManager.RTC_WAKEUP, calendarTarget.getTimeInMillis(), pendingIntent);
                }

                break;
            }
            //TODO 5限まで比較が終わったら明日の1限に設定する
        }
    }
}
