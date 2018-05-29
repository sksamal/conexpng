package fcaviewtab;

import conexp.core.Context;
import conexp.frontend.ContextDocument;
import conexp.frontend.OptionPaneViewChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class FormalLatticeComponentPanel
  extends JPanel
{
  private Context context;
  private ContextDocumentAdapter doc;
  
  public FormalLatticeComponentPanel(Context context)
  {
    this.context = context;
    initUI();
  }
  
  private void initUI()
  {
    this.doc = new ContextDocumentAdapter(this.context);
    
    Component lattice = this.doc.getDocComponent();
    JPanel leftPanel = new JPanel();
    leftPanel = this.doc.getOptionPane();
    OptionPaneViewChangeListener viewlistener = new OptionPaneViewChangeListener(leftPanel);
    
    this.doc.addViewChangeListener(viewlistener);
    this.doc.activateViews();
    
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(this.doc.getToolBar(), "North");
    rightPanel.add(new JScrollPane(lattice), "Center");
    
    JSplitPane splitPane = new JSplitPane(1, true, leftPanel, rightPanel);
    
    splitPane.setOneTouchExpandable(true);
    
    setLayout(new BorderLayout());
    add(splitPane, "Center");
  }
}
