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
  /**
   * The image of "http://farm4.staticflickr.com/3084/3206940931_156126f790_b.jpg"
   * is By SAN_DRINO under Creative Commons License.
   * http://www.flickr.com/photos/san_drino/3206940931/
  */
  private static final String URL_IMAGE = "http://farm4.staticflickr.com/3084/3206940931_156126f790_b.jpg";
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

    byte[] data;
    data = load(FILENAME_BKGND); // (1)

    if (data != null) { // (2)
      Log.i("Dribble","BackgroundImage is loaded from the file.");
    } else {

      data = httpGetBinary(URL_IMAGE); // (3)

      if (data != null) { // (4)
        Log.i("Dribble","BackgroundImage is got from the internet.");
        save(FILENAME_BKGND,data);

      } else {
        Log.i("Dribble","BackgroundImage can't be got from the internet.");
      }
    }

    // (5)
    image = BitmapFactory.decodeByteArray(data, 0, data.length);
  }

  /** 現在の背景画像を取得する */
  static Bitmap image()
  {
    return image;
  }

  /**
   * 指定したURLにGETメソッドでHTTP通信を行い結果をバイナリー形式で返す
   */
  private static byte[] httpGetBinary(final String stringUrl) {
      try {
        final URL url = new URL(stringUrl); // (1)
        final URLConnection connection = url.openConnection(); // (2)

        final InputStream stream = connection.getInputStream(); // (3)

        final byte[] data = toByteArray(stream); // (4)

        stream.close(); // (5)
        return data;

      } catch(final Exception e) { // (6)
        Log.e("Dribble", "URL_IMAGE:" + stringUrl, e);
        return null;
      }
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
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // (1)
    final byte[] temp = new byte[1024]; // (2)

    for(;;) {
      final int read = stream.read(temp); // (3)
      if (read < 0) { // (4)
        break;
      } 
      buffer.write(temp,0,read); // (5)
    }

    return buffer.toByteArray(); // (6)
  }

}
