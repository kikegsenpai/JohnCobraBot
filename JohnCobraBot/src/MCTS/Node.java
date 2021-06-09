package MCTS;

import java.util.ArrayList;

import struct.FrameData;

public class Node {
	//Linked
	public Node parent;
	public ArrayList<Node> children;
	public int depth;
	
	//Data
	public boolean player;
	public FrameData fd;				//State
	public int t;						//Total Score of Node
	public int n;						//Times Visited
	
	
	public Node(FrameData fd,boolean player) {
		parent=null;
		this.player=player;
		this.fd=fd;
		
		depth=0;
		n=0;
		t=0;
	}
	
	public Node(Node parent, FrameData fd, boolean player) {
		this.parent=parent;
		this.player=player;
		this.fd=fd;
		
		depth=depth+1;
		n=0;
		t=0;	
	}
	
	public double calculateUCB1() {
		double c=Math.sqrt(2);
		double result;
		if(n==0)
			result=Double.MAX_VALUE;
		else
			result = t/n+c*Math.sqrt(Math.log(this.parent.n)/n);
		
		return result;
	}
	
	public double calculateScore() {
		double myHp = fd.getCharacter(player).getHp();
		double opponentHp = fd.getCharacter(!player).getHp();
		
		return myHp-opponentHp;
	}
}
