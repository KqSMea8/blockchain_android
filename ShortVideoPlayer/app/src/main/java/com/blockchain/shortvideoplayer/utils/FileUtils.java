package com.blockchain.shortvideoplayer.utils;

import android.os.Environment;
import android.util.Log;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @作者: 刘敏
 * @创建时间: 2015-4-3下午8:30:13
 * @版权: 特速版权所有
 * @描述: 写文件的工具类
 * @更新人: 
 * @更新时间:
 * @更新内容: TODO	 	
 */
public class FileUtils {

	public static final String ROOT_DIR = "Android/data/" +UIUtils.getPackageName();
			
	public static final String DOWNLOAD_DIR = "download";
	public static final String CACHE_DIR = "cache";
	public static final String ICON_DIR = "icon";
	public static final String JSON_DIR = "json";
	public static final String BITMAP_DIR = "bitmap";

	private String SDCardRoot;
	private static boolean isCardExist;

	public FileUtils() throws NoSdcardException {
		getSDCardRoot();
	}

	public String getSDCardRoot() throws NoSdcardException{
		if(isCardExist()){
			SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		}else{
			throw new NoSdcardException();
		}
		return SDCardRoot;
	}

	/** 判断SD卡是否挂载 */
	public static boolean isSDCardAvailable() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}
	/** 获取bitmap目录 */
	public static String getBitmapDir() {
		return getDir(BITMAP_DIR);
	}
	/** 获取json目录 */
	public static String getJsonDir() {
		return getDir(JSON_DIR);
	}
	
	/** 获取下载目录 */
	public static String getDownloadDir() {
		return getDir(DOWNLOAD_DIR);
	}

	/** 获取缓存目录 */
	public static String getCacheDir() {
		return getDir(CACHE_DIR);
	}

	/** 获取icon目录 */
	public static String getIconDir() {
		return getDir(ICON_DIR);
	}

	/** 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录 */
	public static String getDir(String name) {
		StringBuilder sb = new StringBuilder();
		if (isSDCardAvailable()) {
			sb.append(getExternalStoragePath());
		} else {
			sb.append(getCachePath());
		}
		sb.append(name);
		sb.append(File.separator);
		String path = sb.toString();
		if (createDirs(path)) {
			return path;
		} else {
			return null;
		}
	}

	/** 获取SD下的应用目录 */
	public static String getExternalStoragePath() {
		StringBuilder sb = new StringBuilder();
		sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		sb.append(File.separator);
		sb.append(ROOT_DIR);
		sb.append(File.separator);
		return sb.toString();
	}

	/** 获取应用的cache目录 */
	public static String getCachePath() {
		File f = UIUtils.getContext().getCacheDir();
		if (null == f) {
			return null;
		} else {
			return f.getAbsolutePath() + "/";
		}
	}

	/** 创建文件夹 */
	public static boolean createDirs(String dirPath) {
		File file = new File(dirPath);
		if (!file.exists() || !file.isDirectory()) {
			return file.mkdirs();
		}
		return true;
	}

	/** 复制文件，可以选择是否删除源文件 */
	public static boolean copyFile(String srcPath, String destPath,
			boolean deleteSrc) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		return copyFile(srcFile, destFile, deleteSrc);
	}

	/** 复制文件，可以选择是否删除源文件 */
	public static boolean copyFile(File srcFile, File destFile,
			boolean deleteSrc) {
		if (!srcFile.exists() || !srcFile.isFile()) {
			return false;
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = in.read(buffer)) > 0) {
				out.write(buffer, 0, i);
				out.flush();
			}
			if (deleteSrc) {
				srcFile.delete();
			}
		} catch (Exception e) {
			LogUtils.e(e);
			return false;
		} finally {
			IOUtils.close(out);
			IOUtils.close(in);
		}
		return true;
	}

	/** 判断文件是否可写 */
	public static boolean isWriteable(String path) {
		try {
			if (StringUtils.isEmpty(path)) {
				return false;
			}
			File f = new File(path);
			return f.exists() && f.canWrite();
		} catch (Exception e) {
			LogUtils.e(e);
			return false;
		}
	}

	/** 修改文件的权限,例如"777"等 */
	public static void chmod(String path, String mode) {
		try {
			String command = "chmod " + mode + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (Exception e) {
			LogUtils.e(e);
		}
	}

	/**
	 * 把数据写入文件
	 * 
	 * @param is
	 *            数据流
	 * @param path
	 *            文件路径
	 * @param recreate
	 *            如果文件存在，是否需要删除重建
	 * @return 是否写入成功
	 */
	public static boolean writeFile(InputStream is, String path,
			boolean recreate) {
		boolean res = false;
		File f = new File(path);
		FileOutputStream fos = null;
		try {
			if (recreate && f.exists()) {
				f.delete();
			}
			if (!f.exists() && null != is) {
				File parentFile = new File(f.getParent());
				parentFile.mkdirs();
				int count = -1;
				byte[] buffer = new byte[1024];
				fos = new FileOutputStream(f);
				while ((count = is.read(buffer)) != -1) {
					fos.write(buffer, 0, count);
				}
				res = true;
			}
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(fos);
			IOUtils.close(is);
		}
		return res;
	}

	/**
	 * 把字符串数据写入文件
	 * 
	 * @param content
	 *            需要写入的字符串
	 * @param path
	 *            文件路径名称
	 * @param append
	 *            是否以添加的模式写入
	 * @return 是否写入成功
	 */
	public static boolean writeFile(byte[] content, String path, boolean append) {
		boolean res = false;
		File f = new File(path);
		RandomAccessFile raf = null;
		try {
			if (f.exists()) {
				if (!append) {
					f.delete();
					f.createNewFile();
				}
			} else {
				f.createNewFile();
			}
			if (f.canWrite()) {
				raf = new RandomAccessFile(f, "rw");
				raf.seek(raf.length());
				raf.write(content);
				res = true;
			}
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(raf);
		}
		return res;
	}

	/**
	 * 把字符串数据写入文件
	 * 
	 * @param content
	 *            需要写入的字符串
	 * @param path
	 *            文件路径名称
	 * @param append
	 *            是否以添加的模式写入
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String content, String path, boolean append) {
		return writeFile(content.getBytes(), path, append);
	}

	/**
	 * 把键值对写入文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param comment
	 *            该键值对的注释
	 */
	public static void writeProperties(String filePath, String key,
			String value, String comment) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
			return;
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File f = new File(filePath);
		try {
			if (!f.exists() || !f.isFile()) {
				f.createNewFile();
			}
			fis = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fis);// 先读取文件，再把键值对追加到后面
			p.setProperty(key, value);
			fos = new FileOutputStream(f);
			p.store(fos, comment);
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(fis);
			IOUtils.close(fos);
		}
	}

	/** 根据值读取 */
	public static String readProperties(String filePath, String key,
			String defaultValue) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
			return null;
		}
		String value = null;
		FileInputStream fis = null;
		File f = new File(filePath);
		try {
			if (!f.exists() || !f.isFile()) {
				f.createNewFile();
			}
			fis = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fis);
			value = p.getProperty(key, defaultValue);
		} catch (IOException e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(fis);
		}
		return value;
	}

	/** 把字符串键值对的map写入文件 */
	public static void writeMap(String filePath, Map<String, String> map,
			boolean append, String comment) {
		if (map == null || map.size() == 0 || StringUtils.isEmpty(filePath)) {
			return;
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File f = new File(filePath);
		try {
			if (!f.exists() || !f.isFile()) {
				f.createNewFile();
			}
			Properties p = new Properties();
			if (append) {
				fis = new FileInputStream(f);
				p.load(fis);// 先读取文件，再把键值对追加到后面
			}
			p.putAll(map);
			fos = new FileOutputStream(f);
			p.store(fos, comment);
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(fis);
			IOUtils.close(fos);
		}
	}

	/** 把字符串键值对的文件读入map */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> readMap(String filePath,
			String defaultValue) {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		Map<String, String> map = null;
		FileInputStream fis = null;
		File f = new File(filePath);
		try {
			if (!f.exists() || !f.isFile()) {
				f.createNewFile();
			}
			fis = new FileInputStream(f);
			Properties p = new Properties();
			p.load(fis);
			map = new HashMap<String, String>((Map) p);// 因为properties继承了map，所以直接通过p来构造一个map
		} catch (Exception e) {
			LogUtils.e(e);
		} finally {
			IOUtils.close(fis);
		}
		return map;
	}

	/** 改名 */
	public static boolean copy(String src, String des, boolean delete) {
		File file = new File(src);
		if (!file.exists()) {
			return false;
		}
		File desFile = new File(des);
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new FileOutputStream(desFile);
			byte[] buffer = new byte[1024];
			int count = -1;
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
				out.flush();
			}
		} catch (Exception e) {
			LogUtils.e(e);
			return false;
		} finally {
			IOUtils.close(in);
			IOUtils.close(out);
		}
		if (delete) {
			file.delete();
		}
		return true;
	}

	public static boolean isCardExist(){
		isCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)?true:false;
		return isCardExist;
	}
	public File createFileInSDCard(String fileName, String dir)
			throws IOException {
		File file = new File(SDCardRoot + dir + File.separator + fileName);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}
	public File creatSDDir(String dir) {
		File dirFile = new File(SDCardRoot + dir + File.separator);
		dirFile.mkdirs();

		return dirFile;
	}
	public boolean filterFileExist(String path, String filter) {
		File file = new File(SDCardRoot + path + File.separator);
		if (file.exists() && file.isDirectory()) {

			String[] fileNames = file.list(new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".png");
				}
			});
			if (fileNames.length > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 鍒ゆ柇SD鍗′笂鐨勬枃浠舵槸鍚﹀瓨鍦?
	 */
	public boolean isFileExist(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file.exists();
	}
	public File getFile(String fileName,String path){
		File file = new File(SDCardRoot + path + File.separator + fileName);
		return file;
	}
	public void deleteFile(String fileName, String path) {
		File file = new File(SDCardRoot + path + File.separator + fileName);
		boolean result = file.delete();
	}

	public void closeInputStream(InputStream inputStream){
		if(inputStream!=null){
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.e("error", "close failed");
				e.printStackTrace();
			}
		}
	}

	public class NoSdcardException extends Exception{

	}

	public static String getMD5(String info)
	{
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++)
			{
				if (Integer.toHexString(0xff & encryption[i]).length() == 1)
				{
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				}
				else
				{
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}

			return strBuf.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			return "";
		}
		catch (UnsupportedEncodingException e)
		{
			return "";
		}
	}

	/**
	 * 检测SD卡是否存在
	 */
	public static boolean isExistSDcard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 从指定文件夹获取文件
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 上午11:42:24
	 */
	public static File getAppointFile(String folderPath, String fileNmae) {
		File file = new File(getSaveFolder(folderPath).getAbsolutePath()
				+ File.separator + fileNmae);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 获取文件夹对象
	 *
	 * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
	 */
	public static File getSaveFolder(String folderName) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsoluteFile()
				+ File.separator
				+ folderName
				+ File.separator);
		file.mkdirs();
		return file;
	}

	/**
	 * 文件流关闭操作
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 上午11:28:07
	 */
	public static void close(Closeable... closeables) {
		if (null == closeables || closeables.length <= 0) {
			return;
		}
		for (Closeable cb : closeables) {
			try {
				if (null == cb) {
					continue;
				}
				cb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上传文件到服务器 (原帖地址：http://blog.csdn.net/lk_blog/article/details/7706348)
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-4-18 下午1:38:33
	 */
	public static String sendFile(String serverUrl, String filePath,
								  String newName) throws Exception {
		String end = "\r\n";
		String hyphens = "--";
		String boundary = "*****";

		URL url = new URL(serverUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		/* 允许Input、Output，不使用Cache */
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		/* 设置传送的method=POST */
		con.setRequestMethod("POST");
		/* setRequestProperty */

		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);
		/* 设置DataOutputStream */
		DataOutputStream ds = new DataOutputStream(con.getOutputStream());
		ds.writeBytes(hyphens + boundary + end);
		ds.writeBytes("Content-Disposition: form-data; "
				+ "name=\"file1\";filename=\"" + newName + "\"" + end);
		ds.writeBytes(end);

		/* 取得文件的FileInputStream */
		FileInputStream fStream = new FileInputStream(filePath);
		/* 设置每次写入1024bytes */
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int length = -1;
		/* 从文件读取数据至缓冲区 */
		while ((length = fStream.read(buffer)) != -1) {
			/* 将资料写入DataOutputStream中 */
			ds.write(buffer, 0, length);
		}
		ds.writeBytes(end);
		ds.writeBytes(hyphens + boundary + hyphens + end);

		/* close streams */
		fStream.close();
		ds.flush();

		/* 取得Response内容 */
		InputStream is = con.getInputStream();
		int ch;
		StringBuffer b = new StringBuffer();
		while ((ch = is.read()) != -1) {
			b.append((char) ch);
		}
		/* 关闭DataOutputStream */
		ds.close();
		return b.toString();
	}

	/**
	 * 发送邮件的方法
	 * @return
	 */
//	public static boolean sendEmail(String fileName){
//		Properties props = new Properties();
//		props.put("mail.smtp.protocol", "smtp");
//		props.put("mail.smtp.auth", "true");//设置要验证
//		props.put("mail.smtp.host", "smtp.exmail.qq.com");//设置host
//		props.put("mail.smtp.port", "25");  //设置端口
//		PassAuthenticator pass = new PassAuthenticator();   //获取帐号密码
//		Session session = Session.getInstance(props, pass); //获取验证会话
//		try
//		{
//			//配置发送及接收邮箱
//			InternetAddress fromAddress, toAddress;
//			/**
//			 * 这个地方需要改成自己的邮箱
//			 */
//			fromAddress = new InternetAddress("liumin@muyouguanxi.com", "秒赊报错信息");
//			toAddress   = new InternetAddress("liumin@muyouguanxi.com", "自己接收自己发的邮件");
//			/**
//			 * 一下内容是：发送邮件时添加附件
//			 */
//			MimeBodyPart attachPart = new MimeBodyPart();
////			FileDataSource fds = new FileDataSource(Environment.getExternalStorageDirectory()+"/crash-fortrun.log"); //打开要发送的文件
//			FileDataSource fds = new FileDataSource(fileName); //打开要发送的文件
//
//			attachPart.setDataHandler(new DataHandler(fds));
//			attachPart.setFileName(fds.getName());
//			MimeMultipart allMultipart = new MimeMultipart("mixed"); //附件
//			allMultipart.addBodyPart(attachPart);//添加
//			//配置发送信息
//			MimeMessage message = new MimeMessage(session);
////                message.setContent("test", "text/plain");
//			message.setContent(allMultipart); //发邮件时添加附件
//			message.setSubject("秒赊错误日志");
//			message.setFrom(fromAddress);
//			message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);
//			message.saveChanges();
//			//连接邮箱并发送
//			Transport transport = session.getTransport("smtp");
//			/**
//			 * 这个地方需要改称自己的账号和密码
//			 */
//			transport.connect("smtp.exmail.qq.com", "liumin@muyouguanxi.com", "Liu1980min");
//
//			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
//			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
//			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
//			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
//			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
//			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
//			CommandMap.setDefaultCommandMap(mc);
//
//			transport.send(message);
//			transport.close();
//		} catch (Exception e) {
//			e.printStackTrace();
////			throw new RuntimeException();//将此异常向上抛出，此时CrashHandler就能够接收这里抛出的异常并最终将其存放到txt文件中
//			LogUtils.e("sendmail:"+e.toString());
//		}
//		return false;
//	}


	// 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
	public static List<File> getAllFiles(File root){
		List<File> fileList = new ArrayList<File>();
		File files[] = root.listFiles();
		if(files != null){
			for (File f : files){
				if(f.isDirectory()){
					getAllFiles(f);
				}else{
					fileList.add(f);
				}
			}
		}

		return fileList;
	}

}
