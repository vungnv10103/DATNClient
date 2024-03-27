package com.datn.client.ui.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MiddleMultilineTextView extends AppCompatTextView {
    private final String SYMBOL = " ... ";
    private final int SYMBOL_LENGTH = SYMBOL.length();

    public MiddleMultilineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMaxLines() > 1) {
            int originalLength = getText().length();
            int visibleLength = getVisibleLength();

            if (originalLength > visibleLength) {
                setText(smartTrim(getText().toString(), visibleLength - SYMBOL_LENGTH));
            }
        }
    }

    private String smartTrim(String string, int maxLength) {
        if (string == null)
            return null;
        if (maxLength < 1)
            return string;
        if (string.length() <= maxLength)
            return string;
        if (maxLength == 1)
            return string.charAt(0) + "...";

        int midpoint = (int) Math.ceil((double) string.length() / 2);
        int toRemove = string.length() - maxLength;
        int lStrip = (int) Math.ceil((double) toRemove / 2);
        int rStrip = toRemove - lStrip;

        return string.substring(0, midpoint - lStrip) + SYMBOL + string.substring(midpoint + rStrip);
    }

    private int getVisibleLength() {
        int start = getLayout().getLineStart(0);
        int end = getLayout().getLineEnd(getMaxLines() - 1);
        return getText().toString().substring(start, end).length();
    }
}
