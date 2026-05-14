package com.cobolintro.model;

/**
 * RelativeRecord model based on COBOL relative file record.
 * PIC 9(5) id, PIC X(20) data.
 */
public class RelativeRecord {

    private int id;
    private String data;

    public RelativeRecord() {
    }

    public RelativeRecord(int id, String data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RelativeRecord{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
    }
}
