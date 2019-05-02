/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import datastore.*;

import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.fop.svg.PDFTranscoder;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.CDATASection;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;

import path.ResPath;
import util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adam Lugowski
 */
public class ImageGenerator {

    public static final int pixPerCm = 30;
    public RootColumn rootCol;
    public Settings settings;
    public Settings settingsRefFamilyTree;
    public DOMImplementation impl;
    public SVGDocument doc;
    public Element svgRoot;
    public Element patternRoot;
    public Element timeline; // this is to save the timeline element before removing in case of saving to svg or pdf and then adding again.
    public Element timelabel;
    public PatternManager patMan;
    public SVGGraphics2D g;
    public LinkProcessor linkProc = null;
    protected String svgNS;
    protected Element curElement;
    protected Stack groupings;
    protected int clipPathNum = 0;
    public static int gradNum = 0;
    protected boolean allowNegatives;
    protected double canvasWidth; // set by setCanvasSize()
    protected double canvasHeight; // set by setCanvasSize()
    // CrossPlot settings
    public static final int TEXT_SIZE = 20;
    public static final String BORDER_STYLE = "stroke-width:" + Settings.BORDER_WIDTH + "; fill: none; stroke: black;";
    public static final String TIMELINE_STYLE = "stroke: red; stroke-opacity: 0.5;";
    public static final String TIMELINE_LABEL_STYLE = "font-family: verdana; fill: red; fill-opacity: 0.7;";
    //public static final String TIMELINE_LABEL_STYLE_VER2 = "font-family: verdana; fill: green; fill-opacity: 0.7;";
    public static final String CP_MARKER_STYLE = "fill-opacity: 0.7; r: 7;";
    public static final String CP_MODEL_STYLE = "r: 7;";
    public static final String CP_PLOT_POPUP_BOX_STYLE = "fill: black; stroke: none; fill-opacity: 0.6;";
    public static final String CP_PLOT_POPUP_TEXT_STYLE = "fill: white; font-family: verdana;";
    //
    public static String line_color = "gray";
    public static String model_color = "black";
    public static String marker_color = "gray";
    //
    public static final int TOP = 1;
    public static final int CENTER = 2;
    public static final int BOTTOM = 3;
    public static final int PREFERRED = 4;
    public static final int BOTTOM_RIGHT = 8;
    public static final int TOP_LEFT = 9;
    public static final int ONLY_TEXT = 10;
    public static final int TEXT_AND_BACKGROUND = 12;
    public static final int POINT_RECT = 1;
    public static final int POINT_ROUND = 2;
    public static final int POINT_TICK = 3;
    public static final int POINT_DIMENSION = 4; // the diameter of a round point, or the side of a tick/rect
    public static final int SOLID_LINE = 1;
    public static final int DASHED_LINE = 2;
    public static final int DOTTED_LINE = 3;
    public static final double CONTROL_POINT_LENGTH = 0.4;
    public static final double MAX_CONTROL_POINT_LENGTH = 10;
    public static final double MIN_CONTROL_POINT_LENGTH = 0.001;
    private HashMap<EventTarget, datastore.RangeColumn.RangePoint> branchNodeList = null;
    //
    public DataColumn interval_column = null;

    // popup stuff
    public static class PopupInfo {

        static int count = 0;
        public RichText text;
        public String id;
        public String spawnerID;
        DataColumn.FileInfo colFileInfo;

        public PopupInfo(RichText t, DataColumn.FileInfo colFileInfo) {
            if (colFileInfo != null) {
                //Debug.print("Creating new PopupInfo, workingDir = " + colFileInfo.workingDir + ", baseURL=" + colFileInfo.baseURL);
                text = new RichText(HTMLPreprocessor.findAndFixDatapackLinks(t.getSourceText(), colFileInfo),
                        colFileInfo);
            } else {
                // No information about the column's parent directory, so just keep plain vanilla text
                text = t;
            }
            id = "id" + count;
            spawnerID = "spawner" + count;
            count++;
            this.colFileInfo = colFileInfo;
        }
    }

    protected Vector popups;

    /*
    public static boolean includesText(int i) {
    if (i == ONLY_TEXT || i == TEXT_AND_BACKGROUND)
    return true;
    return false;
    }*/
    public static boolean includesBackground(int i) {
        if (i == TEXT_AND_BACKGROUND) {
            return true;
        }
        return false;
    }

    public ImageGenerator(RootColumn col, Settings s, PatternManager patMan) {
        rootCol = col;
        settings = s;
        settingsRefFamilyTree = s;
        this.patMan = patMan;

        reset();
    }

    public final void reset() {
        impl = SVGDOMImplementation.getDOMImplementation();
        svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

        // This is a workaround for a bug in Batik's script interpreter. Without the URI it won't be able
        // to find the script factory. The fix is apparently in the CVS already, so presumably the next
        // Batik release should have this properly fixed and this line can be removed.
        // this was added for the batik 1.7 release.
        doc.setDocumentURI("file:/temp/whatever.svg");

        g = new SVGGraphics2D(doc);

        groupings = new Stack();
        popups = new Vector();

        svgRoot = doc.getDocumentElement();
        curElement = svgRoot;

        loadPatterns();
    }

    public void loadPatterns() {
        patternRoot = pushGrouping();

        ChronColumn.setupPatterns(this);
        popGrouping();
    }

    public void setAllowNegatives(boolean tf) {
        allowNegatives = tf;
    }

    public boolean getAllowNegatives() {
        return allowNegatives;
    }

    public static void write(SVGDocument doc, Writer w) throws TranscoderException {
        SVGTranscoder st = new SVGTranscoder();
        TranscoderOutput to = new TranscoderOutput(w);
        TranscoderInput ti = new TranscoderInput(doc);
        st.transcode(ti, to);
    }

    public void write(Writer w) throws TranscoderException {
        write(doc, w);
    }

