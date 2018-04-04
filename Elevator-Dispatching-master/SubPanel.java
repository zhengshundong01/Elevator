package com.elevator.dispatching;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

class SubPanel extends JPanel implements Runnable {
	
	int[] FloorStop ={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	Thread thread;      
	int CurrentFloor;   
	boolean goFlag;     
	boolean runFlag;    
	int CurrentState;   		
	JComboBox DestFloorCombo;
	String strName;
	private JButton[] dispButton;         //使用按钮来模拟楼层，初始颜色为白色，电梯关闭颜色为蓝色，开门颜色为绿色
	private JLabel dispCurrentFloorLabel;
	private JButton[] operatorButtons;    //模拟在电梯内部的操作按钮 （0 到 20楼） 
	
	public String toString() {
		return strName;
	}

	SubPanel(String str) {
		strName = str;
		thread = new Thread(this);
		thread.setDaemon(true);                        //标记为守护线程
		Border b = BorderFactory.createEtchedBorder(); 
		Border titled = BorderFactory.createTitledBorder(b, str);
		setBorder(titled);                             
		setLayout(new BorderLayout()); 
		JPanel panelControl = new JPanel();            //一部电梯的控制区，控制电梯是否处于工作状态
		CurrentFloor = 1;  
		CurrentState = 0;  
		runFlag = false;   
		goFlag = false;    
		final JButton startButton = new JButton("start"); 
		final JButton stopButton = new JButton("stop");  
		stopButton.setEnabled(false);
		// 启动电梯按钮的监听器类
		ActionListener startL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runFlag = true;
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				for(int i=0;i<operatorButtons.length;++i)
				{
					operatorButtons[i].setEnabled(true);
				}
			}
		};
		startButton.addActionListener(startL);
		panelControl.add(startButton);

