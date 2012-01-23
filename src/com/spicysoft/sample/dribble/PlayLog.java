package com.spicysoft.sample.dribble;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

/**
 * 最後のスコアやベストスコアなどのプレイ記録を管理する
 */
final class PlayLog
{
  /** プレイ記録を保管するファイル名 */
  private static final String FILENAME_PLAYLOG = "playlog.dat";
  /** 参照 */
  private static Context context;
  
  /** 最後のプレイのスコア */
  private static int lastScore = 0;
  /** 最後のプレイがベストスコアであったかどうか? */
  private static boolean isLastBest = false;

  /** ベストスコア */
  private static int bestScore = 0;
  /** ベストスコアを記録した時刻 */
  private static Date bestPlayedAt;

  /**
   * プレイ記録管理機能の初期化
   * @param context
   */
  public static void init(final Context context)
  {
    PlayLog.context = context;
    load();
  }

  /**
   * スコアを記録する。
   * スコアが過去のベストスコアを上回る場合は
   * ベストスコアを更新してデータを保存する。
   *
   * @param score スコア
   * @return ベストスコアならtrueを返す
   */
  public static void logScore(final int score) {
    lastScore = score;
    isLastBest = bestScore < score;
    if (isLastBest) {
      bestScore = score;
      bestPlayedAt = new Date();
      save();
    }
  }
  
  /**
   * 最後にプレイのスコアを返す。
   * @return スコア
   */
  public static int lastScore() {
    return lastScore;
  }

  /** 最後のプレイがベストスコアか？ */
  public static boolean isLastBest() {
    return isLastBest;
  }

  /**
   * ベストスコアを返す。
   * @return ベストスコア
   */
  public static int bestScore() {
    return bestScore;
  }

  /**
   * ベストスコアを獲得した日時
   */
  public static Date bestPlayedAt() {
    return bestPlayedAt;
  }

  /**
   * ベストスコアをセーブする
   */
  public static void save()
  {
    try {
      final FileOutputStream fos = context.openFileOutput(FILENAME_PLAYLOG, Context.MODE_PRIVATE);
      final DataOutputStream dos = new DataOutputStream(fos);
      dos.writeInt(bestScore);
      dos.writeUTF(DateFormat.getDateInstance().format(bestPlayedAt));
      dos.flush();
      fos.close();
    } catch (IOException e) {
      Log.e("Dribble", "PlayLog.save: bestScore=" + bestScore + ",bestPlayedAt=" + bestPlayedAt, e);
    }
  }

  /**
   * ベストスコアをロードする
   */
  public static void load()
  {
    FileInputStream fis;
    try {
      fis = context.openFileInput(FILENAME_PLAYLOG);

    } catch(final FileNotFoundException e) {
      bestScore = 0;
      bestPlayedAt = new Date(0);
      return;
    }
    
    int tempLastScore;
    Date tempLastScorePlayed;
    try {
      final DataInputStream dis = new DataInputStream(fis);
      tempLastScore = dis.readInt();
      tempLastScorePlayed = DateFormat.getDateInstance().parse(dis.readUTF());
      fis.close();
    } catch(final Exception e) {
      Log.e("Dribble", "PlayLog.load:bestScore=" + bestScore + ",bestPlayedAt=" + bestPlayedAt, e);
      return;
    }
    bestScore = tempLastScore;
    bestPlayedAt = tempLastScorePlayed;
  }
}
