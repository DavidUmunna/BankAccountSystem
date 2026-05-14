package AccountManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BankUI extends JFrame {

    // Colour palette
    private static final Color BG          = new Color(15,  17,  26);
    private static final Color CARD_BG     = new Color(24,  27,  42);
    private static final Color ACCENT      = new Color(99, 179, 237);
    private static final Color ACCENT_DARK = new Color(66, 135, 245);
    private static final Color SUCCESS     = new Color(72, 199, 142);
    private static final Color DANGER      = new Color(252, 92,  101);
    private static final Color TEXT_PRI    = new Color(235, 237, 245);
    private static final Color TEXT_SEC    = new Color(120, 130, 160);
    private static final Color BORDER      = new Color(40,  44,  65);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_CARD_LBL= new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BALANCE = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);
    JButton stopButton=new JButton("Stop");

    private final List<AccountThread> accountThreads = new ArrayList<>();
    private final List<JLabel>        balanceLabels  = new ArrayList<>();
    private       JLabel              statusLabel;

    public BankUI() {
        setTitle("BankAccountSystem");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setMinimumSize(new Dimension(620, 440));
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel title = new JLabel("Bank Account System");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRI);

        JLabel subtitle = new JLabel("Concurrent Transfer Simulator");
        subtitle.setFont(FONT_LABEL);
        subtitle.setForeground(TEXT_SEC);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitle);
        header.add(titleBox, BorderLayout.WEST);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT_LABEL);
        statusLabel.setForeground(TEXT_SEC);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(statusLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Account cards ──────────────────────────────────────────────────────
        BankAccount bankAccount = new BankAccount();
        for (int i = 0; i < 3; i++) {
            AccountThread account = new AccountThread();
            bankAccount.addAccount(account.getName());
            accountThreads.add(account);
        }

        JPanel cardsPanel = new JPanel(new GridLayout(1, accountThreads.size(), 16, 0));
        cardsPanel.setBackground(BG);
        cardsPanel.setBorder(new EmptyBorder(24, 28, 12, 28));

        for (AccountThread account : accountThreads) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)
            ));

            JLabel nameTag = new JLabel("ACCOUNT");
            nameTag.setFont(FONT_CARD_LBL);
            nameTag.setForeground(ACCENT);

            JLabel nameLabel = new JLabel(account.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setForeground(TEXT_PRI);

            JLabel balLabel = new JLabel(CURRENCY.format(account.getBalance()));
            balLabel.setFont(FONT_BALANCE);
            balLabel.setForeground(TEXT_PRI);
            balanceLabels.add(balLabel);

            card.add(nameTag);
            card.add(Box.createVerticalStrut(4));
            card.add(nameLabel);
            card.add(Box.createVerticalStrut(14));
            card.add(balLabel);
            cardsPanel.add(card);
        }
        add(cardsPanel, BorderLayout.CENTER);

        // ── Transfer panel ─────────────────────────────────────────────────────
        JPanel transferPanel = new JPanel(new GridBagLayout());
        transferPanel.setBackground(CARD_BG);
        transferPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(20, 28, 24, 28)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 12);
        gc.anchor = GridBagConstraints.WEST;

        String[] names = accountThreads.stream().map(Thread::getName).toArray(String[]::new);
        JComboBox<String> fromCombo = styledCombo(names);
        JComboBox<String> toCombo   = styledCombo(names);
        if (names.length > 1) toCombo.setSelectedIndex(1);

        JTextField amountField = new JTextField("1000", 9);
        amountField.setFont(FONT_LABEL);
        amountField.setBackground(new Color(30, 34, 52));
        amountField.setForeground(TEXT_PRI);
        amountField.setCaretColor(ACCENT);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JButton transferButton = new JButton("Transfer");
        transferButton.setFont(FONT_BTN);
        transferButton.setForeground(Color.WHITE);
        transferButton.setBackground(ACCENT_DARK);
        transferButton.setBorder(new EmptyBorder(8, 22, 8, 22));
        transferButton.setFocusPainted(false);
        transferButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        transferButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { transferButton.setBackground(ACCENT); }
            public void mouseExited(java.awt.event.MouseEvent e)  { transferButton.setBackground(ACCENT_DARK); }
        });

        gc.gridx = 0; gc.gridy = 0; transferPanel.add(fieldLabel("From"), gc);
        gc.gridx = 1;               transferPanel.add(fromCombo, gc);
        gc.gridx = 2;               transferPanel.add(fieldLabel("To"), gc);
        gc.gridx = 3;               transferPanel.add(toCombo, gc);
        gc.gridx = 4;               transferPanel.add(fieldLabel("Amount ($)"), gc);
        gc.gridx = 5; gc.insets = new Insets(0, 0, 0, 16);
        transferPanel.add(amountField, gc);
        gc.gridx = 6; gc.insets = new Insets(0, 0, 0, 0);
        transferPanel.add(transferButton, gc);

        add(transferPanel, BorderLayout.SOUTH);

        // ── Transfer action ────────────────────────────────────────────────────
        transferButton.addActionListener(e -> {
            String fromName = (String) fromCombo.getSelectedItem();
            String toName   = (String) toCombo.getSelectedItem();

            if (fromName.equals(toName)) {
                setStatus("From and To accounts must be different.", DANGER);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountField.getText().trim().replace(",", ""));
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                setStatus("Enter a valid positive amount.", DANGER);
                return;
            }

            AccountThread from = accountThreads.stream()
                    .filter(a -> a.getName().equals(fromName)).findFirst().orElse(null);
            AccountThread to = accountThreads.stream()
                    .filter(a -> a.getName().equals(toName)).findFirst().orElse(null);
            if (from == null || to == null) return;

            final int finalAmount = amount;
            new Thread(() -> {
                if (from.getBalance() >= finalAmount) {
                    FraudDetection.enterTransfers();
                    try {
                        BankAccount.removeFromBalance(from.getName(), finalAmount);
                        BankAccount.addToBalance(to.getName(), finalAmount);
                    } finally {
                        FraudDetection.exitTransfers();
                    }
                    SwingUtilities.invokeLater(() ->
                        setStatus("Transferred " + CURRENCY.format(finalAmount) + " from " + fromName + " to " + toName + ".", SUCCESS));
                } else {
                    SwingUtilities.invokeLater(() ->
                        setStatus("Insufficient balance in " + fromName + ".", DANGER));
                }
            }).start();

        });
        stopButton.addActionListener(e->{
            BankAccount.running.set(false);
            new Thread(()->{
                for(AccountThread account:accountThreads){
                    try {
                        account.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                SwingUtilities.invokeLater(()->{
                    setStatus("All transfers stopped.", TEXT_SEC);
                    stopButton.setEnabled(false);
                });
            }).start();
        });

        // ── Balance refresh ────────────────────────────────────────────────────
        Timer refreshTimer = new Timer(500, e -> {
            for (int i = 0; i < accountThreads.size(); i++) {
                balanceLabels.get(i).setText(CURRENCY.format(accountThreads.get(i).getBalance()));
            }
        });

        refreshTimer.start();

        setVisible(true);
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_CARD_LBL);
        label.setForeground(TEXT_SEC);
        return label;
    }

    private JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(FONT_LABEL);
        combo.setBackground(new Color(30, 34, 52));
        combo.setForeground(TEXT_PRI);
        combo.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(FONT_LABEL);
                setForeground(TEXT_PRI);
                setBackground(isSelected ? ACCENT_DARK : new Color(30, 34, 52));
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        return combo;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankUI::new);
    }
}