		// 关闭电梯按钮的监听器类
		ActionListener stopL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (goFlag == false) {

					runFlag = false;
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					for(int i=0;i<operatorButtons.length;++i)
					{
						operatorButtons[i].setEnabled(false);
					}
				}
			}
		};
		stopButton.addActionListener(stopL);
		panelControl.add(stopButton);
		add(panelControl, BorderLayout.SOUTH);  
		JPanel panelCtrlInElevator = new JPanel();
		panelCtrlInElevator.setLayout(new BorderLayout());
		dispCurrentFloorLabel = new JLabel("                          " + "1");
		panelCtrlInElevator.add(dispCurrentFloorLabel, BorderLayout.NORTH);

		//	电梯内的按钮添加
		JPanel panelButtonsInEvelator = new JPanel();
		panelButtonsInEvelator.setLayout(new GridLayout(20, 2));
		for (int i = 0; i < 4; ++i) {
			panelButtonsInEvelator.add(new JLabel(""));
		}
		operatorButtons = new JButton[21]; //	代表 21 层的21 个按钮
		for (int i = 0; i < 21; ++i) {
				operatorButtons[i] = new JButton("" + i);
			operatorButtons[i].setEnabled(false);
			panelButtonsInEvelator.add(operatorButtons[i]);
			addAction(operatorButtons[i]);
		}
		panelCtrlInElevator.add(panelButtonsInEvelator, BorderLayout.CENTER);
		add(panelCtrlInElevator, BorderLayout.CENTER);

		// 模拟楼层
		JPanel panelElevator = new JPanel();
		panelElevator.setLayout(new GridLayout(21, 1));
		dispButton = new JButton[21];
		for (int i = 0; i < 21; ++i) {
			dispButton[i] = new JButton("      ");
			dispButton[i].setBackground(Color.white); //	初始化的颜色是白色
			dispButton[i].setEnabled(true);
			panelElevator.add(dispButton[i]);
		}
		dispButton[19].setBackground(Color.blue);    // 一楼在初始化时 颜色是红色，表示电梯在一楼
		add(panelElevator, BorderLayout.WEST);
		thread.start(); 
	}

	// 在电梯内部，接受到哪一层楼的指令的监听器类
	private void addAction(final JButton button) {
		ActionListener bL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int num = Integer.parseInt(button.getText().trim());
				if (runFlag != false) {
					if (isStop(FloorStop,num,CurrentFloor,CurrentState)) {
						button.setBackground(Color.blue);
						goFlag = true;
						synchronized (thread) {
							thread.notify();
						}
					}
				}
			}
		};
		button.addActionListener(bL);
	}

	
	//	当某一部电梯有新的任务时，设置该部电梯要停靠的楼层的数组
	public void setTask(int floorNo) {
		FloorStop[floorNo] = 1;
		if (CurrentState == 0) {
			goFlag = true;
			synchronized (thread) {
				//同步一个线程
				thread.notify();
				//唤醒正在等待该对象互斥锁的第一个线程
			}
		}

	}
	
	//	返回该部电梯是否处于工作状态
	public boolean getRunnabel() {
		return runFlag;
	}
	
	//  返回该部电梯是否处于停着
	public boolean isStoped() {
		if (CurrentState == 0) {
			return true;
		} else {
			return false;
		}
	}
	//	返回该部电梯所处的状态：0 停着； 1 正在向上； 2 正在向下
	public int getCurrentState() {
		return CurrentState;
	}
	
	//	返回该部电梯所处的楼层
	public int getCurrentFloor() {
		return CurrentFloor;
	}

	// 当电梯在向上时，只接受高于当前楼层的停靠请求,当电梯在向下时，只接受低于当前楼层的停靠请求
	private boolean isStop(int[] FloorStop,int num,int CurrentFloor,int CurrentState) {
		if (CurrentState == 1) {
			if (num > CurrentFloor) {
				FloorStop[num] = 1;
				return true;
			}
		} else if (CurrentState == 2) {
			if (num < CurrentFloor) {
				FloorStop[num] = 1;
				return true;
			}
		} else {
			FloorStop[num] = 1;
			return true;
		}
		return false;
	}
	
	//判断电梯停靠楼层与请求楼层是否一样
	private boolean isTheSameFloor(int[] FloorStop) {
		if (FloorStop[CurrentFloor] == 1) {
			return true;
		}
		return false;
	}
	
	//判断电梯是否在上升
	private boolean isUP(int[] FloorStop) {
		int i;
		for (i = 0; i <= 20; ++i) {
			if (FloorStop[i] == 1) {
				break;
			}
		}
		if (CurrentFloor < i) {
			return true;
		} else {
			return false;
		}

	}
	
	//	判断该部电梯，是否还要继续向上运行
	private boolean isStillUP(int[] FloorStop, int Current) {
		int i;
		for (i = Current + 1; i <= 21; i++) {
			if (FloorStop[i] == 1) {
				return true;
			}
		}
		return false;
	}
	//	判断该部电梯，是否还要继续向下运行
	private boolean isStillDOWN(int[] FloorStop, int Current) {
		int i;
		for (i = Current; i >= 0; --i) {
			if (FloorStop[i] == 1) {
				return true;
			}
		}
		return false;
	}

	//	判断该部电梯，是否要在某个楼层停靠
	private boolean isStoped(int[] FloorStop, int Current) {
		if (FloorStop[Current] == 1) {
			return true;
		}
		return false;

	}

	// 	处理该电梯到了某个目的楼层后的处理工作
	private void arrivalDisp(int CurrentFloor) 
	{
		operatorButtons[CurrentFloor].setBackground(Color.green);
	}

	//	当电梯在运行时，处理到了某个中途楼层后的显示工作
	private void updateDisp(int CurrentFloor) {
		for (int i = 0; i < dispButton.length; i++) {
			if ((i + CurrentFloor) == 20) {
				dispButton[i].setBackground(Color.blue);
			} else {
				dispButton[i].setBackground(Color.white);
			}
		}
		dispCurrentFloorLabel.setText("                          " + CurrentFloor);
	}

	//	线程运行的函数
	public void run() {
		while (true) {
			try {
				synchronized (thread) {
					if (!runFlag || !goFlag) {
						thread.wait();
					}
				}

				if (isTheSameFloor(FloorStop)) {	
					updateDisp(CurrentFloor);
					FloorStop[CurrentFloor] = 0;
					Thread.sleep(500);
					dispButton[20 - CurrentFloor].setBackground(Color.green);
					arrivalDisp(CurrentFloor);
					Thread.sleep(2500);
					operatorButtons[CurrentFloor].setBackground(Color.lightGray);
					dispButton[20 - CurrentFloor].setBackground(Color.blue);
				} 
				else if (isUP(FloorStop)) {		
					CurrentState = 1;				
					while (isStillUP(FloorStop, CurrentFloor)) {
						CurrentFloor++;
						updateDisp(CurrentFloor);
						if (isStoped(FloorStop, CurrentFloor)) {	
							Thread.sleep(1000);	
							//	显示开门
							dispButton[20 - CurrentFloor].setBackground(Color.green);
							arrivalDisp(CurrentFloor);
							Thread.sleep(1000);	
							FloorStop[CurrentFloor] = 0;
							operatorButtons[CurrentFloor].setBackground(Color.lightGray);
							Thread.sleep(1000);						
						}

						//	显示关门
						dispButton[20 - CurrentFloor].setBackground(Color.blue);
						Thread.sleep(1000);
					}
				} else {					    // 在该电梯向下运行时的处理
					CurrentState = 2;			// 设置电梯状态
					while (isStillDOWN(FloorStop, CurrentFloor)) {
						CurrentFloor--;
						updateDisp(CurrentFloor);
						if (isStoped(FloorStop, CurrentFloor)) {
							Thread.sleep(1000);
							dispButton[20 - CurrentFloor].setBackground(Color.green);
							arrivalDisp(CurrentFloor);
							Thread.sleep(1000);
							FloorStop[CurrentFloor] = 0;
							operatorButtons[CurrentFloor].setBackground(Color.lightGray);
							Thread.sleep(1000);
						}
						dispButton[20 - CurrentFloor].setBackground(Color.blue);
						Thread.sleep(1000);
					}
				}
				goFlag = false;
				CurrentState = 0;
			} catch (InterruptedException e) {
			}
		}
	}
}
