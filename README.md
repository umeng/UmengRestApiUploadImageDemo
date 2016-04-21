# UmengRestApiUploadImageDemo
友盟微社区RestApi图片上传Demo Java版本

<pre><code>
UploadImageDemo uploadImageDemo = new UploadImageDemo();
HashMap map = new HashMap();
//You can put argument into the hashmap,You don't have to put content and size, it is already been done for you.
//你可以把图片上传的参数放进 hashmap里面，用一下方法。"content"和"size"不需要传，因为已经封装好了
//map.put("dir","example_dir");
//map.put("name","advertising.jpg");
String result = uploadImageDemo.uploadImage(token,"/Users/umeng/Desktop/advertising.jpg",map);
System.out.println(result);
</code></pre>

# UmengRestApiUploadImageDemo
