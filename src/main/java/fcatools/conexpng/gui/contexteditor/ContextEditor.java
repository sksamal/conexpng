package fcatools.conexpng.gui.contexteditor;

import static fcatools.conexpng.Util.addMenuItem;
import static fcatools.conexpng.Util.clamp;
import static fcatools.conexpng.Util.createButton;
import static fcatools.conexpng.Util.createToggleButton;
import static fcatools.conexpng.Util.invokeAction;
import static fcatools.conexpng.Util.mod;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.menu.WebPopupMenu;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.ContextChangeEvent;
import fcatools.conexpng.ContextChangeEvents;
import fcatools.conexpng.gui.StatusBarPropertyChangeListener;
import fcatools.conexpng.gui.View;
import fcatools.conexpng.gui.workers.ClarificationReductionWorker;
import fcatools.conexpng.io.locale.LocaleHandler;

/**
 * The class responsible for displaying and interacting with ConExpNG's context
 * editor. The main component of this view is a customised JTable, that is more
 * akin to a spreadsheet editor, serving as our context editor.
 * 
 * Notes:
 * 
 * Generally, the code between ContextEditor and ContextMatrix is divided as per
 * the following guidelines:
 * 
 * - More general code is in ContextMatrix. - Code that also pertains to other
 * parts of the context editor (e.g. toolbar) other than the matrix is in
 * ContextEditor. - Code that needs to know about the MatrixModel specifically
 * and not only about AbstractTableModel is in ContextEditor as ContextMatrix
 * should not be coupled with a concrete model in order to have a seperation
 * between model and view.
 * 
 * E.g. PopupMenu code is in ContextEditor (and not in ContextMatrix) as for a
 * different Model one would probably use different PopupMenus, that means the
 * PopupMenus are coupled with MatrixModel.
 */
@SuppressWarnings("serial")
public class ContextEditor extends View {

    private static final long serialVersionUID = 1660117627650529212L;

    // Choose correct modifier key (STRG or CMD) based on platform
    private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    // Our JTable customisation and its respective data model
    private final ContextMatrix matrix;
    private final ContextMatrixModel matrixModel;
    // Context menus
    final WebPopupMenu cellPopupMenu;
    final WebPopupMenu objectCellPopupMenu;
    final WebPopupMenu attributeCellPopupMenu;

    WebToggleButton compactMatrixButton, showArrowRelationsButton;

    // For remembering which header cell has been right-clicked
    // For movement inside the matrix
    // Due to unfortunate implications of our JTable customisation we need to
    // rely on this "hack"
    int lastActiveRowIndex;
    int lastActiveColumnIndex;

