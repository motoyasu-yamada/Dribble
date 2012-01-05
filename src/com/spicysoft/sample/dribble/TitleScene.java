package com.spicysoft.sample.dribble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * タイトル画面
 */
final class TitleScene implements Scene
{
  /** 1秒で点滅する */
  private static final long BLINK_DURATION = 2000;
  
  /** 点滅アニメーションの開始時刻 */
  private long blinkStarted;
  /** 背景の描画情報 */
  private static Paint backgroundPaint;
  /** タイトル文字の描画情報 */
  private Paint titlePaint;
  /** メッセージの描画情報 */
  private Paint msgPaint;

  /** 初期化 */
  public void init(SurfaceView view) {
    backgroundPaint = new Paint();
    backgroundPaint.setColor(Color.BLACK);

    titlePaint = new Paint();
    titlePaint.setTextSize(48);
    titlePaint.setAntiAlias(true);
    titlePaint.setColor(Color.WHITE);
    titlePaint.setTextAlign(Paint.Align.CENTER);

    msgPaint = new Paint();
    msgPaint.setTextSize(24);
    msgPaint.setAntiAlias(true);
    msgPaint.setColor(Color.WHITE);
    msgPaint.setTextAlign(Paint.Align.CENTER);
  }

  /** 表示開始 */
  public void start(SurfaceView view) {
    blinkStarted = System.currentTimeMillis();
  }

  /** 
   * 各フレーム中の入力イベントやオブジェクトの移動や判定処理
   */
  public void process(SurfaceView view) {
    final MotionEvent e = Main.event();
    if (e != null && e.getAction() == MotionEvent.ACTION_UP) {
      Main.startScene(Main.PLAY);
    }
  }

  /** 
   * 各フレーム毎の描画処理
   */
  public void draw(Canvas canvas) {
    final int w = canvas.getWidth();
    final int h = canvas.getHeight();

    canvas.drawRect(0, 0, w, h, backgroundPaint);

    canvas.drawText("Dribble! 2", w / 2, h / 2, titlePaint);

    final long elapsed = (System.currentTimeMillis() - blinkStarted)
        % BLINK_DURATION;
    final int g;
    if (elapsed <= BLINK_DURATION / 2) {
      g = (int) (255 * elapsed / (BLINK_DURATION / 2));
    } else {
      g = (int) (255 * (BLINK_DURATION - elapsed) / (BLINK_DURATION / 2));
    }
    msgPaint.setColor(Color.rgb(g, g, g));
    canvas.drawText("Touch to Start!", w / 2, h / 2 + 48, msgPaint);
  }

}
