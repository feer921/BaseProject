package common.base.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 字节操作工具
 * <br/>
 * 2015年12月25日-下午11:48:53
 * @author lifei
 */
public class ByteUtil {
    public static byte[] mix2ByteArray(byte[] headerByte,byte[] contentByte){
//        if(headerByte == null || contentByte == null) return null;
        if (headerByte == null) {
            return contentByte;
        }
        if (contentByte == null) {
            return headerByte;
        }
        int headerLen = headerByte.length;
        int contentByteLen = contentByte.length;
        if (headerLen == 0 && contentByteLen == 0) {
            return null;
        }
        if (headerLen == 0) {
            return contentByte;
        }
        if(contentByteLen == 0){
            return headerByte;
        }
        byte[] result = new byte[headerLen + contentByteLen];
        System.arraycopy(headerByte, 0, result, 0, headerLen);
        System.arraycopy(contentByte, 0, result, headerLen, contentByteLen);
        return result;
    }

    /**
     * 混合多个byte数组成一个byte数组
     * @param bytes 可变的byte数组
     * @return 混合后的byte数组
     */
    public static byte[] mixMultyBytes(byte[]... bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        int totalLen = 0;
        for (byte[] curByte : bytes) {
            if (curByte != null && curByte.length > 0) {
                totalLen += curByte.length;
            }
        }
        if (totalLen == 0) {
            return null;
        }
        byte[] result = new byte[totalLen];
        int curToWriteIndex = 0;
        for (byte[] curBytes : bytes) {
            if (curBytes != null && curBytes.length > 0) {
                int curBytesLen = curBytes.length;
                System.arraycopy(curBytes, 0, result, curToWriteIndex, curBytesLen);
                curToWriteIndex += curBytesLen;
            }
        }
        return result;
    }
    /**
     * 打印输入字节数组
     * @param data
     * @param oneLine
     * @param tag
     */
    public static void printByteData(byte[] data,boolean oneLine,String tag){
        if(data == null) return;
        int i = 0;
        StringBuilder sb = null;
        if(oneLine){
            sb = new StringBuilder();
        }
        for(byte b : data){
            String info = "byte[" + i + "] = " + b;
            if(!oneLine){
                CommonLog.i(tag, info);
            }
            else{
                sb.append(info);
            }
        }
        if(sb != null){
            CommonLog.i(tag, sb);
        }
    }

    /**
     * 将byte数组数据据按十六进制打印出来
     * @param data
     * @param TAG
     */
    public static void printByteData2HexStr(byte[] data,String TAG) {
        if (data == null) {
            return;
        }
        CommonLog.i(TAG,bytes2HexStr(data));
    }

    /**
     * 仅将字节数组转换成HEX进制字符串(大写)，并且转换成的HEX不够2个长度前面加0；eg.: byte[]data{1,2,10} --> 01020A
     * @param data
     * @return
     */
    public static String bytes2HexStr(byte[] data) {
        return bytes2HexStr(data,true,null);
    }

