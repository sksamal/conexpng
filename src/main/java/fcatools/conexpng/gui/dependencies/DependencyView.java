package fcatools.conexpng.gui.dependencies;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebTextPane;

import de.tudresden.inf.tcs.fcaapi.FCAImplication;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.ContextChangeEvent;
import fcatools.conexpng.gui.StatusBarPropertyChangeListener;
import fcatools.conexpng.gui.View;
import fcatools.conexpng.gui.workers.AssociationWorker;
import fcatools.conexpng.gui.workers.ImplicationWorker;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.AssociationRule;

public class DependencyView extends View {

    private static final long serialVersionUID = -6377834669097012170L;

    private WebTextPane implpane = new WebTextPane();

    private WebTextPane assopane = new WebTextPane();

    private SimpleAttributeSet[] attrs;

    private SimpleAttributeSet header;

    private final int NON_ZERO_SUPPORT_EXACT_RULE = 0;

    private final int INEXACT_RULE = 1;

    private final int ZERO_SUPPORT_EXACT_RULE = 2;

    private final String FOLLOW = " ==> ";

    private final String END_MARK = ";";

    private final String EOL = System.getProperty("line.separator");

    private DependencySettings settings;

    public DependencyView(final Conf state) {
        super(state);

        attrs = new SimpleAttributeSet[3];
        attrs[NON_ZERO_SUPPORT_EXACT_RULE] = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs[NON_ZERO_SUPPORT_EXACT_RULE], Color.blue);

