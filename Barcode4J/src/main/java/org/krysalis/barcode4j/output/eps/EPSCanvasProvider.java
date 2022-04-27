/*
 * Copyright 2002-2004,2006,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.output.eps;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * CanvasProvider implementation for EPS output (Encapsulated PostScript).
 * @author Jeremias Maerki
 * @version $Id: EPSCanvasProvider.java,v 1.7 2009-03-12 15:04:55 jmaerki Exp $
 */
public class EPSCanvasProvider extends AbstractCanvasProvider {

    private Writer writer;
    private DecimalFormat df;
    private IOException firstError;
    private double height;

    /**
     * Main constructor.
     * @param out OutputStream to write the EPS to
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws IOException in case of an I/O problem
     */
    public EPSCanvasProvider(OutputStream out, int orientation) throws IOException {
        super(orientation);
        try {
            this.writer = new java.io.OutputStreamWriter(out, "US-ASCII");
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(
                    "Incompatible VM: Need US-ASCII encoding. " + uee.getMessage());
        }
    }

    /**
     * Returns the DecimalFormat instance to use internally to format numbers.
     * @return a DecimalFormat instance
     */
    protected DecimalFormat getDecimalFormat() {
        if (this.df == null) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            this.df = new DecimalFormat("0.####", dfs);
        }
        return this.df;
    }

    private String format(double coord) {
        return getDecimalFormat().format(coord);
    }

    private String formatmm(double coord) {
        return getDecimalFormat().format(UnitConv.mm2pt(coord));
    }

    private String formatmm(double x, double y) {
        return formatmm(x) + " "  + formatmm(this.height - y);
    }

    private void writeHeader(double width, double height) throws IOException {
        this.writer.write("%!PS-Adobe-3.0 EPSF-3.0\n");
        double widthpt = UnitConv.mm2pt(width);
        double heightpt = UnitConv.mm2pt(height);
        this.writer.write("%%BoundingBox: 0 0 "
                + Math.round(Math.ceil(widthpt)) + " "
                + Math.round(Math.ceil(heightpt)) + "\n");
        this.writer.write("%%HiResBoundingBox: 0 0 "
                + format(widthpt) + " "
                + format(heightpt) + "\n");
        this.writer.write("%%Creator: Barcode4J (http://barcode4j.krysalis.org)\n");
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.writer.write("%%CreationDate: " + sdf.format(new java.util.Date()) + "\n");
        this.writer.write("%%LanguageLevel: 1\n");
        this.writer.write("%%EndComments\n");
        this.writer.write("%%BeginProlog\n");
        this.writer.write("%%BeginProcSet: barcode4j-procset 1.1\n");
        this.writer.write("/rf {\n"); //rect fill: x y w h rf
        this.writer.write("newpath\n");
        this.writer.write("4 -2 roll moveto\n");
        this.writer.write("dup neg 0 exch rlineto\n");
        this.writer.write("exch 0 rlineto\n");
        this.writer.write("0 neg exch rlineto\n");
        this.writer.write("closepath fill\n");
        this.writer.write("} def\n");

        this.writer.write("/ct {\n"); //centered text: (text) middle-x y ct
        this.writer.write("moveto dup stringwidth\n");
        this.writer.write("2 div neg exch 2 div neg exch\n");
        this.writer.write("rmoveto show\n");
        this.writer.write("} def\n");

        this.writer.write("/rt {\n"); //right-aligned text: (text) x1 x2 y rt
        //Calc string width
        this.writer.write("4 -1 roll dup stringwidth pop\n");
        //Calc available width (x2-x1)
        this.writer.write("5 -2 roll 1 index sub\n");
        //Calc (text-width - avail-width) = diffx
        this.writer.write("3 -1 roll sub\n");
        //Calc x = (x1 + diffx)
        this.writer.write("add\n");
        //moveto and finally show
        this.writer.write("3 -1 roll moveto show\n");
        this.writer.write("} def\n");

        this.writer.write("/jt {\n"); //justified: (text) x1 x2 y jt
        //Calc string width
        this.writer.write("4 -1 roll dup stringwidth pop\n");
        //Calc available width (x2-x1)
        this.writer.write("5 -2 roll 1 index sub\n");
        //Calc (text-width - avail-width)
        this.writer.write("3 -1 roll sub\n");
        //Get string length
        this.writer.write("2 index length\n");
        //avail-width / (string-length - 1) = distributable-space
        this.writer.write("1 sub div\n");
        //setup moveto and ashow
        this.writer.write("0 4 -1 roll 4 -1 roll 5 -1 roll\n");
        this.writer.write("moveto ashow\n");
        this.writer.write("} def\n");

        this.writer.write("%%EndProcSet: barcode4j-procset 1.0\n");
        this.writer.write("%%EndProlog\n");
    }

    /**
     * Writes the EPS trailer. Must be called after barcode painting call
     * returns.
     * @throws IOException if an I/O error happened during EPS generation
     */
    public void finish() throws IOException {
        if (this.firstError != null) {
            throw this.firstError;
        }
        this.writer.write("%%EOF\n");
        this.writer.flush();
    }

    /** {@inheritDoc} */
    @Override
	public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        int orientation = BarcodeDimension.normalizeOrientation(getOrientation());
        if (this.firstError != null) {
            return;
        }
        this.height = dim.getHeightPlusQuiet();
        try {
            writeHeader(dim.getWidthPlusQuiet(orientation),
                    dim.getHeightPlusQuiet(orientation));
            String w = formatmm(dim.getWidthPlusQuiet());
            String h = formatmm(dim.getHeightPlusQuiet());
            switch (orientation) {
            case 90:
                this.writer.write("90 rotate 0" + " -" + h + " translate\n");
                break;
            case 180:
                this.writer.write("180 rotate -" + w + " -" + h + " translate\n");
                break;
            case 270:
                this.writer.write("270 rotate -" + w + " 0 translate\n");
                break;
            default:
                //nop
            }
        } catch (IOException ioe) {
            this.firstError = ioe;
        }
    }

    /** {@inheritDoc} */
    @Override
	public void deviceFillRect(double x, double y, double w, double h) {
        if (this.firstError != null) {
            return;
        }
        try {
            this.writer.write(formatmm(x, y) + " "
                       + formatmm(w) + " " + formatmm(h) + " rf\n");
        } catch (IOException ioe) {
            this.firstError = ioe;
        }
    }

    /** {@inheritDoc} */
    @Override
	public void deviceText(
                String text,
                double x1,
                double x2,
                double y1,
                String fontName,
                double fontSize,
                TextAlignment textAlign) {
        if (this.firstError != null) {
            return;
        }
        checkFontName(fontName);
        try {
            this.writer.write("/" + fontName + " findfont "
                    + UnitConv.mm2pt(fontSize) + " scalefont setfont\n");
            if (textAlign == TextAlignment.TA_LEFT) {
                this.writer.write(formatmm(x1, y1) + " moveto (" + text + ") show\n");
            } else if (textAlign == TextAlignment.TA_RIGHT) {
                this.writer.write("(" + text + ") "
                        + formatmm(x1) + " "
                        + formatmm(x2) + " "
                        + formatmm(this.height - y1) + " rt\n");
            } else if (textAlign == TextAlignment.TA_CENTER) {
                this.writer.write("(" + text + ") "
                        + formatmm((x1 + x2) / 2, y1) + " ct\n");
            } else if (textAlign == TextAlignment.TA_JUSTIFY) {
                this.writer.write("(" + text + ") "
                        + formatmm(x1) + " "
                        + formatmm(x2) + " "
                        + formatmm(this.height - y1) + " jt\n");
            }
        } catch (IOException ioe) {
            this.firstError = ioe;
        }
    }

    private static void checkFontName(String fontName) {
        if (fontName.indexOf(' ') >= 0) {
            throw new IllegalArgumentException("PostScript/EPS output does not support font names"
                    + " with spaces ('" + fontName
                    + "'). Please use the PostScript name of the font!");
        }
    }

}
