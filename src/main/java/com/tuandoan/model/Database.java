package com.tuandoan.model;

public class Database {
    private String name;

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Database{" +
                "name='" + name + '\'' +
                '}';
    }
}
