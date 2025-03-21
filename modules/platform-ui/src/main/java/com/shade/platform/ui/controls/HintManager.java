package com.shade.platform.ui.controls;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Taken from FlatLaf Demo project
 *
 * @see <a href="https://github.com/JFormDesigner/FlatLaf/blob/master/flatlaf-demo/src/main/java/com/formdev/flatlaf/demo/HintManager.java">HintManager.java</a>
 */
public class HintManager {
    private static final List<HintPanel> hintPanels = new ArrayList<>();

    public static void showHint(Hint hint) {
        HintPanel hintPanel = new HintPanel(hint);
        hintPanel.showHint();

        hintPanels.add(hintPanel);
    }

    public static void hideAllHints() {
        HintPanel[] hintPanels2 = hintPanels.toArray(new HintPanel[0]);
        for (HintPanel hintPanel : hintPanels2)
            hintPanel.hideHint();
    }

    private static class HintPanel extends JPanel {
        private final Hint hint;

        private JPanel popup;
        private JLabel hintLabel;

        private HintPanel(Hint hint) {
            this.hint = hint;

            initComponents();

            setOpaque(false);
            updateBalloonBorder();

            hintLabel.setText("<html>" + hint.message + "</html>");

            // grab all mouse events to avoid that components overlapped
            // by the hint panel receive them
            addMouseListener(new MouseAdapter() {});
        }

        @Override
        public void updateUI() {
            super.updateUI();

            setBackground(UIManager.getColor("HintPanel.backgroundColor"));

            if (hint != null) {
                updateBalloonBorder();
            }
        }

        private void updateBalloonBorder() {
            int direction = switch (hint.position) {
                case SwingConstants.LEFT -> SwingConstants.RIGHT;
                case SwingConstants.TOP -> SwingConstants.BOTTOM;
                case SwingConstants.RIGHT -> SwingConstants.LEFT;
                case SwingConstants.BOTTOM -> SwingConstants.TOP;
                default -> throw new IllegalArgumentException();
            };

            setBorder(new BalloonBorder(direction, FlatUIUtils.getUIColor("PopupMenu.borderColor", Color.gray)));
        }

        void showHint() {
            JRootPane rootPane = SwingUtilities.getRootPane(hint.owner);
            if (rootPane == null)
                return;

            JLayeredPane layeredPane = rootPane.getLayeredPane();

            // create a popup panel that has a drop shadow
            popup = new JPanel(new BorderLayout()) {
                @Override
                public void updateUI() {
                    super.updateUI();

                    // use invokeLater because at this time the UI delegates
                    // of child components are not yet updated
                    EventQueue.invokeLater(() -> {
                        validate();
                        setSize(getPreferredSize());
                    });
                }
            };
            popup.setOpaque(false);
            popup.add(this);

            // calculate x/y location for hint popup
            Point pt = SwingUtilities.convertPoint(hint.owner, 0, 0, layeredPane);
            int x = pt.x;
            int y = pt.y;
            Dimension size = popup.getPreferredSize();
            int gap = UIScale.scale(6);

            switch (hint.position) {
                case SwingConstants.LEFT -> x -= size.width + gap;
                case SwingConstants.TOP -> y -= size.height + gap;
                case SwingConstants.RIGHT -> x += hint.owner.getWidth() + gap;
                case SwingConstants.BOTTOM -> y += hint.owner.getHeight() + gap;
            }

            // set hint popup size and show it
            popup.setBounds(x, y, size.width, size.height);
            layeredPane.add(popup, JLayeredPane.POPUP_LAYER);
        }

        void hideHint() {
            if (popup != null) {
                Container parent = popup.getParent();
                if (parent != null) {
                    parent.remove(popup);
                    parent.repaint(popup.getX(), popup.getY(), popup.getWidth(), popup.getHeight());
                }
            }

            hintPanels.remove(this);
        }

        private void gotIt() {
            // hide hint
            hideHint();

            // show next hint (if any)
            if (hint.nextHint != null)
                HintManager.showHint(hint.nextHint);
        }

        private void initComponents() {
            hintLabel = new JLabel();
            JButton gotItButton = new JButton();

            setLayout(new MigLayout("insets dialog,hidemode 3", "[::250,fill]", "[]para[]"));

            hintLabel.setText("hint");
            add(hintLabel, "cell 0 0");

            gotItButton.setText("Got it!");
            gotItButton.setFocusable(false);
            gotItButton.addActionListener(e -> gotIt());
            add(gotItButton, "cell 0 1,alignx right,growx 0");
        }

    }

