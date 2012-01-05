package com.spicysoft.sample.dribble;


/**
 * 最後のスコアやベストスコアなどのプレイ記録を管理する
 */
final class PlayLog
{
  /** 最後のプレイのスコア */
  private static int lastScore = 0;

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
  }
  
  /**
   * 最後にプレイのスコアを返す。
   * @return スコア
   */
  public static int lastScore() {
    return lastScore;
  }
}
