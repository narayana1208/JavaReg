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
 * $Id: RegExWorker.java 597 2011-12-27 07:51:03Z kai $
 */

package com.schwebke.jregexanalyser;

import com.schwebke.jregexanalyser.regex.*;
import com.schwebke.jregexanalyser.regex.Statistics.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Helper to perform matching and analyzing.
 * @author kai
 */
@SuppressWarnings("unused")
public class RegExWorker implements Runnable 
{

    public RegExWorker(BlockingDeque<Task> taskQueue, Publisher publisher) 
    {
        this.taskQueue = taskQueue;
        this.publisher = publisher;
    }

    private final BlockingDeque<Task> taskQueue;
    private final Publisher publisher;

    public void run() {
        while (true) {
            try {
                Task t = taskQueue.takeFirst();
                if (t.mode.equals(Task.SHUTDOWN.mode)) {
                    return;
                }
                Result r = process(t);
                publisher.publish(r);
            } catch (InterruptedException e) {
                System.out.println("warning: unexpected interrupt in dequeue");
            } catch (Exception e) {
                System.out.println("warning: other unexpected exception ("+e.getMessage()+")");
            }
        }
    }


    public static Result process(Task t) 
    {
        Date start = new Date();

        // match the test text
        Matcher m = t.p.matcher(t.text);

        // create tree model and a map to hold mapping between match trace and compiled pattern tree nodes
        DefaultTreeModel compiledPatternTreeModel = null;
        Map<Object, DefaultMutableTreeNode> patternNodeMap = new HashMap<Object, DefaultMutableTreeNode>();

        // create tree models for compiled pattern tree and match result trees,
        // depending on the operation
        Statistics.TreeResult res = null;
        if (t.mode.equals("find")) 
        {
            int splitLimit = t.splitLimit;
            compiledPatternTreeModel = new DefaultTreeModel(t.p.getFindPatternTree(patternNodeMap));
            res = Statistics.findAsTree(m, t.replace, splitLimit);
        }
        if (t.mode.equals("match")) 
        {
            compiledPatternTreeModel = new DefaultTreeModel(t.p.getMatchPatternTree(patternNodeMap));
            res = Statistics.matchAsTree(m, t.replace);
        }
        DefaultTreeModel matchResultTreeModel = new DefaultTreeModel(res.tree);

        Date stop = new Date();
        
        return new Result(
                m.getStatistics(),
                compiledPatternTreeModel,
                patternNodeMap,
                matchResultTreeModel,
                m.getTraceAsTable(),
                res,
                t.p.getFindPatternDot(),
                t.p.getMatchPatternDot(),
                start, stop);
    }

    public final static class Task 
    {
        
        public final static Task SHUTDOWN = new Task("shutdown");
        
        public Task(String mode) 
        {
            this.p = null;
            this.text = null;
            this.replace = null;
            this.mode = mode;
            this.splitLimit = 0;
        }
        
        public Task(Pattern p, String text, String replace, String mode, int splitLimit) 
        {
            this.p = p;
            this.text = text;
            this.replace = replace;
            this.mode = mode;
            this.splitLimit = splitLimit;
        }

        public final Pattern p;
        public final String text;
        public final String replace;
        public final String mode;
        public final int splitLimit;
    }

    public final static class Result 
    {
        public Result(List<Statistics> s,
                      DefaultTreeModel compiledPatternTreeModel,
                      Map<Object, DefaultMutableTreeNode> patternNodeMap,
                      DefaultTreeModel matchResultTreeModel,
                      TableModel traceTableModel,
                      Statistics.TreeResult res,
                      String findPatternDot,
                      String matchPatternDot,
                      Date start, Date stop)
        {
            this.s = s;
            this.compiledPatternTreeModel = compiledPatternTreeModel;
            this.patternNodeMap = patternNodeMap;
            this.matchResultTreeModel = matchResultTreeModel;
            this.traceTableModel = traceTableModel;
            this.res = res;
            this.findPatternDot = findPatternDot;
            this.matchPatternDot = matchPatternDot;
            this.start = start;
            this.stop = stop;
        }
        public final List<Statistics> s;
        public final DefaultTreeModel compiledPatternTreeModel;
        public final Map<Object, DefaultMutableTreeNode> patternNodeMap;
        public final DefaultTreeModel matchResultTreeModel;
        public final TableModel traceTableModel;
        public final Statistics.TreeResult res;
        public final String findPatternDot;
        public final String matchPatternDot;
        public final Date start;
        public final Date stop;
    }

    public static interface Publisher 
    {
        void publish(Result r);
    }

}
