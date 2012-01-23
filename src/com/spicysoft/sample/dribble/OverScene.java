package com.spicysoft.sample.dribble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

/** ゲームオーバー画面 */
final class OverScene implements Scene
{
  /** 誤入力を避けるため、画面を開いてから一定時間(2秒)は、入力操作を無効とする */
  private final static long WAIT = 2000;
  /** 一定時間(10秒)たつとタイトル画面に戻る */
  private final static long AUTO = 10000;
  /** 背景の描画情報 */
  private static Paint backgroundPaint;
  /** スコアの描画情報 */
  private Paint paintScore;
  /** 画面を表示開始した時刻 */
  private long start;

  /** 初期化処理 */
  public void init(SurfaceView view) 
  {
    backgroundPaint = new Paint();
    backgroundPaint.setColor(Color.BLACK);

    paintScore = new Paint();
    paintScore.setTextAlign(Paint.Align.CENTER);
    paintScore.setAntiAlias(true);
    paintScore.setColor(Color.WHITE);
    paintScore.setTextSize(48);

  }

  /** 画面の表示開始 */
  public void start(SurfaceView view)
  {
    start = System.currentTimeMillis();
    Sounds.stopBgm();
  }

  /** 
   * 各フレーム中の入力イベントやオブジェクトの移動や判定処理
   */
  public void process(SurfaceView view)
  {
    final long elapsed = System.currentTimeMillis() - start;
    final MotionEvent e = Main.event();
    if (WAIT < elapsed && e != null && e.getAction() == MotionEvent.ACTION_UP || AUTO <= elapsed) {
      Sounds.playTouch();
      Main.startScene(Main.TITLE);
    }
  }

  /** 
   * 各フレーム毎の描画処理
   */
  public void draw(Canvas canvas)
  {
    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

    final int centerX = canvas.getWidth() / 2;
    final int centerY = canvas.getHeight() / 2;
    canvas.drawText("記録 " + PlayLog.lastScore() + " 回", centerX , centerY, paintScore);
    if (PlayLog.isLastBest()) {
      canvas.drawText("!! ベストスコア !!", centerX , centerY + paintScore.ascent() * 2, paintScore);
    }
  }
}