    public static void writePDF(SVGDocument doc, OutputStream os) throws TranscoderException {
        JOptionPane optionPane = new JOptionPane(
                "SVG file saved, saving to PDF next.\nClick \"OK\" to continue.",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog messageBox = null;
        if (TSCreator.NODE_MODE == false) {
        	messageBox = optionPane.createDialog("Saving ...");
        	messageBox.setVisible(true);
        }
        try {
            Transcoder pt = new PDFTranscoder();
            TranscoderOutput to = new TranscoderOutput(os);
            TranscoderInput ti = new TranscoderInput(doc);
            pt.addTranscodingHint(PDFTranscoder.KEY_HEIGHT, (float)2500);
            pt.transcode(ti, to);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        if (messageBox != null) {
        	messageBox.setVisible(false);
        }
    }

    public void writePDF(OutputStream os) throws TranscoderException {
        writePDF(doc, os);
    }

    public static void writePNG(SVGDocument doc, OutputStream os, int width, int height) throws TranscoderException {
        PNGTranscoder pngTrans = new PNGTranscoder();
        pngTrans.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
        pngTrans.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(height));
        pngTrans.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
        TranscoderOutput to = new TranscoderOutput(os);
        TranscoderInput ti = new TranscoderInput(doc);
        pngTrans.transcode(ti, to);
    }

    public void writePNG(OutputStream os, int width, int height) throws TranscoderException {
        writePNG(doc, os, width, height);
    }

    public static void writeJPG(SVGDocument doc, OutputStream os, int width, int height) throws TranscoderException {
        JPEGTranscoder pngTrans = new JPEGTranscoder();
        pngTrans.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(width));
        pngTrans.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(height));
        pngTrans.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0f));
        pngTrans.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
        TranscoderOutput to = new TranscoderOutput(os);
        TranscoderInput ti = new TranscoderInput(doc);
        pngTrans.transcode(ti, to);
    }

    public void writeJPG(OutputStream os, int width, int height) throws TranscoderException {
        writeJPG(doc, os, width, height);
    }

    public void setCanvasSize(double width, double height) {
        svgRoot.setAttributeNS(null, "onload", "Init(evt)");
        svgRoot.setAttributeNS(null, "width", round(width / 30) + "cm");
        svgRoot.setAttributeNS(null, "height", round(height / 30) + "cm");
        svgRoot.setAttributeNS(null, "viewBox", "0 0 " + round(width) + " " + round(height));


        canvasWidth = width;
        canvasHeight = height;
    }

    public Element pushGrouping() {
        groupings.push(curElement);
        Element newG = doc.createElementNS(svgNS, "g");
        curElement.appendChild(newG);
        curElement = newG;
        return newG;
    }

    protected Element pushElement(Element e) {
        groupings.push(curElement);
        curElement = e;
        return e;
    }

    public void popGrouping() {
        curElement = (Element) groupings.pop();
    }

    public String pushClipPath() {
        String id = "clipPath" + clipPathNum++;
        pushClipPath(id);
        return id;
    }

    public void pushClipPath(String id) {
        groupings.push(curElement);
        Element newCP = doc.createElementNS(svgNS, "clipPath");
        newCP.setAttribute("id", id);
        curElement.appendChild(newCP);
        curElement = newCP;
    }

    public void setClipPath(String id) {
        curElement.setAttribute("clip-path", "url(#" + id + ")");
    }
    
    public void popGradient() {
        popGrouping();
    }
    
    public String pushGradient() {
    	gradNum++;
        String id = "grad" + gradNum;
        pushGradient(id);
        return id;
    }
    
    public void pushGradient(String id){
    	groupings.push(curElement);
    	Element newLG = doc.createElementNS(svgNS, "linearGradient");
    	newLG.setAttribute("id", id);
    	curElement.appendChild(newLG);
        curElement = newLG;	
    }
    
    public Element setStop(double post, String style){
    	Element stop = doc.createElementNS(svgNS, "stop");
    	stop.setAttributeNS(null, "offset", round(post));
    	if (style != null) {
            stop.setAttributeNS(null, "style", style);
        }
    	curElement.appendChild(stop);
        return stop;
    }

    public void pushPattern(String id, double width, double height) {
        groupings.push(curElement);
        Element newPattern = doc.createElementNS(svgNS, "pattern");
        newPattern.setAttributeNS(null, "id", id);
        newPattern.setAttributeNS(null, "x", "0");
        newPattern.setAttributeNS(null, "y", "0");
        newPattern.setAttributeNS(null, "width", round(width));
        newPattern.setAttributeNS(null, "height", round(height));
        newPattern.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
        patternRoot.appendChild(newPattern);
        curElement = newPattern;

        setAllowNegatives(true);
    }

    public void addPattern(Element pattern) {
        Element patternCopy = addElementCopy(pattern);
        patternRoot.appendChild(patternCopy);
    }

    public void popPattern() {
        curElement = (Element) groupings.pop();

        if (curElement.getNodeName().compareToIgnoreCase("pattern") != 0) {
            setAllowNegatives(false);
        }
    }

    public void popClipPath() {
        popGrouping();
    }

    public FontMetrics getFontMetrics(TSCFont f) {
        return g.getFontMetrics(f.getFont());
    }

    public Rectangle2D getStringBounds(TSCFont f, String s) {
        return getFontMetrics(f).getStringBounds(s, g);
    }

    public Rectangle2D getStringBounds(StringWrappingInfo swi) {
        Rectangle2D.Double size = new Rectangle2D.Double();

        /*
        size.height = swi.s.length * swi.lineHeight;
        
        // get the bounds for each row in the
        for (int i = 0; i < swi.s.length; i++) {
        size.width = Math.max(size.width, swi.widths[i]);
        }*/

        size.width = swi.getWidth();
        size.height = swi.getHeight();

        return size;
    }

    protected String round(double d) {
        if (d == 0) {
            return "0";
        }

        // see if only positive numbers allowed
        if (!allowNegatives && d < 0) {
            return "0";
        }

        d += 0.000005; // keep 5 chars after decimal point + '.' = 6 chars
        String s = Double.toString(d);

        // cut off extra digits
        int end = Math.min(s.length(), s.indexOf('.') + 6);
        if (end < s.length()) {
            s = s.substring(0, end);
        }

        return s;
    }

    public Element createCrossPlotGroup(double minAge, double maxAge, double minDepth, double maxDepth) {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlot");
        group.setAttributeNS(null, "CdtType", "0");
        group.setAttributeNS(null, "minAge", round(minAge));
        group.setAttributeNS(null, "maxAge", round(maxAge));
        group.setAttributeNS(null, "minDepth", round(minDepth));
        group.setAttributeNS(null, "maxDepth", round(maxDepth));
        curElement.appendChild(group);
        createCrossplotLimitingBox();
        createCPLinesGroup();
        createCPTimeLabelsGroup();
        createCPTimeLinesGroup();
        createFADGroup();
        createLADGroup();
        createCPMarkersGroup();
        createCPPointsGroup();
        createCrossplotPopupGroup();
        return group;
    }

    public Element createCPPointsGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotModels");
        Element cpGroup = doc.getElementById("CrossPlot");
        group.setAttributeNS(null, "fill-color", model_color);
        group.setAttributeNS(null, "style", CP_MODEL_STYLE);
        group.setAttributeNS(null, "visibility", "visible");
        group.setAttributeNS(null, "type", "0");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createCPMarkersGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotMarkers");
        Element cpGroup = doc.getElementById("CrossPlot");
        group.setAttributeNS(null, "style", CP_MARKER_STYLE);
        group.setAttributeNS(null, "fill-color", marker_color);
        group.setAttributeNS(null, "visibility", "visible");
        group.setAttributeNS(null, "type", "0");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createFADGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotFADLines");
        group.setAttributeNS(null, "stroke", "black");
        group.setAttributeNS(null, "visibility", "visible");
        Element cpGroup = doc.getElementById("CrossPlot");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createLADGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotLADLines");
        group.setAttributeNS(null, "stroke", "black");
        group.setAttributeNS(null, "visibility", "visible");
        Element cpGroup = doc.getElementById("CrossPlot");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createCPLinesGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotLines");
        group.setAttributeNS(null, "stroke", line_color);
        group.setAttributeNS(null, "visibility", "visible");
        Element cpGroup = doc.getElementById("CrossPlot");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createCPTimeLinesGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotTimeLines");
        Element cpGroup = doc.getElementById("CrossPlot");
        group.setAttributeNS(null, "style", TIMELINE_STYLE);
        group.setAttributeNS(null, "stroke-width", "1");
        group.setAttributeNS(null, "visibility", "visible");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createCPTimeLabelsGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        group.setAttributeNS(null, "id", "CrossPlotTimeLabels");
        Element cpGroup = doc.getElementById("CrossPlot");
        group.setAttributeNS(null, "style", TIMELINE_LABEL_STYLE);
        group.setAttributeNS(null, "font-size", Integer.toString(TEXT_SIZE));
        group.setAttributeNS(null, "visibility", "visible");
        cpGroup.appendChild(group);
        return group;
    }

    public Element createCrossplotLimitingBox() {
        Element cpGroup = doc.getElementById("CrossPlot");
        Element box = doc.createElementNS(svgNS, "rect");
        box.setAttributeNS(null, "id", "CrossplotLimitingBox");
        box.setAttributeNS(null, "fill", "none");
        box.setAttributeNS(null, "stroke", "red");
        box.setAttributeNS(null, "stroke-width", "2");
        box.setAttributeNS(null, "x", "0");
        box.setAttributeNS(null, "y", "0");
        box.setAttributeNS(null, "width", "0");
        box.setAttributeNS(null, "height", "0");
        box.setAttributeNS(null, "visibility", "visible");
        cpGroup.appendChild(box);
        return box;
    }

    public Element createCrossplotPopupGroup() {
        Element group = doc.createElementNS(svgNS, "g");
        {
            Element cpGroup = doc.getElementById("CrossPlot");
            {
                group.setAttributeNS(null, "id", "CrossplotPopup");
                group.setAttributeNS(null, "visibility", "hidden");
                group.setAttributeNS(null, "showPopup", "false");
                {
                    Element box = doc.createElementNS(svgNS, "rect");
                    {
                        box.setAttributeNS(null, "id", "CrossplotPopupBox");
                        box.setAttributeNS(null, "style", ImageGenerator.CP_PLOT_POPUP_BOX_STYLE);
                        box.setAttributeNS(null, "x", "0");
                        box.setAttributeNS(null, "y", "0");
                        box.setAttributeNS(null, "rx", "3");
                        box.setAttributeNS(null, "ry", "3");
                        box.setAttributeNS(null, "width", "0");
                        box.setAttributeNS(null, "height", "0");
                    }
                    group.appendChild(box);
                }
                {
                    Element text = doc.createElementNS(svgNS, "text");
                    text.setAttributeNS(null, "id", "CrossplotPopupText");
                    {
                        text.setAttributeNS(null, "id", "CrossplotPopupText");
                        text.setAttributeNS(null, "style", ImageGenerator.CP_PLOT_POPUP_TEXT_STYLE);
                        text.setAttributeNS(null, "font-size", Integer.toString(TEXT_SIZE));
                        text.setAttributeNS(null, "x", Integer.toString(TEXT_SIZE));
                        text.setAttributeNS(null, "y", Integer.toString(TEXT_SIZE + 10));
                        {
                            Element title = doc.createElementNS(svgNS, "tspan");
                            title.setAttributeNS(null, "id", "PopupTextTitle");
                            title.appendChild(doc.createTextNode(""));
                            title.setAttributeNS(null, "font-weight", "bold");
                            title.setAttributeNS(null, "x", Integer.toString(TEXT_SIZE));
                            text.appendChild(title);
                        }
                        {
                            Element note = doc.createElementNS(svgNS, "tspan");
                            note.setAttributeNS(null, "id", "PopupTextNote");
                            note.appendChild(doc.createTextNode(""));
                            note.setAttributeNS(null, "x", Integer.toString(TEXT_SIZE));
                            note.setAttributeNS(null, "dy", Double.toString(1.5 * TEXT_SIZE));
                            text.appendChild(note);
                        }
                    }
                    group.appendChild(text);
                }
                cpGroup.appendChild(group);
            }
            return group;
        }
    }

    public Element drawTimeLine(double x1, double y1, double x2, double y2, double minY, double maxY, double topAge, double baseAge, double vertScale, String style) {
        Element line = doc.createElementNS(svgNS, "line");
        line.setAttributeNS(null, "id", "timeline");
        line.setAttributeNS(null, "x1", round(x1));
        line.setAttributeNS(null, "y1", round(y1));
        line.setAttributeNS(null, "x2", round(x2));
        line.setAttributeNS(null, "y2", round(y2));
        line.setAttributeNS(null, "minY", round(minY));
        line.setAttributeNS(null, "maxY", round(maxY));
        line.setAttributeNS(null, "topAge", round(topAge));
        line.setAttributeNS(null, "baseAge", round(baseAge));
        line.setAttributeNS(null, "vertScale", round(vertScale));
        
        Element line_up = doc.createElementNS(svgNS, "line");
        line_up.setAttributeNS(null, "id", "timeline_up");
        line_up.setAttributeNS(null, "x1", round(x1));
        line_up.setAttributeNS(null, "y1", round(y1));
        line_up.setAttributeNS(null, "x2", round(x2));
        line_up.setAttributeNS(null, "y2", round(y2));
        line_up.setAttributeNS(null, "minY", round(minY));
        line_up.setAttributeNS(null, "maxY", round(maxY));
        line_up.setAttributeNS(null, "topAge", round(topAge));
        line_up.setAttributeNS(null, "baseAge", round(baseAge));
        line_up.setAttributeNS(null, "vertScale", round(vertScale));
        
        Element line_down = doc.createElementNS(svgNS, "line");
        line_down.setAttributeNS(null, "id", "timeline_down");
        line_down.setAttributeNS(null, "x1", round(x1));
        line_down.setAttributeNS(null, "y1", round(y1));
        line_down.setAttributeNS(null, "x2", round(x2));
        line_down.setAttributeNS(null, "y2", round(y2));
        line_down.setAttributeNS(null, "minY", round(minY));
        line_down.setAttributeNS(null, "maxY", round(maxY));
        line_down.setAttributeNS(null, "topAge", round(topAge));
        line_down.setAttributeNS(null, "baseAge", round(baseAge));
        line_down.setAttributeNS(null, "vertScale", round(vertScale));
        
        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }
        curElement.appendChild(line);
        curElement.appendChild(line_up);
        curElement.appendChild(line_down);
        
        return line;
    }
    
    public void removeTimeline() {
        timeline = doc.getElementById("timeline");
        timelabel = doc.getElementById("TimeLineLabel");
        curElement.removeChild(timelabel);
        curElement.removeChild(timeline);
    }

    // addTimeline currently does nothing
    public void addTimeline() {
        curElement.appendChild(timelabel);
        curElement.appendChild(timeline);
    }
    
    // Adds timelabel to plot
    public Element drawTimeLabel() {
        Element label = doc.createElementNS(svgNS, "text");
        Text t = doc.createTextNode("0");
        label.setAttributeNS(null, "id", "TimeLineLabel");
        label.setAttributeNS(null, "x", "0");
        label.setAttributeNS(null, "y", "0");
        label.setAttributeNS(null, "style", "fill-opacity: 0;");
        label.appendChild(t);
        curElement.appendChild(label);
        
        Element labelup = doc.createElementNS(svgNS, "text");
        Text tup = doc.createTextNode("0");
        labelup.setAttributeNS(null, "id", "TimeLineLabelUp");
        labelup.setAttributeNS(null, "x", "0");
        labelup.setAttributeNS(null, "y", "0");
        labelup.setAttributeNS(null, "style", "fill-opacity: 0;");
        labelup.appendChild(tup);
        curElement.appendChild(labelup);
        
        Element labeldown = doc.createElementNS(svgNS, "text");
        Text tdown = doc.createTextNode("0");
        labeldown.setAttributeNS(null, "id", "TimeLineLabelDown");
        labeldown.setAttributeNS(null, "x", "0");
        labeldown.setAttributeNS(null, "y", "0");
        labeldown.setAttributeNS(null, "style", "fill-opacity: 0;");
        labeldown.appendChild(tdown);
        curElement.appendChild(labeldown);
        
        return label;
    }

    public Element drawTimeLineX(double x1, double y1, double x2, double y2, double minX, double maxX, double topAge, double baseAge, double vertScale, String style) {
        Element line = doc.createElementNS(svgNS, "line");
        line.setAttributeNS(null, "id", "timelineX");
        line.setAttributeNS(null, "x1", round(x1));
        line.setAttributeNS(null, "y1", round(y1));
        line.setAttributeNS(null, "x2", round(x2));
        line.setAttributeNS(null, "y2", round(y2));
        line.setAttributeNS(null, "minX", round(minX));
        line.setAttributeNS(null, "maxX", round(maxX));
        line.setAttributeNS(null, "topAge", round(topAge));
        line.setAttributeNS(null, "baseAge", round(baseAge));
        line.setAttributeNS(null, "topLimit", round(topAge));
        line.setAttributeNS(null, "baseLimit", round(baseAge));
        line.setAttributeNS(null, "vertScale", round(vertScale));
        Element cpGroup = doc.getElementById("CrossPlotTimeLines");
        cpGroup.appendChild(line);
        return line;
    }

    public Element drawTimeLabelX() {
        Element label = doc.createElementNS(svgNS, "text");
        Text t = doc.createTextNode("0");
        label.setAttributeNS(null, "id", "TimeLineLabelX");
        label.setAttributeNS(null, "x", "0");
        label.setAttributeNS(null, "y", "0");
        label.appendChild(t);
        Element cpGroup = doc.getElementById("CrossPlotTimeLabels");
        cpGroup.appendChild(label);
        return label;
    }

    public Element drawTimeLineY(double x1, double y1, double x2, double y2, double minY, double maxY, double topAge, double baseAge, double vertScale, String style) {
        Element line = doc.createElementNS(svgNS, "line");
        line.setAttributeNS(null, "id", "timelineY");
        line.setAttributeNS(null, "x1", round(x1));
        line.setAttributeNS(null, "y1", round(y1));
        line.setAttributeNS(null, "x2", round(x2));
        line.setAttributeNS(null, "y2", round(y2));
        line.setAttributeNS(null, "minY", round(minY));
        line.setAttributeNS(null, "maxY", round(maxY));
        line.setAttributeNS(null, "topAge", round(topAge));
        line.setAttributeNS(null, "baseAge", round(baseAge));
        line.setAttributeNS(null, "topLimit", round(topAge));
        line.setAttributeNS(null, "baseLimit", round(baseAge));
        line.setAttributeNS(null, "vertScale", round(vertScale));
        Element cpGroup = doc.getElementById("CrossPlotTimeLines");
        cpGroup.appendChild(line);
        return line;
    }

    public Element drawTimeLabelY() {
        Element label = doc.createElementNS(svgNS, "text");
        Text t = doc.createTextNode("0");
        label.setAttributeNS(null, "id", "TimeLineLabelY");
        label.setAttributeNS(null, "x", "0");
        label.setAttributeNS(null, "y", "0");
        label.appendChild(t);
        Element cpGroup = doc.getElementById("CrossPlotTimeLabels");
        cpGroup.appendChild(label);
        return label;
    }

    // Draw Chart Lines
    public Element drawLine(double x1, double y1, double x2, double y2, String style) {
        Element line = doc.createElementNS(svgNS, "line");
        line.setAttributeNS(null, "x1", round(x1));
        line.setAttributeNS(null, "y1", round(y1));
        line.setAttributeNS(null, "x2", round(x2));
        line.setAttributeNS(null, "y2", round(y2));
        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(line);
        return line;
    }

    public Element drawLineYear(double x1, double y1, double x2, double y2, String style, double baseY) {
        return drawLine(x1, (y1 - settings.topAge) * settings.unitsPerMY + baseY, x2, (y2 - settings.topAge) * settings.unitsPerMY + baseY, style);
    }

    /**
     * Method to draw element based on String input
     *
     * @param x                   X location of picture
     * @param y                   Y location of picture
     * @param width               Width of the picture
     * @param height              Height of the picture
     * @param url                 The URL
     * @param preserveAspectRatio indicates whether aspectRatio to be Preserved
     * @return Image Element
     */
    public Element drawImage(double x, double y, double width, double height, java.net.URL url, String preserveAspectRatio) {
        return this.drawImage(x, y, width, height, url.toString(), preserveAspectRatio);
    }

    /**
     * Method to draw element based on URL input
     *
     * @param x                   X location of picture
     * @param y                   Y location of picture
     * @param width               Width of the picture
     * @param height              Height of the picture
     * @param imagePath           The path of the image
     * @param preserveAspectRatio indicates whether aspectRatio to be Preserved
     * @return Image Element
     */
    public Element drawImage(double x, double y, double width, double height, String imagePath, String preserveAspectRatio) {
        Element image = doc.createElementNS(svgNS, "image");
        image.setAttributeNS(null, "x", round(x));
        image.setAttributeNS(null, "y", round(y));
        image.setAttributeNS(null, "width", round(width));
        image.setAttributeNS(null, "height", round(height));

        // see if there's anything to draw
        if (imagePath == null || imagePath.equals("")) {
            Debug.print("Image path is empty!");
            return null;
        }

        Debug.print("drawing image: " + imagePath);

        // get the file type
        String filename = imagePath.toString();
        String extension = FileUtils.getExtension(filename);
        String mime = null;


        image.setAttributeNS(null, "id", FileUtils.getName(filename));

        if (extension == null) {
            return null;
        } else if (extension.compareToIgnoreCase("png") == 0) {
            mime = "image/png";
        } else if (extension.compareToIgnoreCase("jpg") == 0
                || extension.compareToIgnoreCase("jpeg") == 0
                || extension.compareToIgnoreCase("jpe") == 0) {
            mime = "image/jpeg";
        } else if (extension.compareToIgnoreCase("svg") == 0) {
            mime = "image/svg+xml";
        }
        // GIF is not supported by SVG or Batik.

        if (mime == null) {
            Debug.print("Unable to determine mime type for image " + imagePath);
            return null;
        }


////        image.setAttributeNS(null, "preserveAspectRatio", preserveAspectRatio);

        try {
            image.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, "xlink:href", "data:" + mime + ";base64," + util.Base64.encodeFromFile(filename));

            curElement.appendChild(image);

            return image;
        } catch (Exception e) {
            // this is mostly for invalid filenames on the image
            Debug.print("Unable to set attribute on image or append child on image " + imagePath);
            return null;
        }
    }

    public Element drawRect(double x, double y, double width, double height, String style) {
        Element rect = doc.createElementNS(svgNS, "rect");
        rect.setAttributeNS(null, "x", round(x));
        rect.setAttributeNS(null, "y", round(y));
        rect.setAttributeNS(null, "width", round(width));
        rect.setAttributeNS(null, "height", round(height));
        if (style != null) {
            rect.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(rect);
        return rect;
    }

    public Element drawCircleYear(double x1, double y1, double baseY, double radius, String style, datastore.RangeColumn.RangePoint point, boolean childOff) {
        Element circ = doc.createElementNS(svgNS, "circle");


        circ.setAttributeNS(null, "cx", round(x1));
        circ.setAttributeNS(null, "cy", round((y1 - settings.topAge) * settings.unitsPerMY + baseY));
        circ.setAttributeNS(null, "r", round(radius));
        if (style != null) {
            circ.setAttributeNS(null, "style", style);
        }

        if (childOff) {
            Element animate = doc.createElementNS(svgNS, "animate");
            animate.setAttributeNS(null, "attributeType", "CSS");
            animate.setAttributeNS(null, "attributeName", "fill");
            animate.setAttributeNS(null, "values", "white; red; black; yellow");
            animate.setAttributeNS(null, "dur", "1s");
            animate.setAttributeNS(null, "repeatCount", "indefinite");

            circ.appendChild(animate);
        }
        EventTarget targ = (EventTarget) circ;
        targ.addEventListener("click", new ClickListener(), false);
        if (branchNodeList == null) {
            branchNodeList = new HashMap<EventTarget, datastore.RangeColumn.RangePoint>();
        }

        branchNodeList.put(targ, point);

        //targ.addEventListener("click", point, false);
        curElement.appendChild(circ);

        return circ;
    }
    
    public Element drawCrossYear(double x1, double y1, double baseY, double radius, String style, datastore.RangeColumn.RangePoint point, boolean childOff) {
    	double cx = x1;
    	double cy = (y1 - settings.topAge) * settings.unitsPerMY + baseY;
    	
    	double angle = 45 * Math.PI / 180.0;
    	double p_1_x_1 = cx + radius * Math.cos(angle); 
    	double p_1_y_1 = cy + radius * Math.sin(angle); 

    	double p_2_x_1 = cx - radius * Math.cos(angle); 
    	double p_2_y_1 = cy - radius * Math.sin(angle); 

    	Element line1 = drawLine(p_1_x_1, p_1_y_1, p_2_x_1, p_2_y_1, style);

    	double p_3_x_1 = cx + radius * Math.cos(angle); 
    	double p_3_y_1 = cy - radius * Math.sin(angle); 

    	double p_4_x_1 = cx - radius * Math.cos(angle); 
    	double p_4_y_1 = cy + radius * Math.sin(angle); 

    	Element line2 = drawLine(p_3_x_1, p_3_y_1, p_4_x_1, p_4_y_1, style);

        return null;
    }

    public class ClickListener implements EventListener {

        public void handleEvent(Event evt) {
            DOMMouseEvent elEvt = (DOMMouseEvent) evt;
            EventTarget clickedElement = evt.getTarget();
            datastore.RangeColumn.RangePoint point = branchNodeList.get(clickedElement);

            short rightLeft = elEvt.getButton();
            if (rightLeft == 0) {

                //This part of code is used to toggle the branchponts of Family Tree.
                //If the branch is to be included then the priority check is to be
                //overriden because the user wants to display the branch.
                //point.childRange.setNotIncludeBranch();
                if (point.childRange.branchedAsLeftOrRight != null) {
                	if (point.childRange.branchedAsLeftOrRight == "left") {
                		datastore.RangeColumn.circleDrawingLeftOrRight.put(point.rcd.branchTo, "left");
                	} else {
                		datastore.RangeColumn.circleDrawingLeftOrRight.put(point.rcd.branchTo,"right");
                	}
                }
                boolean setResetBranch = point.childRange.getNotIncludeBranch();
                point.setResetIncludeBranch(setResetBranch);

                settingsRefFamilyTree.tsObj.generateImage();
            } else if (rightLeft == 2) {
                int xCoordinate = elEvt.getClientX();
                int yCoordinate = elEvt.getClientY();
                point.handlePopUps(xCoordinate, yCoordinate);
            }


        }
    }

    public Element drawCircle(double x, double y, double radius, String style) {
        Element circ = doc.createElementNS(svgNS, "circle");
        circ.setAttributeNS(null, "cx", round(x));
        circ.setAttributeNS(null, "cy", round(y));
        circ.setAttributeNS(null, "r", round(radius));
        if (style != null) {
            circ.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(circ);
        return circ;
    }

    public Element drawRectYear(double x, double y, double width, double height, String style, double baseY) {
        return drawRect(x, (y - settings.topAge) * settings.unitsPerMY + baseY, width, height * settings.unitsPerMY, style);
    }

    
    public Element drawPolygon(double[] x, double[] y, String style, String popup, DataColumn.FileInfo colFileInfo) {
        Element e = drawPolygon(x, y, style);
        if (popup != null) {
            doPopupThings(e, popup, colFileInfo);
        }
        return e;
    }
    
    public Element drawArrowWithPolygon(double[] x, double[]y, String style, double baseY) {
    	for (int i = 0; i < y.length; i++) {
    		y[i] = (y[i] - settings.topAge) * settings.unitsPerMY + baseY;
    	}
    	Element e = drawPolygon(x, y, style);
    	
    	return e;
    }

    public Element drawPolygon(double[] x, double[] y, String style) {
        int numPoints = Math.min(x.length, y.length);

        if (numPoints == 2) {
            return drawLine(x[0], y[0], x[1], y[1], style);
        }

        if (numPoints < 2) {
            return null; // can't draw a polygon with 1 or less points
        }
        Element line = doc.createElementNS(svgNS, "polygon");
        String points = "";
        for (int i = 0; i < numPoints; i++) {
            points += round(x[i]) + "," + round(y[i]) + " ";
        }
        line.setAttributeNS(null, "points", points);

        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(line);
        return line;
    }

    public Element drawPolyline(double[] x, double[] y, String style) {
        int numPoints = Math.min(x.length, y.length);

        if (numPoints == 2) {
            return drawLine(x[0], y[0], x[1], y[1], style);
        }

        if (numPoints < 2) {
            return null; // can't draw a polyline with 1 or less points
        }
        Element line = doc.createElementNS(svgNS, "polyline");
        String points = "";
        for (int i = 0; i < numPoints; i++) {
            points += round(x[i]) + "," + round(y[i]) + " ";
        }
        line.setAttributeNS(null, "points", points);

        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(line);
        return line;
    }

    public Element drawSmoothVerticalPolyline(double[] x, double[] y, boolean[] lineTo, String style) {
        int numPoints = Math.min(x.length, y.length);

        if (numPoints == 2) {
            return drawLine(x[0], y[0], x[1], y[1], style);
        }

        if (numPoints < 2) {
            return null; // can't draw a polyline with 1 or less points
        }
        Element line = doc.createElementNS(svgNS, "path");
        String points;
        // starting point
        points = "M" + round(x[0]) + "," + round(y[0]);
        // the Beziers
        for (int i = 1; i < numPoints; i++) {
        	//System.out.println(x[i]);
            if (lineTo[i]) {
                // draw a straight line
                points += " L" + round(x[i]) + "," + round(y[i]);

            } else {
                double[] c1 = getControlPointForVerticalCurves(x, y, lineTo, i - 1, 1);
                double[] c2 = getControlPointForVerticalCurves(x, y, lineTo, i, -1);
                points += " C" + round(c1[0]) + "," + round(c1[1])
                        + " " + round(c2[0]) + "," + round(c2[1])
                        + " " + round(x[i]) + "," + round(y[i]);
            }
        }
        line.setAttributeNS(null, "d", points);

        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(line);
        return line;
    }
    

    /**
     * @param x
     * @param y
     * @param i
     * @param dir positive means towards i+1, negative means towards i-1
     * @return double[2]: index 0 is the x, index 1 is the y
     */
    protected double[] getControlPointForVerticalCurves(double[] x, double[] y, boolean[] lineTo, int i, int dir) {
        double derivative;
        double[] ret = new double[2];

        if (i == 0 || lineTo[i]) { // first point, dir assumed to be 1
            Vector2D v = getControlPoint(x, y, lineTo, i, dir, false, true);

            ret[1] = v.y;
            ret[0] = v.x;

            //derivative = (x[1]-x[0])/(y[1]-y[0]);

            //ret[1] = y[i]*(1-CONTROL_POINT_LENGTH) + y[i+1]*CONTROL_POINT_LENGTH;
            //ret[0] = x[i]*(1-CONTROL_POINT_LENGTH) + x[i+1]*CONTROL_POINT_LENGTH;
            return ret;
        }

        if (i >= (x.length - 1) || lineTo[i]) { // last point, dir assumed to be -1
            Vector2D v = getControlPoint(x, y, lineTo, i, dir, false, true);

            ret[1] = v.y;
            ret[0] = v.x;

            //ret[1] = y[i]*(1-CONTROL_POINT_LENGTH) + y[i-1]*CONTROL_POINT_LENGTH;
            //ret[0] = x[i]*(1-CONTROL_POINT_LENGTH) + x[i-1]*CONTROL_POINT_LENGTH;
            return ret;
        }

        double prevx = x[i - 1], prevy = y[i - 1];
        double nextx = x[i + 1], nexty = y[i + 1];

        double prevdiff = prevx - x[i];
        double nextdiff = nextx - x[i];

        if (prevdiff * nextdiff > 0) {
            derivative = Double.POSITIVE_INFINITY; // this point is either a maximum or a minimum
        } else {
            derivative = (nextx - prevx) / (nexty - prevy);

            double derivativeNext = (nextx - x[i]) / (nexty - y[i]);
            double derivativePrev = (x[i] - prevx) / (y[i] - prevy);

            if (derivative < 0) {
                derivative = Math.max(derivative, derivativeNext);
            }
            if (derivative > 0) {
                derivative = Math.max(derivative, derivativePrev);
            }
        }

        if (dir > 0) {
            // towards i+1
            if (Double.isInfinite(derivative)) {
                ret[1] = y[i] * (1 - CONTROL_POINT_LENGTH) + y[i + 1] * CONTROL_POINT_LENGTH;
                ret[0] = x[i];
            } else {
                Vector2D v = getControlPoint(x, y, lineTo, i, dir, false, true);

                ret[1] = v.y;
                ret[0] = v.x;
                //ret[1] = y[i]*(1-CONTROL_POINT_LENGTH) + y[i+1]*CONTROL_POINT_LENGTH;
                //ret[0] = x[i] + derivative * (ret[1] - y[i]);
            }
        } else {
            // towards i-1
            if (Double.isInfinite(derivative)) {
                ret[1] = y[i] * (1 - CONTROL_POINT_LENGTH) + y[i - 1] * CONTROL_POINT_LENGTH;
                ret[0] = x[i];
            } else {
                Vector2D v = getControlPoint(x, y, lineTo, i, dir, false, true);

                ret[1] = v.y;
                ret[0] = v.x;
                //ret[1] = y[i]*(1-CONTROL_POINT_LENGTH) + y[i-1]*CONTROL_POINT_LENGTH;
                //ret[0] = x[i] + derivative * (ret[1] - y[i]);
            }
        }
        return ret;
    }

    public Element drawSmoothPolyline(double[] x, double[] y, boolean[] sharp, String style, boolean closed) {	
    	int numPoints = Math.min(x.length, y.length);

        if (numPoints == 2) {
            return drawLine(x[0], y[0], x[1], y[1], style);
        }

        if (numPoints < 2) {
            return null; // can't draw a polyline with 1 or less points
        }
        Element line = doc.createElementNS(svgNS, "path");
        String points;
        // starting point
        points = "M" + round(x[0]) + "," + round(y[0]);
        // the Beziers
        for (int i = 1; i < numPoints || (closed && i <= numPoints); i++) {
            Vector2D c1 = getControlPoint(x, y, sharp, i - 1, 1, closed, false);
            Vector2D c2 = getControlPoint(x, y, sharp, i % numPoints, -1, closed, false);
            points += " C" + round(c1.x) + "," + round(c1.y)
                    + " " + round(c2.x) + "," + round(c2.y)
                    + " " + round(x[i % numPoints]) + "," + round(y[i % numPoints]);

            //drawCircle(c1.x, c1.y, 0.5, "stroke-width: 0; fill: green;");
            //drawCircle(c2.x, c2.y, 0.5, "stroke-width: 0; fill: blue;");
            //System.out.println("x: " + c1.x + " y: " + c1.y);
            //System.out.println("x: " + c2.x + " y: " + c2.y);
            //System.out.println();
        }
        if (closed) {
            points += "z";
        }
        line.setAttributeNS(null, "d", points);

        if (style != null) {
            line.setAttributeNS(null, "style", style);
        }

        curElement.appendChild(line);
        return line;
    }

    /**
     * @param x
     * @param y
     * @param i
     * @param dir positive means towards i+1, negative means towards i-1
     * @return the control point
     */
    protected Vector2D getControlPoint(double[] x, double[] y, boolean[] sharpA, int i, int dir, boolean closed, boolean forVertical) {
        boolean sharp = sharpA[i];
        if (i == 0 && !closed) {
            dir = 1;
            sharp = true;
        }
        if (i == x.length - 1 && !closed) {
            dir = -1;
            sharp = true;
        }

        int previ = (i - 1 + x.length) % x.length;
        int nexti = (i + 1) % x.length;

        Vector2D prev = new Vector2D(x[previ] - x[i], y[previ] - y[i]);
        Vector2D next = new Vector2D(x[nexti] - x[i], y[nexti] - y[i]);

        double prevLength = prev.length();
        double nextLength = next.length();

        // see if the two points are on top of each other
        // if they are, punish the user with a squiggle
        if (NumberUtils.isEqual(prevLength, 0)) {
            prev = new Vector2D(0, 1);
            prevLength = 1;
        }
        if (NumberUtils.isEqual(nextLength, 0)) {
            next = new Vector2D(1, 0);
            nextLength = 1;
        }

        if (sharp) {
            if (dir > 0) {
                next.setLength(Math.min(nextLength * CONTROL_POINT_LENGTH, MAX_CONTROL_POINT_LENGTH));
                next.add(x[i], y[i]);
                return next;
            } else {
                prev.setLength(Math.min(prevLength * CONTROL_POINT_LENGTH, MAX_CONTROL_POINT_LENGTH));
                prev.add(x[i], y[i]);
                return prev;
            }
        }

        prev.normalize();
        next.normalize();

        Vector2D mid = prev.addR(next);

        Vector2D end1, end2;

        if (mid.length() > 0.01) {
            mid.normalize();
            mid.perpSlope();

            end1 = mid;
            end2 = new Vector2D(end1);
            end2.mul(-1);
        } else {
            // the two are almost at 180 degrees. poor accuracy here,
            // so calculate in another way.
            Vector2D minus = new Vector2D(next);
            minus.mul(-1);

            end1 = minus.addR(prev);
            end2 = new Vector2D(end1);
            end2.mul(-1);
        }

        if (prev.dotProduct(end1) > 0) {
            // prev = end1, next = end2
            end1.setLength(getControlPointLength(prev, next, prevLength, nextLength, forVertical)); //Math.min(prevLength * CONTROL_POINT_LENGTH, MAX_CONTROL_POINT_LENGTH));
            end2.setLength(getControlPointLength(next, prev, nextLength, prevLength, forVertical)); //Math.min(nextLength * CONTROL_POINT_LENGTH, MAX_CONTROL_POINT_LENGTH));

            end1.add(x[i], y[i]);
            end2.add(x[i], y[i]);

            if (dir > 0) {
                return end2;
            } else {
                return end1;
            }
        } else {
            // prev = end2, next = end1
            end2.setLength(getControlPointLength(prev, next, prevLength, nextLength, forVertical));
            end1.setLength(getControlPointLength(next, prev, nextLength, prevLength, forVertical)); //Math.min(nextLength * CONTROL_POINT_LENGTH, MAX_CONTROL_POINT_LENGTH));

            end1.add(x[i], y[i]);
            end2.add(x[i], y[i]);

            if (dir > 0) {
                return end1;
            } else {
                return end2;
            }
        }
    }

    protected double getControlPointLength(Vector2D thisSeg, Vector2D otherSeg, double thisSegLength, double otherSegLength, boolean forVertical) {
        double thisSegL = Math.max(thisSeg.length(), 0.0001);
        double otherSegL = Math.max(otherSeg.length(), 0.0001);
        double cos = Math.abs(thisSeg.dotProduct(otherSeg) / (thisSegL * otherSegL));
        if (!forVertical && cos < 0.5) {
            cos = 1 - cos;
        }

        double ret = Math.min(Math.max(thisSegLength * CONTROL_POINT_LENGTH * cos, MIN_CONTROL_POINT_LENGTH), MAX_CONTROL_POINT_LENGTH);
        return ret;
    }

    public void drawPoints(double[] x, double[] y, int type, boolean[] shouldDraw, Color c) {
        int numPoints = Math.min(x.length, y.length);

        if (numPoints < 1) {
            return;
        }

        if (c == null) {
            c = Color.black;
        }

        pushGrouping();
        for (int i = 0; i < numPoints; i++) {
            if (shouldDraw != null && !shouldDraw[i]) {
                continue;
            }

            switch (type) {
                case POINT_RECT:
                    drawRect(x[i] - POINT_DIMENSION / 2, y[i] - POINT_DIMENSION / 2, POINT_DIMENSION, POINT_DIMENSION, "stroke-width: 0; fill: " + Coloring.getStyleRGB(c) + ";");
                    break;

                case POINT_ROUND:
                    drawCircle(x[i], y[i], POINT_DIMENSION / 2, "stroke-width: 0; fill: " + Coloring.getStyleRGB(c) + ";");
                    break;

                default: // POINT_TICK
                    drawLine(x[i] - POINT_DIMENSION / 2, y[i], x[i] + POINT_DIMENSION / 2, y[i], "stroke-width: 1; stroke: " + Coloring.getStyleRGB(c) + "");
                    drawLine(x[i], y[i] - POINT_DIMENSION / 2, x[i], y[i] + POINT_DIMENSION / 2, "stroke-width: 1; stroke: " + Coloring.getStyleRGB(c) + "");
            }
        }

        popGrouping();
    }

    public static double getYFromYear(double y, double baseY, Settings settings) {
        return (y - settings.topAge) * settings.unitsPerMY + baseY;
    }

    /**
     * Wraps the string.
     *
     * @arg fontToUse - a font to use in wrapping calculations. Can be null if fm is supplied.
     * @arg fm - a FontMetrics objects to use. Can be null if fontToUse is supplied.
     */
    public StringWrappingInfo wrapString(String s, double windowWidth, TSCFont fontToUse, DataColumn.FileInfo fileInfo) {
        StringWrappingInfo swi = new StringWrappingInfo(g);

        swi.wrapString(new RichText(s, fileInfo), windowWidth, fontToUse);
        return swi;
    }

    public StringWrappingInfo getSWI(RichText s, TSCFont fontToUse, int orientation) {
        return new StringWrappingInfo(g, s, fontToUse, orientation);
    }

    public StringWrappingInfo getSWI(String s, TSCFont fontToUse, int orientation, DataColumn.FileInfo fileInfo) {
        return new StringWrappingInfo(g, new RichText(s, fileInfo), fontToUse, orientation);
    }

    public StringWrappingInfo getSWIOneLine(String s, TSCFont fontToUse, int orientation, DataColumn.FileInfo fileInfo) {
        StringWrappingInfo ret = new StringWrappingInfo(g, new RichText(s, fileInfo), fontToUse, orientation);
        ret.makeOneLine();
        return ret;
    }

    public Element drawString(StringWrappingInfo swi, double startx, double starty, double width, double height, int verticalPlacement) {
        return drawString(swi, startx, starty, width, height, verticalPlacement, starty, ONLY_TEXT, null);
    }

    public Element drawString(StringWrappingInfo swi, double startx, double starty, double width, double height, int verticalPlacement, double prefLoc) {
        return drawString(swi, startx, starty, width, height, verticalPlacement, prefLoc, ONLY_TEXT, null);
    }

    public Element drawStringabsolute(StringWrappingInfo swi, double startx, double starty, double baseY, double width, double height, int verticalPlacement, Color color) {
        starty = (starty - settings.topAge) * settings.unitsPerMY + baseY;
        return drawString(swi, startx, starty, width, height, verticalPlacement, starty, TEXT_AND_BACKGROUND, color);
    }

    public Element drawString(StringWrappingInfo swi, double startx, double starty, double width, double height, int verticalPlacement, double prefLoc, int background, Color backgroundColor) {
        Element text = null;
        Element tspan;
        Element textParent = curElement;
        Text t;
        double x, y;
        double verticalTop;
        double translateX = 0, translateY = 0;
        double swiHeight = swi.getHeight();
        double swiWidth = swi.getWidth();
        double drawWidth;

        // TODO: REMOVE THIS AFTER TESTING:
        //drawRect(startx, starty, width, height, "stroke-width: 1; stroke: blue; fill:none; ");

        // set up the vertical alignment
        switch (verticalPlacement) {
            case CENTER:
                verticalTop = starty + (height - swiHeight) / 2;
                break;
            case BOTTOM:
                verticalTop = starty + height - swiHeight;
                break;
            case BOTTOM_RIGHT:
                verticalTop = starty + height - swiHeight;
                break;
            case TOP_LEFT:
            case TOP:
                verticalTop = starty;
                break;
            case PREFERRED:
                verticalTop = prefLoc - (swiHeight / 2);

                // move the thing around, if needed
                if (verticalTop < starty) {
                    verticalTop = starty;
                } else if (verticalTop + swiHeight > starty + height) // this is in an else in case there isn't enough room
                {
                    verticalTop = starty + height - swiHeight;
                }


                break;
            default:
                return null;
        }
        //double verticalBase = verticalTop + swiHeight;

        translateX = startx;
        translateY = verticalTop;

        switch (swi.getRotateDegrees()) {
            case 90:
                drawWidth = swiHeight;
                translateX += swiWidth + (width - swiWidth) / 2;
                translateY -= swiHeight;
                break;
            case 180:
                drawWidth = width;
                translateX += swiWidth;
                translateY -= swiHeight;
                break;
            case 270:
                drawWidth = swiHeight;
                translateX += (width - swiWidth) / 2;
                translateY += swiHeight;
                break;
            default: // 0
                drawWidth = width;
                break;
        }

        String baseTransform = "";
        if (swi.getRotateDegrees() != 0) {
            baseTransform = "translate(" + round(translateX) + ", " + round(translateY) + ") "
                    + "rotate(" + swi.getRotateDegrees() + ", " + round(0) + ", " + round(0) + ") ";
        } else {
            baseTransform = "translate(" + round(translateX) + ", " + round(translateY) + ") ";
        }


        // set up the background
        if (includesBackground(background) && false) {
            //pushGrouping(); // Taking this out to see if this is what's causing each label to be a layer in Adobe Illustrator

            curElement.setAttributeNS(null, "transform", baseTransform);
        }

        //////////////
        //Element testE = doc.createElementNS(svgNS, "a");
        //testE.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, "href", "http://www.google.com");
        //curElement.appendChild(testE);
        //pushElement(testE);
        //////////////

        // set up the main text element
        text = doc.createElementNS(svgNS, "text");
        text.setAttributeNS(null, "x", round(0));
        text.setAttributeNS(null, "y", round(0));

        text.setAttributeNS(null, "transform", baseTransform);

        String style = "";
        if (swi.font != null) {
            style += swi.font.getSVGStyle();
        }

        String color = getTextColor(backgroundColor);
        if (color == "") {
            style += " fill: " + swi.font.getSVGStyle() + ";";
        } else {
            style += " fill: " + color + ";";
        }
        text.setAttributeNS(null, "style", style);

        // add the individual tspan elements which actually hold each line
        y = 0;
        for (int i = 0; i < swi.getNumLines(); i++) {
            // find the x,y coordinates
            if (verticalPlacement == TOP_LEFT) {
                x = 0;
            } else if (verticalPlacement == BOTTOM_RIGHT) {
                x = (drawWidth - swi.widths[i]);
            } else {
                x = (drawWidth - swi.widths[i]) / 2;
            }

            if (i != 0) {
                y += swi.descents[i - 1];
            }
            y += swi.lineHeights[i] - swi.descents[i];

            RichText.Line line = swi.s.getLine(i);
            for (int j = 0; j < line.elements.size(); j++) {
                RichText.Element elem = (RichText.Element) line.elements.get(j);
                // DRAW IMAGES
                if (elem instanceof RichText.ImageTag) {
                    RichText.ImageTag imgTag = (RichText.ImageTag) elem;
                    if (imgTag.toString().contains("humerosa"))
                    	System.out.println();
                    //System.out.println("image from " + imgTag.source);
                    if (!imgTag.isValid) {
                        continue;
                    }

                    String preserveAspectRatio = "none";
                    Element curElementSave = curElement;
                    curElement = textParent;
                    Element imgElement = drawImage(x, y - imgTag.aboveText, imgTag.width, imgTag.height, imgTag.source, preserveAspectRatio);
                    if (imgElement != null) {
                        imgElement.setAttributeNS(null, "transform", baseTransform);
                    }
                    curElement = curElementSave;

                    x += imgTag.width;
                    continue;
                } else if (!(elem instanceof RichText.StringElement)) {
                    continue;
                }
                //System.out.println(swi.s[i] + ":  DrawWidth: " + drawWidth + "  linewidth: " + swi.widths[i]/2 + " x: " + x);

                /*
                if (includesBackground(background)) {
                Element rect = drawRect(x, y-(swi.lineHeights[i] - swi.descents[i]), swi.widths[i], swi.lineHeights[i], Coloring.getStyle(backgroundColor));
                rect.setAttributeNS(null, "rx", "2");
                rect.setAttributeNS(null, "ry", "2");
                
                ((Element)(curElement.getLastChild())).setAttributeNS(null, "transform", baseTransform);
                }*/

                tspan = doc.createElementNS(svgNS, "tspan");
                tspan.setAttributeNS(null, "x", round(x));
                tspan.setAttributeNS(null, "y", round(y));

                t = doc.createTextNode(elem.visibleString());
                pushElement(tspan);
                int numPops = 1;
                Iterator tagIter = ((RichText.StringElement) elem).openTags.iterator();
                for (; tagIter.hasNext(); numPops++) {
                    RichText.BeginTagElement beginTag = (RichText.BeginTagElement) tagIter.next();

                    Element tagE;
                    tagE = doc.createElementNS(svgNS, beginTag.tagName);

                    if (beginTag.styleChanges != null) {
                        tagE.setAttribute("style", beginTag.styleChanges);
                    }

                    // get the rest of the attributes
                    Iterator attribIter = beginTag.attributes.keySet().iterator();
                    while (attribIter.hasNext()) {
                        String key = attribIter.next().toString();
                        String value = beginTag.attributes.get(key).toString();

                        // if this is a link, then we need to process it.
                        // if the link is relative then it will be made relative to the datafile.
                        if (linkProc != null && LinkProcessor.shouldProcess(beginTag.tagName, key)) {
                            value = linkProc.processLink(value);
                        }

                        // TODO namespace more generic
                        tagE.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, key, value);
                    }
                    // ok, done
                    curElement.appendChild(tagE);
                    pushElement(tagE);
                }

                curElement.appendChild(t);

                for (int k = 0; k < numPops; k++) {
                    popGrouping();
                }

                text.appendChild(tspan);

                x += elem.width;
            }
        }

        if (includesBackground(background) && backgroundColor != null) {
            //set up a copy
            Element outline = this.addElementCopy(text, true);
            String outlineStyle = "";
            if (swi.font != null) {
                outlineStyle += swi.font.getSVGStyleNoColor();
            }

            outlineStyle += " fill: none; stroke: " + Coloring.getColorStyle(backgroundColor) + "; stroke-width: 4; stroke-linecap:butt; stroke-linejoin:round;";

            outline.setAttributeNS(null, "style", outlineStyle);
        }
        curElement.appendChild(text);

        //////////////
        //popGrouping();
        //////////////

        return text;
    }
    
    public Element drawString(StringWrappingInfo swi, double startx, double starty, double width, double height, int verticalPlacement, double prefLoc, int background, Color backgroundColor, boolean resize) {
        Element text = null;
        Element tspan;
        Element textParent = curElement;
        Text t;
        double x, y;
        double verticalTop;
        double translateX = 0, translateY = 0;
        double swiHeight = swi.getHeight();
        double swiWidth = swi.getWidth();
        double drawWidth;

        // TODO: REMOVE THIS AFTER TESTING:
        //drawRect(startx, starty, width, height, "stroke-width: 1; stroke: blue; fill:none; ");

        // set up the vertical alignment
        switch (verticalPlacement) {
            case CENTER:
                verticalTop = starty + (height - swiHeight) / 2;
                break;
            case BOTTOM:
                verticalTop = starty + height - swiHeight;
                break;
            case BOTTOM_RIGHT:
                verticalTop = starty + height - swiHeight;
                break;
            case TOP_LEFT:
            case TOP:
                verticalTop = starty;
                break;
            case PREFERRED:
                verticalTop = prefLoc - (swiHeight / 2);

                // move the thing around, if needed
                if (verticalTop < starty) {
                    verticalTop = starty;
                } else if (verticalTop + swiHeight > starty + height) // this is in an else in case there isn't enough room
                {
                    verticalTop = starty + height - swiHeight;
                }


                break;
            default:
                return null;
        }
        //double verticalBase = verticalTop + swiHeight;

        translateX = startx;
        translateY = verticalTop;

        switch (swi.getRotateDegrees()) {
            case 90:
                drawWidth = swiHeight;
                translateX += swiWidth + (width - swiWidth) / 2;
                translateY -= swiHeight;
                break;
            case 180:
                drawWidth = width;
                translateX += swiWidth;
                translateY -= swiHeight;
                break;
            case 270:
                drawWidth = swiHeight;
                translateX += (width - swiWidth) / 2;
                translateY += swiHeight;
                break;
            default: // 0
                drawWidth = width;
                break;
        }

        String baseTransform = "";
        if (swi.getRotateDegrees() != 0) {
            baseTransform = "translate(" + round(translateX) + ", " + round(translateY) + ") "
                    + "rotate(" + swi.getRotateDegrees() + ", " + round(0) + ", " + round(0) + ") ";
        } else {
            baseTransform = "translate(" + round(translateX) + ", " + round(translateY) + ") ";
        }


        // set up the background
        if (includesBackground(background) && false) {
            //pushGrouping(); // Taking this out to see if this is what's causing each label to be a layer in Adobe Illustrator

            curElement.setAttributeNS(null, "transform", baseTransform);
        }

        //////////////
        //Element testE = doc.createElementNS(svgNS, "a");
        //testE.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, "href", "http://www.google.com");
        //curElement.appendChild(testE);
        //pushElement(testE);
        //////////////

        // set up the main text element
        text = doc.createElementNS(svgNS, "text");
        text.setAttributeNS(null, "x", round(0));
        text.setAttributeNS(null, "y", round(0));

        text.setAttributeNS(null, "transform", baseTransform);

        String style = "";
        if (swi.font != null) {
            style += swi.font.getSVGStyle();
        }

        String color = getTextColor(backgroundColor);
        if (color == "") {
            style += " fill: " + swi.font.getSVGStyle() + ";";
        } else {
            style += " fill: " + color + ";";
        }
        text.setAttributeNS(null, "style", style);

        // add the individual tspan elements which actually hold each line
        y = 0;
        for (int i = 0; i < swi.getNumLines(); i++) {
            // find the x,y coordinates
            if (verticalPlacement == TOP_LEFT) {
                x = 0;
            } else if (verticalPlacement == BOTTOM_RIGHT) {
                x = (drawWidth - swi.widths[i]);
            } else {
                x = (drawWidth - swi.widths[i]) / 2;
            }

            if (i != 0) {
                y += swi.descents[i - 1];
            }
            y += swi.lineHeights[i] - swi.descents[i];

            RichText.Line line = swi.s.getLine(i);
            for (int j = 0; j < line.elements.size(); j++) {
                RichText.Element elem = (RichText.Element) line.elements.get(j);
                // DRAW IMAGES
                if (elem instanceof RichText.ImageTag) {
                    RichText.ImageTag imgTag = (RichText.ImageTag) elem;
                    if (imgTag.toString().contains("humerosa"))
                    	System.out.println();
                    //System.out.println("image from " + imgTag.source);
                    if (!imgTag.isValid) {
                        continue;
                    }

                    String preserveAspectRatio = "none";
                    Element curElementSave = curElement;
                    curElement = textParent;

                    Element imgElement = drawImage(x - imgTag.width / 128, y - imgTag.aboveText - imgTag.height / 128, imgTag.width / 2, imgTag.height, imgTag.source, preserveAspectRatio);
                    if (imgElement != null) {
                        imgElement.setAttributeNS(null, "transform", baseTransform);
                    }
                    curElement = curElementSave;

                    x += imgTag.width;
                    continue;
                } else if (!(elem instanceof RichText.StringElement)) {
                    continue;
                }
                //System.out.println(swi.s[i] + ":  DrawWidth: " + drawWidth + "  linewidth: " + swi.widths[i]/2 + " x: " + x);

                /*
                if (includesBackground(background)) {
                Element rect = drawRect(x, y-(swi.lineHeights[i] - swi.descents[i]), swi.widths[i], swi.lineHeights[i], Coloring.getStyle(backgroundColor));
                rect.setAttributeNS(null, "rx", "2");
                rect.setAttributeNS(null, "ry", "2");
                
                ((Element)(curElement.getLastChild())).setAttributeNS(null, "transform", baseTransform);
                }*/

                tspan = doc.createElementNS(svgNS, "tspan");
                tspan.setAttributeNS(null, "x", round(x));
                tspan.setAttributeNS(null, "y", round(y));

                t = doc.createTextNode(elem.visibleString());
                pushElement(tspan);
                int numPops = 1;
                Iterator tagIter = ((RichText.StringElement) elem).openTags.iterator();
                for (; tagIter.hasNext(); numPops++) {
                    RichText.BeginTagElement beginTag = (RichText.BeginTagElement) tagIter.next();

                    Element tagE;
                    tagE = doc.createElementNS(svgNS, beginTag.tagName);

                    if (beginTag.styleChanges != null) {
                        tagE.setAttribute("style", beginTag.styleChanges);
                    }

                    // get the rest of the attributes
                    Iterator attribIter = beginTag.attributes.keySet().iterator();
                    while (attribIter.hasNext()) {
                        String key = attribIter.next().toString();
                        String value = beginTag.attributes.get(key).toString();

                        // if this is a link, then we need to process it.
                        // if the link is relative then it will be made relative to the datafile.
                        if (linkProc != null && LinkProcessor.shouldProcess(beginTag.tagName, key)) {
                            value = linkProc.processLink(value);
                        }

                        // TODO namespace more generic
                        tagE.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, key, value);
                    }
                    // ok, done
                    curElement.appendChild(tagE);
                    pushElement(tagE);
                }

                curElement.appendChild(t);

                for (int k = 0; k < numPops; k++) {
                    popGrouping();
                }

                text.appendChild(tspan);

                x += elem.width;
            }
        }

        if (includesBackground(background) && backgroundColor != null) {
            //set up a copy
            Element outline = this.addElementCopy(text, true);
            String outlineStyle = "";
            if (swi.font != null) {
                outlineStyle += swi.font.getSVGStyleNoColor();
            }

            outlineStyle += " fill: none; stroke: " + Coloring.getColorStyle(backgroundColor) + "; stroke-width: 4; stroke-linecap:butt; stroke-linejoin:round;";

            outline.setAttributeNS(null, "style", outlineStyle);
        }
        curElement.appendChild(text);

        //////////////
        //popGrouping();
        //////////////

        return text;
    }

    /**
     *
     */
    public Element drawString(String s, double x, double y, String style) {
        Element text = doc.createElementNS(svgNS, "text");
        text.setAttributeNS(null, "x", round(x));
        text.setAttributeNS(null, "y", round(y));
        if (style != null) {
            text.setAttributeNS(null, "style", style);
        }

        Text t = doc.createTextNode(s);
        text.appendChild(t);

        curElement.appendChild(text);

        return text;
    }

    /**
     * Replaces a string with another string, without using the Java 1.4 specific replaceAll().
     */
    public static String replace(String s, String pat, String rep) {
        //System.out.println("replace: " + s + " (pat: " + pat + "   rep: " + rep);
        StringBuilder result = new StringBuilder();

        int start = 0;
        int end = 0;
        while ((end = s.indexOf(pat, start)) >= 0) {
            result.append(s.substring(start, end));
            result.append(rep);

            start = end + pat.length();
        }

        result.append(s.substring(start));
        return result.toString();
    }

    public Element drawStringYear(String s, double x, double y, String style, double baseY) {
        return drawString(s, x, (y - settings.topAge) * settings.unitsPerMY + baseY, style);
    }

    public Element drawStringYearPlusOffsetNoClip(String s, double x, double year, double offset, String style, double baseY) {
        double y = (year - settings.topAge) * settings.unitsPerMY + baseY + offset;
        if (y < baseY + offset) {
            y = baseY + offset;
        }
        return drawString(s, x, y, style);
    }

    public String getTextColor(Color c) {
        if (c == null) {
            return "";
        }

        double grey = c.getRed() * 0.3 + c.getGreen() * 0.59 + c.getBlue() * 0.11;
        if (grey > 120) {
            return "";
        } else {
            return "white";
        }
    }

    public String getTextColor(String backgroundStyle) {
        if (backgroundStyle == null) {
            return "black";
        }

        Pattern pat = Pattern.compile("(color|fill):\\s*[^\\;]*\\;");
        Matcher matcher = pat.matcher(backgroundStyle);

        if (!matcher.find()) {
            return "black";
        }

        String match = matcher.group();
        String color = match.replaceFirst("color:", "").replaceFirst("fill:", "").replaceAll(";", "").trim();
        Color c = Coloring.getColorFromStyle(color);

        return getTextColor(c);
    }

    public void addDeclaration(String d) {
    }

    public void addElement(Element e) {
        curElement.appendChild(e);
    }

    public Element addElementCopy(Element orig) {
        return addElementCopy(orig, false);
    }

    public Element addElementCopy(Element orig, boolean copyText) {
        Element e = doc.createElementNS(svgNS, orig.getTagName());

        // now copy the attributes
        NamedNodeMap nnm = orig.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            Node n = nnm.item(i);
            if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                String name = n.getNodeName();
                String value = n.getNodeValue();

                e.setAttribute(name, value);
            }
        }

        if (copyText) {
            NodeList nl = orig.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.TEXT_NODE) {
                    String text = n.getNodeValue();
                    Node textnode = doc.createTextNode(text);
                    e.appendChild(textnode);
                }
            }
        }

        // recursively copy any child elements
        NodeList nl = orig.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element backup = curElement;
                curElement = e;
                addElementCopy((Element) n, copyText);
                curElement = backup;
            }
        }
        curElement.appendChild(e);
        return e;
    }

    /*
    protected class SubImageInfo {
    public double colWidth;
    public double colHeaderHeight;
    public double dataHeight;

    public double subCanvasWidth;
    public double subCanvasHeight;

    public Settings settings;
    public MetaColumn col;
    }*/
    
    public static class NoColumnsToDrawException extends java.lang.Exception {

        public String nameOfFirstRoot;
        public boolean noRootsSelected = false;

        public NoColumnsToDrawException(String nameOfFirstRoot, boolean noRootsSelected) {
            super(makeMessage(nameOfFirstRoot, noRootsSelected));

            this.nameOfFirstRoot = nameOfFirstRoot;
            this.noRootsSelected = noRootsSelected;
        }

        protected static String makeMessage(String nameOfFirstRoot, boolean noRootsSelected) {
            String message = "There is nothing to display because no columns are selected to be drawn.";
            if (noRootsSelected) {
                message += "\nNote that no root column ";
                if (nameOfFirstRoot != null) {
                    message += "(eg. \"" + nameOfFirstRoot + "\") ";
                }
                message += "is selected.";
            }
            return message;
        }
    }

    public SVGDocument drawImage() throws NoColumnsToDrawException, Exception {
//        settings.backupSettings();
        double borderWidth = Settings.BORDER_WIDTH;
        double halfBorderWidth = borderWidth / 2;

        double timeLineMinY = 0;
        double timeLineMaxY = 0;

        double canvasWidth = 0;
        double canvasHeight = 0;
        String nameOfFirstRoot = null;
        boolean noRootsSelected = true;

        Set patternUsageHolder = null;
        if (patMan != null) {
            patternUsageHolder = patMan.getPatternUsageHolder();
        }

        Settings settingsBackup = settings;

        //Set Family Tree error message as null
        RangeColumn.clearErrMsgFT();

        // go through all the sub units
        Iterator<DataColumn> subRootIter = rootCol.getSubColumns();
        while (subRootIter.hasNext()) {
            DataColumn dc = subRootIter.next();

            MetaColumn subRoot;

            if (dc instanceof MetaColumn) {
                subRoot = (MetaColumn) dc;
            } else {
                continue;
            }

            if (!subRoot.isSelected()) {
                if (nameOfFirstRoot == null) {
                    nameOfFirstRoot = subRoot.getName();
                }
                continue;
            } else {
                noRootsSelected = false;
            }

            Settings subSettings = settingsBackup.getReadOnlySettings(subRoot.unit);

            // this is so all the drawing functions which draw by year work
            settings = subSettings;

            double dataHeight = Math.abs(subSettings.topAge - subSettings.baseAge) * subSettings.unitsPerMY;
            double xOffset = canvasWidth;
            double colWidth = subRoot.getWidth(subSettings, this, dataHeight);
            if (RangeColumn.consolidatedPlottingErrs.equals("") == false) {
                String tempStore = RangeColumn.consolidatedPlottingErrs;
                RangeColumn.consolidatedPlottingErrs = "";
                throw new Exception(tempStore);
            }
            double colHeaderHeight = subRoot.getHeaderHeight(subSettings, this);

            if (dataHeight == 0) {
                continue;
            }
            subRoot.setVariableColoring(subSettings);

            // draw the actual columns
            subRoot.drawHeader(this, xOffset + borderWidth, borderWidth, colWidth, colHeaderHeight, subSettings);
            subRoot.drawData(this, xOffset + borderWidth,
                    borderWidth + colHeaderHeight + borderWidth,
                    colWidth,
                    dataHeight,
                    subSettings);

            if (RangeColumn.exceptionHandling) {
                RangeColumn.exceptionHandling = false;
                throw new Exception("Error in datapack: There are many reasons which can trigger this exception.\n 1. A range can have child nodes declared but the range points (start/end points) are absent.\n This message is for FamilyTree plotting.\n Sorry I couldn't pin-point the range which causes this exception.");
            }

            subRoot.drawFooter(this, xOffset + halfBorderWidth,
                    colHeaderHeight + dataHeight + 2 * borderWidth,
                    colWidth + borderWidth,
                    subSettings);

            // draw the bounding box
            drawRect(xOffset + halfBorderWidth, halfBorderWidth,
                    borderWidth + colWidth + halfBorderWidth - 1,
                    borderWidth + colHeaderHeight + borderWidth + dataHeight + halfBorderWidth - 1,
                    BORDER_STYLE);

            // draw the line separating the header from the data
            drawLine(xOffset,
                    borderWidth + colHeaderHeight + halfBorderWidth,
                    xOffset + borderWidth + colWidth + borderWidth,
                    borderWidth + colHeaderHeight + halfBorderWidth,
                    BORDER_STYLE);


            double subWidth = colWidth + 2 * borderWidth + 1;
            double subHeight = colHeaderHeight + dataHeight + 3 * borderWidth + 1 + (settings.enChartLegend ? subRoot.FOOTER_HEIGHT : 0);

            canvasWidth += subWidth;
            if (subRootIter.hasNext()) {
                canvasWidth += Settings.SUB_IMAGE_SPACING;
            }
            canvasHeight = Math.max(canvasHeight, subHeight);

            timeLineMinY = Math.max(timeLineMinY, borderWidth + colHeaderHeight + halfBorderWidth);
            timeLineMaxY = colHeaderHeight + dataHeight + 3 * borderWidth + 1;
            
            drawTimeLine(0,
            		timeLineMinY,
            		subWidth,
            		timeLineMinY,
            		timeLineMinY,
            		timeLineMaxY,
            		settings.topAge, settings.baseAge, settings.unitsPerMY,
            		null);
            
//            drawTimeLine(500,
//            		timeLineMinY,
//            		800,
//            		timeLineMinY,
//            		timeLineMinY,
//            		timeLineMaxY,
//            		settings.topAge, settings.baseAge, settings.unitsPerMY,
//            		null);
            
            // (double x1, double y1, double x2, double y2, double minY, double maxY, double topAge, double baseAge, double vertScale, String style)
            // 1: left endpoint (x)
            // 2: right corner
            // 3: right endpoint (x)
            // 4: left corner
            // 5: top bound (y)
            // 6: bottom bound (y)
            // 7: top number display
            // 8:
            // 9: bottom number display
            // 10:
            	
            // Only first drawTimeLine draws
                
            drawTimeLabel();
        }

        if (!RangeColumn.errMsg.equals("")) {
            RangeColumn.errorFamilyTree(RangeColumn.errMsg);
        } 
        
        if (canvasWidth < 4) {
            // there is nothing to draw because the user hasn't selected any columns
            throw new NoColumnsToDrawException(nameOfFirstRoot, noRootsSelected);
        }

        setCanvasSize(canvasWidth, canvasHeight);

        settings = settingsBackup;

        if (settings.doPopups) {
            addMouseOverTexts(settings);
            addMouseOverStaticStuff();
        }
        addBrowserScript();
        // Mouse event for the timeline!!!
        addMouseOverTimeLine();

        if (patMan != null) {
            patMan.writePatternsToIG(this, patternUsageHolder);
        }

        return doc;
    }

    public static class ColumnDrawInfo {

        public double width, height;
        public double dataTopY, dataHeight;
    }

    public ColumnDrawInfo drawColumn(DataColumn subRoot, Settings subSettings, double xOffset, double yOffset, boolean drawBoundingBox) {
        double borderWidth = Settings.BORDER_WIDTH;

        double dataHeight = (subSettings.baseAge - subSettings.topAge) * subSettings.unitsPerMY;
        double colWidth = subRoot.getWidth(subSettings, this, dataHeight);
        double colHeaderHeight = subRoot.getHeaderHeight(subSettings, this);
        double halfBorderWidth = borderWidth / 2;

        subRoot.setVariableColoring(subSettings);

        // draw the actual columns
        subRoot.drawHeader(this, xOffset + borderWidth, borderWidth, colWidth, colHeaderHeight, subSettings);
        double dataTopY = borderWidth + colHeaderHeight + borderWidth;
        subRoot.drawData(this, xOffset + borderWidth,
                dataTopY,
                colWidth,
                dataHeight,
                subSettings);
        

        // draw the bounding box
        if (drawBoundingBox) {
            drawRect(xOffset + halfBorderWidth, halfBorderWidth,
                    borderWidth + colWidth + halfBorderWidth - 1,
                    borderWidth + colHeaderHeight + borderWidth + dataHeight + halfBorderWidth - 1,
                    BORDER_STYLE);
        }

        // draw the line separating the header from the data
        drawLine(xOffset,
                borderWidth + colHeaderHeight + halfBorderWidth,
                xOffset + borderWidth + colWidth + borderWidth,
                borderWidth + colHeaderHeight + halfBorderWidth,
                BORDER_STYLE);

        {
            if (settings.doPopups) {
                addMouseOverTexts(settings);
                addMouseOverStaticStuff();
            }
            addBrowserScript();
            addMouseOverCrossPlot();
        }

        double subWidth = colWidth + 2 * borderWidth + 1;
        double subHeight = colHeaderHeight + dataHeight + 3 * borderWidth + 1;

        ColumnDrawInfo ret = new ColumnDrawInfo();
        ret.width = subWidth;
        ret.height = subHeight;
        ret.dataTopY = dataTopY;
        ret.dataHeight = dataHeight;
        return ret;
    }

    public SVGDocument getDocument() {
        return doc;
    }

    public PopupInfo addPopup(RichText text, DataColumn.FileInfo colFileInfo) {
        PopupInfo pi = new PopupInfo(text, colFileInfo);
        popups.add(pi);
        return pi;
    }

    public void appendPopupAttributes(Element node, String spawnerID, String popupID) {
        node.setAttributeNS(null, "onmouseover", "doMOHover(evt, '" + spawnerID + "', '" + popupID + "')");
        node.setAttributeNS(null, "onmouseout", "doMOOut(evt)");
        node.setAttributeNS(null, "onclick", "doMOClick(evt, '" + spawnerID + "', '" + popupID + "')");
        node.setAttributeNS(null, "id", spawnerID);
        node.setAttributeNS(null, "opacity", "0");
    }

    public void doPopupThings(String text, DataColumn.FileInfo colFileInfo) {
        doPopupThings(curElement, text, colFileInfo);
    }
    
    public void doPopupThings(String text, DataColumn.FileInfo colFileInfo, RangeColumn rCol) {
        doPopupThings(curElement, text, colFileInfo, rCol);
    }

    public void doPopupThings(Element node, String text, DataColumn.FileInfo colFileInfo) {
        if (node == null || text == null || text.length() == 0) {
            return;
        }

        PopupInfo pi = addPopup(new RichText(text, true, colFileInfo), colFileInfo);
        appendPopupAttributes(node, pi.spawnerID, pi.id);
    }
    
    public void doPopupThings(Element node, String text, DataColumn.FileInfo colFileInfo, RangeColumn rCol) {
        if (node == null || text == null || text.length() == 0) {
            return;
        }

        PopupInfo pi = addPopup(new RichText(text, true, colFileInfo, rCol), colFileInfo);
        appendPopupAttributes(node, pi.spawnerID, pi.id);
    }

    public void addMouseOverTexts(Settings settings) {
        PopupInfo pi;
        for (int i = 0; i < popups.size(); i++) {
            pi = (PopupInfo) popups.get(i);

            StringWrappingInfo swi = getSWI(pi.text, settings.fonts.getFont(FontManager.POPUP_FONT), StringWrappingInfo.NORMAL);
            //swi.wrap(settings.getPopupViewportWidth());

            Element t = doc.createElement("text"); //changed to text from tsc_popup
            t.setAttributeNS(null, "id", pi.id);
            t.setAttributeNS(null, "visibility", "hidden");
            t.setAttributeNS(null, "popuptext", swi.s.orig);
            if (pi.colFileInfo != null && pi.colFileInfo.baseURL != null) {
                t.setAttributeNS(null, "docbase", pi.colFileInfo.baseURL.toString());
            }

            curElement.appendChild(t);
        }
    }

    public void addMouseOverStaticStuff() {
        addMouseOverScript();
    }

    public void addMouseOverScript() {
        Element script = doc.createElementNS(svgNS, "script");
        script.setAttributeNS(null, "type", "text/ecmascript");
        script.appendChild(getScript(ResPath.getPath("CrossplotJS.mouseOverText")));
        //curElement.appendChild(script);
        svgRoot.insertBefore(script, svgRoot.getFirstChild());
    }

    /*
     * Adds javascript to the svg that runs in browser to apply a "fix"
     * The script does not run ouside a browser because the patterns work there
     */
    public void addBrowserScript() {
        Element fix = doc.createElementNS(svgNS, "script");
        fix.setAttributeNS(null, "type", "text/ecmascript");
        fix.appendChild(getScript(ResPath.getPath("CrossplotJS.browserfix")));
        ;
        //curElement.appendChild(fix);
        svgRoot.insertBefore(fix, svgRoot.getFirstChild());
    }

    // Need to change this function to move line up or down
    public void addMouseOverTimeLine() {
        Element script = doc.createElementNS(svgNS, "script");
        script.setAttributeNS(null, "type", "text/ecmascript");
        script.appendChild(getScript(ResPath.getPath("CrossplotJS.chartTools")));
        //curElement.appendChild(script);
        svgRoot.insertBefore(script, svgRoot.getFirstChild());
    }

    public void addMouseOverCrossPlot() {
        Element script = doc.createElementNS(svgNS, "script");
        script.setAttributeNS(null, "type", "text/ecmascript");
        script.appendChild(getScript(ResPath.getPath("CrossplotJS.crossplotTools")));
        //curElement.appendChild(script);
        svgRoot.insertBefore(script, svgRoot.getFirstChild());
        //System.out.println(svgRoot.getFirstChild().getElementById("timeLabelY"));
    }

    private CDATASection getScript(String filepath) {
        CDATASection t = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(FileUtils.getInputStream(filepath)));
            String str = null;
            String script = "\n";
            while ((str = in.readLine()) != null) {
                script += str + "\n";
            }
            t = doc.createCDATASection(script);
        } catch (Exception e) {
        }
        return t;
    }

}
