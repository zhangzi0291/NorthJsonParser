package com.north.json;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JSONObject extends ConcurrentHashMap<String,Object> implements JSON, Serializable {

    @Override
    public String toJSONString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (Map.Entry<String,Object> entry: this.entrySet() ) {
            setKey(sb,entry.getKey()).append(":");
            if(JSONAnalyze.isJSONObject(entry.getValue())) {
                sb.append( ((JSONObject)entry.getValue()).toJSONString() );
            }else if (JSONAnalyze.isJSONArray(entry.getValue())){
                sb.append(entry.getValue());
            }else{
                if((entry.getValue() instanceof Long) || (entry.getValue() instanceof Double ) || (entry.getValue() instanceof Boolean )){
                    sb.append(entry.getValue());
                }else{
                    sb.append("\"").append(entry.getValue()).append("\"");
                }

            }
            sb.append(" , ");
        }
        sb.delete(sb.length()-2,sb.length());
        sb.append("}");
        return sb.toString();
    }

}