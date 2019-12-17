package cn.king.admin;

import android.app.Application;
import android.icu.text.IDNA;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import cn.king.admin.ChangeTool.DeviceChange;
import cn.king.admin.ChangeTool.androidInfo;
import cn.king.admin.ChangeTool.staticSource;

public class Manager {

    /**
     * hook 初始化框架
      * @param application
     */
    public static void init(Application application)
    {
        androidInfo Info =  androidInfo.ParsingContentToFile();
        Log.i(staticSource.TAG, "init package : "+application.getPackageName());
        if (Info == null)
        {
            Log.i(staticSource.TAG, "init: Load DeviceInfo Fail");
            return;
        }

        for (int i = 0 ; i < Info.Packages.length ; i++)
        {
            if (Info.Packages[i].equals(application.getPackageName()))
            {

                DeviceChange.GetInstance(Info,application);
                return;
            }

        }


    }








}
