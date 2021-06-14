package MCTS;

import java.util.ArrayList;
import java.util.LinkedList;

import aiinterface.CommandCenter;
import enumerate.Action;
import struct.FrameData;
import struct.GameData;

public class Node {

	// CONSTANTS

	// LINK
	public Action associatedAction;
	public Node parent;
	public ArrayList<Node> children;
	public int depth; // Profundidad

	// DATA
	public boolean player;
	public FrameData fd; // State
	public GameData gameData; // GameData
	public int t; // Total Score of Node
	public int n; // Times Visited
	public double ucb1; // Value of select criteria

	public Node(FrameData fd, GameData gameData, boolean player) {
		this.associatedAction = null;
		this.gameData = gameData;
		parent = null;
		children = null;
		this.player = player;
		this.fd = fd;

		depth = 0;
		n = 0;
		t = 0;
	}

	public Node(Node parent, GameData gameData, FrameData fd, boolean player, Action action) {
		this.associatedAction = action;
		this.gameData = gameData;
		this.parent = parent;
		children = null;

		this.player = player;
		this.fd = fd;

		depth = depth + 1;
		n = 0;
		t = 0;
	}

	public boolean isLeaf() {
		return children==null;
	}

	public void printNode(Node node) {
		System.out.println(" :" + node.n);
		for (int i = 0; i < node.children.size(); i++) {
			System.out.println(i + ", :" + node.children.get(i).n + ", :" + node.children.get(i).depth + ",score:"
					+ node.children.get(i).t / node.children.get(i).n + ",ucb:" + node.children.get(i).ucb1);
		}
		System.out.println("");
		for (int i = 0; i < node.children.size(); i++) {
				printNode(node.children.get(i));
		}
	}

}
