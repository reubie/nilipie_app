package com.mobipesa.nilipieapp.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobipesa.nilipieapp.BuildConfig;
import com.mobipesa.nilipieapp.R;

import io.codetail.animation.ViewRevealManager;
import io.codetail.widget.RevealFrameLayout;


/**
 * Created by Mbariah on 27/11/18.
 */

public class PromptPopUpView extends LinearLayout {

    final static int SLOW_DURATION = 400;
    //ActionListener listener;
    RevealFrameLayout stack;
    View nextView;
    View root;
    TextView response;
    private int currentViewIndex = 0;

    public PromptPopUpView(Context context) {
        this(context, null, 0);
    }

    public PromptPopUpView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromptPopUpView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        if (context instanceof ActionListener) {
//            listener = (ActionListener) context;
//        } else {
//            throw new IllegalStateException(context + " must implement" + ActionListener.class.getSimpleName());
//        }
        init();
    }

    public void init() {
        root = inflate(getContext(), R.layout.confirm_popup, this);
        setupSubviews();
    }

    private void setupSubviews() {

        stack = root.findViewById(R.id.view_stack);
        response = root.findViewById(R.id.result);

        final ViewRevealManager revealManager = new ViewRevealManager();
        stack.setViewRevealManager(revealManager);

        root.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {

            if (nextView != null) {

                if (BuildConfig.DEBUG) {
                    Log.i(PromptPopUpView.class.getSimpleName(), "onLayoutChanged: ");
                }

                // get the center for the clipping circle
                int cx = (nextView.getLeft() + nextView.getRight()) / 2;
                int cy = (nextView.getTop() + nextView.getBottom()) / 2;

                // get the final radius for the clipping circle
                int dx = Math.max(cx, nextView.getWidth() - cx);
                int dy = Math.max(cy, nextView.getHeight() - cy);
                float finalRadius = (float) Math.hypot(dx, dy);

//                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(nextView, cx, cy, 0,
//                                finalRadius, View.LAYER_TYPE_HARDWARE);

//                revealAnimator.setDuration(SLOW_DURATION);
//                revealAnimator.setInterpolator(new FastOutLinearInInterpolator());
//                revealAnimator.start();
            }

        });

    }

    public void changeStatus(int color, String text) {

        View v = new View(getContext());
        v.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        response.setText(text);
        if (color == 2) {
            v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.lender_colorAccent));
        }
        stack.addView(v);

        nextView = getNext();
        nextView.bringToFront();
        nextView.setVisibility(View.VISIBLE);
    }

    private View getCurrentView() {
        return stack.getChildAt(currentViewIndex);
    }

    private View getNext() {
        return getViewAt(++currentViewIndex);
    }

    private View getViewAt(int index) {
        if (index >= stack.getChildCount()) {
            index = 0;
        } else if (index < 0) {
            index = stack.getChildCount() - 1;
        }
        return stack.getChildAt(index);
    }

//    public interface ActionListener {
//        String[] dataSelection();
//    }

}
