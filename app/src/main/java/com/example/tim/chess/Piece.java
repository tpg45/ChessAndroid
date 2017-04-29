package com.example.tim.chess;

/**
 * Represents a blank piece
 * @author Nick Prezioso, Tim Gassaway
 *
 */
public class Piece{
	/**
	 * Column number
	 */
	int x;
	/**
	 * Row number
	 */
	int y;
	/**
	 * Color (black or white)
	 */
	char color;
	/**
	 * Whether this piece has moved yet
	 */
	boolean hasMoved;
	
	/**
	 * Creates a blank space piece
	 * @param x - column number
	 * @param y - row number
	 * @param color - white or black
	 */
	public Piece(int x, int y, char color){
		this.x=x;
		this.y=y;
		this.color=color;
		hasMoved=false;
	}
	
	/**
	 * Checks if this piece can legally move to position (x,y).
	 * <p>
	 * Blank Pieces cannot move.
	 * @param x - column to move to
	 * @param y - row to move to
	 * @return false for blank pieces
	 */
	public boolean canMove(int x, int y){
		return false;
	}
	
	/**
	 * Checks if a piece is blank
	 * @return true if blank; false if actual piece
	 */
	public boolean isBlank(){
		return toString().charAt(0)==' ' || toString().charAt(0)=='#';
	}
	
	/**
	 * Gives piece as a string (ex: "wK")
	 * @return string representation of piece
	 */
	public String toString(){
		return color=='w' ? "  ":"##";
	}
}
