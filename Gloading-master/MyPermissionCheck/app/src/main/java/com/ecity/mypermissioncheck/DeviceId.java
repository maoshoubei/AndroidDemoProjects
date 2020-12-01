package com.ecity.mypermissioncheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * @ProjectName: MyPermissionCheck
 * @Package: com.ecity.mypermissioncheck
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2020/7/21
 * @Version: 1.0
 */
public class DeviceId {
    private static final byte[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final String INVALID_MAC_ADDRESS = "02:00:00:00:00:00";

    private static final String INVALID_ANDROID_ID = "9774d56d682e549c";

    private static final String INVALID_Serial_No = "unknown";

    private static byte[] getMacInArray() {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            if (enumeration == null) {
                return null;
            }
            while (enumeration.hasMoreElements()) {
                NetworkInterface netInterface = enumeration.nextElement();
                if (netInterface.getName().equals("wlan0")) {
                    return netInterface.getHardwareAddress();
                }
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage(), e);
        }
        return null;
    }

    public static long getLongMac() {
        byte[] bytes = getMacInArray();
        if (bytes == null || bytes.length != 6) {
            return 0L;
        }
        long mac = 0L;
        for (int i = 0; i < 6; i++) {
            mac |= bytes[i] & 0xFF;
            if (i != 5) {
                mac <<= 8;
            }
        }
        return mac;
    }

    public static String getMacAddress() {
        String mac = formatMac(getMacInArray());
        if (TextUtils.isEmpty(mac) || mac.equals(INVALID_MAC_ADDRESS)) {
            return "";
        }
        return mac;
    }

    private static String formatMac(byte[] bytes) {
        if (bytes == null || bytes.length != 6) {
            return "";
        }
        byte[] mac = new byte[17];
        int p = 0;
        for (int i = 0; i <= 5; i++) {
            byte b = bytes[i];
            mac[p] = HEX_DIGITS[(b & 0xF0) >> 4];
            mac[p + 1] = HEX_DIGITS[b & 0xF];
            if (i != 5) {
                mac[p + 2] = ':';
                p += 3;
            }
        }
        return new String(mac);
    }

    public static String getAndroidID(Context context) {
        if (context != null) {
            @SuppressLint("HardwareIds")
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidId) && !INVALID_ANDROID_ID.equals(androidId)) {
                return androidId;
            }
        }
        return "";
    }

    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    public static String getSerialNo(Context context) {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    serial = Build.getSerial();
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
        }
        if (!TextUtils.isEmpty(serial) && !INVALID_Serial_No.equals(serial)) {
            return serial;
        }
        return "";
    }

    public static String getSubscriberId() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) App.getsInstance().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(App.getsInstance(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String imsi = mTelephonyMgr.getSubscriberId();
            if (!TextUtils.isEmpty(imsi)) {
                return imsi;
            }
            return imsi;
        }
        return "";
    }

    public static String getSimSerialNumber() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) App.getsInstance().getSystemService(Context.TELEPHONY_SERVICE);
        //返回SIM卡的序列号
        if (ActivityCompat.checkSelfPermission(App.getsInstance(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String simSerialNumber = mTelephonyMgr.getSimSerialNumber();
            if (!TextUtils.isEmpty(simSerialNumber)) {
                return simSerialNumber;
            }
        }
        return "";
    }

    public static String getSplitNo() {
        String serial = "";
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
       // serial = getSerialNo();
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getUniqueID(Context context) {
        String id = null;
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
            try {
                UUID uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                id = uuid.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(id)) {
            id = getUUID();
        }

        return TextUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
    }

    private static String getUUID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = android.os.Build.getSerial(); // TODO crash in Q
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial"; // 随便一个初始化
        }

        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取Android Id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取Build的部分信息
     *
     * @return
     */
    public static String getBuildInfo() {
        //这里选用了几个不会随系统更新而改变的值
        StringBuffer buildSB = new StringBuffer();
        buildSB.append(Build.BRAND).append("/");
        buildSB.append(Build.PRODUCT).append("/");
        buildSB.append(Build.DEVICE).append("/");
        buildSB.append(Build.ID).append("/");
        buildSB.append(Build.VERSION.INCREMENTAL);
        return buildSB.toString();
//        return Build.FINGERPRINT;
    }

    /**
     * 最终方案，获取设备ID
     *
     * @param context
     * @return
     */
    public static String getDeviceUUID(Context context) {
        String uuid = loadDeviceUUID(context);
        if (TextUtils.isEmpty(uuid)) {
            uuid = buildDeviceUUID(context);
            saveDeviceUUID(context, uuid);
        }
        return uuid;
    }

    private static String buildDeviceUUID(Context context) {
        String androidId = getAndroidId(context);
        if (!"9774d56d682e549c".equals(androidId)) {
            Random random = new Random();
            androidId = Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt())
                    + Integer.toHexString(random.nextInt());
        }
        return new UUID(androidId.hashCode(), getBuildInfo().hashCode()).toString();
    }

    private static void saveDeviceUUID(Context context, String uuid) {
        context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
                .edit()
                .putString("uuid", uuid)
                .apply();
    }

    @Nullable
    private static String loadDeviceUUID(Context context) {
        return context.getSharedPreferences("device_uuid", Context.MODE_PRIVATE)
                .getString("uuid", null);
    }

    /**
     * 生成16位不重复的随机数，含数字+大小写
     * @return
     */
    public static String getGUID() {
        StringBuilder uid = new StringBuilder();
        //产生16位的强随机数
        Random rd = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            //产生0-2的3位随机数
            int type = rd.nextInt(3);
            switch (type){
                case 0:
                    //0-9的随机数
                    uid.append(rd.nextInt(10));
                    break;
                case 1:
                    //ASCII在65-90之间为大写,获取大写随机
                    uid.append((char)(rd.nextInt(25)+65));
                    break;
                case 2:
                    //ASCII在97-122之间为小写，获取小写随机
                    uid.append((char)(rd.nextInt(25)+97));
                    break;
                default:
                    break;
            }
        }
        return uid.toString();
    }

    private static String prefixxStr(){
        String brand = Build.BRAND;
        if("".equals(brand) || brand == null){
            return "android-";
        }
        return brand + "-";
    }

    /**
     * 华为厂商判断
     *
     * @return
     */
    public static boolean isHuawei() {
        if (Build.BRAND == null) {
            return false;
        } else {
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    /**
     * 小米厂商判断
     *
     * @return
     */
    public static boolean isXiaomi() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("xiaomi");
    }

    /**
     * OPPO厂商判断
     *
     * @return
     */
    public static boolean isOPPO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("oppo");
    }

    /**
     * VIVO厂商判断
     *
     * @return
     */
    public static boolean isVIVO() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("vivo");
    }

    public static boolean isMeizu() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("meizu");
    }

    public static boolean isSamsung() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("samsung");
    }

    public static boolean isLeTV() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("letv");
    }

    public static boolean isSmartisan() {
        return Build.BRAND != null && Build.BRAND.toLowerCase().equals("smartisan");
    }

}
