package com.instagram.android;

import android.app.Application;
import android.util.Log;

import java.net.Socket;

import cn.andHook.AndHook;
import cn.andHook.xposed.XC_MethodHook;
import cn.andHook.xposed.XposedBridge;
import cn.andHook.xposed.XposedHelpers;


/**
 * instagram Hooks
 */
public class Manager {


    public static String TAG = AndHook.TAG;
    public static ClassLoader load;


    /**
     * 初始化hook方法加载
     * @param application 程序的application
     */
    public static void init(Application application)
    {

        Log.i(TAG, "Start init");
        load = application.getClassLoader();
        mqttGetSocket();

    }


    public static void mqttGetSocket()
    {
        //查找类
        Class<?> cls =  XposedHelpers.findClass("X.0La",load);
        XposedBridge.hookAllMethods(cls,"A01",new XC_MethodHook(){

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (param.getResult() != null)
                {
                    try
                    {
                        Socket soc = (Socket)param.getResult();
                        String addr =  soc.getInetAddress().getHostAddress();
                        int Port = soc.getPort();
                        Log.i(TAG, String.format("mqttGetSocket: [addr : %s],[port : %s]",addr,Port));
                    }catch (Exception e)
                    {
                        Log.i(TAG, String.format("mqttGetSocket: [ErrorMessage : %s]",e.getMessage()));
                    }

                }
                super.afterHookedMethod(param);
            }
        });


    }




}
