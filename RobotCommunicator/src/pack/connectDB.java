package pack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class connectDB extends OutputStream
{
    private final JTextArea destination;

    public connectDB (JTextArea destination)
    {
        if (destination == null)
            throw new IllegalArgumentException ("Destination is null");

        this.destination = destination;
    }
    



	@Override
    public void write(byte[] buffer, int offset, int length) throws IOException
    {
        final String text = new String (buffer, offset, length);
        SwingUtilities.invokeLater(new Runnable ()
            {
                @Override
                public void run() 
                {
                    destination.append (text);
                }
            });
    }

    @Override
    public void write(int b) throws IOException
    {
        write (new byte [] {(byte)b}, 0, 1);
    }
    
	// init database constants
	private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://107.180.51.230:3306/AlfredDatabase";
	private static final String USERNAME = "AlfredDataUser";
	private static final String PASSWORD = "B1m24]DVCJdy";
	private static final String MAX_POOL = "250";

	// init connection object
	private Connection connection;
	// init properties object
	private Properties properties;

	// create properties
	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			properties.setProperty("user", USERNAME);
			properties.setProperty("password", PASSWORD);
			properties.setProperty("MaxPooledStatements", MAX_POOL);
		}
		return properties;
	}

	// connect database
	public Connection connect() {
		if (connection == null) {
			try {
				Class.forName(DATABASE_DRIVER);
				connection = DriverManager.getConnection(DATABASE_URL, getProperties());
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	// disconnect database
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

    public static void main (String[] args) throws Exception
    {

    	
    	///
    	
        JTextArea textArea = new JTextArea (25, 80);

        textArea.setEditable (false);

        JFrame frameOut = new JFrame ("stdout");
        Container contentPane = frameOut.getContentPane ();
        contentPane.setLayout (new BorderLayout ());
        contentPane.add (
            new JScrollPane (
                textArea, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
            BorderLayout.CENTER);
        frameOut.pack ();
        frameOut.setVisible (false);

        JTextAreaOutputStream out = new JTextAreaOutputStream (textArea);
        System.setOut (new PrintStream (out));

		connectDB mysqlConnect = new connectDB(textArea);

		////////// Example/////////////////////////
		JFrame frame = new JFrame("Robot to Database");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel() {

			private static final long serialVersionUID = 1L;

			// Fills the panel with a red/black gradient
			protected void paintComponent(Graphics grphcs) {
				Graphics2D g2d = (Graphics2D) grphcs;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color color1 = new Color(12, 54, 122);
				Color color2 = Color.BLACK;
				GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight() - 100, color2);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				super.paintComponent(grphcs);
			}
		};

		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// Some info text at the top of the window
		JLabel welcome = new JLabel("<html><font size='5' color='white'>Send robot data to DB </font></html>", SwingConstants.CENTER);

		welcome.setPreferredSize(new Dimension(200, 25));
		welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		
		// Some info text at the top of the window
		JLabel pos = new JLabel("<html><font size='3' color='white'>Click on map to position robot </font></html>", SwingConstants.CENTER);

		pos.setPreferredSize(new Dimension(200, 25));
		pos.setAlignmentX(Component.CENTER_ALIGNMENT);

		// "Rigid Area" is used to align everything
		panel.add(Box.createRigidArea(new Dimension(0, 30)));
		panel.add(welcome);
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		panel.add(pos);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));

		
		JPanel map = new JPanel();
		map.setBackground(new Color(140, 140, 140));
		map.setMinimumSize(new Dimension(200, 100));
		map.setMaximumSize(new Dimension(200, 100));
		map.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(map);
		panel.add(Box.createRigidArea(new Dimension(0, 5)));
		JLabel robotCor = new JLabel(" ", SwingConstants.CENTER);
		robotCor.setPreferredSize(new Dimension(200, 20));
		robotCor.setMinimumSize(new Dimension(200, 20));
		robotCor.setMaximumSize(new Dimension(200, 20));
		robotCor.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(robotCor);
		panel.add(Box.createRigidArea(new Dimension(0, 30)));

		

		JButton button1 = new JButton("isBase = TRUE");

		button1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						String sql = "UPDATE robotLiveData SET Value='true' WHERE Stat='isBase'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					}
				});
			}

		});
		// Setting up button1
		button1.setAlignmentX(Component.CENTER_ALIGNMENT);
		button1.setMaximumSize(new Dimension(140, 30));
		button1.setMinimumSize(new Dimension(140, 30));


		// Button2 will open a new window where a custom setup can be created
		JButton button2 = new JButton("isBase = FALSE");
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						String sql = "UPDATE robotLiveData SET Value='false' WHERE Stat='isBase'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}

		});

		// Setting up button2
		button2.setAlignmentX(Component.CENTER_ALIGNMENT);
		button2.setMaximumSize(new Dimension(140, 30));
		button2.setMinimumSize(new Dimension(140, 30));

		
		JPanel buttonGroup1 = new JPanel();
		buttonGroup1.setLayout(new BoxLayout(buttonGroup1, BoxLayout.X_AXIS));
		buttonGroup1.setPreferredSize(new Dimension(290, 30));
		buttonGroup1.setMinimumSize(new Dimension(290, 30));
		buttonGroup1.setMaximumSize(new Dimension(290, 30));
		buttonGroup1.setOpaque(false);



		buttonGroup1.add(button1);
		buttonGroup1.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonGroup1.add(button2);

		panel.add(buttonGroup1);
		
		
		
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		

		JButton button3 = new JButton("isMoving = TRUE");

		button3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						String sql = "UPDATE robotLiveData SET Value='true' WHERE Stat='isMoving'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					}
				});
			}

		});
		// Setting up button1
		button3.setAlignmentX(Component.CENTER_ALIGNMENT);
		button3.setMaximumSize(new Dimension(140, 30));
		button3.setMinimumSize(new Dimension(140, 30));


		JButton button4 = new JButton("isMoving = FALSE");
		button4.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						String sql = "UPDATE robotLiveData SET Value='false' WHERE Stat='isMoving'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					}
				});
			}

		});

		// Setting up button2
		button4.setAlignmentX(Component.CENTER_ALIGNMENT);
		button4.setMaximumSize(new Dimension(140, 30));
		button4.setMinimumSize(new Dimension(140, 30));

		
		JPanel buttonGroup2 = new JPanel();
		buttonGroup2.setLayout(new BoxLayout(buttonGroup2, BoxLayout.X_AXIS));
		buttonGroup2.setPreferredSize(new Dimension(290, 30));
		buttonGroup2.setMinimumSize(new Dimension(290, 30));
		buttonGroup2.setMaximumSize(new Dimension(290, 30));
		buttonGroup2.setOpaque(false);



		buttonGroup2.add(button3);
		buttonGroup2.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonGroup2.add(button4);

		panel.add(buttonGroup2);
		
		/////
		
		

		
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		

		JButton button5 = new JButton("isWaitOrder = T");

		button5.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String sql = "UPDATE robotLiveData SET Value='true' WHERE Stat='isWaitOrder'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					}
				});
			}

		});
		// Setting up button1
		button5.setAlignmentX(Component.CENTER_ALIGNMENT);
		button5.setMaximumSize(new Dimension(140, 30));
		button5.setMinimumSize(new Dimension(140, 30));


		JButton button6 = new JButton("isWaitOrder = F");
		button6.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String sql = "UPDATE robotLiveData SET Value='false' WHERE Stat='isWaitOrder'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}

		});

		// Setting up button2
		button6.setAlignmentX(Component.CENTER_ALIGNMENT);
		button6.setMaximumSize(new Dimension(140, 30));
		button6.setMinimumSize(new Dimension(140, 30));

		
		JPanel buttonGroup3 = new JPanel();
		buttonGroup3.setLayout(new BoxLayout(buttonGroup3, BoxLayout.X_AXIS));
		buttonGroup3.setPreferredSize(new Dimension(290, 30));
		buttonGroup3.setMinimumSize(new Dimension(290, 30));
		buttonGroup3.setMaximumSize(new Dimension(290, 30));
		buttonGroup3.setOpaque(false);



		buttonGroup3.add(button5);
		buttonGroup3.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonGroup3.add(button6);

		panel.add(buttonGroup3);
		
		
		/////
		
		

		
		panel.add(Box.createRigidArea(new Dimension(0, 20)));

		

		JButton button7 = new JButton("isWaitAdmin = T");

		button7.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						String sql = "UPDATE robotLiveData SET Value='true' WHERE Stat='isWaitAdmin'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}

						
						
						
					}
				});
			}

		});
		// Setting up button1
		button7.setAlignmentX(Component.CENTER_ALIGNMENT);
		button7.setMaximumSize(new Dimension(140, 30));
		button7.setMinimumSize(new Dimension(140, 30));


		JButton button8 = new JButton("isWaitAdmin = F");
		button8.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						String sql = "UPDATE robotLiveData SET Value='false' WHERE Stat='isWaitAdmin'";

						System.out.println(sql);

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
							statement.executeUpdate();

						} catch (SQLException e) {
							e.printStackTrace();
						}
						String sql2 = "SELECT * FROM `robotLiveData`";

						try {
							PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
							ResultSet rs = statement.executeQuery();

							while (rs.next()) {
								System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
							}
							System.out.println("------------");

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
			}

		});

		// Setting up button2
		button8.setAlignmentX(Component.CENTER_ALIGNMENT);
		button8.setMaximumSize(new Dimension(140, 30));
		button8.setMinimumSize(new Dimension(140, 30));

		
		JPanel buttonGroup4 = new JPanel();
		buttonGroup4.setLayout(new BoxLayout(buttonGroup4, BoxLayout.X_AXIS));
		buttonGroup4.setPreferredSize(new Dimension(290, 30));
		buttonGroup4.setMinimumSize(new Dimension(290, 30));
		buttonGroup4.setMaximumSize(new Dimension(290, 30));
		buttonGroup4.setOpaque(false);



		buttonGroup4.add(button7);
		buttonGroup4.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonGroup4.add(button8);

		panel.add(buttonGroup4);
		
		
		
		
		
		
		
		
		
		// Setting up the start Menu
		frame.setSize(400, 600);
		frame.setResizable(false);

		// Centers the window with respect to the user's screen resolution
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);

		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);


		map.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent me) {
			}

			public void mouseReleased(MouseEvent me) {
			}

			public void mouseEntered(MouseEvent me) {
			}

			public void mouseExited(MouseEvent me) {
			}

			public void mouseClicked(MouseEvent me) {
				int x = me.getX();
				int y = me.getY();
				robotCor.setText("<html><center><font color='white'> Sent X: "+x +" Y: " + y+ " </font></html>");

				String sql1 = "UPDATE robotLiveData SET Value='" + x + "' WHERE Stat='robotX'";

				System.out.println(sql1);

				try {
					PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql1);
					statement.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}

				String sql2 = "UPDATE robotLiveData SET Value='" + y + "'	WHERE Stat='robotY'";
				System.out.println(sql2);

				try {
					PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql2);
					statement.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("------------");

			}
		});
		
		frame.setVisible(true);

		////

		String sql = "SELECT * FROM `robotLiveData`";

		try {
			PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("------------");

		//mysqlConnect.disconnect();
		
		
    	JFrame frame2 = new JFrame();
    	JPanel panel2 = new JPanel();	
    	
    	// Some info text at the top of the window
		JLabel status = new JLabel("TEST", SwingConstants.CENTER);

		panel2.setBackground(new Color(56, 71, 96));
		
		status.setAlignmentX(Component.CENTER_ALIGNMENT);
		status.setAlignmentY(Component.CENTER_ALIGNMENT);
		status.setVerticalAlignment(JLabel.CENTER);
		status.setPreferredSize(new Dimension(200, 100));
		
		frame2.add(panel2);
		panel2.add(status);
		panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel2.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		frame2.setSize(200, 200);
		frame2.setVisible(true);
    	
		while(true){
			System.out.print("!");
			
			String sql3 = "SELECT Value FROM robotLiveData WHERE Stat='actionToDo'";

			try {
				PreparedStatement statement = mysqlConnect.connect().prepareStatement(sql3);
				ResultSet rs = statement.executeQuery();

				while (rs.next()) {
					status.setText("<html><font size='9' color='white'>"+rs.getString(1)+"</font></html>");	
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		    Thread.sleep(300);
		    


		}

	}

}
