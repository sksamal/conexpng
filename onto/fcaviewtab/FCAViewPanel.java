package fcaviewtab;

import conexp.core.Context;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.widget.InstanceClsesPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class FCAViewPanel
  extends JPanel
  implements ActionListener
{
  private KnowledgeBase kb;
  private JButton btnShowContext;
  private JPanel contextPanel;
  private JScrollPane jsPane;
  private FormalContextSetter setter;
  private JSplitPane mainPane;
  private InstanceClsesPanel clsesPanel;
  private Collection instances;
  private String[] selectedAttrs;
  private int typeIndex;
  private FormalAttributesSelectionPane attrSelection;
  private FormalAttributeTypeSelectionPane typeSelection;
  private FormalAttributesFinder finder;
  private FormalLatticeComponentPanel latticePanel;
  
  public FCAViewPanel(KnowledgeBase kb)
  {
    this.kb = kb;
    initUI();
  }
  
  private void initUI()
  {
    JPanel leftPanel = new JPanel(new BorderLayout());
    
    this.clsesPanel = new InstanceClsesPanel(this.kb.getProject());
    this.clsesPanel.setAutoscrolls(true);
    leftPanel.add(this.clsesPanel, "Center");
    
    this.btnShowContext = new JButton("Show Context...");
    this.btnShowContext.addActionListener(this);
    
    JPanel leftBottomPanel = new JPanel(new GridLayout(1, 1));
    leftBottomPanel.add(this.btnShowContext);
    leftPanel.add(leftBottomPanel, "South");
    
    JPanel rightpanel = new JPanel();
    rightpanel.setLayout(new BorderLayout());
    
    this.contextPanel = new JPanel();
    this.jsPane = new JScrollPane();
    this.jsPane.getViewport().add(this.contextPanel, null);
    rightpanel.add(this.jsPane, "Center");
    
    this.mainPane = new JSplitPane(1, true, leftPanel, rightpanel);
    
    this.mainPane.setOneTouchExpandable(true);
    setLayout(new BorderLayout());
    add(this.mainPane, "Center");
  }
  
  public void actionPerformed(ActionEvent e)
  {
    Object s = e.getSource();
    if (s == this.btnShowContext) {
      showContext_actionPerformed();
    }
  }
  
  private Context getContext()
  {
    this.setter = new FormalContextSetter(this.kb, this.instances, this.selectedAttrs, this.typeIndex);
    
    FormalContextAdapter adapter = this.setter.getContextAdapter();
    return adapter.getContext();
  }
  
  private String getSelectionText()
  {
    String text = null;
    this.clsesPanel.setFocusable(true);
    Collection texts = this.clsesPanel.getSelection();
    Iterator it = texts.iterator();
    if (it.hasNext())
    {
      Cls cls = (Cls)it.next();
      text = cls.getName();
    }
    return text;
  }
  
  private void showAttributes_actionPerformed(String clsName)
  {
    this.typeSelection = new FormalAttributeTypeSelectionPane();
    
    Dimension dlgSize = this.typeSelection.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    this.typeSelection.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    
    this.typeSelection.setModal(true);
    this.typeSelection.pack();
    this.typeSelection.show();
    
    this.typeIndex = this.typeSelection.getTypeIndex();
    if (this.typeIndex == 0) {
      booleanType_actionPerformed(clsName);
    } else if (this.typeIndex == 1) {
      multipleType_actionPerformed(clsName);
    }
  }
  
  private void booleanType_actionPerformed(String clsName)
  {
    this.finder = new FormalAttributesFinder(this.kb, clsName);
    String[] listAttributes = this.finder.getPossibleFormatAttributesForBoolean();
    this.instances = this.finder.getFormalObjects();
    
    this.attrSelection = new FormalAttributesSelectionPane(listAttributes);
    
    Dimension dlgSize = this.attrSelection.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    this.attrSelection.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    
    this.attrSelection.setModal(true);
    this.attrSelection.pack();
    this.attrSelection.show();
    this.selectedAttrs = this.attrSelection.getSelectedItems();
  }
  
  private void multipleType_actionPerformed(String clsName)
  {
    this.finder = new FormalAttributesFinder(this.kb, clsName);
    String[] listAttributes = this.finder.getPossibleFormalAttributesForMultiple();
    this.instances = this.finder.getFormalObjects();
    
    this.attrSelection = new FormalAttributesSelectionPane(listAttributes);
    
    Dimension dlgSize = this.attrSelection.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    this.attrSelection.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    
    this.attrSelection.setModal(true);
    this.attrSelection.pack();
    this.attrSelection.show();
    this.selectedAttrs = this.attrSelection.getSelectedItems();
  }
  
  private void showContext_actionPerformed()
  {
    String clsName = getSelectionText();
    showAttributes_actionPerformed(clsName);
    Context formalcontext = getContext();
    this.latticePanel = new FormalLatticeComponentPanel(formalcontext);
    
    this.jsPane.getViewport().add(this.latticePanel, null);
    this.latticePanel.setVisible(true);
  }
}
