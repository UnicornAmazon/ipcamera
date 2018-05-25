package com.afscope.ipcamera.utils;

import android.Manifest;
import android.os.Process;

import com.afscope.ipcamera.MyApplication;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;

import org.apache.commons.lang.time.FastDateFormat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/11/24 0024.
 *
 */
public class Log {
    private static final String TAG = "ipcamera";
    private final static int DAY_LIMIT_OF_KEEPING_LOG_FILE = 5;  //默认保存日志的天数
    private static final String APP_LOG_FILE_SUFFIX = ".log";
    private static final String DEFAULT_XLOG_BACKUP_FILE_SUFFIX = ".bak";
    private static String logFilesDir;

    private static ThreadLocal<SimpleDateFormat> mLocalDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }
    };

    public static void init(boolean isDebuggable){
        //检查权限
        boolean hasPermission = Utils.isPermissionGranted(MyApplication.getInstance(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(/*BuildConfig.DEBUG*/isDebuggable ? LogLevel.INFO   // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                        : LogLevel.DEBUG)
                .tag(TAG)                                                // 指定默认TAG
//                .t()                                                   // 允许打印线程信息，默认禁止
//                .st(6)                                                 // 允许打印深度为2的调用栈信息，默认禁止
//                .b()                                                   // 允许打印日志边框，默认禁止
//                .jsonFormatter(new MyJsonFormatter())                  // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
//                .xmlFormatter(new MyXmlFormatter())                    // 指定 XML 格式化器，默认为 DefaultXmlFormatter
//                .throwableFormatter(new MyThrowableFormatter())        // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
//                .threadFormatter(new MyThreadFormatter())              // 指定线程信息格式化器，默认为 DefaultThreadFormatter
//                .stackTraceFormatter(new MyStackTraceFormatter())      // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
//                .borderFormatter(new MyBoardFormatter())               // 指定边框格式化器，默认为 DefaultBorderFormatter
//                .addObjectFormatter(AnyClass.class,                    // 为指定类添加格式化器
//                        new AnyClassObjectFormatter())                     // 默认使用 Object.toString()
//                .addInterceptor(new BlacklistTagsFilterInterceptor(    // 添加黑名单 TAG 过滤器
//                        "blacklist1", "blacklist2", "blacklist3"))
//                .addInterceptor(new MyInterceptor())                   // 添加一个日志拦截器
                .build();

        Printer androidPrinter = new AndroidPrinter();             // 通过 android.util.Log 打印日志的打印器
        Printer[] printers;
        if (hasPermission){
            logFilesDir = Utils.getLogFilesDir().getAbsolutePath();
            checkExpiredLogFiles(logFilesDir);
            Printer filePrinter = new FilePrinter                      // 打印日志到文件的打印器
                    .Builder(logFilesDir)                              // 指定保存日志文件的路径
                    .fileNameGenerator(new MyLogFileNameGenerator())        // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                    .backupStrategy(new FileSizeBackupStrategy(50 * 1024 * 1024))         // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                    .logFlattener(new MyFlattener())                       // 指定日志平铺器，默认为 DefaultFlattener
                    .build();
            printers = new Printer[]{androidPrinter, filePrinter};
        } else {
            printers = new Printer[]{androidPrinter};
        }

        XLog.init(                                               // 初始化 XLog
                config,                                          // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                printers);
    }

    public static void v(String tag, String msg){
        XLog.tag(tag).v(msg);
    }
    public static void d(String tag, String msg){
        XLog.tag(tag).d(msg);
    }
    public static void i(String tag, String msg) {
        XLog.tag(tag).i(msg);
    }
    public static void e(String tag, String msg){
        XLog.tag(tag).e(msg);
    }
    public static void e(String tag, String msg, Throwable throwable){
        XLog.tag(tag).e(msg, throwable);
    }
    public static void v(String msg){
        XLog.v(msg);
    }
    public static void d(String msg){
        XLog.d(msg);
    }
    public static void i(String msg){
        XLog.i(msg);
    }

    //检查是否有超过5天的日志，如果有，则删除
    private static void checkExpiredLogFiles(String dir){
        android.util.Log.i(TAG, "checkExpiredLogFiles: ");
        File logDir = new File(dir);
        String[] pathNames = logDir.list();
        if(pathNames == null || pathNames.length < 3){    //至少保存两份日志文件，不管时间(机器时间重启后可能被重置)
            return;
        }
        Date now = new Date();
        Date tmpDate = null;
        long interval = 0L;
        String dateString = null;
        boolean result = false;
        for (String string : pathNames) {
            try {
                if (string.endsWith(APP_LOG_FILE_SUFFIX)){
                    dateString = string.substring(0, string.length() - APP_LOG_FILE_SUFFIX.length());
                } else if (string.endsWith(DEFAULT_XLOG_BACKUP_FILE_SUFFIX)){
                    dateString = string.substring(0,
                            string.length()
                            - APP_LOG_FILE_SUFFIX.length()
                            - DEFAULT_XLOG_BACKUP_FILE_SUFFIX.length());
                } else {
                    //非日志文件，直接删除
                    result = Utils.deleteFile(new File(logDir, string));
                    android.util.Log.i(TAG, "checkExpiredLogFiles: find an file: "+string
                            +" without log suffix, delete result: "+result);
                    continue;
                }

                android.util.Log.i(TAG, "checkExpiredLogFiles: file name no suffix: "+dateString);
                tmpDate = mLocalDateFormat.get().parse(dateString);
                if (tmpDate == null){
                    //无法解析出日期的文件，直接删除
                    result = Utils.deleteFile(new File(logDir, string));
                    android.util.Log.i(TAG, "checkExpiredLogFiles: find an file: "+string
                            +" with log suffix, but cannot be parse to date, delete result: "+result);
                    continue;
                }

                interval = TimeUnit.DAYS.convert(now.getTime() - tmpDate.getTime(), TimeUnit.MILLISECONDS);
                android.util.Log.i(TAG, "checkExpiredLogFiles: file name parse to date: "+tmpDate
                        +", now date: "+now);
                if (interval > DAY_LIMIT_OF_KEEPING_LOG_FILE) {
                    //超过5天的日志
                    result = Utils.deleteFile(new File(logDir, string));
                    android.util.Log.i(TAG, "checkExpiredLogFiles: delete file: "+string
                            +" result: "+result);
                }
            } catch (Exception e) {
                android.util.Log.e(TAG, "error when parse filename:"+string+" in log dir");
            }
        }
    }

    private static class MyFlattener implements Flattener {
        private static FastDateFormat formatter = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        @Override
        public CharSequence flatten(int logLevel, String tag, String message) {
            StringBuilder builder = new StringBuilder(formatter.format(new Date()));
            builder.append(" ")
                    .append(Process.myPid())
                    .append(" ")
                    .append(LogLevel.getShortLevelName(logLevel))
                    .append("/")
                    .append(tag)
                    .append(": ")
                    .append(message);

            return builder.toString();
        }
    }

    private static class MyLogFileNameGenerator implements FileNameGenerator {
        @Override
        public boolean isFileNameChangeable() {
            return true;
        }

        @Override
        public String generateFileName(int logLevel, long timestamp) {
            SimpleDateFormat sdf = mLocalDateFormat.get();
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(new Date(timestamp)) + APP_LOG_FILE_SUFFIX;
        }
    }

    //清除所有日志文件
    public static final void clearAllLogFiles(){
        boolean result = Utils.deleteFile(Utils.getLogFilesDir());
        Log.i(TAG, "clearAllLogFiles: result: "+result);
    }
}
