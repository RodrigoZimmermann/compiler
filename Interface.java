import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager;

public class Interface {

	JFrame compiler;
	JPanel painel;
	JFileChooser fileChooser;
	JToolBar toolBar;
	JScrollPane editorScroll;
	JTextArea editor;
	JScrollPane messagesAreaScroll;
	JTextArea messagesArea;
	JSplitPane barDivision;
	JTextPane statusBar;
	JButton newButton;
	JButton openButton;
	JButton saveButton;
	JButton copieButton;
	JButton pasteButton;
	JButton cutOutButton;
	JButton compileButton;
	JButton teamButton;

	public static void main(String[] args) {
		new Interface();
	}

	public Interface() {
		compiler = new JFrame();
		compiler.setMinimumSize(new Dimension(910, 600));

		painel = new JPanel();
		painel.setLayout(new BorderLayout(0, 0));
		compiler.setContentPane(painel);

		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Arquivos de texto (.txt)", "txt"));

		toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setMinimumSize(new Dimension(900, 70));
		painel.add(toolBar, BorderLayout.WEST);

		editorScroll = new JScrollPane();
		editorScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		editorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		editor = new JTextArea();
		editor.setBorder(new NumberedBorder());
		editorScroll.setViewportView(editor);

		messagesAreaScroll = new JScrollPane();
		messagesAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		messagesAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		messagesAreaScroll.setViewportView(messagesArea);

		barDivision = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScroll, messagesAreaScroll);
		painel.add(barDivision, BorderLayout.CENTER);
		barDivision.setDividerLocation(350);

		statusBar = new JTextPane();
		statusBar.setMinimumSize(new Dimension(900, 25));
		statusBar.setEditable(false);
		painel.add(statusBar, BorderLayout.SOUTH);

		newButton = createBarraDeFerramentabutton("novo [ctrl-n]       ", "novo", this::newButtonAction);
		toolBar.add(newButton);

		openButton = createBarraDeFerramentabutton("abrir [ctrl-o]       ", "abrir", this::openButtonAction);
		toolBar.add(openButton);

		saveButton = createBarraDeFerramentabutton("salvar [ctrl-s]    ", "salvar", this::saveButtonAction);
		toolBar.add(saveButton);

		copieButton = createBarraDeFerramentabutton("copiar [ctrl-c]    ", "copiar", editor::copy);
		toolBar.add(copieButton);

		pasteButton = createBarraDeFerramentabutton("colar [ctrl-v]      ", "colar", editor::paste);
		toolBar.add(pasteButton);

		cutOutButton = createBarraDeFerramentabutton("recortar [ctrl-x]", "recortar", editor::cut);
		toolBar.add(cutOutButton);

		compileButton = createBarraDeFerramentabutton("compilar [F7]     ", "compilar", this::compileButtonAction);
		toolBar.add(compileButton);

		teamButton = createBarraDeFerramentabutton("equipe [F1]         ", "equipe", this::teamButtonAction);
		toolBar.add(teamButton);

		setShortcutsListener();

