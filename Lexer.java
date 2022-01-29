package edu.ufl.cise.plc;


import java.util.ArrayList;

public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();

    public Lexer(String input){
        for(int i=0; i<input.length(); i++){
            tokens.add(new Token(input.substring(0, 1)));
        }
        
    }
    public IToken next(){

    }
    public IToken peek(){

    }
}