    public ContextEditor(final Conf state) {
        super(state);
        setLayout(new BorderLayout());

        // Initialize various components
        cellPopupMenu = new WebPopupMenu();
        objectCellPopupMenu = new WebPopupMenu();
        attributeCellPopupMenu = new WebPopupMenu();
        matrixModel = new ContextMatrixModel(state);
        matrix = new ContextMatrix(matrixModel, state.guiConf.columnWidths);
        JScrollPane scrollPane = matrix.createStripedJScrollPane(getBackground());
        scrollPane.setBorder(new EmptyBorder(3, 3, 3, 3));
        toolbar.setFloatable(false);
        panel.setLayout(new BorderLayout());
        panel.add(toolbar, BorderLayout.WEST);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
        // Make context editor tighter
        setBorder(new EmptyBorder(3, -3, -4, -3));
        toolbar.setRound(4);

        // Add actions
        registerActions();
        createMouseActions();
        createKeyActions();
        createButtonActions();
        createContextMenuActions();
        scrollPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                invokeAction(ContextEditor.this, matrix.getActionMap().get("selectNone"));
                matrix.saveSelection();
            }
        });

        // Force an update of the table to display it correctly
        matrixModel.fireTableStructureChanged();
    }

    // If context is not changed through the context editor (e.g. by
    // exploration) be sure to reflect these changes inside the matrix
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e instanceof ContextChangeEvent) {
            ContextChangeEvent cce = (ContextChangeEvent) e;
            if (cce.getName() == ContextChangeEvents.CONTEXTCHANGED) {
                matrixModel.fireTableStructureChanged();
                matrix.invalidate();
                matrix.repaint();
                matrix.restoreSelection();
            } else if (cce.getName() == ContextChangeEvents.NEWCONTEXT
                    || cce.getName() == ContextChangeEvents.LOADEDFILE) {
                matrixModel.loadNewContext(state);
                matrix.loadColumnWidths(state.guiConf.columnWidths);
                matrixModel.fireTableStructureChanged();
                matrix.invalidate();
                matrix.repaint();
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Behaviour Initialization
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        am.put("gotoFirstObject", new MoveAction(0, -10000));
        am.put("gotoLastObject", new MoveAction(0, +10000));
        am.put("gotoFirstAttribute", new MoveAction(-10000, 0));
        am.put("gotoLastAttribute", new MoveAction(+10000, 0));
        am.put("moveObjectUp", new MoveObjectOrAttributeAction(0, -1));
        am.put("moveObjectDown", new MoveObjectOrAttributeAction(0, +1));
        am.put("moveAttributeLeft", new MoveObjectOrAttributeAction(-1, 0));
        am.put("moveAttributeRight", new MoveObjectOrAttributeAction(+1, 0));

        am.put("selectAll", new SelectAllAction());
        am.put("selectNone", new SelectNoneAction());
        am.put("selectUp", new ExpandSelectionAction(0, -1));
        am.put("selectDown", new ExpandSelectionAction(0, +1));
        am.put("selectLeft", new ExpandSelectionAction(-1, 0));
        am.put("selectRight", new ExpandSelectionAction(+1, 0));

        am.put("renameObject", new RenameActiveObjectAction());
        am.put("renameAttribute", new RenameActiveAttributeAction());
        am.put("removeObject", new RemoveActiveObjectAction());
        am.put("removeSelectedObjects", new RemoveSelectedObjectsAction());
        am.put("removeAttribute", new RemoveActiveAttributeAction());
        am.put("removeSelectedAttributes", new RemoveSelectedAttributesAction());
        am.put("addObjectBelow", new AddObjectAfterActiveAction());
        am.put("addObjectAbove", new AddObjectBeforeActiveAction());
        am.put("addObjectAtEnd", new AddObjectAtEndAction());
        am.put("addAttributeRight", new AddAttributeAfterActiveAction());
        am.put("addAttributeLeft", new AddAttributeBeforeActiveAction());
        am.put("addAttributeAtEnd", new AddAttributeAtEndAction());

        am.put("toggle", new ToggleActiveAction());
        am.put("invert", new InvertAction());
        am.put("fill", new FillAction());
        am.put("clear", new ClearAction());

        am.put("clarifyObjects", new ClarifyObjectsAction());
        am.put("clarifyAttributes", new ClarifyAttributesAction());
        am.put("reduceObjects", new ReduceObjectsAction());
        am.put("reduceAttributes", new ReduceAttributesAction());
        am.put("reduce", new ReduceAction());
        am.put("transpose", new TransposeAction());
        am.put("compact", new CompactAction());
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
        im.put(getKeyStroke(KeyEvent.VK_G, 0), "gotoFirstObject");
        im.put(getKeyStroke(KeyEvent.VK_G, KeyEvent.SHIFT_MASK), "gotoLastObject");
        im.put(getKeyStroke(KeyEvent.VK_0, 0), "gotoFirstAttribute");
        im.put(getKeyStroke(KeyEvent.VK_DOLLAR, 0), "gotoLastAttribute"); // Note:
                                                                          // VK_DOLLAR
                                                                          // does
                                                                          // not
                                                                          // work
                                                                          // on
                                                                          // OS
                                                                          // X
        im.put(getKeyStroke(KeyEvent.VK_UP, MASK), "moveObjectUp");
        im.put(getKeyStroke(KeyEvent.VK_DOWN, MASK), "moveObjectDown");
        im.put(getKeyStroke(KeyEvent.VK_LEFT, MASK), "moveAttributeLeft");
        im.put(getKeyStroke(KeyEvent.VK_RIGHT, MASK), "moveAttributeRight");
        im.put(getKeyStroke(KeyEvent.VK_K, MASK), "moveObjectUp");
        im.put(getKeyStroke(KeyEvent.VK_J, MASK), "moveObjectDown");
        im.put(getKeyStroke(KeyEvent.VK_H, MASK), "moveAttributeLeft");
        im.put(getKeyStroke(KeyEvent.VK_L, MASK), "moveAttributeRight");

        im.put(getKeyStroke(KeyEvent.VK_A, MASK), "selectAll");
        im.put(getKeyStroke(KeyEvent.VK_ESCAPE, 0), "selectNone");
        im.put(getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK), "selectUp");
        im.put(getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK), "selectDown");
        im.put(getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK), "selectLeft");
        im.put(getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK), "selectRight");
        im.put(getKeyStroke(KeyEvent.VK_K, KeyEvent.SHIFT_MASK), "selectUp");
        im.put(getKeyStroke(KeyEvent.VK_J, KeyEvent.SHIFT_MASK), "selectDown");
        im.put(getKeyStroke(KeyEvent.VK_H, KeyEvent.SHIFT_MASK), "selectLeft");
        im.put(getKeyStroke(KeyEvent.VK_L, KeyEvent.SHIFT_MASK), "selectRight");

        im.put(getKeyStroke(KeyEvent.VK_R, 0), "renameObject");
        im.put(getKeyStroke(KeyEvent.VK_R, KeyEvent.SHIFT_MASK), "renameAttribute");
        im.put(getKeyStroke(KeyEvent.VK_D, 0), "removeObject");
        im.put(getKeyStroke(KeyEvent.VK_D, KeyEvent.SHIFT_MASK), "removeAttribute");
        im.put(getKeyStroke(KeyEvent.VK_O, 0), "addObjectBelow");
        im.put(getKeyStroke(KeyEvent.VK_O, KeyEvent.SHIFT_MASK), "addObjectAbove");
        im.put(getKeyStroke(KeyEvent.VK_A, 0), "addAttributeRight");
        im.put(getKeyStroke(KeyEvent.VK_A, KeyEvent.SHIFT_MASK), "addAttributeLeft");

        im.put(getKeyStroke(KeyEvent.VK_ENTER, 0), "toggle");
        im.put(getKeyStroke(KeyEvent.VK_T, 0), "toggle");
        im.put(getKeyStroke(KeyEvent.VK_I, 0), "invert");
        im.put(getKeyStroke(KeyEvent.VK_F, 0), "fill");
        im.put(getKeyStroke(KeyEvent.VK_C, 0), "clear");
    }

    private void createButtonActions() {
        ActionMap am = matrix.getActionMap();
        WebButtonGroup group;
        group = new WebButtonGroup(WebButtonGroup.VERTICAL, true, createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.addObject"), "addObject",
                "icons/context editor/add_object.png", am.get("addObjectAtEnd")), createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.clarifyObjects"), "clarifyObjects",
                "icons/context editor/clarify_objects.png", am.get("clarifyObjects")), createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.reduceObjects"), "reduceObjects",
                "icons/context editor/reduce_objects.png", am.get("reduceObjects")));
        group.setButtonsDrawFocus(false);
        toolbar.add(group);
        toolbar.addSeparator();
        group = new WebButtonGroup(WebButtonGroup.VERTICAL, true, createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.addAttribute"), "addAttribute",
                "icons/context editor/add_attribute.png", am.get("addAttributeAtEnd")), createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.clarifyAttributes"), "clarifyAttributes",
                "icons/context editor/clarify_attributes.png", am.get("clarifyAttributes")), createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.reduceAttributes"), "reduceAttributes",
                "icons/context editor/reduce_attributes.png", am.get("reduceAttributes")));
        group.setButtonsDrawFocus(false);
        toolbar.add(group);
        toolbar.addSeparator();
        group = new WebButtonGroup(WebButtonGroup.VERTICAL, true, createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.reduceContext"), "reduceContext",
                "icons/context editor/reduce_context.png", am.get("reduce")), createButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.transposeContext"), "transposeContext",
                "icons/context editor/transpose.png", am.get("transpose")));
        group.setButtonsDrawFocus(false);
        toolbar.add(group);
        toolbar.addSeparator();
        compactMatrixButton = createToggleButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.compactMatrix"), "compactMatrix",
                "icons/context editor/compact.png", (ItemListener) am.get("compact"));
        showArrowRelationsButton = createToggleButton(
                LocaleHandler.getString("ContextEditor.createButtonActions.showArrowRelations"), "showArrowRelations",
                "icons/context editor/show_arrow_relations.png", null); // TODO
        updateButtonSelection();
        group = new WebButtonGroup(WebButtonGroup.VERTICAL, false, compactMatrixButton, showArrowRelationsButton);
        group.setButtonsDrawFocus(false);
        toolbar.add(group);
    }

    /**
     * Updates the toggle button selection of compactMatrixButton and
     * showArrowRelationsButton to reflect the GUIConf state.
     */
    public void updateButtonSelection() {
        // compact matrix
        if (state.guiConf.compactMatrix) {
            compactMatrixButton.setSelected(true);
            matrix.compact();
        } else {
            compactMatrixButton.setSelected(false);
            matrix.uncompact();
        }
        // show arrow relations
        if (state.guiConf.showArrowRelations) {
            showArrowRelationsButton.setSelected(true);
        } else {
            showArrowRelationsButton.setSelected(false);
        }
    }

    private void createContextMenuActions() {
        ActionMap am = matrix.getActionMap();
        // ------------------------
        // Inner cells context menu
        // ------------------------
        addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.selectAll"),
                am.get("selectAll"));
        cellPopupMenu.add(new WebPopupMenu.Separator());
        addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.fill"),
                am.get("fill"));
        addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.clear"),
                am.get("clear"));
        addMenuItem(cellPopupMenu, LocaleHandler.getString("ContextEditor.createContextMenuActions.invert"),
                am.get("invert"));
        cellPopupMenu.add(new WebPopupMenu.Separator());
        addMenuItem(cellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.removeSelectedAttributes"),
                am.get("removeSelectedAttributes"));
        addMenuItem(cellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.removeSelectedObjects"),
                am.get("removeSelectedObjects"));
        // ------------------------
        // Object cell context menu
        // ------------------------
        addMenuItem(objectCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.renameObject"), am.get("renameObject"));
        addMenuItem(objectCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.removeObject"), am.get("removeObject"));
        addMenuItem(objectCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.addObjectAbove"),
                am.get("addObjectAbove"));
        addMenuItem(objectCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.addObjectBelow"),
                am.get("addObjectBelow"));
        // ---------------------------
        // Attribute cell context menu
        // ---------------------------
        addMenuItem(attributeCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.renameAttribute"),
                am.get("renameAttribute"));
        addMenuItem(attributeCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.removeAttribute"),
                am.get("removeAttribute"));
        addMenuItem(attributeCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.addAttributeLeft"),
                am.get("addAttributeLeft"));
        addMenuItem(attributeCellPopupMenu,
                LocaleHandler.getString("ContextEditor.createContextMenuActions.addAttributeRight"),
                am.get("addAttributeRight"));
    }

    private void createMouseActions() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = matrix.rowAtPoint(e.getPoint());
                int j = matrix.columnAtPoint(e.getPoint());
                int clicks = e.getClickCount();
                if (clicks >= 2 && clicks % 2 == 0 && SwingUtilities.isLeftMouseButton(e)) { // Double
                                                                                             // Click
                    if (i > 0 && j > 0) {
                        invokeAction(ContextEditor.this, new ToggleAction(i, j));
                    }
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

    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    class CombineActions extends AbstractAction {
        Action first, second;

        CombineActions(Action first, Action second) {
            this.first = first;
            this.second = second;
        }

        public void actionPerformed(ActionEvent e) {
            invokeAction(e, first);
            invokeAction(e, second);
        }
    }

    class ExpandSelectionAction extends AbstractAction {
        int horizontal, vertical;

        ExpandSelectionAction(int horizontal, int vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            matrix.saveSelection();
            int lastActiveRowIndexAfter = clamp(lastActiveRowIndex + vertical, 1, state.context.getObjectCount());
            int lastActiveColumnIndexAfter = clamp(lastActiveColumnIndex + horizontal, 1,
                    state.context.getAttributeCount());
            boolean wasRowChange = (lastActiveRowIndexAfter - lastActiveRowIndex) != 0;
            boolean wasColumnChange = (lastActiveColumnIndexAfter - lastActiveColumnIndex) != 0;
            boolean isNewRowIndexInsideOldSelection = lastActiveRowIndexAfter >= matrix.getLastSelectedRowsStartIndex()
                    && lastActiveRowIndexAfter <= matrix.getLastSelectedRowsEndIndex();
            boolean isNewColumnIndexInsideOldSelection = lastActiveColumnIndexAfter >= matrix
                    .getLastSelectedColumnsStartIndex()
                    && lastActiveColumnIndexAfter <= matrix.getLastSelectedColumnsEndIndex();
            if (wasRowChange) {
                if (isNewRowIndexInsideOldSelection) {
                    matrix.removeRowSelectionInterval(lastActiveRowIndex, lastActiveRowIndex);
                } else {
                    matrix.addRowSelectionInterval(lastActiveRowIndexAfter, lastActiveRowIndexAfter);
                }
                lastActiveRowIndex = lastActiveRowIndexAfter;
            }
            if (wasColumnChange) {
                if (isNewColumnIndexInsideOldSelection) {
                    matrix.removeColumnSelectionInterval(lastActiveColumnIndex, lastActiveColumnIndex);
                } else {
                    matrix.addColumnSelectionInterval(lastActiveColumnIndexAfter, lastActiveColumnIndexAfter);
                }
                lastActiveColumnIndex = lastActiveColumnIndexAfter;
            }
        }
    }

    class MoveObjectOrAttributeAction extends AbstractAction {
        int horizontal, vertical;

        MoveObjectOrAttributeAction(int horizontal, int vertical) {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }

        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            int lastActiveRowIndexAfter = clamp(lastActiveRowIndex + vertical, 1, state.context.getObjectCount());
            int lastActiveColumnIndexAfter = clamp(lastActiveColumnIndex + horizontal, 1,
                    state.context.getAttributeCount());
            boolean wasRowChange = (lastActiveRowIndexAfter - lastActiveRowIndex) != 0;
            boolean wasColumnChange = (lastActiveColumnIndexAfter - lastActiveColumnIndex) != 0;
            if (wasRowChange) {
                matrixModel.reorderRows(lastActiveRowIndex, lastActiveRowIndexAfter);
                matrixModel.fireTableDataChanged();
                lastActiveRowIndex = lastActiveRowIndexAfter;
                matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
                matrix.saveSelection();
            }
            if (wasColumnChange) {
                matrixModel.reorderColumns(lastActiveColumnIndex, lastActiveColumnIndexAfter);
                matrixModel.fireTableDataChanged();
                lastActiveColumnIndex = lastActiveColumnIndexAfter;
                matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
                matrix.saveSelection();
            }
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
                    i = state.context.getObjectCount() - 1;
                    break;
                }
                if (j < 0) {
                    i -= 1;
                    j = state.context.getAttributeCount() - 1;
                }
                if (i >= state.context.getObjectCount()) {
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
            i = mod(i, state.context.getObjectCount());
            j = mod(j, state.context.getAttributeCount());
            lastActiveRowIndex = i + 1;
            lastActiveColumnIndex = j + 1;
            matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
        }
    }

    class ToggleAction extends AbstractAction {
        int i, j;

        ToggleAction(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            if (i <= 0 || j <= 0)
                return;
            int i = clamp(this.i, 1, state.context.getObjectCount()) - 1;
            int j = clamp(this.j, 1, state.context.getAttributeCount()) - 1;
            state.saveConf();
            state.context.toggleAttributeForObject(state.context.getAttributeAtIndex(j), state.context
                    .getObjectAtIndex(i).getIdentifier());
            matrix.saveSelection();
            matrixModel.fireTableDataChanged();
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class ToggleActiveAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            invokeAction(ContextEditor.this, new ToggleAction(lastActiveRowIndex, lastActiveColumnIndex));
        }
    }

    class SelectAllAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            matrix.selectAll();
            matrix.saveSelection();
        }
    }

    class SelectNoneAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming)
                return;
            matrix.clearSelection();
            matrix.saveSelection();
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
            state.saveConf();
            execute(i1, i2, j1, j2);
            matrixModel.fireTableDataChanged();
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
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

    class ClarifyObjectsAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            Long progressBarId = state.getStatusBar().startCalculation();
            ClarificationReductionWorker crw = new ClarificationReductionWorker(state, matrix, progressBarId, true,
                    false, false);
            crw.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state.getStatusBar()));
            state.getStatusBar().addCalculation(progressBarId, crw);
            crw.execute();
        }
    }

    class ClarifyAttributesAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            Long progressBarId = state.getStatusBar().startCalculation();
            ClarificationReductionWorker crw = new ClarificationReductionWorker(state, matrix, progressBarId, false,
                    false, false);
            crw.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state.getStatusBar()));
            state.getStatusBar().addCalculation(progressBarId, crw);
            crw.execute();
        }
    }

    class ReduceObjectsAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            Long progressBarId = state.getStatusBar().startCalculation();
            ClarificationReductionWorker crw = new ClarificationReductionWorker(state, matrix, progressBarId, true,
                    true, false);
            crw.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state.getStatusBar()));
            state.getStatusBar().addCalculation(progressBarId, crw);
            crw.execute();
        }
    }

    class ReduceAttributesAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            Long progressBarId = state.getStatusBar().startCalculation();
            ClarificationReductionWorker crw = new ClarificationReductionWorker(state, matrix, progressBarId, false,
                    true, false);
            crw.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state.getStatusBar()));
            state.getStatusBar().addCalculation(progressBarId, crw);
            crw.execute();
        }
    }

    class ReduceAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            Long progressBarId = state.getStatusBar().startCalculation();
            ClarificationReductionWorker crw = new ClarificationReductionWorker(state, matrix, progressBarId, false,
                    false, true);
            crw.addPropertyChangeListener(new StatusBarPropertyChangeListener(progressBarId, state.getStatusBar()));
            state.getStatusBar().addCalculation(progressBarId, crw);
            crw.execute();
        }
    }

    class TransposeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            state.saveConf();
            state.context.transpose();
            matrixModel.fireTableStructureChanged();
            matrix.clearSelection();
            matrix.saveSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class AddAttributeAtAction extends AbstractAction {
        int index;

        AddAttributeAtAction(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            state.saveConf();
            matrix.saveSelection();
            addAttributeAt(index);
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class AddObjectAtAction extends AbstractAction {
        int index;

        AddObjectAtAction(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            state.saveConf();
            matrix.saveSelection();
            addObjectAt(index);
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class AddAttributeAfterActiveAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            invokeAction(ContextEditor.this, new AddAttributeAtAction(lastActiveColumnIndex));
            lastActiveColumnIndex += 1;
            matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
            matrix.saveSelection();
        }
    }

    class AddAttributeBeforeActiveAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            invokeAction(ContextEditor.this, new AddAttributeAtAction(lastActiveColumnIndex - 1));
        }
    }

    class AddAttributeAtEndAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            boolean oldIsRenaming = matrix.isRenaming;
            matrix.isRenaming = false;
            invokeAction(ContextEditor.this, new AddAttributeAtAction(state.context.getAttributeCount()));
            matrix.isRenaming = oldIsRenaming;
        }
    }

    class AddObjectAfterActiveAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            invokeAction(ContextEditor.this, new AddObjectAtAction(lastActiveRowIndex));
            lastActiveRowIndex += 1;
            matrix.selectCell(lastActiveRowIndex, lastActiveColumnIndex);
            matrix.saveSelection();
        }
    }

    class AddObjectBeforeActiveAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            invokeAction(ContextEditor.this, new AddObjectAtAction(lastActiveRowIndex - 1));
        }
    }

    class AddObjectAtEndAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            boolean oldIsRenaming = matrix.isRenaming;
            matrix.isRenaming = false;
            invokeAction(ContextEditor.this, new AddObjectAtAction(state.context.getObjectCount()));
            matrix.isRenaming = oldIsRenaming;
        }
    }

    class RenameActiveObjectAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getObjectCount() == 0) {
                return;
            }
            // This code is the way it is to work around the inability of
            // flushing the keyboard buffer in Swing
            final String name = state.context.getObjectAtIndex(lastActiveRowIndex - 1).getIdentifier();
            final JTextField t = matrix.renameRowHeader(lastActiveRowIndex);
            Timer timer = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    t.setText(name);
                    t.selectAll();
                }
            });
            timer.setRepeats(false);
            timer.setInitialDelay(10);
            timer.start();
        }
    }

    class RenameActiveAttributeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getObjectCount() == 0) {
                return;
            }
            // This code is the way it is to work around the inability of
            // flushing the keyboard buffer in Swing
            final String name = state.context.getAttributeAtIndex(lastActiveColumnIndex - 1);
            final JTextField t = matrix.renameColumnHeader(lastActiveColumnIndex);
            Timer timer = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    t.setText(name);
                    t.selectAll();
                }
            });
            timer.setRepeats(false);
            timer.setInitialDelay(10);
            timer.start();
        }
    }

    class RemoveActiveObjectAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getObjectCount() == 0) {
                return;
            }
            matrix.saveSelection();
            try {
                state.saveConf();
                state.context.removeObject(state.context.getObjectAtIndex(lastActiveRowIndex - 1).getIdentifier());
                if (lastActiveRowIndex - 1 >= state.context.getObjectCount()) {
                    lastActiveRowIndex--;
                }
            } catch (IllegalObjectException e1) {
                e1.printStackTrace();
            }
            matrixModel.fireTableStructureChanged();
            matrix.invalidate();
            matrix.repaint();
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class RemoveActiveAttributeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getAttributeCount() == 0) {
                return;
            }
            matrix.saveSelection();
            state.saveConf();
            state.context.removeAttribute(state.context.getAttributeAtIndex(lastActiveColumnIndex - 1));
            matrix.updateColumnWidths(lastActiveColumnIndex);
            if (lastActiveColumnIndex - 1 >= state.context.getAttributeCount()) {
                lastActiveColumnIndex--;
            }
            matrixModel.fireTableStructureChanged();
            matrix.invalidate();
            matrix.repaint();
            matrix.restoreSelection();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class RemoveSelectedObjectsAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getAttributeCount() == 0) {
                return;
            }
            matrix.saveSelection();
            int i = Math.min(matrix.getLastSelectedRowsStartIndex(), matrix.getLastSelectedRowsEndIndex()) - 1;
            int d = Math.abs(matrix.getLastSelectedRowsStartIndex() - matrix.getLastSelectedRowsEndIndex()) + 1;
            state.saveConf();
            for (int unused = 0; unused < d; unused++) {
                try {
                    state.context.removeObject(state.context.getObjectAtIndex(i).getIdentifier());
                } catch (IllegalObjectException e1) {
                    e1.printStackTrace();
                }
            }
            matrixModel.fireTableStructureChanged();
            matrix.invalidate();
            matrix.repaint();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class RemoveSelectedAttributesAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (matrix.isRenaming) {
                return;
            }
            if (state.context.getAttributeCount() == 0) {
                return;
            }
            matrix.saveSelection();
            int i = Math.min(matrix.getLastSelectedColumnsStartIndex(), matrix.getLastSelectedColumnsEndIndex()) - 1;
            int d = Math.abs(matrix.getLastSelectedColumnsStartIndex() - matrix.getLastSelectedColumnsEndIndex()) + 1;
            state.saveConf();
            for (int unused = 0; unused < d; unused++) {
                state.context.removeAttribute(state.context.getAttributeAtIndex(i));
                matrix.updateColumnWidths(i + 1);
            }
            matrixModel.fireTableStructureChanged();
            matrix.invalidate();
            matrix.repaint();
            state.contextChanged();
            state.getContextEditorUndoManager().makeRedoable();
        }
    }

    class CompactAction extends AbstractAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                state.guiConf.compactMatrix = true;
                matrix.compact();
            } else {
                state.guiConf.compactMatrix = false;
                matrix.uncompact();
            }
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper functions
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addAttributeAt(final int i) {
        String collisionFreeName = "attr" + i;
        while (true) {
            if (!state.context.existsAttributeAlready(collisionFreeName)) {
                break;
            }
            collisionFreeName = collisionFreeName + "'";
        }
        state.context.addAttributeAt(collisionFreeName, i);
        matrixModel.fireTableStructureChanged();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                matrix.renameColumnHeader(i + 1);
            }
        });
    }

    private void addObjectAt(final int i) {
        String collisionFreeName = "obj" + i;
        while (true) {
            if (!state.context.existsObjectAlready(collisionFreeName)) {
                break;
            }
            collisionFreeName = collisionFreeName + "'";
        }
        FullObject<String, String> newObject = new FullObject<>(collisionFreeName);
        state.context.addObjectAt(newObject, i);
        matrixModel.fireTableStructureChanged();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                matrix.renameRowHeader(i + 1);
            }
        });
    }

}
