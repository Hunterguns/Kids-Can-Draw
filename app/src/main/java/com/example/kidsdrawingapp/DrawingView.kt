package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndo = ArrayList<CustomPath>()


    init{
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mCanvasPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize = 20.toFloat()
    }

    fun onClickUndo(){
        if(mPaths.size > 0){
            mUndo.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }

    }

    fun onclickRedo(){
        if(mUndo.size > 0){
            mPaths.add(mUndo.removeAt(mUndo.size - 1))
            invalidate()
        }
    }


    internal inner class CustomPath(var color : Int, var brushThickness : Float ) : Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas?.drawPath(path,mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas?.drawPath(mDrawPath!!, mDrawPaint!!)
        }

    }

    /**
     * Handles the events of touch
     * MotionEvent.ACTION_DOWN -> When we touch the screen
     * MotionEvent.ACTION_MOVE -> When we drag or move on the screen
     * MotionEvent.ACTION_UP -> When we release
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var touchX = event?.x
        var touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_MOVE ->{
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }

            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color,mBrushSize)
            }

            else-> return false
        }
        invalidate()

        return true
    }

    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
    }

    fun setColor(newColor : String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

}