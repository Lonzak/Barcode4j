/*
 * Copyright (C) 2007 by Edmond R&D B.V.
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
package org.krysalis.barcode4j.impl.code128;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Code128 encoder.
 * @author branko
 */
public class Code128EncoderTester {
	
	@Test
	public void Code128EncoderTest() {
		
		Assert.assertEquals("Minimal codeset C","StartC|idx10",
                Code128LogicImpl.toString(new DefaultCode128Encoder().encode("10")));
	
        Assert.assertEquals("Simple codeset C with FNC1",
                "StartC|FNC1|idx10", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\36110")));
        
        Assert.assertEquals(
                "Simple codeset C with 2 * FNC1", "StartC|FNC1|FNC1|idx10",
                Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\361\36110")));
        Assert.assertEquals(
                "One digit short for code set C", "StartB|FNC1|idx17", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\3611")));
        Assert.assertEquals("Minimal code set B",
                "StartB|idx65", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("a")));
        Assert.assertEquals("Minimal code set A",
                "StartA|idx64", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\000")));
        Assert.assertEquals("Long code set B",
                "StartB|idx17|idx16|idx33|idx33|idx33|idx33", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("10AAAA")));
        Assert.assertEquals("Long code set A",
                "StartA|idx17|idx16|idx33|idx33|idx64", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("10AA\000")));
        Assert.assertEquals("Shift to B from code set A",
                "StartA|idx33|idx64|Shift/98|idx65|idx64", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("A\000a\000")));
        Assert.assertEquals("Switch to B from code set A",
                "StartA|idx65|CodeB/FNC4|idx65|idx65", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\001aa")));
        Assert.assertEquals("Switch to C from code set A",
                "StartA|idx64|CodeC/99|idx0|idx0", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("\0000000")));
        Assert.assertEquals("Shift to A from code set B",
                "StartB|idx65|Shift/98|idx65|idx65", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("a\001a")));
        Assert.assertEquals("Switch to A from code set B",
                "StartB|idx65|CodeA/FNC4|idx65|idx65", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("a\001\001")));
        Assert.assertEquals("Switch to C from code set B",
                "StartB|idx65|CodeC/99|idx0|idx0", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("a0000")));
        Assert.assertEquals("Switch to A from code set C",
                "StartC|idx0|idx0|CodeA/FNC4|idx64|idx64", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("0000\000\000")));
        Assert.assertEquals("Switch to B from code set C",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65", Code128LogicImpl.toString(new DefaultCode128Encoder().encode("0000aa")));
        Assert.assertEquals(
                "All codeset and shifts",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65|Shift/98|idx64|idx65|CodeA/FNC4|idx64|idx64|Shift/98|idx65|idx64|CodeB/FNC4|idx65|idx65|CodeC/99|idx0|idx0",
                Code128LogicImpl.toString(new DefaultCode128Encoder().encode("0000aa\000a\000\000a\000aa0000")));
    }
}
