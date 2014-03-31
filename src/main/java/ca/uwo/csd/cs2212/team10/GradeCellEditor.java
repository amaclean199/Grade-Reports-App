package ca.uwo.csd.cs2212.team10;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Implements the Cell Editor used in the JTable
 *
 * @author team10
 */
public class GradeCellEditor extends DefaultCellEditor {
    private JTextField textField;

    public GradeCellEditor() {
        super(new JTextField());
        textField = (JTextField)getComponent();
        textField.setHorizontalAlignment(JTextField.RIGHT);
    }
    
    @Override
    public boolean stopCellEditing() {
        try {
            double value = Double.valueOf(textField.getText());
            if (value < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            textField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            return false;
        }
        return super.stopCellEditing();
    }
    
    @Override
    public Object getCellEditorValue() {
        return Double.valueOf(textField.getText());
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        textField.setBorder(BorderFactory.createEmptyBorder());
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}
