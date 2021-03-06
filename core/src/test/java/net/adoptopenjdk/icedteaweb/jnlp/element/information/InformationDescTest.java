/* InformationDescTest.java
   Copyright (C) 2013 Red Hat, Inc.

This file is part of IcedTea.

IcedTea is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation, version 2.

IcedTea is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
IcedTea; see the file COPYING. If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is making a
combined work based on this library. Thus, the terms and conditions of the GNU
General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent modules, and
to copy and distribute the resulting executable under terms of your choice,
provided that you also meet, for each linked independent module, the terms and
conditions of the license of that module. An independent module is a module
which is not derived from or based on this library. If you modify this library,
you may extend this exception to your version of the library, but you are not
obligated to do so. If you do not wish to do so, delete this exception
statement from your version. */

package net.adoptopenjdk.icedteaweb.jnlp.element.information;

import net.adoptopenjdk.icedteaweb.xmlparser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static net.adoptopenjdk.icedteaweb.jnlp.element.information.DescriptionKind.DEFAULT;
import static net.adoptopenjdk.icedteaweb.jnlp.element.information.DescriptionKind.ONE_LINE;
import static net.adoptopenjdk.icedteaweb.jnlp.element.information.DescriptionKind.SHORT;
import static net.adoptopenjdk.icedteaweb.jnlp.element.information.DescriptionKind.TOOLTIP;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InformationDescTest {

    @Test
    public void testLocales() {
        InformationDesc info;

        info = new InformationDesc(new Locale[0]);
        assertArrayEquals(new Locale[0], info.getLocales());

        Locale[] someLocales = new Locale[] { Locale.ENGLISH, Locale.FRENCH };
        info = new InformationDesc(someLocales);
        assertArrayEquals(someLocales, info.getLocales());
    }

    @Test
    public void testOsAndArch() {
        InformationDesc info;

        info = new InformationDesc(new Locale[0], "Win10", "i386");
        assertEquals("Win10", info.getOs());
        assertEquals("i386", info.getArch());
    }

    @Test
    public void testTitle() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("title", "A Title");
        assertEquals("A Title", info.getTitle());
    }

    @Test
    public void testVendor() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("vendor", "Some Vendor");
        assertEquals("Some Vendor", info.getVendor());
    }

    @Test
    public void testHomePage() throws MalformedURLException {
        URL url = new URL("http://some.home.page.example.com");
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("homepage", url);
        assertEquals(url, info.getHomepage());
    }
    @Test
    public void testDescription() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("description-" + DEFAULT.getValue(), "Default Description");
        assertEquals("Default Description", info.getDescription());
    }

    @Test
    public void testDescriptionFallbackOrder() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("description-" + TOOLTIP.getValue(), "Tooltip Description");
        assertEquals("Tooltip Description", info.getDescription());
        info.addItem("description-" + SHORT.getValue(), "Short Description");
        assertEquals("Short Description", info.getDescription());
        info.addItem("description-" + ONE_LINE.getValue(), "One-line Description");
        assertEquals("One-line Description", info.getDescription());
        info.addItem("description-" + DEFAULT.getValue(), "Default Description");
        assertEquals("Default Description", info.getDescription());
    }

    @Test
    public void testDescriptionKind() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        info.addItem("description-" + DEFAULT.getValue(), "Default Description");
        info.addItem("description-" + ONE_LINE.getValue(), "One-line Description");
        info.addItem("description-" + SHORT.getValue(), "Short Description");
        info.addItem("description-" + TOOLTIP.getValue(), "Tooltip Description");

        assertEquals("Default Description", info.getDescription(DEFAULT));
        assertEquals("One-line Description", info.getDescription(ONE_LINE));
        assertEquals("Short Description", info.getDescription(SHORT));
        assertEquals("Tooltip Description", info.getDescription(TOOLTIP));
    }

    @Test
    public void testGetIcons() {
        InformationDesc info = new InformationDesc(new Locale[0]);

        Assert.assertArrayEquals(new IconDesc[0], info.getIcons(IconKind.DEFAULT));

        IconDesc icon1 = new IconDesc(null, null, -1, -1, -1, -1);
        IconDesc icon2 = new IconDesc(null, null, -1, -1, -1, -1);
        info.addItem("icon-" + IconKind.DEFAULT.getValue(), icon1);
        info.addItem("icon-" + IconKind.DEFAULT.getValue(), icon2);

        assertArrayEquals(new IconDesc[] { icon1, icon2 }, info.getIcons(IconKind.DEFAULT));
    }

    @Test
    public void testGetIconLocations() throws MalformedURLException {
        InformationDesc info = new InformationDesc(new Locale[0]);

        URL location1 = new URL("http://location1.example.org");
        URL location2 = new URL("http://location2.example.org");
        IconDesc icon1 = new IconDesc(location1, null, 10, 10, -1, -1);
        IconDesc icon2 = new IconDesc(location2, null, 20, 20, -1, -1);
        info.addItem("icon-" + IconKind.DEFAULT.getValue(), icon1);
        info.addItem("icon-" + IconKind.DEFAULT.getValue(), icon2);

        // exact size matches
        assertEquals(location1, info.getIconLocation(IconKind.DEFAULT, 10, 10));
        assertEquals(location2, info.getIconLocation(IconKind.DEFAULT, 20, 20));

        // match a bigger icon
        assertEquals(location1, info.getIconLocation(IconKind.DEFAULT, 1, 1));
        assertEquals(location2, info.getIconLocation(IconKind.DEFAULT, 15, 15));

        // match a smaller icon
        assertEquals(location2, info.getIconLocation(IconKind.DEFAULT, 25, 25));
    }

    @Test
    public void testIsOfflineAllowed() {
        InformationDesc info = new InformationDesc(new Locale[0], true);
        assertFalse(info.isOfflineAllowed());
        info.addItem("offline-allowed", new Object());
        assertTrue(info.isOfflineAllowed());
    }
    
     @Test
    public void testIsOfflineAllowedNotStrict() {
        InformationDesc info = new InformationDesc(new Locale[0], false);
        assertTrue(info.isOfflineAllowed());
        info.addItem("offline-allowed", new Object());
        assertTrue(info.isOfflineAllowed());
    }

    @Test
    public void testIsSharingAllowed() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        assertFalse(info.isSharingAllowed());
        info.addItem("sharing-allowed", new Object());
        assertTrue(info.isSharingAllowed());
    }

    @Test
    public void testGetShortcut() {
        InformationDesc info = new InformationDesc(new Locale[0]);
        assertNull(info.getShortcut());

        final ShortcutDesc shortcut = new ShortcutDesc(false, false, false);
        info.addItem("shortcut", shortcut);
        assertSame(shortcut, info.getShortcut());
    }

    @Test
    public void testGetAssociation() throws ParseException {
        InformationDesc info = new InformationDesc(new Locale[0]);

        Assert.assertArrayEquals(new AssociationDesc[0], info.getAssociations());

        AssociationDesc association = new AssociationDesc("application/java-archive", new String[0]);
        info.addItem("association", association);
        assertArrayEquals(new AssociationDesc[] { association }, info.getAssociations());
    }

    @Test
    public void testGetRelatedContents() throws MalformedURLException {
        InformationDesc info = new InformationDesc(new Locale[0]);

        Assert.assertArrayEquals(new RelatedContentDesc[0], info.getRelatedContents());

        RelatedContentDesc relatedContent = new RelatedContentDesc(new URL("http://example.com:80"));
        info.addItem(RelatedContentDesc.RELATED_CONTENT_ELEMENT, relatedContent);

        assertArrayEquals(new RelatedContentDesc[] { relatedContent }, info.getRelatedContents());
    }

}
