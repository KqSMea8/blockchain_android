package com.blockchain.shortvideoplayer.utils;


import java.io.Closeable;
import java.io.IOException;

/**
 * @作者: 许明达
 * @创建时间: 2015-4-3下午8:34:06
 * @版权: 微位科技版权所有 
 * @描述: IO操作工具类
 * @更新人: 
 * @更新时间:
 * @更新内容: TODO	 	
 */
public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				LogUtils.e(e);
			}
		}
		return true;
	}
}
