package com.elevator.dispatching;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

class ElevatorPanel extends JPanel {
	public ElevatorPanel() {
		setLayout(new BorderLayout()); 
		JLabel label =new JLabel("Elevator Test",JLabel.CENTER);
		add(label, BorderLayout.NORTH); 
		MainPanel mainPanel = new MainPanel();
		add(mainPanel);                   //显示4部电梯的面板
		validate();                       //验证子组件
		ControlPanel controlPanel = new ControlPanel(mainPanel);//显示电梯外部按钮的面板
		add(controlPanel, BorderLayout.SOUTH); 
	}
}
