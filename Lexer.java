package edu.ufl.cise.plc;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Lexer implements ILexer {
    ArrayList<Token> tokens = new ArrayList<Token>();
    char[] Symbols = {'=', '<', '>', '!', '-'};
    String[] combos = {"==", "<=", ">=", "!=", "->", "->", "<-", "<<", ">>"};
    char [] escapes = {'\\', 'b', 't', 'n', 'f', 'r', '\"', '\''};
    int currentToken = -1;
    int indentCheck = 0;
    boolean isQuote = false;
    int quoteCheck = 0;
    boolean doubleZero = false;
    boolean instaBreak = false;
    public Lexer(String input){
        ArrayList<String> lines = new ArrayList<String>();
        Scanner scan =new Scanner(input);
        while(scan.hasNextLine())
        {
            lines.add(scan.nextLine());
        }

        String stringInput = "";


        for(int i=0; i< lines.size(); i++){//Loops through line array

            if(isAllWhitespace(stringInput)){
                stringInput="";
            }
            boolean wasSymbol = false;
            String line = lines.get(i);
            System.out.println(line);


            if(line.length() == 0) {
                break;
            }
            boolean intLit = intCheck(line);
            int lineSize = lines.get(i).length();

            for(int j = 0; j < lineSize; ++j) {//Iterates through individual strings


                char candidate = line.charAt(j);
                intLit = intCheck(stringInput + candidate);
                int ascii = candidate;


                if(candidate=='\\'){
                    instaBreak =true;
                    stringInput+=candidate;
                    continue;
                }
                if(ascii==9){

                    stringInput+="  ";
                    candidate = ' ';
                }

//                if(quoteCheck == 2)
//                {
//                    quoteCheck = 0;
//                }

                if(candidate == '"') {
                        ++quoteCheck;
                    }



                    if(quoteCheck != 0)
                    {
                        if(quoteCheck == 2 && line.charAt(j-1) != '\\')
                        {
                            stringInput = stringInput + candidate;
                            tokens.add(new Token(stringInput, i, j)); //Send stringInput as a token
                            quoteCheck = 0;
                            stringInput = spaceReplace(stringInput);
                            continue;
                        }
                        else if(quoteCheck == 2 && line.charAt(j-1) == '\\'){
                            --quoteCheck;
                            stringInput = stringInput + candidate;
                            continue;
                        }
                        else {
                            stringInput = stringInput + candidate;
                            continue;
                        }
                    }



//                if (candidate == '"')
///               {
////                    if (isQuote == false) {
////                        isQuote = true;
////                        int nextQuote = line.indexOf('\"', j+1);
////
////                        if(nextQuote==-1){
////                            stringInput+="@";
////                            tokens.add(new Token(stringInput, i, j)); //Send stringInput as a token
////                            stringInput="";
////                            continue;
////                        }
////
////                        stringInput+=line.substring(j, nextQuote+1);
////                        tokens.add(new Token(stringInput, i, j));
////                        stringInput = spaceReplace(stringInput);
////                        isQuote=false;
////                        j = nextQuote;
////                        continue;
////                    }
//                    else if(isQuote == true)
//                    {
//                        isQuote = false;
//                    }
//                    ++quoteCheck;
//                }
                boolean letterOrDigit = Character.isLetterOrDigit(candidate);

                if (letterOrDigit == false && candidate != ' ' && candidate != '\"'&& candidate != '#' && !isSymbol(candidate) && candidate!='_') {//Letter, numbers, spaces, and # do not enter here

                    if(isAllWhitespace(stringInput) == true || stringInput.isEmpty())
                    {

                    tokens.add(new Token(String.valueOf(candidate), i, j)); //Add token if input is not a number/letter
                    }
                    else {

                        tokens.add(new Token(stringInput, i, j)); //Send stringInput as a token
                        stringInput = spaceReplace(stringInput); //Flush string input
                        tokens.add(new Token(String.valueOf(candidate), i, j)); //send non letter/number as a token
                    }

                }
                else {


                    if(isSymbol(candidate) && j == lineSize-1) {//IF END OF LINE AND CANDIDATE IS Symbol
                        stringInput =  stringInput + candidate;
                        tokens.add(new Token(stringInput, i, 0));
                        stringInput = "";
                    }
                    else if (isSymbol(candidate))
                    {
                        if(isSymbol(line.charAt(j+1)) && isCombo(candidate, line.charAt(j+1)) == true) {
                            stringInput =  stringInput + candidate + line.charAt(j+1);

                            tokens.add(new Token(stringInput, i, 0)); //If stringInput is ==, send it as token


                            j += 1;


                            stringInput = spaceReplace(stringInput);
                            wasSymbol = true;


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
                    
                    


                    if((candidate == ' ' && line.charAt(0) != '#' && isQuote == false && line.charAt(0) != '\"' && wasSymbol == false && isAllWhitespace(stringInput) == false) ||
                            (intLit == true && Character.isLetter(candidate) == true && isSymbol(candidate) == false && isQuote == false))
                    {

                        tokens.add(new Token(stringInput, i, 0));
                        stringInput = spaceReplace(stringInput);
                        stringInput = stringInput + candidate;
                        if(j != lineSize-1 && Character.isDigit(line.charAt(j+1)))
                        {

                            intLit = true;
                        }

                    }
                    else if(isSymbol(candidate) == false && !intLit && candidate != '#') {//QUOTES and LETTERS end up here
                        stringInput = stringInput + candidate;//Add candidate to string
                        wasSymbol = false;
                        if(quoteCheck == 2)
                        {
                            tokens.add(new Token(stringInput, i, 0));
                            stringInput = spaceReplace(stringInput);
                        }

                    }
                    else if(Character.isDigit(candidate) && intLit){

                        j = checkNumToken(line, j, i);

                        if(j != lineSize-1){
                            stringInput = spaceReplace(line.substring(0, j+1));
                        }else{
                            stringInput = "";
                        }

                        if(doubleZero==true){
                            intLit = true;
                        }
                        else{
                            intLit= false;
                        }



                    }

                        if((j == lineSize-1 && isAllWhitespace(stringInput)==false) || (j == lineSize-1 && wasSymbol))//we ARE at the end of line and NOT a symbol
                        {


                            if (!stringInput.isEmpty() && line.charAt(0) != '#' && candidate != '\"' && isSymbol(candidate)==false && intLit==false) {

                                tokens.add(new Token(stringInput, i, 0)); //If we get to the end of line, send stringInput as a token
                            }

                            stringInput="";


                        }
                        else if(candidate == '#' && stringInput.isEmpty() == false)
                    {
                        if(isAllWhitespace(stringInput) == false) {
                            System.out.println("Should be here: " + stringInput);
                            tokens.add(new Token(stringInput, i, 0));
                            stringInput = "";
                            break;
                        }
                        else
                        {
                            break;
                        }
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
        return tokens.get(currentToken+1);
    }
    String getAscii(String s){
        int[] ascii = new int[s.length()];
        for(int i=0; i != s.length(); ++i){
            ascii[i] = s.charAt(i);
        }
        return Arrays.toString(ascii);
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

    public boolean isCombo(char first, char second) {
        String combo = "";
        combo = combo + first + second;

        for(int i = 0; i < combos.length; ++i){
            if(combos[i].equals(combo))
                return true;
        }
        return false;
    }

    public boolean isAllWhitespace(String input)
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

    public boolean intCheck(String input)
    {
        for(int i =0; i < input.length(); ++i)
        {
            if(input.charAt(i) != ' ')
            {
                if(Character.isDigit(input.charAt(i)))
                {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public int checkNumToken(String input, int oldJ, int currentI){
        doubleZero=false;
        boolean foundDecimal = false;
        int newJ = input.length();
        if(input.charAt(0)=='0' && input.length()>1){
            if(Character.isDigit(input.charAt(oldJ + 1))){
                newJ=oldJ+1;
                doubleZero =true;
            }
        }
        else{
            for(int i=oldJ; i< input.length();i++){
                char candidate = input.charAt(i);

                if(!Character.isDigit(candidate)){
                    if(candidate == '.' && foundDecimal==false){
                        foundDecimal = true;
                    }
                    else{
                        newJ = i;
                        break;
                    }
                }
            }
        }


        String token = input.substring(oldJ, newJ);

        tokens.add(new Token(token, currentI, oldJ));

        return newJ-1;
    }


}