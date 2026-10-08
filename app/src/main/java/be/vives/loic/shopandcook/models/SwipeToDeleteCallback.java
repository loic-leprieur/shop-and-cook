package be.vives.loic.shopandcook.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import be.vives.loic.shopandcook.R;

/**
 * Swipe-right-to-delete gesture for a RecyclerView row: reveals a red background with a
 * trash icon behind the item as it's dragged away.
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    public interface OnSwipedListener {
        void onSwiped(int position);
    }

    private final ColorDrawable background = new ColorDrawable(Color.parseColor("#D32F2F"));
    private final Drawable deleteIcon;
    private final int iconMargin;
    private final OnSwipedListener listener;

    public SwipeToDeleteCallback(Context context, OnSwipedListener listener) {
        super(0, ItemTouchHelper.RIGHT);
        this.listener = listener;
        this.deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        this.iconMargin = (int) (16 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                           @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder.getBindingAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                             float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        if (dX > 0) {
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            background.draw(c);

            if (deleteIcon != null) {
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
