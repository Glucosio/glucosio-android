/*
 * Copyright (C) 2016 Glucosio Foundation
 *
 * This file is part of Glucosio.
 *
 * Glucosio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Glucosio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Glucosio.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.glucosio.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.adapter.HomePagerAdapter;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.presenter.ExportPresenter;
import org.glucosio.android.presenter.MainPresenter;

import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_INVITE = 1;
    private ExportPresenter exportPresenter;
    private RadioButton exportRangeButton;
    private HomePagerAdapter homePagerAdapter;
    private MainPresenter presenter;
    private ViewPager viewPager;

    private TextView exportDialogDateFrom;
    private TextView exportDialogDateTo;

    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabGlucoseEmpty;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlucosioApplication application = (GlucosioApplication) getApplication();

        setContentView(R.layout.activity_main);
        initPresenters(application);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setLogo(R.drawable.ic_logo);
        }

        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), getApplicationContext());

        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    hideFabAnimation();
                    LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.mainactivity_empty_layout);
                    ViewPager pager = (ViewPager) findViewById(R.id.pager);
                    if (pager.getVisibility() == View.GONE) {
                        pager.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.INVISIBLE);
                    }
                } else {
                    showFabAnimation();
                    checkIfEmptyLayout();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabGlucoseEmpty = (FloatingActionButton) findViewById(R.id.fab_glucose_empty);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_add_reading);
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                // When Fab Menu is opened, dim the main view.
                if (opened) {
                    if (!presenter.isdbEmpty()) {
                        AlphaAnimation alpha = new AlphaAnimation(1F, 0.2F);
                        alpha.setDuration(600);
                        alpha.setFillAfter(true);
                        viewPager.startAnimation(alpha);
                    }
                } else {
                    if (!presenter.isdbEmpty()) {
                        removeWhiteOverlay();
                    }
                }
            }
        });

        // Add Nav Drawer
        final PrimaryDrawerItem itemSettings = new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(R.drawable.ic_settings_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemExport = new PrimaryDrawerItem().withName(R.string.sidebar_backup_export).withIcon(R.drawable.ic_backup_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemFeedback = new PrimaryDrawerItem().withName(R.string.menu_support).withIcon(R.drawable.ic_announcement_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemAbout = new PrimaryDrawerItem().withName(R.string.preferences_about_glucosio).withIcon(R.drawable.ic_info_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemDonate = new PrimaryDrawerItem().withName(R.string.about_donate).withIcon(R.drawable.ic_favorite_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemA1C = new PrimaryDrawerItem().withName(R.string.activity_converter_title).withIcon(R.drawable.ic_calculator_a1c_grey_24dp).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);


        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(new AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.drawer_header)
                        .build()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.equals(itemSettings)) {
                            // Settings
                            openPreferences();
                        } else if (drawerItem.equals(itemAbout)) {
                            // About
                            startAboutActivity();
                        } else if (drawerItem.equals(itemFeedback)) {
                            // Feedback
                            openSupportDialog();
                        } else if (drawerItem.equals(itemExport)) {
                            // Export
                            startExportActivity();
                        } else if (drawerItem.equals(itemDonate)) {
                            // Donate
                            openDonateIntent();
                        } else if (drawerItem.equals(itemA1C)) {
                            openA1CCalculator();
                        }
                        return false;
                    }
                });

        drawerBuilder.addDrawerItems(
                itemA1C,
                itemExport,
                itemSettings,
                itemFeedback,
                itemAbout,
                itemDonate
        )
                .withSelectedItem(-1)
                .build();

        // Restore pager position
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            viewPager.setCurrentItem(b.getInt("pager"));
        }

        checkIfEmptyLayout();
    }

    private void initPresenters(GlucosioApplication application) {
        final DatabaseHandler dbHandler = application.getDBHandler();
        presenter = new MainPresenter(this, dbHandler);
        exportPresenter = new ExportPresenter(this, dbHandler);
    }

    private void openA1CCalculator() {
        Intent calculatorIntent = new Intent(this, A1cCalculatorActivity.class);
        startActivity(calculatorIntent);
    }

    private void openDonateIntent() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.glucosio.org/donate/"));
        startActivity(browserIntent);
    }

    public void startExportActivity() {
        openBackupDialog();
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void startHelloActivity() {
        Intent intent = new Intent(this, HelloActivity.class);
        startActivity(intent);
        finish();
    }

    public void openPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
    }

    public void onGlucoseFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddGlucoseActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onKetoneFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddKetoneActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onPressureFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddPressureActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onHB1ACFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddA1CActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onCholesterolFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddCholesterolActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onWeightFabClicked(View v) {
        fabMenu.toggle(false);
        Intent intent = new Intent(this, AddWeightActivity.class);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt("pager", viewPager.getCurrentItem());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void removeWhiteOverlay() {
        AlphaAnimation alpha = new AlphaAnimation(viewPager.getAlpha(), 1F);
        alpha.setDuration(0);
        alpha.setFillAfter(true);
        viewPager.startAnimation(alpha);
    }

    public void openSupportDialog() {
        final Context mContext = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.menu_support_title));
        builder.setItems(getResources().getStringArray(R.array.menu_support_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Email
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:hello@glucosio.org"));
                    boolean activityExists = emailIntent.resolveActivityInfo(getPackageManager(), 0) != null;

                    if (activityExists) {
                        startActivity(emailIntent);
                    } else {
                        showSnackBar(getResources().getString(R.string.menu_support_error1), Snackbar.LENGTH_LONG);
                    }
                } else {
                    // Forum
                    String url = "http://community.glucosio.org/";
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setPackage("com.android.chrome");
                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        // Chrome is probably not installed
                        // Try with the default browser
                        i.setPackage(null);
                        startActivity(i);
                    }
                }
            }
        });
        builder.show();
    }

    public void openBackupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.activity_main_dialog_backup_export_title));
        builder.setItems(getResources().getStringArray(R.array.menu_backup_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Play Services are not present, show an error message
                    Toast.makeText(getApplicationContext(), R.string.common_google_play_services_unsupported_text, Toast.LENGTH_SHORT).show();
                } else {
                    // Export to CSV
                    showExportCsvDialog();
                }
            }
        });
        builder.show();
    }

    public void showExportCsvDialog() {
        final Dialog exportDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(exportDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        exportDialog.setContentView(R.layout.dialog_export);
        exportDialog.getWindow().setAttributes(lp);
        exportDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        exportDialog.getWindow().setDimAmount(0.5f);
        exportDialog.show();

        exportDialogDateFrom = (TextView) exportDialog.findViewById(R.id.activity_export_date_from);
        exportDialogDateTo = (TextView) exportDialog.findViewById(R.id.activity_export_date_to);
        exportRangeButton = (RadioButton) exportDialog.findViewById(R.id.activity_export_range);
        final RadioButton exportAllButton = (RadioButton) exportDialog.findViewById(R.id.activity_export_all);
        final TextView exportButton = (TextView) exportDialog.findViewById(R.id.dialog_export_add);
        final TextView cancelButton = (TextView) exportDialog.findViewById(R.id.dialog_export_cancel);

        exportRangeButton.setChecked(true);

        exportDialogDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "fromDateDialog");
                dpd.setMaxDate(now);
            }
        });

        exportDialogDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "toDateDialog");
                dpd.setMaxDate(now);
            }
        });

        exportRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = exportRangeButton.isChecked();
                exportDialogDateFrom.setEnabled(true);
                exportDialogDateTo.setEnabled(true);
                exportAllButton.setChecked(!isChecked);
            }
        });

        exportAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = exportAllButton.isChecked();
                exportDialogDateFrom.setEnabled(false);
                exportDialogDateTo.setEnabled(false);
                exportRangeButton.setChecked(!isChecked);
                exportButton.setEnabled(true);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateExportDialog()) {
                    exportPresenter.onExportClicked(exportAllButton.isChecked());
                    exportDialog.dismiss();
                } else {
                    showSnackBar(getResources().getString(R.string.dialog_error), Snackbar.LENGTH_LONG);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDialog.dismiss();
            }
        });

    }

    private boolean validateExportDialog() {
        String dateTo = exportDialogDateTo.getText().toString();
        String dateFrom = exportDialogDateFrom.getText().toString();
        return !exportRangeButton.isChecked() || !(dateTo.equals("") || dateFrom.equals(""));
    }

    public CoordinatorLayout getFabView() {
        return (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    public void reloadFragmentAdapter() {
        homePagerAdapter.notifyDataSetChanged();
    }

    public void turnOffToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public void turnOnToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    private void hideFabAnimation() {
        final View fab = findViewById(R.id.fab_menu_add_reading);
        fab.animate()
                .translationY(-5)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void showFabAnimation() {
        final View fab = findViewById(R.id.fab_menu_add_reading);
        if (fab.getVisibility() == View.INVISIBLE) {
            // Prepare the View for the animation
            fab.setVisibility(View.VISIBLE);
            fab.setAlpha(0.0f);

            fab.animate()
                    .alpha(1f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // do nothing
            // probably swiping from OVERVIEW to HISTORY tab
        }
    }

    public void checkIfEmptyLayout() {
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.mainactivity_empty_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        if (presenter.isdbEmpty()) {
            pager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

            // If empty show only Glucose fab
            fabMenu.setVisibility(View.GONE);
            fabGlucoseEmpty.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getResources().getConfiguration().orientation == 1) {
                    // If Portrait choose vertical curved line
                    ImageView arrow = (ImageView) findViewById(R.id.mainactivity_arrow);
                    arrow.setBackground(getResources().getDrawable(R.drawable.curved_line_vertical));
                } else {
                    // Else choose horizontal one
                    ImageView arrow = (ImageView) findViewById(R.id.mainactivity_arrow);
                    arrow.setBackground((getResources().getDrawable(R.drawable.curved_line_horizontal)));
                }
            }
        } else {
            pager.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            fabGlucoseEmpty.setVisibility(View.GONE);
        }
    }

    public void showExportedSnackBar(int nReadings) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_snackbar_1) + " " + nReadings + " " + getString(R.string.activity_export_snackbar_2), Snackbar.LENGTH_SHORT).show();
    }

    public void showNoReadingsSnackBar() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_no_readings_snackbar), Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBar(String text, int lengthLong) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, text, lengthLong).show();
    }

    public void showShareDialog(Uri uri) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setData(uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_using)));
    }

    private void rebootApp() {
        Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Check which dialog set the date
        if (view.getTag().equals("fromDateDialog")) {
            exportPresenter.setFromYear(year);
            exportPresenter.setFromMonth(monthOfYear);
            exportPresenter.setFromDay(dayOfMonth);

            int monthToShow = monthOfYear + 1;
            String date = +dayOfMonth + "/" + monthToShow + "/" + year;
            exportDialogDateFrom.setText(date);
        } else {
            exportPresenter.setToYear(year);
            exportPresenter.setToMonth(monthOfYear);
            exportPresenter.setToDay(dayOfMonth);

            int monthToShow = monthOfYear + 1;
            String date = +dayOfMonth + "/" + monthToShow + "/" + year;
            exportDialogDateTo.setText(date);
        }
    }

    public void onA1cInfoClicked(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(getString(R.string.overview_hb1ac_info))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .show();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private void showErrorDialogPlayServices() {
        Toast.makeText(getApplicationContext(), R.string.activity_main_error_play_services, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
