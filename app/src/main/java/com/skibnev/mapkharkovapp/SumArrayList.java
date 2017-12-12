package com.skibnev.mapkharkovapp;

import java.util.ArrayList;
import java.lang.Double;
import java.util.Iterator;

/**
 * Created by user on 28.01.2016.
 */
public class SumArrayList<T extends Number> extends ArrayList<Double> {

    private double sumInArray=0;

    public void sum(){
        Iterator<Double> itr=this.iterator();
        while (itr.hasNext()){
            sumInArray+=itr.next();
            itr.remove();
        }

    }

    public double getSumInArray() {
        return sumInArray;
    }

    public void clearSumOfArray(){
        this.sumInArray=0;
    }
}
