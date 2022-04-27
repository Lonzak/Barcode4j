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
package org.krysalis.barcode4j.impl.upcean;

import java.util.Stack;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DrawingUtil;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Logic Handler implementation for painting on a Canvas. This is a special
 * implementation for UPC and EAN barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCEANCanvasLogicHandler.java,v 1.3 2008-05-13 13:00:44 jmaerki Exp $
 */
public class UPCEANCanvasLogicHandler implements ClassicBarcodeLogicHandler {
    
    private UPCEANBean bcBean;
    private Canvas canvas;
    private double x = 0.0;
    private BarcodeDimension dim;
    private String msg;
    private String lastgroup;
    private double groupx;
    private boolean inMsgGroup;
    private boolean inSupplemental;
    private Stack groupStack = new Stack();
    
    /**
     * Main constructor.
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public UPCEANCanvasLogicHandler(AbstractBarcodeBean bcBean, Canvas canvas) {
        if (!(bcBean instanceof UPCEANBean)) {
            throw new IllegalArgumentException("This LogicHandler can only be "
                + "used with UPC and EAN barcode implementations");
        }
        this.bcBean = (UPCEANBean)bcBean;
        this.canvas = canvas;
    }
    
    private double getStartX() {
        if (this.bcBean.hasQuietZone()) {
            return this.bcBean.getQuietZone();
        } else {
            return 0.0;
        }
    }            

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    @Override
	public void startBarcode(String msg, String formattedMsg) {
        this.msg = msg;
        //Calculate extents
        this.dim = this.bcBean.calcDimensions(msg);
        
        this.canvas.establishDimensions(this.dim);
        this.x = getStartX();
        this.inMsgGroup = false;
        this.inSupplemental = false;
        
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    @Override
	public void startBarGroup(BarGroup type, String submsg) {
        if (type == BarGroup.UPC_EAN_GUARD) {
            //nop
        } else if (type == BarGroup.UPC_EAN_GROUP) {
            this.inMsgGroup = true;
            this.groupx = this.x;
            this.lastgroup = submsg;
        } else if (type == BarGroup.UPC_EAN_LEAD) {
            this.lastgroup = submsg;
        } else if (type == BarGroup.UPC_EAN_CHECK) {
            if (!this.inMsgGroup) {
                this.lastgroup = submsg;
            }
        } else if (type == BarGroup.UPC_EAN_SUPP) {
            this.inSupplemental = true;
            this.x += this.bcBean.getQuietZone();
            this.groupx = this.x;
        }
        this.groupStack.push(type);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    @Override
	public void addBar(boolean black, int width) {
        final double w = this.bcBean.getBarWidth(width);
        if (black) {
            final double h;
            final double y;
            if (!this.inSupplemental) {
                if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    this.canvas.drawRectWH(this.x, 0, w, this.bcBean.getHeight());
                } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    if (this.inMsgGroup) {
                        h = this.bcBean.getBarHeight();
                        y = this.bcBean.getHumanReadableHeight();
                    } else {
                        h = this.bcBean.getBarHeight() + (this.bcBean.getHumanReadableHeight() / 2);
                        y = this.bcBean.getHumanReadableHeight() / 2;
                    }
                    this.canvas.drawRectWH(this.x, y, w, h);
                } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    if (this.inMsgGroup) {
                        h = this.bcBean.getBarHeight();
                    } else {
                        h = this.bcBean.getBarHeight() + (this.bcBean.getHumanReadableHeight() / 2);
                    }
                    this.canvas.drawRectWH(this.x, 0.0, w, h);
                }
            } else {
                //Special painting in supplemental
                if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    h = this.bcBean.getBarHeight();
                    y = this.bcBean.getHumanReadableHeight();
                    this.canvas.drawRectWH(this.x, y, w, h);
                } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    h = this.bcBean.getBarHeight() 
                        + (this.bcBean.getHumanReadableHeight() / 2)
                        - this.bcBean.getHumanReadableHeight();
                    y = this.bcBean.getHumanReadableHeight() / 2;
                    this.canvas.drawRectWH(this.x, y, w, h);
                } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    h = this.bcBean.getBarHeight() 
                        + (this.bcBean.getHumanReadableHeight() / 2)
                        - this.bcBean.getHumanReadableHeight();
                    y = this.bcBean.getHumanReadableHeight();
                    this.canvas.drawRectWH(this.x, y, w, h);
                }
            }
        }
        this.x += w;
    }
    
    private boolean isEAN() {
        return (this.bcBean instanceof EAN13Bean) || (this.bcBean instanceof EAN8Bean);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    @Override
	public void endBarGroup() {
        BarGroup group = (BarGroup)this.groupStack.pop();

        if (group == BarGroup.UPC_EAN_GROUP) {
            this.inMsgGroup = false;
            if (this.lastgroup == null) {
                //Guards don't set the lastgroup variable
                return;
            }
            int colonPos = this.lastgroup.indexOf(":");
            String grouptext = this.lastgroup;
            if (colonPos >= 0) {
                String lead = new Character(grouptext.charAt(0)).toString();
                drawLeadChar(lead);
                grouptext = grouptext.substring(colonPos + 1);
            }
    
            //character group text
            drawGroupText(grouptext);
        } else if (group == BarGroup.UPC_EAN_LEAD) {
            if (!isEAN()) {
                drawLeadChar(this.lastgroup);
            }
        } else if (group == BarGroup.UPC_EAN_CHECK) {
            if (!isEAN()) {
                drawTrailingChar(this.lastgroup);
            }
        } else if (group == BarGroup.UPC_EAN_SUPP) {
            drawSupplementalText(UPCEANLogicImpl.retrieveSupplemental(this.msg));
            this.inSupplemental = false;
        }
    }
    
    private void drawLeadChar(String lead) {
        final double leadw = 7 * this.bcBean.getBarWidth(1);
        final double leadx = getStartX() 
                    - 3 * this.bcBean.getBarWidth(1)
                    - leadw;
                    
        if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(this.canvas, this.bcBean, 
                    lead, leadx, leadx + leadw, 
                    this.bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(this.canvas, this.bcBean, 
                    lead, leadx, leadx + leadw, 
                    this.bcBean.getHeight(), TextAlignment.TA_CENTER);
        }
    }

    private void drawTrailingChar(String trailer) {
        final double trailerw = 7 * this.bcBean.getBarWidth(1);
        final double trailerx = getStartX()
                    + this.dim.getWidth()
                    - this.bcBean.supplementalWidth(this.msg)
                    + 3 * this.bcBean.getBarWidth(1);
                    
        if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(this.canvas, this.bcBean, 
                    trailer, trailerx, trailerx + trailerw, 
                    this.bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(this.canvas, this.bcBean, 
                    trailer, trailerx, trailerx + trailerw, 
                    this.bcBean.getHeight(), TextAlignment.TA_CENTER);
        }
    }

    private void drawGroupText(String text) {
        if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(this.canvas, this.bcBean, text, 
                    this.groupx + this.bcBean.getBarWidth(1), 
                    this.x - this.bcBean.getBarWidth(1), 
                    this.bcBean.getHumanReadableHeight(), TextAlignment.TA_JUSTIFY);
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(this.canvas, this.bcBean, text, 
                    this.groupx + this.bcBean.getBarWidth(1), 
                    this.x - this.bcBean.getBarWidth(1), 
                    this.bcBean.getHeight(), TextAlignment.TA_JUSTIFY);
        }
    }
    
    private void drawSupplementalText(String supp) {
        if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(this.canvas, this.bcBean, supp, 
                    this.groupx, 
                    this.x, 
                    this.bcBean.getHeight(), TextAlignment.TA_CENTER);
        } else if (this.bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(this.canvas, this.bcBean, supp, 
                    this.groupx, 
                    this.x, 
                    this.bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        }
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    @Override
	public void endBarcode() {
    }

}

