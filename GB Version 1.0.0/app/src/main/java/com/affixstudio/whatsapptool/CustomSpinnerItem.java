package com.affixstudio.whatsapptool;

public class CustomSpinnerItem {
    private String spinnerItemName;
    private int spinnerItemImage;

    public  CustomSpinnerItem(String spinnerItemName, int spinnerItemImage){
        this.spinnerItemName = spinnerItemName;
        this.spinnerItemImage = spinnerItemImage;
    }
    public  String getSpinnerItemName(){
        return  spinnerItemName;
    }
    public int getSpinnerItemImage(){
        return spinnerItemImage;
    }
}
