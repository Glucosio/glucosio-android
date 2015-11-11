package org.glucosio.android.tools;

import com.google.gson.Gson;

public class JsonTools {

    public JsonTools(){}

    Gson gson = new Gson();

    public String objToJson(Object obj){
        return gson.toJson(obj);
    }
}
