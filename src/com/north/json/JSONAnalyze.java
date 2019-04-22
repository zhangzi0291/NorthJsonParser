package com.north.json;

import com.north.json.exception.NorthJSONException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class JSONAnalyze {


    //词法分析
    public List<Object> analyze(Object jsonObject) {
        char ch = 0;
        String json = jsonObject.toString().strip();
        json = json.substring(1,json.length()-1);
        char[] chars = json.toCharArray();
        List<Object> resultList = new CopyOnWriteArrayList<>();
        StringBuffer arr = new StringBuffer();
        for (AtomicInteger i = new AtomicInteger(); i.get() < chars.length; i.getAndAdd(1)) {
            ch = chars[i.get()];
            arr = new StringBuffer();
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch == '\'') {
                getValue(arr,chars,i,'\'','\'');
                resultList.add(arr.toString());
            } else if (ch == '\"') {
                getValue(arr,chars,i,'\"','\"');
                resultList.add(arr.toString());

            } else if (ch == '{') {
                arr.append('{');
                getObjectValue(arr,chars,i,'{','}');
                arr.append('}');
                resultList.add(arr.toString());

            }else if (ch == '[') {
                arr.append('[');
                getObjectValue(arr,chars,i,'[',']');
                arr.append(']');
                resultList.add(arr.toString());

            } else if (Character.isLetter(ch)) {
                while (Character.isLetter(ch) || Character.isDigit(ch)) {
                    arr.append(ch);
                    if(i.get()==chars.length-1){
                        i.getAndAdd(1);
                        break;
                    }
                    ch = chars[i.addAndGet(1)];
                }
                //回退一个字符
                i.addAndGet(-1);
                if(arr.toString().equals("false")||arr.toString().equals("true")){
                    resultList.add(Boolean.valueOf(arr.toString()));
                }else {
                    resultList.add(arr.toString());
                }
            } else if (Character.isDigit(ch) || (ch == '.')) {
                boolean digitFlag = false;
                while (Character.isDigit(ch) || (ch == '.' && Character.isDigit(chars[i.addAndGet(1)]))) {
                        if(i.get()==chars.length-1){
                            arr.append(ch);
                            break;
                        }
                        if (ch == '.') {
                            if (!digitFlag) {
                                digitFlag = true;
                            } else {
                                throw new NorthJSONException("value:" + arr + " 数字异常");
                            }
                            i.addAndGet(-1);
                        }

//                        ch = chars[i.getAndAdd(1)];
                        arr.append(ch);
                        ch = chars[i.addAndGet(1)];
                }
                if(i.get()==chars.length-1){
                    break;
                }
                i.addAndGet(-1);
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

            Object value = null;
            if(i==list.size()-1){
                break;
            }
            try {
                value = list.get(++i);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
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
//        String testStr =" [ 1,\"abc\", {  \"id\" : \"123\", \"courseID\" : \"huangt-test\", \"title\" : \"提交作业\"}  ,  {  \"content\" : null, \"beginTime\" : 1398873600000,  \"endTime\" :1398873600000 } ]";
        String testStr = "{\"executeResult\":1,\"executeMessage\":\"查询成功\",\"pavilionsList\":[{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182114_888_03ea.png\",\"goodsBrandlist\":[],\"pavilionId\":\"55d54b1105e145f08815f8d0136b03bd\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182118_888_537d.png\",\"pavilionName\":\"超声馆\",\"tagList\":[{\"id\":83,\"name\":\"超声诊断\",\"tagChildList\":[{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"可视人流机\",\"tagId\":272},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"多普勒胎心仪\",\"tagId\":274},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"经颅多普勒血流仪\",\"tagId\":276},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"耳温计\",\"tagId\":279},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"全自动血压计\",\"tagId\":280},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"检眼镜333444\",\"tagId\":281},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"111\",\"tagId\":282},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"非接触额温计222\",\"tagId\":283}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182130_888_bf43.png\",\"goodsBrandlist\":[{\"goodsBrandId\":\"014121952e6943bb8046f36e2d5957ee\",\"goodsBrandName\":\"飞利浦\"},{\"goodsBrandId\":\"078848eaedbc4018843a87f693fc72c9\",\"goodsBrandName\":\"清华同方\"}],\"pavilionId\":\"af3b05d242c34ff78e0d70c1c8ce6dad\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182132_888_5551.png\",\"pavilionName\":\"ED馆\",\"tagList\":[{\"id\":68,\"name\":\"电疗仪器\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"神经肌肉电刺激\",\"tagId\":70},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"经络导平治疗仪\",\"tagId\":71},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"肌电生物反馈\",\"tagId\":72},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"干扰电治疗仪\",\"tagId\":73},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"干扰电治疗仪\",\"tagId\":74},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"骨质增生治疗仪\",\"tagId\":75},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"温热式低周波治疗仪\",\"tagId\":76},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"中低频治疗仪\",\"tagId\":77},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"电针治疗仪\",\"tagId\":78},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"微波治疗仪\",\"tagId\":79},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"生物刺激反馈仪\",\"tagId\":80},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"脑电仿生电刺激仪\",\"tagId\":81},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"理疗用电极\",\"tagId\":82},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"痉挛肌治疗仪\",\"tagId\":122}]},{\"id\":113,\"name\":\"辅助器具\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"辅助治疗\",\"tagId\":114},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"助行器\",\"tagId\":115},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"无障碍辅具\",\"tagId\":116},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"步行辅助器具\",\"tagId\":117}]},{\"id\":118,\"name\":\"压力/熏蒸/牵引\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"牵引治疗仪\",\"tagId\":119},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"超声波治疗仪\",\"tagId\":129},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"空气波压力治疗仪\",\"tagId\":130}]},{\"id\":120,\"name\":\"光疗/磁疗\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"全科治疗仪\",\"tagId\":123},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"脉冲磁治疗仪\",\"tagId\":124},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"经颅磁治疗仪\",\"tagId\":125},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"红光治疗仪\",\"tagId\":126},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"氦氖激光\",\"tagId\":127},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"骨质疏松治疗仪\",\"tagId\":128}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182142_888_3028.png\",\"goodsBrandlist\":[{\"goodsBrandId\":\"685e8a5e1a9048b787dbcced3271237b\",\"goodsBrandName\":\"1213213\"}],\"pavilionId\":\"4ad77c7862a34360b80617e2f76a2cc5\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182145_888_78de.png\",\"pavilionName\":\"内窥镜馆\",\"tagList\":[{\"id\":102,\"name\":\"普放设备\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"防护服\",\"tagId\":103},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"防护巾\",\"tagId\":104},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"防护裤头3388\",\"tagId\":105},{\"imageUrl\":\"https://web-sande.oss-cn-shanghai.aliyuncs.com/formal/threeClassPicture.png\",\"tagName\":\"4444\",\"tagId\":285}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182651_888_04ce.png\",\"goodsBrandlist\":[{\"goodsBrandId\":\"078848eaedbc4018843a87f693fc72c9\",\"goodsBrandName\":\"清华同方\"}],\"pavilionId\":\"617106f9a1fc40c4b8b676178e9255b0\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182654_888_c055.png\",\"pavilionName\":\"泌尿男科\",\"tagList\":[{\"id\":106,\"name\":\"缝合线\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"可吸收缝合线\",\"tagId\":107},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"不可吸收缝合线\",\"tagId\":108}]},{\"id\":161,\"name\":\"手术设备\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术台\",\"tagId\":162},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"产床\",\"tagId\":163},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"耳鼻喉治疗台\",\"tagId\":164},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"无影灯\",\"tagId\":165},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术头灯\",\"tagId\":166},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"麻醉机\",\"tagId\":167},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"呼吸机\",\"tagId\":168},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"超声刀\",\"tagId\":169},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"急救药品箱\",\"tagId\":170},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"喉镜\",\"tagId\":171},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"心肺复苏仪\",\"tagId\":172},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术固定架\",\"tagId\":173},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术床\",\"tagId\":174},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"医用平车\",\"tagId\":175},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术灯\",\"tagId\":176},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"高频电刀\",\"tagId\":178}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182714_888_bff3.png\",\"goodsBrandlist\":[],\"pavilionId\":\"261503755ecc43af903786d6739c8eec\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182716_888_449b.png\",\"pavilionName\":\"麻醉馆\",\"tagList\":[{\"id\":131,\"name\":\"手术设备\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术台\",\"tagId\":132},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"产床\",\"tagId\":133},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"耳鼻喉治疗台\",\"tagId\":134},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"无影灯\",\"tagId\":135},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术头灯\",\"tagId\":136},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"麻醉机\",\"tagId\":137},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"呼吸机\",\"tagId\":138},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"超声刀\",\"tagId\":139},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"急救药品箱\",\"tagId\":140},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"喉镜\",\"tagId\":141},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"心肺复苏仪\",\"tagId\":142},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术固定架\",\"tagId\":143},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术床\",\"tagId\":144},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"医用平车\",\"tagId\":145},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"手术灯\",\"tagId\":146},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"吊塔\",\"tagId\":147},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"高频电刀\",\"tagId\":148}]},{\"id\":149,\"name\":\"复苏供养\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"医用制氧机\",\"tagId\":150},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"空气压缩机\",\"tagId\":151},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"氧气面罩\",\"tagId\":152},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"心肺复苏器\",\"tagId\":153}]},{\"id\":154,\"name\":\"输注辅助\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"输液泵\",\"tagId\":155},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"营养泵\",\"tagId\":156}]},{\"id\":157,\"name\":\"吸引/洗胃\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"负压吸引器\",\"tagId\":158},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"吸痰器\",\"tagId\":159},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"洗胃机\",\"tagId\":160}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2019010318273_888_88fa.png\",\"goodsBrandlist\":[],\"pavilionId\":\"268a58e469a34490be962913c6aee29b\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2019010318276_888_d2b1.png\",\"pavilionName\":\"皮肤美容馆\",\"tagList\":[{\"id\":98,\"name\":\"111\",\"tagChildList\":[{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"111\",\"tagId\":99},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"111\",\"tagId\":100},{\"imageUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2018112895152_888_be43.jpg\",\"tagName\":\"222\",\"tagId\":208}]}],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182729_888_ec0a.png\",\"goodsBrandlist\":[],\"pavilionId\":\"8645ebfb9d7443dc962f2777756a747a\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182731_888_7a2e.png\",\"pavilionName\":\"妇产科馆\",\"tagList\":[],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182838_888_9e5a.png\",\"goodsBrandlist\":[],\"pavilionId\":\"17da6a37c1c4402e87cbf9c1bb148955\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103182841_888_f9e7.png\",\"pavilionName\":\"检验馆\",\"tagList\":[],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2019010318319_888_8bcb.png\",\"goodsBrandlist\":[],\"pavilionId\":\"1aae8cb5a05e42d9a9d94f49b6f20279\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/20190103183112_888_b3db.png\",\"pavilionName\":\"耗材馆\",\"tagList\":[],\"type\":\"\"},{\"switchType\":\"0\",\"pavilionImgUrl\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2019030117150_888_64db.png\",\"goodsBrandlist\":[],\"pavilionId\":\"0eb7089fbd1b4992900428c51837f643\",\"h5\":\"http://web.sande.app.3de.com.cn/test/equipmentClass/2019030117146_888_ed37.png\",\"pavilionName\":\"阿斯顿发2\",\"tagList\":[],\"type\":\"\"}]}";
        JSONAnalyze analyze = new JSONAnalyze();
        List<Object> analyzeList = analyze.analyze(testStr);
        System.out.println(analyzeList);
        JSONObject json = analyze.getJSONObject(analyzeList);
        System.out.println(json.toJSONString());
//        JSONArray json = analyze.getJSONArray(analyzeList);
//        System.out.println(json.toJSONString());
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
    public void getObjectValue(StringBuffer arr,char[] chars,AtomicInteger i,char startChar,char endChar){
        getValue(arr,chars,i,startChar,endChar,true);
    }
    public void getValue(StringBuffer arr,char[] chars,AtomicInteger i,char startChar,char endChar) {
        getValue(arr,chars,i,startChar,endChar,false);
    }
    public void getValue(StringBuffer arr,char[] chars,AtomicInteger i,char startChar,char endChar,boolean isObject){
        int k = 0;
        char ch = chars[i.addAndGet(1)];
        while (true) {
            if(ch == startChar){
                k++;
            }
            if(ch==endChar){
                k--;
            }
            if(isObject) {
                if (ch == endChar && k == -1) {
                    break;
                }
            }else{
                if (ch == endChar && k == 0) {
                    break;
                }
            }
            if(i.get()==chars.length-1){
//                i.getAndAdd(1);
                break;
            }
            arr.append(ch);
            ch = chars[i.addAndGet(1)];
        }
    }
}

