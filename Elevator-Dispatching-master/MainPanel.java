package com.elevator.dispatching;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

class MainPanel extends JPanel {

	SubPanel[] panel;    
	private Timer timer; //定时器
	int[] Up = new int[21];   
	int[] Down = new int[21]; 
	//	定时器类
	private class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (hasUpTask() || hasDownTask()) {
				Command();                
			}
		}
	}
	//	判断各个楼层有没有向上的请求
	private boolean hasUpTask() {
		for (int i = 0; i < Up.length; i++) {
			if (Up[i] == 1) {
				return true;
			}
		}
		return false;
	}
	//	判断各个楼层有没有向下的请求
	private boolean hasDownTask() {
		for (int i = 0; i < Down.length; ++i) {
			if (Down[i] == 2) {
				return true;
			}
		}
		return false;
	}

	public MainPanel() {

		setLayout(new GridLayout(1, 4)); 
		panel = new SubPanel[4];        
		panel[0] = new SubPanel("one"); 
		panel[1] = new SubPanel("two");
		panel[2] = new SubPanel("three");
		panel[3] = new SubPanel("four");
		add(panel[0]);                  
		add(panel[1]);
		add(panel[2]);
		add(panel[3]);
		for (int i = 0; i <= 20; ++i) {
			Down[i] = 0; 
		}
		for (int i = 0; i <= 20; ++i) {
			Up[i] = 0; 
		}
		ActionListener timerlistener = new TimerListener(); 
		timer = new Timer(1000, timerlistener);             //时间间隔为1s
		timer.start();                                      
	}

	//	派发向上或者向下指令
	public void addCommand(int floorNo, int direction) {
		if (direction == 1) {           // 1 代表向上
			Up[floorNo] = 1;
		} else if (direction == 2) {   
			Down[floorNo] = 2;
		}
		Command();              
	}

	// 得到正在运行的电梯数组
	private ArrayList getRunnableElevator() {
		ArrayList runnableElevator = new ArrayList();
		for (int i = 0; i < 4; i++) {
			if (panel[i].getRunnabel()) {
				runnableElevator.add(panel[i]);
			}
		}
		return runnableElevator;
	}

	//	派发命令的函数
	public void Command() {
		ArrayList runnableElevators = getRunnableElevator(); 
		if (runnableElevators.size() == 0) {                 
			for (int i = 0; i < 21; ++i) {
				Up[i] = 0;
				Down[i] = 0;
			}
			return;
		}
		if (hasUpTask()) {                 
			upTask(runnableElevators);

		} else if (hasDownTask()) {        
			downTask(runnableElevators);
		}
	}

	//	当有电梯正在向上，且某楼层有“向上”请求，并且发出“向上“请求的楼层高于正在向上电梯的当前楼层，则调用此方法
	private void upingElevator(ArrayList runnableElevators) {
		if (runnableElevators.size() == 0) {
			return;
		}
		for (int i = 0; i < Up.length; i++) {
			if (Up[i] == 1) {
				int nearest = -1;
				int nearestElevator = -1;
				for (int j = 0; j < runnableElevators.size(); j++) {
					if (((SubPanel) runnableElevators.get(j)).getCurrentState() == 1) {
						//这里需要调用SubPanel类中的getCurrentState()方法，故需要强制类型转换
						int temp = ((SubPanel) runnableElevators.get(j)).getCurrentFloor();
						if (temp > nearest && temp < i) {
							nearest =((SubPanel) runnableElevators.get(j)).getCurrentFloor();
							nearestElevator = j;
						}
					}
				}
				if (nearest != -1) {
					(
						(SubPanel) runnableElevators.get(nearestElevator)).setTask(i);
					//设置使距离请求楼层最近的电梯接收向上命令
					Up[i] = 0;
					nearest = -1;
					nearestElevator = -1;
				}
			}
		}
	}

    //	当有电梯正在向下，且某楼层有“向下”请求，并且发出“向下“请求的楼层低于正在向上电梯的当前楼层，则调用此方法
	private void downingElevator(ArrayList runnableElevators) {
		System.out.println("new task ");
		if (runnableElevators.size() == 0) {
			return;
		}
		for (int i = 0; i < Down.length; i++) {
			if (Down[i] == 2) {
				int nearest = 22;
				int nearestElevator = -1;
				for (int j = 0; j < runnableElevators.size(); j++) {
					if (((SubPanel) runnableElevators.get(j)).getCurrentState() == 2) {
						int temp = ((SubPanel) runnableElevators.get(j)).getCurrentFloor();
						if (temp < nearest && temp > i) {
							nearest = ((SubPanel) runnableElevators.get(j)).getCurrentFloor();
							nearestElevator = j;
						}
					}
				}
				if (nearestElevator != -1) {
					(
						(SubPanel) runnableElevators.get(nearestElevator)).setTask(i);
					Down[i] = 0;
					nearest = 22;
					nearestElevator = -1;
				}
			}
		}
	}

	//	对停着的电梯，楼层有“向下”请求，选择最靠近的电梯
	private void stopElevatorDown(ArrayList runnableElevators) {
		if (runnableElevators.size() == 0) {
			return;
		}
		for (int i = 0; i < Down.length; i++) {
			if (Down[i] == 2) {
				int nearest = 22;
				int nearestElevator = -1;
				for (int j = 0; j < runnableElevators.size(); j++) {
					if (((SubPanel) runnableElevators.get(j)).getCurrentState()== 0) {
						int temp =((SubPanel) runnableElevators.get(j)).getCurrentFloor();
						if (Math.abs(i - temp) < nearest) {
							nearest = Math.abs(i - temp);
							nearestElevator = j;
						}
					}
				}
				if (nearestElevator != -1) {
					(
						(SubPanel) runnableElevators.get(nearestElevator)).setTask(i);
					Down[i] = 0;
					nearest = 22;
					nearestElevator = -1;
				}
			}
		}
	}

	//	对停着的电梯，楼层有“向上”请求，选择最靠近的电梯
	private void stopElevatorUp(ArrayList runnableElevators) {
		if (runnableElevators.size() == 0) {
			return;
		}
		for (int i = 0; i < Up.length; i++) {
			if (Up[i] == 1) {
				int nearest = 22;
				int nearestElevator = -1;
				for (int j = 0; j < runnableElevators.size(); j++) {
					if (((SubPanel) runnableElevators.get(j)).getCurrentState()== 0) {
						int temp = ((SubPanel) runnableElevators.get(j)).getCurrentFloor();
						if (Math.abs(i - temp) < nearest) {
							nearest = Math.abs(i - temp);
							nearestElevator = j;
						}
					}
				}
				if (nearestElevator != -1) {
					(
						(SubPanel) runnableElevators.get(
							nearestElevator)).setTask(
						i);
					Up[i] = 0;
					nearest = 22;
					nearestElevator = -1;
				}
			}
		}
	}

	//	处理各楼层的“向上”指令
	private void upTask(ArrayList runnableElevators) {
		for (int i = 0; i < Up.length; ++i) {
			if (Up[i] == 1) { 
				upingElevator(runnableElevators);
				stopElevatorUp(runnableElevators);
			}
		}

	}

	//	处理各楼层的“向下”指令
	private void downTask(ArrayList runnableElevators) {
		for (int i = 0; i < Down.length; ++i) {
			if (Down[i] == 2) { 
				downingElevator(runnableElevators);
				stopElevatorDown(runnableElevators);
			}
		}
	}

	//	判断所有电梯是否都停着
	private boolean isAllStoped(ArrayList runnableElevators) {
		for (int i = 0; i < runnableElevators.size(); i++) {
			if (!((SubPanel) runnableElevators.get(i)).isStoped()) {
				return false;
			}
		}
		return true;
	}
}
