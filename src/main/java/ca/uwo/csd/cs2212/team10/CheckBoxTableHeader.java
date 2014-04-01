package ca.uwo.csd.cs2212.team10;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.border.Border;

/**
 *
 * @author team10
 */
class CheckBoxTableHeader extends JCheckBox
        implements TableCellRenderer, MouseListener {

    protected CheckBoxTableHeader rendererComponent;
    protected int column;
    protected boolean mousePressed = false;

    public CheckBoxTableHeader(ItemListener itemListener) {
        rendererComponent = this;
        rendererComponent.addItemListener(itemListener);
        rendererComponent.setSelected(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                rendererComponent.setForeground(header.getForeground());
                rendererComponent.setBackground(header.getBackground());
                rendererComponent.setFont(header.getFont());
                rendererComponent.setHorizontalAlignment(SwingConstants.CENTER);
                header.addMouseListener(rendererComponent);
            }
        }
        Border border = UIManager.getBorder("TableHeader.cellBorder");
        rendererComponent.setBorder(border);
        rendererComponent.setBorderPainted(true);
        return rendererComponent;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mousePressed) {
            mousePressed = false;
            JTableHeader header = (JTableHeader) (e.getSource());
            JTable tableView = header.getTable();
            TableColumnModel columnModel = tableView.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = tableView.convertColumnIndexToModel(viewColumn);

            if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                doClick();
            }
        }
        ((JTableHeader) e.getSource()).repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
