//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
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

package jd.gui.skins.simple.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import jd.config.ConfigContainer;
import jd.config.ConfigEntry;
import jd.gui.skins.simple.components.BrowseFile;
import jd.gui.skins.simple.components.JDTextArea;
import jd.gui.skins.simple.components.JDTextField;
import jd.gui.skins.simple.components.JLinkButton;
import jd.gui.skins.simple.config.panels.PremiumPanel;
import jd.utils.JDUtilities;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

/**
 * Diese Klasse fasst ein label / input Paar zusammen und macht das lesen und
 * schreiben einheitlich. Es lassen sich so Dialogelemente Automatisiert
 * einfügen.
 */
public class GUIConfigEntry extends JXPanel implements ActionListener, ChangeListener, PropertyChangeListener, DocumentListener {

    private static final long serialVersionUID = -1391952049282528582L;
    private static final String DEBUG = "";
    private static final String INPUT_WIDTH = "300!";
    private static final String INPUT_WIDTH_SMALL = "100!";
    private static final String GAPRIGHT = "gapright 10";
    private ConfigEntry configEntry;

    /**
     * Die input Komponente
     */

    private JComponent[] input;
    private JComponent[] decoration;
    // private Insets insets = new Insets(1, 5, 1, 5);

    // private JComponent left;

    protected Logger logger = JDUtilities.getLogger();

    // private JComponent right;

    // private JComponent total;

    /**
     * Erstellt einen neuen GUIConfigEntry
     * 
     * @param type
     *            TypID z.B. GUIConfigEntry.TYPE_BUTTON
     * @param propertyInstance
     *            Instanz einer propertyklasse (Extends property).
     * @param propertyName
     *            Name der Eigenschaft
     * @param label
     *            Label
     */
    GUIConfigEntry(ConfigEntry cfg) {
        configEntry = cfg;
        cfg.setGuiListener(this);
        this.addPropertyChangeListener(cfg);
        setLayout(new MigLayout(DEBUG + "ins 0", "[]10[grow,fill," + INPUT_WIDTH + "]"));
        // this.setBorder(BorderFactory.createEtchedBorder());
        input = new JComponent[1];
        decoration = new JComponent[1];

        switch (configEntry.getType()) {

        case ConfigContainer.TYPE_LINK:

            try {
                input[0] = new JLinkButton(configEntry.getLabel(), new URL(configEntry.getPropertyName()));

            } catch (MalformedURLException e) {
                input[0] = new JLabel(configEntry.getPropertyName());
                e.printStackTrace();
            }
            // addInstantHelpLink();
            input[0].setEnabled(configEntry.isEnabled());
            add(input[0]);

            break;

        case ConfigContainer.TYPE_PASSWORDFIELD:
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][grow 100,fill," + INPUT_WIDTH + ",right]"));
            add(decoration[0] = new JLabel(configEntry.getLabel()), "gapleft 5");
            add(input[0] = new JPasswordField(), GAPRIGHT);
            PlainDocument doc = (PlainDocument) ((JPasswordField) input[0]).getDocument();
            doc.addDocumentListener(this);
            // input[0].setMaximumSize(new Dimension(160,20));
            input[0].setEnabled(configEntry.isEnabled());
            ((JPasswordField) input[0]).setHorizontalAlignment(SwingConstants.RIGHT);

            break;

        case ConfigContainer.TYPE_TEXTFIELD:
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][grow 100,fill," + INPUT_WIDTH + ",right]"));
            add(decoration[0] = new JLabel(configEntry.getLabel()), "gapleft 5");
            add(input[0] = new JDTextField(), GAPRIGHT);
            doc = (PlainDocument) ((JDTextField) input[0]).getDocument();
            doc.addDocumentListener(this);
            input[0].setEnabled(configEntry.isEnabled());
            ((JDTextField) input[0]).setHorizontalAlignment(SwingConstants.RIGHT);

