package com.spicysoft.sample.dribble;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * BGMÇ®ÇÊÇ—å¯â âπÇÃä«óùÉNÉâÉX
 */
final class Sounds
{
  private static Context context;
  private static MediaPlayer mediaPlayer;
  private static SoundPool soundPool;
  private static int sidBounce1;
  private static int sidBounce2;
  private static int sidTouch;

  public static void init(final Context context) {
    Sounds.context = context;
    soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    sidBounce1 = soundPool.load(context, R.raw.sfx_bounce1, 1);
    sidBounce2 = soundPool.load(context, R.raw.sfx_bounce2, 1);
    sidTouch = soundPool.load(context, R.raw.sfx_touch, 1);
  }

  public static void term() {
    soundPool.release();
  }

  public static void playBounce1() {
    soundPool.play(sidBounce1, 1.0F, 1.0F, 0, 0, 1.0F);
  }

  public static void playBounce2() {
    soundPool.play(sidBounce2, 1.0F, 1.0F, 0, 0, 1.0F);
  }

  public static void playTouch() {
    soundPool.play(sidTouch, 1.0F, 1.0F, 0, 0, 1.0F);
  }

  public static void playBgmTitle() {
    playBgm(R.raw.bgm_title);
  }

  public static void playBgmPlay() {
    playBgm(R.raw.bgm_play);
  }

  public static void pauseBgm() {
    if (mediaPlayer != null) {
      mediaPlayer.pause();
    }
  }

  public static void resumeBgm() {
    if (mediaPlayer != null) {
      mediaPlayer.start();
    }
  }

  public static void stopBgm() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
    }
  }

  private static synchronized void playBgm(final int resourceId) {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }

    mediaPlayer = MediaPlayer.create(context, resourceId);
    mediaPlayer.setLooping(true);
    mediaPlayer.start();
    mediaPlayer.setVolume(0.1F, 0.1F);
  }
}
