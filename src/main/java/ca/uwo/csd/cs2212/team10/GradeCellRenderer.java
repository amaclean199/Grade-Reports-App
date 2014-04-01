package ca.uwo.csd.cs2212.team10;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DecimalFormat;

/**
 * Implements the Cell Renderer used in the JTable
 *
 * @author team10
 */
public class GradeCellRenderer extends DefaultTableCellRenderer {
    DecimalFormat formatter;
    
    public GradeCellRenderer(){
        super();
        formatter = new DecimalFormat("0.##'%'");
        
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
    @Override
    public void setValue(Object aValue) {
        Double val = (Double)aValue;
        
        if (val == Student.NO_GRADE)
            super.setValue("---");
        else
            super.setValue(formatter.format(val));
    }
}