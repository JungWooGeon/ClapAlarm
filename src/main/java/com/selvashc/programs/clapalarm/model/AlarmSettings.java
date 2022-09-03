package com.selvashc.programs.clapalarm.model;

public class AlarmSettings {

    private String sensitivity;
    private String alarmTime;
    private String volume;

    public String getSensitivity() { return this.sensitivity; }
    public String getAlarmTime() { return this.alarmTime; }
    public String getVolume() { return this.volume; }

    public AlarmSettings(String sensitivity, String alarmTime, String volume) {
        this.sensitivity = sensitivity;
        this.alarmTime = alarmTime;
        this.volume = volume;
    }
}
