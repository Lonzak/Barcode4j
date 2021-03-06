/*
 * Copyright 2003-2004,2007 Jeremias Maerki.
 * Copyright 2006 Robert Deeken (compatibility with Saxon 8.7.1 and later)
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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.AbstractSVGGeneratingCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;

import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.event.SequenceReceiver;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.trans.DynamicError;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ValidationException;

/**
 * This represents the main barcode element.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeStyleElement.java,v 1.4 2007-01-15 11:12:33 jmaerki Exp $
 */
public class BarcodeStyleElement extends StyleElement {

    private Expression message;
    private Expression orientation;
    
    /**
     * @see net.sf.saxon.style.StyleElement#isInstruction()
     */
    @Override
	public boolean isInstruction() {
        return true;
    }

    /**
     * Determine whether this type of element is allowed to contain a template-body
     * @return true: yes, it may contain a template-body (this is done only so that
     * it can contain xsl:fallback)
     */
    @Override
	public boolean mayContainSequenceConstructor() {
        return true;
    }

    /**
     * @throws XPathException 
     * @see StyleElement#prepareAttributes()
     */
    @Override
	public void prepareAttributes() throws XPathException {
        // Get mandatory message attribute
        String msgAtt = this.attributeList.getValue("", "message");
        if (msgAtt == null) {
            reportAbsence("message");
        }
        this.message = makeAttributeValueTemplate(msgAtt);
        String orientationAtt = this.attributeList.getValue("", "orientation");
        if (orientationAtt != null) {
            this.orientation = makeAttributeValueTemplate(orientationAtt);
        } else {
            this.orientation = null;
        }
    }

    /**
     * @see net.sf.saxon.style.StyleElement#validate()
     */
    @Override
	public void validate() throws XPathException {
        super.validate();
        checkWithinTemplate();
        this.message = typeCheck("message", this.message);
        if (this.orientation != null) {
            this.orientation = typeCheck("orientation", this.orientation);
        }
    }


    /**
     * @see net.sf.saxon.style.StyleElement#compile(net.sf.saxon.instruct.Executable)
     */
    @Override
	public Expression compile(Executable exec) throws XPathException {
        NodeOverNodeInfo node = NodeOverNodeInfo.wrap(this);
        final Configuration cfg = ConfigurationUtil.buildConfiguration(node);
        BarcodeExpression inst = new BarcodeExpression(this.message, this.orientation, cfg);
        return inst;
    }
    
    /**
     * @see net.sf.saxon.style.StyleElement#isPermittedChild(net.sf.saxon.style.StyleElement)
     */
    @Override
	protected boolean isPermittedChild(StyleElement styleElement) {
        // I am allowing anything right now
        return true;
    }

    private static class BarcodeExpression extends SimpleExpression {

		private static final long serialVersionUID = 2272767827436196451L;
		private Expression message;
        private Expression orientation;
        private Configuration cfg;
        
        public BarcodeExpression(Expression message, Expression orientation, Configuration cfg) {
            this.message = message;
            this.orientation = orientation;
            this.cfg = cfg;
        }
        
        @Override
		public int getImplementationMethod() {
            return Expression.PROCESS_METHOD;
        }
 
        @Override
		public void process(XPathContext context) throws XPathException {
            String effMessage = this.message.evaluateAsString(context).toString();
            int effOrientation = 0;
            if (this.orientation != null) {
                String s = this.orientation.evaluateAsString(context).toString();
                try {
                    effOrientation = Integer.parseInt(s);
                    effOrientation = BarcodeDimension.normalizeOrientation(effOrientation);
                } catch (NumberFormatException nfe) {
                    throw new ValidationException(nfe);
                } catch (IllegalArgumentException iae) {
                    throw new ValidationException(iae);
                }
            }
            
            try {
                SequenceReceiver out = context.getReceiver();
                
                //Acquire BarcodeGenerator
                final BarcodeGenerator gen =
                        BarcodeUtil.getInstance().createBarcodeGenerator(this.cfg);
                
                //Setup Canvas
                final SVGCanvasProvider svg;
                if (this.cfg.getAttributeAsBoolean("useNamespace", true)) {
                    svg = new SVGCanvasProvider(this.cfg.getAttribute("prefix", "svg"), 
                            effOrientation);
                } else {
                    svg = new SVGCanvasProvider(false, 
                            effOrientation);
                }
                //Generate barcode
                gen.generateBarcode(svg, effMessage);
                
                DocumentWrapper wrapper = new DocumentWrapper(svg.getDOM(),
                        AbstractSVGGeneratingCanvasProvider.SVG_NAMESPACE, context.getConfiguration());
                out.append(wrapper, this.getLocationId(), 1);
                
            } catch (ConfigurationException ce) {
                throw new DynamicError("(Barcode4J) " + ce.getMessage());
            } catch (BarcodeException be) {
                throw new DynamicError("(Barcode4J) " + be.getMessage());
            }
        }
    }
}
