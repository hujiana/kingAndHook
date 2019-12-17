package cn.king.admin.ChangeTool;

import android.util.Log;

import junit.runner.BaseTestRunner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class androidInfo {

    /**
     * android_id
     */
    public String android_id = null;

    /**
     * IMEI
     */
    public String IMEI = null ;

    /**
     * Package List
     */
    public String[] Packages = null;

    /**
     * this static Object
     */
    private static androidInfo Info = null;


    /**
     * 解析本地文件内容到androidInfo
     * @return
     */
    public static androidInfo ParsingContentToFile()
    {
        androidInfo Info = null;
        File infoFile = new File("data/king/androidInfo.txt");
        JSONObject json;
        try {
            FileInputStream fileInput = new FileInputStream(infoFile);
            json = readFileToJObject(fileInput);
        } catch (FileNotFoundException e) {
            Log.e(staticSource.TAG, "ParsingContentToFile Exception : " + e.getMessage() );
            return Info;
        }
        if (json == null)
        {
            Log.e(staticSource.TAG, "ParsingContentToFile Json Object is Null " );
            return Info;
        }

        Info = ParsingToandroidInfoByJson(json);

        return Info;
    }

    /**
     * 读入文件内容转成Json对象
     * @param inputStream
     * @return
     */
    private static JSONObject readFileToJObject(FileInputStream inputStream)
    {

        byte[] buf = new byte[1024];
        int len = 0;
        ByteArrayOutputStream TotalBuffer = new ByteArrayOutputStream();
        while (true) {
            try
            {
                len = inputStream.read(buf,0,buf.length);
                if (len> 0)
                {
                    TotalBuffer.write(buf);
                }else
                {
                    break;
                }
            }catch (Exception e)
            {
                Log.e(staticSource.TAG, "readFileToJObject Exception : " + e.getMessage() );
                return null;
            }
        }

        byte[] locaArray =  TotalBuffer.toByteArray();
        try {
            JSONObject object = new JSONObject(new String(locaArray));
            return  object;
        } catch (JSONException e) {
            Log.e(staticSource.TAG, "readFileToJObject Exception  Json Parsing Fail : " + e.getMessage() );
            return null;
        }


    }


    /**
     * Json 对象到androidInfo
     * @param object
     * @return
     */
    private static androidInfo ParsingToandroidInfoByJson(JSONObject object)
    {
        androidInfo info;
        if (Info == null)
        {
             info = new androidInfo();
        }else
        {
            info = Info;
        }

        try {
            info.android_id = object.getString("android_id");
            info.IMEI = object.getString("IMEI");
            info.Packages = object.getString("package").split("\\|");
        } catch (JSONException e) {
            Log.e(staticSource.TAG, "ParsingToandroidInfoByJson Exception : " + e.getMessage() );
            return null;
        }

        return info;
    }



}
