package com.instagram.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.net.Socket;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedBridge;
import andhook.lib.xposed.XposedHelpers;



/**
 * instagram Hooks
 */
public class Manager {


    public static String TAG ="King";
    public static Application apps = null;

    /**
     * 初始化hook方法加载
     *
     * @param application 程序的application
     */
    public static void init(Application application) {

        //Log.i(TAG, "Start init");
        //apps = application;
        //mqttGetSocket();
        //getToken();
        //getChatMessage(application);
        //getRequestAndResponse();
    }


    /**
     * Hook HttpResponse Body
     */
    public static void getRequestAndResponse(ClassLoader load )
    {
        final Class<?> requestAndResponse = XposedHelpers.findClass("X.0nv", load);
        XposedBridge.hookAllMethods(requestAndResponse, "A00", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                StringBuilder builder = new StringBuilder();
                if(param.args.length >= 2)
                {
                    Object obj =  param.args[1];
                    String RequestContent =  PrintMessageTools.GetRequstContent(obj);
                    builder.append("\r\n---------------------------- Request ----------------------------\r\n"+RequestContent+"\r\n\r\n");
                }



                //获取响应结果
                Object obj = param.getResult();
                if (obj == null) {
                    Log.i(PrintMessageTools.InternalTag, "afterHookedMethod: onBody this object is null ");
                    return;
                }

                String RequestContent = PrintMessageTools.GetResponceContent(obj);
                builder.append("\r\n---------------------------- Response ----------------------------\r\n"+RequestContent+"\r\n\r\n");


                PrintMessageTools.Intelligentoutput(builder);

            }
        });

    }


    /**
     * Get mqtt Socket object
     */
    public static void mqttGetSocket() {
        //查找类
        Class<?> cls = XposedHelpers.findClass("X.0La", apps.getClassLoader());

        XposedBridge.hookAllConstructors(cls, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "mqttGetSocket Constructor args Length : " + param.args.length);
                super.afterHookedMethod(param);
            }
        });


        XposedBridge.hookAllMethods(cls, "A01", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (param.getResult() != null) {
                    try {
                        Socket soc = (Socket) param.getResult();
                        String addr = soc.getInetAddress().getHostAddress();
                        int Port = soc.getPort();
                        Log.i(TAG, String.format("mqttGetSocket: [addr : %s],[port : %s]", addr, Port));
                    } catch (Exception e) {
                        Log.i(TAG, String.format("mqttGetSocket: [ErrorMessage : %s]", e.getMessage()));
                    }

                }
                super.afterHookedMethod(param);
            }
        });


    }


    /**
     * Get mqtt getToken object
     */
    public static void getToken()
    {
        //查找类
        Class<?> cls = XposedHelpers.findClass("X.2f3", apps.getClassLoader());
        XposedBridge.hookAllMethods(cls,"A01", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(TAG, "getToken args Length : " + param.args.length);
                super.afterHookedMethod(param);
            }
        });

    }


    /**
     * Get mqtt chat Message
     */
    public static void getChatMessage(Application application)
    {
        //查找类
        Class<?> cls = XposedHelpers.findClass("X.0Yf", apps.getClassLoader());
        if (cls == null)
            Log.i(TAG, "getChatMessage: find class failure ");
        Log.i(TAG, "getChatMessage: find class Success ");

        XposedBridge.hookAllMethods(cls,"A07", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

                Log.i(TAG, "getChatMessage afterHookedMethod: start");
                if (param.args.length > 0) {
                    String res = param.args[0].toString();
                    Log.i(TAG, "getChatMessage Content : ["+res+"]" );
                }

                super.afterHookedMethod(param);
            }

        });

    }


}
