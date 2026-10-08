package be.vives.loic.shopandcook.activities;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import be.vives.loic.shopandcook.R;

/**
 * android:fitsSystemWindows no longer reserves space for system bars once an app targets
 * API 35+ (edge-to-edge is enforced). This restores the old behavior by padding for the
 * system bars ourselves.
 *
 * When the activity has a top Toolbar (id/toolbar), the status bar inset is applied to the
 * Toolbar's own padding instead of the content root, so its colored background extends behind
 * the status bar instead of leaving a blank gap above it; the Toolbar's layout_height must be
 * wrap_content with a minHeight of ?attr/actionBarSize for this to not squash its contents.
 */
final class EdgeToEdge {
    private EdgeToEdge() {
    }

    static void apply(Activity activity) {
        View content = activity.findViewById(android.R.id.content);
        View toolbar = activity.findViewById(R.id.toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (toolbar != null) {
                toolbar.setPadding(toolbar.getPaddingLeft(), systemBars.top,
                        toolbar.getPaddingRight(), toolbar.getPaddingBottom());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            } else {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });
    }
}
