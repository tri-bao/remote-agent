package org.funsoft.remoteagent.gui.component.table;

import org.apache.commons.lang.StringUtils;
import org.funsoft.remoteagent.gui.component.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Ho Tri Bao
 *
 */
public class ScrollableSortablePanel<D, M extends AbstractTableModel<D>> extends JScrollPane {
    private final SortableTable<D, M> table;
    private String title;
    private boolean showTotal = false;

    public ScrollableSortablePanel(M model, String title, boolean showTotal,
            SortableTable<D, M> table) {
        this.title = title;
        this.showTotal = showTotal;
        this.table = table;
        if (title != null) {
            GUIUtils.makeTitledBorder(this, title, Color.BLUE);
        }
        if (model != null) {
            setModel(model);
        }
        setViewportView(this.table);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public M getSortableModel() {
        return table.getSortableModel();
    }

    public void setModel(M model) {
        table.setModel(model);
    }

    public void updateTotal() {
        if (!showTotal) {
            return;
        }
        GUIUtils.makeTitledBorder(
                this,
                StringUtils.stripToEmpty(title) + " (" + getSortableModel().getRowCount() + ")",
                Color.BLUE);
    }

    public void showResult(List<D> results) {
        table.showResult(results);
        updateTotal();
        table.resetLayout(this.getClass());
    }
    public void addData(D data) {
        table.addData(data);
        updateTotal();
        table.resetLayout(this.getClass());
    }
    public void updateData(D data, int modelIndex) {
        table.updateData(data, modelIndex);
        table.resetLayout(this.getClass());
    }
    public void removeData(int modelIndex) {
        table.removeData(modelIndex);
        updateTotal();
    }
    public void selectFirstRow() {
        table.selectFirstRow();
    }

    public D getSelectedRowData() {
        return table.getSelectedRowData();
    }

    public SortableTable<D, M> getTable() {
        return table;
    }
    public void removeSelectedRow() {
        table.removeSelectedRow();
        updateTotal();
    }
}
