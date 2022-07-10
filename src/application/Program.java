package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();
		
		while (true) {
			try {
				UI.clearScreen();
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc); /*ler a posi��o de origem da pe�a*/
				
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves); //sobrecarga do m�todo 'printBoard'
				System.out.println();
				System.out.print("Target: ");
				ChessPosition target = UI.readChessPosition(sc); /*ler a posi��o de destino da pe�a*/
				
				ChessPiece capturedPiece = chessMatch.performChessMove(source, target);
				/*sempre que efetuar um movimento e ele resultar em uma pe�a capturada, fazer o if pra adicionar a pe�a � lista*/
				if(capturedPiece != null) {
					/*se a pe�a capturada for diferente de nulo, significa que alguma pe�a foi capturada*/
					captured.add(capturedPiece);/*adiciona essa pe�a a lista de pe�as capturadas*/
				}
			}
			catch(ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch(InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}

}
