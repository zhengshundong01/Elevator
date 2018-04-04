package com.elevator.dispatching;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.UIManager;

class ElevatorFrame extends JFrame {

	public ElevatorFrame(String str) {
		super(str);
		setLookAndFeel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600); //设置窗口大小
		Container contentPane = getContentPane();
		ElevatorPanel panel = new ElevatorPanel(); 
		contentPane.add(panel);                    //建立关联
		setVisible(true);
	}
	public void setLookAndFeel() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(Exception exc){
			//ignore error
		}
	}
}
