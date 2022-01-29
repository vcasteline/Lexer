package edu.ufl.cise.plc;

import java.io.*;
import java.util.ArrayList;

public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();
    int currentToken = -1;
    public Lexer(String input){
        String[] lines = input.split("\\r?\\n|\\r");
        System.out.println(lines.length);
        for(int i=0; i< lines.length; i++){
            String line = lines[i];
            String substring = line.substring(0, 1);
//            if(substring.equals(" ")){
//                break;
//            }
            tokens.add(new Token(substring, i, 0));

        }
        tokens.add(new Token("EOF", 0, 0));
    }
    public IToken next(){
        currentToken++;
        return tokens.get(currentToken);

    }
    public IToken peek(){
        return tokens.get(0);
    }
}