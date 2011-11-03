package com.sturtz.viewpagerheader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Shader;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.exallium.gitissues.R;


public class ViewPagerHeader extends View implements ViewPager.OnPageChangeListener {
  
  private ViewPager mPager;
  
  private String mTitleLeft = "";
  private String mTitleCenter = "";
  private String mTitleRight = "";
  
  private int mBackgroundColor = Color.WHITE;
  private int mTextColor = Color.BLACK;
  private int mLineColor = Color.argb(0xFF, 0xA4, 0xC6, 0x39);
  
  private int mTextSize = 20;
  
  private int mWidth = 0;
  private int mHeight = 0;
  
  private float mTitleCenterLeft = -1000.0f;
  
  private int mCurrentPosition;
  private int mOldPosition;
  
  
  private int mLabelHeight = 0;
  
  private float mLabelLeftWidth = 0.0f;
  private float mLabelCenterWidth = 0.0f;
  private float mLabelRightWidth = 0.0f;
  
  private static final int LINE_PADDING = 15;
  private static final int LINE_MAX_ALPHA = 255;
  private static final float LINE_MAX_HEIGHT = 5.0f;
  
  private float mPercent = 100.0f;
  
  // shadow
  
  private static final int SHADOW_WIDTH = 40;
  private static final int SHADOW_DARK_ALPHA = 0xFF;
  
  // padding of each label
  
  private static final int LABEL_PADDING = 30;
  
  
  // provider, listener
  
  private ViewPagerHeaderProvider mProvider;
  private ViewPagerHeaderListener mListener;
  
  
  
  public ViewPagerHeader(Context context) {
    this(context, null);
  }
  
  public ViewPagerHeader(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public ViewPagerHeader(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    
    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerHeader, defStyle, 0);
    
    mTitleLeft = a.getString(R.styleable.ViewPagerHeader_titleLeft);
    mTitleCenter = a.getString(R.styleable.ViewPagerHeader_titleCenter);
    mTitleRight = a.getString(R.styleable.ViewPagerHeader_titleRight);
    mBackgroundColor = a.getColor(R.styleable.ViewPagerHeader_backgroundColor, mBackgroundColor);
    mTextColor = a.getColor(R.styleable.ViewPagerHeader_textColor, mTextColor);
    mLineColor = a.getColor(R.styleable.ViewPagerHeader_lineColor, mLineColor);
    mTextSize = a.getDimensionPixelSize(R.styleable.ViewPagerHeader_textSize, mTextSize);
    
    mCurrentPosition = 0;
    mOldPosition = 0;
    
    setTitles();
    
    a.recycle();
  }
  
  
  public void setViewPager(ViewPager pager) {
    this.mPager = pager;
    this.mPager.setOnPageChangeListener(this);
    
    if (pager.getAdapter() instanceof ViewPagerHeaderProvider) {
      this.mProvider = (ViewPagerHeaderProvider) pager.getAdapter();
    } else {
      throw new IllegalStateException("The pager's adapter has to implement ViewPagerHeaderProvider.");
    }
    
    setTitles();
  }
  
  public void setViewPager(ViewPager pager, int item) {
    setViewPager(pager);
    mPager.setCurrentItem(item);
    setCurrentItem(item);
  }
  
  
  public void setViewPagerHeaderProvider(ViewPagerHeaderProvider source) {
    this.mProvider = source;
  }
  
