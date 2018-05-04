package com.robop.attendancerecord;

import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        String[] tabNames = getResources().getStringArray(R.array.tabNames);    //TabLayoutに表示する文字を管理する配列

        TabLayout tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.pager);

        if (savedInstanceState == null){
            //曜日の数だけFragment生成
            PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabNames);

            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.alertSetting:
                Intent intent = new Intent(this, SettingClassTimeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
