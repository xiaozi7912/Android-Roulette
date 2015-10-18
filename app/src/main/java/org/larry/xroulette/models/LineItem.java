package org.larry.xroulette.models;

import android.util.Log;

/**
 * Created by Larry on 2015-10-18.
 */
public class LineItem {
	private final String LOG_TAG = getClass().getSimpleName();
	public PointItem startPoint = null;
	public PointItem endPoint = null;
	public float length = 0;

	public LineItem() {
		startPoint = new PointItem();
		endPoint = new PointItem();
	}

	public void print() {
		Log.i(LOG_TAG, "print");
		Log.v(LOG_TAG, "length : " + length);
		Log.v(LOG_TAG, "startPoint.x : " + startPoint.x);
		Log.v(LOG_TAG, "startPoint.y : " + startPoint.y);
		Log.v(LOG_TAG, "endPoint.x : " + endPoint.x);
		Log.v(LOG_TAG, "endPoint.y : " + endPoint.y);
		Log.i(LOG_TAG, "-------------------------");
	}
}
