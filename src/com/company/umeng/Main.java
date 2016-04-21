package com.company.umeng;

import org.json.JSONObject;

import java.util.HashMap;


public class Main {
    private static final String BASEURL = "https://rest.wsq.umeng.com";
    private static String api = "/0/image_token";
    private static String api2 = "/0/feed/favourites/create";
    private static String api3 = "/0/get_access_token";

    public static void main(String[] args) {
        // 首先把这里替换成你自己的app key
        UmengHttpClient.APP_KEY = "换成你的app key";
        UmengHttpClient.APP_SECRECT = "换成你的app secrect";
        // access token for upload image only
        String imageToken;
        //String uid = "55c2eb7d41db1a3bd94684fe";
        String feedId = "54d1967aee7850208e366c96";
        // uploading image to
        UmengHttpClient umengHttpClient = new UmengHttpClient();

        //#########################Example 1###############################
        //以下例子为如何获取 access_token;
        //把要穿的数据放在hashmap中
        HashMap<String, Object> objectHashMap = new HashMap<String, Object>();
        objectHashMap.put("source_uid", "123491239324228");
        objectHashMap.put("source", "qq");
        objectHashMap.put("name_l", "default");
        HashMap<String, Object> userInfoHashMap = new HashMap<String, Object>();
        userInfoHashMap.put("name", "this is a test name");
        userInfoHashMap.put("gender", 1);
        objectHashMap.put("user_info", userInfoHashMap);

        String token = umengHttpClient.accessTokenRequest(objectHashMap, BASEURL + api3, UmengHttpClient.APP_SECRECT);
        System.out.println(token);
        JSONObject jsonObject = new JSONObject(token);
        //拿到access_token之后要在 UmengHttpClient.ACCESS_TOKEN 里面设置一下
        UmengHttpClient.ACCESS_TOKEN = jsonObject.optString("access_token");
        // END Example 1



        //#########################Example 2###############################
        // 我们请求 /0/image_token 接口，这个接口返回的是image token,我们拿到image token之后就可以用image token上传图片了!
        // 发送一个POST请求
        HashMap map = new HashMap();
        String postResult = umengHttpClient.sentRequest(BASEURL + api, UmengHttpClient.HttpMethod.POST, map);
        System.out.println(postResult);

        // 好了,已经拿到image token了,可以上传图片了!
        JSONObject jsonObject2 = new JSONObject(postResult);
        if (jsonObject2 != null) {
            imageToken = jsonObject2.optString("token");
            HashMap<String, Object> imageParameter = new HashMap<String, Object>();
            String imageResult = umengHttpClient.uploadImage(imageToken, "/Users/umeng/Desktop/advertising.jpg", imageParameter);
            System.out.println(imageResult);
        }
        // 上传图片完成!
        // END Example2




        //#########################Example 3###############################
        //这是另一个post请求的案例，将请求需要的参数加入Map中,收藏某条feed
        HashMap<String, Object> postData = new HashMap<String, Object>();
        postData.put("feed_id", feedId);
        String feedResult = umengHttpClient.sentRequest(BASEURL + api2, UmengHttpClient.HttpMethod.POST, postData);
        System.out.println(feedResult);
        // END Example3
         }
}
