/**
 * 
 */
package com.paad.compass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author Harrsh
 * 
 */
public class CompassView extends View {

	private float bearing;
	private Paint circlePaint;
	private String northString;
	private String eastString;
	private String southString;
	private String westString;
	private Paint textPaint;
	private int textHeight;
	private Paint markerPaint;

	/**
	 * @return the bearing
	 */
	public float getBearing() {
		return bearing;
	}

	/**
	 * @param bearing
	 *            the bearing to set
	 */
	public void setBearing(float _bearing) {
		bearing = _bearing;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#dispatchPopulateAccessibilityEvent(android.view.
	 * accessibility.AccessibilityEvent)
	 */
	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		super.dispatchPopulateAccessibilityEvent(event);
		if (isShown()) {
			String bearingStr = String.valueOf(bearing);
			if (bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
				bearingStr = bearingStr.substring(0,
						AccessibilityEvent.MAX_TEXT_LENGTH);

			event.getText().add(bearingStr);
			return true;
		} else
			return false;
	}

	/**
	 * @param context
	 */
	public CompassView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initCompassView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initCompassView();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initCompassView();
	}

	int[] borderGradientColors;
	float[] borderGradientPositions;

	int[] glassGradientColors;
	float[] glassGradientPositions;

	int skyHorizonColorFrom;
	int skyHorizonColorTo;
	int groundHorizonColorFrom;
	int groundHorizonColorTo;

	private void initCompassView() {
		// TODO Auto-generated method stub
		setFocusable(true);

		Resources r = this.getResources();
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(r.getColor(R.color.background_color));
		circlePaint.setStrokeWidth(1);
		circlePaint.setStyle(Paint.Style.STROKE);

		northString = r.getString(R.string.cardinal_north);
		eastString = r.getString(R.string.cardinal_east);
		southString = r.getString(R.string.cardinal_south);
		westString = r.getString(R.string.cardinal_west);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(r.getColor(R.color.text_color));
		textPaint.setFakeBoldText(true);
		textPaint.setSubpixelText(true);
		textPaint.setTextAlign(Align.LEFT);

		textHeight = (int) textPaint.measureText("yY");

		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(r.getColor(R.color.marker_color));
		markerPaint.setAlpha(200);
		markerPaint.setStrokeWidth(1);
		markerPaint.setStyle(Paint.Style.STROKE);
		markerPaint.setShadowLayer(2, 1, 1, r.getColor(R.color.shadow_color));

		borderGradientColors = new int[4];
		borderGradientPositions = new float[4];

		borderGradientColors[3] = r.getColor(R.color.outer_border);
		borderGradientColors[2] = r.getColor(R.color.inner_border_one);
		borderGradientColors[1] = r.getColor(R.color.inner_border_two);
		borderGradientColors[0] = r.getColor(R.color.inner_border);
		borderGradientPositions[3] = 0.0f;
		borderGradientPositions[2] = 1 - 0.03f;
		borderGradientPositions[1] = 1 - 0.06f;
		borderGradientPositions[0] = 1.0f;
		
		glassGradientColors=new int[5];
		glassGradientPositions=new float[5];
		
		int glassColor=245;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		/*
		 * Find the center of the control, and store the length of smallest side
		 * as the compass's radius.
		 */
		int mMeasuredWidth = getMeasuredWidth();
		int mMeasuredHeight = getMeasuredHeight();

		int px = mMeasuredWidth / 2;
		int py = mMeasuredHeight / 2;

		int radius = Math.min(px, py);

		// Draw the background
		canvas.drawCircle(px, py, radius, circlePaint);

		/*
		 * Rotate our perspective so that the 'top' is facing the current
		 * bearing.
		 */
		canvas.save();
		canvas.rotate(-bearing, px, py);

		int textWidth = (int) textPaint.measureText("W");
		int cardinalX = px - textWidth / 2;
		int cardinalY = py - radius + textHeight;

		// Draw the marker every 15 degrees and text every 45.
		for (int i = 0; i < 24; i++) {
			// Draw a marker.
			canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint);

			canvas.save();
			canvas.translate(0, textHeight);

			// Draw the cardinal points
			if (i % 6 == 0) {
				String dirString = "";
				switch (i) {
				case (0): {
					dirString = northString;
					int arrowY = 2 * textHeight;
					canvas.drawLine(px, arrowY, px - 5, 3 * textHeight,
							markerPaint);
					canvas.drawLine(px, arrowY, px + 5, 3 * textHeight,
							markerPaint);
					break;
				}
				case (6):
					dirString = eastString;
					break;
				case (12):
					dirString = southString;
					break;
				case (18):
					dirString = westString;
					break;
				}
				canvas.drawText(dirString, cardinalX, cardinalY, textPaint);
			} else if (i % 3 == 0) {
				// Draw the text every alternate 45 deg
				String angle = String.valueOf(i * 15);
				float angleTextWidth = textPaint.measureText(angle);

				int angleTextX = (int) (px - angleTextWidth / 2);
				int angleTextY = py - radius + textHeight;
				canvas.drawText(angle, angleTextX, angleTextY, textPaint);
			}
			canvas.restore();

			canvas.rotate(15, px, py);
		}
		canvas.restore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		/*
		 * The Compass is a circle that fills as much space as possible. Set the
		 * measured dimensions by figuring out shortest boundary, height or
		 * width.
		 */
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);

		int d = Math.min(measuredWidth, measuredHeight);

		setMeasuredDimension(d, d);
	}

	private int measure(int measureSpec) {
		// TODO Auto-generated method stub
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			/*
			 * As you want to fill the available space always return the full
			 * available bounds.
			 */
			result = specSize;
		}
		return result;
	}

	private float pitch;

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @param pitch
	 *            the _pitch to set
	 */
	public void setPitch(float _pitch) {
		pitch = _pitch;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}

	private float roll;

	/**
	 * @return the roll
	 */
	public float getRoll() {
		return roll;
	}

	/**
	 * @param roll
	 *            the _roll to set
	 */
	public void setRoll(float _roll) {
		roll = _roll;
		sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
	}

}
