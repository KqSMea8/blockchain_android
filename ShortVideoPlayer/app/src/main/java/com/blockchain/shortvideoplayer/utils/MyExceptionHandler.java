package com.blockchain.shortvideoplayer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.widget.Toast;


/**
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 实现该接口并注册为程序中的默认未捕获异常处理
 * 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 *
 * @description：
 * @author ldm
 * @date 2016-4-18 上午11:31:19
 */
public class MyExceptionHandler implements UncaughtExceptionHandler {
	// 上下文
	private Context mContext;
	// 是否打开上传
	public boolean openUpload = true;
	// Log文件路径
	public static final String LOG_FILE_DIR = "log";
	// log文件的后缀名
	private static final String FILE_NAME = ".log";
	private static MyExceptionHandler instance = null;
	// 系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
	private UncaughtExceptionHandler mDefaultCrashHandler;

	private MyExceptionHandler(Context cxt) {
		// 获取系统默认的异常处理器
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		// 获取Context，方便内部使用
		this.mContext = cxt.getApplicationContext();
	}

	public synchronized static MyExceptionHandler create(Context cxt) {
		if (instance == null) {
			instance = new MyExceptionHandler(cxt);
		}
		return instance;
	}

	/**
	 * 当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
	 * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			// 保存导出异常日志信息到SD卡中
			saveToSDCard(ex);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
			Toast.makeText(mContext,
					"很抱歉，程序出错，即将退出:\r\n" + ex.getLocalizedMessage(),
					Toast.LENGTH_LONG).show();
			if (mDefaultCrashHandler != null) {
				mDefaultCrashHandler.uncaughtException(thread, ex);
			} else {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 保存文件到SD卡
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 上午11:37:17
	 */
	private void saveToSDCard(Throwable ex) throws Exception {
		final File file = FileUtils.getAppointFile(mContext.getPackageName()
						+ File.separator + LOG_FILE_DIR,
				getDataTime("yyyy-MM-dd-HH-mm-ss") + FILE_NAME);
		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(file)));
		// 导出发生异常的时间
		pw.println(getDataTime("yyyy-MM-dd-HH-mm-ss"));
		// 导出手机信息
		savePhoneInfo(pw);
		pw.println();
		// 导出异常的调用栈信息
		ex.printStackTrace(pw);
		pw.close();

//		new Thread(){
//			@Override
//			public void run() {
//				FileUtils.sendEmail(file.getPath());
//			}
//		}.start();

	}

	/**
	 * 保存手机硬件信息
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 上午11:38:01
	 */
	private void savePhoneInfo(PrintWriter pw) throws NameNotFoundException {
		// 应用的版本名称和版本号
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
				PackageManager.GET_ACTIVITIES);
		pw.print("App Version: ");
		pw.print(pi.versionName);
		pw.print('_');
		pw.println(pi.versionCode);
		pw.println();

		// android版本号
		pw.print("OS Version: ");
		pw.print(Build.VERSION.RELEASE);
		pw.print("_");
		pw.println(Build.VERSION.SDK_INT);
		pw.println();

		// 手机制造商
		pw.print("Manufacturer: ");
		pw.println(Build.MANUFACTURER);
		pw.println();

		// 手机型号
		pw.print("Model: ");
		pw.println(Build.MODEL);
		pw.println();
	}

	/**
	 * 根据时间格式返回时间
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 上午11:39:30
	 */
	private String getDataTime(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}
}