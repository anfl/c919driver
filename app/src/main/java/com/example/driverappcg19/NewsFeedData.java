package com.example.driverappcg19;

/**
 * Created by ADITHYA AN on 03-05-2018.
 */

public class NewsFeedData
{
    String name,address,date,amount,latitude,longitude,count,key;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public NewsFeedData(String name, String address, String date, String amount,String latitude,String longitude,String count,String key) {
        this.name = name;
        this.address = address;
        this.date = date;
        this.amount = amount;
        this.latitude=latitude;
        this.longitude=longitude;
        this.count = count;
        this.key = key;
    }

    public NewsFeedData(){

    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "DAta:"+getName()+","+getAddress();
    }
}
