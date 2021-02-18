import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.Border;

public class ASCII extends JPanel implements ActionListener, AdjustmentListener{
	JFrame frame;
	JButton saveB, openB;
	JPanel buttonP, btmP, scrollBarP, labelP;

	File file;
	String ASCIIOutput;
	BufferedImage img;

	Color backgroundColor = Color.WHITE;
	Color textColor = Color.BLACK;
	Border blackline = BorderFactory.createLineBorder(Color.black);
	//char[]  map = {' ', '.', ',', ':', ';', 'x', 'o', '%', '#', '@', '@'};
	char[]  map = {'@', '@', '#', '%', 'o', 'x', ';', ':', ',', ',', ' '};
	int lineSpace;
	int charSpace;

	JScrollBar fontSc;
	JLabel fontL, instructionsL;
	int fontSize = 6;

	public ASCII(){
		frame = new JFrame("ASCII Maker");
		frame.setSize(1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		buttonP = new JPanel();
		openB = new JButton("Open File");
		openB.addActionListener(this);
		saveB = new JButton("Save File");
		saveB.addActionListener(this);
		buttonP.add(openB);
		buttonP.add(saveB);
		buttonP.setBorder(blackline);
		buttonP.setBackground(Color.RED);

		btmP = new JPanel(new BorderLayout());
		scrollBarP = new JPanel(new GridLayout(1, 1));
		fontSc = new JScrollBar(JScrollBar.HORIZONTAL, fontSize, 0, 1, 20);
		fontSc.addAdjustmentListener(this);
		scrollBarP.add(fontSc);
		labelP = new JPanel();
		fontL = new JLabel("Res: "+fontSize);
		labelP.add(fontL);
		instructionsL = new JLabel("Open a image file and then you export it as text file.", SwingConstants.CENTER);
		instructionsL.setFont(new Font("Arial", Font.PLAIN, 15));
		instructionsL.setForeground(Color.WHITE);
		btmP.add(instructionsL, BorderLayout.NORTH);
		btmP.add(labelP, BorderLayout.WEST);
		btmP.add(scrollBarP, BorderLayout.CENTER);
		btmP.setBorder(blackline);
		btmP.setBackground(Color.RED);

		this.setLayout(new BorderLayout());
		this.add(buttonP, BorderLayout.NORTH);
		this.add(btmP, BorderLayout.SOUTH);
		frame.add(this);
		frame.setVisible(true);
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());

 		lineSpace = g2.getFontMetrics(new Font("Courier New", Font.PLAIN, fontSize)).getHeight();
  		charSpace = g2.getFontMetrics(new Font("Courier New", Font.PLAIN, fontSize)).stringWidth("o");

		g2.setFont(new Font("Courier New", Font.PLAIN, fontSize));
		g2.setColor(textColor);
		//g2.drawString("     .    ,    :    ;    o    x    %    #    @", 0, 200);

		if(file != null){
			try{
				img = ImageIO.read(file);
				double width;
				double height;
				if(img.getWidth() >= img.getHeight() ){
					if(frame.getContentPane().getWidth()/2 < img.getWidth()){
						width = frame.getContentPane().getWidth()/2;
						height = (width/img.getWidth())*img.getHeight();
					}
					else{
						height = frame.getContentPane().getHeight()-buttonP.getHeight()-10;
						width = (height/img.getHeight())*img.getWidth();
					}
				}
				else{
					if(frame.getContentPane().getHeight() < img.getHeight()){
						height = frame.getContentPane().getHeight()-buttonP.getHeight()-10;
						width = (height/img.getHeight())*img.getWidth();
					}
					else{
						width = frame.getContentPane().getWidth()/2;
						height = (width/img.getWidth())*img.getHeight();
					}
				}


				int x = (int)((frame.getContentPane().getWidth()/2 - width)/2);
				int y = (int)(buttonP.getHeight() + (frame.getContentPane().getHeight() - buttonP.getHeight() - height) / 2);
				g2.drawImage(img, x, y, (int)width, (int)height, null);

				Image imgData = img.getScaledInstance((int)width/charSpace, (int)height/lineSpace, Image.SCALE_DEFAULT);
				BufferedImage lowRes = new BufferedImage(imgData.getWidth(null), (int)imgData.getHeight(null), BufferedImage.TYPE_INT_RGB);
				Graphics bg2 = lowRes.getGraphics();
				bg2.drawImage(imgData, 0, 0, null);
				bg2.dispose();

				float[][] bValues = convertToGrayscale(lowRes);
				//g2.drawImage(lowRes, 0, y, lowRes.getWidth(), lowRes.getHeight(), null);
				convertToASCII(g2, bValues, x+frame.getContentPane().getWidth()/2, y);

			}catch(Exception e){
				System.out.println("Could not display image: "+e.toString());
			}
		}

	}
	public float[][] convertToGrayscale(BufferedImage img) {
    	if (img == null)
    		return null;
    	float[][] bValues = new float[img.getHeight()][img.getWidth()];
    	for (int i = 0; i < img.getHeight(); i++) {
    		for (int j = 0; j < img.getWidth(); j++) {
    			Color imageColor = new Color(img.getRGB(j, i));
    			float[] hsb = Color.RGBtoHSB(imageColor.getRed(), imageColor.getGreen(), imageColor.getBlue(), null);
    			bValues[i][j] = hsb[2];
             	img.setRGB(j, i, Color.HSBtoRGB(0, 0, hsb[2]));
        	}
      	}
      	return bValues;
   	}
   	public void convertToASCII(Graphics2D g2, float[][] bValues,int x, int y){
		g2.setFont(new Font("Courier New", Font.PLAIN, fontSize));
		g2.setColor(textColor);
		ASCIIOutput = "";
		for(int i = 0; i < bValues.length; i++){
			String line = "";
			for(int j = 0; j < bValues[0].length; j++){
				line += map[(int)(bValues[i][j]*10)];
			}
			ASCIIOutput+=line+"\n";
			g2.drawString(line, x, y + lineSpace*i);
		}
	}
	public void openFile(){
		JFileChooser fc = new JFileChooser(".");
		FileFilter filter = new FileNameExtensionFilter("JPEG and PNG files", "jpg", "jpeg", "JPG", "JPEG", "png", "PNG");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION){
			file = fc.getSelectedFile();
			repaint();
		}
	}

	public void saveFile(){
		if(ASCIIOutput != null){
			JFileChooser fc = new JFileChooser(".");
			FileFilter filter = new FileNameExtensionFilter("*.txt","txt");
			fc.setFileFilter(filter);
			if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				try{
					String st=file.getAbsolutePath();
					if(!st.contains(".txt"))
						st+=".txt";
					FileWriter myWriter = new FileWriter(st);
					myWriter.write(ASCIIOutput);
					myWriter.close();
				}catch(IOException e){
					System.out.println("Error");
				}
			}
		}
		
	}

	public void actionPerformed(ActionEvent e){
		if(e.getSource() == openB){
			openFile();
		}
		if(e.getSource() == saveB){
			saveFile();
		}
	}
	public void adjustmentValueChanged(AdjustmentEvent e){
		if(e.getSource() == fontSc){
			fontSize = fontSc.getValue();
			fontL.setText("Res: "+fontSize);
		}
		repaint();
	}

	public static void main(String[] args){
		ASCII app = new ASCII();
	}

}