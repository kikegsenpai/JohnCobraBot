import aiinterface.AIInterface;
import aiinterface.CommandCenter;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.MotionData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import MCTS.MCTS;
import enumerate.Action;
import enumerate.State;

/**
 * @author Enrique Garrido Sánchez
 **/
public class JohnCobraBot implements AIInterface {

	// PARÁMETROS DE LANZAMIENTO
	// -n 10 --c1 ZEN --c2 ZEN --a1 JohnCobraBot --a2 SimpleAI --fastmode --mute
	// -n 10 --c1 ZEN --c2 ZEN --a1 JohnCobraBot --a2 SimpleAI --fastmode --mute
	// --disable-window --grey-bg --inverted-player 1

	// ALGORITMO
	MCTS mcts;

	// VARIABLES
	private Key inputKey;
	private boolean playerNumber;
	private FrameData frameData;
	private CommandCenter cc;
	private int lastDistance;
	private Random randomizer = new Random();
	private boolean isLastActionMoved = false;
	private GameData gameData;

	LinkedList<Action> myActs; // Action waiting to be executed
	// LinkedList<Action> simMyActs; //Simulated action
	// LinkedList<Action> simOppActs; //Simulated opponent's action

	LinkedList<Action> gapCloser; // List of actions used to dash or jump closer
	LinkedList<Action> attackGround; // List of actions used on ground
	LinkedList<Action> attackAir; // List of actions used on air
	LinkedList<Action> skillGround; // List of skills that needs ENERGY on ground
	LinkedList<Action> skillAir; // List of skills that needs ENERGY on air
	LinkedList<Action> projectiles; // List of projectiles that can be thrown

	private CharacterData myCharacter;
	private CharacterData opponent;
	private ArrayList<MotionData> myCharacterMotion;
	private ArrayList<MotionData> opponentMotion;
	private LinkedList<Action> enemyActionPool;
	private LinkedList<Action> myActionPool;

	// MÉTODOS
	@Override
	public void close() {

	}

