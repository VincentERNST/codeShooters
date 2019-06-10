package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer;

public class Player extends AbstractPlayer  {
	
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
