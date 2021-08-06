package com.example.pethome.storeapp.ui.common;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.example.pethome.storeapp.R;
public class ListItemSpacingDecoration extends RecyclerView.ItemDecoration {
    private final int mVerticalOffsetSize;
    private final int mHorizontalOffsetSize;
    private final boolean mOffsetBottomFab;
    public ListItemSpacingDecoration(int verticalOffsetSize, int horizontalOffsetSize) {
        this(verticalOffsetSize, horizontalOffsetSize, false);
    }
    public ListItemSpacingDecoration(int verticalOffsetSize, int horizontalOffsetSize, boolean offsetBottomFab) {
        mVerticalOffsetSize = verticalOffsetSize;
        mHorizontalOffsetSize = horizontalOffsetSize;
        mOffsetBottomFab = offsetBottomFab;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        boolean isFirstItem = (position == 0);
        boolean isLastItem = (position == (parent.getAdapter().getItemCount() - 1));
        if (isFirstItem) {
            outRect.top = mVerticalOffsetSize;
        }
        outRect.bottom = mVerticalOffsetSize;
        outRect.left = mHorizontalOffsetSize;
        outRect.right = mHorizontalOffsetSize;
        if (isLastItem && mOffsetBottomFab) {
            Resources resources = view.getContext().getResources();
            outRect.bottom += resources.getDimensionPixelSize(R.dimen.fab_material_margin) * 2
                    + resources.getDimensionPixelSize(R.dimen.fab_material_normal_size);
        }
    }
}