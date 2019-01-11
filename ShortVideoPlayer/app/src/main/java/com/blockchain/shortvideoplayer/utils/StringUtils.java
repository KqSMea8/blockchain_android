package com.blockchain.shortvideoplayer.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2015-4-3下午8:34:29
 * @版权: 微位科技版权所有
 * @描述: 字符串工具类
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class StringUtils {
	public final static String UTF_8 = "utf-8";

	/** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
	public static boolean isEmpty(String value) {
		if (value != null && !"".equalsIgnoreCase(value.trim())
				&& !"null".equalsIgnoreCase(value.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/** 判断多个字符串是否相等，如果其中有一个为空字符串或者null，则返回false，只有全相等才返回true */
	public static boolean isEquals(String... agrs) {
		String last = null;
		for (int i = 0; i < agrs.length; i++) {
			String str = agrs[i];
			if (isEmpty(str)) {
				return false;
			}
			if (last != null && !str.equalsIgnoreCase(last)) {
				return false;
			}
			last = str;
		}
		return true;
	}

	/**
	 * 返回一个高亮spannable
	 * 
	 * @param content
	 *            文本内容
	 * @param color
	 *            高亮颜色
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @return 高亮spannable
	 */
	public static CharSequence getHighLightText(String content, int color,
			int start, int end) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		start = start >= 0 ? start : 0;
		end = end <= content.length() ? end : content.length();
		SpannableString spannable = new SpannableString(content);
		CharacterStyle span = new ForegroundColorSpan(color);
		spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 获取链接样式的字符串，即字符串下面有下划线
	 * 
	 * @param resId
	 *            文字资源
	 * @return 返回链接样式的字符串
	 */
	public static Spanned getHtmlStyleString(int resId) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href=\"\"><u><b>").append(UIUtils.getString(resId))
				.append(" </b></u></a>");
		return Html.fromHtml(sb.toString());
	}

	/** 格式化文件大小，不保留末尾的0 */
	public static String formatFileSize(long len) {
		return formatFileSize(len, false);
	}

	/** 格式化文件大小，保留末尾的0，达到长度一致 */
	public static String formatFileSize(long len, boolean keepZero) {
		String size;
		DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
		DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
		if (len < 1024) {
			size = String.valueOf(len + "B");
		} else if (len < 10 * 1024) {
			// [0, 10KB)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
		} else if (len < 100 * 1024) {
			// [10KB, 100KB)，保留一位小数
			size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
		} else if (len < 1024 * 1024) {
			// [100KB, 1MB)，个位四舍五入
			size = String.valueOf(len / 1024) + "KB";
		} else if (len < 10 * 1024 * 1024) {
			// [1MB, 10MB)，保留两位小数
			if (keepZero) {
				size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024
						/ 1024 / (float) 100))
						+ "MB";
			} else {
				size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100)
						+ "MB";
			}
		} else if (len < 100 * 1024 * 1024) {
			// [10MB, 100MB)，保留一位小数
			if (keepZero) {
				size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024
						/ 1024 / (float) 10))
						+ "MB";
			} else {
				size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10)
						+ "MB";
			}
		} else if (len < 1024 * 1024 * 1024) {
			// [100MB, 1GB)，个位四舍五入
			size = String.valueOf(len / 1024 / 1024) + "MB";
		} else {
			// [1GB, ...)，保留两位小数
			size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100)
					+ "GB";
		}
		return size;
	}

	public static List<String> getList(String s){
		Gson gson=new Gson();
		List<String> piscs=gson.fromJson(s,new TypeToken<List<String>>(){}.getType());
		return piscs;
	}

	public static List<String> getShareList(String s){
		List<String> list=new ArrayList<String>();
		if(s.contains(",")){
			String[] sourceStrArray = s.split(",");
			for (int i = 0; i < sourceStrArray.length; i++) {
				list.add(sourceStrArray[i].trim());
			}
		}
		else{
			list.add(s.trim());
		}
		return list;
	}

	public static List<String> getShareListToHead(String s){
		List<String> list=new ArrayList<String>();
		if(s.contains(",")){
			String[] sourceStrArray = s.split(",");
			for (int i = 0; i < sourceStrArray.length; i++) {
				list.add(0,sourceStrArray[i].trim());
			}
		}
		else{
			list.add(0,s.trim());
		}
		return list;
	}

	public static <T> ArrayList<T> fromJsonList(String json, Class<T> cls) {
		ArrayList<T> mList = new ArrayList<T>();
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for(final JsonElement elem : array){
			mList.add(new Gson().fromJson(elem, cls));
		}
		return mList;
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9

    ------------------------------------------------
    13(老)号段：130、131、132、133、134、135、136、137、138、139
    14(新)号段：145、147
    15(新)号段：150、151、152、153、154、155、156、157、158、159
    17(新)号段：170、171、173、175、176、177、178
    18(3G)号段：180、181、182、183、184、185、186、187、188、189
        */
		String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) return false;
		else return mobiles.matches(telRegex);
	}


	public static String toUtf8(String str) {
		String result = null;
		try {
//			result = new String(str.getBytes("UTF-8"), "UTF-8");
			result = URLEncoder.encode(str,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
