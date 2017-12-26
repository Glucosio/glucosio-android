package org.glucosio.android.db;

import org.glucosio.android.Constants;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserBuilderTest {
    @Test
    public void SetsId_WhenProvided() {
        int id = 100;

        User user = new UserBuilder().setId(id).createUser();

        assertThat(user.getId()).isEqualTo(id);
    }

    @Test
    public void SetsName_WhenAsked() {
        String name = "Heig";

        User user = new UserBuilder().setName(name).createUser();

        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    public void SetsAge_WhenProvided() {
        int age = 32;

        User user = new UserBuilder().setAge(age).createUser();

        assertThat(user.getAge()).isEqualTo(age);
    }

    @Test
    public void SetsCountry_WhenAsked() {
        String country = "NL";

        User user = new UserBuilder().setCountry(country).createUser();

        assertThat(user.getCountry()).isEqualTo(country);
    }

    @Test
    public void SetsPreferredLanguage_WhenAsked() {
        String language = "Dutch";

        User user = new UserBuilder().setPreferredLanguage(language).createUser();

        assertThat(user.getPreferred_language()).isEqualTo(language);
    }

    @Test
    public void SetsGender_WhenAsked() {
        String gender = "M";

        User user = new UserBuilder().setGender(gender).createUser();

        assertThat(user.getGender()).isEqualTo(gender);
    }

    @Test
    public void SetsDiabetesType_WhenProvided() {
        int type = 2;

        User user = new UserBuilder().setDiabetesType(type).createUser();

        assertThat(user.getD_type()).isEqualTo(type);
    }

    @Test
    public void SetsPreferredUnit_WhenAsked() {
        String unit = Constants.Units.MG_DL;

        User user = new UserBuilder().setPreferredUnit(unit).createUser();

        assertThat(user.getPreferred_unit()).isEqualTo(unit);
    }

    @Test
    public void SetsPreferredUnitA1C_WhenAsked() {
        String unit = Constants.Units.MG_DL;

        User user = new UserBuilder().setPreferredA1CUnit(unit).createUser();

        assertThat(user.getPreferred_unit_a1c()).isEqualTo(unit);
    }

    @Test
    public void SetsPreferredWeightUnit_WhenAsked() {
        String unit = "kg";

        User user = new UserBuilder().setPreferredWeightUnit(unit).createUser();

        assertThat(user.getPreferred_unit_weight()).isEqualTo(unit);
    }

    @Test
    public void SetsPreferredRange_WhenAsked() {
        String range = "range";

        User user = new UserBuilder().setPreferredRange(range).createUser();

        assertThat(user.getPreferred_range()).isEqualTo(range);
    }

    @Test
    public void SetsCustomRangeMax_WhenAsked() {
        double rangeValue = 0.45;

        User user = new UserBuilder().setMaxRange(rangeValue).createUser();

        assertThat(user.getCustom_range_max()).isEqualTo(rangeValue);
    }

    @Test
    public void SetsCustomRangeMin_WhenAsked() {
        double rangeValue = 0.98;

        User user = new UserBuilder().setMinRange(rangeValue).createUser();

        assertThat(user.getCustom_range_min()).isEqualTo(rangeValue);
    }
}
