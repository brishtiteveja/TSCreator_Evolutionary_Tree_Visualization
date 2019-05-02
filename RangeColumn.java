package datastore;

import datastore.editor.DataSeries;
import datastore.editor.DataSteward;
import gui.*;
import gui.editor.SpreadSheet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import path.ResPath;
import util.Debug;
import util.NumberUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

// Definittion of range column class
public class RangeColumn extends DataColumn {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final int MIN_RANGE_COL_WIDTH = 50;

    // Definition of range column data point class
    public static class RCDatapoint extends datastore.Datapoint {
        public String type;
        public String speciesOrPhenon = null;
        public String speciesWithFinalTopOfPhenon = null;
        public boolean branch = false; // whether or not this is a family tree branch
        public String branchTo = null; // who the branch is to
        public String subLabel = null; // who the branch is to
        public String branchLabel = null; // label to put on the line for the branch
        public Color branchColor = null; // the color of the branches
        public int branchPrio = 10;
        public boolean notInclude = false;
        public boolean overridePriority = false;
        public boolean splitPhenonBranch = false; // Whether it's a point in a splitted phenon branch

        public RCDatapoint() {
        }

        public RCDatapoint(Datapoint dp) {
            super(dp);
        }

        @Override
        public RCDatapoint copy() {
            RCDatapoint ret = new RCDatapoint(this);
            ret.type = type;
            ret.branch = branch;
            ret.branchTo = branchTo;
            ret.subLabel = subLabel;
            ret.branchLabel = branchLabel;
            ret.branchColor = branchColor;
            ret.branchPrio = branchPrio;

            return ret;
        }

        public void setType(String type) {
            if (type == null) {
                type = null;
                branch = false;
                return;
            }

            if (type.toLowerCase().startsWith("branch")) {
                branch = true;
                type = "branch";
            } else {
                this.type = type;
            }
        }
        //public void parseBranchSide(String s) {
        //	if (s == null) {
        //		branchSide = 0;
        //		return;
        //	}
        //	s = s.toLowerCase();
        //	if (s.startsWith("right")) {
        //		branchSide = 1;
        //	} else if (s.startsWith("left")) {
        //		branchSide = -1;
        //	} else {
        //		branchSide = 0;
        //	}
        //}
        public void printDataPointInfo() {
        	System.out.println();
        	System.out.println("Printing Data Point Info.");
    		System.out.println("Base age =" + baseAge);
    		System.out.println("Branch label =" + branchLabel);
    		System.out.println("Branch priority =" + branchPrio);
    		System.out.println("Branch to =" + branchTo);
    		System.out.println("Direction =" + direction);
    		System.out.println("Label =" + label);
    		System.out.println("Line Type =" + lineType);
    		System.out.println("Serial = " + mySerial);
    		System.out.println("Popup = " + popup);
    		System.out.println("Priority = " + priority);
    		System.out.println("Section = " + section);
    		System.out.println("Series = " + series);
    		System.out.println("SpeciesOrPhenon = " + speciesOrPhenon);
    		System.out.println("SpeciesWithFinalTop = " + speciesWithFinalTopOfPhenon);
    		System.out.println("SubLabel = " + subLabel);
    		System.out.println("Type = " + type);
    		System.out.println("uncertainty = " + uncertainty);
    		System.out.println("*****Range Data Point Info Printing Completed.*****");
    		System.out.println();
        }
    }

    @Override
    public void addData(Datapoint dp) {
        if (!(dp instanceof RCDatapoint)) {
            RCDatapoint rcd = new RCDatapoint(dp);
            dp = rcd;
        }
        super.addData(dp);
    }

