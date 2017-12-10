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

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

public class CholesterolReading extends RealmObject {
    @PrimaryKey
    private long id;

    private double totalReading;
    private double LDLReading;
    private double HDLReading;
    private Date created;

    public CholesterolReading() {
    }

    public CholesterolReading(double totalReading, double LDLReading, double HDLReading, Date created) {
        // mg/dL
        // 0-200
        this.totalReading = totalReading;
        this.LDLReading = LDLReading;
        this.HDLReading = HDLReading;
        this.created = created;
    }

    public double getTotalReading() {
        return totalReading;
    }

    public void setTotalReading(double totalReading) {
        this.totalReading = totalReading;
    }

    public double getLDLReading() {
        return LDLReading;
    }

    public void setLDLReading(double LDLReading) {
        this.LDLReading = LDLReading;
    }

    public double getHDLReading() {
        return HDLReading;
    }

    public void setHDLReading(double HDLReading) {
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