        attrs[ZERO_SUPPORT_EXACT_RULE] = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs[ZERO_SUPPORT_EXACT_RULE], Color.red);

        attrs[INEXACT_RULE] = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs[INEXACT_RULE], new Color(0, 128, 0));

        header = new SimpleAttributeSet();
        StyleConstants.setFontSize(header, 12);

        implpane.setEditable(false);
        assopane.setEditable(false);

        final WebScrollPane scroll2 = new WebScrollPane(assopane);
        scroll2.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                state.guiConf.assoScrollPos = scroll2.getVerticalScrollBar().getValue();
            }
        });

        final WebScrollPane scroll1 = new WebScrollPane(implpane);
        scroll1.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                state.guiConf.implScrollPos = scroll1.getVerticalScrollBar().getValue();
            }
        });
        final WebSplitPane assoImplSplitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, scroll1, scroll2);
        assoImplSplitPane.setOneTouchExpandable(true);
        assoImplSplitPane.setContinuousLayout(true);
        assoImplSplitPane.addDividerListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                state.guiConf.assoImplSplitPanePos = assoImplSplitPane.getDividerLocation();
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }
        });
        view = assoImplSplitPane;

        settingsPanel = new WebPanel(new BorderLayout());

        settings = new DependencySettings(state.guiConf);
        settingsPanel.add(settings, BorderLayout.NORTH);
        settingsPanel.getComponent(0).addPropertyChangeListener(this);
        settingsPanel.setMinimumSize(new Dimension(190, 400));
        toolbar = null;
        super.init();
        setSplitPanePositions();
        this.splitPane.addDividerListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent arg0) {
            }

            @Override
            public void componentResized(ComponentEvent arg0) {
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                state.guiConf.dependenciesSettingsSplitPos = DependencyView.this.splitPane.getDividerLocation();
            }

            @Override
            public void componentHidden(ComponentEvent arg0) {
            }
        });
    }

    /**
     * Set split pane divider positions.
     */
    private void setSplitPanePositions() {
        ((WebSplitPane) view).setDividerLocation(state.guiConf.assoImplSplitPanePos);
        this.splitPane.setDividerLocation(state.guiConf.dependenciesSettingsSplitPos);
    }

    /**
     * Updates the GUI after a new GUIConf is loaded.
     */
    public void updateGUI() {
        // support and confidence
        settings.setSliderValues();
        // lexical or support sorting
        settings.setSorting();
        // split pane divider positions
        setSplitPanePositions();
        // implication and association pane scroll positions don't need to be
        // updated, they are updated when calculating
    }

    public SimpleAttributeSet dependencyStyle(double support, double confidence) {
        if (confidence == 1.0) {
            return support > 0 ? attrs[NON_ZERO_SUPPORT_EXACT_RULE] : attrs[ZERO_SUPPORT_EXACT_RULE];
        }
        return attrs[INEXACT_RULE];
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof ContextChangeEvent) {
            switch (((ContextChangeEvent) evt).getName()) {
            case CANCELCALCULATIONS: {
                if (associationWorker != null && !associationWorker.isDone())
                    associationWorker.cancel(true);
                if (implicationWorker != null && !implicationWorker.isDone())
                    implicationWorker.cancel(true);
                return;
            }
            case NEWCONTEXT: {
                state.associations = new TreeSet<>();
                state.implications = new HashSet<>();
                implpane.clear();
                assopane.clear();
                updateAssociationsLater = true;
                updateImplicationsLater = true;
                ((DependencySettings) settingsPanel.getComponent(0)).setGuiConf(state.guiConf);
                break;
            }
            case CONTEXTCHANGED: {
                state.associations = new TreeSet<>();
                state.implications = new HashSet<>();
                implpane.clear();
                assopane.clear();
                updateAssociationsLater = true;
                updateImplicationsLater = true;
                break;
            }
            case LOADEDFILE: {
                if (state.associations != null && !state.associations.isEmpty())
                    writeAssociations(state.guiConf.assoScrollPos);
                else
                    updateAssociationsLater = true;
                if (state.implications != null && !state.implications.isEmpty())
                    writeImplications(state.guiConf.implScrollPos);
                else
                    updateImplicationsLater = true;
                ((DependencySettings) settingsPanel.getComponent(0)).setGuiConf(state.guiConf);
                break;
            }
            default:
                break;
            }
        }
        if (isVisible() && updateAssociationsLater) {
            updateAssociationsLater = false;
            state.associations = new TreeSet<AssociationRule>();
            updateAssociations();
        }
        if (isVisible() && updateImplicationsLater) {
            updateImplicationsLater = false;
            state.implications = new HashSet<FCAImplication<String>>();
            updateImplications();
        }

        if (evt.getPropertyName().equals("ConfidenceChanged")) {
            writeAssociations(0);
        } else if (evt.getPropertyName().equals("MinimalSupportChanged")) {
            updateAssociations();
        } else if (evt.getPropertyName().equals("ToggleSortingOrder")) {
            writeAssociations(0);
            writeImplications(0);
        }
    }

    // Associations
    // ///////////////////////////////////////////////////////////////////

    private boolean updateAssociationsLater = false;

    private AssociationWorker associationWorker;

    private void updateAssociations() {
        if (associationWorker != null && !associationWorker.isDone()) {
            associationWorker.cancel(true);
        }
        Long progressBarId = state.getStatusBar().startCalculation();
        associationWorker = new AssociationWorker(this, state, state.guiConf.support, 0, progressBarId);
        associationWorker.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state
                .getStatusBar()));
        state.getStatusBar().addCalculation(progressBarId, associationWorker);
        associationWorker.execute();
    }

    public void writeAssociations(final int assoscrollpos) {
        if (state.associations != null && !state.associations.isEmpty())
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int i = 0;
                    StringBuffer buf;
                    assopane.setText("");
                    ArrayList<AssociationRule> t = new ArrayList<>(state.associations);

                    if (!state.guiConf.lexSorting) {
                        Collections.sort(t, new Comparator<AssociationRule>() {
                            @Override
                            public int compare(AssociationRule o1, AssociationRule o2) {
                                int support1 = state.context.supportCount(o1.getPremise());
                                int support2 = state.context.supportCount(o2.getPremise());

                                if (support1 == support2)
                                    return (int) (o2.getConfidence() - o1.getConfidence());
                                else
                                    return support2 - support1;
                            }
                        });
                    }
                    try {
                        assopane.getDocument().insertString(assopane.getDocument().getLength(),
                                LocaleHandler.getString("DependencyView.writeAssociations.assopane.header"), header);
                        i = 0;
                        for (AssociationRule asso : t) {
                            buf = new StringBuffer();
                            if (asso.getConfidence() >= state.guiConf.confidence) {
                                i++;
                                buf.append(i);
                                buf.append("< " + state.context.supportCount(asso.getPremise()) + " > ");
                                buf.append(asso.getPremise()
                                        + " =["
                                        + new BigDecimal(asso.getConfidence()).setScale(3, BigDecimal.ROUND_HALF_UP)
                                                .doubleValue() + "]=> ");
                                Set<String> temp = new HashSet<>(asso.getConsequent());
                                temp.addAll(asso.getPremise());
                                buf.append("< " + state.context.supportCount(temp) + " > " + asso.getConsequent()
                                        + END_MARK);
                                buf.append(EOL);

                                assopane.getDocument().insertString(assopane.getDocument().getLength(), buf.toString(),
                                        dependencyStyle(asso.getSupport(), asso.getConfidence()));
                            }
                        }
                        ((WebScrollPane) ((WebSplitPane) view).getBottomComponent()).getVerticalScrollBar().setValue(
                                assoscrollpos);
                        ((DependencySettings) settingsPanel.getComponent(0)).update(i, state.associations.size());
                    } catch (BadLocationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
    }

    // Implications
    // ////////////////////////////////////////////////////////////////////////////////////////

    public void writeImplications(final int implscrollpos) {
        if (state.implications != null && !state.implications.isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int i = 0;
                    StringBuffer buf;
                    int support = 0;
                    implpane.setText("");
                    ArrayList<FCAImplication<String>> z = new ArrayList<>(state.implications);
                    if (!state.guiConf.lexSorting) {
                        Collections.sort(z, new Comparator<FCAImplication<String>>() {

                            @Override
                            public int compare(FCAImplication<String> o1, FCAImplication<String> o2) {

                                return Integer.compare(state.context.supportCount(o2.getPremise()),
                                        state.context.supportCount(o1.getPremise()));
                            }
                        });
                    }
                    try {
                        implpane.getDocument().insertString(implpane.getDocument().getLength(),
                                LocaleHandler.getString("DependencyView.writeImplications.implpane.header"), header);

                        for (FCAImplication<String> impl : z) {
                            support = state.context.supportCount(impl.getPremise());
                            buf = new StringBuffer();
                            buf.append(i);
                            buf.append("< " + support + " > ");
                            buf.append(impl.getPremise() + FOLLOW + impl.getConclusion() + END_MARK);
                            buf.append(EOL);
                            i++;
                            implpane.getDocument().insertString(implpane.getDocument().getLength(), buf.toString(),
                                    dependencyStyle(support, 1));
                        }
                        ((WebScrollPane) ((WebSplitPane) view).getTopComponent()).getVerticalScrollBar().setValue(
                                implscrollpos);
                    } catch (BadLocationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private ImplicationWorker implicationWorker;

    private boolean updateImplicationsLater = false;

    private void updateImplications() {
        if (implicationWorker != null && !implicationWorker.isDone()) {
            implicationWorker.cancel(true);
        }
        Long progressBarId = state.getStatusBar().startCalculation();
        implicationWorker = new ImplicationWorker(this, progressBarId);
        implicationWorker.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state
                .getStatusBar()));
        state.getStatusBar().addCalculation(progressBarId, implicationWorker);
        implicationWorker.execute();
    }
}
