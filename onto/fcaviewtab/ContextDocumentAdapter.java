package fcaviewtab;

import conexp.core.Context;
import conexp.frontend.ContextDocument;
import conexp.frontend.components.LatticeComponent;
import conexp.frontend.latticeeditor.LatticeDrawing;
import conexp.frontend.latticeeditor.LatticeDrawingOptions;
import conexp.frontend.latticeeditor.PainterOptionsPaneEditor;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JPanel;

public class ContextDocumentAdapter
  extends ContextDocument
{
  private Context context;
  private LatticeDrawingAdapter lnkLatticeDrawingAdapter;
  
  public ContextDocumentAdapter(Context context)
  {
    super(context);
    this.context = context;
  }
  
  public void activateLatticeView()
  {
    super.getLatticeComponent().calculateAndLayoutPartialLattice();
    super.activateView("Lattice");
  }
  
  public JPanel getOptionPane()
  {
    JPanel leftPanel = new JPanel();
    LatticeDrawingAdapter drawAdapter = new LatticeDrawingAdapter();
    leftPanel.setLayout(new BorderLayout());
    LatticeDrawingOptions drawingOptions = new LatticeDrawingOptions();
    
    PainterOptionsPaneEditor optionPane = new PainterOptionsPaneEditor(getLatticeComponent().getDrawing().getPainterOptions(), drawingOptions.getEditableDrawingOptions(), drawAdapter.getParams());
    
    leftPanel.add(optionPane, "Center");
    return leftPanel;
  }
  
  public Context getContext()
  {
    return this.context;
  }
}
