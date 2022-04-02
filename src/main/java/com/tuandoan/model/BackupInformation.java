package com.tuandoan.model;

import java.time.LocalDateTime;

public class BackupInformation {
    private int position;
    private String name;
    private LocalDateTime backupFinishDate;
    private String userName;

    public BackupInformation(int position, String name, LocalDateTime backupStartDate, String userName) {
        this.position = position;
        this.name = name;
        this.backupFinishDate = backupStartDate;
        this.userName = userName;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getBackupFinishDate() {
        return backupFinishDate;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "BackupInformation{" +
                "position=" + position +
                ", name='" + name + '\'' +
                ", backupStartDate=" + backupFinishDate +
                ", userName='" + userName + '\'' +
                '}';
    }
}