            break;
        case ConfigContainer.TYPE_TEXTAREA:
            setLayout(new MigLayout(DEBUG + "ins 0,wrap 1", "[fill, grow]", "[fill,grow]0[fill,grow]"));
            add(decoration[0] = new JLabel(configEntry.getLabel()), "gapleft 5");
            add(new JScrollPane(input[0] = new JDTextArea()), "width 400::800,height 30::450");
            input[0].setEnabled(configEntry.isEnabled());
            doc = (PlainDocument) ((JDTextArea) input[0]).getDocument();
            doc.addDocumentListener(this);

            // this.setLayout(new BorderLayout());
            // this.add(left = new
            // JLabel(configEntry.getLabel()),BorderLayout.NORTH);
            // // JDUtilities.addToGridBag(this, left = new
            // JLabel(configEntry.getLabel()), 0, 0, 1, 1, 0, 0, insets,
            // GridBagConstraints.NONE, GridBagConstraints.WEST);
            // //// addInstantHelpLink();
            // input[0] = new JTextArea();
            // input[0].setEnabled(configEntry.isEnabled());
            // doc = (PlainDocument) ((JTextArea) input[0]).getDocument();
            // doc.addDocumentListener(this);
            // this.add(new JScrollPane(input[0]),BorderLayout.CENTER);
            // //JDUtilities.addToGridBag(this, total = new
            // JScrollPane(input[0]), 0, 1, 3, 1, 1, 1, insets,
            // GridBagConstraints.BOTH, GridBagConstraints.EAST);
            // // total.setMinimumSize(new Dimension(200, 200));
            // total = null;
            break;
        case ConfigContainer.TYPE_CHECKBOX:
            setLayout(new MigLayout(DEBUG + "ins 0"));
            // logger.info("ADD CheckBox");

