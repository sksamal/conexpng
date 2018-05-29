package fcaviewtab;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protege.widget.AbstractWidget;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JScrollPane;

public class FCAViewTab
  extends AbstractTabWidget
{
  private FCAViewPanel fcaViewPanel;
  
  public void initialize()
  {
    setLabel("FCAView Tab");
    
    this.fcaViewPanel = new FCAViewPanel(getKnowledgeBase());
    
    JScrollPane fcaScrollPane = new JScrollPane();
    
    fcaScrollPane.getViewport().add(this.fcaViewPanel, null);
    
    setLayout(new BorderLayout());
    add(fcaScrollPane, "Center");
  }
  
  public static void main(String[] args)
  {
    Application.main(args);
  }
}
