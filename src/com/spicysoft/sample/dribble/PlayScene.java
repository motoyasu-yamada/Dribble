package com.spicysoft.sample.dribble;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

/** ゲームプレイ画面 */
final class PlayScene implements Scene
{
  /** 重力加速度：単位(描画単位 PER 秒の二乗) */
  private static final float GRAVITY = 2000;
  /** 最大パンチ力により重力の15倍の加速度を与える */
  private static final float PUNCH_ACCELERATION = GRAVITY * 15;
  /** どれだけの時間パンチ力をボールに与えられるか? */
  private static final int PUNCH_MSECOND = 50;
  /** ボールが床・壁にあたった時の速度の垂直方向の減衰係数 */
  private static final float DECAY_VERTICAL = 0.7F;
  /** ボールが床・壁にあたった時の速度の水平方向の減衰係数 */
  private static final float DECAY_HORIZONTAL = 0.9F;
  /** ボールの表示半径 */
  private static final float BALL_RADIUS = 32;
  /** ボールのアニメーションのパターン数(180度分) */
  private static final int BALL_ANIMATION = 8;
  /** ボールのアニメーション画像内の各パターンの位置 */
  private static final Rect[] ballSrc = new Rect[BALL_ANIMATION];
  /** ボールを表示する先の矩形 */
  private static final RectF ballDst = new RectF();
  /** ボール画像 */
  private static Bitmap ball;
  /** ボール画像を表示するときの描画情報 */
  private static Paint ballPaint;
  /** 背景の描画情報 */
  private static Paint backgroundPaint;
  /** ボールの位置(X座標) */
  private static float ballX;
  /** ボールの位置(X座標) */
  private static float ballY;
  /** ボールの速度(単位:描画単位 PER 秒) */
  protected static float ballVX;
  /** ボールの速度(単位:描画単位 PER 秒) */
  protected static float ballVY;
  /** スコア(ドリブル回数 */
  private int dribbled;
  /** プレイ残時間 */
  private final static long TIME = 15;
  /** ゲーム開始時刻 */
  private long gameStarted;
  /** 残時間 */
  private long remainedTime;
  /** スコア */
  private Paint paintScore;
  /** 残り時間 */
  private Paint paintRemainTime;
  /** タッチした瞬間のX座標 */
  private float downedX = 0;
  /** タッチした瞬間のY座標 */
  private float downedY = 0;

  /** 初期化 */
  public void init(SurfaceView view) {
    ball = BitmapFactory.decodeResource(view.getResources(), R.drawable.ball);
    final int ballSrcHeight = ball.getHeight();
    for (int n = 0; n < BALL_ANIMATION; n++) {
      ballSrc[n] = new Rect(ballSrcHeight * n, 0, ballSrcHeight * (n + 1),
          ballSrcHeight);
    }

    backgroundPaint = new Paint();
    backgroundPaint.setColor(Color.BLACK);

    ballPaint = new Paint();

    paintScore = new Paint();
    paintScore.setTextAlign(Paint.Align.LEFT);
    paintScore.setAntiAlias(true);
    paintScore.setColor(Color.WHITE);
    paintScore.setTextSize(32);

    paintRemainTime = new Paint();
    paintRemainTime.setTextAlign(Paint.Align.RIGHT);
    paintRemainTime.setAntiAlias(true);
    paintRemainTime.setColor(Color.WHITE);
    paintRemainTime.setTextSize(32);
  }

  /** 画面表示開始 */
  public void start(SurfaceView view) {
    ballX = view.getWidth() / 2;
    ballY = 0;
    ballVX = 0;
    ballVY = 0;
    dribbled = 0;
    gameStarted = System.currentTimeMillis();
    Sounds.playBgmPlay();
  }

