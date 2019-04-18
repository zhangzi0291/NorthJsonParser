package com.north.json;

import java.io.Serializable;

public interface JSON extends Serializable {
    String toJSONString();

    default StringBuffer setKey(StringBuffer sb,String key){
        sb.append("\"").append(key).append("\"");
        return sb;
    }
}
