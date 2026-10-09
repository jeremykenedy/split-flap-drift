package com.jeremykenedy.splitflapdrift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public final class SplitFlapSettingsCoverageTest {
    private SplitFlapPreferences preferences;
    private SplitFlapSettingsProvider provider;

    @Before
    public void setUp() {
        RuntimeEnvironment.getApplication()
                .getSharedPreferences(SplitFlapPreferences.FILE, 0)
                .edit()
                .clear()
                .commit();
        preferences = new SplitFlapPreferences(RuntimeEnvironment.getApplication());
        provider = Robolectric.buildContentProvider(SplitFlapSettingsProvider.class).create().get();
    }

    @Test
    public void preferencesValidatePersistAndRandomizeEachSupportedField() {
        SplitFlapOptions defaults = preferences.read();
        assertEquals(0, defaults.format);
        preferences.set("format", "24 hour");
        preferences.set("palette", "Ocean");
        preferences.set("flipSpeed", "5");
        preferences.set("date", "on");
        preferences.set("seconds", "false");
        preferences.set("seconds", "off");
        preferences.set("lighting", "Night");
        preferences.set("scale", "10");
        preferences.set("brightness", "2");
        SplitFlapOptions saved = preferences.read();
        assertEquals(1, saved.format);
        assertEquals(2, saved.palette);
        assertEquals(5, saved.flipSpeed);
        assertTrue(saved.date);
        assertEquals(false, saved.seconds);
        assertEquals(2, saved.lighting);
        assertEquals(5, saved.scale);
        assertEquals(2, saved.brightness);
        assertEquals(0, saved.randomMask);

        preferences.set("format", "random");
        preferences.set("palette", "random");
        preferences.set("flipSpeed", "random");
        preferences.set("date", "random");
        preferences.set("seconds", "random");
        preferences.set("lighting", "random");
        preferences.set("scale", "random");
        preferences.set("brightness", "random");
        assertEquals(255, preferences.read().randomMask);
        preferences.set("date", "true");
        assertEquals(247, preferences.read().randomMask);

        expectInvalid(() -> preferences.set("unknown", "1"));
        expectInvalid(() -> preferences.set("format", "digital"));
        expectInvalid(() -> preferences.set("flipSpeed", "fast"));
        expectInvalid(() -> preferences.set("date", "maybe"));
    }

    @Test
    public void providerPublishesSchemaAndSettingsMutations() {
        assertTrue(provider.onCreate());
        assertNull(
                provider.query(
                        Uri.parse("content://" + SplitFlapSettingsProvider.AUTHORITY),
                        null,
                        null,
                        null,
                        null));
        assertEquals(
                "application/json",
                provider.getType(Uri.parse("content://" + SplitFlapSettingsProvider.AUTHORITY)));
        String schema = json("get_schema", null, null);
        assertTrue(schema.contains("\"key\":\"format\""));
        assertTrue(schema.contains("\"key\":\"brightness\""));
        assertTrue(json("get_settings", null, null).contains("\"palette\":\"Midnight\""));

        assertTrue(json("set_random_all", null, null).contains("\"brightness\":\"Random\""));
        assertTrue(json("set_random_none", null, null).contains("\"format\":\"12 hour\""));
        Bundle extras = new Bundle();
        extras.putString("value", "Ivory");
        assertTrue(json("set_setting", "palette", extras).contains("\"palette\":\"Ivory\""));
        expectInvalid(() -> provider.call("set_setting", "palette", null));
        expectInvalid(() -> provider.call("set_setting", "palette", new Bundle()));
        expectInvalid(() -> provider.call("set_setting", "unknown", extras));
        expectInvalid(() -> provider.call("unsupported", null, null));
    }

    @Test
    public void providerRejectsUnsupportedMutationMethods() {
        Uri uri = Uri.parse("content://" + SplitFlapSettingsProvider.AUTHORITY);
        try {
            provider.insert(uri, new ContentValues());
            fail("insert must be rejected");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected.getMessage());
        }
        try {
            provider.update(uri, new ContentValues(), null, null);
            fail("update must be rejected");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected.getMessage());
        }
        try {
            provider.delete(uri, null, null);
            fail("delete must be rejected");
        } catch (UnsupportedOperationException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private String json(String method, String arg, Bundle extras) {
        return provider.call(method, arg, extras).getString("json");
    }

    private static void expectInvalid(Runnable operation) {
        try {
            operation.run();
            fail("Expected invalid input to be rejected");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}
