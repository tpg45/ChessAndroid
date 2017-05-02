package com.example.tim.chess;

/**
 * Represents a king piece
 * @author Nick Prezioso, Tim Gassaway
 *
 */
public class King extends Piece{
	
	/**
	 * Creates a king piece
	 * @param x - column number
	 * @param y - row number
	 * @param color - white or black
	 */
	public King(int x, int y, char color) {
		super(x, y, color);
	}
	
	/**
	 * Checks if this piece can legally move to position (x,y).
	 * <p>
	 * Kings may move one space in any direction.
	 * @param x - column to move to
	 * @param y - row to move to
	 */
	@Override
	public boolean canMove(int x, int y){
		if(x<0 || x>7 || y<0 || y>7)
			return false;
		if(MainActivity.board[y][x].toString().charAt(0)==color)
			return false;
		Piece[][] temp = MainActivity.copyBoard(MainActivity.board);
		MainActivity.move(MainActivity.board[this.y][this.x], MainActivity.board[y][x]);
		boolean wouldCheck = MainActivity.isCheck(color!='w');
		MainActivity.turnCounter--;
		MainActivity.board = temp;
		if(wouldCheck)
			return false;

		boolean normalMove = (this.x!=x || this.y!=y) && (this.x-x<=1 && this.x-x>=-1) && (this.y-y<=1 && this.y-y>=-1);
		if(normalMove){
			return true;
		}
		else{
			if(isChecked())
				return false;
			boolean castle = !hasMoved && (this.y==0 || this.y==7) && this.y==y && Math.abs(this.x-x)==2;
			boolean notBlockedOrThreatened = true;
			boolean rookNotMoved = false;
			char enemyColor = color=='w' ? 'b':'w';
			//King's side castle
			if(x>this.x){
				if(hasMoved || MainActivity.board[y][7].color==color || !(MainActivity.board[y][7] instanceof Rook))
					return false;
				for(int i = this.x+1; i<7; ){
					if(!MainActivity.board[y][i].isBlank() || MainActivity.threatened(x, y, enemyColor)){
						notBlockedOrThreatened=false;
						break;
					}
					i++;
				}
				if(!MainActivity.board[y][7].hasMoved)
					rookNotMoved=true;
			}
			//Queen's side castle
			else{
				if(hasMoved || MainActivity.board[y][0].color==color || !(MainActivity.board[y][0] instanceof Rook))
					return false;
				for(int i = 2; i<this.x-1; i++){
					if(!MainActivity.board[y][i].isBlank() || MainActivity.threatened(x, y, enemyColor)){
						notBlockedOrThreatened=false;
						break;
					}
				}
				if(!MainActivity.board[y][0].hasMoved)
					rookNotMoved=true;
			}
			return castle && rookNotMoved && notBlockedOrThreatened;
		}
	}
	
	/**
	 * Checks to see if the king is in check
	 * @return true if in check; false otherwise
	 */
	public boolean isChecked(){
		char enemyColor = color=='w' ? 'b':'w';
		return MainActivity.threatened(x, y, enemyColor);
	}
	
	/**
	 * Checks to see if the king is checkmated
	 * @return true if checkmated; false otherwise
	 */
	public boolean isCheckMated(){
		char enemyColor = color=='w' ? 'b':'w';
		//check for legal moves
		if(x+1<=7 && !MainActivity.threatened(x+1, y, enemyColor))
			return false;
		else if(x+1<=7 && y+1<=7 && !MainActivity.threatened(x+1, y+1, enemyColor))
			return false;
		else if(y+1<=7 && !MainActivity.threatened(x, y+1, enemyColor))
			return false;
		else if(x-1>=0 && y+1<=7 && !MainActivity.threatened(x-1, y+1, enemyColor))
			return false;
		else if(x-1>=0 && !MainActivity.threatened(x-1, y, enemyColor))
			return false;
		else if(x-1>=0 && y-1>=0 && !MainActivity.threatened(x-1, y-1, enemyColor))
			return false;
		else if(y-1>=0 && !MainActivity.threatened(x, y-1, enemyColor))
			return false;
		else if(x+1<=7 && y-1>=0 && !MainActivity.threatened(x+1, y-1, enemyColor))
			return false;
		//no legal moves and in check
		return isChecked();
	}
	
	@Override
	public String toString(){
		return color+"K";
	}
}