  public void setViewPagerHeaderListener(ViewPagerHeaderListener listener) {
    this.mListener = listener;
  }
  
  
  private void setTitles() {
    if (mProvider != null) {
      mTitleLeft = mProvider.getTitle(mCurrentPosition - 1);
      mTitleCenter = mProvider.getTitle(mCurrentPosition);
      mTitleRight = mProvider.getTitle(mCurrentPosition + 1);
    }
    mTitleLeft = mTitleLeft == null ? mTitleLeft = "" : mTitleLeft;
    mTitleCenter = mTitleCenter == null ? mTitleCenter = "" : mTitleCenter;
    mTitleRight = mTitleRight == null ? mTitleRight = "" : mTitleRight;
    
    // re-measure width of center label
    
    final Paint labelCenterPaint = mLabelCenterPaint;
    labelCenterPaint.setTextSize(mTextSize);
    
    mLabelCenterWidth = mTitleCenter != "" ? labelCenterPaint.measureText(mTitleCenter) : 0;
    
    invalidate();
  }
  
  
  public void setCurrentItem(int item) {
    mCurrentPosition = item;
    mOldPosition = item;
    
    setTitles();
  }
  
  
  
  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(resolveSize(0, widthMeasureSpec), resolveSize(0, heightMeasureSpec));
  }
  
  
  
  private Paint mLabelPaint = new Paint();
  private Paint mLabelCenterPaint = new Paint();
  private Paint mArrowPaint = new Paint();
  private Paint mShadowLeftPaint = new Paint();
  private Paint mShadowRightPaint = new Paint();
  
  
  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    final Paint labelPaint = mLabelPaint;
    labelPaint.setColor(mTextColor);
    labelPaint.setTextSize(mTextSize);
    labelPaint.setAntiAlias(true);
    
    final Paint labelCenterPaint = mLabelCenterPaint;
    labelCenterPaint.setTextSize(mTextSize);
    labelCenterPaint.setAntiAlias(true);
    labelCenterPaint.setColor(interpColor(new int[] { mTextColor, mLineColor }, mPercent / 100.0f));
    
    
    final Paint arrowPaint = mArrowPaint;
    arrowPaint.setColor(mLineColor);
    arrowPaint.setAlpha((int) (LINE_MAX_ALPHA * mPercent / 100.0f));
    
    
    int shadowDark = Color.argb(SHADOW_DARK_ALPHA, Color.red(mBackgroundColor), Color.green(mBackgroundColor),
        Color.blue(mBackgroundColor));
    int shadowLight = Color.argb(0x00, Color.red(mBackgroundColor), Color.green(mBackgroundColor),
        Color.blue(mBackgroundColor));
    
    
    final Shader shadowLeftShader = new LinearGradient(0, 0, SHADOW_WIDTH, 0, shadowDark, shadowLight,
        Shader.TileMode.MIRROR);
    final Shader shadowRightShader = new LinearGradient(mWidth - SHADOW_WIDTH, 0, mWidth, 0, shadowLight, shadowDark,
        Shader.TileMode.MIRROR);
    
    final Paint shadowLeftPaint = mShadowLeftPaint;
    shadowLeftPaint.setShader(shadowLeftShader);
    shadowLeftPaint.setAntiAlias(true);
    
    final Paint shadowRightPaint = mShadowRightPaint;
    shadowRightPaint.setShader(shadowRightShader);
    shadowRightPaint.setAntiAlias(true);
    
    
    // measure
    
    final FontMetricsInt metrics = labelPaint.getFontMetricsInt();
    mLabelHeight = Math.abs(metrics.ascent);
    
    
    mLabelLeftWidth = mTitleLeft != "" ? labelPaint.measureText(mTitleLeft) : 0;
    mLabelCenterWidth = mTitleCenter != "" ? labelCenterPaint.measureText(mTitleCenter) : 0;
    mLabelRightWidth = mTitleRight != "" ? labelPaint.measureText(mTitleRight) : 0;
    
    mWidth = getWidth();
    mHeight = getHeight();
    
    float labelLeftX = 0;
    float labelRightX = mWidth - mLabelRightWidth;
    
    if (mTitleCenterLeft == -1000) mTitleCenterLeft = (mWidth - mLabelCenterWidth) / 2;
    
    
    if (labelLeftX + mLabelLeftWidth > mTitleCenterLeft - LABEL_PADDING) {
      labelLeftX = mTitleCenterLeft - LABEL_PADDING - mLabelLeftWidth;
    }
    
    if (labelRightX < mTitleCenterLeft + mLabelCenterWidth + LABEL_PADDING) {
      labelRightX = mTitleCenterLeft + mLabelCenterWidth + LABEL_PADDING;
    }
    
    
    // background
    
    canvas.drawColor(mBackgroundColor);
    
    // indicator
    
    canvas.drawRect(mTitleCenterLeft - LINE_PADDING, mHeight - (LINE_MAX_HEIGHT * mPercent / 100.0f),
        mTitleCenterLeft
            + mLabelCenterWidth + LINE_PADDING, mHeight, arrowPaint);
    
    // labels
    
    final float labelY = (mHeight - mLabelHeight) / 2 + mLabelHeight;
    
    canvas.drawText(mTitleLeft, 0, mTitleLeft.length(), labelLeftX, labelY, labelPaint);
    canvas.drawText(mTitleRight, 0, mTitleRight.length(), labelRightX, labelY, labelPaint);
    
    canvas.drawText(mTitleCenter, 0, mTitleCenter.length(), mTitleCenterLeft, labelY, labelCenterPaint);
    
    // shadows
    
    canvas.drawRect(0, 0, SHADOW_WIDTH, mHeight, shadowLeftPaint);
    canvas.drawRect(mWidth - SHADOW_WIDTH, 0, mWidth, mHeight, shadowRightPaint);
    
  }
  
  
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    
    int action = event.getAction();
    
    if (action == MotionEvent.ACTION_UP) {
      
      float x = event.getX();
      
      float leftX = mWidth / 5;
      float rightX = mWidth / 5 * 4;
      
      if (x > 0 && x < leftX) {
        if (mListener != null) mListener.prev();
        
      } else
        if (x < mWidth && x > rightX) {
          if (mListener != null) mListener.next();
        }
      
    }
    
    return true;
  }
  
  
  
  public void onPageScrollStateChanged(int state) {
  }
  
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    
    mCurrentPosition = positionOffset < .5f ? position : position + 1;
    
    if (mCurrentPosition != mOldPosition) {
      mOldPosition = mCurrentPosition;
      setTitles();
    }
    
    final float startX = 0;
    float endX = mWidth - mLabelCenterWidth;
    final float centerX = (mWidth - mLabelCenterWidth) / 2;
    
    
    // percent calculations
    
    final float positionOffsetNew = positionOffset < .5f ? 1.0f - positionOffset : positionOffset;
    mPercent = 0.0f + (positionOffsetNew - 0.5f) * (100.0f - 0.0f) / (1.0f - 0.5f);
    
    
    // position calculations
    
    final float positionOffsetScaled = 0.0f + (positionOffset - 0.5f) * (100.0f - 0.0f) / (1.0f - 0.5f);
    float x;
    
    if (positionOffsetScaled < 0) {
      x = positionOffsetScaled * (-1);
      mTitleCenterLeft = startX + (centerX * x / 100.0f);
      if (mTitleCenterLeft < startX) mTitleCenterLeft = startX;
    } else {
      x = positionOffsetScaled;
      mTitleCenterLeft = endX - (centerX * x / 100.0f);
      if (mTitleCenterLeft > endX) mTitleCenterLeft = endX;
    }
    
    
    invalidate();
  }
  
  
  public void onPageSelected(int position) {
    if (mListener != null) mListener.onPageSelected(position);
  }
  
  
  
  
  
  private int ave(int s, int d, float p)
  {
    return s + java.lang.Math.round(p * (d - s));
  }
  
  private int interpColor(int colors[], float unit)
  {
    if (unit <= 0)
    {
      return colors[0];
    }
    if (unit >= 1)
    {
      return colors[colors.length - 1];
    }
    
    float p = unit * (colors.length - 1);
    int i = (int) p;
    p -= i;
    
    // now p is just the fractional part [0...1) and i is the index
    int c0 = colors[i];
    int c1 = colors[i + 1];
    int a = ave(Color.alpha(c0), Color.alpha(c1), p);
    int r = ave(Color.red(c0), Color.red(c1), p);
    int g = ave(Color.green(c0), Color.green(c1), p);
    int b = ave(Color.blue(c0), Color.blue(c1), p);
    
    return Color.argb(a, r, g, b);
  }
  
  
  
}
