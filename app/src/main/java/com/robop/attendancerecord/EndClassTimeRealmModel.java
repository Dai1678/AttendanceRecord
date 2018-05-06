package com.robop.attendancerecord;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EndClassTimeRealmModel extends RealmObject {

    @PrimaryKey
    private int id;
    private String endClassTime;
    private long endClassTimeInMillis;

    public int getId() {
        return id;
    }

    public String getEndClassTime() {
        return endClassTime;
    }

    public long getEndClassTimeInMillis() {
        return endClassTimeInMillis;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEndClassTime(String endClassTime) {
        this.endClassTime = endClassTime;
    }

    public void setEndClassTimeInMillis(long endClassTimeInMillis) {
        this.endClassTimeInMillis = endClassTimeInMillis;
    }
}
