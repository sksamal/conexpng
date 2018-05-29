package fcaviewtab;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class FormalAttributesSelectionPane
  extends JDialog
  implements ActionListener
{
  private JLabel listLabel;
  private JList itemList;
  private Object[] listData;
  private JScrollPane scrollPane;
  private JButton btnOK;
  private JButton btnSelectAll;
  private JPanel btnPanel;
  private CheckBoxListModel cblm;
  private CheckBoxCellRenderer cbcr;
  private String[] selectedItems = null;
  
  public FormalAttributesSelectionPane(Object[] listData)
  {
    this.listData = listData;
    Arrays.sort(listData);
    enableEvents(64L);
    try
    {
      initUI();
      setSize(300, 600);
      setTitle("Select Attributes...");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private void initUI()
    throws Exception
  {
    getContentPane().setLayout(new BorderLayout());
    this.listLabel = new JLabel("  Possible Attributes: ");
    
    this.cblm = new CheckBoxListModel();
    for (int i = 0; i < this.listData.length; i++) {
      this.cblm.addElement(this.listData[i]);
    }
    this.cbcr = new CheckBoxCellRenderer();
    
    this.itemList = new JList();
    
    this.itemList.addMouseListener(new ListChecker(this.itemList, this.cbcr));
    this.itemList.setModel(this.cblm);
    this.itemList.setCellRenderer(new CheckBoxCellRenderer());
    this.itemList.setFont(new Font(null));
    this.scrollPane = new JScrollPane();
    this.scrollPane.getViewport().add(this.itemList);
    this.scrollPane.setVisible(true);
    
    this.btnOK = new JButton("Confirm...");
    this.btnOK.addActionListener(this);
    JPanel okPanel = new JPanel();
    okPanel.add(this.btnOK);
    
    this.btnSelectAll = new JButton("Select All...");
    this.btnSelectAll.addActionListener(this);
    JPanel selectPanel = new JPanel();
    selectPanel.add(this.btnSelectAll);
    
    this.btnPanel = new JPanel(new GridLayout(1, 2));
    this.btnPanel.add(selectPanel);
    this.btnPanel.add(okPanel);
    
    getContentPane().add(this.listLabel, "North");
    getContentPane().add(this.scrollPane, "Center");
    getContentPane().add(this.btnPanel, "South");
  }
  
  protected void processWindowEvent(WindowEvent e)
  {
    if (e.getID() == 201) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  
  void cancel()
  {
    dispose();
  }
  
  public void actionPerformed(ActionEvent e)
  {
    Object s = e.getSource();
    if (s == this.btnSelectAll)
    {
      for (int i = 0; i < this.listData.length; i++) {
        this.cblm.setSelectedAt(i, true);
      }
      this.itemList.repaint();
    }
    if (s == this.btnOK)
    {
      Vector vecItems = new Vector();
      for (int i = 0; i < this.listData.length; i++) {
        if (this.cblm.isSelectedAt(i)) {
          vecItems.add(this.listData[i]);
        }
      }
      if (vecItems != null)
      {
        this.selectedItems = new String[vecItems.size()];
        vecItems.copyInto(this.selectedItems);
      }
      cancel();
    }
  }
  
  public String[] getSelectedItems()
  {
    return this.selectedItems;
  }
  
  class ListChecker
    extends MouseAdapter
  {
    private JList jl;
    private FormalAttributesSelectionPane.CheckBoxCellRenderer ccr;
    
    public ListChecker(JList l, FormalAttributesSelectionPane.CheckBoxCellRenderer c)
    {
      this.jl = l;
      this.ccr = c;
    }
    
    public void mouseClicked(MouseEvent e)
    {
      Point p = e.getPoint();
      int idx = this.jl.locationToIndex(p);
      FormalAttributesSelectionPane.CheckBoxListModel clm = (FormalAttributesSelectionPane.CheckBoxListModel)this.jl.getModel();
      try
      {
        if ((idx > -1) && (idx < this.jl.getModel().getSize()) && (clm.getElementAt(idx) != null) && (clm.isEnabledAt(idx)) && ((e.getModifiers() & 0x10) != 0) && ((this.ccr.isInCheckBox(toLocationInCell(p), clm.getElementAt(idx).toString())) || (e.getClickCount() == 2)))
        {
          clm.setSelectedAt(idx, !clm.isSelectedAt(idx));
          this.jl.repaint();
        }
      }
      catch (NoSuchElementException exp) {}
    }
    
    private Point toLocationInCell(Point p)
    {
      int idx = this.jl.locationToIndex(p);
      if (idx == 0) {
        return p;
      }
      Rectangle r = this.jl.getCellBounds(0, idx - 1);
      return new Point(p.x, p.y - r.height);
    }
  }
  
  class CheckBoxCellRenderer
    extends JPanel
    implements ListCellRenderer
  {
    JCheckBox jcb;
    Border noFocusBorder;
    
    public CheckBoxCellRenderer()
    {
      this.noFocusBorder = new EmptyBorder(1, 1, 1, 1);
      setOpaque(true);
      setBorder(this.noFocusBorder);
      setLayout(new BorderLayout());
      
      this.jcb = new JCheckBox();
      
      add(this.jcb, "West");
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      FormalAttributesSelectionPane.CheckBoxListModel clm = (FormalAttributesSelectionPane.CheckBoxListModel)list.getModel();
      this.jcb.setText(value.toString());
      try
      {
        this.jcb.setSelected(clm.isSelectedAt(index));
        this.jcb.setEnabled(clm.isEnabledAt(index));
      }
      catch (NoSuchElementException e) {}
      if (isSelected)
      {
        this.jcb.setBackground(list.getSelectionBackground());
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      }
      else
      {
        this.jcb.setBackground(list.getBackground());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder(cellHasFocus ? UIManager.getBorder("List.focusCellHighlightBorder") : this.noFocusBorder);
      
      return this;
    }
    
    public boolean isInCheckBox(Point p, String cellText)
    {
      this.jcb.setText(cellText);
      return this.jcb.contains(p);
    }
  }
  
  class CheckBoxListModel
    extends DefaultListModel
  {
    private Vector selected;
    private Vector enabled;
    private boolean defaultSelected;
    private boolean defaultEnabled;
    
    public CheckBoxListModel()
    {
      this.selected = new Vector();
      this.enabled = new Vector();
      
      this.defaultSelected = false;
      this.defaultEnabled = true;
    }
    
    public void setDefaultChecked(boolean state)
    {
      this.defaultSelected = state;
    }
    
    public boolean getDefaultChecked()
    {
      return this.defaultSelected;
    }
    
    public void setDefaultEnabled(boolean state)
    {
      this.defaultEnabled = state;
    }
    
    public boolean getDefaultEnabled()
    {
      return this.defaultEnabled;
    }
    
    public boolean isSelectedAt(int index)
    {
      Object obj = this.selected.get(index);
      if (obj == null) {
        throw new NoSuchElementException();
      }
      return ((Boolean)obj).booleanValue();
    }
    
    public void setSelectedAt(int index, boolean selected)
    {
      if (get(index) == null) {
        throw new NoSuchElementException();
      }
      this.selected.set(index, new Boolean(selected));
    }
    
    public boolean isEnabledAt(int index)
    {
      Object obj = this.enabled.get(index);
      if (obj == null) {
        throw new NoSuchElementException();
      }
      return ((Boolean)obj).booleanValue();
    }
    
    public void setEnabledAt(int index, boolean enabled)
    {
      if (get(index) == null) {
        throw new NoSuchElementException();
      }
      this.enabled.set(index, new Boolean(enabled));
    }
    
    public synchronized void add(int index, Object obj, boolean selected)
    {
      this.selected.add(index, new Boolean(selected));
      this.enabled.add(index, new Boolean(this.defaultEnabled));
      super.add(index, obj);
    }
    
    public synchronized void addElement(Object obj, boolean selected)
    {
      add(getSize(), obj, selected);
    }
    
    public synchronized void add(int index, Object obj)
    {
      add(index, obj, this.defaultSelected);
    }
    
    public synchronized void addElement(Object obj)
    {
      addElement(obj, this.defaultSelected);
    }
    
    public synchronized void clear()
    {
      this.selected.clear();
      this.enabled.clear();
      super.clear();
    }
    
    public synchronized void insertElementAt(Object obj, int index)
    {
      add(index, obj);
    }
    
    public synchronized Object remove(int index)
    {
      this.selected.remove(index);
      this.enabled.remove(index);
      return super.remove(index);
    }
    
    public synchronized void removeAllElements()
    {
      this.selected.removeAllElements();
      this.enabled.removeAllElements();
      super.removeAllElements();
    }
    
    public synchronized boolean removeElement(Object obj)
    {
      int i = indexOf(obj);
      this.selected.removeElementAt(i);
      this.enabled.removeElementAt(i);
      return super.removeElement(obj);
    }
    
    public synchronized void removeElementAt(int index)
    {
      remove(index);
    }
    
    public synchronized void removeRange(int fromIndex, int toIndex)
    {
      for (int i = toIndex; i >= fromIndex; i--)
      {
        this.selected.removeElementAt(i);
        this.enabled.removeElementAt(i);
        super.removeElementAt(i);
      }
      fireIntervalRemoved(this, fromIndex, toIndex);
    }
    
    public synchronized Object set(int index, Object element)
    {
      this.selected.setElementAt(new Boolean(this.defaultSelected), index);
      this.enabled.setElementAt(new Boolean(this.defaultEnabled), index);
      return super.set(index, element);
    }
    
    public synchronized void setElementAt(Object obj, int index)
    {
      set(index, obj);
    }
    
    public synchronized void setSize(int newSize)
    {
      this.selected.setSize(newSize);
      this.enabled.setSize(newSize);
      super.setSize(newSize);
    }
    
    public synchronized void trimToSize()
    {
      this.selected.trimToSize();
      this.enabled.trimToSize();
      super.trimToSize();
    }
  }
}
