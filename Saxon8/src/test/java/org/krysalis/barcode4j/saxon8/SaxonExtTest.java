/*
 * Copyright 2003-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.saxon8;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for the Saxon 8.x extension.
 * 
 * @author Jeremias Maerki
 * @version $Id: SaxonExtTest.java,v 1.2 2004-09-04 20:26:15 jmaerki Exp $
 */
public class SaxonExtTest{

	@Test
    public void testSaxonExt() throws Exception {
        Class clazz = Class.forName("net.sf.saxon.TransformerFactoryImpl");
        TransformerFactory factory = (TransformerFactory)clazz.newInstance();
        Transformer trans = factory.newTransformer(new StreamSource(
                new File("src/test/resources/saxon8-test.xsl")));
        Source src = new StreamSource(new File("src/test/resources/xslt-test.xml"));
        StringWriter writer = new StringWriter();
        Result res = new StreamResult(writer);
        
        trans.transform(src, res);
        String output = writer.getBuffer().toString();
        Assert.assertTrue(output.indexOf("svg") >= 0);
        //System.out.println(writer.getBuffer());
    }

}
