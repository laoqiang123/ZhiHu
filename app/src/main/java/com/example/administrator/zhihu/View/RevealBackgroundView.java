package com.example.administrator.zhihu.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Administrator on 2016/8/25 0025.
 * 点击新闻条目进入详细页面的条目的效果控件。水波纹效果
 * @author laoqiang
 */
public class RevealBackgroundView extends View {
    private Paint paint;
    private int startpostionx;
    private int startpositiony;
    private ObjectAnimator objectAnimator;
    private int currentRadius = 0;
    private static  final  Interpolator INTERPOLATOR = new AccelerateInterpolator();
    private static  final  int START = 0;
    private static  final  int PROCESS = 1;
    private static  final  int FINISHED = 2;
    private int state = START;
    private OnStateListener listener;
    public RevealBackgroundView(Context context) {
        super(context);
        initPaint();
    }
    public RevealBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }
    private void initPaint() {
         paint= new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }

    /**
     *
     * @param color
     * 设置画笔颜色
     */
    public void setPaintColor(int color){
        paint.setColor(color);
    }

    /**
     *
     * @param postion
     *
     */
    public void startFromPosition(int postion[]){
        changeState(PROCESS);
        startpostionx = postion[0];
        startpositiony =postion[1];
        /**
         * 设置动画变化效果，半径变换。
         */
        objectAnimator = ObjectAnimator.ofInt(this,"currentRadius",0,getWidth()+getHeight()).setDuration(600);
        objectAnimator.setInterpolator(INTERPOLATOR);//设置加速插值器
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                changeState(FINISHED);
            }
        });
        objectAnimator.start();
    }

    public void setCurrentRadius(int currentradius) {
        this.currentRadius = currentradius;
        invalidate();
    }
    public void changeState(int state){
       if(this.state == state){
           return;
       }
        this.state = state;
        if(listener!=null){
            listener.changeState(state);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(state==FINISHED){
            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        }else{
            canvas.drawCircle(startpostionx,startpositiony,currentRadius,paint);
        }
    }
    public void setOnStateLinstener(OnStateListener linstener){
        this.listener = linstener;
    }
}
