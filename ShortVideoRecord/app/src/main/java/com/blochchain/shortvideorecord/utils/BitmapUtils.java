package com.blochchain.shortvideorecord.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @作者: 刘敏
 * @创建时间: 2015-4-27上午9:36:50
 * @版权: 微位科技版权所有
 * @描述: 处理bitmap的工具类
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class BitmapUtils {
	
	/**
	 * 将scrollview 滚动到中间
	 */
	public static void scrollToCenter(final ScrollView sv,final ImageView iv) {

		sv.post(new Runnable() {
			public void run() {
				if (sv == null || iv == null) {
					return;
				}
				int offset = (iv.getMeasuredHeight() - sv.getHeight())/2;
//				LogUtils.e("iv.getMeasuredHeight():"+iv.getMeasuredHeight()+"sv.getHeight():"+sv.getHeight());
				sv.scrollTo(0, Math.abs(offset));
			}                             
		});
	}

	/**
	 * 将亮度 小于一定值的 像素点变成透明
	 */
	public static Bitmap transparentImage(Bitmap bitmap) {
		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				bitmap.getConfig());
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		canvas.drawBitmap(bitmap, new Matrix(), paint);

		int m_BmpPixel[];
		int m_ImageWidth, m_ImageHeigth;
		m_ImageWidth = bmp.getWidth();
		m_ImageHeigth = bmp.getHeight();
		m_BmpPixel = new int[m_ImageWidth * m_ImageHeigth];
		bmp.getPixels(m_BmpPixel, 0, m_ImageWidth, 0, 0, m_ImageWidth,
				m_ImageHeigth);
		for (int i = 0; i < m_ImageWidth * m_ImageHeigth; i++) {
			int color = m_BmpPixel[i];

			if (((color & 0xff0000) <= 0x660000)
					&& ((color & 0x00FF00) <= 0x006600)
					&& ((color & 0x0000ff) <= 0x000066)) {
				m_BmpPixel[i] = Color.TRANSPARENT;
			}
		}
		bmp.setPixels(m_BmpPixel, 0, m_ImageWidth, 0, 0, m_ImageWidth,
				m_ImageHeigth);
		return bmp;
	}

	 /**
     * 获取裁剪后的圆形图片
     * @param radius
     *            半径
     */
    public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;  

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        // 画边框
        Paint mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStrokeWidth(2);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2-1,
                mBorderPaint);
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }
	
	
	/**
	 * 两个bitmap合成一个bitmap
	 */
	public static Bitmap composeBitmap(Bitmap bitmap, int frontBitmap) {
		// 图片合成画布 先画图片A 再去画图片B
//		Bitmap bitmap = BitmapFactory.decodeResource(UIUtils.getResources(),
//				backBitmap);
//		Bitmap bm=getCroppedRoundBitmap(bitmap, 50);
		Bitmap map = BitmapFactory.decodeResource(UIUtils.getResources(),
				frontBitmap);
		Bitmap alterBitmap = Bitmap.createBitmap(map.getWidth(),
				map.getHeight(), map.getConfig());
		
		
		Canvas canvas = new Canvas(alterBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		

		canvas.drawBitmap(map, new Matrix(), paint);
		
		canvas.drawBitmap(bitmap, new Matrix(), paint);
		
		return alterBitmap;
	}
	/**
	 * 两个bitmap合成一个bitmap
	 */
	public static Bitmap composeBitmap(Bitmap bitmap, Bitmap frontBitmap) {
		// 图片合成画布 先画图片A 再去画图片B
//		Bitmap bitmap = BitmapFactory.decodeResource(UIUtils.getResources(),
//				backBitmap);
//		Bitmap bm=getCroppedRoundBitmap(bitmap, 50);
		Bitmap alterBitmap = Bitmap.createBitmap(frontBitmap.getWidth(),
				frontBitmap.getHeight(), frontBitmap.getConfig());
		
		
		Canvas canvas = new Canvas(alterBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		

		canvas.drawBitmap(frontBitmap, new Matrix(), paint);
		
		canvas.drawBitmap(bitmap, new Matrix(), paint);
		
		return alterBitmap;
	}
	/**
	 * 根据图片的url或的bitmap
	 */
	public static Bitmap getBitmap(String path) {
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(20000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				InputStream inputStream = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				return bitmap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取图片的旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 将图片按照某个角度进行旋转
	 * 
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}
	
//	/**等比压缩图片*/
//	public static Bitmap comp(Bitmap image) {
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//			baos.reset();// 重置baos即清空baos
//			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
//		}
//		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//		BitmapFactory.Options newOpts = new BitmapFactory.Options();
//		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
//		newOpts.inJustDecodeBounds = true;
//		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//		newOpts.inJustDecodeBounds = false;
//		int w = newOpts.outWidth;
//		int h = newOpts.outHeight;
//		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//		float hh = 800;// 这里设置高度为800f
//		float ww = 480;// 这里设置宽度为480f
//		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
//		int be = 1;// be=1表示不缩放
//		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
//			be = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
//			be = (int) (newOpts.outHeight / hh);
//		}
//		if (be <= 0)
//			be = 1;
//		LogUtils.e("压缩比:"+be);
//		newOpts.inSampleSize = be;// 设置缩放比例
//		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//		isBm = new ByteArrayInputStream(baos.toByteArray());
//		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
//	}
	
	public static Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>32) {  //循环判断如果压缩后图片是否大于32kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
		LogUtils.e("大小:"+baos.toByteArray().length);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
	
	public static Bitmap getSmallBitmap(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bmp = null;
		/* 设置为true,可以不把图片读到内存中,但依然可以计算出图片的大小，这正是我们需要的 */
		options.inJustDecodeBounds = true;
		File file = new File(filePath);
		InputStream is = null;
		try {
			if (file.exists()) {

				is = new FileInputStream(file);
				// BitmapFactory.decodeFile(filePath, options);
				BitmapFactory.decodeStream(is, null, options);

				// Calculate inSampleSize
				options.inSampleSize = calculateInSampleSize(options, 400, 200);

				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;

				Log.i("info", "options.inSampleSize=" + options.inSampleSize);
				// 这样重新获取一个新的is输入流,就可以解决decodeStream(is,null, options)返回null的问题
				is = new FileInputStream(file);

				bmp = BitmapFactory.decodeStream(is, null, options);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

//		BitmapFactory.Options bfOptions=new BitmapFactory.Options();
//		bfOptions.inDither=false;
//		bfOptions.inPurgeable=true;
//		bfOptions.inTempStorage=new byte[12 * 1024];
//		// bfOptions.inJustDecodeBounds = true;
//		File file = new File(filePath);
//		FileInputStream fs=null;
//		try {
//			fs = new FileInputStream(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		if(fs != null)
//			try {
//				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}finally{
//				if(fs!=null) {
//					try {
//						fs.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}

		return bmp;
	}
	
	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		/* 压缩一张图片。我们需要知道这张图片的原始大小，然后根据我们设定的压缩比例进行压缩。 */
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		/*
		 * 1、如果图片的原始高度或者宽带大约我们期望的宽带和高度，我们需要计算出缩放比例的数值。否则就不缩放
		 * 2、如果使用大的值作位压缩倍数，则压缩出来的图片大小会小于我们设定的值
		 * 3、如果使用小的值作位压缩倍数，则压缩出来的图片大小会大于我们设定的值
		 */
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	/**裁剪尺寸*/
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}
	/**储存截取的图片*/
	public static String saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {
		File f = new File(FileUtils.getDir(FileUtils.BITMAP_DIR) + bitName + ".jpg");
		String path=FileUtils.getDir(FileUtils.BITMAP_DIR) + bitName + ".jpg";
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);

		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//通知相册刷新
		Uri uriData = Uri.parse("file://" + path);
		UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriData));

		return path;
	}

	public static String getBitmapStrBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		return Base64.encodeToString(bytes, 0);
	}
	// 测试用的方法：把Base64转换成Bitmap
	public Bitmap getBitmapFromBase64(String iconBase64) {
		byte[] bitmapArray;
		bitmapArray = Base64.decode(iconBase64, 0);
		return BitmapFactory
				.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}
    /*
	* 解决小米手机上获取图片路径为null的情况
	* @param intent
	* @return
			*/
	public static  Uri getPictureUri(Intent intent,Context context) {
		Uri uri = intent.getData();
		String type = intent.getType();
		if(!TextUtils.isEmpty(type)){
			if (uri.getScheme().equals("file") && (type.contains("image/"))) {
				String path = uri.getEncodedPath();
				if (path != null) {
					path = Uri.decode(path);
					ContentResolver cr = context.getContentResolver();
					StringBuffer buff = new StringBuffer();
					buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
							.append("'" + path + "'").append(")");
					Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							new String[] { MediaStore.Images.ImageColumns._ID },
							buff.toString(), null, null);
					int index = 0;
					for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
						index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
						// set _id value
						index = cur.getInt(index);
					}
					if (index == 0) {
						// do nothing
					} else {
						Uri uri_temp = Uri
								.parse("content://media/external/images/media/"
										+ index);
						if (uri_temp != null) {
							uri = uri_temp;
						}
					}
				}
			}
		}
		return uri;
	}
}
