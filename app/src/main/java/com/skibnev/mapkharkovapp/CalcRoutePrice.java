package com.skibnev.mapkharkovapp;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 28.01.2016.
 */
public class CalcRoutePrice {


    public Double calcToBus(String url,String busNumber){


        Document d=null;
        String GoToRef=null;
        String link=null;

        String number=this.convertStringToBusNumber(busNumber);

        try {
            d= Jsoup.connect(url).get();
            Elements elem=d.select("table").select("tbody");
            Elements rows=elem.select("tr");
            for (int i=1;i<rows.size();i++){
                Element row=rows.get(i);
                Elements cols=row.select("td");
                for (int j=1;j<cols.size();j++){
                    Element col=cols.get(j);
                    if (col.select("a").attr("title").equals("Подробнее о маршруте...")&& col.select("a").html().equals(number)){
                        link=col.select("a").attr("href");
                        break;
                    }
                }
                if (link!=null){
                    GoToRef="http://gortransport.kharkov.ua"+link;
                    break;
                }
            }

        }catch (IOException exc){
            exc.printStackTrace();
        }
        return this.findPrice(GoToRef, true);
    }
    public Double findPrice(String url,boolean isBus){

        String price="empty";
        String find=null;
        Document d=null;

        if (isBus){
            find="Стоимость проезда:&nbsp;";
        }else {
            find="Стоимость проезда:";
        }
        try {
            d= Jsoup.connect(url).get();
            Elements elem=d.select("table.under_line").select("tbody");
            Elements rows=elem.select("tr");
            for (int k=1;k<rows.size();k++){
                Element rw=rows.get(k);
                if (rw.select("th").html().equals(find)){
                    price=rw.select("td").html();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int start=0;
        int end=4;
        char buf[]=new char[end-start];
        price.getChars(start, end, buf, 0);
        String res=new String(buf);
        return Double.valueOf(res);
    }
    public String convertStringToBusNumber(String num){
        char chars[]=num.toCharArray();
        String str="";
        for (int k=0;k<chars.length;k++){
            switch (chars[k]){
                case '1': str+=1;
                    break;
                case '2':str+=2;
                    break;
                case '3':str+=3;
                    break;
                case '4':str+=4;
                    break;
                case '5':str+=5;
                    break;
                case '6':str+=6;
                    break;
                case '7':str+=7;
                    break;
                case '8':str+=8;
                    break;
                case '9':str+=9;
                    break;
                case '0':str+=0;
                    break;
                default:str+="э";
                    break;
            }
        }
        return str;
    }

}