            // JDUtilities.addToGridBag(this,, 0, 0, 1, 1, 0, 0, insets,
            // GridBagConstraints.NONE, GridBagConstraints.WEST);
            // addInstantHelpLink();
            input[0] = new JCheckBox();
            input[0].setEnabled(configEntry.isEnabled());
            ((JCheckBox) input[0]).addChangeListener(this);
            this.add(input[0]);
            this.add(decoration[0] = new JLabel(configEntry.getLabel()));
            break;
        case ConfigContainer.TYPE_BROWSEFILE:
            // logger.info("ADD Browser");
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][fill]"));
            if (configEntry.getLabel().trim().length() > 0) add(new JLabel(configEntry.getLabel()));
            input[0] = new BrowseFile();
            ((BrowseFile) input[0]).setEnabled(configEntry.isEnabled());

            ((BrowseFile) input[0]).setEditable(true);
            add(input[0], "gapleft 5,alignx right," + GAPRIGHT);
            break;
        case ConfigContainer.TYPE_BROWSEFOLDER:
            // logger.info("ADD BrowserFolder");
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][fill]"));
            if (configEntry.getLabel().trim().length() > 0) add(decoration[0] = new JLabel(configEntry.getLabel()));
            input[0] = new BrowseFile();

            ((BrowseFile) input[0]).setEditable(true);
            ((BrowseFile) input[0]).setEnabled(configEntry.isEnabled());
            ((BrowseFile) input[0]).setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            add(input[0], "gapleft 5,alignx right," + GAPRIGHT);
            break;
        case ConfigContainer.TYPE_SPINNER:
            // logger.info("ADD Spinner");
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][grow 100,fill," + INPUT_WIDTH_SMALL + ",right]"));
            add(decoration[0] = new JLabel(configEntry.getLabel()), "gapleft 5");
            input[0] = new JSpinner(new SpinnerNumberModel(configEntry.getStart(), configEntry.getStart(), configEntry.getEnd(), configEntry.getStep()));
            input[0].setEnabled(configEntry.isEnabled());
            ((JSpinner) input[0]).addChangeListener(this);
            // ((JSpinner)input[0])
            add(input[0], "dock east," + GAPRIGHT);
            break;
        case ConfigContainer.TYPE_BUTTON:
            // //logger.info("ADD Button");
            input[0] = new JButton(configEntry.getLabel());
            ((JButton) input[0]).addActionListener(this);
            ((JButton) input[0]).addActionListener(configEntry.getActionListener());
            input[0].setEnabled(configEntry.isEnabled());
            add(input[0]);
            break;
        case ConfigContainer.TYPE_COMBOBOX:
        case ConfigContainer.TYPE_COMBOBOX_INDEX:
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow 10][grow 100,fill," + INPUT_WIDTH + ",right]"));
            this.add(new JLabel(configEntry.getLabel()), "gapleft 5");
            // JDUtilities.addToGridBag(this, left = new
            // JLabel(configEntry.getLabel()), 0, 0, 1, 1, 0, 0, insets,
            // GridBagConstraints.NONE, GridBagConstraints.WEST);
            // addInstantHelpLink();
            // logger.info(configEntry.getLabel());
            // logger.info("ADD Combobox");
            input[0] = new JComboBox(configEntry.getList());
            ((JComboBox) input[0]).addActionListener(this);
            for (int i = 0; i < configEntry.getList().length; i++) {

                if (configEntry.getList()[i].equals(configEntry.getPropertyInstance().getProperty(configEntry.getPropertyName()))) {
                    ((JComboBox) input[0]).setSelectedIndex(i);

                    break;
                }
            }
            input[0].setEnabled(configEntry.isEnabled());
            add(input[0], "alignx right," + GAPRIGHT);

            break;
        case ConfigContainer.TYPE_RADIOFIELD:
            // //logger.info("ADD Radio");
            input = new JComponent[configEntry.getList().length];
            JRadioButton radio;
            // addInstantHelpLink();
            ButtonGroup group = new ButtonGroup();

            for (int i = 0; i < configEntry.getList().length; i++) {
                radio = new JRadioButton(configEntry.getList()[i].toString());
                ((JRadioButton) input[0]).addActionListener(this);
                radio.setActionCommand(configEntry.getList()[i].toString());
                input[i] = radio;
                input[i].setEnabled(configEntry.isEnabled());
                // Group the radio buttons.

                group.add(radio);

                Object p = configEntry.getPropertyInstance().getProperty(configEntry.getPropertyName());
                if (p == null) {
                    p = "";
                }

                if (configEntry.getList()[i].toString().equals(p.toString())) {
                    radio.setSelected(true);

                }
                add(input[i]);
            }
            break;
        case ConfigContainer.TYPE_PREMIUMPANEL:

            input[0] = new PremiumPanel(this);
            input[0].setEnabled(configEntry.isEnabled());

            // JDUtilities.addToGridBag(this, total = new JScrollPane(input[0]),
            // 0, 1, 3, 1, 1, 1, insets, GridBagConstraints.BOTH,
            // GridBagConstraints.EAST);
            add(input[0]);

        case ConfigContainer.TYPE_LABEL:
            setLayout(new MigLayout(DEBUG + "ins 0", "[fill, grow]"));
            add(decoration[0] = new JLabel(configEntry.getLabel()), "gapleft 5,spanx");
            break;
        case ConfigContainer.TYPE_SEPARATOR:
            // //logger.info("ADD Seperator");
            input[0] = new JSeparator(SwingConstants.HORIZONTAL);
            add(input[0], "spanx");

            break;

        }
        this.firePropertyChange(getConfigEntry().getPropertyName(), null, getText());
    }

    public JComponent[] getInput() {
        return input;
    }

    public void actionPerformed(ActionEvent e) {
        getConfigEntry().valueChanged(getText());

    }

    // private void addInstantHelpLink() {
    // // JDUtilities.addToGridBag(this, new JLabel("HELP"), 1, 0, 1, 1, 1, 0,
    // // insets, GridBagConstraints.NONE, GridBagConstraints.WEST);
    // if (configEntry.getInstantHelp() != null) {
    // try {
    // String url = configEntry.getInstantHelp();
    // JLinkButton link = new JLinkButton("", new
    // ImageIcon(JDImage.getImage(JDTheme
    // .V("gui.images.help")).getScaledInstance(20, 20, Image.SCALE_FAST)), new
    // URL(url));
    // JDUtilities.addToGridBag(this, link, 1, 0, 1, 1, 1, 0, insets,
    // GridBagConstraints.NONE, GridBagConstraints.WEST);
    //
    // } catch (MalformedURLException e) {
    // JDUtilities.addToGridBag(this, new JLabel(configEntry.getInstantHelp()),
    // 1, 0, 1, 1, 1, 0, insets, GridBagConstraints.NONE,
    // GridBagConstraints.WEST);
    //
    // }
    // } else {
    // JDUtilities.addToGridBag(this, new JLabel(""), 1, 0, 1, 1, 1, 0, insets,
    // GridBagConstraints.NONE, GridBagConstraints.WEST);
    //
    // }
    //
    // }

    public void changedUpdate(DocumentEvent e) {
        getConfigEntry().valueChanged(getText());

    }

    public ConfigEntry getConfigEntry() {
        return configEntry;
    }

    /**
     * Gibt den zusstand der Inputkomponente zurück
     * 
     * @return
     */
    public Object getText() {
        // //logger.info(configEntry.getType()+"_2");
        switch (configEntry.getType()) {
        case ConfigContainer.TYPE_LINK:
            return ((JLinkButton) input[0]).getLinkURL().toString();
        case ConfigContainer.TYPE_PASSWORDFIELD:
            return new String(((JPasswordField) input[0]).getPassword());
        case ConfigContainer.TYPE_TEXTFIELD:
            return ((JDTextField) input[0]).getText();
        case ConfigContainer.TYPE_TEXTAREA:
            return ((JDTextArea) input[0]).getText();
        case ConfigContainer.TYPE_CHECKBOX:
            return ((JCheckBox) input[0]).isSelected();
        case ConfigContainer.TYPE_PREMIUMPANEL:
            return ((PremiumPanel) input[0]).getAccounts();
        case ConfigContainer.TYPE_BUTTON:
            return null;
        case ConfigContainer.TYPE_COMBOBOX:
            return ((JComboBox) input[0]).getSelectedItem();
        case ConfigContainer.TYPE_COMBOBOX_INDEX:
            return ((JComboBox) input[0]).getSelectedIndex();
        case ConfigContainer.TYPE_LABEL:
            return null;
        case ConfigContainer.TYPE_RADIOFIELD:
            JRadioButton radio;
            for (JComponent element : input) {

                radio = (JRadioButton) element;

                if (radio.getSelectedObjects() != null && radio.getSelectedObjects()[0] != null) { return radio.getSelectedObjects()[0]; }
            }
            return null;
        case ConfigContainer.TYPE_SEPARATOR:
            return null;
        case ConfigContainer.TYPE_BROWSEFOLDER:
        case ConfigContainer.TYPE_BROWSEFILE:
            return ((BrowseFile) input[0]).getText();
        case ConfigContainer.TYPE_SPINNER:
            return ((JSpinner) input[0]).getValue();
        }

        return null;
    }

    public void insertUpdate(DocumentEvent e) {
        getConfigEntry().valueChanged(getText());
    }

    public void propertyChange(PropertyChangeEvent evt) {

        if (input[0] == null) { return; }
        // logger.info("New Value "+evt.getNewValue());
        if (getConfigEntry().isConditionalEnabled(evt)) {
            input[0].setEnabled(true);
            for (JComponent i : input) {
                if (i != null) i.setEnabled(true);
            }

            for (JComponent i : decoration) {
                if (i != null) i.setEnabled(true);
            }

        } else {
            for (JComponent i : input) {
                if (i != null) i.setEnabled(false);
            }
            for (JComponent i : decoration) {
                if (i != null) i.setEnabled(false);
            }
        }
    }

    public void removeUpdate(DocumentEvent e) {
        getConfigEntry().valueChanged(getText());
    }

    public void setConfigEntry(ConfigEntry configEntry) {
        this.configEntry = configEntry;
    }

    /**
     * Setz daten ind ei INput Komponente
     * 
     * @param text
     */
    public void setData(Object text) {
        if (text == null && configEntry.getDefaultValue() != null) {
            text = configEntry.getDefaultValue();
        }
        // //logger.info(configEntry.getDefaultValue()+" - "+text + "
        // "+input.length+" -
        // "+input[0]);
        switch (configEntry.getType()) {
        case ConfigContainer.TYPE_LINK:
            try {
                ((JLinkButton) input[0]).setLinkURL(new URL(text == null ? "" : text.toString()));
            } catch (MalformedURLException e1) {

                e1.printStackTrace();
            }
            break;
        case ConfigContainer.TYPE_PASSWORDFIELD:
            ((JPasswordField) input[0]).setText(text == null ? "" : text.toString());
            break;
        case ConfigContainer.TYPE_TEXTFIELD:
            ((JDTextField) input[0]).setText(text == null ? "" : text.toString());
            break;
        case ConfigContainer.TYPE_TEXTAREA:
            ((JDTextArea) input[0]).setText(text == null ? "" : text.toString());
            break;
        case ConfigContainer.TYPE_PREMIUMPANEL:
            ((PremiumPanel) input[0]).setAccounts(text);
            break;
        case ConfigContainer.TYPE_CHECKBOX:
            if (text == null) {
                text = false;
            }
            try {
                ((JCheckBox) input[0]).setSelected((Boolean) text);
            } catch (Exception e) {
                logger.severe("Falcher Wert: " + text);
                ((JCheckBox) input[0]).setSelected(false);
            }
            break;
        case ConfigContainer.TYPE_BUTTON:
            break;
        case ConfigContainer.TYPE_COMBOBOX:
            ((JComboBox) input[0]).setSelectedItem(text);
            break;
        case ConfigContainer.TYPE_COMBOBOX_INDEX:
            if (text instanceof Integer) {
                ((JComboBox) input[0]).setSelectedIndex((Integer) text);
            } else {
                ((JComboBox) input[0]).setSelectedItem(text);
            }
            break;
        case ConfigContainer.TYPE_LABEL:
            break;
        case ConfigContainer.TYPE_BROWSEFOLDER:
        case ConfigContainer.TYPE_BROWSEFILE:
            ((BrowseFile) input[0]).setText(text == null ? "" : text.toString());
            break;
        case ConfigContainer.TYPE_SPINNER:
            int value = text instanceof Integer ? (Integer) text : Integer.parseInt(text.toString());
            try {

                value = Math.min((Integer) ((SpinnerNumberModel) ((JSpinner) input[0]).getModel()).getMaximum(), value);
                value = Math.max((Integer) ((SpinnerNumberModel) ((JSpinner) input[0]).getModel()).getMinimum(), value);
                ((JSpinner) input[0]).setModel(new SpinnerNumberModel(value, configEntry.getStart(), configEntry.getEnd(), configEntry.getStep()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case ConfigContainer.TYPE_RADIOFIELD:
            for (int i = 0; i < configEntry.getList().length; i++) {
                JRadioButton radio = (JRadioButton) input[i];
                if (radio.getActionCommand().equals(text)) {
                    radio.setSelected(true);
                } else {
                    radio.setSelected(false);
                }
            }

        case ConfigContainer.TYPE_SEPARATOR:

            break;
        }
        getConfigEntry().valueChanged(getText());
    }

    public void stateChanged(ChangeEvent e) {
        getConfigEntry().valueChanged(getText());
    }

}
