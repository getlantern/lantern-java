package org.lantern.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class FamilyShieldTest {
    @Test
    public void testBlocked() {
        assertTrue(FamilyShield.isBlocked("www.playboy.com"));
        assertFalse(FamilyShield.isBlocked("www.facebook.com"));
    }
}
