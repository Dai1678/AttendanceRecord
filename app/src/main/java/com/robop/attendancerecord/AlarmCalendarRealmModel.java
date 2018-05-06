package com.robop.attendancerecord;

import io.realm.RealmObject;

public class AlarmCalendarRealmModel extends RealmObject {

    private int id;
    private long alarmTimeInMillis;

    public int getId() {
        return id;
    }

    public long getAlarmTimeInMillis() {
        return alarmTimeInMillis;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlarmTimeInMillis(long alarmTimeInMillis) {
        this.alarmTimeInMillis = alarmTimeInMillis;
    }
}
