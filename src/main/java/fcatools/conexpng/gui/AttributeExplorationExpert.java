package fcatools.conexpng.gui;

import static fcatools.conexpng.Util.addMenuItem;
import static fcatools.conexpng.Util.clamp;
import static fcatools.conexpng.Util.invokeAction;
import static fcatools.conexpng.Util.mod;
import static fcatools.conexpng.Util.showMessageDialog;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.scroll.WebScrollPane;

import de.tudresden.inf.tcs.fcaapi.FCAImplication;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.AbstractExpert;
import de.tudresden.inf.tcs.fcalib.FullObject;
import de.tudresden.inf.tcs.fcalib.action.CounterExampleProvidedAction;
import de.tudresden.inf.tcs.fcalib.action.QuestionConfirmedAction;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Util;
import fcatools.conexpng.gui.contexteditor.ContextMatrix;
import fcatools.conexpng.gui.contexteditor.ContextMatrixModel;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FormalContext;

public class AttributeExplorationExpert extends AbstractExpert<String, String, FullObject<String, String>> {

    private WebFrame frame;
    private Conf state;
    private FormalContext context;

    public AttributeExplorationExpert(WebFrame mainFrame, Conf state) {
        this.state = state;
        this.context = state.context;
        this.frame = mainFrame;
    }

    @Override
    public void explorationFinished() {
        showMessageDialog(frame, LocaleHandler.getString("AttributeExplorationExpert.explorationFinished"), false);
    }

    @Override
    public void askQuestion(final FCAImplication<String> question) {
        new SwingWorker<Object, Object>() {
            // to make sure, that the context was changed
            int objCount = state.context.getObjectCount();

            @Override
            protected Object doInBackground() throws Exception {
                showQuestionDialog(question);
                return null;
            }

            protected void done() {
                if (objCount != state.context.getObjectCount())
                    state.contextChanged();
            };
        }.execute();
    }

    @Override
    public void counterExampleInvalid(FullObject<String, String> counterexample, int reason) {
        if (reason == COUNTEREXAMPLE_INVALID) {
            showMessageDialog(
                    frame,
                    counterexample.getIdentifier()
                            + LocaleHandler.getString("AttributeExplorationExpert.counterExampleInvalid.reasonInvalid"),
                    true);
        }
        if (reason == COUNTEREXAMPLE_EXISTS) {
            showMessageDialog(
                    frame,
                    counterexample.getIdentifier()
                            + LocaleHandler.getString("AttributeExplorationExpert.counterExampleInvalid.reasonExists"),
                    true);
        }

    }

    @Override
    public void requestCounterExample(FCAImplication<String> implication) {
        showCounterExampleDialog(implication);
    }

    @Override
    public void forceToCounterExample(FCAImplication<String> implication) {
        // nothing todo
    }

    @Override
    public void implicationFollowsFromBackgroundKnowledge(FCAImplication<String> arg0) {
        // nothing todo
    }

