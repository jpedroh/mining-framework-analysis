package io.yawp.repository.hooks.basic;

import static org.junit.Assert.assertEquals;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.HookedObject;

import java.util.List;

import io.yawp.repository.models.basic.ShieldedObject;
import org.junit.Test;

public class HookTest extends EndpointTestCase {

    @Test
    public void testBeforeSave() {
        HookedObject object = new HookedObject("before_save");

        yawp.save(object);
        assertEquals("xpto before save", object.getStringValue());

        HookedObject retrievedObject = object.getId().fetch();
        assertEquals("xpto before save", retrievedObject.getStringValue());
    }

    @Test
    public void testAfterSave() {
        HookedObject object = new HookedObject("after_save");
        yawp.save(object);
        assertEquals("xpto after save", object.getStringValue());
    }

    public static class LoggedUser {
        public static String filter;
    }

    @Test
    public void testBeforeQuery() {
        try {
            LoggedUser.filter = "xpto1";
            yawp.save(new HookedObject("xpto1"));
            yawp.save(new HookedObject("xpto2"));

            List<HookedObject> objects = yawp(HookedObject.class).list();

            assertEquals(1, objects.size());
            assertEquals("xpto1", objects.get(0).getStringValue());
        } finally {
            LoggedUser.filter = null;
        }
    }

    @Test
    public void testBeforeDestroy() {
        HookedObject objectToDelete = new HookedObject("beforeDestroy test");
        yawp.save(objectToDelete);

        yawp.destroy(objectToDelete.getId());

        List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "beforeDestroy test: " + objectToDelete.getId())
                .list();
        assertEquals(1, objects.size());
    }

    @Test
    public void testAfterDestroy() {
        HookedObject objectToDelete = new HookedObject();
        yawp.save(objectToDelete);

        yawp.destroy(objectToDelete.getId());

        List<BasicObject> objects = yawp(BasicObject.class).where("stringValue", "=", "afterDestroy test: " + objectToDelete.getId())
                .list();
        assertEquals(1, objects.size());
    }
}
