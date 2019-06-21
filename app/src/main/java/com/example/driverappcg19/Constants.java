package com.example.driverappcg19;

import com.google.firebase.database.DatabaseReference;

public class Constants {
    static DatabaseReference databaseReference;

    Constants(DatabaseReference dr){
        this.databaseReference = dr;
    }

    public static DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }
}