    private void showQuestionDialog(final FCAImplication<String> question) {

        String questionstring = question.getPremise().isEmpty() ? LocaleHandler
                .getString("AttributeExplorationExpert.showQuestionDialog.questionObjHaveAttr")
                + getElements(question.getConclusion()) + "?" : LocaleHandler
                .getString("AttributeExplorationExpert.showQuestionDialog.questionObjHasAttr.1")
                + getElements(question.getPremise())
                + LocaleHandler.getString("AttributeExplorationExpert.showQuestionDialog.questionObjHasAttr.2")
                + getElements(question.getConclusion()) + "?";
        Object[] options = { LocaleHandler.getString("yes"), LocaleHandler.getString("no"),
                LocaleHandler.getString("AttributeExplorationExpert.showQuestionDialog.options.stopAttrExploration") };
        final WebOptionPane optionPane = new WebOptionPane(questionstring, WebOptionPane.QUESTION_MESSAGE,
                WebOptionPane.YES_NO_CANCEL_OPTION);
        optionPane.setOptions(options);
        final WebDialog dialog = new WebDialog(frame,
                LocaleHandler.getString("AttributeExplorationExpert.showQuestionDialog.dialog"), true);

        dialog.setContentPane(optionPane);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (dialog.isVisible() && (e.getSource() == optionPane)
                        && (e.getPropertyName().equals(WebOptionPane.VALUE_PROPERTY))) {
                    dialog.setVisible(false);
                }
            }
        });
        dialog.pack();
        Util.centerDialogInsideMainFrame(frame, dialog);
        dialog.setVisible(true);
        String n = (String) optionPane.getValue();
        if (n.equals(LocaleHandler.getString("yes"))) {
            QuestionConfirmedAction<String, String, FullObject<String, String>> action = new QuestionConfirmedAction<>();
            action.setQuestion(question);
            action.setContext(context);
            fireExpertAction(action);
        } else if (n.equals(LocaleHandler.getString("no"))) {
            requestCounterExample(question);
        } else {
            explorationFinished();
        }
    }

    private String getElements(Set<String> set) {
        String result = "";
        for (String string : set) {
            result += string + ", ";
        }
        result = result.substring(0, result.lastIndexOf(","));
        return result;
    }

    private void showCounterExampleDialog(FCAImplication<String> question) {
        MiniContextEditor mce = new MiniContextEditor(question);
        final WebOptionPane pane = new WebOptionPane(mce, WebOptionPane.YES_NO_CANCEL_OPTION);
        pane.setMessageType(WebOptionPane.PLAIN_MESSAGE);
        final WebDialog dialog = new WebDialog(frame,
                LocaleHandler.getString("AttributeExplorationExpert.showCounterExampleDialog.dialog"), true);
        pane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (dialog.isVisible() && (e.getSource() == pane)
                        && (e.getPropertyName().equals(WebOptionPane.VALUE_PROPERTY))) {
                    dialog.setVisible(false);
                }
            }
        });
        dialog.setContentPane(pane);
        Object[] options = {
                LocaleHandler
                        .getString("AttributeExplorationExpert.showCounterExampleDialog.options.provideCounterexample"),
                LocaleHandler
                        .getString("AttributeExplorationExpert.showCounterExampleDialog.options.acceptImplication"),
                LocaleHandler.getString("stop") };
        pane.setOptions(options);
        dialog.pack();
        Util.centerDialogInsideMainFrame(frame, dialog);
        dialog.setVisible(true);
        String n = (String) pane.getValue();
        if (n != null)
            if (n.equals(LocaleHandler
                    .getString("AttributeExplorationExpert.showCounterExampleDialog.options.provideCounterexample"))) {
                state.saveConf();
                CounterExampleProvidedAction<String, String, FullObject<String, String>> action = new CounterExampleProvidedAction<>(
                        context, question, mce.getCounterexample());
                fireExpertAction(action);
                state.getContextEditorUndoManager().makeRedoable();
            } else if (n.equals(LocaleHandler
                    .getString("AttributeExplorationExpert.showCounterExampleDialog.options.acceptImplication"))) {
                QuestionConfirmedAction<String, String, FullObject<String, String>> action = new QuestionConfirmedAction<>();
                action.setQuestion(question);
                action.setContext(context);
                fireExpertAction(action);
            } else {
                explorationFinished();
            }
    }

    // ////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("serial")
    private class MiniContextEditor extends WebPanel {
        Conf mcestate;
        final ContextMatrixModel matrixModel;
        final ContextMatrix matrix;

        // Context menus
        final WebPopupMenu cellPopupMenu;
        final WebPopupMenu objectCellPopupMenu;
        final WebPopupMenu attributeCellPopupMenu;

        int lastActiveRowIndex;
        int lastActiveColumnIndex;

        public MiniContextEditor(FCAImplication<String> question) {
            cellPopupMenu = new WebPopupMenu();
            objectCellPopupMenu = new WebPopupMenu();
            attributeCellPopupMenu = new WebPopupMenu();

            mcestate = new Conf();
            mcestate.context = new FormalContext();
            mcestate.context.addAttributes(context.getAttributes());
            matrixModel = new ContextMatrixModel(mcestate);
            matrix = new ContextMatrix(matrixModel, mcestate.guiConf.columnWidths);
            try {
                mcestate.context.addObject(new FullObject<String, String>("obj" + context.getObjectCount(), question
                        .getPremise()));
            } catch (IllegalObjectException e) {
                // should never happen, because the context is empty
                e.printStackTrace();
            }
            WebScrollPane scrollPane = matrix.createStripedJScrollPane(getBackground());
            // Only the height of 60 is important
            scrollPane.setPreferredSize(new Dimension(100, 60));

            setLayout(new BorderLayout(0, 10));
            add(new WebLabel(
                    LocaleHandler
                            .getString("AttributeExplorationExpert.MiniContextEditor.MiniContextEditor.WabLabel.1")
                            + question), BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int i = matrix.rowAtPoint(e.getPoint());
                    int j = matrix.columnAtPoint(e.getPoint());
                    int clicks = e.getClickCount();
                    if (clicks >= 2 && clicks % 2 == 0 && SwingUtilities.isLeftMouseButton(e)) { // Double
                                                                                                 // Click
                        if (i > 0 && j > 0) {
                            invokeAction(MiniContextEditor.this, new ToggleAction(i, j));
                        } else if (i == 1 && j == 0)
                            matrix.renameRowHeader(i);
                    }
                }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }
            };
            matrix.addMouseListener(mouseAdapter);
            matrix.addMouseMotionListener(mouseAdapter);
            matrixModel.fireTableStructureChanged();
            registerActions();
            createKeyActions();
            createContextMenuActions();
        }

        private void maybeShowPopup(MouseEvent e) {
            int i = matrix.rowAtPoint(e.getPoint());
            int j = matrix.columnAtPoint(e.getPoint());
            lastActiveRowIndex = i;
            lastActiveColumnIndex = j;
            if (e.isPopupTrigger()) {
                if (i == 0 && j == 0) {
                    // Don't show a context menu in the matrix corner
                } else if (i > 0 && j > 0) {
                    if (matrix.getSelectedColumn() <= 0 || matrix.getSelectedRow() <= 0) {
                        matrix.selectCell(i, j);
                    }
                    cellPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else if (j == 0) {
                    objectCellPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    attributeCellPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        private void registerActions() {
            ActionMap am = matrix.getActionMap();
            am.put("up", new MoveAction(0, -1));
            am.put("down", new MoveAction(0, +1));
            am.put("left", new MoveAction(-1, 0));
            am.put("right", new MoveAction(+1, 0));
            am.put("upCarry", new MoveWithCarryAction(0, -1));
            am.put("downCarry", new MoveWithCarryAction(0, +1));
            am.put("leftCarry", new MoveWithCarryAction(-1, 0));
            am.put("rightCarry", new MoveWithCarryAction(+1, 0));
            am.put("toggle", new ToggleActiveAction());
        }

        private void createKeyActions() {
            InputMap im = matrix.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(getKeyStroke(KeyEvent.VK_UP, 0), "up");
            im.put(getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
            im.put(getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
            im.put(getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
            im.put(getKeyStroke(KeyEvent.VK_K, 0), "upCarry");
            im.put(getKeyStroke(KeyEvent.VK_J, 0), "downCarry");
            im.put(getKeyStroke(KeyEvent.VK_H, 0), "leftCarry");
            im.put(getKeyStroke(KeyEvent.VK_L, 0), "rightCarry");
            im.put(getKeyStroke(KeyEvent.VK_ENTER, 0), "toggle");
            im.put(getKeyStroke(KeyEvent.VK_T, 0), "toggle");
        }

        private void createContextMenuActions() {
            // ------------------------
            // Inner cells context menu
            // ------------------------
            // See issue #42
            /*
             * addMenuItem(cellPopupMenu, "Cut", new CutAction());
             * addMenuItem(cellPopupMenu, "Copy", new CopyAction());
             * addMenuItem(cellPopupMenu, "Paste", new PasteAction());
             */
            addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.selectAll"),
                    new SelectAllAction());
            // --------
            cellPopupMenu.add(new WebPopupMenu.Separator());
            // --------
            addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.fill"),
                    new FillAction());
            addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.clear"),
                    new ClearAction());
            addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.invert"),
                    new InvertAction());

            // ------------------------
            // Object cell context menu
            // ------------------------
            addMenuItem(
                    objectCellPopupMenu,
                    LocaleHandler
                            .getString("AttributeExplorationExpert.MiniContextEditor.createContextMenuActions.renameRowHeader"),
                    new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    matrix.renameRowHeader(lastActiveRowIndex);
                }
            });
        }

        class ToggleAction extends AbstractAction {
            int i, j;

            ToggleAction(int i, int j) {
                this.i = i;
                this.j = j;
            }

            public void actionPerformed(ActionEvent e) {
                if (i <= 0 || j <= 0)
                    return;
                int i = clamp(this.i, 1, mcestate.context.getObjectCount()) - 1;
                int j = clamp(this.j, 1, mcestate.context.getAttributeCount()) - 1;
                mcestate.context.toggleAttributeForObject(mcestate.context.getAttributeAtIndex(j), mcestate.context
                        .getObjectAtIndex(i).getIdentifier());
                matrix.saveSelection();
                matrixModel.fireTableDataChanged();
                matrix.restoreSelection();
                mcestate.contextChanged();
            }
        }

        class MoveAction extends AbstractAction {
            int horizontal, vertical;

            MoveAction(int horizontal, int vertical) {
                this.horizontal = horizontal;
                this.vertical = vertical;
            }

            public void actionPerformed(ActionEvent e) {
                if (matrix.isRenaming)
                    return;
                lastActiveRowIndex = clamp(lastActiveRowIndex + vertical, 1, state.context.getObjectCount());
                lastActiveColumnIndex = clamp(lastActiveColumnIndex + horizontal, 1, state.context.getAttributeCount());
                matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
            }
        }

        class MoveWithCarryAction extends AbstractAction {
            int horizontal, vertical;

            MoveWithCarryAction(int horizontal, int vertical) {
                this.horizontal = horizontal;
                this.vertical = vertical;
            }

            public void actionPerformed(ActionEvent e) {
                if (matrix.isRenaming)
                    return;
                if (state.context.getObjectCount() == 0 || state.context.getAttributeCount() == 0)
                    return;
                int i = lastActiveRowIndex + vertical - 1;
                int j = lastActiveColumnIndex + horizontal - 1;
                // noinspection LoopStatementThatDoesntLoop
                while (true) {
                    if (i < 0) {
                        j -= 1;
                        i = 0;
                        break;
                    }
                    if (j < 0) {
                        i -= 1;
                        j = state.context.getAttributeCount() - 1;
                    }
                    if (i >= 1) {
                        j += 1;
                        i = 0;
                        break;
                    }
                    if (j >= state.context.getAttributeCount()) {
                        i += 1;
                        j = 0;
                    }
                    break;
                }
                i = mod(i, 1);
                j = mod(j, state.context.getAttributeCount());
                lastActiveRowIndex = i + 1;
                lastActiveColumnIndex = j + 1;
                matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
            }
        }

        class ToggleActiveAction extends AbstractAction {
            public void actionPerformed(ActionEvent e) {
                if (matrix.isRenaming)
                    return;
                invokeAction(MiniContextEditor.this, new ToggleAction(lastActiveRowIndex, lastActiveColumnIndex));
            }
        }

        class SelectAllAction extends AbstractAction {
            public void actionPerformed(ActionEvent e) {
                if (matrix.isRenaming)
                    return;
                matrix.selectAll();
            }
        }

        abstract class AbstractFillClearInvertAction extends AbstractAction {
            public void actionPerformed(ActionEvent e) {
                if (matrix.isRenaming)
                    return;
                int i1 = matrix.getSelectedRow() - 1;
                int i2 = i1 + matrix.getSelectedRowCount();
                int j1 = matrix.getSelectedColumn() - 1;
                int j2 = j1 + matrix.getSelectedColumnCount();
                matrix.saveSelection();
                execute(i1, i2, j1, j2);
                matrixModel.fireTableDataChanged();
                matrix.restoreSelection();
                state.contextChanged();
            }

            abstract void execute(int i1, int i2, int j1, int j2);
        }

        class FillAction extends AbstractFillClearInvertAction {
            void execute(int i1, int i2, int j1, int j2) {
                state.context.fill(i1, i2, j1, j2);
            }
        }

        class ClearAction extends AbstractFillClearInvertAction {
            void execute(int i1, int i2, int j1, int j2) {
                state.context.clear(i1, i2, j1, j2);
            }
        }

        class InvertAction extends AbstractFillClearInvertAction {
            void execute(int i1, int i2, int j1, int j2) {
                state.context.invert(i1, i2, j1, j2);
            }
        }

        public FullObject<String, String> getCounterexample() {
            return mcestate.context.getObjectAtIndex(0);
        }
    }

}
