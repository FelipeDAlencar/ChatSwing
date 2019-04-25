import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Cliente extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtNome;
	private JLabel lbDigitando;
	public static final String DIGITANDO = "<!dig...!>";
	public static final String NAO_DIGITANDO = "<!no_dig...!>";

	public Cliente() throws IOException {

		JLabel lblMessage = new JLabel("Verificar!");
		txtIP = new JTextField("127.0.0.1");
		txtPorta = new JTextField("12345");
		txtNome = new JTextField("Cliente");
		Object[] texts = { lblMessage, txtIP, txtPorta, txtNome };
		JOptionPane.showMessageDialog(null, texts);

		pnlContent = new JPanel();
		texto = new JTextArea(10, 20);
		texto.setEditable(false);
		texto.setBackground(new Color(240, 240, 240));
		txtMsg = new JTextField(20);
		lblHistorico = new JLabel("Bate papo");
		lbDigitando = new JLabel("");
		lblMsg = new JLabel("Mensagem");
		btnSend = new JButton("Enviar");
		btnSend.setToolTipText("Enviar Mensagem");
		btnSair = new JButton("Sair");
		btnSair.setToolTipText("Sair do Chat");
		btnSend.addActionListener(this);
		btnSair.addActionListener(this);
		btnSend.addKeyListener(this);
		txtMsg.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(texto);
		texto.setLineWrap(true);
		pnlContent.add(lblHistorico);

		pnlContent.add(new JLabel(""));
		pnlContent.add(new JLabel(""));
		pnlContent.add(new JLabel(""));
		pnlContent.add(new JLabel(""));
		pnlContent.add(new JLabel(""));
		// pnlContent.add(lbtextDigitando);
		pnlContent.add(lbDigitando);
		pnlContent.add(scroll);
		pnlContent.add(lblMsg);
		pnlContent.add(txtMsg);
		pnlContent.add(btnSair);
		pnlContent.add(btnSend);
		pnlContent.setBackground(Color.LIGHT_GRAY);
		texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		setTitle(txtNome.getText());
		setContentPane(pnlContent);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void conectar() throws IOException {

		socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);
		bfw.write(txtNome.getText() + "\r\n");
		bfw.flush();
	}

	public void atualizarDigitando(String situacao) {
		if (situacao.equals(DIGITANDO)) {
			lbDigitando.setText("Alguém está digitando");
		} else {
			lbDigitando.setText("");
		}
	}

	public void enviarMensagem(String msg) throws IOException {

		if (msg.equals("Sair") || msg.equals("sair")) {
			bfw.write("Desconectado \r\n");
			texto.append("Desconectado \r\n");
		} else {

			if (msg.equals(DIGITANDO) || msg.equals(NAO_DIGITANDO)) {
				bfw.write(msg + "\r\n");
			} else {
				bfw.write(msg + "\r\n");
				texto.append(txtNome.getText() + ": " + txtMsg.getText() + "\r\n");
				txtMsg.setText("");
			}
			
			System.out.println(msg);

		}
		bfw.flush();

	}

	public void escutar() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (true)

			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Sair") || msg.equals("sair")) {
					texto.append("Servidor caiu! \r\n");
				} else {
					if(msg.contains(DIGITANDO)) {
						atualizarDigitando(DIGITANDO);
					}else if(msg.contains(NAO_DIGITANDO)) {
						atualizarDigitando(NAO_DIGITANDO);
					}else {
						texto.append(msg + "\r\n");
					}
					
				}

			}

	}

	public void sair() throws IOException {

		enviarMensagem("Sair");
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getSource() == btnSend) {
				enviarMensagem(txtNome.getText() + ": " + txtMsg.getText());
			} else if (e.getSource() == btnSair)
				sair();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensagem(txtNome.getText() + ": " + txtMsg.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				enviarMensagem(DIGITANDO);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		try {
			enviarMensagem(NAO_DIGITANDO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) throws IOException {

		Cliente cliente = new Cliente();

		cliente.conectar();
		cliente.escutar();
	}
}
