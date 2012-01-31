package com.spicysoft.sample.dribble;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 背景画像管理
 * 
 */
class BackgroundImage
{
  /** ネットから取得した背景画像を保管するファイル名 */
  private static final String FILENAME_BKGND= "bkgnd.image";
  /** 読み込んだ背景画像 */
  private static Bitmap image = null;
  /** 参照 */
  private static Context context;

  /**
   * インターネットから背景画像を取得し、
   * ローカルファイルとして保存する。
   * 次回の起動以降は、ローカルファイルを読み込む。
   */
  static void init(final Context context)
  {
    BackgroundImage.context = context;
    try {
      byte[] data;
      data = load(FILENAME_BKGND);
      if (data != null) {
        Log.i("Dribble","BackgroundImage is loaded from the file.");
      } else {
        data = httpGetBinary("http://farm4.staticflickr.com/3084/3206940931_156126f790_b.jpg");
        if (data != null) {
          Log.i("Dribble","BackgroundImage is got from the internet.");
          save(FILENAME_BKGND,data);
        } else {
          Log.i("Dribble","BackgroundImage can't be got from the internet.");
        }
      }
      image = BitmapFactory.decodeByteArray(data, 0, data.length);

    } catch(final Exception e) {
      Log.e("Dribble",e.getLocalizedMessage(),e);
    }
  }

  /** 現在の背景画像を取得する */
  static Bitmap image()
  {
    return image;
  }

  /**
   * 指定したURLにGETメソッドでHTTP通信を行い結果をバイナリー形式で返す
   */
  private static byte[] httpGetBinary(final String url) throws Exception {
      final URLConnection connection = new URL(url).openConnection();
      connection.setDoInput(true);

      final InputStream stream = connection.getInputStream();
      final byte[] data = toByteArray(stream);
      stream.close();
      return data;
  }

  /**
   * ファイルにバイナリーデータを保存する
   */
  private static void save(final String filePath,final byte[] data)
  {
    try {
      final FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE);
      fos.write(data);
      fos.close();
    } catch (IOException e) {
      Log.e("Dribble", "filePath:" + filePath, e);
    }
  }

  /**
   * ファイルからバイナリーデーターを読み込む
   */
  private static byte[] load(final String filePath)
  {
    try {
      final FileInputStream stream = context.openFileInput(filePath);
      final byte[] data = toByteArray(stream);
      stream.close();
      return data;
    } catch(final IOException e) {
      Log.e("Dribble", "filePath:" + filePath, e);
      return null;
    }
  }

  /**
   * ストリームからすべてのデータを読み込み
   * バイト配列にデーターを格納して返す。
   */
  private static byte[] toByteArray(final InputStream stream) throws IOException
  {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final byte[] temp = new byte[1024];

    for(;;) {
      final int read = stream.read(temp);
      if (read <= 0) {
        break;
      }
      buffer.write(temp,0,read);
    }
    return buffer.toByteArray();
  }

}
