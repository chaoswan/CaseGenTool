package com.spin.cgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import com.spin.cgt.cmd.Cmd;
import com.spin.cgt.cmd.CmdClient;
import com.spin.cgt.cmd.CmdResult;
import com.spin.cgt.constant.Constant;
import com.spin.cgt.tool.FileTool;
import com.spin.cgt.tool.GotestRunTool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunCaseAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = FileTool.getFile(e);
        String path = file.getPath();
        if (path.contains("cases") && (file.isDirectory() || path.endsWith(".json"))) {
            runCase(e, path);
        } else {
            Messages.showErrorDialog("路径错误", "执行失败");
        }
    }

    private void runCase(AnActionEvent e, String path) {
        Cmd<String> stringCmd = new Cmd<>(Constant.CMD_TYPE_RUN_CASE, path);
        if (CmdClient.checkServer(FileTool.getProject(e).getName()) == 0) {
            CmdResult<List<Map<String, ?>>> cmdResult = new CmdResult<>(new ArrayList<>());
            CmdClient.Cmd(stringCmd, cmdResult);
            if (cmdResult.isSuccess()) {
                ResultTableModel tableModel = new ResultTableModel(cmdResult.getData());
                Map<String, String> pathMap = new HashMap<>();
                for (Map<String, ?> result : cmdResult.getData()) {
                    pathMap.put((String) result.get("name"), (String) result.get("path"));
                }
                JBTable table = new JBTable(tableModel);
                ResultDialogWrapper dialogWrapper = new ResultDialogWrapper(e, table, pathMap);
                dialogWrapper.show();
            }
        } else {
            GotestRunTool.runGoTest(e, "TestCmd", stringCmd);
        }
    }

    private static class ResultTableModel extends AbstractTableModel {
        private List<Map<String, ?>> data;

        public ResultTableModel(List<Map<String, ?>> data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return data.get(rowIndex).get("name");
                case 1:
                    return (Boolean) (data.get(rowIndex).get("is_pass")) ? "PASS" : "FAIL";
                case 2:
                    return data.get(rowIndex).get("path");
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Name";
                case 1:
                    return "Result";
                default:
                    return null;
            }
        }
    }

    private static class ResultDialogWrapper extends DialogWrapper {
        private JBTable table;

        public ResultDialogWrapper(AnActionEvent actionEvent, JBTable table, Map<String, String> pathMap) {
            super(FileTool.getProject(actionEvent));
            this.table = table;
            init();
            setModal(false);
            setTitle("用例执行结果");

            TableColumn column1 = table.getColumnModel().getColumn(0);
            column1.setPreferredWidth(500);
            TableColumn column2 = table.getColumnModel().getColumn(1);
            column2.setPreferredWidth(80);

            table.setDefaultRenderer(Object.class, new FailCaseHighlightRenderer());

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int rowIndex = table.getSelectedRow();
                        if (rowIndex != -1) {
                            String name = (String) table.getValueAt(rowIndex, 0);
                            FileTool.gotoCaseFile(actionEvent, pathMap.get(name));
                        }
                    }
                }
            });
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            return panel;
        }

        @Override
        protected void init() {
            super.init();
            setSize(600, 500);
        }
    }

    private static class FailCaseHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
                component.setForeground(table.getSelectionForeground());
            } else if (!isSelected && value.equals("FAIL")) {
                component.setBackground(Color.YELLOW);
                component.setForeground(table.getForeground());
            } else {
                component.setBackground(table.getBackground());
                component.setForeground(table.getForeground());
            }

            return component;
        }
    }
}
