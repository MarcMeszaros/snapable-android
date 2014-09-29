package ca.hashbrown.snapable.ui.widgets;


import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import ca.hashbrown.snapable.R;

public class ScrollableSwipeRefreshLayout extends SwipeRefreshLayout {

    public ScrollableSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollableSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setColorScheme(
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