	@Override
	public void getInformation(FrameData frameData, boolean isControl) {
		myCharacter = this.frameData.getCharacter(playerNumber);
		opponent = this.frameData.getCharacter(!playerNumber);
		myCharacterMotion = gameData.getMotionData(playerNumber);
		opponentMotion = gameData.getMotionData(!playerNumber);

		this.frameData = frameData;
		this.cc.setFrameData(this.frameData, this.playerNumber);

	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {

		gapCloser = new LinkedList<Action>();
		attackGround = new LinkedList<Action>();
		attackAir = new LinkedList<Action>();
		skillGround = new LinkedList<Action>();
		skillAir = new LinkedList<Action>();
		projectiles = new LinkedList<Action>();

		this.gameData = gameData;
		this.playerNumber = playerNumber;
		this.inputKey = new Key();
		this.cc = new CommandCenter();
		this.frameData = new FrameData();
		mcts = new MCTS(playerNumber);
		orderMoveSets();
		System.out.println("round start");

		return 0;
	}

	@Override
	public Key input() {
		return this.inputKey;
	}

	@Override
	public void processing() {

		if (canProcess()) {
			if (cc.getSkillFlag()) {
				inputKey = cc.getSkillKey();
			} else {
				inputKey.empty();
				cc.skillCancel();

				/*
				 * mctsPrepare();
				 * 
				 * rootNode = new Node(simulatorAheadFrameData, null, myActions, oppActions,
				 * gameData, playerNumber, cc); rootNode.createNode();
				 * 
				 * Action bestAction = rootNode.mcts(); if (JayBot.DEBUG_MODE) {
				 * rootNode.printNode(rootNode); } if (charName.name() == "GARNET") {
				 * if(bestAction.name()=="CROUCH_GUARD") { cc.
				 * commandCall("1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 11"
				 * ); } else if(bestAction.name()=="STAND_GUARD") { cc.
				 * commandCall("4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4"
				 * ); } else if(bestAction.name()=="Action.AIR_GUARD") { cc.
				 * commandCall("7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7"
				 * ); } } if (bestAction.name()=="STAND_GUARD")
				 * cc.commandCall("4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4"); else
				 * if(bestAction.name()=="CROUCH_GUARD")
				 * cc.commandCall("1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1"); else
				 * if(bestAction.name()=="CROUCH_GUARD")
				 * cc.commandCall("7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7 7"); else
				 * cc.commandCall(bestAction.name());
				 * 
				 * setMyActionPool(); setOpponentActionPool();
				 */

				mcts.setNewRoot(frameData, gameData, attackGround, attackGround);
				myActionPool = attackGround;
				enemyActionPool = attackGround;
				mcts.execute();
				mcts.printRoot();
				cc.commandCall(mcts.getMostVisitedChild().name());

			}
		}

	}

	public boolean canProcess() {
		return !frameData.getEmptyFlag() && frameData.getRemainingFramesNumber() > 0;
	}

	@Override
	public void roundEnd(int arg0, int arg1, int arg2) {

	}

	public void orderMoveSets() {

		gapCloser.add(Action.FOR_JUMP);
		gapCloser.add(Action.FORWARD_WALK);
		gapCloser.add(Action.STAND_D_DB_BA);
		gapCloser.add(Action.STAND_D_DB_BB);

		attackGround.add(Action.STAND_A);
		attackGround.add(Action.STAND_B);
		attackGround.add(Action.CROUCH_A);
		attackGround.add(Action.CROUCH_B);
		attackGround.add(Action.STAND_FA);
		attackGround.add(Action.STAND_FB);
		attackGround.add(Action.CROUCH_FA);
		attackGround.add(Action.CROUCH_FB);
		attackGround.add(Action.STAND_F_D_DFA);

		attackAir.add(Action.AIR_A);
		attackAir.add(Action.AIR_B);
		attackAir.add(Action.AIR_DA);
		attackAir.add(Action.AIR_DB);
		attackAir.add(Action.AIR_FA);
		attackAir.add(Action.AIR_FB);
		attackAir.add(Action.AIR_UA);
		attackAir.add(Action.AIR_UB);

		skillGround.add(Action.THROW_A);
		skillGround.add(Action.THROW_B);
		skillGround.add(Action.STAND_D_DF_FA);
		skillGround.add(Action.STAND_D_DF_FB);
		skillGround.add(Action.STAND_D_DF_FC);
		skillGround.add(Action.STAND_F_D_DFA);
		skillGround.add(Action.STAND_F_D_DFB);

		skillAir.add(Action.AIR_D_DB_BA);
		skillAir.add(Action.AIR_D_DB_BB);
		skillAir.add(Action.AIR_D_DF_FA);
		skillAir.add(Action.AIR_D_DF_FB);
		skillAir.add(Action.AIR_F_D_DFA);
		skillAir.add(Action.AIR_F_D_DFB);

		projectiles.add(Action.STAND_D_DF_FA);
		projectiles.add(Action.STAND_D_DF_FB);
		projectiles.add(Action.STAND_D_DF_FC);
		projectiles.add(Action.STAND_F_D_DFB);

	}

	/*
	 * public void setOpponentActionPool() { enemyActionPool.clear();
	 * 
	 * int energy = opponent.getEnergy();
	 * 
	 * if (opponent.getState() == State.AIR) { for (int i = 0; i < attackAir.size();
	 * i++) { if
	 * (Math.abs(opponentMotion.get(Action.valueOf(attackAir.get(i).name()).ordinal(
	 * )).getAttackStartAddEnergy()) <= energy) {
	 * enemyActionPool.add(attackAir.get(i)); } }
	 * 
	 * } else { for (int i = 0; i < attackGround.size(); i++) { if
	 * (Math.abs(opponentMotion.get(Action.valueOf(attackGround.get(i).name()).
	 * ordinal()).getAttackStartAddEnergy()) <= energy) {
	 * enemyActionPool.add(attackGround.get(i)); } } } }
	 */
}
