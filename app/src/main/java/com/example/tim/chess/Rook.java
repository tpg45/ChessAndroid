package com.example.tim.chess;

/**
 * Represents a rook piece
 * @author Nick Prezioso, Tim Gassaway
 *
 */
public class Rook extends Piece{
	
	/**
	 * Creates a rook piece
	 * @param x - column number
	 * @param y - row number
	 * @param color - white or black
	 */
	public Rook(int x, int y, char color) {
		super(x, y, color);
	}
	
	/**
	 * Checks if this piece can legally move to position (x,y).
	 * <p>
	 * Rooks move any number of spaces in cardinal directions.
	 * @param x - column to move to
	 * @param y - row to move to
	 */
	@Override
	public boolean canMove(int x, int y){
		if(x<0 || x>7 || y<0 || y>7)
			return false;
		boolean horizontal = this.y==y;
		boolean vertical = this.x==x;
		boolean notBlocked = true;
		if(horizontal){
			for(int i = Math.min(this.x+1,x); i<=Math.max(this.x-1, x); i++){
				if(!Chess.board[y][i].isBlank()){
					if(i!=x || Chess.board[y][i].color==color){
						notBlocked=false;
						break;
					}
				}
			}
		}
		else if(vertical){
			for(int i = Math.min(this.y+1,y); i<=Math.max(this.y-1, y); i++){
				if(!Chess.board[i][x].isBlank()){
					if(i!=y || Chess.board[i][x].color==color){
						notBlocked=false;
						break;
					}
				}
			}
		}
		else{
			return false;
		}
		return (this.x!=x || this.y!=y) && notBlocked;
	}
	
	@Override
	public String toString(){
		return color+"R";
	}
}
