package org.odk.collect.android.utilities;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.odk.collect.android.utilities.ArrayUtils.toObject;
import static org.odk.collect.android.utilities.ArrayUtils.toPrimitive;

public class ArrayUtilsTest {

    @Test
    public void toPrimitiveCreatesPrimitiveLongArray() {
        assertArrayEquals(new long[] {1, 2, 3, 4, 5}, toPrimitive(new Long[] {1L, 2L, 3L, 4L, 5L}));
    }

    @Test
    public void nullToPrimitiveCreatesEmptyPrimitiveLongArray() {
        assertArrayEquals(new long[0], toPrimitive(null));
    }

    @Test(expected = NullPointerException.class)
    public void arrayContainingNullCausesNpe() {
        toPrimitive(new Long[] {1L, null, 3L, 4L, 5L});
    }

    @Test(expected = NullPointerException.class)
    public void arrayStartingWithNullCausesNpe() {
        toPrimitive(new Long[] {null, 3L, 4L, 5L});
    }

    @Test(expected = NullPointerException.class)
    public void arrayEndingWithNullCausesNpe() {
        toPrimitive(new Long[] {1L, 3L, 4L, null});
    }

    @Test
    public void toObjectCreatesLongArray() {
        assertArrayEquals(new Long[] {1L, 2L, 3L, 4L, 5L}, toObject(new long[] {1, 2, 3, 4, 5}));
    }

    @Test
    public void nullBecomesEmptyLongArray() {
        assertArrayEquals(new Long[0], toObject(null));
    }
}
