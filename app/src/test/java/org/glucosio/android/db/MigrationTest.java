package org.glucosio.android.db;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MigrationTest {
    private Migration migration = new Migration();
    private DynamicRealm realmMock = mock(DynamicRealm.class);
    private DynamicRealmObject objectMock = mock(DynamicRealmObject.class);
    private RealmSchema schemaMock = mock(RealmSchema.class);
    private RealmObjectSchema objectSchemaMock = mock(RealmObjectSchema.class);
    private ArgumentCaptor<RealmObjectSchema.Function> functionCaptor = ArgumentCaptor.forClass(RealmObjectSchema.Function.class);
    private InOrder inOrder = inOrder(schemaMock, objectSchemaMock);


    @Before
    public void setUp() {
        when(realmMock.getSchema()).thenReturn(schemaMock);
        when(schemaMock.get(anyString())).thenReturn(objectSchemaMock);
        when(schemaMock.create(anyString())).thenReturn(objectSchemaMock);
        when(objectSchemaMock.addField(anyString(), any(Class.class))).thenReturn(objectSchemaMock);
        when(objectSchemaMock.addField(anyString(), any(Class.class), eq(FieldAttribute.REQUIRED)))
                .thenReturn(objectSchemaMock);
        when(objectSchemaMock.addField(anyString(), any(Class.class), eq(FieldAttribute.PRIMARY_KEY), eq(FieldAttribute.REQUIRED)))
                .thenReturn(objectSchemaMock);
        when(objectSchemaMock.transform(any(RealmObjectSchema.Function.class))).thenReturn(objectSchemaMock);
        when(objectSchemaMock.removeField(anyString())).thenReturn(objectSchemaMock);
        when(objectSchemaMock.renameField(anyString(), anyString())).thenReturn(objectSchemaMock);
    }

    @Test
    public void ProperlyMigrateWeightFromVersion4() {
        when(objectMock.getInt("reading")).thenReturn(8);

        migration.migrate(realmMock, 4, 5);

        inOrder.verify(schemaMock).get("WeightReading");
        checkObjectIntMigration("reading", 8);
    }

    private void checkObjectIntMigration(String fieldName, int oldValue) {
        String tempFieldName = fieldName + "_tmp";
        inOrder.verify(objectSchemaMock).addField(tempFieldName, Double.class, FieldAttribute.REQUIRED);
        inOrder.verify(objectSchemaMock).transform(functionCaptor.capture());
        inOrder.verify(objectSchemaMock).removeField(fieldName);
        inOrder.verify(objectSchemaMock).renameField(tempFieldName, fieldName);

        functionCaptor.getValue().apply(objectMock);
        verify(objectMock).setDouble(tempFieldName, oldValue);
    }

    @Test
    public void ProperlyMigratePressureFromVersion4() {
        when(objectMock.getInt("minReading")).thenReturn(8);
        when(objectMock.getInt("maxReading")).thenReturn(4);

        migration.migrate(realmMock, 4, 5);

        inOrder.verify(schemaMock).get("PressureReading");
        checkObjectIntMigration("minReading", 8);
        checkObjectIntMigration("maxReading", 4);
    }

    @Test
    public void ProperlyMigrateGlucoseFromVersion4() {
        when(objectMock.getInt("reading")).thenReturn(8);

        migration.migrate(realmMock, 4, 5);

        inOrder.verify(schemaMock).get("GlucoseReading");
        checkObjectIntMigration("reading", 8);
    }

    @Test
    public void ProperlyMigrateUserFromVersion4() {
        when(objectMock.getInt("custom_range_min")).thenReturn(8);
        when(objectMock.getInt("custom_range_max")).thenReturn(4);

        migration.migrate(realmMock, 4, 5);

        inOrder.verify(schemaMock).get("User");
        checkObjectIntMigration("custom_range_min", 8);
        checkObjectIntMigration("custom_range_max", 4);
    }

    @Test
    public void ProperlyMigrateCholesterolFromVersion4() {
        when(objectMock.getInt("totalReading")).thenReturn(8);
        when(objectMock.getInt("LDLReading")).thenReturn(4);
        when(objectMock.getInt("HDLReading")).thenReturn(9);

        migration.migrate(realmMock, 4, 5);

        inOrder.verify(schemaMock).get("CholesterolReading");
        checkObjectIntMigration("totalReading", 8);
        checkObjectIntMigration("LDLReading", 4);
        checkObjectIntMigration("HDLReading", 9);
    }
}
