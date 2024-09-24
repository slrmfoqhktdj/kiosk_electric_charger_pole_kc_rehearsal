package android_serialport_api.idean.tools;
import android.util.Log;

public class Debug {

	public boolean isShow = false;
	public String showLog;
	public static final boolean DEBUG_VESION = true;
	public static final boolean RELEASE_VESION = false;
	public static final int ERROR = 0;
	public static final int WARN = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3; 
	public static final int VERBOSE = 4;
	public Debug() {
	}
	
	/**
	 * @param isShow
	 * @param showLog
	 */
	public Debug(boolean isShow, String showLog) {
		this.isShow = isShow;
		this.showLog = showLog;
	}

	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getShowLog() {
		return showLog;
	}
	public void setShowLog(String showLog) {
		this.showLog = showLog;
	}

	
	public static void showLog(boolean isRelaseVersion,String tag,String log,int level){
		
		if (isRelaseVersion) {
			switch (level) {
			case ERROR:
				Log.e(tag, log);
				break;
			case WARN:
				Log.w(tag, log);
				break;
			case INFO:
				Log.i(tag, log);
				break;
			case DEBUG:
				Log.d(tag, log);
				break;
			default:
				Log.v(tag, log);
				break;
			}
		}
	}
	
	
}
