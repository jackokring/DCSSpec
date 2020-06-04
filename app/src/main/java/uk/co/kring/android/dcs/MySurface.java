package uk.co.kring.android.dcs;

import android.content.Context;
import android.graphics.*;
import androidx.core.graphics.BlendModeColorFilterCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import androidx.core.graphics.BlendModeCompat;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class MySurface extends SurfaceView implements Callback {
    Bitmap[] font;
    Bitmap screen;
    Canvas drawing;
    Paint blend = new Paint();
    Paint bg = new Paint();
    Rect screenRect, viewRect, bottom, top;

    public MySurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        try {
            screen = UtilStatic.getBitmap(context, "font.png");
            screen = screen.copy(Bitmap.Config.ARGB_8888, true);
            drawing = new Canvas(screen);
            font = UtilStatic.getChars();
            bg.setStyle(Paint.Style.FILL);//fill backgrounds
            screenRect = new Rect(0, 0, screen.getWidth(), screen.getHeight());
            viewRect = new Rect(0, 0, getWidth(), getHeight());
            defaultZeroWindow();
        } catch(Exception e) {
            throw new ActivityException(e);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas c = getHolder().lockCanvas();
        draw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Canvas c = getHolder().lockCanvas();
        draw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //null
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bg.setColor(Color.BLACK);
        canvas.drawRect(viewRect, bg);
        canvas.drawBitmap(screen, screenRect,
                viewRect, null);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                xPos = event.getX() / getWidth() *
                        (float)(screen.getWidth() / UtilStatic.width);
                yPos = event.getY() / getHeight() *
                        (float)(screen.getHeight() / UtilStatic.height);
                break;
            default:
                break;
        }
        return true;
    }

    public void charAt(char ch, float x, float y, int fColor, int bColor) {
        bg.setColor(bColor);
        blend.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                fColor, BlendModeCompat.SRC_OVER));//on top tint
        x *= UtilStatic.width;
        y *= UtilStatic.height;
        drawing.drawRect(x, y, x + UtilStatic.width,
                y + UtilStatic.height, bg);
        drawing.drawBitmap(font[ch], x, y, blend);
        invalidate();
    }

    public static class AttributeMap {
        public int foreground(int i) {
            return Color.WHITE;
        }

        public int background(int i) {
            return Color.BLACK;
        }

        public char animator(char ch) {
            return (char)(ch & 1023);
        }
    }

    public void stringAt(String s, float x, float y, AttributeMap color) {
        stringAt(s, x, y, (float)(screen.getWidth() / (float)UtilStatic.width),
                (float)(screen.getHeight() / UtilStatic.height), color);
    }

    public void stringAt(String s, float x, float y,
                         float w, float h, AttributeMap color) {
        if(color == null) color = new AttributeMap();
        float inx = x;//initial x
        float iny = y;
        for(int i = 0; i < s.length(); ++i) {
            charAt(color.animator(s.charAt(i)), x, y,
                    color.foreground(i), color.background(i));
            if(i >= s.length()) break;
            x += 1F;//next
            if(x >= inx + w) {
                x = inx;
                y += 1F;//next line LF
            }
            if(y >= iny + h) {
                y -= 1F;//scroll back
                Bitmap scroll = screen.copy(Bitmap.Config.ARGB_8888, false);
                window((int)(inx * UtilStatic.width), (int)(iny * UtilStatic.height),
                        (int)(w * UtilStatic.width), (int)(h * UtilStatic.height));
                drawing.drawBitmap(scroll, bottom, top, null);//scroll
                defaultZeroWindow();
            }
        }
    }

    public void defaultZeroWindow() {
        window(0, 0, screen.getWidth(), screen.getHeight());
    }

    public void window(int x, int y, int w, int h) {
        bottom = new Rect(x , y + UtilStatic.height,
                x + w, y + h);
        top = new Rect(x, y,
                x + w, y + h - UtilStatic.height);
    }

    public StringBuilder justify(String s, boolean right, int size, char ch) {
        if(s == null) s = "";
        StringBuilder sb = new StringBuilder();
        if(right) {
            if(s.length() > size) {
                sb.append(s.substring(s.length() - size, s.length()));
            } else {
                while(size - sb.length() > s.length()) sb.append(ch);
                sb.append(s);
            }
        } else {
            if(size < s.length()) {
                sb.append(s.substring(0, size));
            } else {
                sb.append(s);
                while(sb.length() < size) sb.append(ch);
            }
        }
        return sb;
    }

    public int getSizeChars() {
        return screen.getWidth() / UtilStatic.width *
                screen.getHeight() / UtilStatic.height;
    }

    public void stringAtClear(String s, AttributeMap color) {
        s = justify(s, false, getSizeChars(), ' ').toString();
        stringAt(s, 0F, 0F, color);//new page
    }

    public void stringAtClear(StringBuffer s, AttributeMap color) {//internal quick
        stringAt(s.toString(), 0F, 0F, color);//new page
    }

    float xPosO, yPosO;
    float xPos, yPos;

    public void setOffsetTouchXY(float x, float y) {
        xPosO = x;
        yPosO = y;
    }

    public float getTouchX() {
        return xPos - xPosO;
    }

    public float getTouchY() {
        return yPos - yPosO;
    }
}
