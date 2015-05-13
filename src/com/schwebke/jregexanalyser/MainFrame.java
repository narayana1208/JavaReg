/*
 * Copyright (c) 2011, Kai Schwebke. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Kai Schwebke (kai@schwebke.com)
 * or visit www.schwebke.com if you need additional information or have any
 * questions.
 */

/*
 * $Id: MainFrame.java 755 2012-08-14 18:05:54Z kai $
 */

package com.schwebke.jregexanalyser;

import com.schwebke.ImageWindow;
import com.schwebke.ProcessWindow;
import com.schwebke.jregexanalyser.regex.*;
import com.schwebke.jregexanalyser.regex.Statistics.*;
import com.schwebke.jregexanalyser.regex.Statistics.MatchTest;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
//import javax.jnlp.FileOpenService;

/**
 * GUI main Window
 */
@SuppressWarnings({ "serial", "unused" })
public class MainFrame extends javax.swing.JFrame implements RegExWorker.Publisher 
{
	private static int instanceCount = 0;

    // session-unique window instance id
    private static int nextInstanceId = 0;
    private final int instanceId;

    // recent test text files
    private Deque<File> recentTestText;

    private String lastRegEx;
    private RegExWorker.Result lastResult;
    private Highlighter textHighlighter;
    private Highlighter.HighlightPainter matchHighlightPainter;
    private Highlighter.HighlightPainter posHighlightPainter;
    private PrefDialog prefDialog;
    @SuppressWarnings("rawtypes")
	private DefaultComboBoxModel regExInputModel;
    @SuppressWarnings("rawtypes")
	private DefaultListModel splitResultList;
    private RegExEdit regExEdit;
    private PatternSyntaxErrorDialogue regExErrorEdit;

    private LinkedBlockingDeque<RegExWorker.Task> backgroundMatching;
    private Thread backgroundMatcher;
    private boolean backgroundMatcherStarted;
    							

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAboutButton;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JButton jClearButton;
    private javax.swing.JTree jCompiledPatternTree;
    private javax.swing.JButton jExitButton;
    private javax.swing.JButton jHelpButton;
    private javax.swing.JButton jImportButton;
    private javax.swing.JButton jImportRecentButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jMLEditButton;
    private javax.swing.JToolBar jMainToolBar;
    private javax.swing.JTree jMatchResultTree;
    private javax.swing.JTable jMatchTraceTable;
    @SuppressWarnings("rawtypes")
	private javax.swing.JComboBox jModeSelector;
    private javax.swing.JButton jNewButton;
    private javax.swing.JCheckBox jOpt_CANON_EQ;
    private javax.swing.JCheckBox jOpt_CASE_INSENSITIVE;
    private javax.swing.JCheckBox jOpt_COMMENTS;
    private javax.swing.JCheckBox jOpt_DOTALL;
    private javax.swing.JCheckBox jOpt_LITERAL;
    private javax.swing.JCheckBox jOpt_MULTILINE;
    private javax.swing.JCheckBox jOpt_UNICODE_CASE;
    private javax.swing.JCheckBox jOpt_UNIX_LINES;
    private javax.swing.JPanel jOptionsPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jPatternDotButton;
    private javax.swing.JToolBar.Separator jPatternDotSeparator;
    private javax.swing.JButton jPreferencesButton;
    @SuppressWarnings("rawtypes")
	private javax.swing.JComboBox jRegExInput;
    private javax.swing.JTextArea jReplacedText;
    private javax.swing.JTextArea jReplacementInput;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSpinner jSplitLimitSpinner;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JLabel jSplitResultLabel;
    @SuppressWarnings("rawtypes")
	private javax.swing.JList jSplitResultList;
    private javax.swing.JTextField jStatusText;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTestText;
    private javax.swing.JButton jToLiteralButton;
    // End of variables declaration//GEN-END:variables

