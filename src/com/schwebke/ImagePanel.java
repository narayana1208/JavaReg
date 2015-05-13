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
 * $Id: ImagePanel.java 595 2011-12-27 06:46:29Z kai $
 */

package com.schwebke;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Scrollable;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel implements Scrollable, MouseMotionListener
{
    public static final int MAX_ZOOM = 4;
    public static final int MIN_ZOOM = -8;

    public ImagePanel(File imgFile) throws IOException {
        super();
        this.imgFile = imgFile;
        image = ImageIO.read(imgFile);
        zoomFactor = 0;
        updateScale();
        while (image.getHeight()*scale > 550.0) {
            --zoomFactor;
            scale = Math.pow(ZOOM_STEP, zoomFactor);
        }
        scaledImage = image.getScaledInstance(
                (int)(image.getWidth()*scale),
                (int)(image.getHeight()*scale),
                Image.SCALE_AREA_AVERAGING);
        preferredSize = new Dimension(450, 560);
        setAutoscrolls(true);
        addMouseMotionListener(this);
    }

    public void zoomIn() {
        ++zoomFactor;
        zoomFactor = Math.min(zoomFactor, MAX_ZOOM);
        updateScale();
    }

    public void zoomOut() {
        --zoomFactor;
        zoomFactor = Math.max(zoomFactor, MIN_ZOOM);
        updateScale();
    }

    public void updateScale() {
        scale = Math.pow(ZOOM_STEP, zoomFactor);
        scaledImage = image.getScaledInstance(
                (int)(image.getWidth()*scale),
                (int)(image.getHeight()*scale),
                Image.SCALE_AREA_AVERAGING);
        revalidate();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(scaledImage.getWidth(null), scaledImage.getHeight(null));
    }

    @Override
    public void paintComponent(Graphics _g) {
        super.paintComponent(_g);

        Graphics2D g = (Graphics2D)_g;
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        //g.scale(scale, scale);
        g.drawImage(scaledImage, 0, 0, null);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return preferredSize;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void mouseDragged(MouseEvent e) {
        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    public void mouseMoved(MouseEvent e) {
    }

    @SuppressWarnings("unused")
	private File imgFile;
    private BufferedImage image;
    private Image scaledImage;
    private Dimension preferredSize;

    private int zoomFactor;
    private double scale;
    private static final double ZOOM_STEP = Math.sqrt(2.0);

}
