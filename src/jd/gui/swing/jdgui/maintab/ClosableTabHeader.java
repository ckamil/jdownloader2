//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.gui.swing.jdgui.maintab;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jd.gui.swing.jdgui.views.ClosableView;
import net.miginfocom.swing.MigLayout;

public class ClosableTabHeader extends JPanel {

    private static final long serialVersionUID = 4463352125800695922L;

    public ClosableTabHeader(ClosableView view) {
        setLayout(new MigLayout("ins 0", "[grow,fill]", "[16!]"));

        putClientProperty("paintActive", Boolean.TRUE);

        JLabel label = new JLabel(view.getTitle());
        label.setIcon(view.getIcon());
        label.setOpaque(false);

        add(label);
        add(view.getCloseButton(), "dock east, hidemode 3,gapleft 5,height 16!, width 16!");
        setOpaque(false);
    }
}
