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

package org.glucosio.android.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.glucosio.android.R;

import java.util.List;

public class LabelledSpinner extends LinearLayout implements AdapterView.OnItemSelectedListener {

    /**
     * The label positioned above the Spinner, similar to the floating
     * label from a {@link android.support.design.widget.TextInputLayout}.
     */
    private TextView mLabel;

    /**
     * The Spinner component used in this layout.
     */
    private Spinner mSpinner;

    /**
     * A thin (1dp thick) divider line positioned below the Spinner,
     * similar to the bottom line in an {@link android.widget.EditText}.
     */
    private View mDivider;

    /**
     * The listener that receives notifications when an item in the
     * AdapterView is selected.
     */
    private OnItemChosenListener mOnItemChosenListener;

    /**
     * The main color used in the widget (the label color and divider
     * color). This may be updated when XML attributes are obtained and
     * again if the color is set programmatically.
     */
    private int mWidgetColor;


    public LabelledSpinner(Context context) {
        this(context, null);
    }

    public LabelledSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.LabelledSpinner,
                0,
                0);
        String labelText = typedArray.getString(R.styleable.LabelledSpinner_labelText);
        mWidgetColor = typedArray.getColor(R.styleable.LabelledSpinner_widgetColor,
                getResources().getColor(R.color.widget_labelled_spinner));
        typedArray.recycle();

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_labelled_spinner, this, true);

        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mLabel = (TextView) getChildAt(0);
        mLabel.setText(labelText);
        mLabel.setPadding(0, dpToPixels(16), 0, 0);
        mLabel.setTextColor(mWidgetColor);

        mSpinner = (Spinner) getChildAt(1);
        mSpinner.setPadding(0, dpToPixels(8), 0, dpToPixels(8));
        mSpinner.setOnItemSelectedListener(this);

        mDivider = getChildAt(2);
        MarginLayoutParams dividerParams =
                (MarginLayoutParams) mDivider.getLayoutParams();
        dividerParams.rightMargin = dpToPixels(4);
        dividerParams.bottomMargin = dpToPixels(8);
        mDivider.setLayoutParams(dividerParams);
        mDivider.setBackgroundColor(mWidgetColor);
        alignLabelWithSpinnerItem(4);
    }


    public TextView getLabel() {
        return mLabel;
    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    public View getDivider() {
        return mDivider;
    }


    /**
     * Sets the text the label is to display.
     *
     * @param labelText The CharSequence value to be displayed on the label.
     * @see #setLabelText(int)
     */
    public void setLabelText(CharSequence labelText) {
        mLabel.setText(labelText);
    }

    public CharSequence getLabelText() {
        return mLabel.getText();
    }

    /**
     * Sets the text the label is to display.
     *
     * @param labelTextId The string resource identifier which refers to
     *                    the string value which is to be displayed on
     *                    the label.
     * @see #setLabelText(CharSequence)
     */
    public void setLabelText(@StringRes int labelTextId) {
        mLabel.setText(getResources().getString(labelTextId));
    }

    public int getColor() {
        return mWidgetColor;
    }

    /**
     * Sets the color to use for the label text and the divider line
     * underneath.
     *
     * @param colorRes The color resource identifier which refers to the
     *                 color that is to be displayed on the widget.
     */
    public void setColor(@ColorRes int colorRes) {
        mLabel.setTextColor(getResources().getColor(colorRes));
        mDivider.setBackgroundColor(getResources().getColor(colorRes));
    }

    /**
     * Sets the array of items to be used in the Spinner.
     *
     * @param arrayResId The identifier of the array to use as the data
     *                   source (e.g. R.array.myArray)
     * @see #setItemsArray(List)
     * @see #setItemsArray(int, int, int)
     */
    public void setItemsArray(@ArrayRes int arrayResId) {
        setItemsArray(
                arrayResId,
                android.R.layout.simple_spinner_item,
                android.R.layout.simple_spinner_dropdown_item
        );
    }

    /**
     * Sets the array of items to be used in the Spinner.
     *
     * @param list The List used as the data source
     * @see #setItemsArray(int)
     * @see #setItemsArray(int, int, int)
     */
    public void setItemsArray(final List<?> list) {
        ArrayAdapter<?> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setAdapter(adapter);
    }

    private void setAdapter(ArrayAdapter<?> adapter) {
        //remove listener to avoid firing item selection
        mSpinner.setOnItemSelectedListener(null);

        mSpinner.setAdapter(adapter);
        //solution from http://stackoverflow.com/questions/2562248/how-to-keep-onitemselected-from-firing-off-on-a-newly-instantiated-spinner
        mSpinner.setSelection(0, false);
        //return back listener
        mSpinner.setOnItemSelectedListener(this);
    }

    /**
     * A private helper method to set the array of items to be used in the
     * Spinner.
     *
     * @param arrayResId      The identifier of the array to use as the data
     *                        source (e.g. R.array.myArray)
     * @param spinnerItemRes  The identifier of the layout used to create
     *                        views (e.g. R.layout.my_item)
     * @param dropdownViewRes The layout resource to create the drop down
     *                        views (e.g. R.layout.my_dropdown)
     * @see #setItemsArray(int)
     * @see #setItemsArray(List)
     */
    private void setItemsArray(@ArrayRes int arrayResId, @LayoutRes int spinnerItemRes, @LayoutRes int dropdownViewRes) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                arrayResId,
                spinnerItemRes);
        adapter.setDropDownViewResource(dropdownViewRes);

        setAdapter(adapter);
    }

    /**
     * Sets the Adapter used to provide the data for the Spinner.
     * This would be similar to setting an Adapter for a normal Spinner
     * component.
     *
     * @param adapter The Adapter which would provide data for the Spinner
     */
    public void setCustomAdapter(SpinnerAdapter adapter) {
        mSpinner.setAdapter(adapter);
    }

    /**
     * Sets the currently selected item.
     *
     * @param position Index (starting at 0) of the data item to be selected.
     */
    public void setSelection(int position) {
        mSpinner.setSelection(position);
    }

    public OnItemChosenListener getOnItemChosenListener() {
        return mOnItemChosenListener;
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been selected.
     * This would be similar to setting an OnItemSelectedListener for a normal
     * Spinner component.
     *
     * @param onItemChosenListener The callback that will run
     */
    public void setOnItemChosenListener(OnItemChosenListener onItemChosenListener) {
        mOnItemChosenListener = onItemChosenListener;
    }

    /**
     * Implemented method from {@link android.widget.AdapterView.OnItemSelectedListener}
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemChosenListener != null) {
            // 'this' refers to this LabelledSpinner component
            mOnItemChosenListener.onItemChosen(this, parent, view, position, id);
        }
    }

    /**
     * Implemented method from {@link android.widget.AdapterView.OnItemSelectedListener}
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (mOnItemChosenListener != null) {
            // 'this' refers to this LabelledSpinner component
            mOnItemChosenListener.onNothingChosen(this, parent);
        }
    }

    /**
     * Adds a 4dp left margin to the label and divider line underneath so that
     * it aligns with the Spinner item text. By default, the additional 4dp
     * margin will not be added.
     *
     * @param indentLabel Whether or not the label will be indented
     * @see #alignLabelWithSpinnerItem(int)
     * <p/>
     * Note: By default, however, a 4dp margin will be added so that the label
     * and divider align correctly with other UI components, such as the label
     * in a {@link android.support.design.widget.TextInputLayout}. This means
     * that if {@param indentLabel} is true, an 8dp left margin will be added
     * (this would be the 4dp margin to align with other UI components with
     * an additional 4dp margin to align the label with the Spinner item text.
     * Also note that if {@param indentLabel} is true, the label and divider
     * will not be aligned with other UI components as they would be 4dp
     * further right from them.
     */
    public void alignLabelWithSpinnerItem(boolean indentLabel) {
        if (indentLabel) {
            alignLabelWithSpinnerItem(8);
        } else {
            alignLabelWithSpinnerItem(4);
        }
    }

    /**
     * A helper method responsible for adding left margins to the label and
     * divider line underneath, used to align these to the start of the Spinner
     * item text.
     *
     * @param indentDps The density-independent pixel value for the left margin
     * @see #alignLabelWithSpinnerItem(boolean)
     */
    private void alignLabelWithSpinnerItem(int indentDps) {
        MarginLayoutParams labelParams =
                (MarginLayoutParams) mLabel.getLayoutParams();
        labelParams.leftMargin = dpToPixels(indentDps);
        mLabel.setLayoutParams(labelParams);

        MarginLayoutParams dividerParams =
                (MarginLayoutParams) mDivider.getLayoutParams();
        dividerParams.leftMargin = dpToPixels(indentDps);
        mDivider.setLayoutParams(dividerParams);
    }

    /**
     * A helper method responsible for the conversion of dp/dip (density-independent
     * pixel) values to pixels, so that they can be used when setting layout
     * parameters such as margins.
     *
     * @param dps The density-independent pixel value
     * @return The pixel value from the conversion
     */
    private int dpToPixels(int dps) {
        if (dps == 0) {
            return 0;
        }
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }


    /**
     * Interface definition for a callback to be invoked when an item in this
     * LabelledSpinner's Spinner view has been selected.
     */
    public interface OnItemChosenListener {

        /**
         * Callback method to be invoked when an item in this LabelledSpinner's
         * spinner view has been selected. This callback is invoked only when
         * the newly selected position is different from the previously selected
         * position or if there was no selected item.
         *
         * @param labelledSpinner The LabelledSpinner where the selection
         *                        happened. This view contains the AdapterView.
         * @param adapterView     The AdapterView where the selection happened. Note
         *                        that this AdapterView is part of the LabelledSpinner
         *                        component.
         * @param itemView        The view within the AdapterView that was clicked.
         * @param position        The position of the view in the adapter.
         * @param id              The row id of the item that is selected.
         */
        void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id);

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         *
         * @param labelledSpinner The LabelledSpinner view that contains the
         *                        AdapterView.
         * @param adapterView     The AdapterView that now contains no selected item.
         */
        void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView);
    }
}