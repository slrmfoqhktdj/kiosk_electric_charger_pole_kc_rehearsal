package android_serialport_api;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.idean.tools.ConvertedType;
import android_serialport_api.idean.tools.Debug;

public abstract class SerialOperatorHelper {

    static final String TAG = "SerialOperatorHelper";
    private int baudRate;
    private String sPort;
    private boolean isOpen = false;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private Handler mHandler;
    public long iDelay = 500;
    public boolean version = Debug.DEBUG_VESION;

    private static byte[] _bLoopData = new byte[]{0x30};

    public void open() throws SecurityException {

        mSerialPort = new SerialPort(sPort, baudRate);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
        isOpen = true;

    }

    public void close() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        isOpen = false;
    }

    //----------------------------------------------------
    public void send(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);

        } catch (IOException e) {
            Logger.e("send SerialOperator err : "+ e);
            e.printStackTrace();
        }
    }

    //----------------------------------------------------
    public void sendHex(String str) {
        byte[] bOutArray = ConvertedType.HexToByteArr(str);
        send(bOutArray);
    }

    //----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    public boolean setHandler(Handler haldler) {
        this.mHandler = haldler;
        return true;
    }

    public String getBaudRate() {
        return String.valueOf(baudRate);
    }

    public String setBaudRate(String baudRate) {
        if (isOpen) {
            return getBaudRate();
        } else {
            this.baudRate = Integer.parseInt(baudRate);
        }
        return getBaudRate();
    }

    public String getPort() {
        return sPort;
    }

    public void setPort(String sPort) {
        this.sPort = sPort;
    }

    public static byte[] getbLoopData() {
        return _bLoopData;
    }

    public boolean isOpen() {
        return isOpen;
    }

    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    //----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    //----------------------------------------------------
    public void setHexLoopData(String sHex) {
        this._bLoopData = ConvertedType.HexToByteArr(sHex);
    }

    //----------------------------------------------------
    public int getiDelay() {
        return (int) iDelay;
    }

    //----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    //----------------------------------------------------
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    //----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    //----------------------------------------------------
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null) return;
                    byte[] buffer = new byte[1024];
                    try {
                        Thread.sleep(50);//延时50ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int size = mInputStream.read(buffer);


                    if (size > 0 && !isInterrupted()) {
//                        Debug.showLog(version, TAG, "mInputStream.read = " + new String(buffer, 0, size) + "com == " + sPort, Debug.ERROR);
                       // ComBean ComRecData = new ComBean(sPort, buffer, size);
                       // onDataReceived(ComRecData);

//						mHandler.sendEmptyMessage(0);  

                        //需要数据传递，用下面方法；
                        Message msg = new Message();
                        msg.obj = buffer;
                        msg.arg1 = size;
//		                msg.obj = new String(buffer, 0, size);//可以是基本类型，可以是对象，可以是List、map等；  
                        msg.what = 0;
                        mHandler.sendMessage(msg);

                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                send(getbLoopData());
                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    protected abstract void onDataReceived(ComBean ComRecData);


}
