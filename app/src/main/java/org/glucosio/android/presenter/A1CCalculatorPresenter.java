package org.glucosio.android.presenter;

import org.glucosio.android.activity.A1Calculator;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.tools.GlucoseConverter;

public class A1CCalculatorPresenter {
    private DatabaseHandler dB;
    private A1Calculator activity;


    public A1CCalculatorPresenter(A1Calculator a1Calculator) {
        this.activity= a1Calculator;
        dB = new DatabaseHandler(a1Calculator.getApplicationContext());
    }

    public double calculateA1C(String glucose){
        GlucoseConverter converter = new GlucoseConverter();
        if (dB.getUser(1).getPreferred_unit().equals("mg/dL")) {
            return converter.glucoseToA1C(Double.parseDouble(glucose));
        } else {
            return converter.glucoseToA1C(converter.glucoseToMgDl(Double.parseDouble(glucose)));
        }
    }

    public void checkUnit(){
        if (!dB.getUser(1).getPreferred_unit().equals("mg/dL")){
            activity.setMmol();
        }
    }

}
