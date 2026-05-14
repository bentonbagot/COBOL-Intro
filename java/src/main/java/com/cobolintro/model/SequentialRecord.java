package com.cobolintro.model;

/**
 * SequentialRecord model based on COBOL sequential file record.
 * PIC 9(5) id, PIC X(30) name.
 */
public class SequentialRecord {

    private int id;
    private String name;

    public SequentialRecord() {
    }

    public SequentialRecord(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SequentialRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
