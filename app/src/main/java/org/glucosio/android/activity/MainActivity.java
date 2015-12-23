package org.glucosio.android.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.glucosio.android.GlucosioApplication;
import org.glucosio.android.R;
import org.glucosio.android.adapter.HomePagerAdapter;
import org.glucosio.android.presenter.ExportPresenter;
import org.glucosio.android.presenter.MainPresenter;
import org.glucosio.android.tools.FormatDateTime;
import org.glucosio.android.tools.GlucoseConverter;
import org.glucosio.android.tools.LabelledSpinner;
import org.glucosio.android.tools.LabelledSpinner.OnItemChosenListener;

import java.text.DecimalFormat;
import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    ExportPresenter exportPresenter;
    private LabelledSpinner spinnerReadingType;
    private Dialog addDialog;
    private TextView dialogCancelButton;
    private TextView dialogAddButton;
    private TextView dialogAddTime;
    private TextView dialogAddDate;
    private TextView dialogReading;
    private TextView exportDialogDateFrom;
    private TextView exportDialogDateTo;
    private RadioButton exportRangeButton;
    private EditText dialogTypeCustom;
    private HomePagerAdapter homePagerAdapter;
    private boolean isCustomType;
    private MainPresenter presenter;

    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabCholestorol;
    private FloatingActionButton fabPressure;
    private FloatingActionButton fabWeight;
    private FloatingActionButton fabKetones;
    private FloatingActionButton fabHbA1c;
    private FloatingActionButton fabGlucose;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);
        exportPresenter = new ExportPresenter(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

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

        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_add_reading);

        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened){
                    AlphaAnimation alpha = new AlphaAnimation(1F, 0.8F);
                    alpha.setDuration(100);
                    alpha.setFillAfter(true);
                    viewPager.startAnimation(alpha);
                } else {
                    AlphaAnimation alpha = new AlphaAnimation(0.8F, 1F);
                    alpha.setDuration(100);
                    alpha.setFillAfter(true);
                    viewPager.startAnimation(alpha);                }
            }
        });

        // Add Nav Drawer
        final PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(R.drawable.ic_settings_black_24dp).withSelectable(false);
        final PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(R.string.title_activity_export).withIcon(R.drawable.ic_share_black_24dp).withSelectable(false);
        final PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName(R.string.preferences_about_glucosio).withIcon(R.drawable.ic_info_black_24dp).withSelectable(false);
        final PrimaryDrawerItem item4 = new PrimaryDrawerItem().withName(R.string.action_feedback).withIcon(R.drawable.ic_feedback_black_24dp).withSelectable(false);
        final PrimaryDrawerItem item5 = new PrimaryDrawerItem().withName(R.string.action_invite).withIcon(R.drawable.ic_face_black_24dp).withSelectable(false);
        final PrimaryDrawerItem item6 = new PrimaryDrawerItem().withName(R.string.about_donate).withIcon(R.drawable.ic_favorite_black_24dp).withSelectable(false);


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
                        if (drawerItem.equals(item1)) {
                            // Settings
                            openPreferences();
                        } else if (drawerItem.equals(item3)) {
                            // About
                            startAboutActivity();
                        } else if (drawerItem.equals(item4)) {
                            // Feedback
                            startGittyReporter();
                        } else if (drawerItem.equals(item5)) {
                            // Invite
                            showInviteDialog();
                        } else if (drawerItem.equals(item2)) {
                            // Export
                            startExportActivity();
                        } else if (drawerItem.equals(item6)) {
                            // Donate
                            openDonateIntent();
                        }
                        return false;
                    }
                });

        if (isPlayServicesAvailable()) {
            drawerBuilder.addDrawerItems(
                    item1,
                    item2,
                    item3,
                    item4,
                    item6
            )
                    .withSelectedItem(-1)
                    .build();
        } else {
            drawerBuilder.addDrawerItems(
                    item1,
                    item2,
                    item3,
                    item6
            )
                    .withSelectedItem(-1)
                    .build();
        }

        checkIfEmptyLayout();

        // Obtain the Analytics shared Tracker instance.
        GlucosioApplication application = (GlucosioApplication) getApplication();
        mTracker = application.getDefaultTracker();
        Log.i("MainActivity", "Setting screen name: " + "main");
        mTracker.setScreenName("Main Activity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void openDonateIntent() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.glucosio.org/donate/"));
        startActivity(browserIntent);
    }

    public void startExportActivity() {
        showExportDialog();
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

    public void startGittyReporter() {
        Intent intent = new Intent(this, GittyActivity.class);
        startActivity(intent);
    }

    public void openPreferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
    }

    public void onGlucoseFabClicked(View v) {
        showAddDialog();
    }

    public void showAddDialog() {
        addDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addDialog.getWindow().getAttributes());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        addDialog.setContentView(R.layout.dialog_add);
        addDialog.getWindow().setAttributes(lp);
        addDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addDialog.getWindow().setDimAmount(0.5f);
        addDialog.show();

        spinnerReadingType = (LabelledSpinner) addDialog.findViewById(R.id.dialog_add_reading_type);
        spinnerReadingType.setItemsArray(R.array.dialog_add_measured_list);

        dialogCancelButton = (TextView) addDialog.findViewById(R.id.dialog_add_cancel);
        dialogAddButton = (TextView) addDialog.findViewById(R.id.dialog_add_add);
        dialogAddTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        dialogAddDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);
        dialogTypeCustom = (EditText) addDialog.findViewById(R.id.dialog_type_custom);

        presenter.updateSpinnerTypeTime();
        this.isCustomType = false;

        spinnerReadingType.setOnItemChosenListener(new OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == 9) {
                    dialogTypeCustom.setVisibility(View.VISIBLE);
                    isCustomType = true;
                } else {
                    if (dialogTypeCustom.getVisibility() == View.VISIBLE) {
                        dialogTypeCustom.setVisibility(View.GONE);
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        dialogAddDate.setText(presenter.getReadingDay() + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear());
        dialogAddTime.setText(formatDateTime.getCurrentTime());
        dialogAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setMaxDate(now);
            }
        });

        dialogAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (android.text.format.DateFormat.is24HourFormat(getApplicationContext())) {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                } else {
                    TimePickerDialog tpd = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                }
            }
        });
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
        dialogAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOnAddButtonPressed();
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("GlucoseDialog")
                        .setAction("Add")
                        .build());
            }
        });

        TextView unitM = (TextView) addDialog.findViewById(R.id.dialog_add_unit_measurement);

        if (presenter.getUnitMeasuerement().equals("mg/dL")) {
            unitM.setText("mg/dL");
        } else {
            unitM.setText("mmol/L");
        }

        // Workaround for ActionBarContextView bug.
        android.view.ActionMode.Callback workaroundCallback = new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }
        };

        dialogReading.setCustomSelectionActionModeCallback(workaroundCallback);

        dialogTypeCustom.setCustomSelectionActionModeCallback(workaroundCallback);
    }

    public void showEditDialog(final int id) {
        //only included for debug
        // printGlucoseReadingTableDetails();
        GlucoseConverter converter = new GlucoseConverter();

        final int readingId = id;
        addDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addDialog.getWindow().getAttributes());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        addDialog.setContentView(R.layout.dialog_add);
        addDialog.getWindow().setAttributes(lp);
        addDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addDialog.getWindow().setDimAmount(0.5f);
        addDialog.show();

        spinnerReadingType = (LabelledSpinner) addDialog.findViewById(R.id.dialog_add_reading_type);
        spinnerReadingType.setItemsArray(R.array.dialog_add_measured_list);

        dialogCancelButton = (TextView) addDialog.findViewById(R.id.dialog_add_cancel);
        dialogAddButton = (TextView) addDialog.findViewById(R.id.dialog_add_add);
        dialogAddTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        dialogAddDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);
        dialogAddButton.setText(getString(R.string.dialog_edit).toUpperCase());

        if (presenter.getUnitMeasuerement().equals("mg/dL")) {
            dialogReading.setText(presenter.getGlucoseReadingReadingById(readingId));
        } else {
            dialogReading.setText(converter.toMmolL(Double.parseDouble(presenter.getGlucoseReadingReadingById(readingId))) + "");
        }

        dialogReading = (TextView) addDialog.findViewById(R.id.dialog_add_concentration);
        dialogTypeCustom = (EditText) addDialog.findViewById(R.id.dialog_type_custom);

        presenter.updateSpinnerTypeTime();
        this.isCustomType = false;

        spinnerReadingType.setSelection(typeStringToInt(presenter.getGlucoseReadingTypeById(readingId)));

        spinnerReadingType.setOnItemChosenListener(new OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                // If other is selected
                if (position == 9) {
                    dialogTypeCustom.setVisibility(View.VISIBLE);
                    dialogTypeCustom.setText(presenter.getGlucoseReadingTypeById(readingId));
                    isCustomType = true;
                } else {
                    if (dialogTypeCustom.getVisibility() == View.VISIBLE) {
                        dialogTypeCustom.setVisibility(View.GONE);
                        dialogTypeCustom.setText("");
                        isCustomType = false;
                    }
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        presenter.getGlucoseReadingTimeById(id);

        dialogAddTime.setText(presenter.getReadingHour() + ":" + presenter.getReadingMinute());
        dialogAddDate.setText(presenter.getReadingDay() + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear());

        dialogAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setMaxDate(now);
            }
        });

        dialogAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(MainActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
                tpd.show(getFragmentManager(), "Timepickerdialog");
                spinnerReadingType.setSelection(presenter.timeToSpinnerType());
            }
        });
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
        dialogAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOnEditButtonPressed(id);
            }
        });

        TextView unitM = (TextView) addDialog.findViewById(R.id.dialog_add_unit_measurement);
        if (presenter.getUnitMeasuerement().equals("mg/dl")) {
            unitM.setText("mg/dl");
        } else {
            unitM.setText("mmol/L");
        }

        // Workaround for ActionBarContextView bug.
        android.view.ActionMode.Callback workaroundCallback = new android.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }
        };

        dialogReading.setCustomSelectionActionModeCallback(workaroundCallback);

        dialogTypeCustom.setCustomSelectionActionModeCallback(workaroundCallback);
    }

    public void showExportDialog() {
        final Dialog exportDialog = new Dialog(MainActivity.this, R.style.GlucosioTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(exportDialog.getWindow().getAttributes());
        exportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                if (validateExportDialog()){
                    exportPresenter.onExportClicked(exportAllButton.isChecked());
                    exportDialog.dismiss();
                } else {
                    showSnackBar(getResources().getString(R.string.dialog_error));
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

    private void dialogOnAddButtonPressed() {
        if (isCustomType) {
            presenter.dialogOnAddButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    dialogTypeCustom.getText().toString());
        } else {
            presenter.dialogOnAddButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    spinnerReadingType.getSpinner().getSelectedItem().toString());
        }
    }

    public void dismissAddDialog() {
        addDialog.dismiss();
        homePagerAdapter.notifyDataSetChanged();
        checkIfEmptyLayout();
    }

    public void showErrorMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.dialog_error2), Toast.LENGTH_SHORT).show();
    }

    private void dialogOnEditButtonPressed(int id) {
        if (isCustomType) {
            presenter.dialogOnEditButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    dialogTypeCustom.getText().toString(), id);
        } else {
            presenter.dialogOnEditButtonPressed(dialogAddTime.getText().toString(),
                    dialogAddDate.getText().toString(), dialogReading.getText().toString(),
                    spinnerReadingType.getSpinner().getSelectedItem().toString(), id);
        }
    }

    public void updateSpinnerTypeTime(int selection) {
        spinnerReadingType.setSelection(selection);
    }

    private void updateSpinnerTypeHour(int hour) {
        spinnerReadingType.setSelection(presenter.hourToSpinnerType(hour));
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

    public void showInviteDialog() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, 0);
    }

    public void checkIfEmptyLayout() {
        LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.mainactivity_empty_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        if (presenter.isdbEmpty()) {
            pager.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

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
            emptyLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void showExportedSnackBar(int nReadings) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_snackbar_1) + " " + nReadings + " " + getString(R.string.activity_export_snackbar_2), Snackbar.LENGTH_SHORT).show();
    }

    public void showNoReadingsSnackBar(){
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, getString(R.string.activity_export_no_readings_snackbar), Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBar(String text) {
        View rootLayout = findViewById(android.R.id.content);
        Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT).show();
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


    public int typeStringToInt(String typeString) {
        //TODO refactor this ugly mess
        int typeInt;
        if (typeString.equals(getString(R.string.dialog_add_type_1))) {
            typeInt = 0;
        } else if (typeString.equals(getString(R.string.dialog_add_type_2))) {
            typeInt = 1;
        } else if (typeString.equals(getString(R.string.dialog_add_type_3))) {
            typeInt = 2;
        } else if (typeString.equals(getString(R.string.dialog_add_type_4))) {
            typeInt = 3;
        } else if (typeString.equals(getString(R.string.dialog_add_type_5))) {
            typeInt = 4;
        } else if (typeString.equals(getString(R.string.dialog_add_type_6))) {
            typeInt = 5;
        } else if (typeString.equals(getString(R.string.dialog_add_type_7))) {
            typeInt = 6;
        } else if (typeString.equals(getString(R.string.dialog_add_type_8))) {
            typeInt = 7;
        } else if (typeString.equals(getString(R.string.dialog_add_type_9))) {
            typeInt = 8;
        } else {
            typeInt = 9;
        }

        return typeInt;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        TextView addTime = (TextView) addDialog.findViewById(R.id.dialog_add_time);
        DecimalFormat df = new DecimalFormat("00");

        presenter.setReadingHour(df.format(hourOfDay));
        presenter.setReadingMinute(df.format(minute));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        FormatDateTime formatDateTime = new FormatDateTime(getApplicationContext());
        addTime.setText(formatDateTime.getTime(cal));
        updateSpinnerTypeHour(Integer.parseInt(df.format(hourOfDay)));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Check which dialog set the date
        if (view.getTag().equals("fromDateDialog")) {
            DecimalFormat df = new DecimalFormat("00");

            exportPresenter.setFromYear(year);
            exportPresenter.setFromMonth(monthOfYear);
            exportPresenter.setFromDay(dayOfMonth);

            int monthToShow = monthOfYear + 1;
            String date = +dayOfMonth + "/" + monthToShow + "/" + year;
            exportDialogDateFrom.setText(date);
        } else if (view.getTag().equals("toDateDialog")) {
            DecimalFormat df = new DecimalFormat("00");

            exportPresenter.setToYear(year);
            exportPresenter.setToMonth(monthOfYear);
            exportPresenter.setToDay(dayOfMonth);

            int monthToShow = monthOfYear + 1;
            String date = +dayOfMonth + "/" + monthToShow + "/" + year;
            exportDialogDateTo.setText(date);
        } else {
            TextView addDate = (TextView) addDialog.findViewById(R.id.dialog_add_date);
            DecimalFormat df = new DecimalFormat("00");

            presenter.setReadingYear(year + "");
            presenter.setReadingMonth(df.format(monthOfYear + 1));
            presenter.setReadingDay(df.format(dayOfMonth));

            String date = +dayOfMonth + "/" + presenter.getReadingMonth() + "/" + presenter.getReadingYear();
            addDate.setText(date);
        }
    }

    private boolean isPlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d("STATUS", "Error connecting with Google Play services. Code: " + String.valueOf(status));
            return false;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
