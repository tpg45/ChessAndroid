package com.example.tim.chess;

/**
 * Represents a pawn piece
 * @author Nick Prezioso, Tim Gassaway
 *
 */
public class Pawn extends Piece{
	
	/**
	 * The turn number of the last time this pawn moved
	 */
	int lastMovedTurn;
	
	/**
	 * Whether the last move this pawn made was a double move
	 */
	boolean lastMoveWasDouble;
	
	/**
	 * Creates a pawn piece
	 * @param x - column number
	 * @param y - row number
	 * @param color - white or black
	 */
	public Pawn(int x, int y, char color, boolean hasMoved) {
		super(x, y, color, hasMoved);
		lastMovedTurn = -1;
		lastMoveWasDouble = false;
	}
	
	/**
	 * CHecks whether a move to (x,y) would be a legal en passant capture
	 * @param x - column to move to
	 * @param y - row to move to
	 * @return whether a move to (x,y) would be a legal en passant move
	 */
	public boolean isLegalEnPassant(int x, int y){
		if(x<0 || x>7 || y<0 || y>7)
			return false;
		if((color=='w' && y<this.y) || (color=='b' && y>this.y))
			return false;
		return Math.abs(this.x-x)==1 && Math.abs(this.y-y)==1 && MainActivity.board[this.y][x] instanceof Pawn &&
			((Pawn)MainActivity.board[this.y][x]).lastMoveWasDouble && ((Pawn)MainActivity.board[this.y][x]).lastMovedTurn == MainActivity.turnCounter - 1;
	}
	
	/**
	 * Checks whether a move to (x,y) would be a legal double forward move
	 * @param x - column to move to
	 * @param y - row to move to
	 * @return whether a move to (x,y) would be a legal double forward move
	 */
	public boolean isLegalDoubleMove(int x, int y){
		Piece target = MainActivity.board[y][x];
		if (this.y<y)
			return (this.x==x && !hasMoved && Math.abs(this.y-y)==2 && target.isBlank())&&(MainActivity.board[y-1][x].isBlank());
		else
			return (this.x==x && !hasMoved && Math.abs(this.y-y)==2 && target.isBlank())&&(MainActivity.board[y+1][x].isBlank());
	}
	
	/**
	 * Checks if this piece can legally move to position (x,y).
	 * <p>
	 * Pawns move forward and capture diagonally, in addition to a few more complicated rules.
	 * @param x - column to move to
	 * @param y - row to move to
	 */
	@Override
	public boolean canMove(int x, int y){
		if(x<0 || x>7 || y<0 || y>7)
			return false;
		if((color=='w' && y<this.y) || (color=='b' && y>this.y))
			return false;
		Piece target = MainActivity.board[y][x];
		boolean normalForward = this.x==x && Math.abs(this.y-y)==1 && target.isBlank();
		boolean doubleForward = isLegalDoubleMove(x, y);
		boolean simpleCapture = Math.abs(this.x-x)==1 && Math.abs(this.y-y)==1 && !target.isBlank() && target.color!=color;
		boolean enPassant = isLegalEnPassant(x, y);
		return normalForward || doubleForward || simpleCapture || enPassant;
	}
	
	@Override
	public String toString(){
		return color+"p";
	}

}
