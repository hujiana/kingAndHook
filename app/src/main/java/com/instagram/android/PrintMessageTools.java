package com.instagram.android;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class PrintMessageTools {


    public static String InternalTag = "kout";


    @SuppressWarnings("deprecation")
    private static StringBuilder LoacMessage(HttpUriRequest request, StringBuilder builder) {
        StringBuilder buf = new StringBuilder();
        if (request != null) {

            //buf.append("args-oneClass:" + request.getClass() + "\r\n");
            buf.append(request.getURI() + "\r\n");
            buf.append(request.getMethod() + "\r\n");

            if (request.getAllHeaders().length > 0) {
                buf.append("Headers:---------------------------------------------\r\n");
                for (Header header : request.getAllHeaders()) {

                    buf.append(header.getName() + ":" + header.getValue() + "\r\n");
                }
            }

            if (request.getMethod().equals("POST") && builder != null) {
                buf.append("Content:---------------------------------------------\r\n");
                buf.append(URLDecoder.decode(builder.toString()) + "\r\n");
            }

        }

        return buf;

    }

    /**
     * HttpPost Method Parsing
     * @param request
     * @return
     */
    private static StringBuilder PostHttp(HttpUriRequest request) {
        StringBuilder SendContent = new StringBuilder();
        try {
            HttpEntity entity = ((HttpPost) request).getEntity();
            ByteArrayInputStream Byte = null;

            if ("java.io.ByteArrayInputStream".equals(entity.getContent().getClass().getName())) {
                Byte = (ByteArrayInputStream) entity.getContent();
                Field[] feilds = Byte.getClass().getDeclaredFields();
                if (feilds.length < 1) {
                    SendContent.append("Fields Count : " + feilds.length);
                }
                byte[] buf1 = null;
                for (Field f : feilds) {
                    f.setAccessible(true);
                    String name = f.getName();
                    if (name.equals("count")) {
                        SendContent.append("Count : " + f.getInt(Byte) + "\r\n");
                    }
                    if (name.equals("buf")) {
                        buf1 = (byte[]) f.get(Byte);
                        SendContent.append("Content : " + new String(buf1) + "\r\n");
                    }

                }

            } else if ("X.2Ba".equals(entity.getContent().getClass().getName())) {
                // HttpPost X.2Ba
                Object cls = entity.getContent();
                SendContent.append("HttpPost X.2Ba\r\n");
                ReadX2baContent(cls, SendContent);

            }
        } catch (Exception e) {
            try {
                Class cls = ((HttpPost) request).getEntity().getContent().getClass();
                Log.i(InternalTag, "Content Class :" + cls.getName());
            } catch (Exception e1) {
                ExceptionOutMessage("PostHttp", e1);
            }

        }

        return SendContent;

    }

    private static void ReadX2baContent(Object cls, StringBuilder builder) throws NoSuchMethodException {

        Class<?> x2ba = cls.getClass();
        if (!x2ba.getName().equals("X.2Ba")) {
            // not Equals java.io.FilterInputStream
            builder.append("Two  -->  Get Class Success : " + x2ba.getName());
            return;
        }

        // java.io.FilterInputStream
//			FilterInputStream file = null;

        try {
            // 反射获取的到类的read方法
            builder.append("One --> Get Class Success : " + x2ba.getName() + "\r\n");
            Class<?> Filter = Class.forName(FilterInputStream.class.getName());
            Field Position = Filter.getDeclaredField("in");
            Position.setAccessible(true);
            SequenceInputStream sequence = (SequenceInputStream) Position.get(cls);

            //继续反射获取buff
            Class<?> Seq = Class.forName(SequenceInputStream.class.getName());
            Field in = Seq.getDeclaredField("in");
            in.setAccessible(true);
            ByteArrayInputStream ByteArray = (ByteArrayInputStream) in.get(sequence);

            //继续反射获取ByteArray的buff
            Class<?> ByteArrayPojo = Class.forName(ByteArrayInputStream.class.getName());
            Field buf = ByteArrayPojo.getDeclaredField("buf");
            buf.setAccessible(true);
            byte[] buffer = (byte[]) buf.get(ByteArray);

            builder.append(new String(buffer));


        } catch (Exception ex) {
            ExceptionOutMessage("Reflection Method ", ex);
        }


    }

    private static void ExceptionOutMessage(String MethodName, Exception e) {
        String Message = e.getMessage();
        StringBuilder builder = new StringBuilder();
        builder.append("Error Method  : " + MethodName + "\r\n");
        builder.append("Error Message : " + e.toString() + "\r\n");
        builder.append("Stack Trace : \r\n");
        for (StackTraceElement stack : e.getStackTrace()) {
            builder.append("File Name : [" + stack.getFileName() + "],Class Name :[ " + stack.getClassName()
                    + "],Method Name : [" + stack.getMethodName() + "],Line Number : [" + stack.getLineNumber()
                    + "]\r\n");
        }
        Log.i(InternalTag, builder.toString());
    }

    /**
     * 输出响应请求头
     */
    public static String PrintHeaderContent(ArrayList<Object> HeaderArray,int mouble) {
        StringBuilder builder = new StringBuilder();
        for (Object headerLine : HeaderArray) {
            try {
                Class on9Cls = headerLine.getClass();
                Field a00fl = on9Cls.getDeclaredField("A00");
                a00fl.setAccessible(true);
                if (mouble == 0)
                {
                    if (a00fl.get(headerLine).toString().equals("Set-Cookie") || a00fl.get(headerLine).toString().equals("Content-Type")) {
                        Method method = headerLine.getClass().getDeclaredMethod("toString");
                        String headerLineStr = method.invoke(headerLine).toString();
                        builder.append(headerLineStr + "\r\n");
                    }
                }else
                {
                    Method method = headerLine.getClass().getDeclaredMethod("toString");
                    String headerLineStr = method.invoke(headerLine).toString();
                    builder.append(headerLineStr + "\r\n");
                }

            } catch (Exception e) {

            }

        }

        return builder.toString();

    }
    /**
     * 输出请求协议头
     */
    public static String PrintCookieContent(CookieManager Cookie) {
        //StringBuilder builder = new StringBuilder();
        try {
            List<HttpCookie> Cookies = Cookie.getCookieStore().getCookies();
            return TextUtils.join(";", Cookies);
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 获取请求信息内容
     */
    public static String GetRequstContent(Object Request) {
        Class RequestCls = Request.getClass();
        StringBuilder builder = new StringBuilder();
        try {
            Field LocalRequest = RequestCls.getDeclaredField("A00");
            LocalRequest.setAccessible(true);
            Object LocalRequestobj = LocalRequest.get(Request);
            Class LocalRequestCls = LocalRequestobj.getClass();


            //获取URL地址
            Field URLfl = LocalRequestCls.getDeclaredField("A08");
            URLfl.setAccessible(true);
            String URLAddress = ((URI) URLfl.get(LocalRequestobj)).toString();
            builder.append(URLAddress + "\r\n");

            //获取请求协议头
            Field Headerfl = LocalRequestCls.getDeclaredField("A02");
            Headerfl.setAccessible(true);
            ArrayList<Object> Headers = (ArrayList<Object>) Headerfl.get(LocalRequestobj);
            String Header = PrintHeaderContent(Headers,1);
            builder.append(Header + "\r\n");

            //获取Cookies
            Field Cookiefl = LocalRequestCls.getDeclaredField("A01");
            Cookiefl.setAccessible(true);
            CookieManager cookieManager = (CookieManager) Cookiefl.get(LocalRequestobj);
            String Cookie = PrintCookieContent(cookieManager);
            builder.append("Cookies : " + Cookie + "\r\n");

            //获取请求内容
            Field RequestBodyfl = LocalRequestCls.getDeclaredField("A00");
            RequestBodyfl.setAccessible(true);
            Object RequestBodyobj = RequestBodyfl.get(LocalRequestobj);
            if (RequestBodyobj != null) {
                Class RequestBodyCls = RequestBodyobj.getClass();

                if (RequestBodyCls.getName().equals("X.0w7")) {
                    //文本类型数据请求
                    Field RequestTypefl = RequestBodyCls.getDeclaredField("A01");
                    RequestTypefl.setAccessible(true);
                    String RequestType = RequestTypefl.get(RequestBodyobj).toString();
                    builder.append("Request Type : " + RequestType + "\r\n");

                    Field ByteArrayfl = RequestBodyCls.getDeclaredField("A03");
                    ByteArrayfl.setAccessible(true);
                    String RequestContent = new String((byte[]) ByteArrayfl.get(RequestBodyobj));
                    builder.append("Request Content : " + RequestContent + "\r\n");


                } else if (RequestBodyCls.getName().equals("X.0wG")) {
                    //上传数据类型请求
                    Field RequestTypefl = RequestBodyCls.getDeclaredField("A02");
                    RequestTypefl.setAccessible(true);
                    String RequestType = RequestTypefl.get(RequestBodyobj).toString();
                    builder.append("Request Type : " + RequestType + "\r\n");
                } else if (RequestBodyCls.getName().equals("X.0wH")) {
                    //上传数据类型请求
                    Field RequestTypefl = RequestBodyCls.getDeclaredField("A00");
                    RequestTypefl.setAccessible(true);
                    String RequestType = RequestTypefl.get(RequestBodyobj).toString();
                    builder.append("Request Type : " + RequestType + "\r\n");

                    Field ByteArrayfl = RequestBodyCls.getDeclaredField("A01");
                    ByteArrayfl.setAccessible(true);
                    String RequestContent = new String((byte[]) ByteArrayfl.get(RequestBodyobj));
                    builder.append("Request Content : " + RequestContent + "\r\n");


                } else {
                    StringBuilder LoaclBUF = new StringBuilder();
                    Field[] Fields = RequestBodyCls.getDeclaredFields();
                    for (Field field : Fields) {
                        LoaclBUF.append(field.getType().getName() + " " + field.getName() + "|");
                    }
                    builder.append("Request Class Name : " + RequestBodyCls.getName() + "\r\n" + LoaclBUF.toString() + "\r\n");
                }


            }


            return builder.toString();
        } catch (Exception e) {

            PrintExceptionStack(e);
            return e.getMessage();
        }


    }
    /**
     * 获取响应内容
     */
    public static String GetResponceContent(Object Request) {
        Class ResponseCls = Request.getClass();
        StringBuilder builder = new StringBuilder();
        try {

            //获取响应头
            Field Headerfl = ResponseCls.getDeclaredField("A00");
            Headerfl.setAccessible(true);
            ArrayList<Object> HeaderList = (ArrayList<Object>) Headerfl.get(Request);
            String Headers = PrintHeaderContent(HeaderList,0);
            builder.append(Headers + "\r\n\r\n");

            if (Headers.contains("image/jpeg") || Headers.contains("video/mp4"))
            {
                builder.append(" Content Automatic neglect\r\n");
                return builder.toString();
            }

            //获取响应内容
            Field Bodyfl = ResponseCls.getDeclaredField("A02");
            Bodyfl.setAccessible(true);
            Object onB = Bodyfl.get(Request);
            Class OnbClass = onB.getClass();

            Method GetInputStream = OnbClass.getDeclaredMethod("ACK");
            GetInputStream.setAccessible(true);
            InputStream stream = (InputStream) GetInputStream.invoke(onB);
            ByteArrayOutputStream outStream = GetInputStreamContent(stream);
            String Content = new String(outStream.toByteArray());
            builder.append(Content);

            //还原输入流
            Field A00fl =  OnbClass.getDeclaredField("A00");
            A00fl.setAccessible(true);
            InputStream inpt = new ByteArrayInputStream(outStream.toByteArray()) ;
            A00fl.set(onB,inpt);

            return builder.toString();
        } catch (Exception e) {
            PrintExceptionStack(e);
            return e.getMessage();
        }


    }

    /**
     * 读取指定输入流的内容
     */
    public static ByteArrayOutputStream GetInputStreamContent(InputStream Stream) {
        int LocalSize = 1024;
        int COunt = 0;
        byte[] Buf = new byte[LocalSize];
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            //Stream.mark(0);
            while ((COunt = Stream.read(Buf, 0, LocalSize)) > 0) {
                buf.write(Buf,0,COunt);
                Buf = new byte[LocalSize];
            }
            //Stream.reset();
            return buf;
        } catch (Exception e) {
            return null;
        }


    }

    /**
     * 输出异常堆栈信息
     */
    public static void PrintExceptionStack(Exception e) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement Element : e.getStackTrace()) {
            builder.append(Element.toString() + "\r\n");
        }
        Log.i(InternalTag, builder.toString());
    }

    /**
     * 智能输出信息
     * */
    public static void Intelligentoutput(StringBuilder Message)
    {
        //Message.insert(0,"\r\n-------------------------Start-------------------------\r\n");
        //Message.append("\r\n-------------------------End-------------------------\r\n");
        Double Count =  Message.length() / 2000.00;
        int CountNum =  (int)Math.ceil(Count);

        for (int x = 0 ; x < CountNum ; x++)
        {
            String Content = null;
            if (CountNum- 1 == x)
            {
                Content =   Message.substring(x*2000);
            }else
            {
                Content =   Message.substring(x*2000,(x+1)*2000);
            }

            Log.i(InternalTag, Content);

        }


    }





}

