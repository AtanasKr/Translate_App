package com.example.translateapp;

public class HistoryData {
    private String langName1;
    private String langName2;
    private String firstInput;
    private String secondInput;
    private int historyNumber;

    public HistoryData(String langName1, String langName2,String firstInput,String secondInput, int historyNumber){
        this.langName1 = langName1;
        this.langName2 = langName2;
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.historyNumber = historyNumber;

    }

    public String getLangName1() {
        return langName1;
    }

    public void setLangName1(String langName1) {
        this.langName1 = langName1;
    }

    public String getLangName2() {
        return langName2;
    }

    public void setLangName2(String langName2) {
        this.langName2 = langName2;
    }

    public String getFirstInput() {
        return firstInput;
    }

    public void setFirstInput(String firstInput) {
        this.firstInput = firstInput;
    }

    public String getSecondInput() {
        return secondInput;
    }

    public void setSecondInput(String secondInput) {
        this.secondInput = secondInput;
    }

    public int getHistoryNumber() {
        return historyNumber;
    }

    public void setHistoryNumber(int historyNumber) {
        this.historyNumber = historyNumber;
    }
}
