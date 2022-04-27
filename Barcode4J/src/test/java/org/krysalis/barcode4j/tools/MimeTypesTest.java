/*
 * Copyright 2002-2004 Jeremias Maerki.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.tools;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the MimeTypes class.
 * 
 * @author Jeremias Maerki
 * @version $Id: MimeTypesTest.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
 */
public class MimeTypesTest {

	@Test
    public void testExpandFormat() {
        Assert.assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat("svg"));
        Assert.assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat("sVG"));
        Assert.assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat(MimeTypes.MIME_SVG));
        Assert.assertEquals(MimeTypes.MIME_EPS, MimeTypes.expandFormat("EPS"));
        Assert.assertEquals("image/bmp", MimeTypes.expandFormat("image/bmp"));
        Assert.assertEquals("anything", MimeTypes.expandFormat("anything"));
        Assert.assertNull(MimeTypes.expandFormat(""));
        Assert.assertNull(MimeTypes.expandFormat(null));
    }
    
	@Test
    public void testIsBitmapFormat() {
        Assert.assertTrue(MimeTypes.isBitmapFormat("tiff"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("tif"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("jpeg"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("jpg"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("gif"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("png"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("image/png"));
        Assert.assertTrue(MimeTypes.isBitmapFormat("image/x-png"));
        Assert.assertFalse(MimeTypes.isBitmapFormat("svg"));
        Assert.assertFalse(MimeTypes.isBitmapFormat("eps"));
    }

}
