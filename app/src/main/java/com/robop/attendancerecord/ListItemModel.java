package com.robop.attendancerecord;

public class ListItemModel {

    private String subjectName; //教科名
    private int attendNum;  //出席回数
    private int absentNum;  //欠席回数
    private int lateNum;    //遅刻回数

    ListItemModel(){};

    public ListItemModel(String subjectName, int attendNum, int absentNum, int lateNum){
        this.subjectName = subjectName;
        this.attendNum = attendNum;
        this.absentNum = absentNum;
        this.lateNum = lateNum;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getAttendNum() {
        return attendNum;
    }

    public int getAbsentNum() {
        return absentNum;
    }

    public int getLateNum() {
        return lateNum;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setAttendNum(int attendNum) {
        this.attendNum = attendNum;
    }

    public void setAbsentNum(int absentNum) {
        this.absentNum = absentNum;
    }

    public void setLateNum(int lateNum) {
        this.lateNum = lateNum;
    }

}
