/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.linaro.wallpaper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class LogoWallpaper extends WallpaperService {

	private final Handler mHandler = new Handler();

	/**
	 * The number of pixels between the left edge of logo.png and where the
	 * first edge of the boxes should start at
	 */
	private final static int BOX_XOFFSET = 48;
	/**
	 * The number of pixels between the bottom of logo.png and where the top
	 * of each box should be located at
	 */
	private final static int BOX_YOFFSET = 36;

	/**
	 * The number of animation frames used to draw each box
	 */
	private final int NUM_FRAMES = 20;

	@Override
	public Engine onCreateEngine() {
		return new WallpaperEngine();
	}

	class WallpaperEngine extends Engine
		implements SharedPreferences.OnSharedPreferenceChangeListener {

		private final Runnable mDrawHandler = new Runnable() {
			@Override
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible;

		private final int mBGColor;
		private final Bitmap mLogo;
		private MovingDrawable mBox[];

		private String mLocation = null;
		private float mLogoX = 0;
		private float mLogoY = 0;

		private int mWidth=0;
		private int mHeight=0;

		//let's each box render mNumFrameDelays before starting to render the
		//next box
		private int mNumFrameDelays = 0;


		WallpaperEngine() {
			mBGColor = Color.rgb(5, 5, 5);
			mLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			SharedPreferences sp = getSharedPreferences();
			mLocation = sp.getString(LogoSettings.KEY_NAME, "Center");
			sp.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mDrawHandler);
			getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

		private SharedPreferences getSharedPreferences() {
			return LogoWallpaper.this.getSharedPreferences(LogoSettings.PREFS_NAME, 0);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mHandler.removeCallbacks(mDrawHandler);
			}
		}

		private void getEndingPoints(int boxX, int boxY, int boxW, Point p[]) {
			for(int i = 0; i < p.length; i++)
				p[i] = new Point(boxX + (i*boxW), boxY);
		}

		private void initAnimationCenter(int width, int height, float density,
										Drawable d, int boxSpacing) {
			mLogoX = (width/2.0f) - (mLogo.getWidth()/2.0f);
			mLogoY = (height/2.0f) - (mLogo.getHeight()/2.0f);

			int boxX = Math.round(mLogoX + (BOX_XOFFSET*density));
			int boxY = Math.round(mLogoY + (BOX_YOFFSET*density));

			Point p[] = new Point[mBox.length];
			getEndingPoints(boxX, boxY, boxSpacing, p);

			//box 1 from the top,left
			Point start = new Point(0, 0);
			mBox[0] = new MovingDrawable(d, start, p[0], NUM_FRAMES);

			//box 2 from the bottom,left
			start = new Point(0, height);
			mBox[1] = new MovingDrawable(d, start, p[1], NUM_FRAMES);

			//box 3 from the top
			start = new Point(boxX + (2*boxSpacing), 0);
			mBox[2] = new MovingDrawable(d, start, p[2], NUM_FRAMES);

			//box 4 from the bottom,right
			start = new Point(width, height);
			mBox[3] = new MovingDrawable(d, start, p[3], NUM_FRAMES);

			//box 5 from the top,left
			start = new Point(width, 0);
			mBox[4] = new MovingDrawable(d, start, p[4], NUM_FRAMES);
		}

		private void initAnimationTL(int width, int height, float density,
									Drawable d, int boxSpacing) {
			mLogoX = 40; //offset just a bit
			mLogoY = 40;

			int boxX = Math.round(mLogoX + (BOX_XOFFSET*density));
			int boxY = Math.round(mLogoY + (BOX_YOFFSET*density));

			Point p[] = new Point[mBox.length];
			getEndingPoints(boxX, boxY, boxSpacing, p);

			Point start = new Point(0, height); //bottom left
			mBox[0] = new MovingDrawable(d, start, p[0], NUM_FRAMES);

			start = new Point(width/2, height); //bottom, middle
			mBox[1] = new MovingDrawable(d, start, p[1], NUM_FRAMES);

			start = new Point(width, height); //bottom right
			mBox[2] = new MovingDrawable(d, start, p[2], NUM_FRAMES);

			start = new Point(width, height/2); //middle right
			mBox[3] = new MovingDrawable(d, start, p[3], NUM_FRAMES);

			start = new Point(width, boxY); //box level, right
			mBox[4] = new MovingDrawable(d, start, p[4], NUM_FRAMES);
		}

		private void initAnimationTR(int width, int height, float density,
									Drawable d, int boxSpacing) {
			mLogoX = width - mLogo.getWidth() - 40; //offset just a bit
			mLogoY = 40;

			int boxX = Math.round(mLogoX + (BOX_XOFFSET*density));
			int boxY = Math.round(mLogoY + (BOX_YOFFSET*density));

			Point p[] = new Point[mBox.length];
			getEndingPoints(boxX, boxY, boxSpacing, p);

			Point start = new Point(0, height/2); //middle left
			mBox[0] = new MovingDrawable(d, start, p[0], NUM_FRAMES);

			start = new Point(0, height); //bottom, left
			mBox[1] = new MovingDrawable(d, start, p[1], NUM_FRAMES);

			start = new Point(width/2, height); //bottom middle
			mBox[2] = new MovingDrawable(d, start, p[2], NUM_FRAMES);

			start = new Point(boxX+(3*boxSpacing), height); //bottom boxX
			mBox[3] = new MovingDrawable(d, start, p[3], NUM_FRAMES);

			start = new Point(0, boxY); //box level, left
			mBox[4] = new MovingDrawable(d, start, p[4], NUM_FRAMES);
		}

		private void initAnimationBL(int width, int height, float density,
									Drawable d, int boxSpacing) {
			mLogoX = 40; //offset just a bit
			mLogoY = height - mLogo.getHeight() - 80;

			int boxX = Math.round(mLogoX + (BOX_XOFFSET*density));
			int boxY = Math.round(mLogoY + (BOX_YOFFSET*density));

			Point p[] = new Point[mBox.length];
			getEndingPoints(boxX, boxY, boxSpacing, p);

			Point start = new Point(boxX, 0); //top boxX
			mBox[0] = new MovingDrawable(d, start, p[0], NUM_FRAMES);

			start = new Point(width/2, 0); //top middle
			mBox[1] = new MovingDrawable(d, start, p[1], NUM_FRAMES);

			start = new Point(width, 0); //top right
			mBox[2] = new MovingDrawable(d, start, p[2], NUM_FRAMES);

			start = new Point(width, height/2); //middle right
			mBox[3] = new MovingDrawable(d, start, p[3], NUM_FRAMES);

			start = new Point(width, boxY); //box level, right
			mBox[4] = new MovingDrawable(d, start, p[4], NUM_FRAMES);
		}

		private void initAnimationBR(int width, int height, float density,
									Drawable d, int boxSpacing) {
			mLogoX = width - mLogo.getWidth() - 40; //offset just a bit
			mLogoY = height - mLogo.getHeight() - 80;

			int boxX = Math.round(mLogoX + (BOX_XOFFSET*density));
			int boxY = Math.round(mLogoY + (BOX_YOFFSET*density));

			Point p[] = new Point[mBox.length];
			getEndingPoints(boxX, boxY, boxSpacing, p);

			Point start = new Point(0, boxY); //boxY left
			mBox[0] = new MovingDrawable(d, start, p[0], NUM_FRAMES);

			start = new Point(0, height/2); //middle left
			mBox[1] = new MovingDrawable(d, start, p[1], NUM_FRAMES);

			start = new Point(0, 0); //top left
			mBox[2] = new MovingDrawable(d, start, p[2], NUM_FRAMES);

			start = new Point(width/2, 0); //top middle
			mBox[3] = new MovingDrawable(d, start, p[3], NUM_FRAMES);

			start = new Point(boxX+(4*boxSpacing), 0); //top boxX
			mBox[4] = new MovingDrawable(d, start, p[4], NUM_FRAMES);
		}

		private void initAnimation(int width, int height) {
			float density = getResources().getDisplayMetrics().density;

			mBox = new MovingDrawable[5];
			mNumFrameDelays = NUM_FRAMES / mBox.length;

			Drawable d = getResources().getDrawable(R.drawable.box);
			int w = d.getIntrinsicWidth() + Math.round(3*density);

			if("Center".equals(mLocation))
				initAnimationCenter(width, height, density, d, w);
			else if("Top-Left".equals(mLocation))
				initAnimationTL(width, height, density, d, w);
			else if("Top-Right".equals(mLocation))
				initAnimationTR(width, height, density, d, w);
			else if("Bottom-Left".equals(mLocation))
				initAnimationBL(width, height, density, d, w);
			else if("Bottom-Right".equals(mLocation))
				initAnimationBR(width, height, density, d, w);
			else
				throw new RuntimeException("unknown logo location: " + mLocation);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);

			mWidth = width;
			mHeight = height;
			initAnimation(width, height);
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawHandler);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			drawFrame();
		}

		/**
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable.
		 */
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null)
					drawFrame(c);
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawHandler);
			if (mVisible) {
				//restart the animation if needed
				if( mBox[mBox.length-1].done() ) {
					for(MovingDrawable m: mBox)
						m.restart();
					//wait longer for redrawing to show the completed image
					mHandler.postDelayed(mDrawHandler, 1000);
				}
				else {
					mHandler.postDelayed(mDrawHandler, 1000/100);
				}
			}
		}

		void drawFrame(Canvas c) {
			c.save();

			c.drawColor(mBGColor);
			c.drawBitmap(mLogo, mLogoX, mLogoY, null);

			for(int i = 0; i < mBox.length; i++) {
				if( i == 0 || mBox[i-1].curFrame() > mNumFrameDelays )
					mBox[i].draw(c);
			}

			c.restore();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
			String val = sp.getString(key, mLocation);
			mLocation = val;
			initAnimation(mWidth, mHeight);
			drawFrame();
		}
 	}
}
