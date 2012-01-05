package com.spicysoft.sample.dribble;

import android.graphics.Canvas;
import android.view.SurfaceView;

interface Scene
{
  public void init(SurfaceView view);

  public void start(SurfaceView view);

  public void process(SurfaceView view);

  public void draw(Canvas canvas);
}