  /**
   * 各フレーム中の入力イベントやオブジェクトの移動や判定処理
   */
  public void process(SurfaceView view) {
    remainedTime = TIME - (System.currentTimeMillis() - gameStarted) / 1000;
    if (remainedTime < 0) {
      PlayLog.logScore(dribbled);
      Main.startScene(Main.OVER);
      return;
    }
    MotionEvent event;
    while ((event = Main.event()) != null) {
      switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        downedX = event.getX();
        downedY = event.getY();
        break;
      case MotionEvent.ACTION_UP:
        final float movedX = event.getX() - downedX;
        final float movedY = event.getY() - downedY;
        final float moved = Math.abs(movedX) + Math.abs(movedY);
        final float actionedX;
        final float actionedY;
        if (moved < 32) {
          Log.d("Dribble", "PUSH");
          actionedX = 0;
          actionedY = PUNCH_ACCELERATION;
        } else {
          Log.d("Dribble", "MOVE");
          actionedX = PUNCH_ACCELERATION * movedX / view.getWidth();
          actionedY = PUNCH_ACCELERATION * movedY / view.getHeight();
        }
        float A = PUNCH_MSECOND / Main.MILLIS_PER_FRAME;
        ballVX += actionedX * A / Main.FRAMES_PER_SECOND;
        ballVY += actionedY * A / Main.FRAMES_PER_SECOND;
        break;
      }
    }

    final float floor = view.getHeight() - BALL_RADIUS;
    ballY = ballY + ballVY / Main.FRAMES_PER_SECOND;

    if (floor <= ballY) {
      // 床に当たった
      float bounce = (ballY - floor) * DECAY_VERTICAL * DECAY_VERTICAL;
      if (bounce < 1 && ballVY < 1) {
        // 床の上で停止している
        ballY = floor;
        ballVY = 0;

      } else {
        // 床の上でバウンド
        ballY = floor - bounce;
        ballVY = -ballVY * DECAY_VERTICAL;
        dribbled++;
        Sounds.playBounce1();
        Log.v("Dribble","Bound on the floor: ballVY=" + ballVY + ",bounce=" + bounce);
  
        final float a = BALL_RADIUS / 2;
        final float b = GRAVITY / Main.FRAMES_PER_SECOND * 3;
        final float c = Math.abs(ballVY);
        Log.v("Dribble",(bounce <= a && c < b) + ":" + a + "," + b + "," + ballVY);
        if (bounce <= a && c < b) {
          // これ以上バウンドせず床の上で停止させる
          ballY = floor;
          ballVY = 0;
          Log.v("Dribble","Stop on the floor.");
        }
      }
      ballVX = ballVX * DECAY_HORIZONTAL;

    } else {
      // 自由落下
      ballVY += GRAVITY / Main.FRAMES_PER_SECOND;
      //Log.v("Dribble","Falling ballVY=" + ballVY);
    }

    final float rightWall = view.getWidth() - BALL_RADIUS;
    ballX = ballX + ballVX / Main.FRAMES_PER_SECOND;
    if (ballX <= BALL_RADIUS) {
      // 左の壁に衝突
      
      ballX = -(BALL_RADIUS - ballX) * DECAY_VERTICAL * DECAY_VERTICAL
          + BALL_RADIUS;
      ballVX = -ballVX * DECAY_VERTICAL;
      ballVY = ballVY * DECAY_HORIZONTAL;
      Sounds.playBounce2();

    } else if (rightWall <= ballX) {
      // 右の壁に衝突
      ballX = rightWall - (ballX - rightWall) * DECAY_VERTICAL * DECAY_VERTICAL;
      ballVX = -ballVX * DECAY_VERTICAL;
      ballVY = ballVY * DECAY_HORIZONTAL;
      Sounds.playBounce2();

    }
  }

  /**
   * 各フレームでの描画
   */
  public void draw(Canvas canvas) {
    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
        backgroundPaint);
    ballDst.set(ballX - BALL_RADIUS, ballY - BALL_RADIUS, ballX + BALL_RADIUS,
        ballY + BALL_RADIUS);

    canvas.drawBitmap(ball, ballSrc[0], ballDst, ballPaint);

    canvas.drawText(dribbled + " 回", 0, 48, paintScore);
    canvas.drawText("あと " + remainedTime + "秒", canvas.getWidth(), 48,
        paintRemainTime);
  }

}
