/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Peferences - Java - Code Style - Code Templates
 */
package gui;

import gui.crossplot.CrossplotFrame;
import gui.editor.DataEditor;
import gui.settings.SelectedTimes;
import gui.transect.TemplateGen;
import gui.transect.TemplateLoader;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument;

import map.MapInformation;

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import path.ResPath;
import util.CSVReader;
import util.Converter;
import util.Debug;
import util.FileUtils;
import util.HTMLPreprocessor;
import util.JavaVMOptions;
import util.ProgramInfo;

import com.google.gson.stream.JsonWriter;

import datastore.Coloring;
import datastore.DataColumn;
import datastore.DatafileCrypto;
import datastore.Datastore;
import datastore.MetaColumn;
import datastore.loader.Loader1;
import datastore.loader.ParseException;
import datastore.loader.SimpleCharStream;

import java.net.URI;
/**
 * @author alugowsk
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class TSCreator extends JApplet implements ActionListener {
    // Password Settings for requiring a password in order to run the program.
    // Note that the MD5 Hash of the password is listed here, NOT the actual password.
    // This is so that programs like "strings" on *nix and BSD systems cannot easily
    // extract the password from the compiled class files.
    // Options added for 5.0 release:

    // You can get an md5 hash of a string with the command 'md5 -s "your string here" ' on a Mac
    public static final String MD5_HASH_OF_PASSWORD = "97aefc1fe31b8e1872308de56bd4eb39";
    public static boolean REQUIRE_PASSWORD = false;
    public static boolean PASSWORD_ENTERED_CORRECTLY = false;
    public static boolean ENABLE_3D = false;
    public static boolean ENABLE_DATAPACK_ENCRYPTION = true; // When set true, the datapack will be saved as encrypted.
    public static final String MAX_MEMORY = "1G";
    public static final int NUMBER_CHARS_LIMIT_PUBLIC = 3000;
    public static final int NUMBER_DATAPACKS_LIMIT_PUBLIC = 3;
    private static int numDatapacksAdded = 0;
    private JFrame getPRO = null;
    private JTextArea getPROMessage = null;
    private JButton getPROButton = null;
    public static final int DEBUGLEVEL = 1;
    public static String DEFAULT_DATA_FILE = ResPath.getRootlessPath("datapacks.default_data_filename");
    public static String DATAPACK_INFO = ResPath.getRootlessPath("datapacks.datapack_info");
    public static String BUILT_IN_DATA_FILE = null;
    public static boolean command_line_datapack_flag = false;
    public static List<String> command_line_datapacks = null;
    public static int command_line_datapack_id = 0;
    public static String SETTINGS_FILE = null;
    public static boolean command_line_settings_flag = false; // Set to true if datapack is specified on command line
    public static final String DEFAULT_COLORING_FILE = ResPath.getPath("settings_xml.coloring");
    public static String DEFAULT_SETTINGS_FILE = ResPath.getPath("settings_xml.default_settings");
    public static boolean SAVE_COMPLETE_SETTINGS = false;
    public static String autoSaveFile = null;
    public static AutoGenOptions autoGenOptions = null; // set by command-line switches for using TSC as a dumb renderer.
    public static boolean isApplet = false;
    public static boolean isActivationRequired = false;
    public static String activationVersionString = null;//"sfsugrad";
    public static boolean allowDefaultDatafilePasswordCancel = false;
    public static final int NORMAL = 1;
    public static final int SUCCESS = 2;
    public static final int ERROR = 3;
    public static final int WARNING = 4;
    public static final int GENERATING = 1;
    public static final int LOADING = 2;
    public static final int SAVING = 3;
    public static volatile TSCreator curCreator = null;
    public static final boolean showMapStuff = true;
    public static boolean showSearchPane = false;
    public static Datastore db;
    public DataEditor editor = null;
    public static File fileChooserPath = null;
    public static Settings settings;
    public static Settings settings1;
    public static boolean topFlag = true;
    public static boolean baseFlag = true;
    public static boolean vertFlag = true;

    // nag : crossplot frame
    public CrossplotFrame crossplotFrame;
    //
    TSCSVGCanvas displayCanvas;
    TSCSVGScrollPane displayCanvasPane;
    public SVGDocument svgImage;
    ImageGenerator ig = null;
    ColumnImageGenerator columnImageGenerator = null;
    // widgets
    JToolBar toolBar;
    JPanel mainView;
    JEditorPane htmlView;
    CardLayout mainViewLayout;
    public static JFrame tscFrame;
    Container contentPane;
    JPanel displayPanel, topPanel;
    JLabel info;
    
    File selFile = null;
    String extension = null;
    String[] pathArray = null;
    //Static depth file.
    public static File depthFile;
    public static String depthFileUnits;
    //Red Line on the svg, timeline
    public static boolean TIMELINE = false;
    // File menu
    public TSCAction loadReplaceWithBuiltInAction = new TSCAction(this, Language.translate("Replace Data with Default Datapack", true), null, Language.translate("Load data that came with the program while clearing everything currently loaded.", true));
    public TSCAction loadAddWithBuiltInAction = new TSCAction(this, Language.translate("Re-add Default Datapack", true), null, Language.translate("Load data from built in datapack and append to the currently loaded data set.", true));
    public TSCAction loadAddFromFileAction = new TSCAction(this, Language.translate("Add Datapack", true), null, Language.translate("Load data from a file and append to the currently loaded data set.", true));
    public TSCAction loadReplaceWithFileAction = new TSCAction(this, Language.translate("Replace Data with Datapack", true), null, Language.translate("Load data from a file while clearing everything currently loaded.", true));
    public TSCAction clearAction = new TSCAction(this, Language.translate("Clear", true), null, Language.translate("Clear the currently loaded data.", true));
    public TSCAction viewPatternsAction = new TSCAction(this, Language.translate("View Loaded Patterns", true), null, Language.translate("Shows the currently available patterns.", true));
    public TSCAction addPatternsAction = new TSCAction(this, Language.translate("Add Patterns", true), null, Language.translate("Adds patterns from the selected.", true));
    public TSCAction showGTSversion = new TSCAction(this, Language.translate("GTS Version", true), null, Language.translate("Shows what Geological Time Scale is used.", true));
    public TSCAction saveAsAction = new TSCAction(this, Language.translate("Save Datapack As", true), null, Language.translate("Save current data into a single datapack.", true));
    public TSCAction saveAsJsonAction = new TSCAction(this, Language.translate("Save Datapack As Json", true), null, Language.translate("Save current data into a single datapack in json.", true));
    public TSCAction saveSVGAction = new TSCAction(this, Language.translate("Save SVG", true), null, Language.translate("Save the generated image to an SVG file.", true));
    public TSCAction saveRasterAction = new TSCAction(this, Language.translate("Save Bitmap (PNG/JPG)", true), null, Language.translate("Save the generated image to a raster bitmap file.", true));
    public TSCAction savePDFAction = new TSCAction(this, Language.translate("Save PDF", true), null, Language.translate("Save the generated image to a PDF file.", true));
    public TSCAction exitAction = new TSCAction(this, Language.translate("Exit", true), null, Language.translate("Exit", true));
    public TSCAction loadDefaultColoringAction = new TSCAction(this, Language.translate("Load Default Colorings", true), null, Language.translate("Load the default USGS and UNESCO color schemes.", true));
    protected TSCAction appletDoAfterLoading = new TSCAction(this, Language.translate("applet do after loading", true), null, Language.translate("Run this if this is an applet and we're done loading the datafile.", true));
    // Data menu
    public TSCAction encryptDataAction = new TSCAction(this, Language.translate("Protect a datafile...", true), null, Language.translate("Password protect a datafile.", true));
    public TSCAction convertDataPackAction = new TSCAction(this, Language.translate("Convert Datapack to sqlite database...", true), null, Language.translate("Convert datapack to sqlite database.", true));
    public TSCAction editDataAction = new TSCAction(this, Language.translate("Edit column data...", true), null, Language.translate("View/Edit the raw column data.", true));
    public TSCAction saveTemplateAction = new TSCAction(this, Language.translate("Save Transect Template...", true), null, Language.translate("Create and export an SVG image which can be used as a template to create a transect column in a graphical drawing program.", true));
    public TSCAction loadTemplateAction = new TSCAction(this, Language.translate("Load Transect Column from Template...", true), null, "");
    public TSCAction editCrittersAction = new TSCAction(this, Language.translate("Edit Fossils...", true), null, Language.translate("View/Edit the raw column data.", true));
    public TSCAction createCrossplotAction = new TSCAction(this, Language.translate("Create Crossplot", true), null, Language.translate("Create an Age/Depth plot.", true));
    public TSCAction addSomeRascFileAction = null;//new TSCAction(this, Language.translate("addrasc"),null, Language.translate("pure testing function.",true));
    // Image menu
    public TSCAction settingsAction = new TSCAction(this, Language.translate("Settings...", true), null, Language.translate("Show the settings dialog box.", true));
    public TSCAction generateAction = new TSCAction(this, Language.translate("Generate Chart", true), null, Language.translate("Generate the image based on the current settings.", true));
    public TSCAction imageSizeAction = new TSCAction(this, Language.translate("Image Size", true), null, Language.translate("View/Change the physical size of the generated image.", true));
    public TSCAction zoomInAction = new TSCAction(this, Language.translate("Zoom In", true), ResPath.getPath("icons.zoomin"), Language.translate("Zoom in on the image.", true));
    public TSCAction zoomOutAction = new TSCAction(this, Language.translate("Zoom Out", true), ResPath.getPath("icons.zoomout"), Language.translate("Zoom out of the image.", true));
    public TSCAction actualSizeAction = new TSCAction(this, Language.translate("Actual Size", true), ResPath.getPath("icons.zoom100"), Language.translate("Set the zoom to 100%.", true));
    public TSCAction fitToWindowAction = new TSCAction(this, Language.translate("Fit to Window", true), ResPath.getPath("icons.zoomfit"), Language.translate("Zoom fit. Zoom such that the entire image is visible.", true));
    public TSCAction showLine = new TSCAction(this, Language.translate("Enable Line Check", true), ResPath.getPath("icons.showline"), Language.translate("Time Line On/Off", true));
    // User Guides menu
    //  public TSCAction languageAction = new TSCAction(this,"Language", null, Language.translate("Select your preferred language",true));
    public TSCAction quickstartAction = new TSCAction(this, Language.translate("Quick Start Guide", true), null, Language.translate("A short guide for getting something to show up.", true));
    public TSCAction tourAction = new TSCAction(this, Language.translate("Tour", true), null, Language.translate("A somewhat longer guide for getting something to show up.", true));
    // Change 1
    public TSCAction contactAction = new TSCAction(this, Language.translate("Contact Us", true), null, Language.translate("A contact box that pulls up contact info for our system", true));
    public TSCAction featureInfoAction = new TSCAction(this, Language.translate("Features reference", true), null, Language.translate("An in-depth look at the options of the program.", true));
    public TSCAction licenseAction = new TSCAction(this, Language.translate("Software License", true), null, Language.translate("License for using this software.", true));
    public TSCAction fileFormatInfoAction = new TSCAction(this, Language.translate("File Format info", true), null, Language.translate("Information about how the file format for the data file is set up.", true));
    public TSCAction websiteInfoAction = new TSCAction(this, Language.translate("Website...", true), null, Language.translate("Link to your page on the TSCreator website.", true));
    public TSCAction aboutAction = new TSCAction(this, Language.translate("About", true), null, Language.translate("Version info and copyright.", true));
    // Server Variabels Nag::
    public static final int MIN_SERVER_THREADS = 1; // Default keep one thread
    public static final int MAX_SERVER_THREADS = 4; // Default keep one thread
    // public static boolean SERVER_MODE = false;
    public static String SERVER_NEW_SETTINGS = ProgramInfo.get("SERVER_NEW_SETTINGS");
    public static String SERVER_TEMP_SETTINGS = ProgramInfo.get("SERVER_TEMP_SETTINGS");
    public static String SERVER_USED_SETTINGS = ProgramInfo.get("SERVER_USED_SETTINGS");
    public static String SERVER_OUTPUT_SETTINGS = ProgramInfo.get("SERVER_OUTPUT_SETTINGS");
    public static String SERVER_PDF_OUTPUT = ProgramInfo.get("SERVER_PDF_OUTPUT");
    public static int NUMBER_OF_THREADS;
    //Rewriting column packs Nag::
    public static String SERVER_COL_PACKS = ProgramInfo.get("SERVER_COL_PACKS");
    public static String SERVER_COL_OUTPUT = ProgramInfo.get("SERVER_COL_OUTPUT");
    public static boolean REWRITE_COLUMN_PACK = false;
    public static String columnpackIn;
    public static String columnpackOut;
    //Node Mode
    public static boolean NODE_MODE = false;
    //VM Already Spawned Mode
    public static boolean VM_ALREADY_SPAWNED = false;
    // Desktop Verison for feedback type.
    public static boolean DESKTOP_VERSION = true;
    // Identify if File has optional flags or starts with data
    public static boolean containsOptionalFlags = true;
    /**
     * This constructor is used when run as an application (main() calls it)
     */
    public static final Object lock = new Object();
    public static boolean testingMode = false;

    public static int myOrder = 0;
    //public MetaColumn derp;

    
    HeaderPanel hp;
    File tempOut = null;
    String tempOutLoc;
    File tempIn = null;
    LASConverterGUI lg = null;


    public JMenu forum = new JMenu(Language.translate("Forum", true));
	private int numCharsInDatapack = -1;
	private int numLinesInDatapack = -1;
	private boolean evTreeColumnExists = false;
    /**
     * This constructor is used when run as an application (main() calls it)
     *
     * @param isApplet - ignored
     */
    public TSCreator(boolean isApplet) {
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This constructor is used when run as an applet (the JVM calls it)
     */
    public TSCreator() {
        if (!isApplet) {
            getContentPane().add(new JLabel("<html>This is a standalone application version of TSCreator. It cannot be used as a Java applet.</html>"));
            return;
        }
    }

    public final void initialize() throws Exception {
        curCreator = this;
        //JavaVersion.printVersion();
        if (autoSaveFile != null) {
            if (autoGenOptions == null && TSCreator.BUILT_IN_DATA_FILE == null) {
                autoGenOptions = new AutoGenOptions();
            }

            // load the default color sets
            this.loadDefaultColoring();

            // load the data
            this.loadDefaultData();
            this.generateImage();

            if (svgImage == null) {
                System.out.println("There was an error generating the image. Quitting.");
                exit();
            }

            RasterSave.RasterSaveOptions autoRasterSaveOptions = null;
            // figure out dimensions of bitmap
            if (TSCreator.BUILT_IN_DATA_FILE == null) {
                autoRasterSaveOptions = autoGenOptions.getRasterSaveOptions(svgImage);
            }

            // save
            String format = FileUtils.getExtension(autoSaveFile);
            if (format == null) {
                System.out.println("Please use an extension on the output file so TSC knows what format to write. Got: " + autoSaveFile);
                exit();
            }

            try {
                if (TSCreator.BUILT_IN_DATA_FILE == null && autoRasterSaveOptions != null) {
                    saveToFile(autoSaveFile, format.toUpperCase(), autoRasterSaveOptions);
                } else {
                    saveToFile(autoSaveFile, format.toUpperCase(), null);
                }
            } catch (Exception e) {
                System.out.println("Error writing output: " + e.getMessage());
                exit();
            } catch (OutOfMemoryError e) {
                System.out.println("Out of memory! You can increase the amount of memory available to TSC using the -Xmx JVM switch. For example, 'java -Xmx1g -jar TSC.jar' will give TSC 1GB of RAM.");
                exit();
            }

            exit();
        }

        generateAction.useThread(this);
        loadAddFromFileAction.useThread(this);
        loadAddWithBuiltInAction.useThread(this);
        loadReplaceWithBuiltInAction.useThread(this);
        loadReplaceWithFileAction.useThread(this);

        //Create and set up the window.
        if (isApplet) {
            contentPane = getContentPane();
        } else {
            tscFrame = new JFrame("Time Scale Creator");
            tscFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            tscFrame.setSize(720, 750);
            contentPane = tscFrame.getContentPane();
        }

        //Create and set up the panels.
        displayPanel = new JPanel();
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        //Add the widgets.
        addWidgets();

        //Set up the menu bar
        if (!isApplet) {
            addMenu();
        }

        //@Gangi -- Make the seach panel available only in Pro Version.
        if (!isApplet) {
            if (!ProgramInfo.isPublic()) {
                showSearchPane = true;
            }
        }

        //Add the panels to the window.
        contentPane.add(topPanel, BorderLayout.NORTH);

        mainViewLayout = new CardLayout();
        mainView = new JPanel(mainViewLayout);

        // Add the SVG viewer
        displayCanvas = new TSCSVGCanvas();
        displayCanvasPane = new TSCSVGScrollPane(displayCanvas);
        mainView.add(displayCanvasPane, "displayCanvas");

        // Set the JSVGCanvas listeners.
        displayCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            //			@Override
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                showInLabel(Language.translate("Rendering Image: Loading...", true), NORMAL);
            }

            //			@Override
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                showInLabel(Language.translate("Document Loaded.", true), NORMAL);
            }

            //			@Override
            public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
                showInLabel(Language.translate("Rendering Failed", true), ERROR);
                bigError(true);
            }
        });

        displayCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                showInLabel(Language.translate("Rendering Image: Building...", true), NORMAL);
            }

            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                showInLabel(Language.translate("Build Done.", true), NORMAL);
            }

            @Override
            public void gvtBuildFailed(GVTTreeBuilderEvent e) {
                showInLabel(Language.translate("Rendering Failed", true), ERROR);
                bigError(true);
            }
        });

        displayCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            @Override
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                showInLabel(Language.translate("Rendering Image: Rendering...", true), NORMAL);
            }

            @Override
            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                showInLabel("Successfully Rendered", SUCCESS);

                // Nag: Try to revert to previous Zoom if needed.
                displayCanvasPane.zoomToPreviousZoom();

                // try to clean up some ram for later
                Runtime.getRuntime().gc();
            }

            @Override
            public void gvtRenderingFailed(GVTTreeRendererEvent e) {
                showInLabel(Language.translate("Rendering Failed", true), ERROR);
                bigError(true);
            }
        });

        displayCanvas.clearLinkListeners();
        displayCanvas.addLinkActivationListener(new TSCLinkActivationListener());

        // Add the HTML viewer
        htmlView = new JEditorPane();
        htmlView.setEditable(false);
        htmlView.setContentType("text/html");
        htmlView.addHyperlinkListener(new TSCLinkActivationListener());
        htmlView.setText(HTMLPreprocessor.process(ResPath.getPath("html.introscreen")));
        htmlView.addHyperlinkListener(new TSCLinkActivationListener());
        // The "setBase" function sets the root directory for all relative paths in the HTMLDocument.
        ((HTMLDocument) (htmlView.getDocument())).setBase(HTMLPreprocessor.getGlobalBaseDirectory());

        HTMLDocument doc = (HTMLDocument) htmlView.getDocument();
        if (doc.getBase() != null) {
            Debug.print("HTMLDocument base for introscreen = " + doc.getBase().toString());
        } else {
            Debug.print("HTMLDocument base for introscreen = null.");
        }


        mainView.add(new JScrollPane(htmlView), "htmlView");

        contentPane.add(mainView);
        mainViewLayout.show(mainView, "htmlView");

        // set the default enabled status for the toolbar buttons
        loadSuccessful(false);
        saveSVGAction.setEnabled(false);
        saveRasterAction.setEnabled(false);
        savePDFAction.setEnabled(false);
        //clearAction.setEnabled(false);

        imageSizeAction.setEnabled(false);

        zoomInAction.setEnabled(false);
        zoomOutAction.setEnabled(false);
        actualSizeAction.setEnabled(false);
        fitToWindowAction.setEnabled(false);
        showLine.setEnabled(false);
        //Display the window.
        if (!isApplet) {
            tscFrame.setVisible(true);
        }

        // load the default color sets
        loadDefaultColoringAction.actionPerformed(null);

        // load the default data
        logAndShow(Language.translate("Loading built-in data...", true), NORMAL);
        clearData(); // this will make the loader create a new db, thus clearing the current stuff
        numDatapacksAdded = 0;
        loadDefaultData();


        File settingsFileCheck = new File(Settings.settingsBackupFile);
        settingsFileCheck.deleteOnExit();
        if (settingsFileCheck.exists()) {
            Settings.saveBackup(settingsFileCheck);
        }

        synchronized (lock) {
            lock.notifyAll();
        }

    }

    private void addWidgets() {
        // Set up the info label
        info = new JLabel("", SwingConstants.LEFT);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Set up the toolbar
        toolBar = new JToolBar("Main");
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolBar.add(settingsAction);
        toolBar.add(generateAction);
        toolBar.addSeparator();
        toolBar.add(zoomInAction);
        toolBar.add(zoomOutAction);
        toolBar.add(actualSizeAction);
        toolBar.add(fitToWindowAction);
        toolBar.add(showLine);

        toolBar.addSeparator();

        if (addSomeRascFileAction != null) {
            toolBar.add(addSomeRascFileAction);
        }

        // Add the widgets to the top panel
        topPanel.add(toolBar);
        topPanel.add(info);
    }

    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu(Language.translate("File", true));
        JMenu data = new JMenu(Language.translate("Data", true));
        JMenu image = new JMenu(Language.translate("Image", true));
        JMenu help = new JMenu(Language.translate("Help", true));
        JMenu contact = new JMenu(Language.translate("Contact", true));
        
        // File menu
        menuBar.add(file);

        file.add(loadReplaceWithBuiltInAction);
        file.add(loadAddWithBuiltInAction);
        file.add(loadAddFromFileAction);
        file.add(loadReplaceWithFileAction);

        // New for v4.3.0: only enable saving datapacks in Pro version
        if (!ProgramInfo.isPublic()) {
            file.addSeparator();
            //file.add(saveAsAction);
            //file.add(clearAction);
        }

        //file.add(saveAsJsonAction);

        //file.addSeparator();

        file.add(viewPatternsAction);
        file.add(addPatternsAction);

        file.addSeparator();
        file.add(showGTSversion);
        file.addSeparator();
        file.add(saveSVGAction);
        file.add(savePDFAction);
        file.add(saveRasterAction);
        if (!isApplet) {
            file.addSeparator();
            file.add(exitAction);
        }

        // Data menu
        if (!ProgramInfo.isPublic()) {
            menuBar.add(data);
            data.add(encryptDataAction);
            data.addSeparator();
            //data.add(editDataAction);
            data.add(saveTemplateAction);
            data.add(loadTemplateAction);
            data.add(convertDataPackAction);
            convertDataPackAction.setEnabled(false);
        }

        // Image menu
        menuBar.add(image);
        image.add(settingsAction);
        image.add(generateAction);

        image.addSeparator();

        image.add(imageSizeAction);

        image.addSeparator();

        image.add(zoomInAction);
        image.add(zoomOutAction);

        image.add(actualSizeAction);
        image.add(fitToWindowAction);
        image.add(showLine);

        JMenu crossplotMenu = new JMenu(Language.translate("Crossplot", true));
        crossplotMenu.add(createCrossplotAction);
        menuBar.add(crossplotMenu);

        // Help menu
        menuBar.add(help);
        help.add(Language.languageSelectionPanel());//language selection
        help.add(quickstartAction);
        help.add(tourAction);
        help.add(featureInfoAction);
        help.add(licenseAction);
        help.add(fileFormatInfoAction);

        if (!ProgramInfo.isPublic()) {
            help.addSeparator();
            help.add(websiteInfoAction);
            help.addSeparator();
        }
        help.add(aboutAction);
        //change 1
        menuBar.add(contact);
        contact.add(contactAction);
        
        // forum
        menuBar.add(forum);
        // Mouselistener added to forum
        forum.addMouseListener(new MouseListener(){
        	
        	public void mouseClicked(MouseEvent e){
              try{
          	     URI uri;
				try {
					uri = new URI("https://engineering.purdue.edu/Stratigraphy/tscreator/forum/forum.php");
					Desktop.getDesktop().browse(uri);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
              } catch (IOException ioe) {
                	ioe.printStackTrace();
                }
        	}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        });
        
        // add the menu bar to the frame
        if (isApplet) {
            this.setJMenuBar(menuBar);
        } else {
            tscFrame.setJMenuBar(menuBar);
        }
    }

    public void loadDefaultColoring() {
        try {
            Coloring.readColoring(FileUtils.getInputStream(DEFAULT_COLORING_FILE));
        } catch (Exception e) {
            ErrorHandler.showError(e, null, "Error loading color schemes!", ERROR);
        }
    }

    public DataColumn.FileInfo figureOutWhichDefaultDataWeAreUsing() {
        DataColumn.FileInfo fileInfo = new DataColumn.FileInfo();

        StringTokenizer tk = new StringTokenizer(BUILT_IN_DATA_FILE, ",");
        while (tk.hasMoreElements()) {
            String attempt = tk.nextToken();

            try {
                InputStream is = FileUtils.getInputStream(attempt, true);
                is.close();
            } catch (Exception e) {
                // nope, doesn't exit
                continue;
            }

            // yay! we found one
            fileInfo.loadPath = attempt;
            fileInfo.resource = true;

            URL base = FileUtils.getURL(attempt);
            String baseS = base.toString();
            baseS = baseS.replaceAll("/[^/]*?\\z", "/");
            fileInfo.workingDir = baseS;
            try {
                fileInfo.baseURL = new URL(baseS);
            } catch (java.net.MalformedURLException mue) {
            }
            fileInfo.ensureTrailingSlashes();


            Debug.print("Datafile base directory = " + fileInfo.workingDir);
            Debug.print("Datafile base URL = " + fileInfo.baseURL);
            return fileInfo;
        }

        return null;
    }

    public void loadDefaultData() throws Exception {
        int dataLoadSuccess = 0;
        try {
            if (db == null) {
                db = new Datastore();
                settings = new Settings(db);
                settings.addTSObj(this);
                settings.setGenerateAction(generateAction);
                db.addBasicColumns();
            } else if (settings == null) {
                settings = new Settings(db);
                settings.addTSObj(this);
                settings.setGenerateAction(generateAction);
            }

            if (CrossplotFrame.crossplot != null) {
                CrossplotFrame.crossplot.clearAllPoints();
            }

            doUIChanges(LOADING, true, false);

            if (TSCreator.NODE_MODE) {
                this.loadDatapackFile(new File(TSCreator.BUILT_IN_DATA_FILE));
            } else {
                DataColumn.FileInfo fileInfo = figureOutWhichDefaultDataWeAreUsing();

                if (fileInfo == null) {
                    throw new Exception("Default datafile missing!");
                }

                if (!loadEncrypted(fileInfo, true)) {
                    throw new Exception("Nothing Loaded.");
                }
            }

            // set up the FontManagers for the whole image
            db.rootColumn.setParentFontManager(settings.fonts);

            // notify the settings that things have changed
            settings.dataAdded();
            
            
            double changedTopAge = settings.topAge;
            double changedBaseAge = settings.baseAge;
            double changedVertScale = settings.scale;

            // apply default settings (if any)
            applyDefaultSettings();
            if (TSCreator.NODE_MODE && command_line_datapack_flag && command_line_datapacks != null ) {
            	while(command_line_datapack_id < command_line_datapacks.size()) {
            		numDatapacksAdded += 1;
            		loadDataFromFile(false, false);
            	}
            	// Apply default settings again as the default settings include the columns for new datapacks too
            	if(command_line_settings_flag)
            		applyDefaultSettings();
            }

            // set top age, base age and vertical scale as given in the default datapack 
            if (settings.changedDefaultTopAge) {
            	String topString = Double.toString(changedTopAge);
            	topFlag = true;

            	if(topFlag){
            		settings.setTopAge(db.getCurrentUnits(), topString);
            		settings.timesChanged(settings.TIME_INTERVAL_CHANGED, db.getCurrentUnits());
            	}
            }

            if (settings.changedDefaultBaseAge) {
            	String baseString = Double.toString(changedBaseAge);
            	baseFlag = true;

            	if(baseFlag){
            		settings.setBaseAge(db.getCurrentUnits(), baseString);
            		settings.timesChanged(settings.TIME_INTERVAL_CHANGED, db.getCurrentUnits());
            	}
            }

            if (settings.changedDefaultScale) {
            	String vertScale = Double.toString(changedVertScale);
                      	   
            	vertFlag = true;
                      	  
            	if(vertFlag){
            		settings.setVertScale(db.getCurrentUnits(), vertScale);
            		settings.timesChanged(settings.VERTSCALE_CHANGED, db.getCurrentUnits());
            	}
            }

            //generate settings
            if (TSCreator.SETTINGS_FILE != null) {
                settings.setupWrite(TSCreator.SETTINGS_FILE);
            }
            // announce success
            logAndShow(Language.translate("Data loaded successfully!", true), SUCCESS);

            loadSuccessful(true);
        } catch (Exception e) {
            logAndShow(Language.translate("Unable to load datafile!", true), ERROR);
            if (e.getMessage().equals("Public Limit Reached")) {
                showGetProPopup();
            } else {
                ErrorHandler.showError(e, null, "Error loading data file!", ERROR);//shows full error screen, Nicki added this line 2-2-11
            }
        }
        doUIChanges(LOADING, false, false);
        //sets static version of settings variable
        getStaticSettings();
    }
    
    

    public void applyDefaultSettings() {
        // see if these exist
        if (TSCreator.autoGenOptions != null) {
            // automatically figure out the top and base ages and the vertical scale.
            settings.setFromAutoGenOptions(TSCreator.autoGenOptions);
            settings.grayOutEmptyColumns();
            db.setAutomaticGenSettings(settings, TSCreator.autoGenOptions);
        }

        if (FileUtils.doesFileExist(DEFAULT_SETTINGS_FILE)) {
            if (!settings.isPopulated && TSCreator.autoGenOptions == null) {
                settings.populate(db);
            }
            settings.setupRead(DEFAULT_SETTINGS_FILE);
        }
        settings.printTimeSelections();
    }

    public void saveDataAsJson() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        FileFilter filter = new FileNameExtensionFilter("*json", "json");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(tscFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String absolutePath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), "json");
            JsonWriter writer = new JsonWriter(new FileWriter(absolutePath));
            db.writeToJson(writer);
            writer.close();

            logAndShow(Language.translate("Saved data to ", true) + absolutePath, SUCCESS);
        }
    }

    public void saveDataAs() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileFilter filter = new FileNameExtensionFilter("*.txt, *.zip", "txt", "zip");
        chooser.setFileFilter(filter);
        if (fileChooserPath != null) {
            chooser.setCurrentDirectory(fileChooserPath);
        }
        int returnVal = chooser.showSaveDialog(tscFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String absolutePath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), "txt");
            fileChooserPath = chooser.getCurrentDirectory();
            String backupOutPath;
            if (ENABLE_DATAPACK_ENCRYPTION) { // When saving the datapack save the file as a backup
                backupOutPath = absolutePath + ".bkp";
            } else {
                backupOutPath = absolutePath;
            }

            logAndShow(Language.translate("Saving data to ", true) + absolutePath, NORMAL);

            BufferedWriter bw = new BufferedWriter(new FileWriter(backupOutPath));

            db.write(bw);

            bw.close();

            //@Gangi -- Saving the Datapack in PRO is to be encrypted.

            if (ENABLE_DATAPACK_ENCRYPTION) {
                FileInputStream fis = new FileInputStream(backupOutPath);

                FileOutputStream fos = new FileOutputStream(absolutePath);

                DatafileCrypto.HeaderInfo hi = new DatafileCrypto.HeaderInfo();
                hi.message = null;

                String error;
                if ((error = DatafileCrypto.encrypt(fos, fis, null, hi)) == null) {
                    fis.close();
                    fos.close();

                    logAndShow(Language.translate("Saved data to ", true) + absolutePath, SUCCESS);

                } else {
                    logAndShow(Language.translate("Error while protecting file: ", true) + error, ERROR);
                    fis.close();
                    fos.close();
                }

                File tempFileHandle = new File(backupOutPath);
                tempFileHandle.delete();
            } else {

                logAndShow(Language.translate("Saved data to ", true) + absolutePath, SUCCESS);
            }

        }
    }

    public File openLoadFileChooser() {

        FileDialog fileChooser = new FileDialog(tscFrame, "Choose Datapack to Load", FileDialog.LOAD);
        fileChooser.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File directory, String filename) {
                return (filename.endsWith(".txt")
                		|| filename.endsWith(".tre")
                		|| filename.endsWith(".nwk")
                		|| filename.endsWith(".newick")
                		|| filename.endsWith(".nex")
                		|| filename.endsWith(".nexus")
                        || filename.endsWith(".dpk")
                        || filename.endsWith(".mdpk")
                        || filename.endsWith(".map")
                        || filename.endsWith(".zip")
                		|| filename.endsWith(".las"));
            }
        });
        fileChooser.setVisible(true);
        File selFile = null;
        if (fileChooser.getFile() != null && !fileChooser.getFile().isEmpty()) {
            selFile = new File(fileChooser.getDirectory() + fileChooser.getFile());
       
        }
        
        
        return selFile;
    }
    
    public void loadLASFile(boolean clearCurrent, boolean addDefault) {
        tempIn = selFile;
        
        lg = new LASConverterGUI(selFile.getAbsolutePath(), this);
        lg.f.setVisible(true);
        
        class MyActionListener implements ActionListener {
     	    private TSCreator tsObj;
     	    private boolean clearCurrent;
     	    private boolean addDefault;
     	    private Datastore db;
     	    private LASConverterGUI lg;
     	    public MyActionListener(TSCreator tsObj, Datastore db, boolean clearCurrent, boolean addDefault, LASConverterGUI lg) {
     	        this.tsObj = tsObj;
     	        this.clearCurrent = clearCurrent;
     	        this.addDefault = addDefault;
     	        this.db = db;
     	        this.lg = lg;
     	    }

     	    public void actionPerformed(ActionEvent evt) {
     	    	
     	    }
     	}
        
        lg.OKButtonTop.addActionListener(new MyActionListener(this, db, clearCurrent, addDefault, this.lg) {
            public void actionPerformed(ActionEvent evt) {
           	String fileName = OKButtonActionPerformed(evt); 
               selFile = new File(fileName);  
                  
               if (selFile != null) {
                  // load the datapack
                  logAndShow("Loading datapack :: " + selFile.getName(), SUCCESS);
                  ErrorHandler.log("Loading data from " + selFile.getAbsolutePath());

                  if (ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled()) {
                      savePDFAction.setText(savePDFAction.getText() + " (Disabled by Demo)");
                      savePDFAction.setEnabled(false);

                      saveRasterAction.setText(saveRasterAction.getText() + " (Disabled by Demo)");
                      saveRasterAction.setEnabled(false);

                      saveSVGAction.setText(saveSVGAction.getText() + " (Disabled by Demo)");
                      saveSVGAction.setEnabled(false);

                      ProgramInfo.setSavingEnabled(false);
                  }

                  try {
                      if (super.clearCurrent) {
                          clearData();
                      }
                      
                      // load the datapack
               	  
                      if (super.db == null || super.clearCurrent) {
                   	       db = new Datastore();
                           super.db = db;
                           settings = new Settings(super.db);
                           settings.addTSObj(super.tsObj);
                           settings.setGenerateAction(generateAction);
                      	   super.db.addBasicColumns();

                           //String topString = Double.toString(settings1.topAge);
                      	   //String baseString = Double.toString(settings1.baseAge);
                      	   //String vertScale = Double.toString(settings1.scale);

                      	   loadDatapackFile(selFile);
 
                           topFlag = true;
                   	       baseFlag = true;
                   	       vertFlag = true;

                      	   if(vertFlag){
                          	   settings.setVertScale(db.getCurrentUnits(), Double.toString(settings1.scale));
          	                }
                          	if(topFlag || baseFlag){
                               settings.setTopAge(db.getCurrentUnits(), Double.toString(settings1.topAge));
                               settings.setBaseAge(db.getCurrentUnits(), Double.toString(settings1.baseAge));
                          	}
                            super.db.rootColumn.setParentFontManager(settings.fonts);

                            //notify the settings that things have changed
                            settings.dataAdded();

                      }
                      else{
                      	    topFlag = true;
                     	    baseFlag = true;
                     	    vertFlag = true;
                      	    loadDatapackFile(selFile);

                            //String topString = Double.toString(settings1.topAge);
                            //String baseString = Double.toString(settings1.baseAge);
                            //String vertScale = Double.toString(settings1.scale);

                      	    if(vertFlag){
                           	   settings.setVertScale(db.getCurrentUnits(), Double.toString(settings1.scale));
       	                    }
                       	    if(topFlag || baseFlag){
                                settings.setTopAge(db.getCurrentUnits(), Double.toString(settings1.topAge));
                                settings.setBaseAge(db.getCurrentUnits(), Double.toString(settings1.baseAge));
                       	    }
                      	    settings.dataAdded(); 
                      }
                  } catch (Exception e) {
                      // announce and log failure
                      showInLabel(Language.translate("Error loading data file!", true), ERROR);
                      String errorMsgList = "";
                      if (db.errorList.size() > 0) {
                          Iterator iter = db.errorList.iterator();
                          while (iter.hasNext()) {
                              errorMsgList += "\n" + (String) iter.next();
                          }
                          errorMsgList += "\n";
                          ErrorHandler.showError(errorMsgList + e.getLocalizedMessage(), "Error loading file!", ERROR);
                      } else {
                          ErrorHandler.showError(null, "Error loading data file!", ERROR);//shows full error screen, Nicki added this line 2-2-11
                      }
                      doUIChanges(LOADING, false, true);
                  } 
                 
                  lg.f.dispose();
               }
              }
          }); 

        lg.OKButtonBottom.addActionListener(new MyActionListener(this, db, clearCurrent, addDefault, this.lg) {
          public void actionPerformed(ActionEvent evt) {
         	String fileName = OKButtonActionPerformed(evt); 
             selFile = new File(fileName);  

             if (selFile != null) {
                // load the datapack
                logAndShow("Loading datapack :: " + selFile.getName(), SUCCESS);
                ErrorHandler.log("Loading data from " + selFile.getAbsolutePath());

                if (ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled()) {
                    savePDFAction.setText(savePDFAction.getText() + " (Disabled by Demo)");
                    savePDFAction.setEnabled(false);

                    saveRasterAction.setText(saveRasterAction.getText() + " (Disabled by Demo)");
                    saveRasterAction.setEnabled(false);

                    saveSVGAction.setText(saveSVGAction.getText() + " (Disabled by Demo)");
                    saveSVGAction.setEnabled(false);

                    ProgramInfo.setSavingEnabled(false);
                }

                try {
                    if (super.clearCurrent) {
                        clearData();
                    }

                    // load the datapack

                    if (super.db == null || super.clearCurrent) {
                        db = new Datastore();
                        super.db = db;
                        settings = new Settings(super.db);
                        settings.addTSObj(super.tsObj);
                        settings.setGenerateAction(generateAction);
                        super.db.addBasicColumns();
                        //String topString = Double.toString(settings1.topAge);
                        //String baseString = Double.toString(settings1.baseAge);
                        //String vertScale = Double.toString(settings1.scale);

                        topFlag = true;
                        baseFlag = true;
                        vertFlag = true;

                        loadDatapackFile(selFile);

                        if(vertFlag){
                           settings.setVertScale(db.getCurrentUnits(), Double.toString(settings1.scale));
                        }
                        if(topFlag || baseFlag){
                           settings.setTopAge(db.getCurrentUnits(), Double.toString(settings1.topAge));
                           settings.setBaseAge(db.getCurrentUnits(), Double.toString(settings1.baseAge));
                        }
                        super.db.rootColumn.setParentFontManager(settings.fonts);

                         // notify the settings that things have changed
                        settings.dataAdded();

                    }
                    else{
                    	topFlag = true;
                   	    baseFlag = true;
                   	    vertFlag = true;
                    	loadDatapackFile(selFile);

                    	//String topString = Double.toString(settings1.topAge);
                    	//String baseString = Double.toString(settings1.baseAge);
                    	//String vertScale = Double.toString(settings1.scale);

                    	if(vertFlag){
                     	   settings.setVertScale(db.getCurrentUnits(), Double.toString(settings1.scale));
     	                }
                     	if(topFlag || baseFlag){
                     	   settings.setTopAge(db.getCurrentUnits(), Double.toString(settings1.topAge));
                           settings.setBaseAge(db.getCurrentUnits(), Double.toString(settings1.baseAge));
                     	}
                    	settings.dataAdded(); 
                    }
                } catch (Exception e) {
                    // announce and log failure
                    showInLabel(Language.translate("Error loading data file!", true), ERROR);
                    String errorMsgList = "";
                    if (db.errorList.size() > 0) {
                        Iterator iter = db.errorList.iterator();
                        while (iter.hasNext()) {
                            errorMsgList += "\n" + (String) iter.next();
                        }
                        errorMsgList += "\n";
                        ErrorHandler.showError(errorMsgList + e.getLocalizedMessage(), "Error loading file!", ERROR);
                    } else {
                        ErrorHandler.showError(null, "Error loading data file!", ERROR);//shows full error screen, Nicki added this line 2-2-11
                    }
                    doUIChanges(LOADING, false, true);
                } 
               
                lg.f.dispose();
             }
            }
        }); 
    }
    
    public void loadDataFromFile(boolean clearCurrent, boolean addDefault) throws Exception {
        
        if(addDefault){
            selFile = new File(TSCreator.BUILT_IN_DATA_FILE);
        }
        else{
        	if (NODE_MODE == false) {
        		selFile = this.openLoadFileChooser();
        	}
        	else {
        		System.out.println(command_line_datapacks.get(command_line_datapack_id));
        		selFile = new File(command_line_datapacks.get(command_line_datapack_id));
        		command_line_datapack_id++;
        	}
        }
    	
    	if (selFile != null) {
    		pathArray = selFile.getAbsolutePath().split("\\.");
        	extension = pathArray[pathArray.length - 1];
    	} 
        if(extension != null && extension.equals("las")){
           loadLASFile(clearCurrent, addDefault);
        }    
        else{
          if (selFile != null) {
            // load the datapack
            logAndShow("Loading datapack :: " + selFile.getName(), SUCCESS);
            ErrorHandler.log("Loading data from " + selFile.getAbsolutePath());

            // Pro Demo disables saving if you loaded an external file
            // Since the previous code had isProDemo = isPublic, then this is being changed to isPublic
            if (ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled()) {
                savePDFAction.setText(savePDFAction.getText() + " (Disabled by Demo)");
                savePDFAction.setEnabled(false);

                saveRasterAction.setText(saveRasterAction.getText() + " (Disabled by Demo)");
                saveRasterAction.setEnabled(false);

                saveSVGAction.setText(saveSVGAction.getText() + " (Disabled by Demo)");
                saveSVGAction.setEnabled(false)                	;

                ProgramInfo.setSavingEnabled(false);
            }

            try {
                if (clearCurrent) {
                    clearData();
                }

                /*
                if (db == null || clearCurrent) {
                    db = new Datastore();
                    settings = new Settings(db);
                    settings.addTSObj(this);
                    settings.setGenerateAction(generateAction);
                    db.addBasicColumns();
                }
                */

                // load the datapack
                if (db == null || clearCurrent) {
                    db = new Datastore();
                	settings = new Settings(db);
                	settings.addTSObj(this);
                	settings.setGenerateAction(generateAction);
                	db.addBasicColumns();

                	myOrder = 0;
                	MetaColumn.flamer = 0;
                	
                	topFlag = true;
               	    baseFlag = true;
               	    vertFlag = true;

                	loadDatapackFile(selFile);

                	String topString = Double.toString(settings.topAge);
                	String baseString = Double.toString(settings.baseAge);
                	String vertScale = Double.toString(settings.scale);

                	if(vertFlag){
                	   settings.setVertScale(db.getCurrentUnits(), vertScale);
	                }
                	if(topFlag || baseFlag){
                	   settings.setTopAge(db.getCurrentUnits(), topString);
                       settings.setBaseAge(db.getCurrentUnits(), baseString);
                	}

                	db.rootColumn.setParentFontManager(settings.fonts); 
                    settings.dataAdded(); 
                }
                else{
                	topFlag = true;
               	    baseFlag = true;
               	    vertFlag = true;
                	loadDatapackFile(selFile);

                	String topString = Double.toString(settings.topAge);
                	String baseString = Double.toString(settings.baseAge);
                	String vertScale = Double.toString(settings.scale);

                	if(vertFlag){
                	   settings.setVertScale(db.getCurrentUnits(), vertScale);
	                }
                	if(topFlag || baseFlag){
                	   settings.setTopAge(db.getCurrentUnits(), topString);
                       settings.setBaseAge(db.getCurrentUnits(), baseString);
                	}

                	settings.dataAdded();
                }
            } catch (Exception e) {
                // announce and log failure
                showInLabel(Language.translate("Error loading data file!", true), ERROR);
                String errorMsgList = "";
                if (db.errorList.size() > 0) {
                    Iterator iter = db.errorList.iterator();
                    while (iter.hasNext()) {
                        errorMsgList += "\n" + (String) iter.next();
                    }
                    errorMsgList += "\n";
                    ErrorHandler.showError(errorMsgList + e.getLocalizedMessage(), "Error loading file!", ERROR);
                } else {
                    ErrorHandler.showError(null, "Error loading data file!", ERROR);//shows full error screen, Nicki added this line 2-2-11
                }
                this.doUIChanges(LOADING, false, true);
            }
        }
      }
      
    }

    //Server mode -- adds columns from the current datapack. (@nag)
    public void addDatapack(File server_datapack) throws Exception {
        try {
            if (db == null) {
                db = new Datastore();
                settings = new Settings(db);
                settings.addTSObj(this);
                settings.setGenerateAction(generateAction);
                db.addBasicColumns();
            }
            // load the datapack
            loadDatapackFile(server_datapack);
        } catch (Exception e) {
            // announce and log failure
            showInLabel(Language.translate("Error loading data file!", true), ERROR);
            ErrorHandler.showError(e, null, "Error loading file!", ERROR);
        }
    }

    /**
     * *
     * Takes in a file and loads it as a datapack based on its extension
     *
     * @param selFile
     * @throws Exception
     */
    public void loadDatapackFile(File selFile) throws Exception {
    	//System.out.println("\n" + selFile.getAbsolutePath());
        // Zsika: Check for optional settings:
        setFlagsOptions(selFile);


        // check and set the depth file

        if (TSCreator.depthFile == null) {
            checkForDepthFile(selFile);
        }
        doUIChanges(LOADING, true, false);

        String path = selFile.getAbsolutePath();
        String extension = FileUtils.getExtension(selFile.getName());
        File extractDir = null;
        DataColumn.FileInfo fileInfo = new DataColumn.FileInfo();

        int dataLoadSuccess = 0;

        try {
            if (extension.compareToIgnoreCase("zip") == 0) {
                // temporarily extract the datafile from the zip and continue loading
                ZipFile zipFile = new ZipFile(path);
                // extract the datafile into a temporary directory and read it from there.
                extractDir = File.createTempFile(selFile.getName(), null);
                extractDir.delete();
                if (extractDir == null || !extractDir.mkdir()) {
                    throw new Exception("Unable to extract zip file to temporary directory.");
                }

                extractDir.deleteOnExit();
                if (!extractZip(zipFile, extractDir, true)) {
                    throw new Exception("Unable to extract zip file to temporary directory.");
                }


                List<File> listOfFiles = Arrays.asList(extractDir.listFiles());

                HashMap<String, File> sortedList = new HashMap<String, File>();


                for (int i = 0; i < listOfFiles.size(); i++) {
                    File currentFile = listOfFiles.get(i);
                    String ext = FileUtils.getExtension(currentFile.getName());

                    if (ext != null) {
                        sortedList.put(currentFile.getName(), currentFile);
                    }
                }

                boolean hasMetapack = false;
                if (sortedList.containsKey("Metapack.txt")) {
                    File metapack = sortedList.get("Metapack.txt");
                    if (TSCreator.isDatapackInfoFile(metapack)) {
                        BufferedReader br;
                        try {
                            br = new BufferedReader(new FileReader(metapack));
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.equalsIgnoreCase("DATAPACK-INFO")) {
                                } else if (sortedList.containsKey(line)) {
                                    File currentFile = sortedList.get(line);
                                    String ext = FileUtils.getExtension(currentFile.getName());

                                    if (ext == null) {
                                        // directory, do nothing
                                    } else if (ext.equalsIgnoreCase("txt") && !MapInformation.isMapFile(currentFile) && !TSCreator.isDatapackInfoFile(currentFile)) {
                                        loadDatapackFile(currentFile);
                                    } else if (ext.equalsIgnoreCase("zip")) {
                                        loadDatapackFile(currentFile);
                                    }
                                }
                            }
                            hasMetapack = true;
                            br.close();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(tscFrame, "Error Parsing Metapack", "Parsing Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                if (!hasMetapack) {
                    for (int i = 0; i < listOfFiles.size(); i++) {
                        File currentFile = listOfFiles.get(i);
                        String ext = FileUtils.getExtension(currentFile.getName());

                        if (ext == null) {
                            // directory, do nothing
                        } else if (ext.equalsIgnoreCase("txt") && !MapInformation.isMapFile(currentFile) && !TSCreator.isDatapackInfoFile(currentFile)) {
                            loadDatapackFile(currentFile);
                        } else if (ext.equalsIgnoreCase("zip")) {
                            loadDatapackFile(currentFile);
                        }
                    }
                }
            } else if (extension.equalsIgnoreCase("map")) {
                try {
                    doUIChanges(LOADING, true, false);

                    // parse the map pack and the map packs located within it
                    if (!parseMapPack(selFile)) {
                        logAndShow(Language.translate("No map data loaded.  Please input a proper .map file with data.", true), WARNING);
                        doUIChanges(LOADING, false, false);
                        return;
                    }

					/*
                     * } catch (Exception e) { // announce and log failure
					 * showInLabel(Language.translate("Error loading data
					 * file!", ERROR); ErrorHandler.showError(e, null, "Error
					 * loading file!", ERROR); }
					 */

                    settings.mapDataAdded();

                    // announce success
                    logAndShow(Language.translate("Map Data loaded successfully!", true), SUCCESS);

                    loadSuccessful(true);
                } catch (Exception e) {
                    // announce and log failure
                    showInLabel(Language.translate("Error loading map file!", true), ERROR);
                    ErrorHandler.showError(e, null, "Error map file!", ERROR);
                }
                doUIChanges(LOADING, false, false);
            } else {
                fileInfo.workingDir = selFile.getParentFile().toString();
                fileInfo.baseURL = new URL("file:" + selFile.getParentFile().getAbsolutePath());
                fileInfo.loadPath = path;
                fileInfo.resource = false;
                fileInfo.ensureTrailingSlashes();
                if (extension.compareToIgnoreCase("dat") == 0) {
                    // RASC
                    fileInfo.encrypted = false;
                    db.loadRASC(fileInfo);
                } else if (extension.equalsIgnoreCase("svg")) {
                    new TemplateLoader(tscFrame, selFile.getAbsolutePath(), db);
                } 
                else if (extension.equalsIgnoreCase("json")) {
                    db.loadJSON(fileInfo);
                } 
                else if (extension.equalsIgnoreCase("nwk") || extension.equalsIgnoreCase("newick") || extension.equalsIgnoreCase("tre")) {
                    db.loadNewick(fileInfo);
                } 
                else if (extension.equalsIgnoreCase("nex") || extension.equalsIgnoreCase("nexus") ) {
                    db.loadNexus(fileInfo);
                } 
                else if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("dpk") || extension.equalsIgnoreCase("mdpk")) {
                    if (!loadEncrypted(fileInfo, false)) {
                        throw new Exception("Nothing Loaded.");
                    }
                }
            }

            // set up the FontManagers for the whole image
            db.rootColumn.setParentFontManager(settings.fonts);

            // notify the settings that things have changed
            settings.dataAdded();

            // announce success
            logAndShow(selFile.getName() + ":: Data loaded successfully!", SUCCESS);

            loadSuccessful(true);

        } catch (Exception e) {
            logAndShow(selFile.getName() + ":: Unable to load datafile!", ERROR);
            e.printStackTrace();
            if (e.getMessage().equals("Public Limit Reached")) {
                showGetProPopup();
            } else {
                String errorMsgList = "";
                if (db.errorList.size() > 0) {
                    Iterator iter = db.errorList.iterator();
                    while (iter.hasNext()) {
                        errorMsgList += "\n" + (String) iter.next();
                    }
                    errorMsgList += "\n";
                    ErrorHandler.showError(errorMsgList + e.getLocalizedMessage(), "Error loading file!", ERROR);
                } else {
                    ErrorHandler.showError(e.getLocalizedMessage(), "Error loading data file!", ERROR);//shows full error screen, Nicki added this line 2-2-11
                }
            }
        }
        doUIChanges(LOADING, false, false);
    }

    private void checkForDepthFile(File file) {
        try {
            String extension = FileUtils.getExtension(file.getName());
            if (extension.equalsIgnoreCase("txt")) {
                CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), '\t');
                String[] value;
                depthFile = null;
                int i = 0;
                while ((value = reader.readNext()) != null && i < 10) {
//					Nag :: Check for age units in first 10 lines if not found the assume the default for age units
                    if (value[0].equalsIgnoreCase("age units:") && !(value[1].equalsIgnoreCase("ma"))) {
                        depthFile = file;
                        depthFileUnits = value[1];
                        break;
                    }
                    i++;
                }
            }

        } catch (Exception e) {
            Debug.print(e.getMessage());
        }
    }


    private void showGetProPopup() {
        if (getPRO == null) {
            getPRO = new JFrame("Get A Pro Version.");
            SpringLayout layout = new SpringLayout();
            getPRO.setLayout(layout);
            getPRO.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            getPRO.setSize(400, 220);

            getPROMessage = new JTextArea("We're sorry, but this version does not support three or more non-encrypted external files that exceed 3000 characters each.  Please contact us about getting a Pro version via the \"contact us\" at www.tscreator.org. Thank you.");
            getPROMessage.setEditable(false);
            getPROMessage.setWrapStyleWord(true);
            getPROMessage.setLineWrap(true);
            getPROMessage.setSize(new Dimension(380, 200));

            getPROButton = new JButton("OK");
            getPROButton.setSize(new Dimension(50, 20));

            getPROButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object item = e.getSource();
                    if (item == getPROButton) {
                        getPRO.setVisible(false);
                    }
                }
            });

            getPRO.add(getPROMessage);
            getPRO.add(getPROButton);
            layout.putConstraint(SpringLayout.WEST, getPROMessage, 10, SpringLayout.WEST, getPRO);
            layout.putConstraint(SpringLayout.NORTH, getPROMessage, 10, SpringLayout.NORTH, getPRO);
            layout.putConstraint(SpringLayout.WEST, getPROButton, 170, SpringLayout.WEST, getPROMessage);
            layout.putConstraint(SpringLayout.NORTH, getPROButton, 30, SpringLayout.SOUTH, getPROMessage);

            //set Location
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

            // Determine the new location of the window
            int w = getPRO.getSize().width;
            int h = getPRO.getSize().height;
            int x = (dim.width - w) / 2;
            int y = (dim.height - h) / 2;
            getPRO.setLocation(x, y);

            getPRO.setVisible(true);
        } else {
            getPRO.setVisible(true);
        }

    }

    /**
     * Extracts a map pack to a temporary directory and loads all of the .csv
     * files as a map pack as well as loads any map packs located in it
     *
     * @param mapPack
     * @return
     */
    private boolean parseMapPack(File mapPack) throws Exception {
        try {
            ZipFile zipFile = new ZipFile(mapPack);
            File tempDir = null;
			/*
			 We use a vector to hold the maps to add because each map pack
			 could contain multiple maps.  If some maps parse correctly,
			 and some incorrectly, we don't want to add just some maps
			 because the user thinks he/she is adding a map pack which should
			 include all of the submaps, not just a few maps.
			
			 So we create a temp list of maps to add and after everything
			 finishes without error for the map pack, then we add the
			 temp list to the db.
			 */
            Vector<MapInformation> mapsToAdd = new Vector<MapInformation>();

            // We also have a list of datapacks to add because we want to
            // only add the list of datapacks after we successfully load the maps
            // so that we can "roll back" bad mappacks easily
            Vector<File> datapacksToAdd = new Vector<File>();
            Vector<File> mappacksToAdd = new Vector<File>();

            // create a temporary directory to extract the mappack
            tempDir = File.createTempFile(mapPack.getName(), null);
            tempDir.delete();
            if (tempDir == null || !tempDir.mkdir()) {
                throw new Exception("Unable to extract zip file to temporary directory.");
            }

            tempDir.deleteOnExit();
            if (!extractZip(zipFile, tempDir, true)) {
                throw new Exception("Unable to extract zip file to temporary directory.");
            }

            // loop through files in temp directory and
            // handle them based on their extension
            File[] listOfFiles = tempDir.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                File currentFile = listOfFiles[i];
                String extension = FileUtils.getExtension(currentFile.getName());

                if (extension == null) {
                    // directory, do nothing
                } else if (extension.equalsIgnoreCase("txt")
                        && MapInformation.isMapFile(currentFile)) {
                    // load map information
                    MapInformation map = MapInformation.MapParseFromFile(currentFile.getAbsolutePath());
                    mapsToAdd.add(map);
                } else if (extension.equalsIgnoreCase("map")) {
                    // load this map pack
                    mappacksToAdd.add(currentFile);
                } else if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("dpk") || extension.equalsIgnoreCase("zip")) {
                    datapacksToAdd.add(currentFile);
                }
            }

            // assuming no errors so far, we should be able to parse the
            // datapacks since we know the mappacks have already been parsed successfully
            Iterator it = datapacksToAdd.iterator();
            while (it.hasNext()) {
                File f = (File) it.next();
                loadDatapackFile(f);
            }

            // Add Mappacks form the list
            it = mappacksToAdd.iterator();
            while (it.hasNext()) {
                File f = (File) it.next();
                parseMapPack(f);
            }

            // no errors this far means that the datapacks added successfully
            // so we can add the maps to the mapslist
            it = mapsToAdd.iterator();
            while (it.hasNext()) {
                MapInformation mi = (MapInformation) it.next();
                db.addMap(mi.getMapName(), mi);
            }

            if (mapsToAdd.isEmpty() && datapacksToAdd.isEmpty()) {
                return false;
            }


            // no errors
            return true;
        } catch (Exception e) {
            throw e;
        }
    }
    
    String whitespace_chars =  ""       /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            ;        
    /* A \s that actually works for Java’s native character set: Unicode */
    String     whitespace_charclass = "["  + whitespace_chars + "]";
	private boolean encryptedDatapack = false;    
    
    public int checkevTreeColumnExistsAndCalculateCharNums(InputStream input, DataColumn.FileInfo fileInfo) throws ParseException {
        String enc = null;
        UnicodeInputStream uin = new UnicodeInputStream(input, enc);
        enc = uin.getEncoding(); // check for BOM mark and skip bytes
        //Debug.print("Encoding = " + enc);
        if (enc == null) {
            //Debug.print("Encoding is null, defaulting to winows-1252 or macroman as tab delimited file in microsoft excel has these encodings.");
        	if (System.getProperty("os.name").contains("Mac OS X"))
        		enc = "macroman";
        	else
        		enc = "windows-1252";
//            enc = "UTF-8";
        } // This was the only way to support foreign chars in Windows
        
        int ln, ci, nln;
        HashMap<Integer, String> evTreeColumnMap = new HashMap<Integer, String> ();
        HashMap<Integer, Integer> evTreeColumnMapStart = new HashMap<Integer, Integer> ();
        HashMap<Integer, Integer> evTreeColumnMapEnd = new HashMap<Integer, Integer> ();
        HashMap<Integer, Integer> evTreeColumnCharCount = new HashMap<Integer, Integer> ();
		int evTreeTotalCharCount = -1;

        try { 
        	SimpleCharStream stream = new SimpleCharStream(uin, enc, 1, 1); 
        	String s = stream.toString();
        	
        	String dString = ""; 
        	StringBuilder line = new StringBuilder(); 
      
        	ln = 0; //line number 
        	ci = 0; //character number
        	nln = 0; // new line number
        	char c;
        	
        	
        	String[] matchColumnChars = {
        			"\trange\t"			, "\trange-overlay\t"		, "\trange-underlay\t"	  	, "\trange-only\t"		,

        			"\tchron\t"			, "\tchron-overlay\t"		, "\tchron-underlay"		, "\tchron-only\t"		,
        			"\tfacies\t"		, "\tfacies-overlay\t"		, "\tfacies-underlay\t"		, "\tfacies-only\t"		,
        			"\tblank\t"			, "\tblank-overlay\t"		, "\tblank-underlay\t"		, "\tblank-only\t"		,
        			"\tblock\t"			, "\tblock-overlay\t"		, "\tblock-underlay\t"		, "\tblock-only\t"      ,
        			"\tevent\t"			, "\tevent-overlay\t"		, "\tevent-underlay\t"		, "\tevent-only\t"      ,
        			"\tpoint\t"			, "\tpoint-overlay\t"		, "\tpoint-underlay\t"   	, "\tpoint-only\t"		,
        			"\tsequence\t"		, "\tsequence-overlay\t"	, "\tsequence-underlay\t" 	, "\tsequence-only\t"	,
        			"\ttrend\t"			, "\ttrend-overlay\t"	    , "\ttrend-underlay\t"		, "\ttrend-only\t"		,
        			"\taverage\t"		, "\taverage-overlay\t"		, "\taverage-underlay\t"	, "\taverage-only\t"	,
        			"\ttransect\t"		, "\ttransect-overlay\t"	, "\ttransect-underlay\t"	, "\ttransect-only\t"	,
        			"\tfreehand\t"		, "\tfreehand-underlay\t"	, "\tfreehand-overlay\t"    , "\tfreehand-only\t"	,
        			"\t:\t"
        	};
        	int evTreeColumnID = 0;
        	int evTreeCharCount = 0;
			boolean evTreeColumnStartRow = false;
			boolean evTreeColumnStart = false;

			try {
				c = stream.readChar();
				ci++;
	        	do {
	        		line.append(c);
	        		
	        		if (evTreeColumnStartRow == false && 
	        				(	line.toString().contains(matchColumnChars[0]) == true || 
	        					line.toString().contains(matchColumnChars[1]) == true || 
	        					line.toString().contains(matchColumnChars[2]) == true || 
	        					line.toString().contains(matchColumnChars[3]) == true 
	        				)
	        			) {
	        			evTreeColumnStartRow = true; // tracks just the first line of the evtree column
	        			evTreeColumnStart = true;    // tracks all the rows of evtree column
	        			evTreeColumnExists  = true;  // 
	        			
	        			// find the starting line of the tree column 
	        			evTreeColumnMap.put(evTreeColumnID, line.toString());
	        			evTreeColumnMapStart.put(evTreeColumnID, ln);
	        		}
	        		
	        		if(evTreeColumnStart == true) {
	        			evTreeCharCount++;
	        		}
	        		
	        		// when the current line ends
	        		if (c == '\n' || c == '\r') {
	        			// only when currently parsing a evTreeColumn
	        			if(evTreeColumnStart == true) {
	        				// find the ending line of the tree column
	        				for(int ix=4; ix<matchColumnChars.length; ix++) {
	        					String oc = matchColumnChars[ix];
	        					if (line.toString().contains(oc) == true) {
	        						evTreeColumnMapEnd.put(evTreeColumnID, ln-1);

	        						// remove the current line characters
	        						evTreeCharCount -= line.length() - 1;
	        						evTreeColumnCharCount.put(evTreeColumnID, evTreeCharCount);
	        						evTreeTotalCharCount += evTreeCharCount;

	        						// resetting character count
	        						evTreeCharCount = 0;
	        						evTreeColumnStart = false;
	        						evTreeColumnID++;
	        						break;
	        					}
	        				}
	        			}

	        			if(evTreeColumnStartRow == true) {
	        				evTreeColumnStartRow = false;
	        			}
	        			
	        			dString += new String(line);
	        			ln++;

	        			String newLine = line.toString().replaceAll(whitespace_charclass,"");
	        			if (newLine.length() == 0) {
	        				nln++;
	        			}
	        			
	        			line = new StringBuilder();
	        		}
	        		c = stream.readChar();
	        		ci++;
	        	} while(true);
			} catch (IOException e) {
				// checking whether the exception happened while parsing the tree column
				if(evTreeColumnStart == true) {
    				evTreeColumnMapEnd.put(evTreeColumnID, ln-1);

    				// remove the current line characters
    				evTreeColumnCharCount.put(evTreeColumnID, evTreeCharCount);
    				evTreeTotalCharCount += evTreeCharCount;

    				// resetting character count
    				evTreeCharCount = 0;
    				evTreeColumnStart = false;
    				evTreeColumnID++;
    			}
				//e.printStackTrace();
				System.out.println("Reached end of datapack while calculating the character numbers for evolutionary tree columns.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			numCharsInDatapack = ci - nln;
			numLinesInDatapack = ln - nln;
	        System.out.println("Number of characters in datapack = "+ numCharsInDatapack);
	        System.out.println("Number of lines in datapack = "+ numLinesInDatapack);
        } catch(java.io.UnsupportedEncodingException e) { 
        	throw new RuntimeException(e); 
        } catch(Exception e) {
        	throw new RuntimeException(e);
        }
        
        return evTreeTotalCharCount;
    }
    
    private int getEVTreeColumnCharCount(DataColumn.FileInfo fileInfo) throws Exception, ParseException {
        InputStream in = FileUtils.getInputStream(fileInfo.loadPath, fileInfo.resource);
        int evTreeTotalCharCount = -1;
        try {
        	evTreeTotalCharCount = checkevTreeColumnExistsAndCalculateCharNums(in, fileInfo);
        } catch (ParseException e) {
            in.close();
            throw e;
        }

        if (in != null) {
            in.close();
        }
        
        return evTreeTotalCharCount;
        
    }

    protected boolean loadEncrypted(DataColumn.FileInfo fileInfo, boolean defaultPack) throws Exception {

        boolean violatedLimitPublic = false;
        ParseException getMsg = null;
        // try loading it as an unencrypted file.
        try {
            fileInfo.encrypted = false;
            if (!ProgramInfo.isPublic() || defaultPack) {
            	if (db != null) {
            		db.load(fileInfo);
            		return true;
            	} else {
            		return false;
            	}
            } else 
            {
            	// check whether evolutionary tree datapack exists or not, if exists then count
            	//characters without it
            	String extension = FileUtils.getExtension(fileInfo.loadPath);
            	
            	int count = 0;
            	int evTreeColumnCharCount = 0;
            	
            	InputStream in = FileUtils.getInputStream(fileInfo.loadPath, fileInfo.resource);   
                encryptedDatapack = !DatafileCrypto.isDecryptedFile(in);
                in.close();
                
            	if (!defaultPack && extension.equalsIgnoreCase("txt") && encryptedDatapack == false) {
            		evTreeColumnCharCount = getEVTreeColumnCharCount(fileInfo);
            	}
            	
            	if (!defaultPack && extension.equalsIgnoreCase("txt") && encryptedDatapack == false) {
            		count = numCharsInFile(fileInfo.loadPath);
            		count -= evTreeColumnCharCount;
            	} 
            	
            	
                // resetting variables
                numCharsInDatapack = -1;
                numLinesInDatapack = -1;
                evTreeColumnExists = false;
                encryptedDatapack = false;
                
                if (defaultPack || count <= NUMBER_CHARS_LIMIT_PUBLIC) {
                    if (numDatapacksAdded < NUMBER_DATAPACKS_LIMIT_PUBLIC) {
                        db.load(fileInfo);
                        numDatapacksAdded++;

                        return true;
                    } else {
                        violatedLimitPublic = true;
                    }
                } else {
                    violatedLimitPublic = true;
                    logAndShow(Language.translate("Cannot load unencrpted large files in Public Version", true), ERROR);
                    throw new Exception("Public Limit Reached");
                }
            }

        } catch (ParseException e) {
            getMsg = e;
        }

        // nope, didn't load. See if it's an encrypted file

        // read in the entire file so that the rest of the operations can be done without weird stream operations
        InputStream in = FileUtils.getInputStream(fileInfo.loadPath, fileInfo.resource);
        if (in.available() <= 0) {
            throw new Exception("invalid file");
        }

        byte[] encFileContents = new byte[in.available()];
        if (encFileContents == null) {
            throw new Exception("Not enough memory to decrypt file!");
        }

        int read = in.read(encFileContents, 0, encFileContents.length);
        int totalRead = read;
        try {
            while (totalRead < encFileContents.length && in.available() > 0) {
                read = in.read(encFileContents, totalRead, encFileContents.length - totalRead);
                if (read == -1) {
                    break;
                }
                totalRead += read;
            }
        } catch (Exception ioe) {
            throw new Exception("IO error: " + ioe.getMessage());
        }

        if (totalRead != encFileContents.length) {
            throw new Exception("Entire datafile couldn't be read. Read " + totalRead + " of " + encFileContents.length);
        }

        in.close();

        in = new ByteArrayInputStream(encFileContents);

        DatafileCrypto.HeaderInfo hi = DatafileCrypto.readEncryptedHeader(in);
        if (hi == null) {
            if (!ProgramInfo.isPublic()) {
                throw getMsg; //error in Datapack
            } else {
                if (violatedLimitPublic) {
                    logAndShow(Language.translate("Cannot load unencrpted large files in Public Version", true), ERROR);
                    throw new Exception("Public Limit Reached");
                } else {
                    throw getMsg; //error in Datapack
                }
            }
        }

        // find the end of the encrypted header (two newlines in a row)
        boolean found = false;
        int contentStart = 0;
        for (int i = 3; i < encFileContents.length && i < 5000; i++) {
            if (encFileContents[i - 3] == 13
                    && encFileContents[i - 2] == 10
                    && encFileContents[i - 1] == 13
                    && encFileContents[i] == 10) {
                found = true;
                in.close();
                contentStart = i + 1;
                break;
            }
        }
        if (!found) {
            throw new Exception("unknown file");
        }

        // verify the integrity of the file by making sure the hash in the header matches
        // the hash of the encrypted section
        MessageDigest md = DatafileCrypto.getIntegrityHashFunction();
        md.update(encFileContents, contentStart, encFileContents.length - contentStart);
        byte[] encryptedHash = md.digest();

        if (hi.integrityHash == null || !DatafileCrypto.verifyHash(hi.integrityHash, encryptedHash)) {
            throw new Exception("The integrity of the datafile could not be verified.\r\nheader:    " + DatafileCrypto.byteToHexString(hi.integrityHash) + "\r\ncontents: " + DatafileCrypto.byteToHexString(encryptedHash));
        }

        in = new ByteArrayInputStream(encFileContents, contentStart, encFileContents.length);

		/*
		 * @Gangi: To avoid displaying the popup frame, assume the datapacks are
		 * not password protected // ok, it's an encrypted file. Ask for a
		 * password. DecryptionOptions opt = new DecryptionOptions(hi, true,
		 * allowPasswordCancel); if (!opt.isOK()) { return false; // user
		 * cancelled }
		 *
		 */
        Cipher cipher = DatafileCrypto.getCipher(null, hi, false);
        InputStream cryptIn = new CipherInputStream(in, cipher);

        while (true) {
            // make sure the file decrypted correctly
            if (DatafileCrypto.isDecryptedFile(cryptIn)) {
                // it did
                //cryptIn.close(); Quick fix for padding issue on Java 8.(Changes to CipherInputStream causes BadPaddingException in stable code. https://bugs.openjdk.java.net/browse/JDK-8061619)
            	cryptIn = null;
                in.close();
                cipher = DatafileCrypto.getCipher(null, hi, false);

                // decrypt the entire file
                byte[] decryptedContents = cipher.doFinal(encFileContents, contentStart, encFileContents.length - contentStart);
                if (decryptedContents == null) {
                    throw new Exception("Not enough memory to decrypt file!");
                }

                in = new ByteArrayInputStream(decryptedContents);

                fileInfo.encrypted = true;
                db.load(in, fileInfo);
                return true; //successfully loaded
            }

			/*
			 * @Gangi: To avoid asking for password. // the decryption failed,
			 * ask for the password again opt = new DecryptionOptions(hi, false,
			 * allowPasswordCancel); if (!opt.isOK()) { return false; // user
			 * cancelled }
			 *
			 */
            //cryptIn.close(); // Quick fix for padding issue on Java 8.(Changes to CipherInputStream causes BadPaddingException in stable code. https://bugs.openjdk.java.net/browse/JDK-8061619)
            cryptIn = null;
            in.close();
            in = new ByteArrayInputStream(encFileContents, contentStart, encFileContents.length);
            cipher = DatafileCrypto.getCipher(null, hi, false);
            cryptIn = new CipherInputStream(in, cipher);
            //Loading decrypted file
            if (FileUtils.getExtension(fileInfo.loadPath).compareToIgnoreCase("dpk") == 0) {
                byte[] decryptedContents = cipher.doFinal(encFileContents, contentStart, encFileContents.length - contentStart);
                if (decryptedContents == null) {
                    throw new Exception("Not enough memory to decrypt file!");
                }

                in = new ByteArrayInputStream(decryptedContents);
                // read in the entire input stream
                byte[] b = new byte[in.available()];

                int len = in.read(b, 0, b.length);
                File temp = File.createTempFile("tempDataTSC", ".zip");
                FileOutputStream fos = new FileOutputStream(temp);
                while (len != -1) {
                    fos.write(b, 0, len);
                    len = in.read(b);
                }
                fos.close();
                loadDatapackFile(temp);
                return true;
            } else if (FileUtils.getExtension(fileInfo.loadPath).compareToIgnoreCase("mdpk") == 0) {
                byte[] decryptedContents = cipher.doFinal(encFileContents, contentStart, encFileContents.length - contentStart);
                if (decryptedContents == null) {
                    throw new Exception("Not enough memory to decrypt file!");
                }

                in = new ByteArrayInputStream(decryptedContents);
                // read in the entire input stream
                byte[] b = new byte[in.available()];

                int len = in.read(b, 0, b.length);
                File temp = File.createTempFile("tempMapTSC", ".map");
                FileOutputStream fos = new FileOutputStream(temp);
                while (len != -1) {
                    fos.write(b, 0, len);
                    len = in.read(b);
                }
                fos.close();

                try {
                    doUIChanges(LOADING, true, false);

                    // parse the map pack and the map packs located within it
                    if (!parseMapPack(temp)) {
                        logAndShow(Language.translate("No map data loaded.  Please input a proper .map file with data.", true), WARNING);
                        doUIChanges(LOADING, false, false);
                        return false;
                    }

                    settings.mapDataAdded();

                    // announce success
                    logAndShow(Language.translate("Map Data loaded successfully!", true), SUCCESS);

                    loadSuccessful(true);
                } catch (Exception e) {
                    // announce and log failure
                    showInLabel(Language.translate("Error loading map file!", true), ERROR);
                    ErrorHandler.showError(e, null, "Error map file!", ERROR);
                }
                doUIChanges(LOADING, false, false);
                return true;
            }

        }
    }

    private int numCharsInFile(String filename) throws IOException {
        InputStream in;
        byte[] encFileContents;
        int numChars = 0;
        try {
            in = FileUtils.getInputStream(filename, true);

            encFileContents = new byte[in.available()];
            if (encFileContents == null) {
                throw new Exception("Not enough memory to decrypt file!");
            }
            numChars = encFileContents.length;
        } catch (Exception e) {
        }

        return numChars;
    }

    public void encryptDatafile() {
        JFileChooser chooser = new JFileChooser();

        ExtensionFileFilter eff = new ExtensionFileFilter();
        eff.setDescription("TSCreator files with extension (*.txt, *.zip, *.map)");
        eff.addExtension("txt", true);
        eff.addExtension("zip", true);
        eff.addExtension("map", true);
        chooser.setFileFilter(eff);
        if (fileChooserPath != null) {
            chooser.setCurrentDirectory(fileChooserPath);
        }

        int returnVal = chooser.showOpenDialog(tscFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileChooserPath = chooser.getCurrentDirectory();

            // make sure this is a valid datafile
            try {
                String inPath = chooser.getSelectedFile().getAbsolutePath();
                FileInputStream fis = new FileInputStream(inPath);

                if (FileUtils.getExtension(inPath).equalsIgnoreCase("txt")) {
                    if (!DatafileCrypto.isDecryptedFile(fis)) {
                        ErrorHandler.messageBox("The selected file does not appear to be a TSCreator datafile.", "Cannot protect file", ERROR);
                        return;
                    }
                }

                fis.close(); // it will be reopenned later

				/*
				 * @Gangi: It is not password protected any more. // pop up a
				 * dialog asking for password EncryptionOptions opt = new
				 * EncryptionOptions(); if (!opt.isOK()) { return; // user
				 * cancelled }
				 */

                DatafileCrypto.HeaderInfo hi = new DatafileCrypto.HeaderInfo();
                hi.message = null;

                // Let the user select the output file
                returnVal = chooser.showSaveDialog(tscFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fis = new FileInputStream(inPath);
                    FileOutputStream fos;
                    if (FileUtils.getExtension(inPath).equalsIgnoreCase("txt")) {
                        String outPath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), "txt");
                        fos = new FileOutputStream(outPath);
                    } else if (FileUtils.getExtension(inPath).equalsIgnoreCase("map")) {
                        String outPath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), "mdpk");
                        fos = new FileOutputStream(outPath);
                    } else {
                        String outPath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), "dpk");
                        fos = new FileOutputStream(outPath);
                    }
                    String error;
                    if ((error = DatafileCrypto.encrypt(fos, fis, null, hi)) == null) {
                        logAndShow(Language.translate("Protected file created successfully!", true), SUCCESS);
                    } else {
                        logAndShow(Language.translate("Error while protecting file: ", true) + error, ERROR);
                    }
                    fis.close();
                    fos.close();
                }

            } catch (FileNotFoundException e) {
                logAndShow(Language.translate("File not found!", true), ERROR);
                ErrorHandler.showError(e, null, "File not found!", ERROR);
            } catch (IOException e) {
                logAndShow(Language.translate("IO Error!", true), ERROR);
                ErrorHandler.showError(e, null, "I/O Error!", ERROR);
            }
        }
    }

    public void showSettings() {
        if (!settings.isPopulated) {
            settings.populate(db);
        }
        settings.setVisible(true);

    }
    
    public void clearSettings() {
        if (settings != null) {
            settings.setVisible(false);
            settings = null;
        }
    }

    public void clearData() {
        if (settings != null) {
            settings.setVisible(false);
            settings = null;
        }
        if (editor != null) {
            editor.setVisible(false);
            editor = null;
        }
        if (crossplotFrame != null) {
            CrossplotFrame.crossplot.clearAllPoints();
            crossplotFrame.setVisible(false);
            crossplotFrame = null;
        }
        ig = null;
        columnImageGenerator = null;
        db = null;
    }

    public void generateImage() {
        if (!settings.isValidToGenerate()) {
            if (autoGenOptions == null) {
                showSettings();
            }
            System.out.println("Settings is not valid to generate chart.");
            return;
        }

        ImageGenerator derp = new ImageGenerator(db.rootColumn, settings, db.patMan);
    	Iterator<DataColumn> subRootIter = derp.rootCol.getSubColumns();
        while (subRootIter.hasNext()) {
           DataColumn dc = subRootIter.next();

       	   Settings settingsBackup = settings.getReadOnlySettings(dc.unit);

       	   if(dc.isOutcrop){
       		   settings.setTopAge(dc.unit, Double.toString(-settingsBackup.topAge));
	           settings.setBaseAge(dc.unit, Double.toString(-settingsBackup.baseAge));
	           settings.timesChanged(TSCreator.settings.TIME_INTERVAL_CHANGED, dc.unit);
       	   }
        }

        doUIChanges(GENERATING, true, false);

        // switch to the svg view from the html view
        if (mainView != null) {
            mainViewLayout.show(mainView, "displayCanvas");
        }

        logAndShow(Language.translate("Generating Image...", true), NORMAL);
        try {
        	if (!TSCreator.NODE_MODE) {
        		this.displayCanvasPane.retainZoom(true);
        	}
        	if (displayCanvas != null) {
                displayCanvas.setDocument(null);
            }
            ig = new ImageGenerator(db.rootColumn, settings, db.patMan);
            columnImageGenerator = new ColumnImageGenerator(db.rootColumn, settings, db.patMan);
            svgImage = ig.drawImage();
        } catch (ImageGenerator.NoColumnsToDrawException e) {
            showInLabel(Language.translate("Error! No columns selected", true), ERROR);
            ErrorHandler.showError(null, e.getMessage(), "Error! No columns selected", ErrorHandler.ERROR);
            bigError(false);
            doUIChanges(GENERATING, false, true);
            ig = columnImageGenerator = null;
            return;
        } catch (Exception e) {
            showInLabel(Language.translate("Internal error while generating!", true), ERROR);
            ErrorHandler.showError(e, null, "Internal error while generating!", ErrorHandler.ERROR);
            bigError(false);
            doUIChanges(GENERATING, false, true);
            ig = columnImageGenerator = null;
            return;
        } catch (OutOfMemoryError oeme) {
            logAndShow(Language.translate("Out of Memory!", true), ERROR);
            bigError(false);
            doUIChanges(GENERATING, false, true);
            ig = columnImageGenerator = null;
            return;
        }
        try {
            if (displayCanvas != null) {
                logAndShow(Language.translate("Displaying Image...", true), SUCCESS);
                displayCanvas.setDocument(svgImage);
                logAndShow(Language.translate("Image generated! (please wait for it to appear)", true), SUCCESS);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        } catch (OutOfMemoryError oeme) {
            logAndShow(Language.translate("Out of Memory.", true), ERROR);
            bigError(true);
        }
        doUIChanges(GENERATING, false, false);
        //ig = columnImageGenerator = null;

        subRootIter = derp.rootCol.getSubColumns();
        while (subRootIter.hasNext()) {
            DataColumn dc = subRootIter.next();

            Settings settingsBackup = settings.getReadOnlySettings(dc.unit);

            if(dc.isOutcrop){
        	   settings.setTopAge(dc.unit, Double.toString(-settingsBackup.topAge));
 	           settings.setBaseAge(dc.unit, Double.toString(-settingsBackup.baseAge));
 	           settings.timesChanged(TSCreator.settings.TIME_INTERVAL_CHANGED, dc.unit);
        	}
         }
    }

    public void doUIChanges(int action, boolean gen, boolean error) {
        settings.showGenerating(gen);

        loadReplaceWithBuiltInAction.setEnabled(!gen);
        loadAddWithBuiltInAction.setEnabled(!gen);
        loadAddFromFileAction.setEnabled(!gen);
        loadReplaceWithFileAction.setEnabled(!gen);

        generateAction.setEnabled(!gen);
        settingsAction.setEnabled(!gen);
        editDataAction.setEnabled(!gen);
        saveTemplateAction.setEnabled(!gen);
        loadTemplateAction.setEnabled(!gen);
        createCrossplotAction.setEnabled(!gen);

        switch (action) {
            case GENERATING:
                saveSVGAction.setEnabled(!gen && !error);
                saveRasterAction.setEnabled(!gen && !error);
                //saveRasterAction.setEnabled(true);
                savePDFAction.setEnabled(!gen && !error);

                imageSizeAction.setEnabled(!gen && !error);
                zoomInAction.setEnabled(!gen && !error);
                zoomOutAction.setEnabled(!gen && !error);
                actualSizeAction.setEnabled(!gen && !error);
                fitToWindowAction.setEnabled(!gen && !error);
                showLine.setEnabled(!gen && !error);
                break;
            case LOADING:
                saveSVGAction.setEnabled(false);
                saveRasterAction.setEnabled(false);
                //saveRasterAction.setEnabled(true);
                savePDFAction.setEnabled(false);

                imageSizeAction.setEnabled(false);
                zoomInAction.setEnabled(false);
                zoomOutAction.setEnabled(false);
                actualSizeAction.setEnabled(false);
                fitToWindowAction.setEnabled(false);
                showLine.setEnabled(false);
                break;
            case SAVING:
                saveSVGAction.setEnabled(!gen);
                saveRasterAction.setEnabled(!gen);
                //saveRasterAction.setEnabled(true);
                savePDFAction.setEnabled(!gen);
                break;
        }

    }

    public void loadSuccessful(boolean tf) {
        settingsAction.setEnabled(tf);
        generateAction.setEnabled(tf);
        synchronized (TSCreator.lock) {
            TSCreator.lock.notifyAll();
        }
    }

    public void addPatterns() throws Exception {
        JFileChooser chooser = new JFileChooser();

        ExtensionFileFilter eff = new ExtensionFileFilter();
        eff.setDescription("Patterns (*.svg)");
        eff.addExtension("svg", true);
        chooser.setFileFilter(eff);
        if (fileChooserPath != null) {
            chooser.setCurrentDirectory(fileChooserPath);
        }

        int returnVal = chooser.showOpenDialog(tscFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            ErrorHandler.log("Loading patterns from " + path);
            fileChooserPath = chooser.getCurrentDirectory();

            db.patMan.readPatternsFromSVG(path, false);
            logAndShow(Language.translate("Done reading patterns.", true), NORMAL);
        }
    }

    public void save(String format, RasterSave.RasterSaveOptions rasterOptions) {

        if (format.compareTo("SVG") != 0 && format.compareTo("PDF") != 0 && format.compareTo("PNG") != 0 && format.compareTo("JPG") != 0 && format.compareTo("RASTER") != 0) {
            return;
        }
        
        Element timelabel = ig.doc.getElementById("TimeLineLabel");
        Element timelabel_up = ig.doc.getElementById("TimeLineLabelUp");
        Element timelabel_down = ig.doc.getElementById("TimeLineLabelDown");

        //if (timeline != null && timelabel != null) {
        if (timelabel != null && !TIMELINE) {
           timelabel.setAttributeNS(null, "visibility", "hidden");
           timelabel_up.setAttributeNS(null, "visibility", "hidden");
           timelabel_down.setAttributeNS(null, "visibility", "hidden");
        }
         
        JFileChooser chooser = new JFileChooser();
        
        // raster files can be any supported raster image format
        ExtensionFileFilter effPNG = new ExtensionFileFilter();
        ExtensionFileFilter effJPG = new ExtensionFileFilter();
        if (format.compareTo("RASTER") == 0) {
            effPNG.setDescription("PNG file (*.png)");
            effPNG.addExtension("png", true);
            effJPG.setDescription("JPEG file (*.jpg)");
            effJPG.addExtension("jpg", true);
            effJPG.addExtension("jpeg", true);
            chooser.addChoosableFileFilter(effJPG);
            chooser.setFileFilter(effPNG);
        } else {
            ExtensionFileFilter eff = new ExtensionFileFilter();
            eff.setDescription(format + " file (*." + format.toLowerCase() + ")");
            eff.addExtension(format.toLowerCase(), true);
            chooser.setFileFilter(eff);
        }
        
        int returnVal = chooser.showSaveDialog(tscFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // get the format when one wasn't given
            if (format.compareTo("RASTER") == 0) {
                String ext = FileUtils.getExtension(chooser.getSelectedFile().getName());
                if (ext == null) {
                    ExtensionFileFilter eff = (ExtensionFileFilter) chooser.getFileFilter();
                    if (eff == effPNG) {
                        format = "PNG";
                    } else {
                        format = "JPG";
                    }
                } else {
                    if (ext.compareToIgnoreCase("PNG") == 0) {
                        format = "PNG";
                    } else {
                        format = "JPG";
                    }
                }
            }
            
            String absolutePath = FileUtils.appendExtension(chooser.getSelectedFile().getAbsolutePath(), format.toLowerCase());
            
            // Confirmation for overwrite if file already exists
            File f = new File(absolutePath);
            if(f.exists() && !f.isDirectory()){
                int reply = JOptionPane.showConfirmDialog(null, "A file with the same name already exists, are you sure you want to overwrite it?", "Overwrite", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION)
                {
                    save(format, rasterOptions);
                    return;
                }
            }
            
            logAndShow("Writing " + format + " to " + absolutePath + "...", NORMAL);
            
            try {
                //Better temp fix for saving an SVG before a PDF
                if (format.compareToIgnoreCase("PDF") == 0) {
                    saveToFile(FileUtils.appendExtension(absolutePath, "svg"), "SVG", rasterOptions);
                }
                saveToFile(absolutePath, format, rasterOptions);
            } catch (Exception e) {
                ErrorHandler.showError(e, null, "Error writing " + format, ERROR);
                showInLabel("", ERROR);
                return;
            } catch (OutOfMemoryError oome) {
                logAndShow(Language.translate("Error writing ", true) + format + Language.translate("! Out of memory!", true), ERROR);
                return;
            }
            logAndShow(format + Language.translate(" file successfully written.", true), SUCCESS);

        }
        
//        timelabel.setAttributeNS(null, "visibility", "visible");
//        timelabel_up.setAttributeNS(null, "visibility", "visible");
//        timelabel_down.setAttributeNS(null, "visibility", "visible");
        
    }

    public void saveToFile(String absolutePath, String format, RasterSave.RasterSaveOptions rasterOptions) throws Exception {

        if (format.compareToIgnoreCase("SVG") == 0) {
            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(absolutePath), "UTF-8");
            ig.write(fw);
            fw.close();
        } else if (format.compareToIgnoreCase("PDF") == 0) {
            OutputStream fw = new FileOutputStream(absolutePath);
            ig.writePDF(fw);
            fw.close();
        } else if (format.compareToIgnoreCase("PNG") == 0) {
            FileOutputStream fw = new FileOutputStream(absolutePath);
            ig.writePNG(fw, rasterOptions.width, rasterOptions.height);
            fw.close();
        } else if (format.compareToIgnoreCase("JPG") == 0 || format.compareToIgnoreCase("JPEG") == 0) {
            FileOutputStream fw = new FileOutputStream(absolutePath);
            ig.writeJPG(fw, rasterOptions.width, rasterOptions.height);
            fw.close();
        }
        
    }

    public static boolean extractZip(ZipFile zip, File dir, boolean deleteOnExit) {
        if (!dir.isDirectory()) {
            return false;
        }

        Enumeration entries = zip.entries();

        String slash = System.getProperty("file.separator");
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            // set up the filename
            String path = dir + slash + entry.getName();
            File f = new File(path);

            if (entry.isDirectory()) {
                f.mkdirs();
                if (deleteOnExit) {
                    f.deleteOnExit();
                }
                continue;
            }

            try {
                File fdir = f.getParentFile();
                if (!fdir.exists()) {
                    fdir.mkdirs();
                    if (deleteOnExit) {
                        fdir.deleteOnExit();
                    }
                }

                f.createNewFile();

                if (deleteOnExit) {
                    f.deleteOnExit();
                }

                FileOutputStream fos = new FileOutputStream(f);
                InputStream is = zip.getInputStream(entry);

                // copy the file from the zip to its final place
                byte[] b = new byte[10240];
                while (is.available() > 0) {
                    int read = is.read(b);

                    fos.write(b, 0, read);
                }
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    public void actionPerformed(ActionEvent evt) {
        Object e = evt.getSource();
      //  System.out.println("THIS IS THE SOURCE: e: " + e);
        String confirmMessage1 = "Are you sure to replace current datapack with default datapack?";
        String confirmMessage2 = "Are you sure to replace current datapack with new datapack?";
        String title = "Replace Confirmation";
        int reply;
        
        try {
        	if (e == null) {
            } else if (e == loadReplaceWithBuiltInAction) {
                reply = JOptionPane.showConfirmDialog(null, confirmMessage1,title, JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION) {
                    clearData(); // this will make the loader create a new db, thus clearing the current stuff
                    numDatapacksAdded = 0;
                    loadDefaultData();
                }
            } else if(e == loadAddWithBuiltInAction){
                //numDatapacksAdded += 1;
                // load the default data
                clearSettings();
                loadDefaultData();
                // loadDataFromFile(false,true);

            }else if (e == loadReplaceWithFileAction) {
                reply = JOptionPane.showConfirmDialog(null, confirmMessage2,title, JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION) {
                    numDatapacksAdded = 0;
                    System.gc();//Aditya added this line
                    loadDataFromFile(true, false);// As far as I understand this is the line of code which is used to replace
                    //The current datapack with new datapack.
                    //if the datapack is loaded should numDatapack be incremented: Adi ?
                }
            } else if (e == loadAddFromFileAction) {
                loadDataFromFile(false, false);
            	//numDatapacksAdded += 1;
            } else if (e == saveAsAction) {
                saveDataAs();
            } else if (e == saveAsJsonAction) {
                saveDataAsJson();
            } else if (e == clearAction) {
            } else if (e == viewPatternsAction) {
                new PatternViewer(db.patMan).setVisible(true);
            } else if (e == addPatternsAction) {
                addPatterns();
            } else if (e == showGTSversion) {
                GTSVersion v = new GTSVersion(tscFrame);
                v.setVisible(true);
            } else if (e == saveSVGAction) {
                //if (!(isProDemo && isProDemoTripped))
                if ((ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled())
                        || !ProgramInfo.isPublic()) {
                    save("SVG", null);
                }
            } else if (e == saveRasterAction) {
                //if (!(isProDemo && isProDemoTripped)) {
                if ((ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled())
                        || !ProgramInfo.isPublic()) {
                    RasterSave rs = new RasterSave(tscFrame, svgImage, displayCanvas.getCurrentZoomRatio(), this);
                    rs.setVisible(true);
                }
            } else if (e == savePDFAction) {
                //if (!(isProDemo && isProDemoTripped))
                if ((ProgramInfo.isPublic() && ProgramInfo.isSavingEnabled())
                        || !ProgramInfo.isPublic()) {
                    save("PDF", null);
                }
            } else if (e == encryptDataAction) {
                encryptDatafile();
            } else if (e == convertDataPackAction) { 
            	//Class.forName("org.sqlite.JDBC");
            	Converter conv = new Converter();
            	Converter.running = false;
            	conv.initGUI();
            } else if (e == editDataAction) {
                if (editor == null) {
                    editor = new DataEditor(db, this);
                }
                editor.setVisible(true);
            } else if (e == saveTemplateAction) {
                TemplateGen gen = new TemplateGen(tscFrame);
                gen.setVisible(true);
            } else if (e == loadTemplateAction) {
                JFileChooser chooser = new JFileChooser();

                ExtensionFileFilter eff = new ExtensionFileFilter();
                eff.setDescription("Patterns (*.svg)");
                eff.addExtension("svg", true);
                chooser.setFileFilter(eff);
                if (TSCreator.fileChooserPath != null) {
                    chooser.setCurrentDirectory(TSCreator.fileChooserPath);
                }

                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    TSCreator.fileChooserPath = chooser.getCurrentDirectory();

                    new TemplateLoader(tscFrame, path, db);
                }
            } else if (e == createCrossplotAction) {
                crossplotFrame = new CrossplotFrame(curCreator);
                CrossplotFrame.crossplot.clearAllPoints();
                crossplotFrame.setVisible(true);
            } else if (e == addSomeRascFileAction) {
                DataColumn.FileInfo fileInfo = new DataColumn.FileInfo();
                // Aaron asks: What is the file below???
                // Adam answers: RASC is a program used for making correlations in well data. Felix uses it. When I was writing a loader for it I wanted a quick way to do a load, so I made an action that will load some file to make life easy. That's what this is.
                fileInfo.loadPath = "F:\\QSCreator\\rasc\\14CEN.DAT";
                db.loadRASC(fileInfo);
            } else if (e == exitAction) {
                System.exit(0);
            } else if (e == loadDefaultColoringAction) {
                loadDefaultColoring();
            } else if (e == settingsAction) {
                showSettings();

            } else if (e == generateAction) { /*
				 * tscFrame.setVisible(true);
				 */
                generateImage();
            } else if (e == imageSizeAction) {
                ImageSizeDialog d = new ImageSizeDialog(tscFrame, svgImage);
                d.setVisible(true);
            } else if (e == zoomInAction) {
                displayCanvasPane.zoomBy(1.25);
            } else if (e == zoomOutAction) {
                displayCanvasPane.zoomBy(0.8);
            } else if (e == actualSizeAction) {
                displayCanvasPane.resetTransform();
                displayCanvasPane.recenter();
            } else if (e == fitToWindowAction) {
                displayCanvasPane.zoomToFit();
            } else if (e == showLine) { // nag : toggling for timeline
                TIMELINE = !TIMELINE;
                Element timeline = ig.doc.getElementById("timeline");
                Element timeline_up = ig.doc.getElementById("timeline_up");
                Element timeline_down = ig.doc.getElementById("timeline_down");
                Element timelabel = ig.doc.getElementById("TimeLineLabel");
                Element timelabel_up = ig.doc.getElementById("TimeLineLabelUp");
                Element timelabel_down = ig.doc.getElementById("TimeLineLabelDown");
                if (timeline != null && timelabel != null) {
                    timeline.setAttribute("style", "stroke-opacity: 0;");
                    timeline_up.setAttribute("style", "stroke-opacity: 0;");
                    timeline_down.setAttribute("style", "stroke-opacity: 0;");
                    timelabel.setAttribute("style", "fill-opacity: 0;");
                    timelabel_up.setAttribute("style", "fill-opacity: 0;");
                    timelabel_down.setAttribute("style", "fill-opacity: 0;");
                    if (TIMELINE) {
                    	//Generate a new timeline
                        timeline.setAttribute("style", ImageGenerator.TIMELINE_STYLE);
                        timeline_up.setAttribute("style", ImageGenerator.TIMELINE_STYLE);
                        timeline_down.setAttribute("style", ImageGenerator.TIMELINE_STYLE);
                        timelabel.setAttribute("style", ImageGenerator.TIMELINE_LABEL_STYLE);
                        timelabel_up.setAttribute("style", ImageGenerator.TIMELINE_LABEL_STYLE);
                        timelabel_down.setAttribute("style", ImageGenerator.TIMELINE_LABEL_STYLE);
                    }
                }
            } else if (e == quickstartAction) {
                new HTMLViewerDialog("Quick Start Guide", ResPath.getPath("html.quickstart"), 600, 400, null).setVisible(true);
            } else if (e == tourAction) {
                new HTMLViewerDialog("Tour", ResPath.getPath("html.tour"), 600, 400, null).setVisible(true);
            } else if (e == featureInfoAction) {
                new HTMLViewerDialog("Features", ResPath.getPath("html.features_summary"), 1000, 500, null).setVisible(true);
            } else if (e == licenseAction) {
                new HTMLViewerDialog("License", ResPath.getPath("html.license"), 650, 400).setVisible(true);
            } else if (e == fileFormatInfoAction) {
                new HTMLViewerDialog("File Format Info", ResPath.getPath("html.file_format_guide"), 600, 500, null).setVisible(true);
            } else if (e == contactAction) {
            	HTMLViewerDialog HDia = new HTMLViewerDialog("Contact", ResPath.getPath("html.contact"), 450, 560, null);
            	HDia.setVisible(true); 
            	HDia.setResizable(false);//change 1 
            	
            } else if (e == websiteInfoAction) {
                new WebsiteInfoDialog(tscFrame).setVisible(true);
            } else if (e == aboutAction) {
                new HTMLViewerDialog("About", ResPath.getPath("html.about"), 600, 350).setVisible(true);
            } else if (e == appletDoAfterLoading) {
                appletDoAfterLoading();
            }
        } catch (Exception ex) {
            ErrorHandler.showError(ex, null, "Unknown error", ERROR);
        }
    }

    public void bigError(boolean canSave) {
        svgImage = null;
        displayCanvas.setDocument(null);

        saveSVGAction.setEnabled(canSave);
        savePDFAction.setEnabled(canSave);

        imageSizeAction.setEnabled(false);
        zoomInAction.setEnabled(true);
        zoomOutAction.setEnabled(true);
        actualSizeAction.setEnabled(true);
        fitToWindowAction.setEnabled(true);
        showLine.setEnabled(true);

        htmlView.setEditable(false);
        htmlView.setContentType("text/html");
        String filename = ResPath.getPath("html.bigerror-cansave");
        if (!canSave) {
            filename = ResPath.getPath("html.bigerror-cannotsave");
        }

        htmlView.setText(HTMLPreprocessor.process(filename));

        mainViewLayout.show(mainView, "htmlView");
        Runtime.getRuntime().gc();
    }

    public static void showInLabel(String s, int type) {
    	if (curCreator == null) {
    		//System.out.println("Currently TSCreator program is not executing. May be converter or other program which uses the loader.");
    	    return;
    	}
    	
        // Show the string with the info label
        if (curCreator.info == null) {
            return;
        }

        synchronized (curCreator.info) {
            // set the text color based on the type
            switch (type) {
                case SUCCESS:
                    curCreator.info.setForeground(new Color(0, 128, 0));
                    break;
                case WARNING:
                    curCreator.info.setForeground(new Color(128, 128, 0));
                    break;
                case ERROR:
                    curCreator.info.setForeground(new Color(128, 0, 0));
                    break;
                default: // includes NORMAL
                    curCreator.info.setForeground(new Color(0, 0, 0));
                    break;
            }

            // set the text
            curCreator.info.setText(s);
        }
    }

    public static void logAndShow(String s, int type) {
        showInLabel(s, type);

        // log the event
        ErrorHandler.log(s, type);
    }

    public static void launchBrowser(String url) {
        try {
            ErrorHandler.log("launching " + url, ErrorHandler.INFO);
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e) {
            logAndShow(Language.translate("Error launching browser.", true), ERROR);
        }
    }

    public static void exit() {
        System.exit(1);
    }

    @Override
    public void init() {
        if (!isApplet) {
            return;
        }

		/*
		 * this seems to only work halfway resulting in a very ugly look try {
		 * String nativeLF = UIManager.getSystemLookAndFeelClassName();
		 * UIManager.setLookAndFeel(nativeLF); } catch (Exception e) {}
		 */

        // read the datafile parameter which sets the default datafile.
        // it can be an http address
        try {
            String datafileParam = getParameter("datafile");
            if (datafileParam != null) {
                ErrorHandler.log("Setting datafile from parameter to: " + datafileParam);
                DEFAULT_DATA_FILE = datafileParam;
            }
        } catch (Exception e) {
        }

        // load the data
        loadReplaceWithBuiltInAction.doWhenDone(appletDoAfterLoading);

        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("rename meta to group");

        curCreator = this;
    }

    protected void appletDoAfterLoading() {
        // read the topage/baseage parameters
        try {
            String topAge = getParameter("topage");
            String baseAge = getParameter("baseage");

            if (topAge != null && baseAge != null) {
                settings.setTopAge(db.getCurrentUnits(), topAge);
                settings.setBaseAge(db.getCurrentUnits(), baseAge);
            }
        } catch (Exception e) {
        }

        // read the generate parameter
        try {
            String generateParam = getParameter("generate");
            boolean generate = Boolean.parseBoolean(generateParam);
            if (generate) {
                generateAction.actionPerformed(null);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public static boolean isApplet() {
        return isApplet;
    }

    public static void activationSucceeded() {
        startTSC();
    }

    public static void activationFailed() {
        // quit
        System.exit(1);
    }

    public static void main(String[] args) {
        // look for some command-line parameters
        if (args != null) {
            boolean exit = false;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-proifpro")) {
                    System.out.println(ProgramInfo.isPublic() ? "" : "PRO");
                    exit = true;
                } else if (args[i].equalsIgnoreCase("-version")) {
                    Debug.turnOffDebugMessages();
                    System.out.println(ProgramInfo.get("TSC_VERSION_NUMBER"));
                    Debug.turnOnDebugMessages();
                    exit = true;
                } else if (args[i].equalsIgnoreCase("-d")) {
                    if (args.length == i + 1) {
                        continue;
                    }

                    TSCreator.BUILT_IN_DATA_FILE = args[i + 1];
                    TSCreator.command_line_datapack_flag = true;
                    System.out.println("Using datafile: " + BUILT_IN_DATA_FILE);
                    
                    // Get other datapacks
                    command_line_datapacks = new ArrayList<String>();
                    int n = 2;
                    String nextArg = args[i+n];
                    while(nextArg.length() > 1 && nextArg.contains("-") == false) {
                    	command_line_datapacks.add(nextArg);
                    	n++;
                    	nextArg = args[i+n];
                    }
                    System.out.println(command_line_datapacks.size());
                    
                } else if (args[i].equalsIgnoreCase("-ss")) {
                    if (args.length == i + 1) {
                        continue;
                    }

                    TSCreator.SETTINGS_FILE = args[i + 1];
                    TSCreator.command_line_settings_flag = true;
                    System.out.println("Saving settings to: " + SETTINGS_FILE);
                } else if (args[i].equalsIgnoreCase("-o")) {
                    if (args.length == i + 1) {
                        System.out.println("-o switch requires a file.");
                        exit();
                    }

                    TSCreator.autoSaveFile = args[i + 1];
                    System.out.println("Will save to: " + autoSaveFile);
                } else if (args[i].equalsIgnoreCase("-oo")) {
                    if (autoGenOptions == null) {
                        autoGenOptions = new AutoGenOptions();
                    }

                    if (args.length == i + 1) {
                        System.out.println("-oo switch requires an argument");
                        exit();
                    } else {
                        if (!autoGenOptions.parseCommandLineArgument(args[i + 1])) {
                            System.out.println("Unknown switch -oo " + args[i + 1] + "\r\nQuitting.");
                            exit();
                        }
                    }
                } else if (args[i].equalsIgnoreCase("-s")) {
                    if (args.length == i + 1) {
                        System.out.println("-s switch requires a file.");
                        exit();
                    }

                    TSCreator.DEFAULT_SETTINGS_FILE = args[i + 1];
                    TSCreator.command_line_settings_flag = true;
                    System.out.println("Using settings file: " + DEFAULT_SETTINGS_FILE);
                } else if (args[i].equalsIgnoreCase("-node")) {
                	System.out.println("Running node...");
                    TSCreator.NODE_MODE = true;
                } else if (args[i].equalsIgnoreCase("-rw")) {
                    try {
                        REWRITE_COLUMN_PACK = true;
                        columnpackIn = args[i + 1];
                        columnpackOut = args[i + 2];
                    } catch (Exception e) {
                        System.out.println("Usage : -rw file1 file2");
                        exit();
                    }
                } else if (args[i].equalsIgnoreCase("-testing")) {
                    // This should be called by the test script
                    // Just runs TSCreator without restarting JVM
                    testingMode = true;
                } else if (args[i].equalsIgnoreCase("-h")) {
                    String help = ""
                            + "TSCreator command line options:\r\n"
                            + " -h                  - This help screen\r\n"
                            + " -d file             - Datapack to load at startup.\r\n"
                            + " -ss file            - Save Settings.\r\n"
                            + " -d file             - Datapack to load at startup.\r\n"
                            + " -s file             - Settings file to load at startup.\r\n"
                            + " -o file             - Output file. This causes TSCreator to load its data, immediately generate an image, save it to this file, and quit. Can be combined with the -d, -s, and -oo options for generating automated charts on the fly.\r\n"
                            + " -oo option          - Output Option, combined with one of the following. Multiple -oo options are ok. Allows basic control over settings to use with -o. \r\n"
                            + " -server num         - Starts the server mode for the web interface tsCreatorLite with num number of threads.\r\n"
                            + " -rw  file1 file2    - Rewrites the columnpack with information such as column description minAge and maxAge.\r\n"
                            + " -node           - Runs TSCreator for node application\r\n\n"
                            + " -testing			- Runs TSCreator without restarting JVM. \r\n\n"
                            + AutoGenOptions.getSupportedCommandLineArgumentOptions();

                    System.out.println(help);
                    exit();
                } else if (args[i].equalsIgnoreCase("-pass")) {
                } else if (args[i].equalsIgnoreCase("-alreadySpawned")) {
                	VM_ALREADY_SPAWNED = true;
                }
            }
            if (exit) {
                return;
            }
        }


        if (autoGenOptions != null) {
            autoGenOptions.finishUp();
        }

        // Now that we've processed all the command-line options, go ahead and restart the VM with
        // more memory.
        if (!testingMode) {
            try {
                // reference: http://stackoverflow.com/questions/3571203/what-are-runtime-getruntime-totalmemory-and-freememory

                // The 32 bit version of Java has a recommended/maximum heap size somewhere less than 1.5G.
                // Because of this we cannot request 2G of heap when we are running in a 32 bit JVM.  On most
                // architectures "sun.arch.data.model" will be either "64" or "32", indicating if we are running in
                // a 64 or 32 bit VM.  If this is 64 we know for sure that we are in a 64 bit VM and are able to
                // request more memory.
                // For 32 bit JVMs we play it safe and only request 1G of heap.
                String vmBits = System.getProperty("sun.arch.data.model");
                if (vmBits.equals("64")) {
                    JavaVMOptions.start("2048m", args, VM_ALREADY_SPAWNED);
                } else {
                    JavaVMOptions.start("1024m", args, VM_ALREADY_SPAWNED);
                }
            } catch (IOException e) {
                System.out.println("FATAL ERROR: could not spawn new Java VM!");
                e.printStackTrace();
            }
        }
        // By the time we get to this line, we should have the correct amount of memory.


        // Special Password prompt added by Aaron for Felix Gradstein's demo on Feb. 9, 2011.
        // Before proceeding further, require a proper password from the user.
        if (REQUIRE_PASSWORD) {
            PasswordDialog.requirePassword(MD5_HASH_OF_PASSWORD);
        }
        // We will not get to this line until user enters proper password.


        // Initialize any dependent data from static classes.  This is necessary, for instance, because
        // one of the paths in ResPath depends on ProgramInfo (i.e. it's read from the versioninfo.xml settings
        // file).  However, the ProgramInfo static class needs ResPath to get the path to versioninfo.xml.  Therefore,
        // it is impossible to fully initialize both of them because of the circular dependency.  So, the
        // solution is to take all the stuff that IS dependent, and move it to its own function.
        ResPath.initializeDependentData();
        if (TSCreator.BUILT_IN_DATA_FILE == null) {
            TSCreator.BUILT_IN_DATA_FILE = ResPath.getPath("datapacks.builtin_data");
            Debug.print("Built-in datapack is: " + TSCreator.BUILT_IN_DATA_FILE);
        }

        try {
            String nativeLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(nativeLF);
        } catch (Exception e) {
        }

        if (isApplet) {
            ErrorHandler.showError("This version of TSCreator is a Java applet and cannot be run as a standalone application.", "TSCreator PRO APPLET", ERROR);
            return;
        }

        // check expiration
        Calendar c = util.Dater.getExpireDate();
        if (!util.Dater.isOk(c)) {
            System.out.println("This trial version of TSCreator is expired.");
            ErrorHandler.messageBox("This trial version of TSCreator is expired.", "Trial Expired", ErrorHandler.ERROR);
            return;
        }

        try {
            System.out.println("Available heap memory: " + Runtime.getRuntime().maxMemory() + " bytes.");
        } catch (java.lang.NoSuchMethodError e) {
        } // maxMemory doesn't exist on jre 1.3

        if (isActivationRequired) {
            ActivationChecker ac = new ActivationChecker(null, ProgramInfo.get("TSC_VERSION"));
            ac.pack();
            ac.setVisible(true);
        } else {
            startTSC();
        }
    }

    protected static void startTSC() {
        new TSCreator(false);
    }

    public ColumnImageGenerator getColumnImageGenerator() {
        return new ColumnImageGenerator(db.rootColumn, settings, db.patMan);
    }

    public static boolean isDatapackInfoFile(File file) {
        BufferedReader br;
        boolean result = false;

        try {
            br = new BufferedReader(new FileReader(file));

            String line = br.readLine();
            if (line.startsWith("DATAPACK-INFO")) {
                result = true;
            }
            br.close();
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    public Settings getStaticSettings(){
        return settings1 = settings;
    }
    
    public static void setSettings(SelectedTimes st) {
       if (settings != null) {
    	  settings1 = settings;
       } else {
    	  settings1 = settings = new TSCreator().settings;
       }
       settings.baseAge = settings1.baseAge = st.baseAge;
       settings.topAge = settings1.topAge = st.topAge;
    }

    public void setFlagsOptions(File file){
        try {
            String extension = FileUtils.getExtension(file.getName());
            //Check for txt extention
            if (extension.equalsIgnoreCase("txt")) {
                CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), '\t');
                String[] value;
                String key = "";
                depthFile = null;
                int i = 0;

                // read the initial lines of the file, stop if you find text.

                value = reader.readNext();
                key =value[0];

                if((key.compareToIgnoreCase("Default chronostrat:") == 0 || key.compareToIgnoreCase("format version:") == 0 || key.compareToIgnoreCase("outcrop:") == 0 || key.compareToIgnoreCase("date:") == 0 || key.compareToIgnoreCase("age units:") == 0 || key.compareToIgnoreCase("default chronotrat") == 0 || key.compareToIgnoreCase("chart title:") == 0 || key.compareToIgnoreCase("interval column:") == 0 || key.compareToIgnoreCase("settop:") == 0 || key.compareToIgnoreCase("setbase:") == 0 || key.compareToIgnoreCase("setscale:") == 0 || key.compareToIgnoreCase("TSCreator Encrypted Datafile") == 0)== false){
                    containsOptionalFlags = false;
                }
                else{
                    containsOptionalFlags = true;
                }
            }
        } catch (Exception e) {
            Debug.print(e.getMessage());
        }
    }
    
    public String OKButtonActionPerformed(ActionEvent evt){
        //JFileChooser fc = new JFileChooser(); 
   //     String inputFile;
 //       String outputFile;
        LASConverter lscv = new LASConverter(); 
        List<String> contentList = new ArrayList<String>();
        List<List<String>> fileList = new ArrayList<List<String>>();
        List<String> units = new ArrayList<String>();
        String ageUnit;
        List<Integer> maxPoints = new ArrayList<Integer>();
  	  
        List<String> titleList = new ArrayList<String>();
        List<String> widthList = new ArrayList<String>();
        List<String> colorList = new ArrayList<String>();
        List<String> pointList = new ArrayList<String>();
        List<String> lineList = new ArrayList<String>();
        List<String> fillList = new ArrayList<String>();
        List<String> lowList = new ArrayList<String>();
        List<String> highList = new ArrayList<String>();
        List<String> smoothedList = new ArrayList<String>();
        double top;
        double base;
        double scale;
       // String version;
        //String date;
        String title;
      //  String [] titleList = null;
        List<String> header;
       // inputFile =  inputChoosed.getText();
        //lg = new LASConverterGUI(tempIn.getAbsolutePath(), this);
        //int saved = fc.showSaveDialog(null);
   //     File inFile = new File(this.inputFile);;
       // if(inFile.exists() && !inFile.isDirectory()){
        //    if(saved == JFileChooser.APPROVE_OPTION){
              titleList = lg.getTitleList();
              widthList = lg.getWidthList();
              colorList = lg.getColorList();
              pointList = lg.getPointList();
              lineList = lg.getLineList();
              fillList = lg.getFillList();
              lowList = lg.getLowList();
              highList = lg.getHighList();
              smoothedList = lg.getSmoothedList();
              
              try{
            	  JFileChooser saveFileChooser = new JFileChooser();
            	  saveFileChooser.setFileFilter(new FileNameExtensionFilter(".txt",".txt"));
            	  String saveFileName = tempIn.getName().split("\\.")[0];
            	  saveFileChooser.setSelectedFile(new File(saveFileName));
            	  if(saveFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            		  tempOut = saveFileChooser.getSelectedFile();
            		  tempOutLoc = tempOut.getAbsolutePath();
            		  
            		  if(!tempOutLoc.endsWith(".txt"))
            			  tempOutLoc += ".txt";
            	  }
	                  contentList = lscv.readfile(tempIn.getAbsolutePath());
	                  header = lscv.getHeader(contentList);
 
                     fileList = lscv.fileProcessing(contentList, header);
                     units = lscv.getUnits(contentList);
                     maxPoints = lscv.getMaxPoint(fileList);
                     ageUnit = lg.hp.ageUnitText.getText();
                     top = Double.parseDouble(lg.hp.topText.getText());
                     base = Double.parseDouble(lg.hp.baseText.getText());
                     scale = Double.parseDouble(lg.hp.scaleText.getText());
                     title = lg.hp.titleText.getText();
                     lscv.writeFile(fileList, tempOutLoc, units, ageUnit,titleList, widthList, colorList, pointList, lineList, fillList, lowList, highList, smoothedList, top, base, scale, title);

                     return tempOutLoc;
              } catch (Exception e){
           	   JOptionPane.showMessageDialog(null, "Temporary file creation failed!");
           	   e.printStackTrace();
              }
              
         return null;
 }
    

}