    // manual GUI elements
    private javax.swing.JPopupMenu jRecentTestTextPopup;
    /** Creates new form MainFrame */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public MainFrame() 
    {
        ++instanceCount;
        instanceId = ++nextInstanceId;

        lastRegEx = "";
        backgroundMatching = new LinkedBlockingDeque<RegExWorker.Task>();
        
        initComponents();
        
        jTestText.setLineWrap(Main.getWrapText());
        jReplacedText.setLineWrap(Main.getWrapText());

        // create recent test text popup menu
        jRecentTestTextPopup = new JPopupMenu();
        recentTestText = new LinkedList<File>();
        int i;
        for (i = 0; i < Main.getHistorySize(); ++i) 
        {
            String s = Main.getRecentTestText(i);
            if (s != null) 
            {
                File f = new File(s);
                recentTestText.addLast(f);
            }
        }
        
        updateRecentTestTextMenu();
        
        // attach combo box document listener to get changes during edit
        JTextComponent tc = (JTextComponent) jRegExInput.getEditor().getEditorComponent();
        tc.getDocument().addDocumentListener(new ChangedDocumentListener(tc));

        // attach listeners to other text inputs
        DocumentListener triggerDocumentListener = new ChangedTriggerDocumentListener();
        jTestText.getDocument().addDocumentListener(triggerDocumentListener);
        jReplacementInput.getDocument().addDocumentListener(triggerDocumentListener);

        // attach listeners to check boxes and buttons
        ActionListener triggerListener = new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                triggerMatching();
            }
        };
        jOpt_CANON_EQ.addActionListener(triggerListener);
        jOpt_CASE_INSENSITIVE.addActionListener(triggerListener);
        jOpt_COMMENTS.addActionListener(triggerListener);
        jOpt_DOTALL.addActionListener(triggerListener);
        jOpt_LITERAL.addActionListener(triggerListener);
        jOpt_MULTILINE.addActionListener(triggerListener);
        jOpt_UNICODE_CASE.addActionListener(triggerListener);
        jOpt_UNIX_LINES.addActionListener(triggerListener);
        jModeSelector.addActionListener(triggerListener);

        jSplitLimitSpinner.addChangeListener(new ChangeListener() 
        {
            public void stateChanged(ChangeEvent e) 
            {
                triggerMatching();
            }
        });

        // main window position is not handled by preferences,
        // so we always have a reasonable default
        setBounds(100, 100, 900, 700);

        // fill regex entry history from preferences
        regExInputModel = (DefaultComboBoxModel) jRegExInput.getModel();
        regExInputModel.removeAllElements();
        
        for (i = 0; i < Main.getHistorySize(); ++i) 
        {
            String e = Main.getHistoryEntry(i);
            if (e != null) 
            {
                regExInputModel.addElement(e);
                if (e.contains("\n")) 
                {
                    jRegExInput.setEditable(false);
                    jStatusText.setText("history has multi line entries; enabling multi line mode");
                }
            }
        }
        jRegExInput.setSelectedIndex(regExInputModel.getSize()-1);

        // prepare background matcher
        backgroundMatcher = new Thread(new RegExWorker(backgroundMatching, this),"backgroundMatcher-"+instanceId);
        backgroundMatcher.setDaemon(true);
    }

    public void startBackgroundTasks() 
    {
        // start background matcher
        backgroundMatching.clear();
        backgroundMatcher.start();
        backgroundMatcherStarted = true;
        jStatusText.setText(Main.version);

        // check for new version
        if (Main.getCheckVersion() && (instanceId == 1) && (!Main.isWebStart())) {
            @SuppressWarnings("rawtypes")
			SwingWorker checkVersionWorker = new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception 
                {
                    URL onlineVersionURL = new URL(Main.onlineVersionURL);
                    BufferedReader r =
                            new BufferedReader(
                                new InputStreamReader(
                                    onlineVersionURL.openStream()));
                    String onlineVersion = r.readLine();
                    r.close();
                    return onlineVersion;
                }

                @Override
                protected void done() 
                {
                    String onlineVersion = null;
                    try 
                    {
                        onlineVersion = get();
                    } catch (Exception e) {
                        // ignore
                        return;
                    }
                    if (onlineVersion.compareTo(Main.version) > 0) 
                    {
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "A new version of jRegExAnalyser has been released.\n"+
                            "Version "+onlineVersion+" is available at jregexanalyser.schwebke.com",
                            "New Version Information",
                            JOptionPane.OK_OPTION|JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            };
            checkVersionWorker.execute();
        }
    }

    /**
     * Trigger matching with text component contents as expression.
     * Used to handle reg ex input component changes.
     */
    public class ChangedDocumentListener implements DocumentListener 
    {
        public ChangedDocumentListener(JTextComponent tc) 
        {
            this.tc = tc;
        }
        public void insertUpdate(DocumentEvent e) 
        {
            triggerMatching(false, tc.getText());
        }

        public void removeUpdate(DocumentEvent e) 
        {
            triggerMatching(false, tc.getText());
        }

        public void changedUpdate(DocumentEvent e) 
        {
            triggerMatching(false, tc.getText());
        }
        private final JTextComponent tc;
    }

    /**
     * Trigger matching with last regex.
     * Used to handle other text input changes (test text, replace text, ...).
     */
    public class ChangedTriggerDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            triggerMatching(false, null);
        }

        public void removeUpdate(DocumentEvent e) {
            triggerMatching(false, null);
        }

        public void changedUpdate(DocumentEvent e) {
            triggerMatching(false, null);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRegExInput = new javax.swing.JComboBox();
        jApplyButton = new javax.swing.JButton();
        jOptionsPanel = new javax.swing.JPanel();
        jOpt_CANON_EQ = new javax.swing.JCheckBox();
        jOpt_CASE_INSENSITIVE = new javax.swing.JCheckBox();
        jOpt_COMMENTS = new javax.swing.JCheckBox();
        jOpt_DOTALL = new javax.swing.JCheckBox();
        jOpt_LITERAL = new javax.swing.JCheckBox();
        jOpt_MULTILINE = new javax.swing.JCheckBox();
        jOpt_UNICODE_CASE = new javax.swing.JCheckBox();
        jOpt_UNIX_LINES = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jModeSelector = new javax.swing.JComboBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTestText = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jMatchResultTree = new javax.swing.JTree();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jCompiledPatternTree = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jMatchTraceTable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jReplacementInput = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jReplacedText = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jSplitResultLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSplitLimitSpinner = new javax.swing.JSpinner();
        jScrollPane7 = new javax.swing.JScrollPane();
        jSplitResultList = new javax.swing.JList();
        jStatusText = new javax.swing.JTextField();
        jMLEditButton = new javax.swing.JButton();
        jToLiteralButton = new javax.swing.JButton();
        jMainToolBar = new javax.swing.JToolBar();
        jNewButton = new javax.swing.JButton();
        jExitButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jClearButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jImportButton = new javax.swing.JButton();
        jImportRecentButton = new javax.swing.JButton();
        jPatternDotSeparator = new javax.swing.JToolBar.Separator();
        jPatternDotButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jPreferencesButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jHelpButton = new javax.swing.JButton();
        jAboutButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("jRegExAnalyser");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconApp.png")).getImage()));

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("RegEx");

        jRegExInput.setEditable(true);
        jRegExInput.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconReload.png"))); // NOI18N
        jApplyButton.setToolTipText("apply");
        jApplyButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jApplyButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jApplyButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });

        jOpt_CANON_EQ.setText("CANON_EQ");

        jOpt_CASE_INSENSITIVE.setText("CASE_INSENSITIVE");

        jOpt_COMMENTS.setText("COMMENTS");

        jOpt_DOTALL.setText("DOTALL");

        jOpt_LITERAL.setText("LITERAL");

        jOpt_MULTILINE.setText("MULTILINE");

        jOpt_UNICODE_CASE.setText("UNICODE_CASE");

        jOpt_UNIX_LINES.setText("UNIX_LINES");

        jLabel2.setText("Mode");

        jModeSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "find", "match" }));

        javax.swing.GroupLayout jOptionsPanelLayout = new javax.swing.GroupLayout(jOptionsPanel);
        jOptionsPanel.setLayout(jOptionsPanelLayout);
        jOptionsPanelLayout.setHorizontalGroup(
            jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOpt_CANON_EQ)
                    .addComponent(jOpt_LITERAL))
                .addGap(18, 18, 18)
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jOpt_MULTILINE, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOpt_CASE_INSENSITIVE, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(18, 18, 18)
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOpt_UNICODE_CASE)
                    .addComponent(jOpt_COMMENTS))
                .addGap(18, 18, 18)
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOpt_DOTALL)
                    .addComponent(jOpt_UNIX_LINES))
                .addGap(41, 41, 41)
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jModeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jOptionsPanelLayout.setVerticalGroup(
            jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jOpt_CANON_EQ)
                    .addComponent(jOpt_CASE_INSENSITIVE)
                    .addComponent(jOpt_COMMENTS)
                    .addComponent(jOpt_DOTALL)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jOpt_MULTILINE)
                    .addComponent(jOpt_UNICODE_CASE)
                    .addComponent(jOpt_UNIX_LINES)
                    .addComponent(jModeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jOpt_LITERAL))
                .addContainerGap())
        );

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane1.setDividerLocation(190);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);

        jTestText.setColumns(20);
        jTestText.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTestText.setRows(5);
        jTestText.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTestText);
        textHighlighter = new DefaultHighlighter();
        matchHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
        posHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
        jTestText.setHighlighter(textHighlighter);

        jLabel8.setText("Text");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(jPanel1);

        jSplitPane2.setBorder(null);
        jSplitPane2.setDividerLocation(200);

        jLabel3.setText("Matches");

        jMatchResultTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jMatchResultTree.setModel(null);
        jMatchResultTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jMatchResultTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jMatchResultTree);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(jPanel3);

        jSplitPane3.setBorder(null);
        jSplitPane3.setDividerLocation(250);
        jSplitPane3.setResizeWeight(1.0);

        jLabel4.setText("Pattern Explorer");

        jCompiledPatternTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jCompiledPatternTree.setModel(null);
        jScrollPane3.setViewportView(jCompiledPatternTree);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(jLabel4))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane3.setLeftComponent(jPanel4);

        jLabel5.setText("Matcher Operations");

        jMatchTraceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jMatchTraceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jMatchTraceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                jMatchTraceTableListSelectionChanged(e);
            }
        });
        jScrollPane5.setViewportView(jMatchTraceTable);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane3.setRightComponent(jPanel5);

        jSplitPane2.setRightComponent(jSplitPane3);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 887, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jSplitPane2)
                    .addGap(0, 0, 0)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jSplitPane2)
                    .addGap(0, 0, 0)))
        );

        jTabbedPane1.addTab("Matching", jPanel7);

        jLabel6.setText("Replacement");

        jReplacementInput.setColumns(20);
        jReplacementInput.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jReplacementInput.setRows(1);
        jScrollPane6.setViewportView(jReplacementInput);

        jReplacedText.setEditable(false);
        jReplacedText.setColumns(20);
        jReplacedText.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jReplacedText.setRows(5);
        jReplacedText.setWrapStyleWord(true);
        jScrollPane4.setViewportView(jReplacedText);

        jLabel7.setText("Result");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 771, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel7)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Replacing", jPanel8);

        jSplitResultLabel.setText("Split Result");

        jLabel9.setText("Limit");

        jSplitLimitSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(-1), null, Integer.valueOf(1)));

        jSplitResultList.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jSplitResultList.setModel(splitResultList = new DefaultListModel());
        jScrollPane7.setViewportView(jSplitResultList);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitResultLabel)
                    .addComponent(jLabel9)
                    .addComponent(jSplitLimitSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitResultLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitLimitSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(173, Short.MAX_VALUE))
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Splitting", jPanel9);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jSplitPane1.setRightComponent(jPanel2);

        jStatusText.setEditable(false);
        jStatusText.setEnabled(false);

        jMLEditButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconEdit.png"))); // NOI18N
        jMLEditButton.setToolTipText("multi line edit");
        jMLEditButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jMLEditButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jMLEditButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jMLEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMLEditButtonActionPerformed(evt);
            }
        });

        jToLiteralButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconToLiteral.png"))); // NOI18N
        jToLiteralButton.setToolTipText("convert to string literal");
        jToLiteralButton.setMaximumSize(new java.awt.Dimension(30, 30));
        jToLiteralButton.setMinimumSize(new java.awt.Dimension(30, 30));
        jToLiteralButton.setPreferredSize(new java.awt.Dimension(30, 30));
        jToLiteralButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToLiteralButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jRegExInput, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMLEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToLiteralButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jApplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jStatusText)
            .addComponent(jSplitPane1)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                        .addComponent(jLabel1)
                        .addComponent(jRegExInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jApplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToLiteralButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMLEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatusText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel6, java.awt.BorderLayout.CENTER);

        jMainToolBar.setRollover(true);

        jNewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconNew.png"))); // NOI18N
        jNewButton.setToolTipText("new window");
        jNewButton.setFocusable(false);
        jNewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jNewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jNewButton);

        jExitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconExit.png"))); // NOI18N
        jExitButton.setToolTipText("exit");
        jExitButton.setFocusable(false);
        jExitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jExitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExitButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jExitButton);
        jMainToolBar.add(jSeparator3);

        jClearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconTrash.png"))); // NOI18N
        jClearButton.setToolTipText("clear history");
        jClearButton.setFocusable(false);
        jClearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jClearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jClearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jClearButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jClearButton);
        jMainToolBar.add(jSeparator2);

        jImportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconImport.png"))); // NOI18N
        jImportButton.setToolTipText("import test text from file");
        jImportButton.setFocusable(false);
        jImportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jImportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jImportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImportButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jImportButton);

        jImportRecentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconImportRecent.png"))); // NOI18N
        jImportRecentButton.setToolTipText("import recent test text");
        jImportRecentButton.setEnabled(false);
        jImportRecentButton.setFocusable(false);
        jImportRecentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jImportRecentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jImportRecentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImportRecentButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jImportRecentButton);
        jMainToolBar.add(jPatternDotSeparator);

        jPatternDotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconDot.png"))); // NOI18N
        jPatternDotButton.setToolTipText("visualise pattern");
        jPatternDotButton.setEnabled(false);
        jPatternDotButton.setFocusable(false);
        jPatternDotButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPatternDotButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        if (Main.isWebStart()) {
            jPatternDotSeparator.setVisible(false);
            jPatternDotButton.setVisible(false);
        }
        jPatternDotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPatternDotButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jPatternDotButton);
        jMainToolBar.add(jSeparator4);

        jPreferencesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconPreferences.png"))); // NOI18N
        jPreferencesButton.setToolTipText("change preferences");
        jPreferencesButton.setFocusable(false);
        jPreferencesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPreferencesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPreferencesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPreferencesButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jPreferencesButton);
        jMainToolBar.add(jSeparator1);

        jHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconHelp.png"))); // NOI18N
        jHelpButton.setToolTipText("help");
        jHelpButton.setFocusable(false);
        jHelpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jHelpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHelpButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jHelpButton);

        jAboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/schwebke/jregexanalyser/res/IconAbout.png"))); // NOI18N
        jAboutButton.setToolTipText("about");
        jAboutButton.setFocusable(false);
        jAboutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jAboutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jAboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAboutButtonActionPerformed(evt);
            }
        });
        jMainToolBar.add(jAboutButton);

        getContentPane().add(jMainToolBar, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateRecentTestTextMenu() 
    {
        jRecentTestTextPopup.removeAll();
        jImportRecentButton.setEnabled(!recentTestText.isEmpty());
        
        while (recentTestText.size() > Main.getHistorySize()) 
        {
            recentTestText.pollLast();
        }
        
        int i = 0;
        for (File f : recentTestText) 
        {
            JMenuItem mItem = new JMenuItem(f.getName());
            final File myFile = f;
            jRecentTestTextPopup.add(mItem);
            mItem.addActionListener(new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    try 
                    {
                        InputStream ins = new FileInputStream(myFile);
                        importTestText(ins);
                    } 
                    catch (IOException ioe) {
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            ioe.getMessage(),
                            "jRegExAnalyser: file import error",
                            JOptionPane.OK_OPTION|JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            try 
            {
                Main.setRecentTestText(i, myFile.getCanonicalPath());
                ++i;
            }
            catch (Exception e) { /* ignore */ }
        }
    }

    private int getRegExHistoryIndex(String regEx) 
    {
        for (int i = 0; i < regExInputModel.getSize(); ++i) 
        {
            if (regExInputModel.getElementAt(i).equals(regEx)) 
            {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
	public void appendRegExHistory(String regEx) 
    {
        // check for multi line entry
        if (regEx.contains("\n")) 
        {
            // disable combo box editing; it would destroy multi line entries
            jRegExInput.setEditable(false);
            jStatusText.setText("multi line edit enabled");
        }

        int i = getRegExHistoryIndex(regEx);
        if (i == -1) 
        {
            regExInputModel.addElement(regEx);
            jRegExInput.setSelectedIndex(regExInputModel.getSize()-1);
        }
        else 
        {
            jRegExInput.setSelectedIndex(i);
        }
        // trim history
        while (regExInputModel.getSize() > Main.getHistorySize()) 
        {
            regExInputModel.removeElementAt(0);
        }
        // store history
        for (i = 0; i < jRegExInput.getItemCount(); ++i) 
        {
            Main.setHistoryEntry(i, (String)regExInputModel.getElementAt(i));
        }
    }

    public String getRegEx() 
    {
        String s = (String)jRegExInput.getSelectedItem();
        System.out.println("Regular Expression String is:  \t"+s);
        return (s == null)?(""):(s);
    }

    /**
     * Compile Pattern from expression text and selected options.
     * @return compiled pattern
     * @throws PatternSyntaxException
     */
    private Pattern compilePattern(String regEx) throws PatternSyntaxException 
    {
    	System.out.println(" The passed RegEx is :\t"+regEx);
        return Pattern.compile(
                    regEx,
                    (jOpt_CANON_EQ.isSelected() ? Pattern.CANON_EQ : 0)
                    | (jOpt_CASE_INSENSITIVE.isSelected() ? Pattern.CASE_INSENSITIVE : 0)
                    | (jOpt_COMMENTS.isSelected() ? Pattern.COMMENTS : 0)
                    | (jOpt_DOTALL.isSelected() ? Pattern.DOTALL : 0)
                    | (jOpt_LITERAL.isSelected() ? Pattern.LITERAL : 0)
                    | (jOpt_MULTILINE.isSelected() ? Pattern.MULTILINE : 0)
                    | (jOpt_UNICODE_CASE.isSelected() ? Pattern.UNICODE_CASE : 0)
                    | (jOpt_UNIX_LINES.isSelected() ? Pattern.UNIX_LINES : 0));
    }

    /**
     * Publish result in the GUI thread.
     * @param r
     */
    public void publish(final RegExWorker.Result r) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                updateGui(r);
            }
        });
    }

    /**
     * Update the GUI to show the results from a matching and analyzing task.
     * @param r
     */
    @SuppressWarnings("unchecked")
	private void updateGui(RegExWorker.Result r) 
    {
        // set result vars
        lastResult = r;
        jPatternDotButton.setEnabled(true);
        
        // update tree models and text
        jCompiledPatternTree.setModel(r.compiledPatternTreeModel);
        jMatchResultTree.setModel(r.matchResultTreeModel);
        jReplacedText.setText(r.res.repl.toString());
        splitResultList.clear();
        
        if (r.res.split != null) 
        {
            int i = 0;
            for (String s : r.res.split) 
            {
                String entry;
                if (s.length() == 0) 
                {
                    entry = String.format("%3d: <empty>", i);
                }
                else 
                {
                    entry = String.format("%3d: %s", i, s);
                }
                splitResultList.addElement(entry);
                ++i;
            }
        }

        // create match trace table model
        jMatchTraceTable.setModel(r.traceTableModel);

        // set column widths
        for (int i = 0; i < 6; ++i) 
        {
            if (i == 3) 
            {
                jMatchTraceTable.getColumnModel().getColumn(i).setPreferredWidth(200);
            }
            else {
                jMatchTraceTable.getColumnModel().getColumn(i).setPreferredWidth(100);
            }
        }

        // select all match results
        /*
         * Changing the match result selection changes the caret position
         * to scroll to the first match.
         * This is disturbing when editing the test text. So in case the
         * test text has the focus we will conserve the caret position.
         */
        if (jTestText.isFocusOwner()) {
            int cpos = jTestText.getCaretPosition();
            jMatchResultTree.setSelectionRow(0);
            jTestText.setCaretPosition(cpos);
        } 
        else
        {
            jMatchResultTree.setSelectionRow(0);
        }

        jStatusText.setText(String.format("matching done, %.4fs, %d ops",
                (r.stop.getTime()-r.start.getTime())/1000.0,
                jMatchTraceTable.getModel().getRowCount()));
    }


    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jApplyButtonActionPerformed
        triggerMatching(true, getRegEx());
    }

    /**
     * Shortcut for triggerMatching(false, null).
     * Performs an implicit trigger with last regEx.
     */
    public void triggerMatching() 
    {
        triggerMatching(false, null);
    }
    
    /**
     * Triggers a background matching operation.
     * If explicit matching is requested, a pattern
     * syntax error message box may be displayed,
     * the pattern is included in the history
     * and the matching is always triggered.
     * Otherwise errors will not show a popup and the history will
     * not be extended.
     * Also disabling the automatching pref will drop implicit triggers.
     * Explicit triggers are used for explicit matchings requested by button.
     * @param explicit
     * @param regEx Regular Expression pattern to use; pass null to use previous one
     */
    public void triggerMatching(boolean explicit, String regEx) 
    {
        // check if bg match thread was started, but died
        if ( (backgroundMatcherStarted) && (!backgroundMatcher.isAlive()) ) 
        {
            throw new RuntimeException(
                    "The background matcher thread died.\n"+
                    "This may be caused by an out of memory condition.\n"+
                    "Running jRegExAnalyser with a console window may yield more information.");
        }
        
        // match implicit only if enabled
        if ((!explicit) && (!Main.getPermanentMatching())) 
        {
            return;
        }

        // use last regex for null arg
        if (regEx != null) 
        {
            lastRegEx = regEx;
        } 
        else 
        {
            regEx = lastRegEx;
        }

        // match empty regex only if explicit
        if ((!explicit) && (regEx.equals(""))) {
            return;
        }

        jStatusText.setText("matching...");
        Pattern p = null;
        
        String replace = (String)jReplacementInput.getText();
        if (replace == null) {
            replace = "";
        }

        // get regex and
        // try to compile regex
        try 
        {
            p = compilePattern(regEx);
        } 
        catch (PatternSyntaxException e) 
        {
            jStatusText.setText("pattern syntax error");
            if (explicit) 
            {
                if (regExErrorEdit == null) 
                {
                    regExErrorEdit = new PatternSyntaxErrorDialogue(this);
                }
                regExErrorEdit.show(regEx, e);
            }
            /*
            int errorPos = e.getIndex();
            if (errorPos > 0) {
                jRegExInput.setCaretPosition(errorPos + 1);
            }*/
            return;
        }

        // compile ok; add regex to combo box, if not already in history
        if (explicit) {
            appendRegExHistory(p.pattern());
        }
       // System.out.println(" Test text from window"+jTestText.getText());

        // create task
        int splitLimit = (Integer)jSplitLimitSpinner.getValue();
        RegExWorker.Task t = new RegExWorker.Task(
                p, jTestText.getText(),
                replace,
                (String)jModeSelector.getSelectedItem(),
                splitLimit);

        // enqueue task
        try {
            backgroundMatching.clear();
            backgroundMatching.putFirst(t);
        } catch (InterruptedException e) {
            System.out.println("warning: unexpected interrupt in enqueue");
        }
    }//GEN-LAST:event_jApplyButtonActionPerformed

    private void jMatchResultTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) 
    {//GEN-FIRST:event_jMatchResultTreeValueChanged
        // update highlight in test text to reflect selected match (all, match or match group)
        textHighlighter.removeAllHighlights();
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) jMatchResultTree.getLastSelectedPathComponent();
        if (n != null) 
        {
            if (Statistics.Match.class.isInstance(n.getUserObject())) 
            {
                Statistics.Match match = (Statistics.Match) n.getUserObject();
                jTestText.setCaretPosition(match.getFrom());
                try 
                {
                    textHighlighter.addHighlight(match.getFrom(), match.getTo(), matchHighlightPainter);
                }
                catch (BadLocationException ex) 
                {
                    // ignore
                }
            } 
            else 
            {
                // all or none matches selected, so highlight all
                for (Statistics s : lastResult.s) 
                {
                    int first = s.getFirst();
                    int last = s.getLast();
                    if (first >= 0) 
                    {
                        jTestText.setCaretPosition(first);
                        try 
                        {
                            textHighlighter.addHighlight(first, last, matchHighlightPainter);
                        }
                        catch (BadLocationException ex) 
                        {
                            // ignore
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jMatchResultTreeValueChanged

    private void jImportButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jImportButtonActionPerformed
        // import a test text from a file
        InputStream ins = null;

        // try regular file open
        try 
        {
            JFileChooser fc;
            File lastDir = Main.getLastDir();
            if ((lastDir != null) && (lastDir.isDirectory())) 
            {
                fc = new JFileChooser(lastDir);
            } 
            else 
            {
                fc = new JFileChooser();
            }
            int ret = fc.showOpenDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) 
            {

                File in = fc.getSelectedFile();
                ins = new FileInputStream(in);

                lastDir = in.getParentFile();
                Main.setLastDir(lastDir);
                recentTestText.addFirst(in);
                updateRecentTestTextMenu();
            }
        } 
        catch (IOException ioe) 
        {
            // plain IO error: show message and abort
            ins = null;
            JOptionPane.showMessageDialog(
                            this,
                            ioe.getMessage(),
                            "jRegExAnalyser: file import error",
                            JOptionPane.OK_OPTION|JOptionPane.ERROR_MESSAGE);
        } 
        catch (Exception eFC) 
        {
            // try web start file open service, may be running in a sandbox
            try 
            {
                javax.jnlp.FileOpenService fos = (javax.jnlp.FileOpenService)javax.jnlp.ServiceManager.lookup("javax.jnlp.FileOpenService");
                javax.jnlp.FileContents fc = fos.openFileDialog(null, null);
                ins = fc.getInputStream();
            } 
            catch (Exception e2) 
            {
                ins = null;
                JOptionPane.showMessageDialog(
                        this,
                        e2.getMessage(),
                        "jRegExAnalyser: file import error",
                        JOptionPane.OK_OPTION|JOptionPane.ERROR_MESSAGE);
            }
        }

        if (ins != null) {
            importTestText(ins);
        }
    }//GEN-LAST:event_jImportButtonActionPerformed

    private void importTestText(InputStream ins) 
    {
        try {
            BufferedReader r =
                    new BufferedReader(
                    new InputStreamReader(
                    ins,
                    Main.getCharset()));
            StringBuilder b = new StringBuilder();
            char[] buff = new char[1024];
            int cnt;
            while ((cnt = r.read(buff)) >= 0) {
                b.append(buff, 0, cnt);
            }
            r.close();
            jTestText.setText(b.toString());
            jMatchResultTree.setModel(null);
            jCompiledPatternTree.setModel(null);
            jMatchTraceTable.setModel(new DefaultTableModel());
        }
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "jRegExAnalyser: file import error",
                        JOptionPane.OK_OPTION|JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jPreferencesButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jPreferencesButtonActionPerformed
        if (prefDialog == null) {
            prefDialog = new PrefDialog(this);
        }
        prefDialog.showPrefs();
    }//GEN-LAST:event_jPreferencesButtonActionPerformed

    private void jAboutButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jAboutButtonActionPerformed
        JOptionPane.showMessageDialog(
                this,
                Main.about,
                Main.version,
                JOptionPane.OK_OPTION|JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jAboutButtonActionPerformed

    private void jHelpButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jHelpButtonActionPerformed
        // try to browse to online help page
        // in case of error display an ordinary message box
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) 
        {
            desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) 
            {
                try {
                    desktop.browse(new URI("http://jregexanalyser.schwebke.com/jregexanalyser/webdoc/"));
                    return;
                }
                catch (Exception e) 
                {
                    // ignore
                }
            }
        }
        JOptionPane.showMessageDialog(
                this,
                "For help please visit jregexanalyser.schwebke.com.",
                "jRegExAnalyser: Help",
                JOptionPane.OK_OPTION|JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jHelpButtonActionPerformed

    private void jExitButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jExitButtonActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_jExitButtonActionPerformed

    private void jMLEditButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jMLEditButtonActionPerformed
        // create edit window
        if (regExEdit == null) 
        {
            regExEdit = new RegExEdit(this);
        }

        // show edit window
        regExEdit.setRegEx(getRegEx());
        regExEdit.setVisible(true);
    }//GEN-LAST:event_jMLEditButtonActionPerformed

    private void jClearButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jClearButtonActionPerformed
        regExInputModel.removeAllElements();
        jRegExInput.setEditable(true);
        jStatusText.setText("history cleared");
        for (int i = 0; i < Main.getHistorySize(); ++i) {
            Main.setHistoryEntry(i, "");
        }
    }//GEN-LAST:event_jClearButtonActionPerformed

    private void jToLiteralButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jToLiteralButtonActionPerformed
        String flags = (jOpt_CANON_EQ.isSelected() ? "|Pattern.CANON_EQ" : "")
                    + (jOpt_CASE_INSENSITIVE.isSelected() ? "|Pattern.CASE_INSENSITIVE" : "")
                    + (jOpt_COMMENTS.isSelected() ? "|Pattern.COMMENTS" : "")
                    + (jOpt_DOTALL.isSelected() ? "|Pattern.DOTALL" : "")
                    + (jOpt_LITERAL.isSelected() ? "|Pattern.LITERAL" : "")
                    + (jOpt_MULTILINE.isSelected() ? "|Pattern.MULTILINE" : "")
                    + (jOpt_UNICODE_CASE.isSelected() ? "|Pattern.UNICODE_CASE" : "")
                    + (jOpt_UNIX_LINES.isSelected() ? "|Pattern.UNIX_LINES" : "");
        if (flags.length() > 0) 
        {
            flags = flags.substring(1);
        }
        LiteralDialog lf = new LiteralDialog(this, getRegEx(), flags);
        lf.setVisible(true);
    }//GEN-LAST:event_jToLiteralButtonActionPerformed

    private void jPatternDotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPatternDotButtonActionPerformed
        String fileBaseName = System.getProperty("user.home")+"/jrea"+(Main.sessionStart%1000)+"-"+instanceId;
        final File dotFile = new File(fileBaseName+".dot");
        final File pngFile = new File(fileBaseName+".png");
        dotFile.deleteOnExit();
        pngFile.deleteOnExit();

        // export dot file
        try {
            pngFile.delete();
            Writer dotWriter = new FileWriter(dotFile);
            String dot = "?";
            if (jModeSelector.getSelectedItem().equals("find")) {
                dot = lastResult.findPatternDot;
            }
            if (jModeSelector.getSelectedItem().equals("match")) {
                dot = lastResult.matchPatternDot;
            }
            dotWriter.write(dot);
            dotWriter.close();
            jStatusText.setText("DOT diagram file '"+dotFile.getCanonicalPath()+"'");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to write the dot export file to\n"+
                    "'"+dotFile.getPath()+"'\n\n"+
                    ioe.getMessage(),
                    "jRegExAnalyser: Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // run dot
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "dot",
                    "-Tpng",
                    dotFile.getPath(),
                    "-o",
                    pngFile.getPath());
            Process proc = pb.start();
            final ProcessWindow pw = new ProcessWindow(proc, "jRegExAnalyser: running dot");

            // run process in background
            new Thread(new Runnable() 
            {
                public void run() 
                {
                    try {
                        pw.run();
                        pw.waitFor();
                        if (!pngFile.canRead()) {
                            throw new IOException("cannot read '"+pngFile.getPath()+"'");
                        }
                        java.awt.EventQueue.invokeLater(new Runnable() 
                        {
                            public void run() 
                            {
                                try 
                                {
                                    new ImageWindow(MainFrame.this, "jRegExAnalyser: dot", pngFile).setVisible(true);
                                }
                                catch (Exception e) 
                                {
                                    JOptionPane.showMessageDialog(
                                            MainFrame.this,
                                            "Failed to load dot created image\n"+
                                            "'"+pngFile.getPath()+"'.\n\n"+
                                            e.getMessage(),
                                            "jRegExAnalyser: Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                    } 
                    catch (final Exception e) 
                    {
                        java.awt.EventQueue.invokeLater(new Runnable() 
                        {
                            public void run() 
                            {
                                JOptionPane.showMessageDialog(
                                        MainFrame.this,
                                        "Failed to run the dot utility.\n"+
                                        "Make sure dot is installed properly\n"+
                                        "and in the system PATH.\n"+
                                        "You can run dot manually on the dot file\n"+
                                        "'"+dotFile.getPath()+"'\n\n"+
                                        e.getMessage(),
                                        "jRegExAnalyser: Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                }
            }).start();
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(
                    MainFrame.this,
                    "Failed to run the dot utility.\n"+
                    "Make sure dot is installed properly\n"+
                    "and in the system PATH.\n"+
                    "You can run dot manually on the dot file\n"+
                    "'"+dotFile.getPath()+"'\n\n"+
                    e.getMessage(),
                    "jRegExAnalyser: Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPatternDotButtonActionPerformed

    private void jNewButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jNewButtonActionPerformed
        MainFrame mainFrame = new MainFrame();
        Rectangle r = getBounds();
        mainFrame.setBounds(r.x+50, r.y+25, r.width, r.height);
        mainFrame.setVisible(true);
        mainFrame.startBackgroundTasks();
    }//GEN-LAST:event_jNewButtonActionPerformed

    private void jImportRecentButtonActionPerformed(java.awt.event.ActionEvent evt) 
    {//GEN-FIRST:event_jImportRecentButtonActionPerformed
        Rectangle b = jImportRecentButton.getBounds();
        jRecentTestTextPopup.show(jImportRecentButton, 0, b.height);
    }//GEN-LAST:event_jImportRecentButtonActionPerformed



    private void jMatchTraceTableListSelectionChanged(ListSelectionEvent e) 
    {
        textHighlighter.removeAllHighlights();
        int row = e.getLastIndex();
        TableModel tableModel = jMatchTraceTable.getModel();
        if (Statistics.TraceTableModel.class.isInstance(tableModel)) {
            Statistics.TraceTableModel ttm = (Statistics.TraceTableModel) tableModel;
            MatchTest mt = ttm.getMatchTest(row);
            if (mt == null) {
                return;
            }
            DefaultTreeModel tm = (DefaultTreeModel) jCompiledPatternTree.getModel();
            jTestText.setCaretPosition(mt.getPos());
            try {
                textHighlighter.addHighlight(0, mt.getPos(), posHighlightPainter);
            } catch (BadLocationException ex) {
                // ignore
            }
            jCompiledPatternTree.setSelectionPath(
                    new TreePath(
                    tm.getPathToRoot(
                    lastResult.patternNodeMap.get(
                    mt.getNode()))));
        }
    }

    @Override
    public void dispose() 
    {
        super.dispose();
        --instanceCount;
        
        // enqueue background matcher shutdown task
        try 
        {
            backgroundMatching.clear();
            backgroundMatching.putFirst(RegExWorker.Task.SHUTDOWN);
        }
        catch (InterruptedException e) 
        {
            System.out.println("warning: unexpected interrupt in enqueue");
        }
        
        if (instanceCount == 0) 
        {
            System.exit(0);
        }
    }

    



    // number of windows
    }
