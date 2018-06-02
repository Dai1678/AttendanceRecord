package com.robop.attendancerecord;

import io.realm.RealmObject;

public class EndClassTimeRealmModel extends RealmObject {

    private int id;
    private String endClassTimeStr;
    private long endClassTimeInMillis;

    public int getId() {
        return id;
    }

    public String getEndClassTimeStr() {
        return endClassTimeStr;
    }

    public long getEndClassTimeInMillis() {
        return endClassTimeInMillis;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEndClassTimeStr(String endClassTime) {
        this.endClassTimeStr = endClassTime;
    }

    public void setEndClassTimeInMillis(long endClassTimeInMillis) {
        this.endClassTimeInMillis = endClassTimeInMillis;
    }
}
