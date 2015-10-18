package org.larry.xroulette;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.larry.xroulette.models.LineItem;
import org.larry.xroulette.models.PointItem;
import org.larry.xroulette.models.RouletteItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends Activity {
	private final String LOG_TAG = getClass().getSimpleName();
	private Activity mActivity = this;
	private Handler mHandler = new Handler();
	private DisplayMetrics mDisplayMetrics = null;
	private ArrayList<RouletteItem> mDataList = null;

	private ImageView mRouletteImage = null;
	private ImageView mColorRankImage = null;
	private Button mStartButton = null;

	private Bitmap mResultBitmap = null;
	private Canvas mCanvas = null;

	private final int MAX_ITEM_LENGTH = 12;

	private float mRouletteRadius = 256;
	private int mRunningCount = 0;
	private boolean mIsRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		initRouletteData();
		drawColorRank();
		drawRoulette(0);
		drawIndicator();
		mRouletteImage.setImageBitmap(mResultBitmap);
	}

	private void initView() {
		Log.i(LOG_TAG, "initView");
		mRouletteImage = (ImageView) findViewById(R.id.main_image_roulette);
		mColorRankImage = (ImageView) findViewById(R.id.main_image_color_rank);
		mStartButton = (Button) findViewById(R.id.main_btn_start);

		mStartButton.setOnClickListener(onClickListener);

		mDisplayMetrics = getResources().getDisplayMetrics();
		mRouletteRadius = mRouletteRadius * mDisplayMetrics.scaledDensity;
		mResultBitmap = Bitmap.createBitmap((int) mRouletteRadius, (int) mRouletteRadius, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mResultBitmap);

		Log.v(LOG_TAG, "densityDpi : " + mDisplayMetrics.densityDpi);
		Log.v(LOG_TAG, "scaledDensity : " + mDisplayMetrics.scaledDensity);
		Log.v(LOG_TAG, "-------------------------");
	}

	private void initRouletteData() {
		Log.i(LOG_TAG, "initRouletteData");
		mDataList = new ArrayList<RouletteItem>();
		Random random = new Random(new Date().getTime());

		for (int i = 0; i < MAX_ITEM_LENGTH; i++) {
			RouletteItem rouletteItem = new RouletteItem();
			int r = random.nextInt(255);
			int g = random.nextInt(255);
			int b = random.nextInt(255);
			rouletteItem.defaultColor = Color.argb(150, r, g, b);
			rouletteItem.selectedColor = Color.argb(100, r, g, b);
			mDataList.add(rouletteItem);
		}
		Log.v(LOG_TAG, "-------------------------");
	}

	private void drawColorRank() {
		Log.i(LOG_TAG, "initRouletteData");
		float imageLength = 200 * mDisplayMetrics.scaledDensity;
		PointItem startPoint = new PointItem();
		PointItem endPoint = new PointItem();
		Bitmap bitmap = Bitmap.createBitmap((int) imageLength, (int) imageLength, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setStrokeWidth(5);

		for (int i = 0; i < mDataList.size(); i++) {
			RouletteItem currentItem = mDataList.get(i);
			paint.setColor(currentItem.defaultColor);
			startPoint.x = 10;
			startPoint.y = 10 * (i + 1);
			endPoint.x = 110;
			endPoint.y = startPoint.y;
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
		}

		mColorRankImage.setImageBitmap(bitmap);
		Log.v(LOG_TAG, "-------------------------");
	}

	private void drawIndicator() {
		Log.i(LOG_TAG, "drawRoulette");
		LineItem edgeA = new LineItem();
		LineItem edgeB = new LineItem();
		Paint paint = new Paint();
		Path path = new Path();
		float startY = 5 * mDisplayMetrics.scaledDensity;

		edgeA.length = (float) (mRouletteRadius * 0.2);
		edgeA.startPoint.x = (mRouletteRadius / 2) - (edgeA.length / 2);
		edgeA.startPoint.y = startY;
		edgeA.endPoint.x = edgeA.startPoint.x + edgeA.length;
		edgeA.endPoint.y = edgeA.startPoint.y;
		edgeA.print();

		edgeB.length = (float) (mRouletteRadius * 0.3);
		edgeB.startPoint.x = edgeA.endPoint.x;
		edgeB.startPoint.y = edgeA.endPoint.y;
		edgeB.endPoint.x = mRouletteRadius / 2;
		edgeB.endPoint.y = edgeB.startPoint.y + edgeB.length;
		edgeB.print();

		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		path.moveTo(edgeA.startPoint.x, edgeA.startPoint.y);
		path.lineTo(edgeA.endPoint.x, edgeA.endPoint.y);
		path.lineTo(edgeB.endPoint.x, edgeB.endPoint.y);
		path.lineTo(edgeA.startPoint.x, edgeA.startPoint.y);
		mCanvas.drawPath(path, paint);
		Log.v(LOG_TAG, "-------------------------");
	}

	private void drawRoulette(int shift) {
		Log.i(LOG_TAG, "drawRoulette");
		float degree = 360 / MAX_ITEM_LENGTH;
		float borderWidth = 2;

		Paint trianglePaint = new Paint();
		trianglePaint.setAntiAlias(true);
		for (int i = 0; i < MAX_ITEM_LENGTH; i++) {
			RouletteItem currentItem = mDataList.get(i);
			trianglePaint.setColor(currentItem.defaultColor);
			mCanvas.drawArc(new RectF((float) (mRouletteRadius * 0.05), (float) (mRouletteRadius * 0.05), (float) (mRouletteRadius * 0.95), (float) (mRouletteRadius * 0.95)), degree * i - 120 + (degree / 2) + shift, degree, true, trianglePaint);
		}

		Paint borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setStrokeWidth(borderWidth);
		borderPaint.setStyle(Paint.Style.STROKE);
		mCanvas.drawCircle(mRouletteRadius / 2, mRouletteRadius / 2, (float) (mRouletteRadius / 2 * 0.9), borderPaint);
		Log.v(LOG_TAG, "-------------------------");
	}

	public void clearCanvas() {
		Log.i(LOG_TAG, "clearCanvas");
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		mCanvas.drawPaint(paint);
		Log.v(LOG_TAG, "-------------------------");
	}

	public void startTurnRoulette() {
		Log.i(LOG_TAG, "startTurnRoulette");
		mRunningCount = 0;
		new Thread(new Runnable() {
			@Override
			public void run() {
				float degree = 360 / MAX_ITEM_LENGTH;
				Random random = new Random(new Date().getTime());

				while (mIsRunning) {
					mRunningCount++;
					clearCanvas();
					drawRoulette((int) (mRunningCount * degree));
					drawIndicator();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mRouletteImage.setImageBitmap(mResultBitmap);
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 1; i <= random.nextInt(100); i++) {
					mRunningCount++;
					clearCanvas();
					drawRoulette((int) (mRunningCount * degree));
					drawIndicator();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mRouletteImage.setImageBitmap(mResultBitmap);
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.v(LOG_TAG, "mRunningCount : " + mRunningCount);
			}
		}).start();
		Log.v(LOG_TAG, "-------------------------");
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mIsRunning = (mIsRunning) ? false : true;
			if (mIsRunning) {
				startTurnRoulette();
			}
		}
	};
}
