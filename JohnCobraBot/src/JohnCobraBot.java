import aiinterface.AIInterface;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import aiinterface.CommandCenter;

import java.util.LinkedList;
import java.util.Random;
import enumerate.Action;

/**
 * @author Enrique Garrido Sánchez
 **/
public class JohnCobraBot implements AIInterface {
	
	//PARÁMETROS DE LANZAMIENTO
	// -n 10 --c1 ZEN --c2 ZEN --a1 JohnCobraBot --a2 SimpleAI --fastmode --mute 
	// -n 10 --c1 ZEN --c2 ZEN --a1 JohnCobraBot --a2 SimpleAI --fastmode --mute --disable-window --grey-bg --inverted-player 1
	
	// VARIABLES
	private Key inputKey;
    private boolean playerNumber;
    private FrameData frameData;
    private CommandCenter cc;
    private int lastDistance;
    private Random randomizer = new Random();
    private boolean isLastActionMoved = false;
    
    
    LinkedList<Action> myActs; //Action waiting to be executed
	//LinkedList<Action> simMyActs; //Simulated action
	//LinkedList<Action> simOppActs; //Simulated opponent's action
    
	LinkedList<Action> gapCloser; 				//List of actions used to dash or jump closer
	LinkedList<Action> attackGround; 			//List of actions used on ground
	LinkedList<Action> attackAir; 				//List of actions used on air
	LinkedList<Action> skillGround;			 	//List of skills that needs ENERGY on ground
	LinkedList<Action> skillAir;				//List of skills that needs ENERGY on air
	LinkedList<Action> projectiles;				//List of projectiles that can be thrown
	
    //MÉTODOS
	@Override
	public void close() {

	}

	@Override
	public void getInformation(FrameData frameData, boolean isControl) {
		
		this.frameData = frameData;
        this.cc.setFrameData(this.frameData, this.playerNumber);
        
	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {

		this.playerNumber = playerNumber;
        this.inputKey = new Key();
        this.cc = new CommandCenter();
        this.frameData = new FrameData();
		System.out.println("round start");

        
		return 0;
	}

	@Override
	public Key input() {
		return this.inputKey;
	}

	@Override
	public void processing() {
		if(frameData.getEmptyFlag() || frameData.getRemainingTimeMilliseconds()<=0) {
			return;
		}
		
		if (this.cc.getSkillFlag()) {
            this.inputKey = cc.getSkillKey();

            return;
        }

        this.lastDistance = frameData.getDistanceX();

        if (this.lastDistance >= 100 && this.isLastActionMoved) {
            return;
        }

        this.inputKey.empty();
        this.cc.skillCancel();

        if (this.lastDistance < 100) {
            int nextActionInt = this.randomizer.nextInt(3) + 1;

            switch (nextActionInt) {
            case 1:
                this.cc.commandCall("A");
                break;
            case 2:
                this.cc.commandCall("A");
                break;
            case 3:
                this.cc.commandCall("A");
                break;
            }

            this.isLastActionMoved = false;

            return;
        }

        if (this.lastDistance >= 100) {
            this.cc.commandCall("FORWARD_WALK");
            this.isLastActionMoved = true;

            return;
        }
        
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
}
