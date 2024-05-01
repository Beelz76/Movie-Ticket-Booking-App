package com.example.myproject.models;

public class Seat {
    private int seatId;
    private int row;
    private int number;
    private int isTaken;

    public Seat() {}

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getIsTaken() {
        return isTaken;
    }

    public void setTaken(int taken) {
        isTaken = taken;
    }
}
