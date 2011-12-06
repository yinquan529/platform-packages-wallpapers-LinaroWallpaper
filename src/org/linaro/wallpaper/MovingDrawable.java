package org.linaro.wallpaper;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class MovingDrawable {

	private final Drawable mDrawable;
	private final Point mPositions[];

	private int mCurPosition = -1;

	/**
	 * A wrapper class for a drawable object that can be moved from a staring
	 * point to an ending point
	 *
	 * @param numFrames - the number of animation iterations wanted to go from
	 *                    start to finish
	 */
	public MovingDrawable(Drawable d, Point start, Point end, int numFrames) {
		mDrawable = d;
		mPositions = new Point[numFrames];

		int xinc = (end.x - start.x) / (numFrames-1);
		int yinc = (end.y - start.y) / (numFrames-1);
		for(int i = 0; i < numFrames; i++)
			mPositions[i] = new Point(start.x+(i*xinc), start.y+(i*yinc));

		//just to be sure we don't screw up on rounding on the destination
		mPositions[mPositions.length-1] = end;
	}

	private static void move(Drawable d, Point p) {
		d.setBounds(p.x, p.y, d.getIntrinsicWidth()+p.x, d.getIntrinsicHeight()+p.y);
	}

	public void draw(Canvas c) {
		if( mCurPosition < mPositions.length -1)
			mCurPosition++;
		move(mDrawable, mPositions[mCurPosition]);
		mDrawable.draw(c);
	}

	public boolean done() {
		return mCurPosition == mPositions.length-1;
	}

	public void restart() {
		mCurPosition = -1;
	}

	public int curFrame() {
		return mCurPosition;
	}
}
