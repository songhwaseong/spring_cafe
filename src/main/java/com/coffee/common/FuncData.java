package com.coffee.common;

import java.util.HashMap;
import java.util.Map;

public class FuncData {

    public static String decodeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '\\' && i + 1 < input.length() && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                sb.append((char) Integer.parseInt(hex, 16));
                i += 6;
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    public static Map<String, String> parseInfo(String jsonResponse) {
        Map<String, String> userInfo = new HashMap<>();

        // "response" 블록만 추출
        int start = jsonResponse.indexOf("\"response\":{");
        int end = jsonResponse.lastIndexOf("}");
        String responseBlock = jsonResponse.substring(start + "\"response\":{".length(), end);

        // key-value 파싱
        String[] pairs = responseBlock.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                userInfo.put(key, value);
            }
        }

        return userInfo;
    }

    public static String parseStr(String jsonResponse, String str) {
        // JSONObject 없이 간단 파싱
        String key = "\""+str+"\":\"";
        int startIdx = jsonResponse.indexOf(key);
        if (startIdx == -1) return null;

        startIdx += key.length();
        int endIdx = jsonResponse.indexOf("\"", startIdx);
        return jsonResponse.substring(startIdx, endIdx);
    }
}
