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
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.adapter.HomePagerAdapter;
import org.glucosio.android.analytics.Analytics;
import org.glucosio.android.db.DatabaseHandler;
import org.glucosio.android.presenter.ExportPresenter;
import org.glucosio.android.presenter.MainPresenter;
import org.glucosio.android.tools.LocaleHelper;
import org.glucosio.android.view.ExportView;

import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ExportView {

    private static final String INTENT_EXTRA_DROPDOWN = "history_dropdown";
    private static final int REQUEST_INVITE = 1;
    private static final String INTENT_EXTRA_PAGER = "pager";

    private BottomSheetBehavior bottomSheetBehavior;
    private ExportPresenter exportPresenter;
    private RadioButton exportRangeButton;
    private HomePagerAdapter homePagerAdapter;
    private MainPresenter presenter;
    private ViewPager viewPager;
    private BottomSheetDialog bottomSheetAddDialog;
    private TextView exportDialogDateFrom;
    private TextView exportDialogDateTo;
    private View bottomSheetAddDialogView;
    private TabLayout tabLayout;
    private LocaleHelper localeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlucosioApplication application = (GlucosioApplication) getApplication();

        initPresenters(application);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        tabLayout = (TabLayout) findViewById(R.id.activity_main_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);

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
        tabLayout.addOnTabSelectedListener(
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
                    LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.activity_main_empty_layout);
                    ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);
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

        FloatingActionButton fabAddReading = (FloatingActionButton) findViewById(R.id.activity_main_fab_add_reading);
        fabAddReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetAddDialog.show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetAddDialog = new BottomSheetDialog(this);

        // Add Nav Drawer
        final PrimaryDrawerItem itemSettings = new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_settings_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemExport = new PrimaryDrawerItem().withName(R.string.sidebar_backup_export).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_backup_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemFeedback = new PrimaryDrawerItem().withName(R.string.menu_support).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_announcement_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemAbout = new PrimaryDrawerItem().withName(R.string.preferences_about_glucosio).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_info_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemInvite = new PrimaryDrawerItem().withName(R.string.action_invite).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_face_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemDonate = new PrimaryDrawerItem().withName(R.string.about_donate).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_favorite_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemA1C = new PrimaryDrawerItem().withName(R.string.activity_converter_title).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_calculator_a1c_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);
        final PrimaryDrawerItem itemReminders = new PrimaryDrawerItem().withName(R.string.activity_reminders_title).withIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_alarm_grey_24dp, null)).withSelectable(false).withTypeface(Typeface.DEFAULT_BOLD);

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
                        } else if (drawerItem.equals(itemInvite)) {
                            // Invite
                            showInviteDialog();
                        } else if (drawerItem.equals(itemExport)) {
                            // Export
                            startExportActivity();
                        } else if (drawerItem.equals(itemDonate)) {
                            // Donate
                            openDonateIntent();
                        } else if (drawerItem.equals(itemA1C)) {
                            openA1CCalculator();
                        } else if (drawerItem.equals(itemReminders)) {
                            openRemindersActivity();
                        }
                        return false;
                    }
                });

        if (isPlayServicesAvailable()) {
            drawerBuilder.addDrawerItems(
                    itemA1C,
                    itemReminders,
                    itemExport,
                    itemSettings,
                    itemFeedback,
                    itemAbout,
                    itemDonate,
                    itemInvite
            )
                    .withSelectedItem(-1)
                    .build();
        } else {
            drawerBuilder.addDrawerItems(
                    itemA1C,
                    itemReminders,
                    itemExport,
                    itemSettings,
                    itemFeedback,
                    itemAbout,
                    itemDonate
            )
                    .withSelectedItem(-1)
                    .build();
        }

        // Restore pager position
        Bundle b = getIntent().getExtras();
        if (b != null) {
            viewPager.setCurrentItem(b.getInt("pager"));
        }

        checkIfEmptyLayout();
        bottomSheetAddDialog.setContentView(bottomSheetAddDialogView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetAddDialogView.getParent());
        bottomSheetBehavior.setHideable(false);

        Analytics analytics = application.getAnalytics();
        Log.i("MainActivity", "Setting screen name: " + "main");
        analytics.reportScreen("Main Activity");
    }

    private void openRemindersActivity() {
        Intent intent = new Intent(this, RemindersActivity.class);
        startActivity(intent);
    }

    private void initPresenters(GlucosioApplication application) {
        final DatabaseHandler dbHandler = application.getDBHandler();
        localeHelper = new LocaleHelper();
        presenter = new MainPresenter(this, dbHandler);
        exportPresenter = new ExportPresenter(this, dbHandler);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onExportStarted(int numberOfItemsToExport) {
        showExportedSnackBar(numberOfItemsToExport); // TODO: 09/09/16 Instead of calling this method, move logic to this callback ?
        Log.d("Activity", "onExportStarted(): you might want to track this event");
    }

    @Override
    public void onNoItemsToExport() {
        showNoReadingsSnackBar(); // TODO: 09/09/16 Instead of calling this method, move logic to this callback ?
        Log.e("Activity", "onNoItemsToExport(): you might want to track this event");
    }

    @Override
    public void onExportFinish(Uri uri) {
        showShareDialog(uri); // TODO: 09/09/16 Instead of calling this method, move logic to this callback ?
        Log.e("Activity", "onExportFinish(): you might want to track this event");
    }

    @Override
    public void onExportError() {
        showExportError(); // TODO: 09/09/16 Instead of calling this method, move logic to this callback ?
        Log.e("Activity", "onExportError(): you might want to track this event");
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
        finishActivity();
    }

    public void finishActivity() {
        // dismiss dialog if still expanded
        bottomSheetAddDialog.dismiss();
        // then close activity
        finish();
    }

    public void onGlucoseFabClicked(View v) {
        openNewAddActivity(AddGlucoseActivity.class);
    }

    public void onKetoneFabClicked(View v) {
        openNewAddActivity(AddKetoneActivity.class);
    }

    public void onPressureFabClicked(View v) {
        openNewAddActivity(AddPressureActivity.class);
    }

    public void onHB1ACFabClicked(View v) {
        openNewAddActivity(AddA1CActivity.class);
    }

    public void onCholesterolFabClicked(View v) {
        openNewAddActivity(AddCholesterolActivity.class);
    }

    public void onWeightFabClicked(View v) {
        openNewAddActivity(AddWeightActivity.class);
    }

    private void openNewAddActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        // Pass pager position to open it again later
        Bundle b = new Bundle();
        b.putInt(INTENT_EXTRA_PAGER, viewPager.getCurrentItem());
        b.putInt(INTENT_EXTRA_DROPDOWN, homePagerAdapter.getHistoryFragment().getHistoryDropdownPosition());
        intent.putExtras(b);
        startActivity(intent);
        finishActivity();
    }

    public void openSupportDialog() {
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
                    if (checkPlayServices()) {
                        // TODO: Finish backup in next release
                        Intent intent = new Intent(getApplicationContext(), BackupActivity.class);
                        startActivity(intent);
                    } else {
                        dialog.dismiss();
                    }
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
        return !exportRangeButton.isChecked() || !(TextUtils.isEmpty(dateTo) || TextUtils.isEmpty(dateFrom));
    }

    public CoordinatorLayout getFabView() {
        return (CoordinatorLayout) findViewById(R.id.activity_main_coordinator_layout);
    }

    public void reloadFragmentAdapter() {
        homePagerAdapter.notifyDataSetChanged();
    }

    public void turnOffToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_main_appbar_layout);

        //turn off scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(0);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public void turnOnToolbarScrolling() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_main_appbar_layout);

        //turn on scrolling
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        mToolbar.setLayoutParams(toolbarLayoutParams);

        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    public Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.activity_main_toolbar);
    }

    public LocaleHelper getLocaleHelper() { return localeHelper; }

    private void hideFabAnimation() {
        final View fab = findViewById(R.id.activity_main_fab_add_reading);
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
        final View fab = findViewById(R.id.activity_main_fab_add_reading);
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

    public void showInviteDialog() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    public void checkIfEmptyLayout() {
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.activity_main_empty_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        if (presenter.isdbEmpty()) {
            pager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

            bottomSheetAddDialogView = getLayoutInflater().inflate(R.layout.fragment_add_bottom_dialog_disabled, null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (getResources().getConfiguration().orientation == 1) {
                    // If Portrait choose vertical curved line
                    ImageView arrow = (ImageView) findViewById(R.id.activity_main_arrow);
                    arrow.setBackground(getResources().getDrawable(R.drawable.curved_line_vertical));
                } else {
                    // Else choose horizontal one
                    ImageView arrow = (ImageView) findViewById(R.id.activity_main_arrow);
                    arrow.setBackground((getResources().getDrawable(R.drawable.curved_line_horizontal)));
                }
            }
        } else {
            pager.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            bottomSheetAddDialogView = getLayoutInflater().inflate(R.layout.fragment_add_bottom_dialog, null);
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

    public void showExportError() {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_issue_generic), Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBar(String text, int lengthLong) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, text, lengthLong).show();
    }

    public void showShareDialog(Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setData(uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_using)));
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

    private boolean isPlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d("STATUS", "Error connecting with Google Play services. Code: " + String.valueOf(status));
            return false;
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

        addA1cAnalyticsEvent();
    }

    private void addA1cAnalyticsEvent() {
        Analytics analytics = ((GlucosioApplication) getApplication()).getAnalytics();
        analytics.reportAction("A1C", "A1C disclaimer opened");
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("Glucosio", "This device is not supported.");
                showErrorDialogPlayServices();
            }
            return false;
        }
        return true;
    }

    private void showErrorDialogPlayServices() {
        Toast.makeText(getApplicationContext(), R.string.activity_main_error_play_services, Toast.LENGTH_SHORT).show();
    }
}
