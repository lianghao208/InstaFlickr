package com.example.administrator.instagramdemo;

import android.net.Uri;

import com.example.administrator.instagramdemo.Item.GalleryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/6.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "c5f8754e87102cc9fcb3699813975c2d";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";


    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            //若Http连接失败，返回的信息不为连接成功则抛出异常
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //Throwable接口传入错误信息，进入栈查找并在控制台打印错误信息
                throw new IOException(connection.getResponseMessage() + ":with" + urlSpec);
            }


        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer))>0){
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        return out.toByteArray();

        } finally{
        connection.disconnect();
    }
    }
    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 解析网址的方法
     */
    public List<GalleryItem> fetchItems(){
        List<GalleryItem> items = new ArrayList<>();

        try {
            //传入Uri的parse方法解析得到一个格式满足解析条件的网址字符串
            String url = Uri.parse("https://www.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method","flickr.photos.getRecent")
                .appendQueryParameter("api_key",API_KEY)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .appendQueryParameter("extras","owner_name,url_s,description")//小尺寸图片也返回url
                //.appendQueryParameter("extras","description")//图片描述
                .build().toString();

            //解析得到的json格式字符串
            String jsonString = getUrlString(url);

            //将Json格式字符串传入JSONObject类中建立对应点key，value关系
            JSONObject jsonBody = new JSONObject(jsonString);
            //解析jsonBody将其存入items的List中
            parseItems(items,jsonBody);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     *将jsonBody中的内容提取存进GalleryItem类中
     * {"photos":
     *      {"page":1
     *      ,"pages":10
     *      ,"perpage":100
     *      ,"total":1000
     *      ,"photo":
     *          [{"id":"32456669954","owner":"149678168@N08","secret":"8357fb997f","server":"622","farm":1,"title":"Motion Detection","ispublic":1,"isfriend":0,"isfamily":0}
     *          ]
     *       }
     *    ,"stat":"ok"}
     * @param items
     * @param jsonBody
     */
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException ,IOException{

        //photosJsonObject保存所有照片的属性
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        //photosJsonArray保存所有照片的id，用户信息等详细信息
        JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photosJsonArray.length(); i++){
            //依次从JsonArray中取出每张图片的JsonObject信息
            JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setmId(photoJsonObject.getString("id"));
            item.setmCaption(photoJsonObject.getString("title"));

            //若图片无url的key则不获取，重新进去循环读取下一个jsonobject
            if(!photoJsonObject.has("url_s")){
                continue;
            }
            //若图片无描述的key则不获取，重新进去循环读取下一个jsonobject
            if(!photoJsonObject.has("title")){
                continue;

            }
            if(!photoJsonObject.has("ownername")){
                continue;

            }

            item.setmUrl(photoJsonObject.getString("url_s"));
            item.setmTitle(photoJsonObject.getString("title"));
            item.setmOwnername(photoJsonObject.getString("ownername"));
            //item.setmDescription(photoJsonObject.getString("description"));
            //将每张图片作为一个item添加进List中
            items.add(item);
        }
    }

}
