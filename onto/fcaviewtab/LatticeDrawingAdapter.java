package fcaviewtab;

import conexp.frontend.latticeeditor.ConceptSetDrawing;
import conexp.frontend.latticeeditor.LatticeDrawing;
import conexp.util.gui.paramseditor.ParamInfo;

public class LatticeDrawingAdapter
  extends LatticeDrawing
{
  public ParamInfo[] getParams()
  {
    return getLabelingStrategiesParams();
  }
}
