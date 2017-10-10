package net.undergroundim.client.gui;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.undergroundim.client.AudioPlayer;
import net.undergroundim.client.Client;
import net.undergroundim.client.Constants;
import net.undergroundim.client.ImageRenderer;


/**
 * 
 * @author Troy
 *
 */
public class FriendList extends JFrame{
	private static final long serialVersionUID = -2834540331055941876L;
	
	public JTable friendList;
	private JScrollPane friendListContainer;
	private DefaultTableModel friendListData;

	public PopupMenu popupMenu;
	public MenuBar menuBar = new MenuBar();

	/**
	 * Constructor.
	 */
	public FriendList(){
		this.setIconImage(Constants.icon);
		this.setTitle("Underground IM");
		this.setSize(300, 400);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setJMenuBar(menuBar.getMenuBar());
		
		//Friend List
		friendListData = new DefaultTableModel(new Object[0][0],new String[]{"Status","Friends"}){
			private static final long serialVersionUID = -3666163903937562582L;

			@Override
			public boolean isCellEditable(int a, int b){
				return false;
			}
		};
		
		friendList = new JTable(friendListData);
		friendList.setBounds(0, 0, getWidth() -15, getHeight() -60);
		friendList.setFont(new Font("Arial", Font.PLAIN, 12));
		friendList.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		
		friendListContainer = new JScrollPane(friendList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		friendListContainer.setBounds(0, 0, getWidth() -15, getHeight() -90);
		
		//Add components
		this.add(friendListContainer);
		
		popupMenu = new PopupMenu(null,friendList);
		
		TrayIcon trayIcon = null;
		if(SystemTray.isSupported()/* && !System.getProperty("os.name").toLowerCase().contains("linux")*/) {
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = Constants.icon;
		    
		    int trayIconWidth = new TrayIcon(image).getSize().width;
		    
		    trayIcon = new TrayIcon(image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Underground IM", null);
		    trayIcon.addMouseListener(new MouseAdapter() {
		        public void mouseReleased(MouseEvent ee) {
		            if(ee.getButton() == 3) {
		            	popupMenu.getTrayMenu().setLocation(System.getProperty("os.name").toLowerCase().contains("linux") ? ee.getX() - 50 : ee.getX(), 
		            			System.getProperty("os.name").toLowerCase().contains("linux") ? ee.getY() - 130 : ee.getY());
		            	popupMenu.getTrayMenu().setInvoker(popupMenu.getTrayMenu());
		            	popupMenu.getTrayMenu().setVisible(true);
		            }else if(ee.getButton() == 1){
		            	Constants.getFriendList().setVisible(true);
		            }
		        }
		    });

		    try{
		        tray.add(trayIcon);
		    }catch (AWTException e) {
		        System.err.println(e);
		    }
		}else{
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	}
	
	/**
	 * Method to update the friend's list.
	 */
	public void updateFriends(){
		//Clear rows
		int count = friendListData.getRowCount();
		boolean online = false;

		for(int i = 0; i < count; i++)		
			friendListData.removeRow(0);
		
		//Add rows
		for(Client c : Constants.getFriends()){
			if(c.isOnline()){
				switch(c.getStatus()){
				case 0:
					if(!c.isPlayedSound()){
						online = true;
						c.setPlayedSound(true);
					}
					friendListData.addRow(new Object[]{Constants.onlineIcon,c.getUsername()});
					break;
				case 1:
					friendListData.addRow(new Object[]{Constants.awayIcon,c.getUsername()});
					break;
				case 2:
					friendListData.addRow(new Object[]{Constants.dndIcon,c.getUsername()});
					break;
				}
			}else{
				friendListData.addRow(new Object[]{Constants.offlineIcon,c.getUsername()});
			}
		}

		repaint();
		
		if(Constants.isPlaySounds() && online)
			AudioPlayer.ONLINE.play(Constants.getVolume());
	}

}
