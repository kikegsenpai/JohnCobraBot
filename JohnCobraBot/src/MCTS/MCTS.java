package MCTS;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

import aiinterface.CommandCenter;
import enumerate.Action;
import enumerate.State;
import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;

public class MCTS {
	
	//CONSTANTS
	public static final int N_FRAMES_SIMULATED = 60;	
	public static final int N_ITERATIONS = 10;	

	public static final double C = Math.sqrt(2);

	
	//DATA
	boolean player;
	Node root;
	
	private LinkedList<Action> myActionPool;
	private LinkedList<Action> enemyActionPool;
	private Simulator simulator;
	
	/*
	private LinkedList<Action> selectedMyActions;
	private int myOriginalHp;
	private int oppOriginalHp;
	private FrameData frameData;
	private CommandCenter commandCenter;
	private GameData gameData;
	private boolean isCreateNode;
	Deque<Action> mAction;
	Deque<Action> oppAction;
	*/
	
	private Random random;
	private CharacterData oppCharacter;
	private CharacterData myCharacter;

	
	public MCTS(boolean player) {
		random=new Random();
		this.player=player;
	}
	
	public void expansion(Node node) {
		node.children=new ArrayList<Node>();
		for(int i=0;i<myActionPool.size();i++) {
			node.children.add(new Node(node,node.gameData, node.fd, player, myActionPool.get(i)));
		}
		
	}
	
	public Node selection(Node node) {
		
		Node selectedNode=null;
		double bestUCB1=-9999;
		for (Node child : node.children) {
			if (child.n == 0) {
				calculateUCB1(child);
			}
			if(bestUCB1 < child.ucb1) {
		        selectedNode = child;
		        bestUCB1 = child.ucb1;
			}
		}
		
		return selectedNode;
	}
	
	public double playout(Node node) {
		simulator=new Simulator(node.gameData);
		FrameData result = simulator.simulate(node.fd, node.player, myActionPool, enemyActionPool, N_FRAMES_SIMULATED);
		return calculateScore(result);
		
	}
	
	public void update(Node node, double score) {
		
		Node current=node;
		while(current!=null) {
			current.n++;
			current.t+=score;
			current=current.parent;
		}
		
	}
	
	public void execute() {
		
		Node current=root;
		expansion(current);

		boolean doneIteration=false;

		for(int i=0;i<N_ITERATIONS;i++) {
			while(!doneIteration) {
				
				current=selection(current);						//SELECIONA BEST UCB1
				
				if(current.isLeaf()) {							//SI ES HOJA -> SE HARÁ UNA SIMULACIÓN
					
					if(current.n==0) {							//SI NO HA SIDO VISITADA ANTES -> ADEMÁS SE LE AÑADIRAN LOS HIJOS , Y SE SELECCIONARÁ UNO PARA LA SIMULACIÓN
						expansion(current);
						current=selection(current);
					}
					
					update(current,playout(current));			//SIMULAMOS Y ACTUALIZAMOS LOS VALORES DE N (VECES VISITADA) Y T (SCORE TOTAL)
					doneIteration=true;							//TERMINAMOS LA ITERACIÓN
					
				}												//SI NO ES HOJA -> EXPLORAMOS LOS SIGUIENTES
			}
		}
	}
	
	public Action getBestUCB1Child() {
		return null;
	}
	
	public Action getMostVisitedChild() {
		double mostVisited=0;
		Action mostVisitedAction=Action.BACK_STEP;
		
		for(int i=0; i<root.children.size();i++) {
			if(root.children.get(i).n>mostVisited) {
				mostVisited=root.children.get(i).n;
				mostVisitedAction = root.children.get(i).associatedAction;
			}
		}
		
		return mostVisitedAction;
	}
	
	public void setNewRoot(FrameData fd,GameData gameData,LinkedList<Action> myActionPool,LinkedList<Action> enemyActionPool) {
		this.root=new Node(null, gameData, player);
		
		this.enemyActionPool=enemyActionPool;
		this.myActionPool=myActionPool;
		System.out.print("HOLY SHIT");

	}
	
	public double calculateScore(FrameData fd) { 													// ADD MULTIPLOS SITUACIONES FAVORABLES COMO STUN
		double myHp = fd.getCharacter(player).getHp();
		double opponentHp = fd.getCharacter(!player).getHp();
		return myHp-opponentHp;
	}
	
	public double calculateUCB1(Node node) {
		double result;
		if(node.n==0)
			result=99999 + random.nextInt();
		else
			result = node.t/node.n+C*Math.sqrt(Math.log(node.parent.n)/node.n);
		node.ucb1=result;
		return result;
	}
	
	public void printRoot() {
		root.printNode(root);
	}
	
}


