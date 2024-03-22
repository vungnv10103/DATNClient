package com.datn.client.ui.components;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;

public class MyDragShadowBuilder extends View.DragShadowBuilder {
    // The drag shadow image, defined as a drawable object.
    private static Drawable shadow;

    // Constructor.
    public MyDragShadowBuilder(View view) {
        // Store the View parameter.
        super(view);
        // Create a draggable image that fills the Canvas provided by the  system.
        // shadow = new ColorDrawable(Color.RED);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
        // Define local variables.
        int width, height;
        // Set the width of the shadow to half the width of the original View.
        width = getView().getWidth();
        // Set the height of the shadow to half the height of the original View.
        height = getView().getHeight();
        // The drag shadow is a ColorDrawable. Set its dimensions to
        // be the same as the Canvas that the system provides. As a result,
        // the drag shadow fills the Canvas.

        // shadow.setBounds(0, 0, width, height);

        // Set the size parameter's width and height values. These get back
        // to the system through the size parameter.
        outShadowSize.set(width, height);
        // Set the touch point's position to be in the middle of the drag shadow.
        outShadowTouchPoint.set(width / 2, height / 10);
    }

    @Override
    public void onDrawShadow(@NonNull Canvas canvas) {
        super.onDrawShadow(canvas);
        // Draw the ColorDrawable on the Canvas passed in from the system.
        // shadow.draw(canvas);
    }
}
