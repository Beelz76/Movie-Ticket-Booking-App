package com.example.myproject.models;

public class Seat implements Comparable<Seat> {
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

    @Override
    public int compareTo(Seat seat) {
        int rowComparison = Integer.compare(row, seat.row);
        if (rowComparison == 0) {
            return Integer.compare(number, seat.number);
        } else {
            return rowComparison;
        }
    }
}
