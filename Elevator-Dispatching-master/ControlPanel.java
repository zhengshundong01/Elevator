package com.elevator.dispatching;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

class ControlPanel extends JPanel {
	JTextField People=new JTextField(6);        
	JTextField Weight=new JTextField(6);        
	JComboBox currentFloorCombo;                
	MainPanel mainPanel;                        //显示四部电梯的区域的panel的引用，方便消息传递

	public ControlPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		Border b = BorderFactory.createEtchedBorder(); 
		setBorder(b); 
		addButton(); 
	}

	//	加入操作按钮：向上、向下按钮
	private void addButton() {
		final JButton upButton = new JButton("UP");
		upButton.setEnabled(false);
		final JButton downButton = new JButton("DOWN");
		downButton.setEnabled(false);
		JButton peoButton = new JButton("People");
		JButton weiButton = new JButton("Weight");

		// 为upButton按钮提供监听器类
		ActionListener UpL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					//从下拉列表框中，读出在电梯外，哪一层有按钮命令
	String InputStr = (String) currentFloorCombo.getSelectedItem();
				int floorNo = Integer.parseInt(InputStr);
				if (floorNo != -1) {	
					mainPanel.addCommand(floorNo, 1);
				} else {
					mainPanel.addCommand(floorNo, 2);
				}
			}
		};
		upButton.addActionListener(UpL);

		//	为downButton按钮提供监听器类
		ActionListener DownL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					//	从下拉列表框中，读出在电梯外，哪一层有按钮命令
	String InputStr = (String) currentFloorCombo.getSelectedItem();
	int floorNo = Integer.parseInt(InputStr);
					mainPanel.addCommand(floorNo, 2);
			}
		};
		downButton.addActionListener(DownL);
		
		//为peoButton按钮提供监听器类
		ActionListener Peo=new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//读出people文本区域的数字，大于零小于十，否则不允许使用 up down按钮
				String str=People.getText();
				String str1=Weight.getText();
				int wei=Integer.parseInt(str1);
				int peo=Integer.parseInt(str);
				if((peo>0&&peo<=10)&&(wei>0&&wei<=800)){
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
				else{
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
				}
		};
		peoButton.addActionListener(Peo);
		ActionListener Wei=new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//读出people文本区域的数字，大于零小于十，否则不允许使用 up down按钮
				String str=Weight.getText();
				String str1=People.getText();
				int peo=Integer.parseInt(str1);
				int wei=Integer.parseInt(str);
				if((wei>0&&wei<=800)&&(peo>0&&peo<=10)){
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
				else{
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
				}
		};
		weiButton.addActionListener(Wei);
		add(People);
		add(peoButton);
		add(Weight);
		add(weiButton);
		addComboBox(); 
		add(upButton);
		add(downButton);
	}

	//	加入下拉列表框，模拟在不同的楼层按下“向上”、”向下“按钮
	private void addComboBox() {

		currentFloorCombo = new JComboBox();
		currentFloorCombo.setEditable(true);
		for (int i = 0; i <= 20; ++i) {
				currentFloorCombo.addItem("" + i);
		}

		currentFloorCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

			}
		});

		add(currentFloorCombo); //	往panel中加入下拉列表框
 
		People.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
	}
}
