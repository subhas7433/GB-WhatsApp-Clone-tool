package com.affixstudio.whatsapptool.getData;

import java.util.ArrayList;

public class ArrayData {
    ArrayList<String> names=new ArrayList<>();
    ArrayList <String> message=new ArrayList<>();
    ArrayList <String> WANumebr=new ArrayList<>();
    ArrayList <String> date=new ArrayList<>();
    ArrayList<Integer> isDraft=new ArrayList<>();
    ArrayList<Integer> id=new ArrayList<>();
    ArrayList<String> CountryCode=new ArrayList<>();
    ArrayList<String> sendThrough=new ArrayList<String>();


    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message.add(message) ;
    }

    public ArrayList<String> getWANumebr() {
        return WANumebr;
    }

    public void setWANumebr(String WANumebr) {
        this.WANumebr.add(WANumebr) ;
    }

    public ArrayList<String> getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date.add(date);

    }

    public ArrayList<Integer> getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(int isDraft) {
        this.isDraft.add(isDraft);;
    }

    public ArrayList<Integer> getId() {
        return id;
    }

    public void setId(int id) {
        this.id.add(id);
    }

    public ArrayList<String> getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode.add(countryCode);
    }

    public ArrayList<String> getSendThrough() {
        return sendThrough;
    }

    public void setSendThrough(String sendThrough) {
        this.sendThrough.add(sendThrough);
    }
    public void ArrayClear()
    {
        id.clear();
        names.clear();
        message.clear();
        WANumebr.clear();
        date.clear();
        isDraft.clear();
        CountryCode.clear();
    }
    public boolean Empty(){

        return  message.isEmpty() && WANumebr.isEmpty() && date.isEmpty()  &&
                isDraft.isEmpty();
    }
}
