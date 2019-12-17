package cn.king.admin.ChangeTool;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.UUID;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedBridge;
import andhook.lib.xposed.XposedHelpers;

import static com.instagram.android.Manager.getRequestAndResponse;

/**
 * 设备信息重定向
 */
public class DeviceChange {

    private androidInfo Info;

    private Context con;

    private static DeviceChange thisObject = null;

    private DeviceChange() {

    }

    private DeviceChange(androidInfo info, Context con) {

        Info = info;
        this.con = con;
    }


    /**
     * 获取一个改机对象
     * @param info 新注入的设备信息
     * @param con 上下文
     * @return
     */
    public static void GetInstance(androidInfo info, Context con)
    {

        if (thisObject == null)
        {
            thisObject = new DeviceChange(info,con);
        }else
        {
            thisObject.con = con;
            thisObject.Info = info;
        }

        //这里写需要hook的设备方法
        thisObject.changeDeviceID();
        //ins请求
        getRequestAndResponse(con.getClassLoader());
        //adid
        thisObject.changeadid();
        //Waterfaill
        thisObject.changeWaterfallid();

    }



    /**
     * hook android_id
     */
    public void changeDeviceID()
    {
        /*String ANDROID_ID = Settings.System.getString(con.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;*/

        Log.i(staticSource.TAG, "changeDeviceID");
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure",con.getClassLoader(),"getString", ContentResolver.class,String.class,new XC_MethodHook()
        {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(staticSource.TAG, "changeDeviceID Push start ");
                param.setResult(Info.android_id);
                Log.i(staticSource.TAG, "changeDeviceID Push over");
                super.afterHookedMethod(param);
            }
        });





    }


    /**
     * 修改google_ad_id
     */
    public  void changeadid()
    {

        Log.i(staticSource.TAG, "changeadid");
        XposedHelpers.findAndHookMethod("X.7sa",con.getClassLoader(),"A04",new XC_MethodHook()
        {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(staticSource.TAG, "changeadid Push start ");
                param.setResult(UUID.randomUUID().toString());
                Log.i(staticSource.TAG, "changeadid Push over");
                super.afterHookedMethod(param);
            }
        });




    }

    /**
     * 修改waterfall id
     */
    public  void changeWaterfallid()
    {

        Log.i(staticSource.TAG, "changeWaterfallid");
        XposedHelpers.findAndHookMethod("X.0Zw",con.getClassLoader(),"A00",new XC_MethodHook()
        {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(staticSource.TAG, "changeWaterfallid Push start ");
                param.setResult(UUID.randomUUID().toString());
                Log.i(staticSource.TAG, "changeWaterfallid Push over");
                super.afterHookedMethod(param);
            }
        });
    }







}
