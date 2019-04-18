package com.north.json;

import com.north.json.exception.NorthJSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class JSONAnalyze {

    private String keyWord[] = {"null"};
    private char ch;

    private boolean isKey(StringBuffer str) {
        for (int i = 0; i < keyWord.length; i++) {
            if (keyWord[i].equals(str))
                return true;
        }
        return false;
    }

    //词法分析
    public List<Object> analyze(Object jsonObject) {
        String json = jsonObject.toString().strip();

        json = json.substring(1,json.length()-1);
        char[] chars = json.toCharArray();
        List<Object> resultList = new CopyOnWriteArrayList<>();
        StringBuffer arr = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            ch = chars[i];
            arr = new StringBuffer();
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch == '\'') {
                ch = chars[++i];
                while (ch != '\'') {
                    arr.append(ch);
                    ch = chars[++i];
                }
                resultList.add(arr.toString());
            } else if (ch == '\"') {
                ch = chars[++i];
                while (ch != '\"') {
                    arr.append(ch);
                    ch = chars[++i];
                }
                resultList.add(arr.toString());

            } else if (ch == '{') {
                arr.append("{");
                ch = chars[++i];
                while (ch != '}') {
                    arr.append(ch);
                    ch = chars[++i];
                }
                arr.append("}");
                resultList.add(arr.toString());

            }else if (ch == '[') {
                arr.append("[");
                ch = chars[++i];
                while (ch != ']') {
                    arr.append(ch);
                    ch = chars[++i];
                }
                arr.append("]");
                resultList.add(arr.toString());

            } else if (Character.isLetter(ch)) {
                while (Character.isLetter(ch) || Character.isDigit(ch)) {
                    arr.append(ch);
                    if(i==chars.length-1){
                        i++;
                        break;
                    }
                    ch = chars[++i];
                }
                //回退一个字符
                i--;
                if(arr.toString().equals("false")||arr.toString().equals("true")){
                    resultList.add(Boolean.valueOf(arr.toString()));
                }else {
                    resultList.add(arr.toString());
                }
            } else if (Character.isDigit(ch) || (ch == '.')) {
                boolean digitFlag = false;
                while (Character.isDigit(ch) || (ch == '.' && Character.isDigit(chars[++i]))) {
                    if (ch == '.') {
                        if (!digitFlag) {
                            digitFlag = true;
                        } else {
                            throw new NorthJSONException("value:" + arr + " 数字异常");
                        }
                        i--;
                    }
                    arr.append(ch);
                    ch = chars[++i];
                }
                i--;
                if(arr.toString().contains(".")){
                    resultList.add(Double.valueOf(arr.toString()));
                }else{
                    resultList.add(Long.valueOf(arr.toString()));
                }

            } else if (':' == ch || ',' == ch) {
                resultList.add(arr.append(ch).toString());
            }

        }
        return resultList;
    }

    public JSONObject getJSONObject(List<Object> list) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i++).toString();
            Object value = list.get(++i);
            if(isJSONObject(value)){
                List<Object> analyzeList = analyze(value);
                value = getJSONObject(analyzeList);
            }
            if(isJSONArray(value)){
                List<Object> analyzeList = analyze(value);
                value = getJSONArray(analyzeList);
            }
            jsonObject.put(key, value);
            i++;
        }
        return jsonObject;
    }

    public JSONArray getJSONArray(List<Object> list) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            Object value = list.get(i++);
            if(isJSONObject(value)){
                List<Object> analyzeList = analyze(value);
                value = getJSONObject(analyzeList);
            }
            jsonArray.add(value);
        }
        return jsonArray;
    }

    boolean isValue(List<Object> result) {
        return result.size() % 4 == 3 ? true : false;
    }


    public static void main(String[] args) {
//        String testStr ="{\"oppose_right\": [1,2,3], \"algorithm_right\": {\"sarcasm\": \"none\"}, \"is_answer_oppose_reason_visiable\": '123.123', \"is_answer_rewardable\": false, \"is_creator\": false,}\n";
        String testStr =" [ 1,\"abc\", {  \"id\" : \"123\", \"courseID\" : \"huangt-test\", \"title\" : \"提交作业\"}  ,  {  \"content\" : null, \"beginTime\" : 1398873600000,  \"endTime\" :1398873600000 } ]";
        JSONAnalyze analyze = new JSONAnalyze();
        List<Object> analyzeList = analyze.analyze(testStr);
        System.out.println(analyzeList);
        JSONArray json = analyze.getJSONArray(analyzeList);
        System.out.println(json.toJSONString());
        System.out.println();
    }

    public static boolean isJSONObject(Object json){
        String jsonStr = json.toString().strip();
        if(jsonStr.startsWith("{")&&jsonStr.endsWith("}")){
            return true;
        }
        return false;
    }

    public static boolean isJSONArray(Object json){
        String jsonStr = json.toString().strip();
        if(jsonStr.startsWith("[")&&jsonStr.endsWith("]")){
            return true;
        }
        return false;
    }
}

