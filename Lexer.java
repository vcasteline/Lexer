package edu.ufl.cise.plc;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();
    int currentToken = -1;
    public Lexer(String input){
        ArrayList<String> lines = new ArrayList<String>();
        Scanner scan =new Scanner(input);
        while(scan.hasNextLine())
        {
            lines.add(scan.nextLine());
        }

        String stringInput = "";
        System.out.println(lines.size());

        for(int i=0; i< lines.size(); i++){//Loops through line array
            String line = lines.get(i);
            int lineSize = lines.get(i).length();
            for(int j = 0; j < lineSize; ++j) {//Iterates through individual strings

                char candidate = line.charAt(j);
                boolean letterOrDigit = Character.isLetterOrDigit(candidate);
                if (letterOrDigit == false && candidate != ' ' && candidate != '\"') {
                    if(stringInput.isEmpty())
                    {
                    tokens.add(new Token(String.valueOf(candidate), i, j)); //Add token if input is not a number/letter
                    }
                    else {
                        tokens.add(new Token(stringInput, i, j)); //Send stringInput as a token
                        stringInput = ""; //Flush string input
                        tokens.add(new Token(String.valueOf(candidate), i, j)); //send non letter/number as a token
                    }

                }
                else {
                    stringInput = stringInput + candidate;
                    if(j == lineSize-1)
                    {
                        tokens.add(new Token(stringInput, i, j)); //If we get to the end of line, send stringInput as a token
                        stringInput = "";
                    }
                }

            }
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