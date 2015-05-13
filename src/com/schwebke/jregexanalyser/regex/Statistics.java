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
 * $Id: Statistics.java 597 2011-12-27 07:51:03Z kai $
 */

package com.schwebke.jregexanalyser.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class Statistics 
{

    public Statistics(int from, int to, int pos, CharSequence seq) 
    {
        this.from = from;
        this.to = to;
        this.pos = pos;
        this.seq = seq;
        level = 0;
        trace = new ArrayList<MatchTest>();
        first = -1;
    }

    public int getLevel() 
    {
        return level;
    }

    public void incLevel() 
    {
        ++level;
    }

    public void decLevel() 
    {
        --level;
    }

    public void setResult(int first, int last) 
    {
        this.first = first;
        this.last = last;
    }

    public int getFirst() 
    {
        return first;
    }

    public int getLast() 
    {
        return last;
    }

    public List<MatchTest> getTrace() 
    {
        return trace;
    }

    public static class Match 
    {
        public Match(int i, int j, int from, int to, CharSequence cs) 
        {
            this.i = i;
            this.j = j;
            this.from = from;
            this.to = to;
            this.cs = cs;
        }

        @Override
        public String toString() 
        {
            if (j == 0) 
            {
                return String.format("match %d", i);
            }
            return String.format("group %d", j);
        }

        public String getText() 
        {
            return cs.subSequence(from, to).toString();
        }

        public int getFrom() 
        {
            return from;
        }

        public int getTo() 
        {
            return to;
        }

        private final int i;
        private final int j;
        private final int from;
        private final int to;
        private final CharSequence cs;
    }

    /**
     * Result of a find() or match() operation.
     * Contains the tree of matched result, the text created from replacement
     * and the list created from splitting.
     */
    public static class TreeResult 
    {
        TreeResult(TreeNode t, StringBuffer r, ArrayList<String> s) 
        {
            tree = t;
            repl = r;
            split = s;
        }
        public final TreeNode tree;
        public final StringBuffer repl;
        public final ArrayList<String> split;
    }

    /**
     * helper function to perform multiple find() operations with the supplied
     * matcher and create a TreeResult.
     */
    public static TreeResult findAsTree(Matcher m, String replace, int splitLimit) 
    {
        DefaultMutableTreeNode r = new DefaultMutableTreeNode("root");
        
        StringBuffer repl = new StringBuffer();

        ArrayList<String> split = new ArrayList<String>();
        int splitIndex = 0;
        boolean splitMatchLimited = splitLimit > 0;

        int i = 0;
        while (m.find()) 
        {
            ++i;
            DefaultMutableTreeNode match = new DefaultMutableTreeNode(new Match(i, 0, m.start(), m.end(), m.text));
            r.add(match);

            int j;
            for (j = 1; j <= m.groupCount(); ++j) 
            {
                if (m.start(j) >= 0) 
                {
                    DefaultMutableTreeNode group = new DefaultMutableTreeNode(
                            new Match(i, j, m.start(j), m.end(j), m.text));
                    match.add(group);
                }
            }

            // replace handling
            try 
            {
                m.appendReplacement(repl, replace);
            } catch (IndexOutOfBoundsException e) 
            {
                // ignore
            }

            // split handling
            if (!splitMatchLimited || split.size() < splitLimit - 1) 
            {
                String splitPart = m.text.subSequence(splitIndex, m.start()).toString();
                split.add(splitPart);
                splitIndex = m.end();
            } 
            else if (split.size() == splitLimit - 1) 
            { // last one
                String splitPart = m.text.subSequence(splitIndex,
                                                 m.text.length()).toString();
                split.add(splitPart);
                splitIndex = m.end();
            }
        }

        // replace handling
        m.appendTail(repl);

        // split handling
        //    If no match was found, return this
        if (splitIndex == 0) 
        {
            split.add(m.text.toString());
        }

        //    Add remaining segment
        if (!splitMatchLimited || split.size() < splitLimit) 
        {
            split.add(m.text.subSequence(splitIndex, m.text.length()).toString());
        }

        //    trim list
        int splitResultSize = split.size();
        if (splitLimit == 0) 
        {
            while (splitResultSize > 0 && split.get(splitResultSize-1).equals("")) 
            {
                splitResultSize--;
            }
            while (split.size() > splitResultSize) 
            {
                split.remove(split.size()-1);
            }
        }

        r.setUserObject(String.format("%d %s", i, (i==1)?("match"):("matches")));
        return new TreeResult(r, repl, split);
    }

    /**
     * Helper function to perform a match() operation with the supplied matcher.
     * The tree result will contain a match tree (with one match entry, if any),
     * and a replacement text result, but no split result list.
     */
    public static TreeResult matchAsTree(Matcher m, String replace) 
    {
        StringBuffer repl = new StringBuffer();
        if (m.matches()) 
        {
            DefaultMutableTreeNode r = new DefaultMutableTreeNode(new Match(1, 0, m.start(), m.end(), m.text));
            int j;
            for (j = 1; j <= m.groupCount(); ++j) 
            {
                DefaultMutableTreeNode group = new DefaultMutableTreeNode(new Match(0, j, m.start(j), m.end(j), m.text));
                r.add(group);
            }
            m.appendReplacement(repl, replace);
            return new TreeResult(r, repl, null);
        } 
        else 
        {
            m.appendTail(repl);
            return new TreeResult(new DefaultMutableTreeNode("no match"), repl, null);
        }
    }

    static DefaultMutableTreeNode getPatternAsTree(Pattern.Node root,Map<Object, DefaultMutableTreeNode> nodeMap)
    {
        DefaultMutableTreeNode r = new DefaultMutableTreeNode(getNodeClassName(root.getClass()));
        nodeMap.put(root, r);
        for (Pattern.Node c : root.getChilds()) 
        {
            if (c != null) 
            {
                if (!nodeMap.containsKey(c)) 
                {
                    r.add(getPatternAsTree(c, nodeMap));
                }
            }
        }
        return r;
    }

    @SuppressWarnings("rawtypes")
	static String getPatternAsDot(Pattern.Node root) 
    {
        StringBuilder b = new StringBuilder();

        
        // Instance Name generated from uniquely numbered Class
        Map<Pattern.Node, String> nameMap = new HashMap<Pattern.Node, String>();
        Map<Class, Integer> nameId = new HashMap<Class, Integer>();

        b.append("digraph Pattern {\n");
        b.append("   graph [bgcolor=\"transparent\"];\n");
        b.append("   node [fillcolor=\"#ffffff\" style=\"filled\"];\n");
        String pName = String.format("%s", getNodeClassName(root.getClass()));
        nameMap.put(root, pName);
        Set<Pattern.Node> processedNodes = new HashSet<Pattern.Node>();
        getPatternAsDot(root, b, pName, nameMap, nameId, processedNodes);

        for (Pattern.Node n : processedNodes) 
        {
            try 
            {
                String info = n.getInfo();
                if (info != null) 
                {
                    info = info.replace("\"", "\\\"");
                    info = info.replace(Integer.toString(Integer.MAX_VALUE), "*");
                    b.append("   ");
                    b.append(nameMap.get(n));
                    b.append("[label=\"");
                    b.append(nameMap.get(n));
                    b.append("\\n");
                    b.append(info);
                    b.append("\"];\n");
                }
            } catch (Exception e) 
            {
                // just in case there are format/conversion bugs in getInfo()
                System.out.println("warning: unexpected exception in Node.getInfo(): "+e.getMessage());
            }
        }

        b.append("}\n");


        return b.toString();
    }

    static void getPatternAsDot(Pattern.Node n, StringBuilder b,
            String pName,
            Map<Pattern.Node, String> nameMap,
            @SuppressWarnings("rawtypes") Map<Class, Integer> nameId,
            Set<Pattern.Node> processedNodes)
    {
        if (processedNodes.contains(n)) 
        {
            return;
        }
        processedNodes.add(n);
        for (Pattern.Node c : n.getChilds()) 
        {
            if (c != null) {
                String cName = "?";
                if (!nameMap.containsKey(c)) 
                {
                    int id = 1;
                    if (!nameId.containsKey(c.getClass())) 
                    {
                        nameId.put(c.getClass(), id);
                    }
                    else 
                    {
                        id = nameId.get(c.getClass());
                        ++id;
                        nameId.put(c.getClass(), id);
                    }
                    cName = String.format("%s_%d", getNodeClassName(c.getClass()), id);
                    if (cName.equals("Node_1")) {
                        cName = "End";
                    }
                    cName = cName.replace("$", "_");
                    cName = cName.replaceAll("^(\\d)", "N$1");
                    nameMap.put(c, cName);
                } 
                else 
                {
                    cName = nameMap.get(c);
                }
                String style = "";
                if (cName.equals("End") || pName.equals("Start")) 
                {
                    style = " [style = dotted]";
                }
                b.append(String.format("   %s -> %s%s;\n",
                        pName, cName, style));
                getPatternAsDot(c, b, cName, nameMap, nameId, processedNodes);
            }
        }
    }

    static String getNodeClassName(@SuppressWarnings("rawtypes") Class nc) 
    {
        return nc.getName().replace(
            Pattern.class.getName()+"$", "");
    }

    MatchTest newMatchTest(Pattern.Node node, int pos) 
    {
        MatchTest m = new MatchTest(node, getLevel(), pos);
        trace.add(m);
        return m;
    }

    public static class MatchTest 
    {
        private MatchTest(Pattern.Node node, int level, int pos) 
        {
            this.node = node;
            this.level = level;
            this.pos = pos;
        }

        public void setResult(boolean result) 
        {
            this.result = result;
        }

        public String getIndent() 
        {
            StringBuilder r = new StringBuilder();
            int i;
            for (i = 0; i < level; ++i) 
            {
                r.append("   ");
            }
            return r.toString();
        }

        @Override
        public String toString() 
        {
            return String.format("%8d%s%s = %s",
                    pos,
                    getIndent(),
                    getNodeClassName(),
                    (result)?("true"):("false"));
        }

        public String getNodeClassName() 
        {
            return Statistics.getNodeClassName(node.getClass());
        }

        public Object getNode() 
        {
            return node;
        }

        public int getLevel() 
        {
            return level;
        }

        public boolean getResult() 
        {
            return result;
        }

        public int getPos() 
        {
            return pos;
        }

        private final Pattern.Node node;
        private final int level;
        private boolean result;
        private final int pos;
    }
    
    @SuppressWarnings("serial")
	public static class TraceTableModel extends AbstractTableModel 
	{
        
        public TraceTableModel(List<Statistics> statistics) 
        {
            this.statistics = statistics;
            
            rows = 0;
            offset = new int[statistics.size()+1];
            int i = 0;
            for (Statistics s : statistics) 
            {
                offset[i++] = rows;
                List<MatchTest> t = s.getTrace();
                rows += t.size();
            }
            offset[i] = rows;
        }

        public int getRowCount() 
        {
            return rows;
        }

        public int getColumnCount() 
        {
            // Trace | Op | Level | NodeClassName | Pos | Result
            return 6;
        }

        @Override
        public String getColumnName(int columnIndex) 
        {
            switch (columnIndex) 
            {
                case 0:
                    return "Trace";

                case 1:
                    return "Op";

                case 2:
                    return "Level";

                case 3:
                    return "NodeClass";

                case 4:
                    return "Pos";

                case 5:
                    return "Res";
            }
            return "?";
        }

        public int getTraceIndex(int rowIndex) 
        {
            int i = 0;
            while ((i < offset.length) && (offset[i] <= rowIndex)) 
            {
                ++i;
            }
            --i;
            return i;
        }

        public MatchTest getMatchTest(int rowIndex) 
        {
            int i = getTraceIndex(rowIndex);
            if ((i >= 0) && (i < statistics.size())) 
            {
                List<MatchTest> t = statistics.get(i).getTrace();
                return t.get(rowIndex-offset[i]);
            }
            return null;
        }

        public Object getValueAt(int rowIndex, int columnIndex) 
        {
            int i = getTraceIndex(rowIndex);
            MatchTest mt = getMatchTest(rowIndex);
            switch (columnIndex) 
            {
                case 0:
                    return new Integer(i+1);

                case 1:
                    return new Integer(rowIndex-offset[i]+1);

                case 2:
                    return new Integer(mt.getLevel());

                case 3:
                    return mt.getNodeClassName();

                case 4:
                    return new Integer(mt.getPos());

                case 5:
                    return mt.getResult()?"T":"F";
            }
            return "?";
        }

        private List<Statistics> statistics;
        private int[] offset;
        private int rows;
    }

    static TableModel getTraceAsTable(List<Statistics> statistics) 
    {
        return new TraceTableModel(statistics);
    }



    private List<MatchTest> trace;
    private int level;
    @SuppressWarnings("unused")
	private final CharSequence seq;
    @SuppressWarnings("unused")
	private int from;
    @SuppressWarnings("unused")
	private int to;
    @SuppressWarnings("unused")
	private int pos;
    private int first;
    private int last;
}