		compiler.setVisible(true);
	}

	private JButton createBarraDeFerramentabutton(String nomebutton, String nomeIcone, clickbutton clickbutton) {
		JButton button = new JButton(nomebutton);
		String resource = "/resources/" + nomeIcone + ".png";
		button.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(resource))));
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.addActionListener(e -> clickbutton.onbuttonClick());

		return button;
	}

	public interface clickbutton {
		void onbuttonClick();
	}

	private void newButtonAction() {
		editor.setText("");
		messagesArea.setText("");
		statusBar.setText("");
	}

	private void openButtonAction() {
		int retornoArquivo = fileChooser.showOpenDialog(painel);

		if (retornoArquivo == JFileChooser.APPROVE_OPTION) {
			try {
				File arquivo = fileChooser.getSelectedFile();
				editor.read(new FileReader(arquivo.getAbsolutePath()), null);

				messagesArea.setText("");
				statusBar.setText(arquivo.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveButtonAction() {
		if (!statusBar.getText().isBlank()) {
			try {
				String arquivoAtual = fileChooser.getSelectedFile().getAbsolutePath();

				if (!arquivoAtual.contains(".txt")) {
					arquivoAtual += ".txt";
				}

				BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoAtual));
				bw.write(editor.getText());
				bw.close();

				messagesArea.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			int retornoArquivo = fileChooser.showSaveDialog(painel);

			if (retornoArquivo == JFileChooser.APPROVE_OPTION) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile() + ".txt"));
					bw.write(editor.getText());
					bw.close();

					messagesArea.setText("");
					statusBar.setText(fileChooser.getSelectedFile().getAbsolutePath() + ".txt");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void compileButtonAction() {
		Lexico lexico = new Lexico();
		lexico.setInput(editor.getText());

		String[] textLine = editor.getText().split("\n");

		int lastLine = 0;
		try {
			Token t = null;
			StringBuilder text = new StringBuilder();
			text.append("Linha").append("\t").append("Classe").append("\t").append("\t").append("Lexema").append("\n");
			while ((t = lexico.nextToken()) != null) {
				for (int i = 0; i < textLine.length; i++) {
					if (textLine[i].contains(t.getLexeme())) {
						lastLine = i + 1;
						text.append(lastLine).append("\t").append(checkIdConstans(t.getId())).append("\t")
								.append(t.getLexeme()).append("\n");
					}
				}
			}
			text.append("programa compilado com sucesso");
			messagesArea.setText(text.toString());
		} catch (LexicalError e) {
			if(lastLine == 0) {
				lastLine = 1;
			}
			if (e.getMessage().equals(ScannerConstants.SCANNER_ERROR[0])) {
				messagesArea.setText("Erro na linha " + lastLine + " - " + editor.getText().charAt(e.getPosition())
						+ " " + e.getMessage());
			} else {
				messagesArea.setText("Erro na linha " + lastLine + " - " + e.getMessage());
			}
		}
	}

	private String checkIdConstans(int id) {
		StringBuilder name = new StringBuilder();
		if (id == 2) {
			name.append("constante_int").append("\t");
		} else if (id == 3) {
			name.append("constante_float").append("\t");
		} else if (id == 4) {
			name.append("constante_bin").append("\t");
		} else if (id == 5) {
			name.append("constante_string").append("\t");
		} else if (id == 6) {
			name.append("identificador").append("\t");
		} else if (id >= 7 && id <= 18) {
			name.append("palavra reservada");
		} else if (id >= 19 && id <= 34) {
			name.append("símbolo especial");
		} else {
			name.append("");
		}
		return name.toString();
	}

	private void teamButtonAction() {
		messagesArea.setText("Rodrigo Luís Zimmermann, Matheus Felipe Sychocki, José Perich");
	}

	private void setShortcutsListener() {
		KeyboardFocusManager keyManager = getCurrentKeyboardFocusManager();
		keyManager.addKeyEventDispatcher(e -> {
			boolean controlDownAndKeyPressed = e.isControlDown() && e.getID() == KeyEvent.KEY_PRESSED;
			boolean keyPressed = e.getID() == KeyEvent.KEY_PRESSED;

			if (controlDownAndKeyPressed && e.getKeyCode() == KeyEvent.VK_N) {
				newButtonAction();
			} else if (controlDownAndKeyPressed && e.getKeyCode() == KeyEvent.VK_O) {
				openButtonAction();
			} else if (controlDownAndKeyPressed && e.getKeyCode() == KeyEvent.VK_S) {
				saveButtonAction();
			} else if (keyPressed && e.getKeyCode() == KeyEvent.VK_F7) {
				compileButtonAction();
			} else if (keyPressed && e.getKeyCode() == KeyEvent.VK_F1) {
				teamButtonAction();
			}
			return false;
		});
	}
}