    // public variables for evolutionary tree and ranges
    public static final double RANGE_COLUMN_TITLE_PADDING = 10;
    public static final double RANGE_LABEL_BOTTOM_MARGIN = 1;
    public static final double RANGE_LABEL_TOP_MARGIN = 3;
    public static final double RANGE_TO_TOP_LABEL_MARGIN = 3;
    public static final double RANGE_TO_BOTTOM_LABEL_MARGIN = 3;
    public static final double RANGE_LABEL_TO_RANGE_MARGIN = 0.5;
    public static final double RANGE_PADDING = 2;
    public static double RANGE_LABEL_X_PADDING = 2.5;
    public static double RANGE_LABEL_Y_PADDING = 2.5;
    public static double AGE_LABEL_X_PADDING = 3.5;
    public static double AGE_LABEL_Y_PADDING = 0;
    public static final double PHENON_RANGE_LINE_STROKE_WIDTH = 2;
    public static final double RANGE_BOX_STROKE_WIDTH = 4;
    public static final double RANGE_BOX_EXTRA_WIDTH = 120;
    public static final double RANGE_CANVAS_START_PADDING = 30; // Initial space at the left of the canvas 
    public static final double WIDTH_FOR_SPECIES_BOX_INTERVAL = 50; // Space between species boxes
    //public static final double PADDING_EVO_TREE = 14;//Corey Hopp -- added this variable for paddding in the evolutionary tree
    public static final double PADDING_EVO_TREE = 2;//Corey Hopp -- added this variable for paddding in the evolutionary tree
    public double MAXIMUM_PADDING = 1000;
    public double MINIMUM_PADDING = 100;
    public double TOTAL_RANGE_WIDTH = 0;
    public static final double RANGE_ARROW_STALK_LENGTH = 10; // in screen units
    public static final double RANGE_ARROW_HEAD_LENGTH = 4; // in screen units
    public static final double RANGE_ARROW_HEAD_WIDTH = 4; // in screen units
    public static final double SAMPLE_RADIUS = 3;
    public static final double RANGE_ARROW_LENGTH = 2.5; // integrated tree range split arrow length
    public static final double RANGE_ARROW_VERTICAL_PADDING = 0.15;
    //public static final double MAX_RANGE_THICKNESS = 9;
    public static final RangeStyle style_sample = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 1);
    public static final RangeStyle style_missing = new RangeStyle("", 0);
    public static final RangeStyle style_rare = new RangeStyle(Settings.DASHED_STROKE + " stroke: black;", 0.5);
    public static final RangeStyle style_conjectured = new RangeStyle(Settings.DOTTED_STROKE + " stroke: black;", 0.5);
    public static final RangeStyle style_common = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 0.75);
    public static final RangeStyle style_frequent = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 2.5);
    public static final RangeStyle style_abundant = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 5);
    public static final RangeStyle style_flood = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 9);
    double maxRangeLabelHeaderHeight;
    protected SortedSet<Range> ranges = null; // Species-phenon combined set. Sorted set which contains both species and phenon ranges
    protected SortedSet<Range> rangesWithoutSplit = null;
    protected SortedSet<Range> phenonRanges = null;
    protected SortedSet<Range> extendedRanges = null;
    public int rangeSort = RangeComparator.FIRST_OCCURANCE;
    public boolean hasTrees = false;
    public boolean drawTrees = true;
    public boolean drawLabelsInHeader = false;
    // Range Column integrated tree drawing flag
    public boolean speciesPhenonTreeDrawing = false;
    public boolean sideBySideTreeStructure = false;
    public boolean integratedTreeStructure = false;
    //Family Tree
    public static String errMsg = "";
    public static JFrame errFamilyTree = null;
    public static JTextArea errFamilyTreeMessage = null;
    public static JButton errFamilyTreeButton = null;
    public static boolean ConserveSpace = false;
    private int minLocation = 0;
    public static boolean initialLoading = true;             //flag to check whether this is the first time loading or not.
    public static boolean speciesBoxBranchClicked = false;

    //public static double myX = 100;
    public static HashMap<String,String> circleDrawingLeftOrRight = new HashMap<String,String>();
    private HashMap<Range, Boolean> phenonRangeStateMap = new HashMap<Range, Boolean>();;
    
    // definition of range size class
    protected class RangeSize {

        protected Double base;
        protected Double top;
        protected Double maxWidth;
        protected Range range = null;

        RangeSize(Double base, Double top, Double maxWidth, Range range) {
            this.base = base;
            this.top = top;
            this.range = range;
            this.maxWidth = maxWidth;
        }
    }

    private LinkedList<RangeSize> assignedLocationList = null;

    public static enum LabelLocation {
        HEADER,
        LEFT_SIDE,
        RIGHT_SIDE,
        TOP,
        BOTTOM,
        RESIZE,
        UNDECIDED
    }

    public LabelLocation labelLocSearchOrder[] = {LabelLocation.LEFT_SIDE, LabelLocation.RIGHT_SIDE, LabelLocation.TOP, LabelLocation.BOTTOM, LabelLocation.HEADER};

    /**
     * @param colName
     */
    public RangeColumn(String colName) {
        super(colName);

        iconPath = ResPath.getPath("icons.col_icon_range");

        this.createDataSet(new Datapoint.LabelComparator());

        setColor(new Coloring(Color.white));
    }

    // options panel in settings window
    @Override
    public JPanel getOptionsPanel() {
        if (optionsPanel == null) {
            optionsPanel = new JPanel();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

            final RangeColumn thisRC = this;

            // now the sort options
            final JPanel sortPanel = new JPanel();
            sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));
            sortPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
            final JRadioButton firstR = new JRadioButton(Language.translate("First Occurrence", true));
            final JRadioButton lastR = new JRadioButton(Language.translate("Last Occurrence", true));
            final JRadioButton alphaR = new JRadioButton(Language.translate("Alphabetical", true));
            ButtonGroup group = new ButtonGroup();
            group.add(firstR);
            group.add(lastR);
            group.add(alphaR);

            sortPanel.add(new JLabel("<html>" + Language.translate("sort by:", true) + "</html>"));
            sortPanel.add(firstR);
            sortPanel.add(lastR);
            sortPanel.add(alphaR);

            optionsPanel.add(sortPanel);

            firstR.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent ie) {
                    if (((JRadioButton) ie.getItem()).isSelected()) {
                        thisRC.rangeSort = RangeColumn.RangeComparator.FIRST_OCCURANCE;
                    }
                }
            });

            lastR.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent ie) {
                    if (((JRadioButton) ie.getItem()).isSelected()) {
                        thisRC.rangeSort = EventColumn.RangeComparator.LAST_OCCURANCE;
                    }
                }
            });

            alphaR.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent ie) {
                    if (((JRadioButton) ie.getItem()).isSelected()) {
                        thisRC.rangeSort = EventColumn.RangeComparator.ALPHABETICAL;
                    }
                }
            });

            switch (rangeSort) {
                case RangeColumn.RangeComparator.FIRST_OCCURANCE:
                    firstR.setSelected(true);
                    break;
                case RangeColumn.RangeComparator.LAST_OCCURANCE:
                    lastR.setSelected(true);
                    break;
                case RangeColumn.RangeComparator.ALPHABETICAL:
                    alphaR.setSelected(true);
                    break;
            }

            if (this.speciesPhenonTreeDrawing == true || this.sideBySideTreeStructure || this.integratedTreeStructure) {
            	// For integrated tree, tree structure type panel
                final JPanel treeStructureTypePanel = new JPanel();
                treeStructureTypePanel.setLayout(new BoxLayout(treeStructureTypePanel, BoxLayout.Y_AXIS));
                treeStructureTypePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
                final JRadioButton iR = new JRadioButton(Language.translate("Integrated Tree", true));
                final JRadioButton sR = new JRadioButton(Language.translate("Side By Side Tree", true));
                
                ButtonGroup group2 = new ButtonGroup();
                group2.add(iR);
                group2.add(sR);

                treeStructureTypePanel.add(new JLabel("<html>" + Language.translate("Choose tree structure:", true) + "</html>"));
                
            	treeStructureTypePanel.add(iR);
            	treeStructureTypePanel.add(sR);
            
                optionsPanel.add(treeStructureTypePanel);

                sR.addItemListener(new ItemListener() {

                    public void itemStateChanged(ItemEvent ie) {
                        if (((JRadioButton) ie.getItem()).isSelected()) {
                        	thisRC.sideBySideTreeStructure = true;
                        	thisRC.integratedTreeStructure = false;
                        	thisRC.speciesPhenonTreeDrawing = false;
                        }
                    }
                });

                iR.addItemListener(new ItemListener() {

                    public void itemStateChanged(ItemEvent ie) {
                        if (((JRadioButton) ie.getItem()).isSelected()) {
                        	thisRC.sideBySideTreeStructure = false;
                        	thisRC.integratedTreeStructure = true;
                        	thisRC.speciesPhenonTreeDrawing = true;
                        }
                    }
                });

                if (this.speciesPhenonTreeDrawing || this.integratedTreeStructure) {
                	iR.setSelected(true);
                } else {
                	sR.setSelected(true);
                }
            } else {
            	this.speciesPhenonTreeDrawing = false;
            	this.integratedTreeStructure = false;
            	this.sideBySideTreeStructure = false;
            }
        }        
        return optionsPanel;
    }

    // function to determine width of the range/tree column
    @Override
    public double getWidth(Settings settings, ImageGenerator ig, double dataHeight) {
    	// Distinguish the phenon points from the species points and assign "phenon" to all points of a phenon range
        RCDatapoint parentP, childP;
        Iterator iterParent = this.getDrawingData();
        
        // Determining phenon point or child point for speciesPhenon Integrated Tree
        while (iterParent.hasNext()) {
        	parentP = (RCDatapoint) iterParent.next();
        	if (parentP.label.contains(" <img") && parentP.speciesOrPhenon != null && parentP.speciesOrPhenon.equalsIgnoreCase("phenon")) {
        		// making speciesPhenonTreeDrawing flag true if integratedTreeStructure or initialLoading is true.
        		this.speciesPhenonTreeDrawing = true;
        		String parentPointName = parentP.label.split(" <img")[0];
        		Iterator iterChild = this.getDrawingData();
        		while (iterChild.hasNext()) {
        			childP = (RCDatapoint) iterChild.next();
        			if (!childP.label.contains(" <img") && parentPointName.contains(childP.label)) {
        				childP.speciesOrPhenon = parentP.speciesOrPhenon; // assigning "phenon" status to the child points
        			}
        		}
        	}
        }
    	
        double totalPadding = 0;
        
        //Sorts through the ranges
    	if(findRanges(settings, ig, rangeSort) == false) {
    		return 0;
    	}
    	
    	//Gets the width of the ranges
        findMaxRangeLabelLocations(settings, 0, dataHeight, ig);
        derivebranchColors();
        assignedLocationList = null;
        minLocation = assignLocationAttributes();

        //Adjust widths
        Iterator iter = ranges.iterator();
        Range range = null;
        Range myRange = null;
        StringWrappingInfo testSWI = null;
        TSCFont rangeLabelFont = new gui.TSCFont("Arial", Font.BOLD, 12, new Color(255, 255, 255));
        TSCFont rangeLabelFontSmall = new gui.TSCFont("Arial", Font.BOLD, 10, new Color(255, 255, 255));

        while (iter.hasNext()) {
            range = (Range) iter.next();
            if ((range.branchDisabled == false) && (range.subLabel != null)) {
                range.subLabelSWI = ig.getSWIOneLine(range.subLabel, rangeLabelFont, StringWrappingInfo.NORMAL, fileInfo);
                if (range.subLabelSWI.getWidth() > range.widthSubTree) {
                    range.subLabelSWI = ig.getSWIOneLine(range.subLabel, rangeLabelFontSmall, StringWrappingInfo.NORMAL, fileInfo);
                    if (range.subLabelSWI.getWidth() > range.widthSubTree) {
                        range.width = range.subLabelSWI.getWidth() - (range.widthSubTree - range.width);//Corey Hopp
                    }
                }

            }
        }
        
        // Extending the width of species ranges depending on the number of phenons contained
        if (this.speciesPhenonTreeDrawing) {
        	Iterator<Range> iter2 = ranges.iterator();
            while (iter2.hasNext()) {
            	Range rangeSpecies = iter2.next();
            	if (rangeSpecies.phenonsContainingSpecies.size() == 0) { // for phenon ranges no width change
            		continue;
            	}
            	Iterator<Range> iterPhenon = rangeSpecies.phenonsContainingSpecies.iterator();
            	while (iterPhenon.hasNext()) {
            		Range rangePhenon = iterPhenon.next();
            		rangeSpecies.width += rangePhenon.width;
            	}
            	if (rangeSpecies.phenonsContainingSpecies.size() >= 1) {
            		rangeSpecies.width += RANGE_BOX_EXTRA_WIDTH;
            	}
            }
        }

        myWidth = RANGE_PADDING;
        iter = ranges.iterator();
        range = null;

        // conserve space feature handling
        if(ConserveSpace){
        	double minTop = 0;
        	double maxBase = 0;
        	
	        for (int i = 0; i < ranges.size(); i++){
	        	int j = i*-1;
	        	Iterator myiter = ranges.iterator();
	        	while(myiter.hasNext()){
	        		myRange = (Range) myiter.next();
	        		if(j == myRange.location){
	        			double a2 = (myRange.base - settings.topAge) * settings.unitsPerMY + 86.4931640625;
	        	    	double b2 = (myRange.top - settings.topAge) * settings.unitsPerMY + 86.4931640625;
	        	    	double currentHeight = 0;
	        	    	
	        	    	if(b2 < a2 - myRange.nameHeight){
	        	      	   currentHeight = b2;
	        	      	} else {
	        	      	   currentHeight = a2 - myRange.nameHeight;
	        	      	}
	        	    	
	        	    	if (myRange.branchDisabled == false && (myRange.disabled4Priority == false)
	                            && (myRange.base > settings.topAge && !Double.isNaN(myRange.base))
	                            && (myRange.top < settings.baseAge && !Double.isNaN(myRange.top))) {

	        	    		if(minTop == 0 && maxBase == 0){
	        	    			//Corey Hopp added padding here
		                    	if (this.speciesPhenonTreeDrawing) {
		                    		if (myRange.isPhenonRange == false) { // Only adding the species box width
		                    			myWidth += myRange.getWidth() + RangeColumnWidthPadding + BranchSpacing;
		                    		}
		                    	} else {
		                    		myWidth += myRange.getWidth() + RangeColumnWidthPadding + BranchSpacing;
		                    	}

		                        totalPadding += PADDING_EVO_TREE;
		            			minTop = currentHeight;
		            			maxBase = a2;
		            		} else if(minTop > a2){
		            			//rangeX = prevX;
		            			minTop = currentHeight;
		            		} else if(currentHeight > maxBase){
		            			//rangeX = prevX;
		            			maxBase = a2;
		            		} else{
		            			//Corey Hopp added padding here
		                    	if (this.speciesPhenonTreeDrawing) {
		                    		if (myRange.isPhenonRange == false) { // Only adding the species box width
		                    			myWidth += myRange.getWidth() + RangeColumnWidthPadding + BranchSpacing;
		                    		}
		                    	} else {
		                    		myWidth += myRange.getWidth() + RangeColumnWidthPadding + BranchSpacing;
		                    	}

		                        totalPadding += PADDING_EVO_TREE;
		            			minTop = currentHeight;
		            			maxBase = a2;
		            		}
	                    	
	                    } 

	        			break;
	        		}
	        	}
	        }
        }
        else{
	        while (iter.hasNext()) {
	            range = (Range) iter.next();
	            // calculate width for only these ranges that are with in the given range of settings.
	            if (range.branchDisabled == false && (range.disabled4Priority == false)
	                    && (range.base > settings.topAge && !Double.isNaN(range.base))
	                    && (range.top < settings.baseAge && !Double.isNaN(range.top))) {
	
	            	//Corey Hopp added padding here
	            	if (this.speciesPhenonTreeDrawing) {
	            		if (range.isPhenonRange == false) { // Only adding the species box width
	            			myWidth += range.getWidth() + RangeColumnWidthPadding + BranchSpacing;
	            		}
	            	} else {
	            		myWidth += range.getWidth() + RangeColumnWidthPadding + BranchSpacing;
	            	}
	
	                totalPadding += PADDING_EVO_TREE;
	            } 
	        }
        }
            
        myWidth += (MINIMUM_PADDING*0.5);

        if (myWidth < MIN_RANGE_COL_WIDTH) {
            myWidth = MIN_RANGE_COL_WIDTH;
        }

        myWidth += 2 * RANGE_PADDING;
        return myWidth;
    }

    protected void findMaxRangeLabelLocations(Settings settings, double starty, double height, ImageGenerator ig) {
        maxRangeLabelHeaderHeight = 0;
        // now account for the range labels

        Iterator iter = ranges.iterator();

        for (int rangeCount = 0; iter.hasNext(); rangeCount++) {
            Range r = (Range) iter.next();
            
            StringWrappingInfo  testLabelSWI = null;
            if (r.isPhenonRange) {  // Ranges inside the box
            	testLabelSWI = ig.getSWIOneLine(r.name, fonts.getFont(FontManager.RANGE_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);
            } else {
               	testLabelSWI = ig.getSWIOneLine(r.name, fonts.getFont(FontManager.RANGE_BOX_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);                   	
            }
            
            double labelHeight = RANGE_TO_BOTTOM_LABEL_MARGIN + testLabelSWI.getHeight() + RANGE_TO_TOP_LABEL_MARGIN;

            // figure out where to put the label
            for (int locI = 0; locI < labelLocSearchOrder.length; locI++) {
                if (labelLocSearchOrder[locI] == LabelLocation.TOP) {
                    // make sure there is space
                    double rangeTop = ImageGenerator.getYFromYear(r.top, starty, settings);
                    if (rangeTop - labelHeight > starty) {
                        // we have room
                        r.labelloc = LabelLocation.TOP;
                        break;
                    }
                } else if (labelLocSearchOrder[locI] == LabelLocation.BOTTOM) {
                    // make sure there is space
                    double rangeBottom = ImageGenerator.getYFromYear(r.base, starty, settings);
                    if (rangeBottom + labelHeight < starty + height) {
                        // we have room
                        r.labelloc = LabelLocation.BOTTOM;
                        break;
                    }
                } else if (labelLocSearchOrder[locI] == LabelLocation.LEFT_SIDE) {
                	if (labelHeight >= height) {
                		r.labelloc = LabelLocation.RESIZE;
                		break;
                	}
                	else{
                        // we have room
                        r.labelloc = LabelLocation.LEFT_SIDE;
                        break;
                    }
                
                } else if (labelLocSearchOrder[locI] == LabelLocation.RIGHT_SIDE) {
                	if (labelHeight >= height) {
                		r.labelloc = LabelLocation.RESIZE;
                		break;
                	}
                	else{
                        // we have room
                        r.labelloc = LabelLocation.RIGHT_SIDE;
                        break;
                    }
                } else { //if (labelLocSearchOrder[locI] == LabelLocation.HEADER) {
                    // there is always space in the header
                    maxRangeLabelHeaderHeight = Math.max(maxRangeLabelHeaderHeight, testLabelSWI.getHeight());
                    r.labelloc = LabelLocation.HEADER;
                    drawLabelsInHeader = true;
                    break;
                }
            }

            r.calculateWidth(ig);
            TOTAL_RANGE_WIDTH += r.width;
        }
    }

    protected void findMaxRangeLabelHeight(ImageGenerator ig) {
        maxRangeLabelHeaderHeight = 0;
        // now account for the range labels

        Iterator iter = ranges.iterator();

        for (int rangeCount = 0; iter.hasNext(); rangeCount++) {
            Range r = (Range) iter.next();
            StringWrappingInfo  testLabelSWI = null;
            
            if (r.isPhenonRange) {  // Ranges inside the box
            	testLabelSWI = ig.getSWIOneLine(r.name, fonts.getFont(FontManager.RANGE_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);
            } else {
               	testLabelSWI = ig.getSWIOneLine(r.name, fonts.getFont(FontManager.RANGE_BOX_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);                   	
            }
            
            maxRangeLabelHeaderHeight = Math.max(maxRangeLabelHeaderHeight, testLabelSWI.getHeight());
        }
    }

    /**
     * This function assumes that getWidth() was called
     *
     * @param settings
     * @param ig
     * @return
     */
    @Override
    public double getHeaderHeight(Settings settings, ImageGenerator ig) {
        super.getHeaderHeight(settings, ig);

        if (maxRangeLabelHeaderHeight > 0) {
            myOwnHeaderHeight += maxRangeLabelHeaderHeight + RANGE_LABEL_BOTTOM_MARGIN + RANGE_LABEL_TOP_MARGIN;
        }

        myHeaderHeight = myOwnHeaderHeight;
        return myHeaderHeight;
    }

    @Override
    public void drawHeader(ImageGenerator ig, double startx, double starty, double width, double height, Settings settings) {
        if (myWidth < 0) {
            myWidth = getWidth(settings, ig, height);
        }
        if (myHeaderHeight < 0) {
            myHeaderHeight = getHeaderHeight(settings, ig);
        }

        // set up link processing so that relative URLs work
        if (fileInfo != null) {
            ig.linkProc = new LinkProcessor(fileInfo);
        } else {
            ig.linkProc = null;
        }

        // draw the title
        if (name.length() > 0 && drawTitle) {
            //ig.drawString(nameWrap, startx, starty, width, height - maxRangeLabelHeaderHeight - RANGE_LABEL_TOP_MARGIN - RANGE_LABEL_BOTTOM_MARGIN, ImageGenerator.BOTTOM);
            
        	StringWrappingInfo myWrap = nameWrap;
        	TSCFont myWrapHolder = fonts.getFont(FontManager.COLUMN_HEADER_FONT);

        	double eggy = Math.floor(myWrapHolder.getSize());
            double myLayer = myWrap.getWidth()*myWrap.getNumLines();
            double myThreshold = myWidth*2;
            double eggy2 = eggy;
            
            String myString = name;
            String[] split = myString.split( "(\\s)+" );
            int myLength = 0;
            
            AffineTransform affinetransform = new AffineTransform();     
            FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
            Font font = new Font("Arial", Font.PLAIN, 14);
            int marker = 0;
            
            for(int x = 0; x < split.length; x++){
            	int textwidth = (int)(font.getStringBounds(split[x], frc).getWidth());
            	if(textwidth > myLength){
            		myLength = textwidth;
            		marker = x;
            	}
            }

            if(myWrap.getNumLines() > 2 || myLength >= nameWrap.getWidth()){
    	        while(eggy > 6 && (myWrap.getNumLines() > 2 || myLength >= nameWrap.getWidth())){
    	        	myWrapHolder.setSize(eggy);
    	        	myWrap = ig.wrapString(name, myWidth, myWrapHolder, fileInfo);
    	        	myLayer = myWrap.getWidth()*myWrap.getNumLines();
    	        	font = new Font("Arial", Font.PLAIN, (int) eggy);
    	        	myLength = (int)(font.getStringBounds(split[marker], frc).getWidth());
    	        	eggy--;
    	        }
            }

            ig.drawString(nameWrap, startx, starty - RANGE_COLUMN_TITLE_PADDING, width, height - maxRangeLabelHeaderHeight - RANGE_LABEL_TOP_MARGIN - RANGE_LABEL_BOTTOM_MARGIN, ImageGenerator.BOTTOM);
            eggy = eggy2;
            myWrapHolder.setSize(eggy);
        }

        // draw the range labels
        Iterator iter = ranges.iterator();

        double rangeStart = RANGE_PADDING;
        while (iter.hasNext()) {
            Range r = (Range) iter.next();

            //StringWrappingInfo labelSWI = ig.getSWIOneLine(r.name, fonts.getFont(FontManager.RANGE_LABEL_FONT), StringWrappingInfo.VERTICAL);

            if (r.labelloc == LabelLocation.HEADER) {
                ig.drawString(r.nameSWI,
                        startx + rangeStart, // X
                        starty + height - RangeColumn.RANGE_LABEL_BOTTOM_MARGIN - maxRangeLabelHeaderHeight, // Y
                        r.getWidth(), maxRangeLabelHeaderHeight,
                        ImageGenerator.BOTTOM);

                if (r.popup != null && settings.doPopups) {
                    ig.pushGrouping();
                    ig.doPopupThings(r.popup, fileInfo);
                    ig.drawRect(startx + rangeStart,
                            starty + height - RangeColumn.RANGE_LABEL_BOTTOM_MARGIN - maxRangeLabelHeaderHeight,
                            r.getWidth(), maxRangeLabelHeaderHeight, Settings.POPUP_HIGHLIGHT_STYLE);

                    ig.popGrouping();
                }
            }

            rangeStart += r.getWidth() + RANGE_PADDING;
        }

        // add the popup
        if (popup != null && settings.doPopups) {
            ig.pushGrouping();
            ig.doPopupThings(popup, fileInfo);
            // draw an invisible rectangle over where the popup should be so that
            // the popup works outside the text too
            ig.drawRect(startx, starty, width, height, Settings.POPUP_HIGHLIGHT_STYLE);
            ig.popGrouping();
        }
    }

    // definition of range style class 
    public static class RangeStyle {

        protected String style;
        protected double width;

        public RangeStyle(String style, double width) {
            this.style = style;
            this.width = width;
        }

        public String getStyle() {
            return "stroke-width: " + width + "; " + style;
        }

        public double getWidth() {
            return width;
        }
    }

    public static RangeStyle getRangeStyle(String type) {
        if (type.compareToIgnoreCase("sample") == 0) {
            return style_sample;
        }
        if (type.compareToIgnoreCase("missing") == 0) {
            return style_missing;
        }
        if (type.compareToIgnoreCase("rare") == 0) {
            return style_rare;
        }
        if (type.compareToIgnoreCase("conjectured") == 0) {
            return style_conjectured;
        }
        if (type.compareToIgnoreCase("common") == 0) {
            return style_common;
        }
        if (type.compareToIgnoreCase("frequent") == 0) {
            return style_frequent;
        }
        if (type.compareToIgnoreCase("abundant") == 0) {
            return style_abundant;
        }
        if (type.compareToIgnoreCase("flood") == 0) {
            return style_flood;
        }

        return style_frequent;
    }

    protected int rangePointSerial = 0;

    // definition of range point class 
    public class RangePoint implements HyperlinkListener {

        double age;
        String type;
        String name;
        String popup;
        boolean top = false;
        boolean used = false;
        int mySerial = 0;
        public Range childRange = null;
        protected boolean childOutofRange = false;
        public RCDatapoint rcd = null;
        public JFrame popupFrame = null;
        public JEditorPane contentPane = null;

        public RangePoint() {
            mySerial = rangePointSerial++;
        }
        
        public RangePoint copy() {
        	RangePoint ret = new RangePoint();
        	ret.age = age;
        	ret.childOutofRange = childOutofRange;
        	ret.childRange = childRange;
        	ret.contentPane = contentPane;
        	ret.mySerial = ++rangePointSerial;
        	ret.name = name;
        	ret.popup = popup;
        	ret.popupFrame = popupFrame;
        	ret.rcd = rcd.copy();
        	ret.top = top;
        	ret.type = type;
        	ret.used = used;
        	
        	return ret;
        }
        
        public void printRangePointInfo() {
        	System.out.println("Age = " + age);
        	System.out.println("Type = " + type);
        	System.out.println("Name = " + name);
        	System.out.println("Popup = " + popup);
        	System.out.println("Top = " + top);
        	System.out.println("Used = " + used);
        	System.out.println("My Serial = " + mySerial);
        	System.out.println("Child Range = " + childRange);
//        	childRange.printRangeInfo();
        	System.out.println("Child Out Of Range = " + childOutofRange);
        	System.out.println("RCDataPoint = " + rcd);
        	System.out.println("RCDataPointInfo = ");
        	rcd.printDataPointInfo();
        }

        public void setResetIncludeBranch(boolean notInclude) {
            if (rcd != null) {
                rcd.notInclude = notInclude;
            }

            if (notInclude == false) {
                rcd.overridePriority = true;
            } else {
                rcd.overridePriority = false;
            }
        }

        public void handlePopUps(int xCoordinate, int yCoordinate) {
            if (popupFrame == null) {
                popupFrame = new JFrame("Branch Info");
                popupFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                popupFrame.setSize(300, 200);
                popupFrame.setLocation(xCoordinate, yCoordinate + 100);

                contentPane = new JEditorPane();
                contentPane.setEditable(false);
                contentPane.setContentType("text/html");
                if (popup == null) {
                    popup = "No Branch Information present in Datapack";
                }
                contentPane.setText(popup);
                contentPane.addHyperlinkListener(this);

                popupFrame.getContentPane().add(contentPane);
                popupFrame.setVisible(true);
            } else {
                if (popupFrame.isVisible()) {
                    popupFrame.setVisible(false);
                } else {
                    popupFrame.setVisible(true);
                }
            }
        }
        

        //Hyperlink listener method for URLs embedded in the Comments Pane.
        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                gui.TSCreator.launchBrowser(event.getURL().toExternalForm());
            }
        }
    }

    // Different comparison function to compare ranges
    protected static class RangePointComparator implements java.util.Comparator {

        /**
         * Compare Datapoint objects based on their age.
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            // convert parameters to Datapoint classes
            RangePoint left = (RangePoint) arg0;
            RangePoint right = (RangePoint) arg1;
            if (left == null || right == null) {
                return 0;
            }

            // look at the difference in age
            double result = left.age - right.age;

            // see if they're equal
            if (result == 0f
                    || (result > 0 && result < NumberUtils.EQUAL_MARGIN)
                    || (result < 0 && result > -NumberUtils.EQUAL_MARGIN)) {


                // based on which one was created first
                return left.mySerial - right.mySerial;
            }

            // not equal, give direction as an integer
            if (result < 0) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    protected int rangeSerial = 0;

    // definition of range class
    public class Range {
        double base = Double.NaN, top = Double.NaN;
        String name;
        String popup = "";
        int mySerial;
        SortedSet points = Collections.synchronizedSortedSet(new TreeSet(new RangePointComparator()));
        Map ageLabelSWIs;
        StringWrappingInfo nameSWI;
        double width = 0;
        double widthSubTree = 0;
        double lineX = 0;
        double ageLabelRightX = 0;
        // family tree stuff
        Range parent = null;
        Range containedBySpeciesRange = null;
        java.util.List<Range> children = new ArrayList<Range>();
        int location = Integer.MAX_VALUE;
        boolean rangeTraversed4Location = false;
        double rangeXCanvas;
        double baseofRange;
        boolean branchDisabled = false;
        boolean disabled4Priority = false;
        boolean overridePriority = false;
        private int rangePrio = 10;
        String branchInfoTemp = "";
        //Following six fields are used to trap the exception resulting from branch nodes age
        //being out of range of Range's starting and ending age.
        public ArrayList<CriticalErrors> flaggingBranchBelowMsg = new ArrayList<CriticalErrors>();
        public ArrayList<CriticalErrors> flaggingBranchAboveMsg = new ArrayList<CriticalErrors>();
        private boolean flaggingBranchBelowErr = false;
        private boolean flaggingBranchAboveErr = false;
        public double branchBase = Double.NaN;
        public double branchTop = Double.NaN;
        public String branchColor = "rgb(0,0,0)"; //black
        String subLabel = null;
        StringWrappingInfo subLabelSWI;
        // drawing information
        double rangeWidth = 0;
        double nameWidth;
        double nameHeight;
        LabelLocation labelloc = LabelLocation.UNDECIDED;
        
        public boolean isPhenonRange = false;	//Whether this range is a phenon range or a species range
        public String speciesNameWithFinalTop = null; //Which species range contains the final top of the phenon range
        public ArrayList<Range> phenonsContainingSpecies = new ArrayList<Range>();
        
        public String branchedAsLeftOrRight = null;
        // Symbol for range top and base
        public int topSymbol;
        public int baseSymbol;
        
        // Phenon top and base symbol constants for the Species-Phenon integrated tree
        public static final int PHENON_FINAL_TOP_LIVING_SYMBOL = 0;
        public static final int PHENON_FINAL_TOP_SYMBOL = 1;
        public static final int PHENON_BASE_SYMBOL = 2;
        public static final int PHENON_TOP_CONTINUATION_SYMBOL = 3;
        public static final int PHENON_BASE_CONTINUATION_SYMBOL = 4;
        
        public boolean speciesBoxBranchIsVisible = true;
        
        public Range() {
            mySerial = rangeSerial;
            rangeSerial++;

            if (drawAgeLabel) {
                ageLabelSWIs = Collections.synchronizedMap(new HashMap());
            }
        }
        
        // constructor to deep copy existing range
        public Range(Range r) {
        	this.ageLabelRightX = r.ageLabelRightX;
        	if (r.ageLabelSWIs != null && !r.ageLabelSWIs.isEmpty())
        		this.ageLabelSWIs = Collections.synchronizedMap(r.ageLabelSWIs);
        	else 
        		this.ageLabelSWIs = r.ageLabelSWIs;
        	
        	this.base = r.base;
        	this.baseofRange = r.baseofRange;
        	this.baseSymbol = r.baseSymbol;
        	this.branchBase = r.branchBase;
        	this.branchColor = r.branchColor;
        	this.branchDisabled = r.branchDisabled;
        	this.branchInfoTemp = r.branchInfoTemp;
        	this.branchTop = r.branchTop;
        	
        	if (r.children != null) {
        		Iterator it1 = r.children.iterator();
            	while(it1.hasNext()) {
            		Range rn = (Range)it1.next();
            		this.children.add(rn);
            	}
        	} 
        	
        	this.containedBySpeciesRange = r.containedBySpeciesRange;
        	this.disabled4Priority = r.disabled4Priority;
        	this.flaggingBranchAboveErr = r.flaggingBranchAboveErr;
        	
        	if (r.flaggingBranchAboveMsg != null) {
        		Iterator it2 = r.flaggingBranchAboveMsg.iterator();
            	while(it2.hasNext()) {
            		CriticalErrors ce = (CriticalErrors)it2.next();
            		this.flaggingBranchAboveMsg.add(ce);
            	}	
        	} 
        	
        	this.flaggingBranchBelowErr = r.flaggingBranchAboveErr;
       
        	if (r.flaggingBranchBelowMsg != null) {
        		Iterator it3 = r.flaggingBranchBelowMsg.iterator();
            	while(it3.hasNext()) {
            		CriticalErrors ce = (CriticalErrors)it3.next();
            		this.flaggingBranchBelowMsg.add(ce);
            	}	
        	} 
        	
        	this.isPhenonRange = r.isPhenonRange;
        	this.labelloc = r.labelloc;
        	this.lineX = r.lineX;
        	this.location = r.location;
        	this.mySerial = r.mySerial;
        	this.name = r.name;
        	this.nameHeight = r.nameHeight;
        	this.nameSWI = r.nameSWI;
        	this.nameWidth = r.nameWidth;
        	this.overridePriority = r.overridePriority;
        	this.parent = r.parent;
        	if (r.points != null && !r.points.isEmpty()) {
        		Iterator<RangePoint> iterPoint = r.points.iterator();
        		while(iterPoint.hasNext()) {
        			RangePoint p = iterPoint.next();
        			points.add(p);
        		}
        	}
        	else 
        		this.points = r.points;
        	this.popup = r.popup;
        	this.rangePrio = r.rangePrio;
        	this.rangeTraversed4Location = r.rangeTraversed4Location;
        	this.rangeWidth = r.rangeWidth;
        	this.rangeXCanvas = r.rangeXCanvas;
        	this.speciesNameWithFinalTop = r.speciesNameWithFinalTop;
        	if (r.phenonsContainingSpecies != null && !r.phenonsContainingSpecies.isEmpty()) {
        		Iterator it4 = r.phenonsContainingSpecies.iterator();
        		while(it4.hasNext()) {
        			Range tr = (Range)it4.next();
        			this.phenonsContainingSpecies.add(tr);
        		}	
        	}
        	this.subLabel = r.subLabel;
        	this.subLabelSWI = r.subLabelSWI;
        	this.top = r.top;
        	this.topSymbol = r.topSymbol;
        	this.width = r.width;
        	this.widthSubTree = r.widthSubTree;
        }
        
        public class CriticalErrors {

            double age = Double.NaN;
            public String range = "";
            public String branchTo = "";

            public CriticalErrors(double age, String parent, String child) {
                this.age = age;
                this.range = parent;
                this.branchTo = child;
            }
        }

        // function to add range data point to an range
        public void addPoint(RangePoint p) {

			/*	Add Point keeps track of age related errors for family tree.
             * 	If the age of a branch exceeds the range's start and ending point
			 *  it is added to list of error messages. As and when we find new
			 *  range points covering these branch points the corresponding error points are deleted.
			 *  In the end only those items remain which are actual errors in the datafile.
			 */
            if (Double.isNaN(base) || p.age > base || p.rcd.branch) {
                if (p.rcd.branch) {
                    if (Double.isNaN(branchBase) || p.age > branchBase ) {
                        branchBase = p.age;
                    }
                    
                    if (Double.isNaN(base) || p.age > base) {
                    	flaggingBranchBelowErr = true;
                        if (flaggingBranchBelowMsg == null) {
                            flaggingBranchBelowMsg = new ArrayList<CriticalErrors>();
                        }

                        flaggingBranchBelowMsg.add(new CriticalErrors(p.age, p.name.split(" <img")[0], p.rcd.branchTo));

                    }
                } else {
                    base = p.age;

                    if (flaggingBranchBelowMsg != null) {
                        Iterator iter = flaggingBranchBelowMsg.iterator();
                        while (iter.hasNext()) {
                            CriticalErrors error = (CriticalErrors) iter.next();
                            if (error.age <= p.age) {
                                iter.remove();
                            }
                        }

                        if (flaggingBranchBelowMsg.isEmpty()) {
                            flaggingBranchBelowErr = false;
                        }
                    } else {
                        flaggingBranchBelowErr = false;
                    }
                }
            }
            if (Double.isNaN(top) || p.age < top || p.rcd.branch) {
                if (p.rcd.branch) {
                    if (Double.isNaN(branchTop) || p.age < branchTop) {
                        branchTop = p.age;
                    }
                    if (Double.isNaN(top) || p.age < top) {
                    	flaggingBranchAboveErr = true;
                        if (flaggingBranchAboveMsg == null) {
                            flaggingBranchAboveMsg = new ArrayList<CriticalErrors>();
                        }

                        flaggingBranchAboveMsg.add(new CriticalErrors(p.age, p.name.split(" <img")[0], p.rcd.branchTo));
                    }
                } else {
                    top = p.age;
                    if (flaggingBranchAboveMsg != null) {
                        Iterator iter = flaggingBranchAboveMsg.iterator();
                        while (iter.hasNext()) {
                            CriticalErrors error = (CriticalErrors) iter.next();
                            if (error.age >= p.age) {
                                iter.remove();
                            }
                        }

                        if (flaggingBranchAboveMsg.isEmpty()) {
                            flaggingBranchAboveErr = false;
                        }
                    } else {
                        flaggingBranchAboveErr = false;
                    }
                }
            }
            points.add(p);

            rangeWidth = Math.max(rangeWidth, getRangeStyle(p.type).width);
        }

        public void setNotIncludeBranch() {
            if (branchDisabled) {
                branchDisabled = false;
            } else {
                branchDisabled = true;
            }
        }

        public boolean getNotIncludeBranch() {
        	boolean integratedTreeDrawing = false;
        	if (rangesWithoutSplit.size() < extendedRanges.size()) {
        		integratedTreeDrawing = true;
        	}
        	if (integratedTreeDrawing && !this.isPhenonRange) {
        		RangeColumn.speciesBoxBranchClicked = true;
        		Queue<Range> speciesRangeQueue = new LinkedList<Range>();
        		speciesRangeQueue.add(this);
        	
        		while(!speciesRangeQueue.isEmpty()) {
//        			dequeue the top speciesRange from the queue
        			Range thisRange = speciesRangeQueue.poll();
        			
//        			add all the child speciesRange to the queue
        			Iterator<Range> thisRangeChildrenIterator = thisRange.children.iterator();
        			while (thisRangeChildrenIterator.hasNext()) {
        				Range childSpeciesRange = thisRangeChildrenIterator.next();
        				speciesRangeQueue.add(childSpeciesRange);
        			}
        			
//        			perform branch disabling for all the phenon ranges of the dequeued species range
        			if (thisRange.phenonsContainingSpecies.size() > 0) {
                    	Iterator<Range> iterPhenon = thisRange.phenonsContainingSpecies.iterator();
                    
                    	while(iterPhenon.hasNext()) {
                    		Range phenonRange = iterPhenon.next();
                    		Iterator<Range> iterE = extendedRanges.iterator();
                    		while (	iterE.hasNext()) {
                    			Range r = iterE.next();
                    			if (r.isPhenonRange) {
                    				if (phenonRange.name.equalsIgnoreCase(r.name) == true 
                    						&& phenonRange.top == r.top
                    						&& phenonRange.base == r.base) {
                    					if (r.speciesBoxBranchIsVisible) {
                    						r.branchDisabled = true;
                    					} else {
                    						r.branchDisabled = false;
                    					}
                    					r.speciesBoxBranchIsVisible = !r.speciesBoxBranchIsVisible;
                    				}
                    			}
                    		}
                    	}
                	}
        		}
        	} 
        	
        	return !(branchDisabled || disabled4Priority);
        }

        // function to calculate width of a range
        public double calculateWidth(ImageGenerator ig) {
            double maxAgeLabelWidth = 0;
            
            
            if (this.isPhenonRange == false) { // Range Box/Species Range
            	nameSWI = ig.getSWIOneLine(name, fonts.getFont(FontManager.RANGE_BOX_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);
            } else {
            	nameSWI = ig.getSWIOneLine(name, fonts.getFont(FontManager.RANGE_LABEL_FONT), StringWrappingInfo.VERTICAL, fileInfo);
            }
            
            // create the age labels
            if (drawAgeLabel) {
                Iterator iter = points.iterator();
                while (iter.hasNext()) {
                    RangePoint p = (RangePoint) iter.next();
                    StringWrappingInfo swi = ig.getSWIOneLine(getAgeLabel(p.age), fonts.getFont(FontManager.AGE_LABEL_FONT), StringWrappingInfo.NORMAL, fileInfo);
                    ageLabelSWIs.put(p, swi);
                    double lwidth = swi.getWidth();
                    if (lwidth > maxAgeLabelWidth) {
                        maxAgeLabelWidth = lwidth;
                    }
                }
            }

            nameWidth = nameSWI.getWidth();//.getFontHeight();
            nameHeight = nameSWI.getHeight();

            // already set: rangeWidth = MAX_RANGE_THICKNESS;

            double rangePlusAgeLabels = rangeWidth + maxAgeLabelWidth;

            ageLabelRightX = maxAgeLabelWidth;
            if (labelloc == LabelLocation.LEFT_SIDE || labelloc == LabelLocation.RESIZE) {
                lineX = ageLabelRightX - rangeWidth / 2;
                width = rangePlusAgeLabels + RANGE_LABEL_TO_RANGE_MARGIN + nameWidth;
            } else if (labelloc == LabelLocation.RIGHT_SIDE || labelloc == LabelLocation.RESIZE) {
                lineX = ageLabelRightX + rangeWidth / 2;
                width = rangePlusAgeLabels + RANGE_LABEL_TO_RANGE_MARGIN + nameWidth;
            } else {
                lineX = ageLabelRightX + Math.max(rangeWidth / 2, nameWidth / 2);
                width = Math.max(nameWidth, rangePlusAgeLabels);
            }
            
            if (!isPhenonRange) {
            	width += nameWidth + WIDTH_FOR_SPECIES_BOX_INTERVAL;
            }
            
            

            //ageLabelRightX = lineX - SAMPLE_RADIUS;
            //lineX = width - nameWidth / 2;
            return width;
        }

        public double getWidth() {
            return width;
        }

        public double getLineX() {
            return lineX;
        }

        public double getAgeLabelRightX() {
            return ageLabelRightX;
        }

        // function to get age label
        public StringWrappingInfo getAgeLabelSWI(RangePoint p) {
        	StringWrappingInfo prevSWI = (StringWrappingInfo)ageLabelSWIs.get(p);
        	String ages = prevSWI.s.getOriginalString();
        	
        	StringWrappingInfo newSWI = prevSWI;
        	
        	// Add additional zeros to make two digits after the decimal point
        	String splittedString[] = ages.split("\\.");
        	if (splittedString.length > 0) {
        		String beforeDecS = splittedString[0];
        
        		String newAge = "";
        		if (splittedString.length == 2) {
        			String afterDecS = splittedString[1];
        			if (afterDecS.length() == 1)
        				afterDecS += "0";
        			else if (afterDecS.length() == 0)
        				afterDecS += "00";
        			newAge = (beforeDecS + "." + afterDecS);
        		} else if (splittedString.length == 1) {
        			newAge = beforeDecS + ".00";
        		}

        		if (newAge != "") {
        			newSWI = new StringWrappingInfo(prevSWI.g, 
        					new RichText(newAge, null), prevSWI.origFont, prevSWI.getOrientation());
        			newSWI.makeOneLine();
        			newSWI.useOriginalLineBreaks();
        		}
        	} else {
        		//System.out.println("No split by decimal points.");
        	}

            return newSWI;
        }

        /*
        public void negateRange() {
        RangePoint p, breaker = null;
        double lastAge = 0;
        Iterator iter = points.iterator();

        SortedSet ret = Collections.synchronizedSortedSet(new TreeSet(points.comparator()));

        while (iter.hasNext()) {
        p = (RangePoint)iter.next();

        if (p.type.compareToIgnoreCase("sample") == 0) {
        p.age = -p.age;
        ret.add(p);
        continue;
        }

        if (breaker == null) {
        breaker = p;
        lastAge = p.age;
        continue;
        }

        double thisAge = p.age;
        p.age = -lastAge;
        ret.add(p);
        lastAge = thisAge;
        }

        // finish off
        if (breaker != null) {
        breaker.age = -lastAge;
        ret.add(breaker);
        }

        points = ret;
        double temp = top;
        top = -base;
        base = -temp;
        }*/
        public int locAttribute(int upperbound) {

            widthSubTree = 0;

            int numChilds;
            if (children != null) {
                numChilds = children.size();
            } else {
                numChilds = 0;
            }

            int minLocation = upperbound;
            int i = 0;
            Range range = null;
            while (i < numChilds) {
                range = children.get(((numChilds - i - 1) / 2) * 2);
            	//range = children.get(numChilds - i - 1);
                if (range.branchDisabled == false) {
                    minLocation = range.locAttribute(minLocation);

                    //find the maximum width child of all the childs for a parent on one side.
                    if (widthSubTree < range.width) {
                        widthSubTree = range.width;
                    }
                }
                i = i + 2;
                //i = i + 1;
            }

            int count = 0;
            if (disabled4Priority == false) {
                if (minLocation == Integer.MAX_VALUE) {
                    location = 0;
                    /*
                    if (ConserveSpace) {
                        if (assignedLocationList == null) {
                            assignedLocationList = new LinkedList<RangeSize>();
                        }
                        assignedLocationList.addFirst(new RangeSize(base, top, width, this));
                    }
                    */
                } else {
                	/*
                    if (ConserveSpace) {
                        Iterator iter = assignedLocationList.iterator();
                        RangeSize rangeSize = null;
                        RangeSize prevRangeSize = null;
                        while (iter.hasNext()) {
                            rangeSize = (RangeSize) iter.next();
                            if ((rangeSize.range.parent == this)) {
                                break;
                            }
                            if (((base <= rangeSize.base) && (base >= rangeSize.top)) || ((top <= rangeSize.base) && (top >= rangeSize.top))) {
                                break;
                            }
                            prevRangeSize = rangeSize;
                            count++;
                        }

                        if (prevRangeSize != null) {
                            if (base < prevRangeSize.base) {
                                prevRangeSize.top = top;
                            } else {
                                prevRangeSize.base = base;
                            }
                            prevRangeSize.range = this;
                            if (prevRangeSize.maxWidth < width) {
                                prevRangeSize.maxWidth = width;
                            }
                        } else {
                            assignedLocationList.addFirst(new RangeSize(base, top, width, this));
                        }
                    }
                    */

                    location = minLocation + count - 1;
                }
                if (count == 0) {
                    minLocation = location;
                }

                // add width of one side of range with width of the range.
                widthSubTree += width;
            }
            rangeTraversed4Location = true;

            i = 1;
            double widthSubTreeRight = 0;
            while (i < numChilds) {
                range = children.get(i);
                if (range.branchDisabled == false) {
                    minLocation = range.locAttribute(minLocation);

                    //find the maximum width child of all the childs for a parent on the other side.
                    if (widthSubTreeRight < range.width) {
                        widthSubTreeRight = range.width;
                    }
                }
                i = i + 2;
            }

            widthSubTree += widthSubTreeRight;

            return minLocation;
        }
        
        public void printRangeInfo() {
        	System.out.println("Name = " + name);
        	System.out.println("Base = " + base);
        	System.out.println("Top =" + top);
        	if (containedBySpeciesRange != null) {
        		System.out.println("Contained by Species Range = " + containedBySpeciesRange.name);
        	}
        	System.out.println("Number of Phenons = " + phenonsContainingSpecies.size());
        	System.out.println("Number of Points = " + points.size());
        	Iterator<RangePoint> iterPoint = points.iterator();
        	int count = 0;
        	while (iterPoint.hasNext()) {
        		RangePoint p = iterPoint.next();
        		System.out.println("--- Point " + ++count + ":");
        		p.printRangePointInfo();
        		System.out.println();
        	}
        	System.out.println("Age Label Right X = " + ageLabelRightX);
        	System.out.println("Base Of Range = " + baseofRange);
        	System.out.println("Base Symbol = " + baseSymbol);
        	System.out.println("Branch Base = " + branchBase);
        	System.out.println("Branch Color = " + branchColor);
        	System.out.println("Branch Info Temp = " + branchInfoTemp);
        	System.out.println("Branch Top = " + branchTop);
        	System.out.println("Line X = " + lineX);
        	System.out.println("Location = " + location);
        	System.out.println("My Serial = " + mySerial);
        	System.out.println("Name Height = " + nameHeight);
        	System.out.println("Name Width = " + nameWidth);
        	System.out.println("Popup = " + popup);
        	System.out.println("Range Priority = " + rangePrio);
        	System.out.println("Range Width = " + rangeWidth);
        	System.out.println("Range X Canvas = " + rangeXCanvas);
        	System.out.println("Species Name With Final Top = " + speciesNameWithFinalTop);
        	System.out.println("SubLabel = " + subLabel);
        	System.out.println("Top Symbol = " + topSymbol );
        	System.out.println("Width = " + width);
        	System.out.println("Width Sub Tree = " + widthSubTree);
        	System.out.println();
        }
    }

    // Comparator class for ranges
    protected static class RangeComparator implements java.util.Comparator {

        public static final int FIRST_OCCURANCE = 1;
        public static final int LAST_OCCURANCE = 2;
        public static final int ALPHABETICAL = 3;
        public static final int OTHER = 0;
        int type;

        /**
         *
         */
        public RangeComparator(int type) {
            super();

            this.type = type;
        }

        /**
         * Compare Datapoint objects based on their age.
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            // convert parameters to Datapoint classes
            Range left = (Range) arg0;
            Range right = (Range) arg1;
            if (left == null || right == null) {
                return 0;
            }

            // do the comparisons. Order:
            // 1st. chosen one
            // 2nd. if by 1st, do last in reverse order.
            //      if by last, do 1st in reverse order.
            // 3rd. alphabetical
            // 4th. default (roughly date/file order)
            int result = compareByType(left, right, type);

            if (result == 0 && type == FIRST_OCCURANCE) {
                result = -compareByType(left, right, LAST_OCCURANCE); // reversed order
            } else if (result == 0 && type == LAST_OCCURANCE) {
                result = -compareByType(left, right, FIRST_OCCURANCE); // reversed order
            }
            if (result == 0 && type != ALPHABETICAL) {
                result = compareByType(left, right, ALPHABETICAL);
            }

            if (result == 0) {
                result = compareByType(left, right, OTHER);
            }

            return result;
        }

        public int compareByType(Range left, Range right, int type) {
            double result;

            if (type == FIRST_OCCURANCE) {
                if (Double.isNaN(left.base) && Double.isNaN(right.base)) {
                    return 0;
                }
                if (Double.isNaN(left.base)) {
                    return -1;
                }
                if (Double.isNaN(right.base)) {
                    return 1;
                }
                result = right.base - left.base;
                if (result > -NumberUtils.EQUAL_MARGIN && result < NumberUtils.EQUAL_MARGIN) {
                    return 0;
                }
                if (result > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (type == LAST_OCCURANCE) {
                if (Double.isNaN(left.top) && Double.isNaN(right.top)) {
                    return 0;
                }
                if (Double.isNaN(left.top)) {
                    return -1;
                }
                if (Double.isNaN(right.top)) {
                    return 1;
                }
                result = left.top - right.top;
                if (result > -NumberUtils.EQUAL_MARGIN && result < NumberUtils.EQUAL_MARGIN) {
                    return 0;
                }
                if (result > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
            if (type == ALPHABETICAL) {
                return left.name.compareToIgnoreCase(right.name);
            }

            return left.mySerial - right.mySerial;
        }
    }
    
    
    // function where splitting of phenon ranges happen
    public boolean findSpecificRanges(Settings settings, boolean forPhenonRange, boolean choosePhenonRangeSet) {
     	ArrayList<Range> ScratchRanges = new ArrayList<Range>();
        
     	// find the pairings by looking for identical names
     	java.util.List<RangePoint> endpoints = new ArrayList<RangePoint>(this.data.size());
     	getEndPoints(endpoints);
        
        boolean tmpIsPhenonRange = false;
        String tmpSpeciesNameWithFinalTop = null;
    	// go at each datapoint and try to find a match for it
        Iterator iter = endpoints.iterator();
        int count = 0;
        while (iter.hasNext()) {
            RangePoint rp = (RangePoint) iter.next();
            
            // checking whether to skip current iteration
            if ( (forPhenonRange == false && rp.rcd.speciesOrPhenon != null && rp.rcd.speciesOrPhenon.equals("phenon") == true) ||	
               	 (forPhenonRange == true && rp.rcd.speciesOrPhenon != null && rp.rcd.speciesOrPhenon.equals("phenon") == false) ||
               	 (forPhenonRange == true && rp.rcd.speciesOrPhenon == null)
               ) {
               	continue;
            } 
            
            if (rp.used) // see if this point is already done
            {
                continue;
            }
           
            Iterator matchIter = ScratchRanges.iterator();
            while (matchIter.hasNext()) {
                Range match = (Range) matchIter.next();
                
                if (rp.name.split(" <img")[0].equalsIgnoreCase(match.name.split(" <img")[0]) == false) {
                    // If phenon information available for the points
                    if ( rp.rcd.speciesOrPhenon != null && 
                    	 rp.rcd.speciesOrPhenon.equals("phenon") &&
                    	 rp.rcd.speciesWithFinalTopOfPhenon != null) {
                    	tmpIsPhenonRange = true;
                    	tmpSpeciesNameWithFinalTop = rp.rcd.speciesWithFinalTopOfPhenon;
                    }
                	continue;
                }
            	
                rp.used = true;
               
                // rp points are iterated from top (low) to bottom (high) here (may change depending on sort)
            	if (rp.rcd.speciesOrPhenon != null && rp.rcd.speciesOrPhenon.equals("phenon") ) {
            		if (rp.rcd.speciesWithFinalTopOfPhenon != null) {
                		match.isPhenonRange = true;
                		match.speciesNameWithFinalTop = rp.rcd.speciesWithFinalTopOfPhenon;
                		if (choosePhenonRangeSet)  { // Find top only when we are dealing with phenon ranges only. Don't change when being called from extendRangeSets()
                			match.top = rp.age;      // Update top age for the match range with each iteration
                		}
            		} else { 
            			if (choosePhenonRangeSet && rp.age > match.base)   // Find top only when we are dealing with phenon ranges only. Don't change when being called from extendRangeSets()
            				match.base = rp.age;
            		}
            	}
            	
                if (!choosePhenonRangeSet) {// Don't add points when being called from extendRangeSets()
                	match.addPoint(rp);     // Top point added before and so will be skipped now
                }
                if (rp.rcd.notInclude == false) {
                    match.branchDisabled = false;
                } else {
                    match.branchDisabled = true;
                }

                if (match.name.length() < rp.name.length()) {
                		match.name = rp.name;
                }

                if (rp.popup != null) {
                    if (rp.rcd.branch) {
                        match.branchInfoTemp += "<br><br><b>Branch Info. of " + rp.rcd.branchTo + ": </b>" + rp.popup;
                    } else {
                        match.popup += "<br><br>" + rp.popup;
                    }
                }
            }

            if (!rp.used) {
                // create a new range for this point
                Range range = new Range();
                range.name = rp.name;
                if (rp.popup != null) {
                    if (rp.rcd.branch) {
                        range.branchInfoTemp += "<br><br><b>Branch Info. of " + rp.rcd.branchTo + ": </b>" + rp.popup;
                    } else {
                        range.popup = rp.popup;
                    }
                }
                rp.used = true;
                if (!choosePhenonRangeSet) // Don't add points when being called from extendRangeSets()
                	range.addPoint(rp);
                else if (Double.isNaN(range.base)) {
                	range.base = rp.age;
                }

                ScratchRanges.add(range);
            }
        }

        
        iter = ScratchRanges.iterator();
        while (iter.hasNext()) {
            Range r = (Range) iter.next();
            Iterator<RangePoint> iterPoints = r.points.iterator();
            while (iterPoints.hasNext()) {
            	RangePoint rp = iterPoints.next();
            	if (rp.rcd.speciesOrPhenon != null && rp.rcd.speciesOrPhenon.equals("phenon") && rp.rcd.speciesWithFinalTopOfPhenon != null) {
            		r.isPhenonRange = true;
            		r.speciesNameWithFinalTop = rp.rcd.speciesWithFinalTopOfPhenon;
            	}
            }
     

			/*
			 * Function addPoint is modified to incorporate error handling and those changes
			 * require use of two types of age range for Ranges. One - for normal range points (base, top)
			 * and the other for branch points - (branchBase, branchTop). So, two extra conditions are added
			 * to this if condition.
			 */
//			if ((r.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(r.base))
//					|| (r.branchBase < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(r.branchBase))
//					|| (r.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(r.top))
//					|| (r.branchTop > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(r.branchTop))) {
//				continue;
//			}
            // Commented the above condition as we need all the datapoints to determing the branch color inheritance and
            // also whether a branch should be on or off based on the parent.

            Iterator iterErr;
            if (r.flaggingBranchAboveErr) {
                iterErr = r.flaggingBranchAboveMsg.iterator();
                while (iterErr.hasNext()) {
                    Range.CriticalErrors error = (Range.CriticalErrors) iterErr.next();
                    consolidatedPlottingErrs += "Range " + error.range + " has branch node at " + error.age + " Ma to " + error.branchTo + " above top age " + r.base + " Ma.\n";
                }
            }
            if (r.flaggingBranchBelowErr) {
                iterErr = r.flaggingBranchBelowMsg.iterator();
                while (iterErr.hasNext()) {
                    Range.CriticalErrors error = (Range.CriticalErrors) iterErr.next();
                    consolidatedPlottingErrs += "Range " + error.range + " has branch node at " + error.age + " Ma to " + error.branchTo + " below base age " + r.base + " Ma.\n";
                }
            }
            
            if (choosePhenonRangeSet) { // If phenonRanges set is chosen
            	phenonRanges.add(r);
            } else {				    // If phenon-species combined set is chosen
            	ranges.add(r);
            }
        }
        
        return true;
    }

    // function to extend range sets by splitting phenons for integrated tree and to link parent and child ranges
    public boolean findRanges(Settings settings, ImageGenerator ig, int sortBy) {
    	SortedSet<Range> tmpSavedExtendedRanges = null;

    	// Initializing ranges
        ranges = Collections.synchronizedSortedSet(new TreeSet(new RangeComparator(sortBy)));
        rangesWithoutSplit = Collections.synchronizedSortedSet(new TreeSet(new RangeComparator(sortBy)));
        phenonRanges = Collections.synchronizedSortedSet(new TreeSet(new RangeComparator(sortBy)));
        extendedRanges = Collections.synchronizedSortedSet(new TreeSet(new RangeComparator(sortBy)));

        if (!findSpecificRanges(settings, false, false)) { // Firstly deal only with species ranges and the second argument is to choose species-phenon combined ranges
        	return false;
        }
       
//        Iterator<Range> iterRn = ranges.iterator();
//        while (iterRn.hasNext()) {
//        	Range r = iterRn.next();
//        	r.printRangeInfo();
//        }
        
        // Each parent and children branch is doubly linked. Species and Phenon range determination also will be done in this step
        // firstly link only the species range
        linkRangeSets(false); // true for phenon range, false for species range    

        // Use the species range parent and child links to split the phenon ranges
        // Split the phenon ranges over species boxes and extend the range set
        if (this.speciesPhenonTreeDrawing) {
        	extendRangeSets(settings);
            
            if (!findSpecificRanges(settings, true, false)) { // Now deal with phenon ranges, the second argument chooses the species-phenon combined set
            	return false;
            }
        }
        
        // Now link the parent and child phenon ranges.
        // The linking is being done in two stages, first for species above with forPhenonRange=false and next for phenons, because of 
        //  the new species-phenon integrated tree construction. It used to be done only once 
        //  before as we had only separate species and phenon trees, didn't have a species-phenon integrated tree.
        // In the extendRangeSets() function above the phenon ranges are getting splitted into different species boxes, 
        //  therefore before even having the phenon ranges yet, linking the parent phenon with a child phenon
        //  does not make any sense. That is why, the next liking with forPhenonRange=true is going to be done. This way,
        //  phenons won't have unwanted children ranges or range points due to the splitting done in the previous step.
        if (this.sideBySideTreeStructure) {
        	this.speciesPhenonTreeDrawing = false;
        	RangeColumn.speciesBoxBranchClicked = false;
        } else if (this.speciesPhenonTreeDrawing == true){
        	this.integratedTreeStructure = true;
        	this.speciesPhenonTreeDrawing = true;
        }
        
        if (this.speciesPhenonTreeDrawing) { // integratedTreeStructure flag is set from the settings
        	rangesWithoutSplit = ranges;
        	ranges = extendedRanges;
        	linkRangeSets(true);
        } else {
        	linkRangeSets(true);
        	rangesWithoutSplit = ranges; // Storing the ranges with no split
        }
        
        
        if (this.speciesPhenonTreeDrawing) {
        	Iterator<Range> iterRange = extendedRanges.iterator();
        	while (iterRange.hasNext()) {
        		Range r = iterRange.next();
        		if (!r.isPhenonRange) {
        			Iterator<Range> iterPhenonRange = r.phenonsContainingSpecies.iterator();
        			while (iterPhenonRange.hasNext()) {
        				Range phenonRange = iterPhenonRange.next();
        				phenonRange.containedBySpeciesRange = r;
        			}
        		}
        	}
        }
              
        if (this.speciesPhenonTreeDrawing && RangeColumn.speciesBoxBranchClicked && tmpSavedExtendedRanges != null && tmpSavedExtendedRanges.size() > 0) {
        	RangeColumn.speciesBoxBranchClicked = false;
        	
        	Iterator<Range> iterPhenon = tmpSavedExtendedRanges.iterator();
            while (iterPhenon.hasNext()) {
            	Range phenonRange = iterPhenon.next();
            	if (phenonRange.isPhenonRange) {
            		Iterator<Range> iterExtendedRanges = extendedRanges.iterator();
            		
            		while (iterExtendedRanges.hasNext()) {
            			Range r = iterExtendedRanges.next();
            			
         				if ( !(phenonRange.name.equalsIgnoreCase(r.name) == true && 
         						phenonRange.top == r.top && 
         						phenonRange.base == r.base)) {
         					continue;
         				}
         		
                		r.branchDisabled = phenonRange.branchDisabled;
                		r.speciesBoxBranchIsVisible = phenonRange.speciesBoxBranchIsVisible;
            		}
            	}
            }
        }
        
        if (extendedRanges != null && extendedRanges.size() > 0 && this.speciesPhenonTreeDrawing) {
        	Iterator<Range> iterExtendedRanges = extendedRanges.iterator();
            while (iterExtendedRanges.hasNext()) {
            	Range r = iterExtendedRanges.next();
            	if (r.isPhenonRange) {
            		if (r.containedBySpeciesRange.branchDisabled) {
            			phenonRangeStateMap.put(r,r.branchDisabled);
            			r.branchDisabled = true;
            		}
            	}
            }
        }
        
        if (consolidatedPlottingErrs.equals("") == false) {
            consolidatedPlottingErrs = "Could not proceed to Family Tree Plotting. Following errors present in datafile.\n" + consolidatedPlottingErrs + "\nIf a range has age NaN(Infinity) understand that its regular range points are not defined. The most common reason for this could be incorrect spelling between range & branch points\n";
            return false;
        }
        
        return true;
    }

    public static boolean exceptionHandling = false;
    public static String consolidatedPlottingErrs = "";

    
    // utility function to draw age label for range points
    public void drawAgeLabelForRangePoint(ImageGenerator ig, 
    									  Range range, 
    									  RangePoint p, 
    									  double rangeX,
    									  double startx, 
    									  double starty, 
    									  double height, 
    									  Settings settings
    									 ) {
    	// DRAW AGE LABEL
        StringWrappingInfo swi = range.getAgeLabelSWI(p);
        double ageWidth = swi.getWidth();
        double ageHeight = swi.getHeight();
        double ageY = ImageGenerator.getYFromYear(p.age, starty, settings);
        
        ageY -= ageHeight / 2;
        if (ageY < starty) {
        	ageY = starty;
        }

        if (ageY + ageHeight > starty + height) {
        	ageY = starty + height - ageHeight;
        }

        if (range.labelloc == LabelLocation.LEFT_SIDE) {
        	ig.drawString(swi,
                rangeX + AGE_LABEL_X_PADDING - range.getAgeLabelRightX() - ageWidth / 2.5, // x
                ageY - AGE_LABEL_Y_PADDING, // y
                ageWidth,
                ageHeight,
                ImageGenerator.CENTER);
        } else {
        	ig.drawString(swi,
                rangeX - AGE_LABEL_X_PADDING + range.getAgeLabelRightX() + ageWidth, // x
                ageY - AGE_LABEL_Y_PADDING, // y
                ageWidth,
                ageHeight,
                ImageGenerator.CENTER);
        }
    }

    // function called by image generator to draw range/tree column data on the svg canvas
    // this function ultimately draws the vertical range lines by estimating the position of ranges and their children
    @Override
    public void drawData(ImageGenerator ig, double startx, double starty, double width, double height, Settings settings) {
        super.drawData(ig, startx, starty, width, height, settings);

        int tempRangeLocation = 0;

        Range range = null;   // To iterate through ranges in rangesCurrLocation
        Range rangeInRanges = null;  // To iterate through ranges in either extendedRanges, rangesWithoutSplit or ranges  
       
        //overlapShift - prevents the text from overlapping on the larger range lines (e.g flood)
        //myLineShift - Shifts the placement of new lines as the previous lines/text are shifted to prevent overlap.
        double rangeX, lineX, myWidth, maxWidth, maxRangeX = 0, myLineShift = 0, overlapShift = 0;
        double rangeStart = startx + RANGE_PADDING;

        double prevWidth = 0;
        double prevX = 0;
        
        double prevTop = 0;
        double prevBase = 0;
        double prevHeight = 0;
        
        double currentHeight = 0;
        
        double minTop = 0;
        double maxBase = 0;
        
        boolean isTop = false;

        Iterator iter = null;
        ArrayList<String> blackListedRangesForPhenonRangeDrawing = new ArrayList<String>();
        
        boolean nextHorizontalTopLineDrawing = true;
        Range prevSpeciesRange = null;
        Range prevPhenonRange = null;
        HashMap<String, Integer> phenonCountForCurrentRange = new HashMap<String, Integer>();
        HashMap<String, Integer> totalEnabledPhenonForCurrentRange = new HashMap<String, Integer>();
        String prevRangeName = null;

    	Iterator<Range> iterE = extendedRanges.iterator();
    	while (iterE.hasNext()) {
    		Range speciesRange = iterE.next();
    		if (!speciesRange.isPhenonRange) {
//    			initially phenon count will be zero
    			phenonCountForCurrentRange.put(speciesRange.name.split(" <img")[0], 0);

//    			counting how many phenons are disabled and putting into the hash map
    			int totalEnabledPhenon = 0;
				Iterator<Range> iterR = speciesRange.phenonsContainingSpecies.iterator();
				while (iterR.hasNext()) {
					Range r = iterR.next();
					if (r.branchDisabled == false)
						totalEnabledPhenon++;
				}
				totalEnabledPhenonForCurrentRange.put(speciesRange.name.split(" <img")[0], totalEnabledPhenon);
    		}
    	}

        while (true) {
            rangeX = 0;
            lineX = 0;
            myWidth = 0;
            maxWidth = 0;
            ArrayList<Range> rangesCurrLocation = null;
            if (tempRangeLocation < minLocation) {
                break;
            }

            if (this.speciesPhenonTreeDrawing) {
            	iter = extendedRanges.iterator();
			} else if (rangesWithoutSplit != null){
            	iter = rangesWithoutSplit.iterator();
            } else {
            	iter = ranges.iterator();
            }

            int count = 0;
            while (iter.hasNext()) {
                rangeInRanges = (Range) iter.next();
                
                //if (this.speciesPhenonTreeDrawing  && range.isPhenonRange) 
                	//continue;

                if ((tempRangeLocation == rangeInRanges.location) & (rangeInRanges.branchDisabled == false) && (rangeInRanges.disabled4Priority == false)) {
                    lineX = rangeInRanges.getLineX();
                    myWidth = rangeInRanges.getWidth();
                    if (rangesCurrLocation == null) {
                        rangesCurrLocation = new ArrayList<Range>();
                    } 

                    rangesCurrLocation.add(rangeInRanges);
                    
                    // Added this condition so that it doesn't include widths of lables that are absent.
                    if (rangeX < rangeStart + lineX + myLineShift) {
                        rangeX = rangeStart + lineX + myLineShift;
                        overlapShift = 0;
                    }
                    if (myWidth > maxWidth) {
                        if (((rangeInRanges.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(rangeInRanges.base))
                                || (rangeInRanges.branchBase < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(rangeInRanges.branchBase))
                                || (rangeInRanges.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(rangeInRanges.top))
                                || (rangeInRanges.branchTop > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(rangeInRanges.branchTop)))) {
//							continue;
                        } else {
                            maxWidth = myWidth;
                        }
                    }
                }
            }
            tempRangeLocation = tempRangeLocation - 1;

//            Family Tree Null Message Exception because of missing range for
//              which a location is attributed.
//            if (rangesCurrLocation == null) {
//                exceptionHandling = true;
//                return;
//            }
            if (rangesCurrLocation == null) {
            	continue;
            } 
//            else {
//            	rangeX += 100;
//            }
            Iterator iterRangeCurrLocation = null;
            iterRangeCurrLocation = rangesCurrLocation.iterator();
            while (iterRangeCurrLocation.hasNext()) {
                range = (Range) iterRangeCurrLocation.next();
                
                double curTopAge = settings.topAge;
                boolean nextIsTop = true;

                Iterator pointIter = range.points.iterator();
                
                RCDatapoint lastUsedRCD = null;
                
                if (!range.isPhenonRange && this.speciesPhenonTreeDrawing) {
                	rangeX += range.nameWidth;
                }
                
                prevPhenonRange = null;
                
                RangePoint bp = (RangePoint)range.points.first(); // previous range point
                isTop = true;
                while (pointIter.hasNext()) {
                    RangePoint p = (RangePoint) pointIter.next();
                    if (p.rcd.branch || p.rcd.notInclude) {
                        if (!(p.rcd.type == "TOP" || p.rcd.type == "common"  || p.rcd.type == "frequent")) { 
                        	continue;
                        }
                    }
                    lastUsedRCD = p.rcd;
               
                    // draw samples
                    if (p.type.compareToIgnoreCase("sample") == 0) {
                        if (p.age > settings.baseAge || p.age < settings.topAge) {
                            continue;
                        }

                        ig.drawCircle(rangeX,
                                ImageGenerator.getYFromYear(p.age, starty, settings),
                                SAMPLE_RADIUS, "fill: black;");
                        continue;
                    }

                    if (nextIsTop) {
                        nextIsTop = false;
                        if (p.age > curTopAge) {
                            curTopAge = p.age;
                        }
                        continue;
                    }

                    double base = p.age;
                    if (base < settings.topAge) {
                        continue;
                    }
                    if (base > settings.baseAge) {
                        base = settings.baseAge;
                    }

                    RangeStyle style = getRangeStyle(p.type);
                    if (p.rcd != null && p.rcd.type != null && p.rcd.type.compareToIgnoreCase("rare") == 0) {
                        style.style = Settings.DASHED_STROKE + " stroke: " + range.branchColor + ";";
                        AGE_LABEL_X_PADDING = 4;
                    } else if (p.rcd != null && p.rcd.type != null && p.rcd.type.compareToIgnoreCase("conjectured") == 0) {
                        style.style = Settings.DOTTED_STROKE + " stroke: " + range.branchColor + ";";
                        AGE_LABEL_X_PADDING = 4.25;
                    } else {
                        style.style = Settings.SOLID_STROKE + " stroke: " + range.branchColor + ";";
                        // For frequent keep the default value of AGE_X
                    }

                    if (this.sideBySideTreeStructure) {
                    	this.speciesPhenonTreeDrawing = false;
                    	this.integratedTreeStructure = false;
                    } else if (this.integratedTreeStructure) {
                    	this.speciesPhenonTreeDrawing = true;
                    	this.integratedTreeStructure = true;
                    }
                    
                    if (range.name != prevRangeName) {
                    	rangeX += RANGE_CANVAS_START_PADDING;
                    }
                    
                    if (!range.isPhenonRange && this.integratedTreeStructure) {
                    	//Used to properly aline the boxes - Jason 
                    	prevRangeName = range.name;
                    	Iterator<Range> iterPhenonCounter = range.phenonsContainingSpecies.iterator();
                    	int totalPhenon = range.phenonsContainingSpecies.size();
                    	int enabledPhenonCounter = 0;
                    	while (iterPhenonCounter.hasNext()) {
                    		Range pR = iterPhenonCounter.next();
                    		if (pR.branchDisabled == false) {
                    			enabledPhenonCounter++;
                    		}
                    	}
                    	
                    	double rangeXForOtherSide = rangeX + range.width - range.nameWidth - WIDTH_FOR_SPECIES_BOX_INTERVAL;
                    	double rectWidth = rangeXForOtherSide - rangeX;
                    	double tmpRectWidth = rectWidth;
                    	if (enabledPhenonCounter == 0 && totalPhenon != 0) {
                    		rectWidth = rectWidth / totalPhenon;
                    	}
                    	else if (totalPhenon != enabledPhenonCounter) {
                    		rectWidth = (rectWidth / totalPhenon) * enabledPhenonCounter;
                    	}
                    	
                    	// ig.drawRectYear(rangeX, curTopAge, rectWidth, base - curTopAge,  "stroke: black; stroke-width: 0; fill: rgb(0,230,240);", starty); //"stroke-width: 1.0;  stroke: rgb(0,0,255);"                        
                    	String styleStr = style.getStyle();
                    	String fillColor = styleStr.substring(styleStr.indexOf("rgb"),styleStr.length());
                    	String rectStyle = styleStr + " fill: " + fillColor + " opacity: 0.2;";

                    	ig.drawRectYear(rangeX, curTopAge, rectWidth, base - curTopAge, rectStyle, starty); //"stroke-width: 1.0;  stroke: rgb(0,0,255);"                        
                    	range.rangeWidth = rectWidth;
//                    	range.width = rectWidth;
                    	
                    	// Changing Stroke Width
                    	int strokeWidthIdx = styleStr.indexOf("stroke-width");
                    	String beforeStrokeWidth = styleStr.substring(0, strokeWidthIdx);	
                    	String fromStrokeWidthStr = styleStr.substring(strokeWidthIdx, styleStr.length());
                    	int firstSemiColonAfterStrokeWidthIdx = fromStrokeWidthStr.indexOf(";");	
                    	String afterStrokeWidth	= styleStr.substring(strokeWidthIdx + firstSemiColonAfterStrokeWidthIdx + 1, styleStr.length());	
                    	String lineStyle = beforeStrokeWidth + "stroke-width: " + RANGE_BOX_STROKE_WIDTH + ";" + afterStrokeWidth;

                    	ig.drawLineYear(rangeX, base, rangeX, curTopAge, lineStyle, starty);
                    	double newRangeXForOtherSide = rangeXForOtherSide - (tmpRectWidth - rectWidth);
                    	ig.drawLineYear(newRangeXForOtherSide, base, newRangeXForOtherSide, curTopAge, lineStyle, starty);
                    	range.rangeWidth = newRangeXForOtherSide - rangeX;

                    	if (nextHorizontalTopLineDrawing) {
                    		ig.drawLineYear(rangeX, curTopAge, newRangeXForOtherSide, curTopAge, lineStyle, starty);
                    	} else {
                    		nextHorizontalTopLineDrawing = true;
                    	}
                    	
                    	//detecting range with conjectured point as base
                    	if (range.base != p.age && p.rcd.type.compareToIgnoreCase("conjectured") == 0) {
                    		nextHorizontalTopLineDrawing = false;
                    	}
                    		
                    	ig.drawLineYear(rangeX, base, newRangeXForOtherSide, base, lineStyle, starty);
                    	
                    	if (rangeX > maxRangeX) {
                    		maxRangeX = rangeX;
                    	}

                    	if (drawAgeLabel) {
                    		String bpS = Double.toString(bp.age);
                            String beforeDecimal = bpS.split("\\.")[0];
                            int bpL = beforeDecimal.length() + 2;

                            String pS = Double.toString(p.age);
                            beforeDecimal = pS.split("\\.")[0];
                            int pL = beforeDecimal.length() + 2;

                            int pd = Math.abs(bpL- pL);

                            double fontSize = Math.floor(fonts.getFont(FontManager.AGE_LABEL_FONT).getSize());

                            //This is used to prevent the ageLabels that have the same amount of characters from being misaligned.
                            int fPd = 0;

                            if(pd > 0){
                               fPd = 1;
                            }
                            
                            if (range.baseSymbol != Range.PHENON_BASE_CONTINUATION_SYMBOL ) 
                            {
                            	drawAgeLabelForRangePoint(ig, range, p, rangeX - RangeColumnRangeAgeLabelPadding, startx, starty, height, settings);
                            }

                            if (bp != null && bp.rcd != null && bp.rcd.type != null && bp.rcd.type.equals("TOP") && range.topSymbol != Range.PHENON_TOP_CONTINUATION_SYMBOL) {
                            	if (bp.age != 0) {
                            		//drawAgeLabelForRangePoint(ig, range, bp, rangeX - RangeColumnRangeAgeLabelPadding, startx, starty, height, settings);
                            		drawAgeLabelForRangePoint(ig, range, bp, rangeX - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, height, settings);
                            	}
                            }	
                    	}
                    } else if (range.isPhenonRange && this.integratedTreeStructure && range.branchDisabled == false) {
                    	prevRangeName = range.name;
                    	Iterator iterRanges = ranges.iterator();

                    	while (iterRanges.hasNext()) {
                    		Range speciesRange = (Range)iterRanges.next();
                    		if (!speciesRange.isPhenonRange) {	
                    			Iterator it = blackListedRangesForPhenonRangeDrawing.iterator();
                    			while (it.hasNext()) {
                    				String usedSpeciesRangeName = (String)it.next();
                    				if (usedSpeciesRangeName.equals(speciesRange.name.split(" <img")[0])) {
                    					continue;
                    				}
                    			}

//                    			if (prevPhenonRange != null && !prevPhenonRange.speciesNameWithFinalTop.equals(speciesRange.name)) {
//            						prevPhenonRange = null;
//            					}	

                    			Iterator iterContainingPhenon = speciesRange.phenonsContainingSpecies.iterator();
                    			while(iterContainingPhenon.hasNext()) {
                    				Range phenonRange = (Range)iterContainingPhenon.next();

                    				String rName = range.name.split(" <img")[0];
                    				String speciesRangeName = speciesRange.name.split(" <img")[0];
                    				String phenonRangeName = phenonRange.name.split(" <img")[0];

                    				if (rName.equals(phenonRangeName) 
                    						&& phenonRange.base == range.base 
                    						&& phenonRange.top == range.top) {
//                    					if (prevSpeciesRange != null  && phenonRange.branchDisabled == false) {
//                    						int phenonCounter = phenonCountForCurrentRange.get(speciesRangeName);
//                    						
////                    						if number of phenons exceeds total enabled phenon then make counter zero
//                    						if (phenonCounter > totalEnabledPhenonForCurrentRange.get(speciesRangeName)) {
//                        						phenonCountForCurrentRange.put(speciesRangeName, 0);
//                        					} else {
//                        						phenonCountForCurrentRange.put(speciesRangeName, phenonCounter + 1);
//                        					}
//                    					}
                        				
                    					if (prevPhenonRange == null || (range.base != prevPhenonRange.base && range.top != prevPhenonRange.top)) {
                    						Iterator<Range> iterPhenonCounter = speciesRange.phenonsContainingSpecies.iterator();
                                        	int totalPhenon = speciesRange.phenonsContainingSpecies.size();
                                        	int enabledPhenonCounter = 0;
                                        	while (iterPhenonCounter.hasNext()) {
                                        		Range pR = iterPhenonCounter.next();
                                        		if (pR.branchDisabled == false) {
                                        			enabledPhenonCounter++;
                                        		}
                                        	}

                    						double speciesRangeWidth = speciesRange.width * 0.92 - speciesRange.nameWidth - WIDTH_FOR_SPECIES_BOX_INTERVAL;
                    						if (enabledPhenonCounter == 0 && totalPhenon != 0) {
                                        		speciesRangeWidth = speciesRangeWidth / totalPhenon;
                                        	}
                                        	else if (totalPhenon != enabledPhenonCounter) {
                                        		speciesRangeWidth = (speciesRangeWidth / totalPhenon) * enabledPhenonCounter;
                                        	}

                                        	int tmpTotalEnabledPhenon = totalEnabledPhenonForCurrentRange.get(speciesRangeName);
                    						double unitWidth = speciesRangeWidth / (tmpTotalEnabledPhenon * 1.2);
//                                        	rangeX = speciesRange.rangeXCanvas + unitWidth * phenonCount + WIDTH_FOR_SPECIES_BOX_INTERVAL;//+ rangeWidth / speciesRange.phenonsContainingSpecies.size() * phenonCount ;
//                                        
                                        	rangeX = speciesRange.rangeXCanvas + speciesRangeWidth / 2;
                                        	int nthPhenon = phenonCountForCurrentRange.get(speciesRangeName) + 1;
                                        	phenonCountForCurrentRange.put(speciesRangeName, nthPhenon);
                                        	if (tmpTotalEnabledPhenon % 2 == 0) {
                                        		if (nthPhenon <= tmpTotalEnabledPhenon ) {
                                        			rangeX = rangeX - (tmpTotalEnabledPhenon/2 - nthPhenon) * speciesRangeWidth/tmpTotalEnabledPhenon - speciesRangeWidth/(2*tmpTotalEnabledPhenon); 
                                        		} else {
                                        			rangeX = rangeX - (nthPhenon - tmpTotalEnabledPhenon/2 - 1) * speciesRangeWidth/tmpTotalEnabledPhenon + speciesRangeWidth/(2*tmpTotalEnabledPhenon); 
                                        		}
                                        	} else {
                                        		if (nthPhenon == Math.ceil((double)tmpTotalEnabledPhenon/2)) {
                                        			//here
                                        			//rangeX += RANGE_BOX_EXTRA_WIDTH;
                                        		} else if (nthPhenon <= tmpTotalEnabledPhenon/2) {
                                        			rangeX = rangeX - (tmpTotalEnabledPhenon/2 - nthPhenon + 1) * speciesRangeWidth/tmpTotalEnabledPhenon; 
                                        		}else {
                                        			rangeX = rangeX + (nthPhenon - tmpTotalEnabledPhenon/2 - 1) * speciesRangeWidth/tmpTotalEnabledPhenon; 
                                        		}
                                        	}
                                        	prevPhenonRange = range;
                        					prevPhenonRange.rangeXCanvas = rangeX;
                    					}

//                    					if (prevSpeciesRange != null && !prevSpeciesRange.name.split(" <img")[0].equals(speciesRangeName)) {
//                    						prevPhenonRange = new Range(phenonRange);
//                    					} 

                    					if (prevSpeciesRange == null) {
                    						prevSpeciesRange = new Range(speciesRange);
                    					}
                    				}
                    			}
                    		}
                    	}
                    	String styleStr = style.getStyle();
                    	int strokeWidthIdx = styleStr.indexOf("stroke-width");
                    	String beforeStrokeWidth = styleStr.substring(0, strokeWidthIdx);	
                    	String fromStrokeWidthStr = styleStr.substring(strokeWidthIdx, styleStr.length());
                    	int firstSemiColonAfterStrokeWidthIdx = fromStrokeWidthStr.indexOf(";");	
                    	String afterStrokeWidth	= styleStr.substring(strokeWidthIdx + firstSemiColonAfterStrokeWidthIdx + 1, styleStr.length());	
                    	String lineStyle = beforeStrokeWidth + "stroke-width: " + PHENON_RANGE_LINE_STROKE_WIDTH + ";" + afterStrokeWidth;

                    	//ig.drawLineYear(rangeX, base, rangeX, curTopAge, style.getStyle(), starty);
                    	ig.drawLineYear(rangeX, base, rangeX, curTopAge, lineStyle, starty);

                    	if (range.branchDisabled == false) {
                    		if (range.topSymbol == Range.PHENON_FINAL_TOP_LIVING_SYMBOL && range.top >= settings.topAge) {
//                        		ig.drawCircle(rangeX, range.top, 3, style.getStyle());
                        		//ig.drawCircleYear(rangeX, range.top, starty, 4, "stroke: red; stroke-width:1; fill: rgb(255,0,0);", p, false);
                    			
                    			// draw circle above top marging
                            	double mar = 6; 
                    			double r = 4;
                    			// the first circle
                    			ig.drawCircleYear(rangeX, range.top, starty - mar, r, "stroke: red; stroke-width:1; fill: rgb(255,0,0);", p, false);
                    			r = 3;
                    			// the second empty circle inside the first circle
                    			ig.drawCircleYear(rangeX, range.top, starty - mar, r, "stroke: red; stroke-width:1; fill: rgb(255,255,255);", p, false);
                                
                        	} else if (range.topSymbol == Range.PHENON_FINAL_TOP_SYMBOL && range.top >= settings.topAge) {
                        		if (curTopAge >= settings.topAge) {
                        			//top base cross symbol
                        			double r = 5;
                        			String rlineStyle = "stroke: red; stroke-width:2; fill: rgb(255,0,0);";
                        			ig.drawCrossYear(rangeX, range.top, starty, r, rlineStyle, p, false);
                        			
                        		}
                        	} else if (range.topSymbol == Range.PHENON_TOP_CONTINUATION_SYMBOL && range.top >= settings.topAge) {
//                        		ig.drawRectYear(rangeX - 5, base - 0.025, 10, 0.0625,"stroke: red; stroke-width:1; fill: rgb(255,0,0);" ,starty); 

                           	 	double x[] = new double[3];
                                double y[] = new double[3];

                                double length = 2.5;
                                x[0] = rangeX - length;
                                x[1] = rangeX;
                                x[2] = rangeX + length;

                                y[0] = range.top + Math.sqrt(2) * length * 0.05;
                                y[1] = range.top; 
                                y[2] = range.top + Math.sqrt(2) * length * 0.05;
                                
                                if ( y[1] >=  settings.topAge) {
                                	ig.drawArrowWithPolygon(x, y , "stroke: red; stroke-width:1; fill: rgb(255,0,0);", starty );
                                }
                        	} else {

                        	}

                        	if (range.baseSymbol == Range.PHENON_BASE_SYMBOL && range.baseSymbol <= settings.baseAge && !p.rcd.branch) {
                        		if (range.base <= settings.baseAge) {  //&& range.parent == null
                        			ig.drawRectYear(rangeX - 5, range.base, 10, 0.0625,"stroke: red; stroke-width:1; fill: rgb(255,0,0);" ,starty);               	
                        		}
                        	} else if (range.baseSymbol == Range.PHENON_BASE_CONTINUATION_SYMBOL && range.baseSymbol <= settings.baseAge) {
//                        		ig.drawRectYear(rangeX - 5, base - 0.025, 10, 0.0625,"stroke: red; stroke-width:1; fill: rgb(255,0,0);" ,starty); 

                           	 	double x[] = new double[3];
                                double y[] = new double[3];

                                double length = RANGE_ARROW_LENGTH;
                                x[0] = rangeX - length;
                                x[1] = rangeX;
                                x[2] = rangeX + length;

                                y[0] = range.base + Math.sqrt(2) * length * 0.05 - RANGE_ARROW_VERTICAL_PADDING;
                                y[1] = range.base - RANGE_ARROW_VERTICAL_PADDING; 
                                y[2] = range.base + Math.sqrt(2) * length * 0.05 - 0.15;
                                if ( y[1] <=  settings.baseAge) {
                                	ig.drawArrowWithPolygon(x, y , "stroke: red; stroke-width:1; fill: rgb(255,0,0);", starty );
                                }
                        	} else  {

                        	}
                    	}

                    	if (drawAgeLabel) {
                    		String bpS = Double.toString(bp.age);
                            String beforeDecimal = bpS.split("\\.")[0];
                            int bpL = beforeDecimal.length() + 2;

                            String pS = Double.toString(p.age);
                            beforeDecimal = pS.split("\\.")[0];
                            int pL = beforeDecimal.length() + 2;

                            int pd = Math.abs(bpL- pL);

                            double fontSize = Math.floor(fonts.getFont(FontManager.AGE_LABEL_FONT).getSize());

                            //This is used to prevent the ageLabels that have the same amount of characters from being misaligned.
                            int fPd = 0;

                            if(pd > 0){
                               fPd = 1;
                            }

                        	//drawAgeLabelForRangePoint(ig, range, p, rangeX, startx, starty, height, settings);
                            
                            // for splitted phenon continuation age label is disabled
                            if (range.baseSymbol != Range.PHENON_BASE_CONTINUATION_SYMBOL ) 
                            {
                            	drawAgeLabelForRangePoint(ig, range, p, rangeX - RangeColumnRangeAgeLabelPadding, startx, starty, height, settings);
                            	// draw age label for the top point
                            }

                            if (bp != null && bp.rcd != null && bp.rcd.type != null && bp.rcd.type.equals("TOP") && range.topSymbol != Range.PHENON_TOP_CONTINUATION_SYMBOL) {
                            	if (bp.age != 0) {
                            		//drawAgeLabelForRangePoint(ig, range, bp, rangeX, startx, starty, height, settings);
                            		drawAgeLabelForRangePoint(ig, range, bp, rangeX - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, height, settings);
                            	} 
                            }
                    	}
                    } else {
                    	//Jason: This is the line you need to work with
                    	//ig.drawLineYear(rangeX, base, rangeX, curTopAge, style.getStyle(), starty);

                    	overlapShift = style.getWidth()*0.66;

                    	if(prevX > rangeX){
                            rangeX = prevX;
                        }

                    	if(prevRangeName != range.name && prevRangeName != null){// && ConserveSpace){
                           rangeX = prevX + prevWidth + BranchSpacing;
                        }

                    	if(ConserveSpace){
	                    	double a2 = (range.base - settings.topAge) * settings.unitsPerMY + starty;
	                    	double b2 = (range.top - settings.topAge) * settings.unitsPerMY + starty;
	                    	
	                    	if(b2 < a2 - range.nameHeight){
	                     	   currentHeight = b2;
	                     	} else {
	                     	   currentHeight = a2 - range.nameHeight;
	                     	}
	
                    		if(minTop == 0 && maxBase == 0){
                    			minTop = currentHeight;
                    			maxBase = a2;
                    		} else if(minTop > a2){
                    			rangeX = prevX;
                    			minTop = currentHeight;
                    		} else if(currentHeight > maxBase){
                    			rangeX = prevX;
                    			maxBase = a2;
                    		} else{
                    			minTop = currentHeight;
                    			maxBase = a2;
                    		}
                    	}

                    	ig.drawLineYear(rangeX, base, rangeX, curTopAge, style.getStyle(), starty);
                    	
                    	if ((range.top < settings.baseAge && range.base > settings.topAge && !Double.isNaN(range.top) && !Double.isNaN(range.base))){
                             prevX = rangeX;
                             prevRangeName = range.name;
                             prevWidth = range.width;
                        }

                        if (drawAgeLabel) {
                        	String bpS = Double.toString(bp.age);
                            String beforeDecimal = bpS.split("\\.")[0];
                            int bpL = beforeDecimal.length() + 2;

                            String pS = Double.toString(p.age);
                            beforeDecimal = pS.split("\\.")[0];
                            int pL = beforeDecimal.length() + 2;

                            int pd = Math.abs(bpL- pL);

                            double fontSize = Math.floor(fonts.getFont(FontManager.AGE_LABEL_FONT).getSize());

                            //This is used to prevent the ageLabels that have the same amount of characters from being misaligned.
                            int fPd = 0;

                            if(pd > 0){
                               fPd = 1;
                            }

                            //Note: range.labelloc changes as chart is cut from the top to the point where the text starts going out of the chart
                            //and resize is implemented. Ask Gabi, Jim or Andy about how to fix it.
                            if (p.age < settings.baseAge && p.age > settings.topAge && !Double.isNaN(p.age)){
                        	   drawAgeLabelForRangePoint(ig, range, p, rangeX - RangeColumnRangeAgeLabelPadding, startx, starty, height, settings);
                            }

                        	// draw age label for the top point
                        	if (bp != null && bp.rcd != null && bp.rcd.type != null && bp.rcd.type.equals("TOP")) {
                        		if (bp.age != 0 && bp.age > settings.topAge && bp.age < settings.baseAge) {
                        			//drawAgeLabelForRangePoint(ig, range, bp, rangeX, startx, starty, height, settings);
                        			drawAgeLabelForRangePoint(ig, range, bp, rangeX - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, height, settings);
                        		} else if (range.top >= settings.topAge && range.top < settings.baseAge){
                        			double mar = 6; // draw circle above top marging
                        			double r = 4;
                        			ig.drawCircleYear(rangeX, range.top, starty - mar, r, "stroke: red; stroke-width:1; fill: rgb(255,0,0);", p, false);
                        			r = 3;
                        			ig.drawCircleYear(rangeX, range.top, starty - mar, r, "stroke: red; stroke-width:1; fill: rgb(255,255,255);", p, false);
                        		}
                        	} else if(bp.rcd.type == null && isTop == true){
                        		//Note: used to print the top age in evolutionary trees.
                        		if (bp.age != 0 && bp.age > settings.topAge && bp.age < settings.baseAge) {
                        			drawAgeLabelForRangePoint(ig, range, bp, rangeX - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, height, settings);
                        		}
                        		isTop = false;
                        	} 
                        }
                    }

                    range.rangeXCanvas = rangeX;
                    range.baseofRange = base;

                    curTopAge = base;
                    bp = p;
                    
                    prevTop = range.top;
                    prevBase = range.base;
                    prevHeight = currentHeight;
                }
                // This condition prevents the labels of the branches that are not in the range from being drawn.
                /*
                if ((range.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.base))
                        || (range.branchBase < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.branchBase))
                        || (range.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.top))
                        || (range.branchTop > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.branchTop))) {
                    continue;
                }
                */
                if ((range.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.base))
                        || (range.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(range.top))) {
                	continue;
                } 
                if (range.branchDisabled == false) {
                    // draw the label
                    boolean drawLabelHere = false;
                    boolean resize = false;
                    double textX = Double.NaN, textY = Double.NaN;
                    switch (range.labelloc) {

                        case TOP:
                            textX = rangeX - (range.nameWidth / 2);
                            textY = ImageGenerator.getYFromYear(range.top, starty, settings) - RANGE_TO_TOP_LABEL_MARGIN - range.nameHeight;
                            drawLabelHere = true;
                            break;
 
                        case BOTTOM:
                            textX = rangeX - (range.nameWidth / 2);
                            textY = ImageGenerator.getYFromYear(range.base, starty, settings) + RANGE_TO_BOTTOM_LABEL_MARGIN;
                            drawLabelHere = true;
                            break;

                        case LEFT_SIDE:
                            textX = rangeX - (range.rangeWidth / 2) + RANGE_LABEL_TO_RANGE_MARGIN;
                            textY = ImageGenerator.getYFromYear(range.base, starty, settings) - RANGE_LABEL_BOTTOM_MARGIN - range.nameHeight;

                            if (textY + RANGE_LABEL_TOP_MARGIN + range.nameHeight + RANGE_LABEL_BOTTOM_MARGIN > starty + height) {
                                textY = starty + height - (RANGE_LABEL_TOP_MARGIN + range.nameHeight + RANGE_LABEL_BOTTOM_MARGIN);
                            }
                            if (textY < starty + RANGE_LABEL_TOP_MARGIN) {
                                textY = starty + RANGE_LABEL_TOP_MARGIN;
                            }

                            drawLabelHere = true;
                            break;
                            
                        case RIGHT_SIDE:
                            textX = rangeX + (range.rangeWidth / 2) + RANGE_LABEL_TO_RANGE_MARGIN;
                            textY = ImageGenerator.getYFromYear(range.base, starty, settings) - RANGE_LABEL_BOTTOM_MARGIN - range.nameHeight;

                            if (textY + RANGE_LABEL_TOP_MARGIN + range.nameHeight + RANGE_LABEL_BOTTOM_MARGIN > starty + height) {
                                textY = starty + height - (RANGE_LABEL_TOP_MARGIN + range.nameHeight + RANGE_LABEL_BOTTOM_MARGIN);
                            }
                            if (textY < starty + RANGE_LABEL_TOP_MARGIN) {
                                textY = starty + RANGE_LABEL_TOP_MARGIN;
                            }

                            drawLabelHere = true;
                            break;
                        
                        // when the image is larger than the height of the SVG, needs to be truncated
                        case RESIZE:
                            textX = rangeX + (range.rangeWidth / 2) + RANGE_LABEL_TO_RANGE_MARGIN;
                            textY = starty + height - (RANGE_LABEL_TOP_MARGIN + range.nameHeight + RANGE_LABEL_BOTTOM_MARGIN);
                            resize = true;
                            //textY = starty + RANGE_LABEL_TOP_MARGIN;

                            drawLabelHere = true;
                            break;
                    }

                    if (drawLabelHere) {
                        if (Double.isNaN(textX)) {
                            Debug.critical("incorrectly calculated label coordinates in range column!");
                        }
                        
                        if (!range.isPhenonRange && this.integratedTreeStructure) {
                        	double textXForOtherSide = rangeX + range.rangeWidth;
                        	
                        	ig.drawString(range.nameSWI,
                                    textXForOtherSide + RANGE_LABEL_X_PADDING, // X
                                    textY - RANGE_LABEL_Y_PADDING, // Y
                                    range.nameWidth, range.nameHeight,
                                    ImageGenerator.BOTTOM, textY, gui.ImageGenerator.TEXT_AND_BACKGROUND, color.getColor(lastUsedRCD.baseAge, lastUsedRCD.baseAge));		
                        	
                        	if (((range.popup != "") || (range.branchInfoTemp != "")) && settings.doPopups) {
                                ig.pushGrouping();
                                ig.doPopupThings(range.popup + range.branchInfoTemp, fileInfo, this);
                                ig.drawRect(textXForOtherSide + RANGE_LABEL_X_PADDING,
                                        textY - RANGE_LABEL_Y_PADDING,
                                        range.nameWidth, range.nameHeight, Settings.POPUP_HIGHLIGHT_STYLE);

                                ig.popGrouping();
                            }
                        } else if (range.isPhenonRange && this.integratedTreeStructure) {
                        	try {
                        		ig.drawString(range.nameSWI,
                                        textX + RANGE_LABEL_X_PADDING, // X
                                        textY - RANGE_LABEL_Y_PADDING, // Y
                                        range.nameWidth, range.nameHeight,
                                        ImageGenerator.BOTTOM, textY, gui.ImageGenerator.TEXT_AND_BACKGROUND, color.getColor(lastUsedRCD.baseAge, lastUsedRCD.baseAge));
                        	} catch (NullPointerException e){
                        		System.out.println(e.getMessage());
                        	};
                        	                        	 	
                        	if (((range.popup != "") || (range.branchInfoTemp != "")) && settings.doPopups) {
                                ig.pushGrouping();
                                ig.doPopupThings(range.popup + range.branchInfoTemp, fileInfo);
                                ig.drawRect(textX + RANGE_LABEL_X_PADDING,
                                        textY - RANGE_LABEL_Y_PADDING,
                                        range.nameWidth, range.nameHeight, Settings.POPUP_HIGHLIGHT_STYLE);

                                ig.popGrouping();
                            }
                        } else {
                        	try {
                        		if (resize == true)
                        		{
                        			ig.drawString(range.nameSWI,
                                            textX + RANGE_LABEL_X_PADDING, // X
                                            textY - RANGE_LABEL_Y_PADDING, // Y
                                            range.nameWidth, range.nameHeight,
                                            ImageGenerator.BOTTOM, textY, gui.ImageGenerator.TEXT_AND_BACKGROUND, color.getColor(lastUsedRCD.baseAge, lastUsedRCD.baseAge), resize);
                        		} else
                        		{
                        			ig.drawString(range.nameSWI,
                        					textX + RANGE_LABEL_X_PADDING + overlapShift, // X
                                            textY - RANGE_LABEL_Y_PADDING, // Y
                                            range.nameWidth, range.nameHeight,
                                            ImageGenerator.BOTTOM, textY, gui.ImageGenerator.TEXT_AND_BACKGROUND, color.getColor(lastUsedRCD.baseAge, lastUsedRCD.baseAge));
                        		}
                        	} catch (NullPointerException e){
                        		System.out.println(e.getMessage());
                        	};
                        	
                        	 	
                        	if (((range.popup != "") || (range.branchInfoTemp != "")) && settings.doPopups) {
                                ig.pushGrouping();
                                ig.doPopupThings(range.popup + range.branchInfoTemp, fileInfo);
                                ig.drawRect(textX ,
                                        textY ,
                                        range.nameWidth, range.nameHeight, Settings.POPUP_HIGHLIGHT_STYLE);

                                ig.popGrouping();
                            }
                        	
                        	myLineShift = myLineShift + overlapShift;
                        }
                        
                    }
                }
                //rangeX += 30;
                
                // Update range info in original ranges from the range in rangesCurrLocation
                Iterator iterUpdate = null;
                if (this.speciesPhenonTreeDrawing) {
                	iterUpdate = extendedRanges.iterator();
    			} else if (rangesWithoutSplit != null){
                	iterUpdate = rangesWithoutSplit.iterator();
                } else {
                	iterUpdate = ranges.iterator();
                } 
                Range ru = null;
                while (iterUpdate.hasNext()) {
                    ru = (Range) iterUpdate.next();
                    if (ru.name  == range.name && ru.base == range.base && ru.top == range.top && ru.points.size() == range.points.size()) {
                    	ru.width = range.width;
                    	ru.rangeWidth = range.width;
                    	ru.rangeXCanvas = range.rangeXCanvas;
                    	ru.lineX = range.lineX;
                    	ru.widthSubTree = range.widthSubTree;
                    }
                }
            }

            rangeStart += maxWidth + RANGE_PADDING;
        }

        drawBranches(ig, startx, starty, width, height, settings);
    
//        reverting the phenon range to the previous enabled/disabled state which 
        if (this.speciesPhenonTreeDrawing && phenonRangeStateMap != null) {
        	Iterator<Range> iterEE = extendedRanges.iterator();
        	
        	while (iterEE.hasNext()) {
        		Range phenonRange = iterEE.next();
        		if (phenonRange.isPhenonRange && phenonRangeStateMap.containsKey(phenonRange)) {
        			phenonRange.branchDisabled = phenonRangeStateMap.get(phenonRange);
        		}
        	}
        }
    }

    /**
     * Returns the number of different subsections of data that this column has. Most columns return 1, but some like event or freehand can have multiple.
     *
     * @return
     */
    @Override
    public int getNumSeries() {
        return 1;
    }

    /**
     * returns true if the editor can add/remove series. Usually false, though freehand can add/remove shapes.
     *
     * @return
     */
    @Override
    public boolean canAlterSeries() {
        return false;
    }

    /**
     * Returns a table model for data in the specified series
     *
     * @param which
     * @return
     */
    @Override
    public DataSeries getSeriesModel(int which) {
        if (which != 0) {
            return null;
        }

        DataSeries series = new DataSeries();
        series.setSingle(new DataSteward(data, new RangeColumn.TableInterpreter()));
        return series;
    }

    /**
     * Returns the name of the series.
     *
     * @param which
     * @return
     */
    @Override
    public String getSeriesName(int which) {
        if (which != 0) {
            return null;
        }
        return "Ranges";
    }

    @Override
    public void readOneSetting(Element setting, Settings settings) {
        super.readOneSetting(setting, settings);

        String attrname = setting.getAttribute("name");
        if (attrname == null) {
        } else if (attrname.compareToIgnoreCase("rangeSort") == 0) {
            String t = Settings.getNodeTextContent(setting);
            if (t.compareToIgnoreCase("first occurrence") == 0) {
                rangeSort = RangeComparator.FIRST_OCCURANCE;
            } else if (t.compareToIgnoreCase("last occurrence") == 0) {
                rangeSort = RangeComparator.LAST_OCCURANCE;
            } else if (t.compareToIgnoreCase("alphabetical") == 0) {
                rangeSort = RangeComparator.ALPHABETICAL;
            } else {
                rangeSort = RangeComparator.OTHER;
            }
        }
    }

    @Override
    public void writeSettings(Element element, Document doc) {
        super.writeSettings(element, doc);

        Element rangeSortE = Settings.createSimpleSetting(doc, "rangeSort", null);
        switch (rangeSort) {
            case RangeComparator.FIRST_OCCURANCE:
                Settings.setNodeTextContent(rangeSortE, "first occurrence", doc);
                break;
            case RangeComparator.LAST_OCCURANCE:
                Settings.setNodeTextContent(rangeSortE, "last occurrence", doc);
                break;
            case RangeComparator.ALPHABETICAL:
                Settings.setNodeTextContent(rangeSortE, "alphabetical", doc);
                break;
            default:
                Settings.setNodeTextContent(rangeSortE, "other", doc);
                break;
        }
        element.appendChild(rangeSortE);
    }

    @Override
    public void write(Writer w) throws IOException {
        writeHeader(w, "range");

        Iterator iter = getData();
        while (iter.hasNext()) {
            RCDatapoint dp = (RCDatapoint) iter.next();

            w.write("\t" + dp.label + "\t" + Double.toString(dp.baseAge));

            if (dp.type != null) {
                w.write("\t" + dp.type.toString());
            }
			/*
			switch (dp.lineType) {
			case Datapoint.DASHED_LINE: w.write("dashed\t"); break;
			case Datapoint.DOTTED_LINE: w.write("dotted\t"); break;
			case Datapoint.SOLID_LINE: w.write("solid\t"); break;
			default:
			w.write('\t');
			}
			
			if (dp.popup != null) {
			writeRichText(w, dp.popup);
			}*/

            w.write("\r\n");
        }

        writeOverlaysAndUnderlays(w);
    }

    public class TableInterpreter extends DataColumn.TableInterpreter {

        public String myNames[] = {"Critter", "Age", "Point Type"};
        public String myToolTips[] = {"Critter this range point is for", "Base age at this range point", "Specifies how the interval for which this point is the base will be drawn"};
        public Class myClasses[] = {String.class, Double.class, PointType.class};

        public TableInterpreter() {
            names = myNames;
            classes = myClasses;
            toolTips = myToolTips;
        }

        public class PointType extends gui.editor.ComboBoxRenderer {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public PointType() {
                super(new String[]{"sample", "LAD", "missing", "rare", "conjectured", "common", "frequent", "abundant", "flood"});
            }
        }

        @Override
        public void registerEditorsAndRenderers(SpreadSheet spread) {
            spread.setDefaultEditor(PointType.class, new DefaultCellEditor(new PointType().getComboBox()));
        }

        /* (non-Javadoc)
         * @see datastore.AbstractDatapointTableInterpreter#getValue(datastore.Datapoint, int)
         */
        @Override
        public Object getValue(Datapoint p, int col) {
            switch (col) {
                case 0:
                    return p.label;
                case 1:
                    return new Double(p.baseAge);
                case 2:
                    return ((RCDatapoint) p).type;
				/*case 2:
				case ImageGenerator.DOTTED_LINE: return "dotted";
				default: return "";
				}*/
                case 3:
                    return p.popup;
                default:
                    return null;
            }
        }

        /* (non-Javadoc)
         * @see datastore.AbstractDatapointTableInterpreter#setValue(datastore.Datapoint, java.lang.Object, int)
         */
        @Override
        public void setValue(Datapoint p, Object value, int col) {
            if (value == null) {
                value = "";
            }

            switch (col) {
                case 0:
                    p.label = value.toString();
                    if (p.label.compareTo("TOP") == 0) {
                        p.breaker = true;
                    } else {
                        p.breaker = false;
                    }
                    break;
                case 1:

                    double age = Double.NaN;
                    if (value instanceof Double) {
                        age = ((Double) value).doubleValue();
                    } else {
                        try {
                            age = Double.parseDouble(value.toString());
                        } catch (Exception e) {
                            age = Double.NaN;
                        }
                    }

                    if (!Double.isNaN(age)) {
                        p.baseAge = age;
                    } else {
                        p.baseAge = 0;
                    }

                    updateMinMaxAges();
                    break;

                case 2:
                    ((RCDatapoint) p).setType(value.toString());
                    break;
				/*
				String s = value.toString().trim();
				if (s.compareToIgnoreCase("dashed") == 0)
				p.lineType = ImageGenerator.DASHED_LINE;
				else if (s.compareToIgnoreCase("dotted") == 0)
				p.lineType = ImageGenerator.DOTTED_LINE;
				else
				p.lineType = ImageGenerator.SOLID_LINE;
				break;
				 */
				/*
				case 3:
				p.popup = value.toString();
				break;
				 */
            }
        }
    }

    // function to link ranges
    public void linkRangeSets(boolean forPhenonRange) {
        Iterator<Range> iterParent = ranges.iterator();
        Iterator iter2 = null;
        Iterator<Range> iterChild = null;
        Range rangeParent = null;
        Range rangeChild = null;
        RangePoint point = null;
        boolean found = false;
        
        try {
            //Create a double link between parent and child trees.
            while (iterParent.hasNext()) {
                rangeParent = (Range) iterParent.next();
                iter2 = rangeParent.points.iterator();
          
                while (iter2.hasNext()) {
                    point = (RangePoint) iter2.next();
                    
                    // May already be done in the findSpecificRanges method
                    //Determining whether phenon range or species range and specifying the name of the species which contains the final top of the phenon
                    if (point.rcd.speciesOrPhenon != null && point.rcd.speciesOrPhenon.equalsIgnoreCase("phenon") && point.rcd.speciesWithFinalTopOfPhenon != null) {
                		rangeParent.isPhenonRange = true;
                		rangeParent.speciesNameWithFinalTop = point.rcd.speciesWithFinalTopOfPhenon;
                    }
                    
                    if (forPhenonRange == true) {
                    	if (rangeParent.isPhenonRange == false) { // if linkRanges Sets is to be done for phenon range only
                    		//but rangeParent is not a Phenon range but a species range, then don't link and break the loop. 
                    		break;
                    	}
                    } else {
                    	if (rangeParent.isPhenonRange == true) { // if linkRanges Sets is to be done for species range only
                    		//but rangeParent is a Phenon range, but not a species range, then don't link and break the loop.
                    		break;
                    	}
                    }             
                    
                   
                    found = false;
                    if (point.rcd.branch) {
                    	iterChild = ranges.iterator();
                        while (iterChild.hasNext()) {
                            rangeChild = iterChild.next();
                            if (point.rcd.branchTo.equalsIgnoreCase(rangeChild.name.split(" <img")[0])) {
                                //If base of child is below branch age then throw an error with the message.
                            	
                                if (rangeChild.base > point.age) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
                                	rangeParent.printRangeInfo();
                                	rangeChild.printRangeInfo();
                                    
                                	Iterator<RangePoint> iterRP = rangeChild.points.iterator();
                                    while (iterRP.hasNext()) {
                                    	RangePoint rp = iterRP.next();  
                                    }
                                	
                                	
                                    errMsg = errMsg + this.name + "\n" + "Base age " + rangeChild.base + " of Child " + rangeChild.name + " is lower than Branch age " + point.age + " at Parent " + point.name + "\n";
                                    //System.out.println(errMsg);
                                }
                                if (rangeParent.children == null) {
                                    rangeParent.children = new ArrayList<Range>();
                                }
                                rangeParent.children.add(rangeChild);
                                rangeChild.parent = rangeParent;
                                rangeChild.branchColor = rangeParent.branchColor;

                                //Assign Priority to the child range from the parent datapoint. Also assign enable/disable
                                //for the range based on the notInclude (default on/off) column of the parent
                                rangeChild.rangePrio = point.rcd.branchPrio;
                                rangeChild.branchDisabled = point.rcd.notInclude;
                                rangeChild.overridePriority = point.rcd.overridePriority || rangeParent.overridePriority;
                                rangeChild.disabled4Priority = (rangeChild.rangePrio < this.priorityVal) && this.priorityEnable && !rangeChild.overridePriority;

                                point.childRange = rangeChild;

                                if (point.rcd.subLabel != null) {
                                    rangeChild.subLabel = point.rcd.subLabel;
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            point.childOutofRange = true;
                        }
                    }
                }
            }

            //After linking the nodes, disable the branches which are default off.
            iterParent = ranges.iterator();
            while (iterParent.hasNext()) {
                rangeParent = iterParent.next();
                if (rangeParent.children != null) {
                    iterChild = rangeParent.children.iterator();
                    while (iterChild.hasNext()) {
                        if (rangeParent.branchDisabled) {
                            iterChild.next().branchDisabled = true;
                        } else {
                            iterChild.next();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }
    
    public void getEndPoints(java.util.List<RangePoint> endpoints) {
    	Iterator iter = this.getDrawingData();
        RCDatapoint p;

        // create endpoints out of the event data
        while (iter.hasNext()) {
            p = (RCDatapoint) iter.next();
            
            // parse out all names
            StringTokenizer st = null;
           	//st = new StringTokenizer(p.label, ","); //Something with comma
            if (!this.speciesPhenonTreeDrawing) {
            	st = new StringTokenizer(p.label, ";"); //Something with comma
            } else {
            	st = new StringTokenizer(p.label, ","); //Something with comma
            }
            while (st.hasMoreTokens()) {
                String l = st.nextToken().trim();

                if (l.length() < 1) {
                    continue;
                }

                RangePoint rp = new RangePoint();
                rp.age = p.baseAge;
                rp.name = l;
                if (rp.popup == null) {
                    rp.popup = p.popup;
                }
                String pType = "";
                if (p.type != null) {
                    pType = p.type.toString();
                }
                if (pType.compareToIgnoreCase("LAD") == 0) {
                    rp.top = true;
                } else if (pType.compareToIgnoreCase("FAD") == 0) {
                    rp.top = false;
                }
                rp.type = pType;
                rp.rcd = p;

                endpoints.add(rp);
            }
        }
    }
    
    // function to add points to the splitted phenon range
    public void addPointsToSplittedPhenon(Range speciesRange, Range splittedPhenon, boolean forBase, boolean isSplit) {
    	java.util.List<RangePoint> endpoints = new ArrayList<RangePoint>(this.data.size());
    	getEndPoints(endpoints);
    	
    	// Will find out the closest point near the top or base of the splitted Phenon
    	// Add it as top or base point of it  ----> Subjected to change
    	RangePoint closestPointBelow = null;
    	Double closeness = Double.POSITIVE_INFINITY;
    	
    	// Check whether there exists point in top and base. These may occur when top and branch are at the same location
    	boolean basePointExists = false;
    	boolean topPointExists = false;
    	RangePoint tmpBasePoint = null;
    	RangePoint tmpTopPoint = null;
    	Iterator<RangePoint> iterPointI = endpoints.iterator();
    	while (iterPointI.hasNext()) {
    		RangePoint p = iterPointI.next();
    		if (p.rcd.speciesOrPhenon != null && p.rcd.speciesOrPhenon.equals("phenon") == true && p.name.split(" <img")[0].equals(splittedPhenon.name.split(" <img")[0]))
    		{
    			if (p.age == splittedPhenon.base) {
        			basePointExists = true;
        			tmpBasePoint = p.copy();
        			
        			if (tmpBasePoint.rcd.type == null) {
        				tmpBasePoint.rcd.type = "common";
        			}
//        			if (tmpBasePoint.rcd.type == null && tmpBasePoint.rcd.type.equals("branch")) {
//        				
//        			} else if (tmpBasePoint.rcd.type == null && tmpBasePoint.rcd.type.equals("TOP")) {
//        				tmpBasePoint.rcd.type = "common"
//        			}
        		}     		
        		if (p.age == splittedPhenon.top) {
        			topPointExists = true;
        			tmpTopPoint = p.copy(); 
        			if (tmpTopPoint.rcd.type == null || (!tmpTopPoint.rcd.type.equals("branch") && !tmpTopPoint.rcd.type.equals("TOP"))) {
        				tmpTopPoint.rcd.type = "TOP";
        			} else if (tmpTopPoint.rcd.type != null && tmpTopPoint.rcd.type.equals("branch")) {
        				tmpTopPoint.rcd.type = "TOP";
        				tmpTopPoint.rcd.branch = false;
        				tmpTopPoint.rcd.branchTo = null;
        				tmpTopPoint.rcd.branchColor = null;
        				tmpTopPoint.rcd.branchLabel = null;
        				tmpTopPoint.rcd.branchPrio = 0;
        			}
        		}	
    		}
    	}
    	
    	Iterator<RangePoint> iterPoint = endpoints.iterator();
    	while (iterPoint.hasNext()) {
    		RangePoint p = iterPoint.next();
    		
    		//Adding existing points which are in between top and base of the range
    		if (p.name.split(" <img")[0].equals(splittedPhenon.name.split(" <img")[0]) &&
    				p.age <=splittedPhenon.base && p.age >= splittedPhenon.top) {
    			
    			//Check whether point exists or not
    			boolean pointExists = false;
			    Iterator<RangePoint> iterP = splittedPhenon.points.iterator();
			    while (iterP.hasNext()) {
			    	RangePoint rpp = iterP.next();
			    	if (rpp.age == p.age && rpp.rcd.branchTo == p.rcd.branchTo) {
			    		pointExists = true;
			    		
			    		// If point exists, check whether top point and base point has their point type fixed
				    	if (rpp.age == splittedPhenon.top && rpp.rcd.type == null) {
				    		rpp.rcd.type = "TOP";
				    	}
				    	
				    	if (rpp.age == splittedPhenon.base && rpp.rcd.type == null) {
				    		rpp.rcd.type = "common";
				    	}
			    	}
			    }
			    
			    if (pointExists == false) {
			    	if (basePointExists && tmpBasePoint.age == p.age && tmpBasePoint.name == p.name) {
	    				splittedPhenon.addPoint(tmpBasePoint);
	    			} else if (topPointExists && tmpTopPoint.age == p.age && tmpTopPoint.name == p.name) {
	    				splittedPhenon.addPoint(tmpTopPoint);
	    			} else {
	    				splittedPhenon.addPoint(p);
	    			}
			    }
    		}
    		
    		if (isSplit && p.name.split(" <img")[0].equals(splittedPhenon.name.split(" <img")[0])) {
    			if (basePointExists == false && forBase == true ) {
    				if ((p.age >= splittedPhenon.base && Math.abs(p.age - splittedPhenon.base) < closeness) || closeness == null) {
    					closeness = Math.abs(p.age - splittedPhenon.base);
    					
    					closestPointBelow = p.copy();
    					
    					closestPointBelow.age = splittedPhenon.base;
    					
//    					closestPointBelow.rcd.splitPhenonBranch = true;
    					if (tmpBasePoint != null && tmpBasePoint.type == "branch") {
    						closestPointBelow.rcd.type = "branch";
    					} else {
    						closestPointBelow.rcd.type = "common";
    						if (closestPointBelow.rcd.branch) {
    							closestPointBelow.rcd.branch = false;
    							closestPointBelow.rcd.branchTo = null;
    							closestPointBelow.rcd.branchLabel = null;
    							closestPointBelow.rcd.branchPrio = 0;
    							closestPointBelow.rcd.branchColor = null;
    						}
    					}
    					closestPointBelow.rcd.baseAge = splittedPhenon.base;
    				}
    			} else if (topPointExists == false && forBase == false){
    				if ((p.age >= splittedPhenon.top && Math.abs(p.age - splittedPhenon.top) < closeness) || closeness == null) {
    					closeness = Math.abs(p.age - splittedPhenon.top);
    					
    					closestPointBelow = p.copy();
    					
    					closestPointBelow.age = splittedPhenon.top;
    					
    					closestPointBelow.rcd.splitPhenonBranch = true;
    					if (tmpTopPoint != null && tmpTopPoint.type == "branch") {
    						closestPointBelow.rcd.type = "branch";
    					} else {
    						closestPointBelow.rcd.type = "TOP";
    						if (closestPointBelow.rcd.branch) {
    							closestPointBelow.rcd.branch = false;
    							closestPointBelow.rcd.branchTo = null;
    							closestPointBelow.rcd.branchLabel = null;
    							closestPointBelow.rcd.branchPrio = 0;
    							closestPointBelow.rcd.branchColor = null;
    						}
    					}
    				}
    			}
    		}
    	}
    	if (closestPointBelow != null) {
			splittedPhenon.addPoint(closestPointBelow);
    	}
    }
    
    // function to extend the range sets when drawing the integrated tree
    public void extendRangeSets(Settings settings) {
    	// Get the phenonRanges only. 
      findSpecificRanges(settings, true, true); //The second argument chooses the phenonRanges set
    	
      Iterator<Range> iterPhenons = phenonRanges.iterator();
      
      while(iterPhenons.hasNext()) {
    		Range rangePhenon = iterPhenons.next();
    		if (rangePhenon.isPhenonRange) { //only for the phenon range
    			String speciesToSearch = rangePhenon.speciesNameWithFinalTop;
    			Iterator<Range> iterSpecies = ranges.iterator();
    			
    			//Find the species which contains the final top of the current phenon range
    			Range rangeSpecies = null;
    			while(iterSpecies.hasNext()) {
    				rangeSpecies = iterSpecies.next();
    				if (!rangeSpecies.isPhenonRange) {
    					if (speciesToSearch.equalsIgnoreCase(rangeSpecies.name.split(" <img")[0])) {
    						break;
    					}
    				}
    			}
    		
    			Range splittedPhenon = new Range(rangePhenon);
    			splittedPhenon.top = rangePhenon.top;  
    			if (splittedPhenon.top == 0)
    				splittedPhenon.topSymbol = Range.PHENON_FINAL_TOP_LIVING_SYMBOL;
    			else 
    				splittedPhenon.topSymbol = Range.PHENON_FINAL_TOP_SYMBOL;
    			
    			Range tmpRange = null;
    			Range r = rangeSpecies;
    			boolean isSplit = false;
    			do {
    				if (splittedPhenon.base > r.base) {
    					splittedPhenon.base = r.base;
    					splittedPhenon.baseSymbol = Range.PHENON_BASE_CONTINUATION_SYMBOL;
    					
//    					Iterator<RangePoint> it = splittedPhenon.points.iterator();
//    					while (it.hasNext()) {
//    						RangePoint p = it.next();
//    						if (p.age > r.base || p.age < r.top) {
//    							splittedPhenon.points.remove(p);
//    						}
//    					}
//    					
    					splittedPhenon.branchColor = r.branchColor;
    					r.phenonsContainingSpecies.add(splittedPhenon);
    					
    					// Adding point to the first split
    					
    					splittedPhenon.points.clear();
    				    addPointsToSplittedPhenon(r, splittedPhenon, true, true); // adding an extra top point by making second argument forbase=false
    				    
    				    // For more than two split, if there's no range point at the top age of the range, we need to add both top and base point for the range if no point exists at top and base age
    				    boolean topRangePointExists = false;
    				    Iterator<RangePoint> iterP = splittedPhenon.points.iterator();
    				    while (iterP.hasNext()) {
    				    	RangePoint rpp = iterP.next();
    				    	if (rpp.age == splittedPhenon.top) {
    				    		topRangePointExists = true;
    				    	}
    				    }
    				    if (topRangePointExists == false) {
    				    	addPointsToSplittedPhenon(r, splittedPhenon, false, true);
    				    }
    					
    				    // Adding the first split to the extendedRanges
    					extendedRanges.add(splittedPhenon);
    					tmpRange = splittedPhenon;
    					
    					//Preparing the second split
    					//Deep cloning the range phenon to create the splitted phenon range
    					splittedPhenon = new Range(rangePhenon);
    					
    					splittedPhenon.top = r.base;
    					splittedPhenon.topSymbol = Range.PHENON_TOP_CONTINUATION_SYMBOL;
    					
    					r = r.parent; 
    					isSplit = true;
    				
    				} else {
    					splittedPhenon.base = rangePhenon.base;
    					if (!isSplit) {
    						//Finding the first phenon range to add the Phenon base symbol
    						Iterator<Range> iterPhenonRanges = phenonRanges.iterator();
    						while (iterPhenonRanges.hasNext()) {
    							Range pR = iterPhenonRanges.next();
    							if (pR.name.contains(splittedPhenon.name)) {
    								splittedPhenon.baseSymbol = Range.PHENON_BASE_SYMBOL;
    							}
    						}
    					}
    					
    					// Adding the splitted Phenon range
    					r.phenonsContainingSpecies.add(splittedPhenon);
    					// Adding point to the second split
    					splittedPhenon.points.clear();
    					addPointsToSplittedPhenon(r, splittedPhenon, false, isSplit); // adding an extra top point by making second argument forbase=false
    					// Make top splitted branch a child range of the base splitted branch
    					Iterator iterPoint = splittedPhenon.points.iterator();
    					while(iterPoint.hasNext()) {
    						RangePoint rp = (RangePoint)iterPoint.next();
    						if (rp.rcd.splitPhenonBranch) {
    							splittedPhenon.children.add(tmpRange);
    							rp.childRange = tmpRange;
    	    					splittedPhenon.baseSymbol = -1;
    						}
    					}
    					
    					// Adding the second split to the extended ranges
    					extendedRanges.add(splittedPhenon);
    					
    					isSplit = false;
    					break;
    				}   				
    			} while (r.parent != null); 
    		}
    	}
    
    	// Adding the species ranges
    	Iterator<Range> it1 = ranges.iterator();
    	while(it1.hasNext()) {
    		Range r = it1.next();
    		if (!r.isPhenonRange) { // Only when r is a species range
                extendedRanges.add(r);
                //Range r2 = new Range(r); //copy/duplicate the species vertical range
                //extendedRanges.add(r2);  
    		}
    	}
    }

    // function to derive branch color and range color from branch points. Successive inheritance is also ensured
    private void derivebranchColors() {
        Iterator iterRange = null;
        
        if (this.speciesPhenonTreeDrawing) {
        	iterRange = extendedRanges.iterator();
        } else {
        	iterRange = ranges.iterator();
        }
        Range range = null;
        Iterator points = null;
        RangePoint p = null;
        while (iterRange.hasNext()) {
            range = (Range) iterRange.next();
            points = range.points.iterator();
//            if (range.name.contains("|")) {
//            	System.out.println();
//            }
            while (points.hasNext()) {
                p = (RangePoint) points.next();
                if ((p.rcd.branch && (p.childOutofRange == false)) || (this.speciesPhenonTreeDrawing && p.rcd.splitPhenonBranch)) {
                    if (p.rcd.branchColor != null) {
                        p.childRange.branchColor = Coloring.getStyleRGB(p.rcd.branchColor);
                    } else if (range.branchColor != null) {
                    	if (p.childRange != null) {
                            p.childRange.branchColor = range.branchColor;
                    	}
                    } else {
                        p.childRange.branchColor = "rgb(0,0,0)"; //black
                    }
                    
                    // Check a splitted phenon range by checking its name matched with the childRange and parent to be null and 
                    Iterator iterPhenonRange = null;
                    if (this.speciesPhenonTreeDrawing) {
                    	iterPhenonRange = extendedRanges.iterator();
                    } else {
                    	iterPhenonRange = ranges.iterator();
                    }
                    while(iterPhenonRange.hasNext()) {
                    	Range phenonRange = (Range) iterPhenonRange.next();
                    	// avoid species ranges
                    	if(phenonRange.isPhenonRange == false)
                    		continue;
                    	if (p.childRange != null && p.childRange.name.equals(phenonRange.name)) {
                    		// For all such phenonRanges set the branchColor
                    		if (phenonRange.parent == null) {
                                if (p.rcd.branchColor != null) {
                                    phenonRange.branchColor = Coloring.getStyleRGB(p.rcd.branchColor);
                                } else if (range.branchColor != null) {
                                	phenonRange.branchColor = range.branchColor;
                                } else {
                                    phenonRange.branchColor = "rgb(0,0,0)"; //black
                                }
                            }
                    	}
                    }

                } else if (p.rcd.branch == false && p.rcd.branchColor == null && range.branchColor != null) {
                	if (p.childRange != null) {
                        p.childRange.branchColor = range.branchColor;
                	} else {
                		//p.rcd.branchColor = new Color(range.branchColor);
                	}
                }
            }
        }
    }

    private int assignLocationAttributes() {
        Iterator rangeIterator = ranges.iterator();
        Range rootRange = null;
        int minLocation = Integer.MAX_VALUE;
        while (rangeIterator.hasNext()) {
            rootRange = (Range) rangeIterator.next();
            if (rootRange.rangeTraversed4Location) {
                continue;
            }

            if ((rootRange.location == Integer.MAX_VALUE) && (rootRange.branchDisabled == false)) {
                minLocation = rootRange.locAttribute(minLocation);
            }
        }

        return minLocation;
    }

    // function to draw horizontal Branch lines after drawing vertical range lines 
    private void drawBranches(ImageGenerator ig, double startx, double starty, double widthOrig, double heightOrig, Settings settings) {
        Iterator iterRange = ranges.iterator();
        Range range = null;
        Iterator points = null;
        RangePoint p = null;
        double rangeXParent;
        double baseYParent;
        double rangeXChild;
        double baseYChild;
        double startXsubLabel = 0;
        double startYsubLabel = 0;
        double width = 0;
        RangeStyle style = new RangeStyle(Settings.SOLID_STROKE + " stroke: black;", 1);
        while (iterRange.hasNext()) {
            range = (Range) iterRange.next();
            
//            if (this.speciesPhenonTreeDrawing && range.isPhenonRange) {
//            	continue;
//            }
            
            //This condition fixes the red-dots on the canvas issue. Previously, even though, if the current branch is disabled from plotting, the branch nodes (circle)
            //were getting plotted on the canvas. As there is no location for ranges, the circle is plotted at initilzed x-axis value (0), towards the left-corner
            //of the canvas. This condition now skips plotting the circles if the range in itself is disabled.

            if (range.branchDisabled || range.disabled4Priority) {
                continue;
            }

            points = range.points.iterator();
            while (points.hasNext()) {
                p = (RangePoint) points.next();

                if (p.rcd.branch & (p.childOutofRange == false)) {
                    rangeXParent = range.rangeXCanvas;
                    baseYParent = p.age;
                    rangeXChild = p.childRange.rangeXCanvas;
                    baseYChild = p.childRange.baseofRange;
                    if (p.rcd.branchLabel != null) {
                        if (p.rcd.branchLabel.equalsIgnoreCase("dashed")) {
                            style.style = Settings.DASHED_STROKE;
                        } else if (p.rcd.branchLabel.equalsIgnoreCase("dotted")) {
                            style.style = Settings.DOTTED_STROKE;
                        } else {
                            style.style = Settings.SOLID_STROKE;
                        }
                    } else {
                        style.style = Settings.SOLID_STROKE;
                    }

                    if (p.rcd.branchColor != null) {
                        style.style = style.style + " stroke: " + Coloring.getStyleRGB(p.rcd.branchColor) + ";";
                    } else {
                        style.style = style.style + " stroke: " + range.branchColor + ";";
                    }

                    Iterator<Range> iterPhenonCounter = range.phenonsContainingSpecies.iterator();
                	int totalPhenon = range.phenonsContainingSpecies.size();
                	int enabledPhenonCounter = 0;
                	while (iterPhenonCounter.hasNext()) {
                		Range pR = iterPhenonCounter.next();
                		if (pR.branchDisabled == false) {
                			enabledPhenonCounter++;
                		}
                	}
                	
//                	if (enabledPhenonCounter == 0 && totalPhenon != 0) {
//                		rectWidth = rectWidth / totalPhenon;
//                	}
//                	else if (totalPhenon != enabledPhenonCounter) {
//                		rectWidth = (rectWidth / totalPhenon) * enabledPhenonCounter;
//                	}
                	
                    if (p.childRange.branchDisabled == false && (p.childRange.disabled4Priority == false)) {
                    	/*
                        if ((p.childRange.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.base))
                                || (p.childRange.branchBase < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.branchBase))
                                || (p.childRange.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.top))
                                || (p.childRange.branchTop > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.branchTop))) {
                            continue;
                        }
                        */

                    	if ((p.childRange.base < settings.topAge + NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.base))
                                || (p.childRange.top > settings.baseAge - NumberUtils.EQUAL_MARGIN && !Double.isNaN(p.childRange.top))) {
                            continue;
                        }
                       
                        // When integrated tree drawing option is not selected and range is not phenon range (rather a regular species regular range)
                        if (!range.isPhenonRange && this.speciesPhenonTreeDrawing) {
                        	if (p.childRange.rangeXCanvas < range.rangeXCanvas) {
                        	    Iterator<Range> iterChildPhenonCounter = p.childRange.phenonsContainingSpecies.iterator();
                             	int totalChildPhenon = p.childRange.phenonsContainingSpecies.size();
                             	int enabledChildPhenonCounter = 0;
                             	while (iterChildPhenonCounter.hasNext()) {
                             		Range pR = iterChildPhenonCounter.next();
                             		if (pR.branchDisabled == false) {
                             			enabledChildPhenonCounter++;
                             		}
                             	}
                             	double rectWidth = p.childRange.width;
                             	if (enabledChildPhenonCounter == 0 && totalChildPhenon != 0) {
                            		rectWidth = rectWidth / totalChildPhenon;
                            	}
                            	else if (totalChildPhenon != enabledChildPhenonCounter) {
                            		rectWidth = (rectWidth / totalChildPhenon) * enabledChildPhenonCounter;
                            	}
                            	double rangeXShiftedWithSpeciesBox = rangeXChild + rectWidth - p.childRange.nameWidth - WIDTH_FOR_SPECIES_BOX_INTERVAL;
                            	
                            	String styleStr = style.getStyle();
                            	int strokeWidthIdx = styleStr.indexOf("stroke-width");
                            	String beforeStrokeWidth = styleStr.substring(0, strokeWidthIdx);	
                            	String fromStrokeWidthStr = styleStr.substring(strokeWidthIdx, styleStr.length());
                            	int firstSemiColonAfterStrokeWidthIdx = fromStrokeWidthStr.indexOf(";");	
                            	String afterStrokeWidth	= styleStr.substring(strokeWidthIdx + firstSemiColonAfterStrokeWidthIdx + 1, styleStr.length());	
                            	String lineStyle = beforeStrokeWidth + "stroke-width: " + RANGE_BOX_STROKE_WIDTH + ";" + afterStrokeWidth;
                            	
                            	ig.drawLineYear(rangeXShiftedWithSpeciesBox, baseYParent, rangeXParent, baseYChild, lineStyle, starty);
                        	} else {
                        		double rectWidth = range.width - range.nameWidth - WIDTH_FOR_SPECIES_BOX_INTERVAL;
                        		double rangeXShiftedWithSpeciesBox = 0;
                        		if (enabledPhenonCounter == 0 && totalPhenon != 0) {
                            		rectWidth = rectWidth / totalPhenon;
                            	}
                            	else if (totalPhenon != enabledPhenonCounter) {
                            		rectWidth = (rectWidth / totalPhenon) * enabledPhenonCounter;
                            	}
                        		rangeXShiftedWithSpeciesBox = rangeXParent + rectWidth;
                        		
                        		String styleStr = style.getStyle();
                            	int strokeWidthIdx = styleStr.indexOf("stroke-width");
                            	String beforeStrokeWidth = styleStr.substring(0, strokeWidthIdx);	
                            	String fromStrokeWidthStr = styleStr.substring(strokeWidthIdx, styleStr.length());
                            	int firstSemiColonAfterStrokeWidthIdx = fromStrokeWidthStr.indexOf(";");	
                            	String afterStrokeWidth	= styleStr.substring(strokeWidthIdx + firstSemiColonAfterStrokeWidthIdx + 1, styleStr.length());	
                            	String lineStyle = beforeStrokeWidth + "stroke-width: " + RANGE_BOX_STROKE_WIDTH + ";" + afterStrokeWidth;
                            	
                        		ig.drawLineYear(rangeXShiftedWithSpeciesBox, baseYParent, rangeXChild, baseYChild, lineStyle, starty);
                        	}
                        } else {
                        	ig.drawLineYear(rangeXParent, baseYParent, rangeXChild, baseYChild, style.getStyle(), starty);
                        }
                        
                        //Sub Label stuff
                        if (p.rcd.subLabel != null) {
                            //place the label exactly below the child tree with the midpoint of the subLabel aligning with the child range.
                            width = Math.max(p.childRange.subLabelSWI.getWidth(), p.childRange.widthSubTree);
                            startXsubLabel = Math.max(startx, rangeXChild - (width / 2));
                            startYsubLabel = (baseYParent + baseYChild) / 2;
                            
                            if (!range.isPhenonRange && this.speciesPhenonTreeDrawing) {
                            	double newStartXsubLabel = startXsubLabel; 
                             	ig.drawStringabsolute(p.childRange.subLabelSWI, newStartXsubLabel, startYsubLabel, starty + 5, width, p.childRange.subLabelSWI.getHeight(), ImageGenerator.CENTER, p.rcd.branchColor);
                            } else {
                            	ig.drawStringabsolute(p.childRange.subLabelSWI, startXsubLabel, startYsubLabel, starty, width, p.childRange.subLabelSWI.getHeight(), ImageGenerator.CENTER, p.rcd.branchColor);
                            }
                         }
                    }

                    ig.pushGrouping();
                		
                    // Auxiliary information for drawing branch point age label
                    String bpS = Double.toString(p.age);
                    String beforeDecimal = bpS.split("\\.")[0];
                    int bpL = beforeDecimal.length() + 2;

                    String pS = Double.toString(p.age);
                    beforeDecimal = pS.split("\\.")[0];
                    int pL = beforeDecimal.length() + 2;

                    int pd = Math.abs(bpL- pL);

                    double fontSize = Math.floor(fonts.getFont(FontManager.AGE_LABEL_FONT).getSize());

                    //This is used to prevent the ageLabels that have the same amount of characters from being misaligned.
                    int fPd = 0;

                    if(pd > 0){
                       fPd = 1;
                    }
                    
                    // When integrated tree drawing option is not selected and range is not phenon range (rather a regular species regular range)
                    if (!range.isPhenonRange && this.speciesPhenonTreeDrawing) {
                    	double rectWidth = range.width - range.nameWidth - WIDTH_FOR_SPECIES_BOX_INTERVAL;
                		double rangeXShiftedWithSpeciesBox = 0;
                		if (enabledPhenonCounter == 0 && totalPhenon != 0) {
                    		rectWidth = rectWidth / totalPhenon;
                    	}
                    	else if (totalPhenon != enabledPhenonCounter) {
                    		rectWidth = (rectWidth / totalPhenon) * enabledPhenonCounter;
                    	}
                		rangeXShiftedWithSpeciesBox = rangeXParent + rectWidth;
                		
                		if (baseYParent >= settings.topAge && baseYParent <= settings.baseAge) {
                			if (p.childRange.rangeXCanvas > range.rangeXCanvas ) {
                        		p.childRange.branchedAsLeftOrRight = "right";
                            	ig.drawCircleYear(rangeXShiftedWithSpeciesBox, baseYParent, starty, 4, Settings.POPUP_HIGHLIGHT_STYLE, p, (p.childRange.branchDisabled || p.childRange.disabled4Priority));
                            	
                            	// draw branch point age label
                            	if(drawAgeLabel)
                            		drawAgeLabelForRangePoint(ig, range, p, rangeXShiftedWithSpeciesBox - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, heightOrig, settings);
                            } else if (p.childRange.branchDisabled) {
                            	if (circleDrawingLeftOrRight.size() > 0) {
                            		if (circleDrawingLeftOrRight.get(p.rcd.branchTo) == "right") {
                            			ig.drawCircleYear(rangeXShiftedWithSpeciesBox, baseYParent, starty, 4, Settings.POPUP_HIGHLIGHT_STYLE, p, (p.childRange.branchDisabled || p.childRange.disabled4Priority));
                            			if(drawAgeLabel)
                            				drawAgeLabelForRangePoint(ig, range, p, rangeXShiftedWithSpeciesBox - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, heightOrig, settings);
                            		} else if (circleDrawingLeftOrRight.get(p.rcd.branchTo) == "left"){
                            			ig.drawCircleYear(rangeXParent, baseYParent, starty, 4, Settings.POPUP_HIGHLIGHT_STYLE, p, (p.childRange.branchDisabled || p.childRange.disabled4Priority));
                            			if(drawAgeLabel)
                            				drawAgeLabelForRangePoint(ig, range, p, rangeXParent - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, heightOrig, settings);
                            		}
                            	} 
                            } else {
                            	p.childRange.branchedAsLeftOrRight = "left";
                            	ig.drawCircleYear(rangeXParent, baseYParent, starty, 4, Settings.POPUP_HIGHLIGHT_STYLE, p, (p.childRange.branchDisabled || p.childRange.disabled4Priority));
                            	if(drawAgeLabel)
                            		drawAgeLabelForRangePoint(ig, range, p, rangeXParent - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, heightOrig, settings);
                            }
                		}
                    } else {
                    	if (baseYParent >= settings.topAge && baseYParent <= settings.baseAge) {
                    		ig.drawCircleYear(rangeXParent, baseYParent, starty, 4, Settings.POPUP_HIGHLIGHT_STYLE, p, (p.childRange.branchDisabled || p.childRange.disabled4Priority));
                            if(drawAgeLabel)
                            	drawAgeLabelForRangePoint(ig, range, p, rangeXParent - RangeColumnRangeAgeLabelPadding + pd*2 + fPd*((fontSize - 6)/3), startx, starty, heightOrig, settings);
                    	}
                    }
                    ig.popGrouping();
                }
            }
        }
    }

    // Family tree related errors
    public static void errorFamilyTree(String errMsg) {
        if (errFamilyTree == null) {
            errFamilyTree = new JFrame("Errors in Family Tree Display");
            SpringLayout layout = new SpringLayout();
            errFamilyTree.setLayout(layout);
            errFamilyTree.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            errFamilyTree.setSize(400, 220);

            errFamilyTreeMessage = new JTextArea(errMsg);
            errFamilyTreeMessage.setEditable(false);
            errFamilyTreeMessage.setWrapStyleWord(true);
            errFamilyTreeMessage.setLineWrap(true);
            errFamilyTreeMessage.setSize(new Dimension(380, 200));

            errFamilyTreeButton = new JButton("OK");
            errFamilyTreeButton.setSize(new Dimension(50, 20));

            errFamilyTreeButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Object item = e.getSource();
                    if (item == errFamilyTreeButton) {
                        errFamilyTree.setVisible(false);
                    }
                }
            });

            errFamilyTree.add(errFamilyTreeMessage);
            errFamilyTree.add(errFamilyTreeButton);
            layout.putConstraint(SpringLayout.WEST, errFamilyTreeMessage, 10, SpringLayout.WEST, errFamilyTree);
            layout.putConstraint(SpringLayout.NORTH, errFamilyTreeMessage, 10, SpringLayout.NORTH, errFamilyTree);
            layout.putConstraint(SpringLayout.WEST, errFamilyTreeButton, 170, SpringLayout.WEST, errFamilyTreeMessage);
            layout.putConstraint(SpringLayout.NORTH, errFamilyTreeButton, 30, SpringLayout.SOUTH, errFamilyTreeMessage);

            //set Location
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            // Determine the new location of the window
            int w = errFamilyTree.getSize().width;
            int h = errFamilyTree.getSize().height;
            int x = (dim.width - w) / 2;
            int y = (dim.height - h) / 2;
            errFamilyTree.setLocation(x, y);

            errFamilyTree.setVisible(true);
        } else {
            errFamilyTree.setVisible(true);
        }

    }

    public static void clearErrMsgFT() {
        errFamilyTree = null;
        errFamilyTreeMessage = null;
        errFamilyTreeButton = null;
        errMsg = "";
    }
}
