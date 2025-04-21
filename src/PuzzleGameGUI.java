import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PuzzleGameGUI {
    private JFrame frame;
    private JPanel panel;
    private JButton[] buttons;
    private List<Integer> tiles;
    private int emptyIndex;
    private JButton hintButton;
    private JButton checkButton;  // 答え合わせボタン

    public PuzzleGameGUI() {
        // フレームの設定
        frame = new JFrame("15パズルゲーム");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500); // 答え合わせボタンを追加したのでサイズを調整
        frame.setLayout(new BorderLayout());

        // パズルのボタン（タイル）を配置するパネル
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));  // 4x4 のグリッド

        buttons = new JButton[16];
        tiles = new ArrayList<>();

        // 数字のリストを作成
        for (int i = 1; i <= 15; i++) {
            tiles.add(i);
        }
        tiles.add(0);  // 0は空きスペースを示す

        shuffleTiles();  // タイルをシャッフル

        // ボタンを作成
        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 30));
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setText(tiles.get(i) == 0 ? "" : String.valueOf(tiles.get(i)));
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveTile(index);  // タイルの移動を処理
                }
            });
            panel.add(buttons[i]);
        }

        // ヒントボタンを作成
        hintButton = new JButton("ヒント");
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHint();  // ヒントを表示
            }
        });

        // 答え合わせボタンを作成
        checkButton = new JButton("答え合わせ");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();  // 答え合わせを行う
            }
        });

        // フレームにパネルとボタンを追加
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(hintButton);
        bottomPanel.add(checkButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // フレームを表示
        frame.setVisible(true);
    }

    // タイルをシャッフルするメソッド
    private void shuffleTiles() {
        Collections.shuffle(tiles);  // タイルをランダムにシャッフル
        emptyIndex = tiles.indexOf(0);  // 空きスペースの位置
    }

    // タイルの移動を処理するメソッド
    private void moveTile(int index) {
        // 空きスペースの隣にあるタイルだけ移動できる
        if (isAdjacent(index, emptyIndex)) {
            // タイルを移動
            Collections.swap(tiles, index, emptyIndex);
            emptyIndex = index;

            // ボタンのテキストを更新
            updateButtons();
        }

        // パズルが解けたかどうかをチェック
        if (isSolved()) {
            JOptionPane.showMessageDialog(frame, "パズルを解きました！");
        }
    }

    // 隣接しているかどうかを確認するメソッド
    private boolean isAdjacent(int index1, int index2) {
        int row1 = index1 / 4;
        int col1 = index1 % 4;
        int row2 = index2 / 4;
        int col2 = index2 % 4;
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    // パズルが解けたかどうかをチェックするメソッド
    private boolean isSolved() {
        for (int i = 0; i < 15; i++) {
            if (tiles.get(i) != i + 1) {
                return false;
            }
        }
        return tiles.get(15) == 0;  // 最後が空きスペースであることを確認
    }

    // ボタンのテキストを更新するメソッド
    private void updateButtons() {
        for (int i = 0; i < 16; i++) {
            buttons[i].setText(tiles.get(i) == 0 ? "" : String.valueOf(tiles.get(i)));
        }
    }

 // 🧠 改良されたヒントメソッド：一番ゴールに近づく動きを提示
    private void showHint() {
        int bestMoveIndex = -1;
        int minHeuristic = Integer.MAX_VALUE;

        for (int i = 0; i < 16; i++) {
            if (isAdjacent(i, emptyIndex)) {
                Collections.swap(tiles, i, emptyIndex);
                int heuristic = calculateManhattanDistance();
                if (heuristic < minHeuristic) {
                    minHeuristic = heuristic;
                    bestMoveIndex = i;
                }
                Collections.swap(tiles, i, emptyIndex);
            }
        }

        if (bestMoveIndex != -1) {
            JOptionPane.showMessageDialog(frame, "次に動かすと良いタイルは: " + tiles.get(bestMoveIndex));
        } else {
            JOptionPane.showMessageDialog(frame, "動かせるタイルがありません。");
        }
    }

    // マンハッタン距離で「解答にどれだけ近いか」を評価
    private int calculateManhattanDistance() {
        int distance = 0;
        for (int i = 0; i < 16; i++) {
            int value = tiles.get(i);
            if (value == 0) continue;
            int targetRow = (value - 1) / 4;
            int targetCol = (value - 1) % 4;
            int currentRow = i / 4;
            int currentCol = i % 4;
            distance += Math.abs(currentRow - targetRow) + Math.abs(currentCol - targetCol);
        }
        return distance;
    }

    // 答え合わせを行うメソッド
    private void checkAnswer() {
        if (isSolved()) {
            JOptionPane.showMessageDialog(frame, "おめでとうございます！パズルが解けました！");
        } else {
            JOptionPane.showMessageDialog(frame, "パズルはまだ解けていません。頑張ってください！");
        }
    }

    // メインメソッド
    public static void main(String[] args) {
        new PuzzleGameGUI();  // ゲームを開始
    }
}
