package com.company.umeng;

import org.json.JSONObject;

import java.util.HashMap;


public class Main {
    private static final String BASEURL = "https://rest.wsq.umeng.com";
//   private static final String BASEURL = "http://opentest.rest.api.wsq.umeng.com";
    private static String api = "/0/image_token";
    private static String api2 = "/0/feed/favourites/create";
    private static String api3 = "/0/get_access_token";
    private static String api4 = "/0/point/details";
    private static String api5 = "/0/point/op";
    public static void main(String[] args) {
        // 首先把这里替换成你自己的app key
        UmengHttpClient.APP_KEY = "54d19086fd98c540a2001155";
        UmengHttpClient.APP_SECRECT = "de08937649e99e3c81d7924b145b3f79";
//        UmengHttpClient.APP_KEY = "5292e17f56240b8ba110920c";
//        UmengHttpClient.APP_SECRECT = "ab3ecfeeb46be257dfc880804c0264df";
        // access token for upload image only
        String imageToken;
        //String uid = "55c2eb7d41db1a3bd94684fe";
        String feedId = "54d1967aee7850208e366c96";

        //创建UmengHttpClient请求对象，之后用此对象发送请求
        UmengHttpClient umengHttpClient = new UmengHttpClient();

        //#########################Example 1###############################
        //以下例子为如何获取 access_token;
        //把要穿的数据放在HashMap中
        HashMap<String, Object> objectHashMap = new HashMap<String, Object>();
        objectHashMap.put("source_uid", "123491239324228");
        objectHashMap.put("source", "qq");
        objectHashMap.put("name_l", "defaul1t");
        HashMap<String, Object> userInfoHashMap = new HashMap<String, Object>();
        userInfoHashMap.put("name", "name123");
        userInfoHashMap.put("gender", 1);
        objectHashMap.put("user_info", userInfoHashMap);
        String token = umengHttpClient.accessTokenRequest(objectHashMap, BASEURL + api3, UmengHttpClient.APP_SECRECT);
        System.out.println(token);
        JSONObject jsonObject = new JSONObject(token);
        //拿到access_token之后要在 UmengHttpClient.ACCESS_TOKEN 里面设置一下
        System.out.println("token="+jsonObject.optString("access_token"));
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
            System.out.println("imagetoken="+jsonObject2.optString("token"));
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




        //#########################Example 4###############################
        //这是另一个post请求的案例，将请求需要的参数加入Map中,控制积分接口
        HashMap<String,Object> data = new HashMap<>();
        data.put("page","1");
        data.put("count","100");

        String pointsResult = umengHttpClient.sentRequestpoint(BASEURL + api4, UmengHttpClient.HttpMethod.GET, data);
        System.out.println(pointsResult);
        //#########################Example 5###############################
        //这是另一个post请求的案例，将请求需要的参数加入Map中,控制积分接口
        HashMap<String,Object> dataop = new HashMap<>();
        dataop.put("ak",UmengHttpClient.APP_KEY);
        dataop.put("point",10);
        dataop.put("desc","sss");
        dataop.put("identity","1");
        dataop.put("use_unit",0);
        String pointsopResult = umengHttpClient.sentRequest(BASEURL + api5, UmengHttpClient.HttpMethod.POST, dataop);
        System.out.println(pointsopResult);
         }




}