    public static String aByte2HexStr(byte data) {
        int byte2IntValue = data & 0xFF;
        String aUpperCaseHexStr = Integer.toHexString(byte2IntValue).toUpperCase();
        return aUpperCaseHexStr.length() == 1 ? "0" + aUpperCaseHexStr : aUpperCaseHexStr;
    }
    /**
     * 字节数组转换成HEX进制的字符串(大写显示)
     * @param data 需要转换的字节数组
     * @param toHexNeedLen2 一个字节转换成HEX进制时是否需要2个长度的字符串，即不够2个长度时前面添0
     * @param splitStr 两个字节转各自转换成HEX进制后之间的分隔字符 eg.: FF:EE 其中“:”为分隔字符
     * @return 转换成HEX进行的字符串
     */
    public static String bytes2HexStr(byte[] data,boolean toHexNeedLen2, String splitStr) {
        if (data == null || data.length < 1) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            int b2IntValue = b & 0xFF;
            String hexStr = Integer.toHexString(b2IntValue).toUpperCase();
            if (toHexNeedLen2) {
                sb.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }
            else {
                sb.append(hexStr);
            }
//            sb.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            if (!Util.isEmpty(splitStr)) {
                sb.append(splitStr);
            }
        }
        return sb.toString();
    }
    /**
     * 从已有一个字节数组中复制需要的长度的字节
     * @param targetBytes 被复制的目标字节数组
     * @param toCopyLen 需要复制的长度(从左往右顺序)
     * @return 复制出的新数组，or null;
     */
    public static byte[] copyBytesFromTarget(byte[] targetBytes,int toCopyLen) {
        if (targetBytes == null) {
            return null;
        }
        int targetBytesLen = targetBytes.length;
        if(targetBytesLen < 1){
            return null;
        }
        if (toCopyLen > targetBytesLen || toCopyLen <= 0) {
            return null;
        }
        byte[] copyResult = new byte[toCopyLen];
        System.arraycopy(targetBytes, 0, copyResult, 0, toCopyLen);
        return copyResult;
    }

    /**
     * 从目标字节数组中复制出想要的部分字节数据
     * @param targetBytes 被复制的源字节数组
     * @param copyFromIndex 从哪个下序号开始复制，注：下标从0开始
     * @param toCopyLen 要复制的字节长度
     * @return 复制出的新字节数组
     */
    public static byte[] copyBytesFromTarget(byte[] targetBytes, int copyFromIndex, int toCopyLen) {
        if (targetBytes == null || copyFromIndex < 0 || toCopyLen <= 0) {
            return null;
        }
        int tagetBytesArrayLen = targetBytes.length;
        if (tagetBytesArrayLen < 1) {
            return null;
        }
        if ((copyFromIndex + toCopyLen) > tagetBytesArrayLen) {
            return null;
        }
        byte[] copyResultBytes = new byte[toCopyLen];
        System.arraycopy(targetBytes,copyFromIndex,copyResultBytes,0,toCopyLen);
        return copyResultBytes;
    }
    /**
     * 计算出一个最多4个字节的byte数组的int值; 适合高位在前，低位在后的情况
     * @param toCalculateMax4Bytes 最多4个字节的数组，因为int只能装下4个字节
     * @return 计算出的int值
     */
    public static int bytesArray2Int(byte[] toCalculateMax4Bytes) {
        if (toCalculateMax4Bytes == null || toCalculateMax4Bytes.length == 0) {
            return 0;
        }
        if(toCalculateMax4Bytes.length > 4){
            return 0;//超出int范围了
        }
        byte[] full4Bytes = new byte[4];
        //与4个字节相差几个字节,如果toCalculateMax4Bytes 只有2个字节，则这2个字节要复制到full4Bytes的后两位上
        //即执行arraycopy时，从目标字节数组的第2个字节写入
        int destinationStartIndex = 4 - toCalculateMax4Bytes.length;
        System.arraycopy(toCalculateMax4Bytes,0,full4Bytes,destinationStartIndex,toCalculateMax4Bytes.length);
        //再去计算
        int result = 0;
        result = (int)
                ( ((full4Bytes[0] & 0xFF)<<24)
                |((full4Bytes[1] & 0xFF)<<16)
                |((full4Bytes[2] & 0xFF)<<8)
                |(full4Bytes[3] & 0xFF));
        return result;
    }

    /**
     * 计算出一个最多4个字节的byte数组的int值; 适合低位在前、高位在后
     * @param toCalculateMax4Bytes 最多4个字节的数组，因为int只能装下4个字节
     * @return 计算出的int值
     */
    public static int bytesArray2IntLHOrder(byte[] toCalculateMax4Bytes) {
        if (toCalculateMax4Bytes == null || toCalculateMax4Bytes.length == 0) {
            return 0;
        }
        if (toCalculateMax4Bytes.length > 4) {
            return 0;//超出int范围了
        }
        byte[] full4Bytes = new byte[4];
        //与4个字节相差几个字节,如果toCalculateMax4Bytes 只有2个字节，则这2个字节要复制到full4Bytes的后两位上
        //即执行arraycopy时，从目标字节数组的第2个字节写入
        int destinationStartIndex = 4 - toCalculateMax4Bytes.length;
        System.arraycopy(toCalculateMax4Bytes,0,full4Bytes,destinationStartIndex,toCalculateMax4Bytes.length);
        //再去计算
        int value;
        value = (int) ((full4Bytes[0]&0xFF)
                | ((full4Bytes[1]<<8) & 0xFF00)
                | ((full4Bytes[2]<<16)& 0xFF0000)
                | ((full4Bytes[3]<<24) & 0xFF000000));
        return value;
    }

    /**
     * 依据高位、低位字节顺序计算出一个字节数组中从偏移位置起 4个字节的 int值
     * @param data 要取值的字节数组
     * @param offset 偏移位置 eg.:从data数组中的0位置开始
     * @param isHlOrder 是否为 H高位在前，l低位在后 ; false: 则为l低位在前，H高位在后
     * @return 从offset偏移位置起4个字节内的int值
     */
    public static int bytes2IntBaseOrder(byte[] data,int offset,boolean isHlOrder) {
        if (data == null || offset < 0) {
            return 0;
        }
        if (data.length < offset + 4) {//data中的长度，不足以计算到 以offset开始的4个字节
            return 0;
        }
        int result = 0;
        if(isHlOrder){
            result = (int)
                    ( ((data[offset] & 0xFF)<<24)
                            |((data[offset + 1] & 0xFF)<<16)
                            |((data[offset + 2] & 0xFF)<<8)
                            |(data[offset + 3] & 0xFF));
        }
        else{//低位在前，高位在后
            result = (int) ((data[offset]&0xFF)
                    | ((data[offset + 1]<<8) & 0xFF00)
                    | ((data[offset + 2]<<16)& 0xFF0000)
                    | ((data[offset + 3]<<24) & 0xFF000000));
        }
        return result;
    }

    /**
     * int类型的值转换成指定长度的bytes数组表示
     * @param intValue
     * @param targetBytesLen 需要的表示该int值的数组长度
     * @return
     */
    public static byte[] int2Bytes(int intValue, int targetBytesLen) {
        byte[] source4Bytes = ByteBuffer.allocate(4).putInt(intValue).array();
        byte[] resultBytes = new byte[targetBytesLen];
        System.arraycopy(source4Bytes,4 - targetBytesLen,resultBytes,0,targetBytesLen);
        return resultBytes;
    }

    /**
     * 字节数组按编码格式转换成字符串
     * @param byteDatas 要转换的字节数组
     * @param charsetName 编码格式，如果为null，则使用系统默认
     * @return 按指定编码格式转换成的字符串
     */
    public static String byteArray2Str(byte[] byteDatas,String charsetName) {
        String byteStr = "";
        if (byteDatas != null && byteDatas.length > 0) {
            try {
                if (Util.isEmpty(charsetName)) {
                    byteStr = new String(byteDatas);
                }
                else{
                    byteStr = new String(byteDatas, charsetName);
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return byteStr;
    }

    /**
     * 将字节数组中每个字节上的值拼成字符串
     * eg.: byte b[]{1,1,2,3,6} --> "11236"
     * @param byteDatas
     * @return
     */
    public static String byteArray2StringDisplay(byte[] byteDatas) {
        String byteStr = "";
        if (byteDatas != null && byteDatas.length > 0) {
            for (byte b : byteDatas) {
                int value = b & 0xFF;
                byteStr += value + ":";
            }
        }
        return byteStr;
    }

    public static int parseStr2Int(String integerStr) {
        int result = -1;
        try {
            result = Integer.parseInt(integerStr);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 十六进制字符串转化成字节数组;字符串中2个字符对应一个字节
     * @param hexStr eg: fb0046 --> fb--> 11111011 00--> 00000000;46-->01000110
     * @return 十六进制的字符串的长度折半的长度的字节数组
     */
    public static byte[] hexStr2ByteArray(String hexStr) {
        if (Util.isEmpty(hexStr)) {
            return null;
        }
        hexStr = hexStr.toUpperCase();
        int hexStrLen = hexStr.length();
        if (hexStrLen % 2 != 0) {//长度不是2的整数倍,转化会不正常
            return null;
        }
        int toBytesLen = hexStrLen / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] d = new byte[toBytesLen];
        for (int i = 0; i < toBytesLen; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
