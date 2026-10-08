package be.vives.loic.shopandcook.activities;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import be.vives.loic.shopandcook.R;

/**
 * Step by step cooking guide: one instruction at a time, screen kept on while cooking.
 */
public class CookingModeActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "recipe_title";
    public static final String EXTRA_STEPS = "recipe_steps";
    private static final String STATE_INDEX = "step_index";

    private ArrayList<String> steps;
    private int index;

    private TextView stepCounter;
    private TextView stepText;
    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooking);
        EdgeToEdge.apply(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        steps = getIntent().getStringArrayListExtra(EXTRA_STEPS);
        if (steps == null || steps.isEmpty()) {
            finish();
            return;
        }
        index = savedInstanceState != null ? savedInstanceState.getInt(STATE_INDEX) : 0;

        stepCounter = findViewById(R.id.stepCounter);
        stepText = findViewById(R.id.stepText);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(v -> showStep(index - 1));
        nextButton.setOnClickListener(v -> {
            if (index == steps.size() - 1) {
                finish();
            } else {
                showStep(index + 1);
            }
        });
        showStep(index);
    }

    private void showStep(int newIndex) {
        index = Math.max(0, Math.min(newIndex, steps.size() - 1));
        stepCounter.setText(getString(R.string.step_of, index + 1, steps.size()));
        stepText.setText(steps.get(index));
        prevButton.setEnabled(index > 0);
        nextButton.setText(index == steps.size() - 1 ? R.string.finish : R.string.next);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_INDEX, index);
    }
}
