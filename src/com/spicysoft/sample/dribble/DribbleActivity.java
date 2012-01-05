package com.spicysoft.sample.dribble;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

/**
 * アプリケーションの起動
 */
public final class DribbleActivity extends Activity
{
  @Override public void onCreate(Bundle savedInstanceState) {
    Log.d("Dribble", "onCreate");
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(new DribbleView(this));
  }

  @Override protected void onPause() {
    Log.d("Dribble", "onPause");
    super.onPause();
  }

  @Override protected void onResume() {
    Log.d("Dribble", "onResume");
    super.onResume();
  }

  @Override protected void onDestroy() {
    Log.d("Dribble", "onDestroy");
    super.onDestroy();
  }
}

/**
 * ゲーム内オブジェクトの処理、タッチの処理、画面の表示等のゲームの基本処理を行うクラス
 */
class DribbleView extends SurfaceView implements SurfaceHolder.Callback
{
  /** コンストラクタ */
  public DribbleView(final Context context) {
    super(context);
    setFocusable(true);
    getHolder().addCallback(this);
  }

  /** 実際にSurfaceViewが生成完了された際にAPIからコールバックされる */
  @Override public void surfaceCreated(SurfaceHolder holder) {
    Log.d("Dribble", "surfaceCreated");
    Main.createThread(this);
  }

  /** 表示状態(画面サイズや回転状態)が変更された際にAPIからコールバックされる */
  @Override public void surfaceChanged(SurfaceHolder holder, int format,
      int width, int height) {
    Log.d("Dribble", "surfaceChanged");
  }

  /** SurfaceViewが破棄された際に、APIからコールバックされる */
  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    Log.d("Dribble", "surfaceDestroyed");
  }

  /**
   * タッチされた際にAPIからコールバックされる ゲームループ内で処理できるように、 ユーザがタッチもしくはフリックのアクションをしたこと、
   * その際にどれぐらいボールに影響を与えるアクションをしたかを記録する
   */
  @Override public boolean onTouchEvent(final MotionEvent event) {
    Main.queue(event);
    return true;
  }

}
