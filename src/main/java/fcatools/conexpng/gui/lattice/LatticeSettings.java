package fcatools.conexpng.gui.lattice;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.alee.extended.panel.WebAccordion;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;

import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FormalContext;

/**
 * This class implements the accordion menu of the lattice tab.
 * 
 */
public class LatticeSettings extends WebAccordion {

    /**
     *
     */
    private static final long serialVersionUID = 3981827958628799515L;
    private Conf state;
    private FormalContext context;
    private List<WebCheckBox> attributeCheckBoxes;
    private List<WebCheckBox> objectCheckBoxes;

    public LatticeSettings(Conf state) {
        this.state = state;
        this.context = state.context;
        this.attributeCheckBoxes = new ArrayList<>();
        this.objectCheckBoxes = new ArrayList<>();
        this.addPane(0, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.0"), getLatticePanel());
        this.addPane(1, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.1"), getObjectPanel());
        this.addPane(2, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.2"), getAttributePanel());
    }

    private WebScrollPane getLatticePanel() {
        WebPanel panel = new WebPanel(new BorderLayout());
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(getLatticeObjectPanel(), gbc);

        gbc.gridx = 1;

        panel.add(getLatticeAttrPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new WebLabel(LocaleHandler.getString("LatticeSettings.getLatticePanel.WebLabel.1")), gbc);
        final WebRadioButton noneEdges = new WebRadioButton(
                LocaleHandler.getString("LatticeSettings.getLatticePanel.noneEdges"));
        gbc.gridy++;
        final WebRadioButton showEdges = new WebRadioButton(
                LocaleHandler.getString("LatticeSettings.getLatticePanel.showEdges"));
        // select if edges shall be displayed based on GUIConf
        if (state.guiConf.showEdges) {
            showEdges.setSelected(true);
        } else {
            noneEdges.setSelected(true);
        }
        noneEdges.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showEdges = false;
                noneEdges.setSelected(true);
                showEdges.setSelected(false);
                state.showLabelsChanged();
            }
        });
        showEdges.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showEdges = true;
                showEdges.setSelected(true);
                noneEdges.setSelected(false);
                state.showLabelsChanged();
            }
        });

        panel.add(noneEdges, gbc);
        gbc.gridx = 1;
        panel.add(showEdges, gbc);

        return new WebScrollPane(panel);
    }

    // this methode places object's radiobuttons
    private WebPanel getLatticeObjectPanel() {
        WebPanel panelObjects = new WebPanel(new BorderLayout());
        panelObjects.setLayout(new GridBagLayout());
        GridBagConstraints gbo = new GridBagConstraints();
        gbo.anchor = GridBagConstraints.WEST;
        gbo.gridx = 0;
        gbo.gridy = 1;
        panelObjects
                .add(new WebLabel(LocaleHandler.getString("LatticeSettings.getLatticeObjectPanel.WebLabel.1")), gbo);
        gbo.gridy = 2;
        final WebRadioButton noneObjects = new WebRadioButton();
        noneObjects.setText(LocaleHandler.getString("LatticeSettings.getLatticeObjectPanel.noneObjects"));

        final WebRadioButton labelsObjects = new WebRadioButton();
        labelsObjects.setText(LocaleHandler.getString("LatticeSettings.getLatticeObjectPanel.labelsObjects"));

        // select if edges shall be displayed based on GUIConf
        if (state.guiConf.showObjectLabel) {
            labelsObjects.setSelected(true);
        } else {
            noneObjects.setSelected(true);
        }

        noneObjects.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showObjectLabel = false;
                noneObjects.setSelected(true);
                labelsObjects.setSelected(false);
                state.showLabelsChanged();
            }
        });
        labelsObjects.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showObjectLabel = true;
                labelsObjects.setSelected(true);
                noneObjects.setSelected(false);
                state.showLabelsChanged();
            }
        });
        panelObjects.add(noneObjects, gbo);
        gbo.gridy = 3;
        panelObjects.add(labelsObjects, gbo);
        return panelObjects;
    }

    // this methode places attributes' radiobuttons
    private WebPanel getLatticeAttrPanel() {
        WebPanel panelAttributes = new WebPanel(new BorderLayout());
        panelAttributes.setLayout(new GridBagLayout());
        GridBagConstraints gba = new GridBagConstraints();
        gba.anchor = GridBagConstraints.WEST;
        gba.gridx = 0;
        gba.gridy = 1;
        panelAttributes.add(new WebLabel(LocaleHandler.getString("LatticeSettings.getLatticeAttrPanel.WebLabel.1")),
                gba);
        gba.gridy = 2;
        final WebRadioButton noneAttributes = new WebRadioButton();
        noneAttributes.setText(LocaleHandler.getString("LatticeSettings.getLatticeAttrPanel.noneAttributes"));
        final WebRadioButton labelsAttributes = new WebRadioButton();
        labelsAttributes.setText(LocaleHandler.getString("LatticeSettings.getLatticeAttrPanel.labelsAttributes"));

        // select if edges shall be displayed based on GUIConf
        if (state.guiConf.showAttributeLabel) {
            labelsAttributes.setSelected(true);
        } else {
            noneAttributes.setSelected(true);
        }

        noneAttributes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showAttributeLabel = false;
                noneAttributes.setSelected(true);
                labelsAttributes.setSelected(false);
                state.showLabelsChanged();
            }
        });
        labelsAttributes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                state.guiConf.showAttributeLabel = true;
                labelsAttributes.setSelected(true);
                noneAttributes.setSelected(false);
                state.showLabelsChanged();
            }
        });
        panelAttributes.add(noneAttributes, gba);
        gba.gridy = 3;
        panelAttributes.add(labelsAttributes, gba);
        return panelAttributes;
    }

    // creates a jpanel including checkbox for each attribute
    private WebScrollPane getAttributePanel() {
        WebPanel panel = new WebPanel(new BorderLayout());
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (String s : context.getAttributes()) {
            gbc.gridy++;
            final WebCheckBox box = new WebCheckBox(s);
            box.setSelected(!state.context.getDontConsideredAttr().contains(box.getText()));
            box.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (!box.isSelected()) {
                        state.context.dontConsiderAttribute(box.getText());
                    } else {
                        state.context.considerAttribute(box.getText());
                    }
                    state.temporaryContextChanged();
                }
            });

            panel.add(box, gbc);
            attributeCheckBoxes.add(box);
        }
        WebScrollPane sp = new WebScrollPane(panel);
        return sp;
    }

    // creates a jpanel including checkbox for each object
    private WebScrollPane getObjectPanel() {
        WebPanel panel = new WebPanel(new BorderLayout());
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (FullObject<String, String> s : context.getObjects()) {
            gbc.gridy++;
            final WebCheckBox box = new WebCheckBox(s.getIdentifier());
            final FullObject<String, String> temp = s;
            box.setSelected(!state.context.getDontConsideredObj().contains(temp));
            box.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (!box.isSelected()) {
                        state.context.dontConsiderObject(temp);
                    } else {
                        state.context.considerObject(temp);
                    }
                    state.temporaryContextChanged();
                }
            });

            panel.add(box, gbc);
            objectCheckBoxes.add(box);
        }
        WebScrollPane sp = new WebScrollPane(panel);
        return sp;
    }

    /**
     * Updates the panels.
     * 
     * @param state
     */
    public void update(Conf state) {
        this.removePane(0);
        this.addPane(0, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.0"), getLatticePanel());
        this.context = state.context;
        this.removePane(1);
        objectCheckBoxes.clear();
        this.addPane(1, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.1"), getObjectPanel());
        this.removePane(2);
        attributeCheckBoxes.clear();
        this.addPane(2, LocaleHandler.getString("LatticeSettings.LatticeSettings.pane.2"), getAttributePanel());
    }
}
