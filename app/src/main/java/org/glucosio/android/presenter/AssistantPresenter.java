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

package org.glucosio.android.presenter;

import org.glucosio.android.fragment.AssistantFragment;

public class AssistantPresenter {
    private AssistantFragment fragment;

    public AssistantPresenter(AssistantFragment assistantFragment) {
        this.fragment = assistantFragment;
    }

    public void userAskedAddReading() {
        fragment.addReading();
    }

    public void userAskedExport() {
        fragment.startExportActivity();
    }

    public void userAskedA1CCalculator() {
        fragment.startA1CCalculatorActivity();
    }

    public void userSupportAsked() {
        fragment.openSupportDialog();
    }
}
