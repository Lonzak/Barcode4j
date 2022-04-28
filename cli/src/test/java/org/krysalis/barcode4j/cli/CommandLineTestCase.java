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
package org.krysalis.barcode4j.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.avalon.framework.ExceptionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the command line application
 * @author Jeremias Maerki
 * @version $Id: CommandLineTestCase.java,v 1.3 2004-10-02 14:58:23 jmaerki Exp $
 */
public class CommandLineTestCase {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private ExitHandlerForTests exitHandler;

    private void dumpResults() throws Exception {
        System.out.println("Msg: " + this.exitHandler.getLastMsg());
        System.out.println("Exit code: " + this.exitHandler.getLastExitCode());
        if (this.exitHandler.getLastThrowable() != null) {
            System.out.println(ExceptionUtil.printStackTrace(
                this.exitHandler.getLastThrowable()));
        }
        System.out.println("--- stdout (" + this.out.size() + ") ---");
        System.out.println(new String(this.out.toByteArray(), "US-ASCII"));
        System.out.println("--- stderr (" + this.err.size() + ") ---");
        System.out.println(new String(this.err.toByteArray(), "US-ASCII"));
        System.out.println("---");
    }

    private static void callCLI(String[] args) {
        Main app = new Main();
        try {
            app.handleCommandLine(args);
        } catch (SimulateVMExitError se) {
            //ignore
        }
    }

    @Before
    public void setUp() {
        this.out = new ByteArrayOutputStream();
        this.err = new ByteArrayOutputStream();
        Main.stdout = new PrintStream(this.out);
        Main.stderr = new PrintStream(this.err);
        this.exitHandler = new ExitHandlerForTests();
        Main.setExitHandler(this.exitHandler);
    }
    
    @Test
    public void testSVG() {
        final String[] args = {"-s", "ean13", "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("No output", this.out.size() > 0);
        Assert.assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    @Test
    public void testEPS() {
        final String[] args = {"-s", "ean13", "-f", "eps", "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("No output", this.out.size() > 0);
        Assert.assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    @Test
    public void testBitmapJPEG() {
        final String[] args = {"-s", "ean13", "-f", "image/jpeg", "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("No output", this.out.size() > 0);
        Assert.assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    @Test
    public void testNoArgs() {
        final String[] args = new String[0];
        callCLI(args);
        Assert.assertEquals("Exit code must be -2", -2, this.exitHandler.getLastExitCode());
        Assert.assertNotNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("CLI help expected on stdout", this.out.size() > 0);
        Assert.assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    @Test
    public void testUnknownArg() {
        final String[] args = {"--badArgument"};
        callCLI(args);
        Assert.assertEquals("Exit code must be -2", -2, this.exitHandler.getLastExitCode());
        Assert.assertNotNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("CLI help expected on stdout", this.out.size() > 0);
        Assert.assertTrue("Error message expected on stderr", this.err.size() > 0);
    }
    
    @Test
    public void testWrongConfigFile() {
        final String[] args = {"-c", "NonExistingConfigFile", "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be -3", -3, this.exitHandler.getLastExitCode());
        Assert.assertNotNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("In case of error stdout may only be written to if there's "
            + "a problem with the command-line", this.out.size() == 0);
        Assert.assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    public void testValidConfigFile() {
        File cfgFile = new File("src/test/resources/good-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(),
            "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
    }

    @Test
    public void testBadConfigFile() {
        File cfgFile = new File("src/test/resources/bad-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(),
            "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be -6", -6, this.exitHandler.getLastExitCode());
        Assert.assertNotNull(this.exitHandler.getLastMsg());
        Assert.assertNotNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("In case of error stdout may only be written to if there's "
            + "a problem with the command-line", this.out.size() == 0);
        Assert.assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    @Test
    public void testToFile() throws Exception {
        File out = File.createTempFile("krba", ".tmp");
        final String[] args = {"-s", "ean-13", "-o", out.getAbsolutePath(),
                 "9771422985503+00006"};
        callCLI(args);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertNull(this.exitHandler.getLastMsg());
        Assert.assertNull(this.exitHandler.getLastThrowable());
        Assert.assertTrue("Application header expected on stdout",
            this.out.size() > 0);
        Assert.assertTrue("No output expected on stderr", this.err.size() == 0);
        Assert.assertTrue("Target file does not exist", out.exists());
        Assert.assertTrue("Target file must not be empty", out.length() > 0);
        if (!out.delete()) {
        	Assert.fail("Target file could not be deleted. Not closed?");
        } 
    }

    @Test
    public void testDPI() throws Exception {
        File out100 = File.createTempFile("krba", ".tmp");
        final String[] args100 = {"-s", "ean-13", 
                 "-o", out100.getAbsolutePath(),
                 "-f", "jpeg", 
                 "-d", "100", "9771422985503+00006"};
        callCLI(args100);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertTrue("Target file does not exist", out100.exists());

        File out300 = File.createTempFile("krba", ".tmp");
        final String[] args300 = {"-s", "ean-13", 
                 "-o", out300.getAbsolutePath(),
                 "-f", "jpeg",
                 "--dpi", "300", "9771422985503+00006"};
        callCLI(args300);
        Assert.assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        Assert.assertTrue("Target file does not exist", out300.exists());

        Assert.assertTrue("300dpi file must be greater than the 100dpi file", 
            out300.length() > out100.length());
        if (!out100.delete()) {
        	Assert.fail("Target file could not be deleted. Not closed?");
        } 
        if (!out300.delete()) {
        	Assert.fail("Target file could not be deleted. Not closed?");
        } 
    }

}
