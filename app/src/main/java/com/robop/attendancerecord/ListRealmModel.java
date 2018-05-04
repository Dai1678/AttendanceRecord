package com.robop.attendancerecord;

import io.realm.RealmObject;

public class ListRealmModel extends RealmObject {

    private String subjectName;
    private int attendResult;
    private int absentResult;
    private int lateResult;
    private int classId;
    private int dayOfWeekId;

    public String getSubjectName() {
        return subjectName;
    }

    public int getAttendResult() {
        return attendResult;
    }

    public int getAbsentResult() {
        return absentResult;
    }

    public int getLateResult() {
        return lateResult;
    }

    public int getClassId() {
        return classId;
    }

    public int getDayOfWeekId() {
        return dayOfWeekId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setAttendResult(int attendResult) {
        this.attendResult = attendResult;
    }

    public void setAbsentResult(int absentResult) {
        this.absentResult = absentResult;
    }

    public void setLateResult(int lateResult) {
        this.lateResult = lateResult;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setDayOfWeekId(int dayOfWeekId) {
        this.dayOfWeekId = dayOfWeekId;
    }
}
