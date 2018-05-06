package com.robop.attendancerecord;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmSwitchRealmModel extends RealmObject {

    @PrimaryKey
    private int id;
    private boolean alarmSwitch;

    public int getId() {
        return id;
    }

    public boolean isAlarmSwitch() {
        return alarmSwitch;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlarmSwitch(boolean alarmSwitch) {
        this.alarmSwitch = alarmSwitch;
    }
}
