package com.join;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class LineAutoCompleteTextView extends AutoCompleteTextView{
	

		private Paint mPaint;
		/**
		 * @param context
		 * @param attrs
		 */
		public LineAutoCompleteTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.GRAY);
		}
		
		@Override
		public void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);		
			canvas.drawLine(0,this.getHeight()-1,  this.getWidth()-1, this.getHeight()-1, mPaint);
		}
	
}
