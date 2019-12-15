package de.datlag.darkmode.view.helper;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import de.datlag.darkmode.R;

public class MaterialSnackbar {

    private static int margin = 24;
    private static int actionBarHeight = 112;

    public static void configSnackbar(Activity activity, Snackbar snackbar) {
        getDimensions(activity);
        addMargins(snackbar);
        displayTop(snackbar);
        setCustomBackground(activity, snackbar);
    }

    private static void getDimensions(@NotNull Activity activity) {
        margin = (int) activity.getResources().getDimension(R.dimen.snackbar_margin);
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
    }

    private static void addMargins(@NotNull Snackbar snackbar) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(margin, (margin + actionBarHeight), margin, margin);
        snackbar.getView().setLayoutParams(params);
    }

    private static void setCustomBackground(@NotNull Activity activity, @NotNull Snackbar snackbar) {
        snackbar.getView().setBackground(ContextCompat.getDrawable(activity, R.drawable.snackbar_background));
    }

    private static void displayTop(@NotNull Snackbar snackbar) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbar.getView().setLayoutParams(params);
    }
}
