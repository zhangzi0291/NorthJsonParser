package com.north.json;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class JSONArray extends CopyOnWriteArrayList implements JSON, Serializable {
    @Override
    public String toJSONString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (Object entry: this ) {
            if(JSONAnalyze.isJSONObject(entry)) {
//            if(entry instanceof JSONObject) {
                sb.append(((JSONObject) entry).toJSONString());
            }else if (JSONAnalyze.isJSONArray(entry)){
//            }else if(entry instanceof JSONArray) {
                sb.append(((JSONArray)entry).toJSONString() );
            }else{
                if((entry instanceof Long) || (entry instanceof Double ) || (entry instanceof Boolean )){
                    sb.append(entry);
                }else{
                    sb.append("\"").append(entry).append("\"");
                }

            }
            sb.append(" , ");
        }
        if(sb.length()>3){
            sb.delete(sb.lastIndexOf(","),sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}
