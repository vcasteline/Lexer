package edu.ufl.cise.plc;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();
    int currentToken = -1;
    int indentCheck = 0;
    public Lexer(String input){
        ArrayList<String> lines = new ArrayList<String>();
        Scanner scan =new Scanner(input);
        while(scan.hasNextLine())
        {
            lines.add(scan.nextLine());
        }

        String stringInput = "";
        System.out.println("Lines: " + lines.size());

        for(int i=0; i< lines.size(); i++){//Loops through line array
            String line = lines.get(i);
            int lineSize = lines.get(i).length();
            for(int j = 0; j < lineSize; ++j) {//Iterates through individual strings

                char candidate = line.charAt(j);
                boolean letterOrDigit = Character.isLetterOrDigit(candidate);

                if (letterOrDigit == false && candidate != ' ' && candidate != '\"'&& candidate != '#' && candidate != '=') {//Letter, numbers, spaces, and # do not enter here
                    if(stringInput.isEmpty())
                    {
                    tokens.add(new Token(String.valueOf(candidate), i, 0)); //Add token if input is not a number/letter
                    }
                    else {
                        tokens.add(new Token(stringInput, i, 0)); //Send stringInput as a token
                        stringInput = ""; //Flush string input
                        tokens.add(new Token(String.valueOf(candidate), i, 0)); //send non letter/number as a token
                    }

                }
                else {



                    if(candidate == '=' && j == lineSize-1) {//IF END OF LINE AND CANDIDATE IS '='
                        //Do Nothing
                    }
                    else if (candidate == '=')
                    {
                        if(line.charAt(j+1) == '=') {
                            String tempString = stringInput;
                            tempString = tempString + "==";
                            tokens.add(new Token(tempString, i, 0)); //If stringInput is ==, send it as token
                            j += 1;
                            candidate = ' ';
                            stringInput = stringInput + candidate;

                        }
                        else
                        {
                            String tempString = stringInput;
                            tempString = tempString + candidate;
                            tokens.add(new Token(tempString, i, 0));
                            candidate = ' ';
                        }
                    }

                    stringInput = stringInput + candidate;//Add candidate to string

                        if(j == lineSize-1 && stringInput.isEmpty() == false)
                        {
                            if (stringInput.charAt(0) != '#') {
                                tokens.add(new Token(stringInput, i, 0)); //If we get to the end of line, send stringInput as a token
                            }
                            stringInput="";

                        }
                }

            }
        }
        tokens.add(new Token("EOF", 0, 0));
    }
    public IToken next() throws LexicalException {
        currentToken++;
        if(tokens.get(currentToken).checkError() == true)//Check if token is an error
        {
            throw new LexicalException("Error");
        }
        return tokens.get(currentToken);

    }
    public IToken peek(){
        return tokens.get(0);
    }
}