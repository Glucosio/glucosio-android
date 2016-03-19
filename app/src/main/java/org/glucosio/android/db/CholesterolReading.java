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

package org.glucosio.android.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CholesterolReading extends RealmObject {
    @PrimaryKey
    private long id;

    private int totalReading;
    private int LDLReading;
    private int HDLReading;
    private Date created;

    public CholesterolReading() {
    }

    public CholesterolReading(int totalReading, int LDLReading, int HDLReading, Date created) {
        // mg/dL
        // 0-200
        this.totalReading = totalReading;
        this.LDLReading = LDLReading;
        this.HDLReading = HDLReading;
        this.created = created;
    }

    public int getTotalReading() {
        return totalReading;
    }

    public void setTotalReading(int totalReading) {
        this.totalReading = totalReading;
    }

    public int getLDLReading() {
        return LDLReading;
    }

    public void setLDLReading(int LDLReading) {
        this.LDLReading = LDLReading;
    }

    public int getHDLReading() {
        return HDLReading;
    }

    public void setHDLReading(int HDLReading) {
        this.HDLReading = HDLReading;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
