package ca.hashbrown.snapable.ui.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import ca.hashbrown.snapable.R;

public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout {

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setColorSchemeColors(
                R.color.progress_color_1,
                R.color.progress_color_2,
                R.color.progress_color_3,
                R.color.progress_color_4);
    }

    @Override
    public boolean canChildScrollUp() {
        View list = findViewById(android.R.id.list);
        return list.getVisibility() == VISIBLE && list.canScrollVertically(-1);
    }
}