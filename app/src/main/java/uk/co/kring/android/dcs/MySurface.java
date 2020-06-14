package uk.co.kring.android.dcs;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import androidx.core.graphics.BlendModeColorFilterCompat;
import android.view.SurfaceHolder.Callback;
import androidx.core.graphics.BlendModeCompat;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class MySurface extends SurfaceView implements Callback {

    Bitmap[] font;
    Bitmap screen;
    Canvas drawing;
    Paint blend = new Paint();
    Paint bg = new Paint();
    Rect screenRect, viewRect, bottom, top;
    boolean isPaused = true;

    //============================== PUBLIC INTERFACE
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
            setInput();
        } catch(Exception e) {
            throw new ActivityException(e);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas c = getHolder().lockCanvas();
        draw(c);
        getHolder().unlockCanvasAndPost(c);
        setFocusable(true);
        requestFocus();
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
                xPos = (event.getX() / getWidth()) *
                        (float)(screen.getWidth() / UtilStatic.width);
                yPos = (event.getY() / getHeight()) *
                        (float)(screen.getHeight() / UtilStatic.height);
                sxPos = (event.getX() / getWidth()) *
                        (float)(screen.getWidth() / UtilStatic.sprite);
                syPos = (event.getY() / getHeight()) *
                        (float)(screen.getHeight() / UtilStatic.sprite);
                break;
            default:
                break;
        }
        return true;
    }

    public final static int UP       = 1;
    public final static int LEFT     = 2;
    public final static int RIGHT    = 3;
    public final static int DOWN     = 4;

    //action buttons
    public final static int A        = 0;//primary
    public final static int B        = 1;//exit/back
    public final static int X        = 2;
    public final static int Y        = 3;
    public final static int L1       = 4;
    public final static int R1       = 5;
    public final static int MENU     = 6;
    public final static int PAUSE    = 7;
    public final static int META     = 8;
    public final static int ACTION   = 9;
    public final static int BACK     = 10;
    public final static int SCAN     = 11;
    public final static int INFO     = 12;

    public static int[] codes = new int[INFO];
    public static boolean[] buttons = new boolean[INFO];

    public void setInput() {
        codes[A] = UtilStatic.A;
        codes[B] = UtilStatic.B;
        codes[X] = UtilStatic.X;
        codes[Y] = UtilStatic.Y;
        codes[L1] = UtilStatic.L1;
        codes[R1] = UtilStatic.R1;
        codes[MENU] = UtilStatic.MENU;
        codes[PAUSE] = UtilStatic.PAUSE;
        codes[META] = UtilStatic.META;
        codes[ACTION] = UtilStatic.ACTION;
        codes[BACK] = UtilStatic.BACK;
        codes[SCAN] = UtilStatic.SCAN;
        codes[INFO] = UtilStatic.INFO;
        UtilStatic.configJoystick(getContext());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        for(int i = 0; i < codes.length; ++i) {
            if(UtilStatic.isButton(event, codes[i], isPaused, true)) {
                buttons[i] = true;
                return true;
            }
        }
        dx = UtilStatic.getDirectionPressedX(event, dx, true);
        dy = UtilStatic.getDirectionPressedX(event, dy, true);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        for(int i = 0; i < codes.length; ++i) {
            if(UtilStatic.isButton(event, codes[i], isPaused, false)) {
                buttons[i] = false;
                return true;
            }
        }
        dx = UtilStatic.getDirectionPressedX(event, dx, false);
        dy = UtilStatic.getDirectionPressedX(event, dy, false);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.isFromSource(InputDevice.SOURCE_CLASS_JOYSTICK)) {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                dx = UtilStatic.getDirectionPressedX(event, dx, true);
                dy = UtilStatic.getDirectionPressedX(event, dy, true);
                return true;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    //surface handling
    public void charAt(char ch, float x, float y, int fColor, int bColor) {
        bg.setColor(bColor);
        blend.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                fColor, BlendModeCompat.SRC_OVER));//on top tint
        x *= UtilStatic.width;
        y *= UtilStatic.height;
        drawing.drawRect(x, y, x + UtilStatic.width,
                y + UtilStatic.height, bg);
        //default to 1K characters
        drawing.drawBitmap(font[ch & 1023], x, y, blend);
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
    float sxPos, syPos;
    float dx, dy;

    public void setOffsetTouchXY(float x, float y) {
        xPosO = x;
        yPosO = y;
    }

    public float getTouchX() {
        return xPos - xPosO;
    }

    public float getTouchY(boolean bottom) {
        return yPos - yPosO - (bottom ? (screen.getHeight() / 2F) / UtilStatic.height : 0);
    }

    public float getMapTouchX() {
        return sxPos;
    }

    public float getMapTouchY(boolean bottom) {
        return syPos - (bottom ? (screen.getHeight() / 2F) / UtilStatic.sprite : 0);
    }

    public float joyX() {
        return (float)(dx * ((Math.abs(dy) > 0.1f) ? Math.sqrt(1f / 2f) : 1f));
    }

    public float joyY() {
        return (float)(dy * ((Math.abs(dx) > 0.1f) ? Math.sqrt(1f / 2f) : 1f));
    }
}
