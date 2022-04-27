/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl;

import org.junit.Assert;
import org.junit.Test;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.postnet.POSTNET;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.postnet.POSTNETLogicImpl;

import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Test class for the POSTNET implementation.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNETTest.java,v 1.5 2008-05-13 13:00:43 jmaerki Exp $
 */
public class POSTNETTest {

	@Test
    public void testChecksum() {
		Assert.assertEquals('1', POSTNETLogicImpl.calcChecksum("75368"));
		Assert.assertEquals('7', POSTNETLogicImpl.calcChecksum("110119000"));
		Assert.assertEquals('7', POSTNETLogicImpl.calcChecksum("11011-9000"));
		Assert.assertEquals('0', POSTNETLogicImpl.calcChecksum("400017265951"));
    }
    
	@Test
    public void testIllegalArguments() {
        try {
            POSTNET impl = new POSTNET();
            impl.generateBarcode(null, null);
            Assert.fail("Expected an NPE");
        } catch (NullPointerException npe) {
        	Assert.assertNotNull("Error message is empty", npe.getMessage());
        }
    }
    
	@Test
    public void testIgnoreChars() {
    	Assert.assertEquals("75368", POSTNETLogicImpl.removeIgnoredCharacters("75368"));
    	Assert.assertEquals("110119000", POSTNETLogicImpl.removeIgnoredCharacters("11011-9000"));
    }
    
	@Test
    public void testLogic() {
        StringBuffer sb = new StringBuffer();
        POSTNETLogicImpl logic;
        String expected;
        
        try {
            logic = new POSTNETLogicImpl(ChecksumMode.CP_AUTO, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123���2");
            Assert.fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        logic = new POSTNETLogicImpl(ChecksumMode.CP_AUTO, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "75368");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        Assert.assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_ADD, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "75368");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:1>B1W-1B1W-1B1W-1B2W-1B2W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        Assert.assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_CHECK, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "753681");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:1>B1W-1B1W-1B1W-1B2W-1B2W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        Assert.assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_CHECK, false);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "753685");
            Assert.fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

	@Test
    public void testDefaultsInXML() throws Exception {
        POSTNETBean refBean = new POSTNETBean();
        
        POSTNET gen = new POSTNET();
        DefaultConfiguration cfg = new DefaultConfiguration("postnet");
        gen.configure(cfg);
        POSTNETBean xmlBean = gen.getPOSTNETBean();
        Assert.assertEquals(refBean.getBarHeight(), xmlBean.getBarHeight(), 0.01);
        Assert.assertEquals(refBean.getBaselinePosition(), xmlBean.getBaselinePosition());
        Assert.assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        Assert.assertEquals(refBean.isDisplayChecksum(), xmlBean.isDisplayChecksum());
        Assert.assertEquals(refBean.getFontSize(), xmlBean.getFontSize(), 0.01);
        Assert.assertEquals(refBean.getHeight(), xmlBean.getHeight(), 0.01);
        Assert.assertEquals(refBean.getHumanReadableHeight(), xmlBean.getHumanReadableHeight(), 0.01);
        Assert.assertEquals(refBean.getIntercharGapWidth(), xmlBean.getIntercharGapWidth(), 0.01);
        Assert.assertEquals(refBean.getModuleWidth(), xmlBean.getModuleWidth(), 0.01);
        Assert.assertEquals(refBean.getQuietZone(), xmlBean.getQuietZone(), 0.01);
        Assert.assertEquals(refBean.getShortBarHeight(), xmlBean.getShortBarHeight(), 0.01);
        Assert.assertEquals(refBean.getVerticalQuietZone(), xmlBean.getVerticalQuietZone(), 0.01);
        Assert.assertEquals(refBean.hasQuietZone(), xmlBean.hasQuietZone());
        Assert.assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        Assert.assertEquals(refBean.getMsgPosition(), xmlBean.getMsgPosition());
        Assert.assertEquals(refBean.getPattern(), xmlBean.getPattern());
    }
    
}