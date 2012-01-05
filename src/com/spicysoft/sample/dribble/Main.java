package com.spicysoft.sample.dribble;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ゲーム内オブジェクトの処理、タッチの処理、画面の表示等のゲームの基本処理を行うクラス
 */
final class Main
{
  /** FPS 1秒あたりのフレーム数。ゲームの処理単位 */
  public static final int FRAMES_PER_SECOND = 50;
  /** フレームあたりのミリ秒数 */
  public static final int MILLIS_PER_FRAME = 1000 / FRAMES_PER_SECOND;
  /** タイトル画面用シーン */
  public static final Scene TITLE = new TitleScene();
  /** ゲームプレイ画面用シーン */
  public static final Scene PLAY = new PlayScene();
  /** ゲームオーバー画面用シーン */
  public static final Scene OVER = new OverScene();

  /** スレッド */
  private static Thread thread;
  /** シーン */
  private static Scene current;
  /** イベントキュー */
  private static final Queue<MotionEvent> events = new LinkedList<MotionEvent>();
  /** 一回目の初期化か？ */
  private static boolean first = true;
  /** ビュー */
  private static SurfaceView view;

  /**
   * ゲームループスレッドを生成し ゲームループを開始 or 再開
   */
  public static void createThread(final SurfaceView view) {
    Main.view = view;
    if (first) {
      Main.initScenes(view);
      startScene(Main.TITLE);
      first = false;
    }
    thread = new Thread() {
      @Override public void run() {
        Main.runLoop();
      }
    };
    thread.start();
  }

  /**
   * ゲームループスレッドを中断する
   */
  public static void interruptThread() {
    thread.interrupt();
  }

  /**
   * ゲームループ中の１フレームの処理
   */
  public static void runLoop() {
    Log.d("Dribble", "Loop in thread will start.");
    for (;;) {
      if (thread.isInterrupted()) {
        Log.d("Dribble", "Thread is interrupted");
        break;
      }
      final long start = System.currentTimeMillis();
      final Scene scene = current;
      scene.process(view);

      // (1) ダブルバッファリング開始
      final SurfaceHolder holder = view.getHolder();
      final Canvas canvas = holder.lockCanvas();
      if (canvas != null) {
        try {
          scene.draw(canvas);
        } finally {
          // (3) ダブルバッファリング完了
          holder.unlockCanvasAndPost(canvas);
        }
      }

      final long elapsed = System.currentTimeMillis() - start;
      final long towait = MILLIS_PER_FRAME - elapsed;
      if (0 < towait) {
        try {
          Thread.sleep(towait);
        } catch (final InterruptedException e) {
          Log.d("Dribble", "On calling Thread.sleep", e);
          break;
        }
      }
    }

    Log.d("Dribble", "Loop in thread exits");
    thread = null;
  }

  /**
   * 全てのシーンを初期化する
   */
  public static void initScenes(final SurfaceView view) {
    TITLE.init(view);
    PLAY.init(view);
    OVER.init(view);
  }

  /**
   * シーンを変更する
   */
  public static void startScene(final Scene scene) {
    Log.d("Dribble", "startScene: scene = " + scene);
    current = scene;
    events.clear();
    current.start(view);
  }

  /** キューからイベントを1件取得 */
  public static synchronized MotionEvent event() {
    return events.poll();
  }

  /** キューにイベントを格納 */
  public static synchronized void queue(final MotionEvent event) {
    events.add(event);
  }

}