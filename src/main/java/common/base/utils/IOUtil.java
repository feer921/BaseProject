package common.base.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 针对IO流的工具类
 * <br/>
 * 2015年12月23日-上午11:02:33
 * @author lifei
 */
public class IOUtil {
    /**
     * 关闭输入输出流
     * @param io
     * @return
     */
    public static boolean safeCloseIO(Closeable io){
        if(io != null){
            try {
                io.close();
                return true;
            } catch (Exception e) {
                CommonLog.e("info",io +" --> close occur " + e);
            }
        }
        return false;
    }
    /**
     * 从输入流中读取必要长度的字节
     * 作用为：读取一段命令(数据)时，本来完整的数据为N个字节，但是并不是一次读取正好能读取到N个字节,
     * 因此用该方法规避一次读取不完整的问题
     * @param needLen 需要读取完整的字节数
     * @param curInputStream 
     * @return 正确读取出的必须长度的字节数组 或者指定长度的空字节数据
     * @throws IOException
     */
    public static byte[] readNeedLenByte(int needLen,InputStream curInputStream) throws IOException{
        byte [] back = new byte [needLen];
        int pos = 0;
        while( needLen > 0){
            byte [] temp = new byte [needLen];
            int len = curInputStream.read(temp);
            if (len < 0) {
                throw new IOException("inputStream read to the end");
            }
            needLen = needLen -len;
            System.arraycopy(temp, 0, back, pos, len);
            pos += len;
        }
        return back;
    }
    /**
     * 从输入流中读取必要长度的字节
     * 作用为：读取一段命令(数据)时，本来完整的数据为N个字节，但是并不是一次读取正好能读取到N个字节,
     * 因此用该方法规避一次读取不完整的问题
     * @param needLen 需要读取完整的字节数
     * @param curInputStream
     * @return 正确读取出的必须长度的字节数组 或者指定长度的空字节数据
     * @throws IOException
     */
    public static byte[] readNeedLenByte(int needLen,InputStream curInputStream,String tagOwner)
            throws IOException{
        byte [] back = new byte [needLen];
        int pos = 0;
        while( needLen > 0){
            byte [] temp = new byte [needLen];
            int len = curInputStream.read(temp);
            CommonLog.w("info",tagOwner +"--> readNeedLenByte() needLen = " +needLen + " temp " +
                    "bytes = " + ByteUtil
                    .bytes2HexStr(temp) +" read len = " + len);
            if (len < 0) {
                throw new IOException("inputStream read to the end");
            }
            needLen = needLen -len;
            System.arraycopy(temp, 0, back, pos, len);
            pos += len;
        }
        return back;
    }
    public static void letCurThreadSleep(long timeMills){
        try {
            Thread.sleep(timeMills);
        } catch (InterruptedException e) {
        }
    }
}
