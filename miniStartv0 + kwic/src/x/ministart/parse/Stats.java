package x.ministart.parse;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import x.ministart.sys.SwingUI;
import x.ministart.sys.SwingWorkerExecutor;
import x.ministart.utils.Chrono;

public class Stats {
	
private SwingUI ui;
protected String cherche;
public Stats i;

	public Stats(final SwingUI ui) {
		this.ui = ui;
		i=this;
		JFrame fenetre = new JFrame("mot a chercher");
		fenetre.setSize(300, 100);
		fenetre.setLayout(new BorderLayout());
		final JTextField text = new JTextField();
		fenetre.add(text);
		fenetre.setVisible(true);
		
		text.addActionListener (new ActionListener(){
			public void actionPerformed (ActionEvent e) {
				i.GiveWord(text.getText());
				text.setText("");
				SwingWorker<Integer, Object> swingWorker = new RunStats();
				SwingWorkerExecutor.instance().execute(swingWorker);
				swingWorker.addPropertyChangeListener( new PropertyChangeListener() {
					@Override
					public  void propertyChange(PropertyChangeEvent evt) {
						if ("progress".equals(evt.getPropertyName())) {
							if (ui.progressBar != null){
								ui.progressBar.setValue((Integer)evt.getNewValue());
							}
						}
					}

				});	
			}
		});
	}

	public void GiveWord(String mot){
		cherche=mot;
	}

	public class RunStats extends SwingWorker<Integer, Object> {

		private Chrono chrono = new Chrono();

		@Override
		protected Integer doInBackground() throws Exception {
			StyledDocument doc = (StyledDocument) ui.main_txtarea.getDocument();
		    ui.main_txtarea.setDocument(new DefaultStyledDocument());
			
			ui.statusInfo.setText ("Parse ALL - running");
			this.chrono.start();
			
			int nbLigne = 0, nbMot = 0, occ = 0, parse_ind = 0, docLen = doc.getLength();
			String mot = new String("");
			boolean litMot = false;
			try{
				
				for (int txt_ind = 0; txt_ind < docLen ; txt_ind++) {
					setProgress( (txt_ind) * 100 / docLen);
					
					char c_char = doc.getText(parse_ind, 1).charAt(0);
					if(c_char=='\n'){
						nbLigne++;
					}
					
					if(Character.isWhitespace(c_char) && litMot==true){
						litMot=false;
						nbMot++;
						if(mot.equals(cherche))
							occ++;
						mot="";
					}
					
					if(!Character.isWhitespace(c_char)){
						if(litMot==false)
							litMot=true;
						if(c_char!='.' && c_char!=',')
							mot=mot + c_char;
						
					}	
					++parse_ind;
				}
				
				
			}catch (BadLocationException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			ui.main_txtarea.setDocument(doc);
			System.out.println("nbLigne : " + nbLigne + " nbMot : " + nbMot + " et le mot : '" + cherche + "' apparait " + occ +" fois");
			return null;

		}

	}
}
