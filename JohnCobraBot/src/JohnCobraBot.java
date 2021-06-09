import aiinterface.AIInterface;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import aiinterface.CommandCenter;
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
                this.cc.commandCall("B");
                break;
            case 2:
                this.cc.commandCall("B");
                break;
            case 3:
                this.cc.commandCall("B");
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
	
	private void addMovementList() {
		
	}
}
