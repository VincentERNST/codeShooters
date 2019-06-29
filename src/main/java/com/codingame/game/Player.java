package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer  {
	
	private int outNbr;
	@Override
	public int getExpectedOutputLines() {
		// TODO Auto-generated method stub
		return outNbr;
	}
	
	public void setExpectedOutput(int n){
		outNbr=n;
	}
}
