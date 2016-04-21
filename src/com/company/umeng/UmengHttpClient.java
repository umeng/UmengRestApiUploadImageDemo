package com.company.umeng;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by umeng on 4/8/16.
 */
public class UmengHttpClient {

    public static final String SERVER_ADDRESS = "http://upload.wsq.umeng.com/api/proxy/upload";
    private static final String END = "\r\n";
    private static String boundary = UUID.randomUUID().toString();

    public static String ACCESS_TOKEN = null;
    // this is for demo use

    public static String APP_KEY = null;

    public static String APP_SECRECT = null;
    public enum HttpMethod {
        POST, GET, PUT, DELETE
    }


    public String sentRequest(String fullUrl, HttpMethod httpMethod, Map<String, Object> data) {
        String result = null;
        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
            fullUrl = fullUrl + buildParameter(ACCESS_TOKEN, APP_KEY, data);
            System.out.println("umeng rest url:" + fullUrl);
        } else {
            fullUrl = fullUrl + buildParameter(ACCESS_TOKEN, APP_KEY, null);
            System.out.println("umeng rest url:" + fullUrl);
        }
        HttpsURLConnection urlConnection;
        OutputStream outputStream;
        try {
            urlConnection = (HttpsURLConnection) new URL(fullUrl).openConnection();
            setRequestMethod(httpMethod, urlConnection);
            if (HttpMethod.POST == httpMethod || HttpMethod.PUT == httpMethod) {
                outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.write(buildParameter(null, null, data).getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
            }
            if (urlConnection.getResponseCode() == 200) {
                result = convertStreamToString(urlConnection.getInputStream());
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    /**
     * @param accessToken
     * @param appKey
     * @param data
     * @return
     */
    private String buildParameter(String accessToken, String appKey, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        if (appKey != null && !appKey.equals("")) {
            sb.append("?ak=" + APP_KEY);
        } else {
            System.out.println("umeng app key is empty or null");
        }
        if (accessToken != null && !accessToken.equals("")) {
            sb.append("&access_token=" + ACCESS_TOKEN);
        }
        if (data != null && !data.isEmpty()) {
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object value = data.get(key);
                if (value instanceof String) {
                    sb.append("&" + key + "=" + value);
                } else if (value instanceof Integer) {
                    sb.append("&" + key + "=" + value);
                }
            }
        }
        return sb.toString();
    }

    private void setRequestMethod(HttpMethod httpMethod, HttpURLConnection urlConnection) throws ProtocolException {
        switch (httpMethod) {
            case POST:
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                break;
            case PUT:
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                break;
            case GET:
                urlConnection.setRequestMethod("GET");
                break;
            case DELETE:
                urlConnection.setRequestMethod("DELETE");
                break;
        }
    }

    /**
     * 此方法一封装AES加密算法
     *
     * @param data
     * @param url
     * @return getting access token for the application
     */
    public String accessTokenRequest(Map<String, Object> data, String url, String APP_SECRET) {
        String encry_data = null;
        JSONObject jsonObject = new JSONObject(data);
        String stringData = jsonObject.toString();
        System.out.println(stringData);
        encry_data = AESUtils.getEncryptedMap(stringData.length()+stringData, APP_SECRET);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("encrypted_data", encry_data);
        return sentRequest(url, HttpMethod.POST, hashMap);
    }


    /**
     * @param accessToken 你的微社区图片上传 token
     * @param pathOfImage 图片的路径
     * @param map         传输参数map
     * @return
     */
    public String uploadImage(String accessToken, String pathOfImage, Map<String, Object> map) {
        HttpURLConnection urlConnection;
        OutputStream outputStream;
        try {
            URL url = new URL(SERVER_ADDRESS);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            //加入你的access token 到header里面
            urlConnection.setRequestProperty("Authorization", accessToken);
            System.out.println("token:"+accessToken);
            urlConnection.setConnectTimeout(7000);
            urlConnection.setReadTimeout(7000);
            urlConnection.setDoOutput(true);
            Path path = Paths.get(pathOfImage);

            byte[] data = Files.readAllBytes(path);
            if (map == null) {
                map = new HashMap<String, Object>();
                map.put("size", Files.size(path));
            } else {
                if (map.get("size") == null) {
                    map.put("size", Files.size(path));
                }
            }
            outputStream = new DataOutputStream(urlConnection.getOutputStream());
            addBodyParams(outputStream, map);
            addBinaryParams(pathOfImage, outputStream, data);

            if (urlConnection.getResponseCode() == 200) {
                System.out.println("upload success");
                return convertStreamToString(urlConnection.getInputStream());
            } else {
                System.out.println("upload fail" + convertStreamToString(urlConnection.getInputStream()));
                return convertStreamToString(urlConnection.getInputStream());
            }

        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void addBinaryParams(String name, OutputStream outputStream, byte[] data) throws IOException {
        addFilePart(name, data, boundary, outputStream);
        finishWrite(outputStream, boundary);
    }

    private void addFilePart(final String fieldName, byte[] data, String boundary, OutputStream outputStream)
            throws IOException {
        StringBuilder writer = new StringBuilder();
        writer.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"")
                .append("content").append("\"; filename=\"").append(fieldName)
                .append("\"").append(END).append("Content-Type: ")
                .append("application/octet-stream").append(END)
                .append("Content-Transfer-Encoding: binary").append(END)
                .append(END);
        outputStream.write(writer.toString().getBytes());
        outputStream.write(data);
        outputStream.write(END.getBytes());
    }

    private void addBodyParams(OutputStream outputStream, Map<String, Object> bodyMaps)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (bodyMaps != null) {
            Set<String> keys = bodyMaps.keySet();
            for (String key : keys) {
                Object value = bodyMaps.get(key);
                // 列表类型,即同名的多个参数。
                if (isListParams(value)) {
                    if (value != null) {
                        addListParams(stringBuilder, key, (List<String>) value);
                    }
                } else {
                    if (bodyMaps.get(key) != null) {
                        addFormField(stringBuilder, key, bodyMaps.get(key).toString(), boundary);
                    }
                }
            }
            outputStream.write(stringBuilder.toString().getBytes());
        }
    }


    private void finishWrite(OutputStream outputStream, String boundary) throws IOException {
        outputStream.write(END.getBytes());
        outputStream.write(("--" + boundary + "--").getBytes());
        outputStream.write(END.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private boolean isListParams(Object param) {
        return (param instanceof List<?>);
    }

    private void addFormField(StringBuilder writer, final String name, final String value, String boundary) {
        writer.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(END)
                .append("Content-Type: text/plain; charset=").append("UTF-8")
                .append(END).append("Content-Transfer-Encoding: 8bit")
                .append(END).append(END).append(value).append(END);
    }

    private void addListParams(StringBuilder stringBuilder, String key, List<String> param) {
        // Prepare Category Array
        for (String value : param) {
            addFormField(stringBuilder, key, value, boundary);
        }
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {

            return null;
        } finally {
            if (is != null) {
                is.close();
            }

        }
        return sb.toString();
    }
}
