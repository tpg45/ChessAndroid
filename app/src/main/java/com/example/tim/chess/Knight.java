package com.example.tim.chess;

/**
 * Represents a knight piece
 * @author Nick Prezioso, Tim Gassaway
 *
 */
public class Knight extends Piece{
	
	/**
	 * Creates a knight piece
	 * @param x - column number
	 * @param y - row number
	 * @param color - white or black
	 */
	public Knight(int x, int y, char color) {
		super(x, y, color);
	}
	
	/**
	 * Checks if this piece can legally move to position (x,y).
	 * <p>
	 * Knights move in L-shaped patterns (2 spaces in one direction, then 1 space in a perpendicular direction).
	 * <p>
	 * Knights may jump pieces.
	 * @param x - column to move to
	 * @param y - row to move to
	 */
	@Override
	public boolean canMove(int x, int y){
		if(x<0 || x>7 || y<0 || y>7)
			return false;
		boolean onPath = (Math.abs(this.x-x)==2 || Math.abs(this.y-y)==2) && (Math.abs(this.x-x)==1 || Math.abs(this.y-y)==1);
		Piece target = Chess.board[y][x];
		boolean notBlocked = target.isBlank() || target.color!=color;
		return onPath && notBlocked;
	}
	
	@Override
	public String toString(){
		return color+"N";
	}
}
