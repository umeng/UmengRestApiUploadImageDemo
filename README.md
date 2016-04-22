# UmengRestApiUploadImageDemo
Java版RestAPi使用说明

具体使用方法请参见/src/com/company/umeng/Main.java

<pre><code>
//在使用前需要先配置APP_KEY和APP_SECRECT
UmengHttpClient.APP_KEY = "换成你的app key";
UmengHttpClient.APP_SECRECT = "换成你的app secrect";
</code></pre>

<pre><code>
//获取access_token
HashMap<String, Object> objectHashMap = new HashMap<String, Object>();
objectHashMap.put("source_uid", "123491239324228");
objectHashMap.put("source", "qq");
objectHashMap.put("name_l", "default");
HashMap<String, Object> userInfoHashMap = new HashMap<String, Object>();
userInfoHashMap.put("name", "username");
userInfoHashMap.put("gender", 1);
objectHashMap.put("user_info", userInfoHashMap);
String token = umengHttpClient.accessTokenRequest(objectHashMap, "https://rest.wsq.umeng.com/0/get_access_token", UmengHttpClient.APP_SECRECT);
</code></pre>
