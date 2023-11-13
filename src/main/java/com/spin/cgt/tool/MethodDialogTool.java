package com.spin.cgt.tool;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.spin.cgt.cmd.model.GenModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MethodDialogTool {
    private static String[] regions = new String[]{"ID", "MY", "VN", "PH", "TH", "SG"};
    private static String[] envs = new String[]{"test", "uat", "live"};
    private static String[] types = new String[]{"rpc", "task", "event"};

    public static void showDialog(@NotNull String title, @Nullable GenModel model, @NotNull Consumer<GenModel> callFunc) {
        JBPanel panel = new JBPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<JRadioButton> regionRadios = addRadioButton(regions, model != null ? model.region : null, "地区:    ", panel);
        List<JRadioButton> envRadios = addRadioButton(envs, model != null ? model.env : null, "环境:    ", panel);
        List<JRadioButton> typeRadios = addRadioButton(types, model != null ? model.type : null, "用例类型:    ", panel);

        JBTextField methodT = new JBTextField();
        JPanel methodP = createFormItem("方法路径:    ", methodT);
        panel.add(methodP);
        if (model != null && model.method != null) {
            methodT.setText(model.method);
        }

        JBTextField suffixT = new JBTextField();
        JPanel suffixP = createFormItem("用例名后缀:    ", suffixT);
        panel.add(suffixP);
        if (model != null && model.suffix != null) {
            suffixT.setText(model.suffix);
        }

        JBTextField dirT = new JBTextField();
        JPanel dirP = createFormItem("用例存放目录:    ", dirT);
        panel.add(dirP);
        if (model != null) {
            if (model.dir != null) {
                dirT.setText(model.dir);
            } else {
                dirT.setText(model.method.replace(".", "/"));
            }
        }

        List<JBTextArea> requestTs = addRequestText(panel, model);

        methodT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                dirT.setText(methodT.getText().replace(".", "/"));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dirT.setText(methodT.getText().replace(".", "/"));
            }
        });

        DialogWrapper genDialog = new DialogWrapper(true) {
            {
                setTitle(title);
                init();
            }

            @Override
            protected JComponent createCenterPanel() {
                panel.setPreferredSize(new Dimension(500, 600));
                return panel;
            }

            protected void doOKAction() {
                GenModel genModel = new GenModel();
                for (JRadioButton regionRadio : regionRadios) {
                    if (regionRadio.isSelected()) {
                        genModel.region = regionRadio.getText();
                        break;
                    }
                }

                for (JRadioButton envRadio : envRadios) {
                    if (envRadio.isSelected()) {
                        genModel.env = envRadio.getText();
                        break;
                    }
                }

                for (JRadioButton typeRadio : typeRadios) {
                    if (typeRadio.isSelected()) {
                        genModel.type = typeRadio.getText();
                        break;
                    }
                }

                genModel.method = methodT.getText();
                genModel.suffix = suffixT.getText();
                genModel.dir = dirT.getText();
                genModel.request = new String[requestTs.size()];
                IntStream.range(0, requestTs.size()).forEach(
                        idx -> {
                            genModel.request[idx] = requestTs.get(idx).getText();
                        }
                );

                callFunc.accept(genModel);

                super.doOKAction();
            }
        };
        genDialog.show();
    }


    private static JPanel createFormItem(String text, List<? extends Component> comps) {
        return createFormItem(text, comps.toArray(new Component[]{}));
    }

    private static JPanel createFormItem(String text, Component... comps) {
        JBPanel panel = new JBPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JBLabel(text));
        panel.setSize(500, 30);
        for (Component comp : comps) {
            panel.add(comp);
        }
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    @NotNull
    private static List<JRadioButton> addRadioButton(String[] options, String value, String text, JBPanel panel) {
        ButtonGroup regionB = new ButtonGroup();
        List<JRadioButton> regionRadios = new LinkedList<>();
        for (String option : options) {
            JRadioButton regionRadio = new JRadioButton(option);
            regionRadio.setBorder(new EmptyBorder(0, 10, 0, 0));
            regionB.add(regionRadio);
            regionRadios.add(regionRadio);

            if (value != null && value.equals(option)) {
                regionRadio.setSelected(true);
            }
        }
        JPanel regionP = createFormItem(text, regionRadios);
        panel.add(regionP);
        return regionRadios;
    }

    private static List<JBTextArea> addRequestText(JBPanel panel, GenModel model) {
        List<JBTextArea> requestTs = new LinkedList<>();

        int totalHeight = 900;
        int totoRow = 24;
        int requestSize = 1;// 目前仅有 一个参数场景
        for (int i = 0; i < requestSize; i++) {
            JBPanel areaP = new JBPanel();
            areaP.setLayout(new BoxLayout(areaP, BoxLayout.X_AXIS));
            areaP.add(new JBLabel("参数" + (i + 1) + ":    "));
            JBTextArea requestT = new JBTextArea();
            requestT.setAlignmentX(Component.LEFT_ALIGNMENT);
            requestT.setAutoscrolls(true);
            requestT.setRows(totoRow / requestSize);
            areaP.add(requestT);
            areaP.setSize(500, totalHeight / requestSize);
            areaP.setBorder(new EmptyBorder(0, 0, 10, 0));
            areaP.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(areaP);
            requestTs.add(requestT);
            if (model != null && model.request != null && model.request.length > i) {
                requestT.setText(model.request[i]);
            }
        }
        return requestTs;
    }
}