    private static class BalloonBorder extends FlatEmptyBorder {
        private static final int ARC = 8;
        private static final int ARROW_XY = 16;
        private static final int ARROW_SIZE = 8;
        private static final int SHADOW_SIZE = 6;
        private static final int SHADOW_TOP_SIZE = 3;
        private static final int SHADOW_SIZE2 = SHADOW_SIZE + 2;

        private final int direction;
        private final Color borderColor;

        private final Border shadowBorder;

        public BalloonBorder(int direction, Color borderColor) {
            super(1 + SHADOW_TOP_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE);

            this.direction = direction;
            this.borderColor = borderColor;

            switch (direction) {
                case SwingConstants.LEFT -> left += ARROW_SIZE;
                case SwingConstants.TOP -> top += ARROW_SIZE;
                case SwingConstants.RIGHT -> right += ARROW_SIZE;
                case SwingConstants.BOTTOM -> bottom += ARROW_SIZE;
            }

            if (UIManager.getLookAndFeel() instanceof FlatLaf) {
                shadowBorder = new FlatDropShadowBorder(
                    UIManager.getColor("Popup.dropShadowColor"),
                    new Insets(SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2),
                    FlatUIUtils.getUIFloat("Popup.dropShadowOpacity", 0.5f)
                );
            } else {
                shadowBorder = null;
            }
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                FlatUIUtils.setRenderingHints(g2);
                g2.translate(x, y);

                // shadow coordinates
                int sx = 0;
                int sy = 0;
                int sw = width;
                int sh = height;
                int arrowSize = UIScale.scale(ARROW_SIZE);
                switch (direction) {
                    case SwingConstants.LEFT -> {
                        sx += arrowSize;
                        sw -= arrowSize;
                    }
                    case SwingConstants.TOP -> {
                        sy += arrowSize;
                        sh -= arrowSize;
                    }
                    case SwingConstants.RIGHT -> sw -= arrowSize;
                    case SwingConstants.BOTTOM -> sh -= arrowSize;
                }

                // paint shadow
                if (shadowBorder != null)
                    shadowBorder.paintBorder(c, g2, sx, sy, sw, sh);

                // create balloon shape
                int bx = UIScale.scale(SHADOW_SIZE);
                int by = UIScale.scale(SHADOW_TOP_SIZE);
                int bw = width - UIScale.scale(SHADOW_SIZE + SHADOW_SIZE);
                int bh = height - UIScale.scale(SHADOW_TOP_SIZE + SHADOW_SIZE);
                g2.translate(bx, by);
                Shape shape = createBalloonShape(bw, bh);

                // fill balloon background
                g2.setColor(c.getBackground());
                g2.fill(shape);

                // paint balloon border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(UIScale.scale(1f)));
                g2.draw(shape);
            } finally {
                g2.dispose();
            }
        }

        private Shape createBalloonShape(int width, int height) {
            int arc = UIScale.scale(ARC);
            int xy = UIScale.scale(ARROW_XY);
            int awh = UIScale.scale(ARROW_SIZE);

            Shape rect;
            Shape arrow;

            switch (direction) {
                case SwingConstants.LEFT -> {
                    rect = new RoundRectangle2D.Float(awh, 0, width - 1 - awh, height - 1, arc, arc);
                    arrow = FlatUIUtils.createPath(awh, xy, 0, xy + awh, awh, xy + awh + awh);
                }
                case SwingConstants.TOP -> {
                    rect = new RoundRectangle2D.Float(0, awh, width - 1, height - 1 - awh, arc, arc);
                    arrow = FlatUIUtils.createPath(xy, awh, xy + awh, 0, xy + awh + awh, awh);
                }
                case SwingConstants.RIGHT -> {
                    rect = new RoundRectangle2D.Float(0, 0, width - 1 - awh, height - 1, arc, arc);
                    int x = width - 1 - awh;
                    arrow = FlatUIUtils.createPath(x, xy, x + awh, xy + awh, x, xy + awh + awh);
                }
                case SwingConstants.BOTTOM -> {
                    rect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1 - awh, arc, arc);
                    int y = height - 1 - awh;
                    arrow = FlatUIUtils.createPath(xy, y, xy + awh, y + awh, xy + awh + awh, y);
                }
                default -> throw new RuntimeException();
            }

            Area area = new Area(rect);
            area.add(new Area(arrow));
            return area;
        }
    }

    public record Hint(String message, Component owner, int position, HintManager.Hint nextHint) {}
}
