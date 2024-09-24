package android_serialport_api;

import android.util.Log;

import java.text.SimpleDateFormat;


/**
 * @author Tim
 * 数据
 */
public class ComBean {
    private static final String TAG = "ComBean";
    public byte[] bRec = null;
    public String sRecTime = "";
    public String sComPort = "";

    public ComBean(String sPort, byte[] buffer, int size) {
        sComPort = sPort;
        bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            bRec[i] = buffer[i];
        }
        Log.d(TAG, "new String(ComRecData.bRec) = " + new String(buffer, 0, size));
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        sRecTime = sDateFormat.format(new java.util.Date());
    }
}