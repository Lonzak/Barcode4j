/*
 * Copyright 2006 Jeremias Maerki
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

/* $Id: SymbolInfoTest.java,v 1.3 2008-09-22 08:59:08 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the SymbolInfo class.
 */
public class SymbolInfoTest {

	@Test
    public void testSymbolInfo() {
        DataMatrixSymbolInfo info;
        info = DataMatrixSymbolInfo.lookup(3);
        Assert.assertEquals(5, info.errorCodewords);
        Assert.assertEquals(8, info.matrixWidth);
        Assert.assertEquals(8, info.matrixHeight);
        Assert.assertEquals(10, info.getSymbolWidth());
        Assert.assertEquals(10, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(3, SymbolShapeHint.FORCE_RECTANGLE);
        Assert.assertEquals(7, info.errorCodewords);
        Assert.assertEquals(16, info.matrixWidth);
        Assert.assertEquals(6, info.matrixHeight);
        Assert.assertEquals(18, info.getSymbolWidth());
        Assert.assertEquals(8, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(9);
        Assert.assertEquals(11, info.errorCodewords);
        Assert.assertEquals(14, info.matrixWidth);
        Assert.assertEquals(6, info.matrixHeight);
        Assert.assertEquals(32, info.getSymbolWidth());
        Assert.assertEquals(8, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(9, SymbolShapeHint.FORCE_SQUARE);
        Assert.assertEquals(12, info.errorCodewords);
        Assert.assertEquals(14, info.matrixWidth);
        Assert.assertEquals(14, info.matrixHeight);
        Assert.assertEquals(16, info.getSymbolWidth());
        Assert.assertEquals(16, info.getSymbolHeight());

        try {
            info = DataMatrixSymbolInfo.lookup(1559);
            Assert.fail("There's no rectangular symbol for more than 1558 data codewords");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        try {
            info = DataMatrixSymbolInfo.lookup(50, SymbolShapeHint.FORCE_RECTANGLE);
            Assert.fail("There's no rectangular symbol for 50 data codewords");
        } catch (IllegalArgumentException iae) {
            //expected
        }

        info = DataMatrixSymbolInfo.lookup(35);
        Assert.assertEquals(24, info.getSymbolWidth());
        Assert.assertEquals(24, info.getSymbolHeight());

        Dimension fixedSize = new Dimension(26, 26);
        info = DataMatrixSymbolInfo.lookup(35,
                SymbolShapeHint.FORCE_NONE, fixedSize, fixedSize, false);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(45,
                SymbolShapeHint.FORCE_NONE, fixedSize, fixedSize, false);
        Assert.assertNull(info);

        Dimension minSize = fixedSize;
        Dimension maxSize = new Dimension(32, 32);

        info = DataMatrixSymbolInfo.lookup(35,
                SymbolShapeHint.FORCE_NONE, minSize, maxSize, false);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(40,
                SymbolShapeHint.FORCE_NONE, minSize, maxSize, false);
        Assert.assertEquals(26, info.getSymbolWidth());
        Assert.assertEquals(26, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(45,
                SymbolShapeHint.FORCE_NONE, minSize, maxSize, false);
        Assert.assertEquals(32, info.getSymbolWidth());
        Assert.assertEquals(32, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(63,
                SymbolShapeHint.FORCE_NONE, minSize, maxSize, false);
        Assert.assertNull(info);
    }

}
