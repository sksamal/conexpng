package fcaviewtab;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class FormalAttributeTypeSelectionPane
  extends JDialog
  implements ActionListener
{
  private JLabel listLabel;
  private JRadioButton jrbBoolean;
  private JRadioButton jrbMultiple;
  private ButtonGroup bg;
  private JButton btnOK;
  private int typeIndex;
  
  public FormalAttributeTypeSelectionPane()
  {
    enableEvents(64L);
    try
    {
      initUI();
      setSize(400, 300);
      setResizable(false);
      setTitle("Select Attribute Type...");
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
    this.listLabel = new JLabel("  Attribute Type: ");
    
    this.jrbBoolean = new JRadioButton("Slots with Boolean Type");
    this.jrbBoolean.setSelected(true);
    this.jrbMultiple = new JRadioButton("Slots with Multiple Instance Type");
    this.bg = new ButtonGroup();
    this.bg.add(this.jrbBoolean);
    this.bg.add(this.jrbMultiple);
    
    JPanel jrbPanel = new JPanel(new GridLayout(2, 1));
    jrbPanel.add(this.jrbBoolean);
    jrbPanel.add(this.jrbMultiple);
    
    this.btnOK = new JButton("OK...");
    this.btnOK.addActionListener(this);
    JPanel okPanel = new JPanel();
    okPanel.add(this.btnOK);
    
    getContentPane().add(this.listLabel, "North");
    getContentPane().add(jrbPanel, "Center");
    getContentPane().add(okPanel, "South");
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
    if (s == this.btnOK)
    {
      if (this.jrbBoolean.isSelected()) {
        this.typeIndex = 0;
      }
      if (this.jrbMultiple.isSelected()) {
        this.typeIndex = 1;
      }
    }
    cancel();
  }
  
  public int getTypeIndex()
  {
    return this.typeIndex;
  }
}
