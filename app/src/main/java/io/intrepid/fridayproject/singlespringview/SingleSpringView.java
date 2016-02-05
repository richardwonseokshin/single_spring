package io.intrepid.fridayproject.singlespringview;

/**
 * Intrepid Pursuits
 * 222 3rd St. Suite 4000, Cambridge, MA 02142
 * Friday Time Project: Simple single spring simulation (no dampening)
 *
 * @author Won Seok Shin
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class SingleSpringView extends View {

    // update params
    private static final int TIMER_UPDATE_INTERVAL = 50; // millis
    // spring params
    private static final int SPRING_COIL_RADIUS = 40;
    private static final int SPRING_NUM_COILS = 10;
    private static final float SPRING_CONSTANT = 0.15f;
    private static final float EQUILIBRIUM_HEIGHT = 300;
    private static final float MOCK_INITIAL_DISPLACEMENT = 75;
    // rendering params
    private static final int PAINT_COLOR_GROUND = Color.argb(255, 165, 42, 42);
    private static final int PAINT_COLOR_SPRING = Color.BLUE;
    private static final int PAINT_SPRING_COIL_STROKE_WIDTH = 5;

    // rendering
    private Paint paintGround;
    private Paint paintSpringTip;
    private Paint paintSpringCoil;

    // updating
    private Timer timer;

    // spring properties
    private float springPosY;
    private float springVelocity = 0;

    public SingleSpringView(Context context) {
        super(context);
        init();
    }

    public SingleSpringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SingleSpringView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // init paints
        paintGround = new Paint();
        paintGround.setStyle(Paint.Style.FILL);
        paintGround.setColor(PAINT_COLOR_GROUND);

        paintSpringTip = new Paint();
        paintSpringTip.setStyle(Paint.Style.FILL);
        paintSpringTip.setColor(PAINT_COLOR_SPRING);

        paintSpringCoil = new Paint();
        paintSpringCoil.setStyle(Paint.Style.STROKE);
        paintSpringCoil.setColor(PAINT_COLOR_SPRING);
        paintSpringCoil.setStrokeWidth(PAINT_SPRING_COIL_STROKE_WIDTH);

        // introduce spring displacement
        setSpringDisplacement(MOCK_INITIAL_DISPLACEMENT);

        // run updater
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                postInvalidate();
            }
        }, 0, TIMER_UPDATE_INTERVAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = canvas.getWidth() / 2;
        float centerY = canvas.getHeight() / 2;

        // flip canvas so it is rightside up
        canvas.scale(1, -1, centerX, centerY);

        // draw the ground
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight() / 2, paintGround);

        // draw a circle at the spring's tip
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawCircle(0, springPosY, 30, paintSpringTip);

        // draw the spring coils
        canvas.drawPath(getSpringCoilPath(), paintSpringCoil);
    }

    private void setSpringDisplacement(float displacement) {
        springPosY = EQUILIBRIUM_HEIGHT + displacement;
    }

    private void update() {
        // find force on object using Hooke's Law
        float displacement = EQUILIBRIUM_HEIGHT - springPosY;
        float springAcceleration = SPRING_CONSTANT * displacement;
        // use basic kinematics (we can ignore mass) to integrate acceleration->velocity->position
        springVelocity = springVelocity + springAcceleration;
        springPosY = springPosY + springVelocity;
    }

    private Path getSpringCoilPath() {
        // make a path to draw the spring
        Path springCoilPath = new Path();
        springCoilPath.moveTo(0, 0);

        int coilRadius = SPRING_COIL_RADIUS;
        int numCoils = SPRING_NUM_COILS;
        for (float i = 0; i < numCoils; i++) {
            float coilXPos = i == 0 || i == numCoils - 1 ? 0 : coilRadius;
            float coilYPos = springPosY * (i / (numCoils - 1));
            springCoilPath.lineTo(coilXPos, coilYPos);
            coilRadius = -coilRadius;
        }

        return springCoilPath;
    }

}
