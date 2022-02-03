package edu.ufl.cise.plc;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();
    char[] Symbols = {'=', '<', '>', '!', '-'};
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
//        System.out.println("Lines: " + lines.size());

        for(int i=0; i< lines.size(); i++){//Loops through line array
            boolean wasSymbol = false;
            String line = lines.get(i);
            //System.out.println("I'm here!" + i);

            if(line.length() == 0) {
                break;
            }
            boolean intLit = Character.isDigit(line.charAt(0));
            int lineSize = lines.get(i).length();
            for(int j = 0; j < lineSize; ++j) {//Iterates through individual strings



                char candidate = line.charAt(j);
                boolean letterOrDigit = Character.isLetterOrDigit(candidate);

                if (letterOrDigit == false && candidate != ' ' && candidate != '\"'&& candidate != '#' && !isSymbol(candidate) && candidate!='_') {//Letter, numbers, spaces, and # do not enter here
                   System.out.println("checkMod: " + candidate);
                    if(spaceCheck(stringInput) == true || stringInput.isEmpty())
                    {
                        System.out.println("checkModTop: " + candidate);
                    tokens.add(new Token(String.valueOf(candidate), i, j)); //Add token if input is not a number/letter
                    }
                    else {
                        System.out.println("checkModBottom: " + candidate);
                        tokens.add(new Token(stringInput, i, j)); //Send stringInput as a token
                        spaceReplace(stringInput); //Flush string input
                        tokens.add(new Token(String.valueOf(candidate), i, j)); //send non letter/number as a token
                    }

                }
                else {



//                    if(candidate == '=' && j == lineSize-1) {//IF END OF LINE AND CANDIDATE IS '='
//                        stringInput =  stringInput + candidate;
//                    }
//                    else if (candidate == '=')
//                    {
//                        if(line.charAt(j+1) == '=') {
//                           stringInput =  stringInput + candidate + candidate;
//
//                            tokens.add(new Token(stringInput, i, 0)); //If stringInput is ==, send it as token
//                            j += 1;
//                            stringInput = spaceReplace(stringInput);
//                            wasSymbol = true;
//                        }
//                        else
//                        {
//                            stringInput = stringInput + candidate;
//                            tokens.add(new Token(stringInput, i, 0));
//                           // candidate = ' ';
//                            stringInput = spaceReplace(stringInput);
//                            wasSymbol = true;
//
//                        }
//                    }


                    if(isSymbol(candidate) && j == lineSize-1) {//IF END OF LINE AND CANDIDATE IS Symbol
                        stringInput =  stringInput + candidate;
                        tokens.add(new Token(stringInput, i, 0));
                        stringInput = "";
                    }
                    else if (isSymbol(candidate))
                    {
                        if(isSymbol(line.charAt(j+1))) {
                            stringInput =  stringInput + candidate + line.charAt(j+1);

                            tokens.add(new Token(stringInput, i, 0)); //If stringInput is ==, send it as token


                            j += 1;


                            stringInput = spaceReplace(stringInput);
                            wasSymbol = true;
                            System.out.println("checkSpace: " + candidate);

                        }
                        else
                        {
                            stringInput = stringInput + candidate;
                            tokens.add(new Token(stringInput, i, 0));
                            // candidate = ' ';
                            stringInput = spaceReplace(stringInput);
                            wasSymbol = true;

                        }
                    }
                    
                    


                    if((candidate == ' ' && line.charAt(0) != '#' && line.charAt(0) != '\"' && wasSymbol == false && spaceCheck(stringInput) == false) ||
                            (intLit == true && Character.isLetter(candidate) == true && isSymbol(candidate) == false))
                    {
                        System.out.println("I'm here! " + wasSymbol);
                        tokens.add(new Token(stringInput, i, 0));
                        stringInput = spaceReplace(stringInput);
                        stringInput = stringInput + candidate;
                        if(j != lineSize-1 && Character.isDigit(line.charAt(j+1)))
                        {
                            intLit = true;
                        }

                    }
                    else if(isSymbol(candidate) == false) {
                        stringInput = stringInput + candidate;//Add candidate to string
                        wasSymbol = false;

                    }


                        if(j == lineSize-1 && stringInput.isEmpty() == false)//we ARE at the end of line and NOT a symbol
                        {
                            if (stringInput.charAt(0) != '#' && !isSymbol(candidate)) {
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

    public String spaceReplace(String input)
    {
        String returnString = "";
        for(int i = 0; i < input.length(); ++i)
        {
            returnString = returnString + " ";
        }
        return returnString;
    }

    public boolean spaceCheck(String input)
    {
        boolean allSpaces = true;
        for(int i = 0; i < input.length(); ++i)
        {
            if(input.charAt(i) != ' ')
            {
                allSpaces = false;
            }
        }
        return allSpaces;
    }
    
    /////////SYMBOL HELPER FUNCTIONS//////////////////
    public boolean isSymbol(char candidate)
    {
        for(int i = 0; i < Symbols.length; ++i)
        {
            if(candidate == Symbols[i])
            {
                return true;
            }
        }
        return false;
    }


}