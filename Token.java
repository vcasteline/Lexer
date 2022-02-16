package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken;

import java.util.ArrayList;

public class Token implements IToken {
    Kind kind;
    char token;
    String input;
    int line = 0;
    int col = 0;
    int returnInt = 0;
    float returnFloat= 0;
    boolean returnBoolean =false;
    String returnString = "";
    ArrayList<String> protectedWords = new ArrayList<String>();

    public Token(String input, int line, int col){
        populateProtected();
        input = clearWhiteSpace(input);
        this.input = input;


        if(input.charAt(0) == '\"'){
            kind = Kind.STRING_LIT;
            returnString = input;
        }
        else if(input.equals("int") || input.equals("float") || input.equals("string") || input.equals("boolean") || input.equals("color") || input.equals("image"))
        {
            kind = Kind.TYPE;
        }
        else if(input.equals("BLUE") || input.equals("CYAN") || input.equals("GRAY") || input.equals("DARK_GRAY") || input.equals("GREEN") || input.equals("LIGHT_GRAY") || input.equals("MAGENTA") || input.equals("ORANGE") || input.equals("PINK")|| input.equals("RED")||input.equals("WHITE")||input.equals("YELLOW")||input.equals("BLACK"))
        {
            kind = Kind.COLOR_CONST;
        }
        else if(input.equals("getRed") || input.equals("getBlue") || input.equals("getGreen"))
        {
            kind = Kind.COLOR_OP;
        }
        else if(input.equals("getWidth") || input.equals("getHeight"))
        {
            kind = Kind.IMAGE_OP;
        }
        else if(input.charAt(0) == '$' || input.charAt(0) == '_' || //If input starts with a $ or _
                (Character.isLetter(input.charAt(0)) == true && checkProtected(input) == false))//If input starts with a letter and is not a protected word
        {
            kind = Kind.IDENT;
        }
        else if(Character.isDigit(input.charAt(0)))
        {
//

            if (input.length() > 10) {
                kind = Kind.ERROR;
                return;
            }
                for(int i = 0; i < input.length(); ++i) {
                    if (input.charAt(i) == '.') {
                        kind = Kind.FLOAT_LIT;

                        returnFloat = Float.parseFloat(input);
                    }

                }
                if(kind != Kind.FLOAT_LIT){
                    kind = Kind.INT_LIT;
                    //System.out.println("Input: " + input);
                    returnInt = Integer.parseInt(input);
                }
            this.col = col;
        }
        else
        {
            switch (input) {
                // case INT_LIT:
                //     kind = Kind.INT_LIT;
                // case FLOAT_LIT:
                //     kind =  Kind.FLOAT_LIT;
                // case STRING_LIT:
                //     kind = Kind.STRING_LIT;
                case "true":
                    kind = Kind.BOOLEAN_LIT;// 'true','false'
                    returnBoolean = true;
                    break;
                case "false":
                    kind = Kind.BOOLEAN_LIT;
                    returnBoolean = false;
                    break;
                case "(":
                    kind = Kind.LPAREN; // '('
                    break;
                case ")":
                    kind = Kind.RPAREN; // ')'
                    break;
                case "[":
                    kind = Kind.LSQUARE; // '['
                    break;
                case "]":
                    kind = Kind.RSQUARE; // ']'
                    break;
                case "<<":
                    kind = Kind.LANGLE;  // '<<'
                    break;
                case ">>":
                    kind = Kind.RANGLE;  // '>>'
                    break;
                case "+":
                    kind = Kind.PLUS;  // '+'
                    break;
                case "-":
                    kind = Kind.MINUS; // '-'
                    break;
                case "*":
                    kind = Kind.TIMES;  // '*'
                    break;
                case "/":
                    kind = Kind.DIV;  //  '/'
                    break;
                case "%":
                    kind = Kind.MOD;  // '%'
                    break;
                 //case "COLOR_CONST":
                     //kind = Kind.COLOR_CONST; // 'BLACK','BLUE','CYAN','DARK_GRAY','GRAY','GREEN','LIGHT_GRAY','MAGENTA','ORANGE','PINK',
                             // 'RED','WHITE','YELLOW'
                case "if":
                    kind = Kind.KW_IF; // 'if'
                    break;
                case "fi":
                    kind = Kind.KW_FI; //'fi'
                    break;
                case "else":
                    kind = Kind.KW_ELSE; //'else'
                    break;
                case "write":
                    kind = Kind.KW_WRITE; // 'write'
                    break;
                case "console":
                    kind = Kind.KW_CONSOLE; // 'console'
                    break;
                case "&":
                    kind = Kind.AND; // '&'
                    break;
                case "|":
                    kind = Kind.OR;  // '|'
                    break;
                case "!":
                    kind = Kind.BANG; // '!'
                    break;
                case "<":
                    kind = Kind.LT;  // '<'
                    break;
                case ">":
                    kind = Kind.GT;  // '>'
                    break;
                case "==":
                    kind = Kind.EQUALS; // '=='
                    break;
                case "!=":
                    kind = Kind.NOT_EQUALS; // '!='
                    break;
                case "<=":
                    kind = Kind.LE; //  '<='
                    break;
                case ">=":
                    kind = Kind.GE; //  '>='
                    break;
                 //case "TYPE":
                   //  kind = Kind.TYPE; //int, float, string, boolean, color, image
                // case COLOR_OP:
                //     kind = Kind.COLOR_OP; //getRed, getGreen, getBlue
                // case IMAGE_OP:
                //     kind = Kind.IMAGE_OP; //getWidth, getHeight
                case ";":
                    kind = Kind.SEMI; // ';'
                    break;
                case ",":
                    kind = Kind.COMMA; // ','
                    break;
                case "=":
                    kind = Kind.ASSIGN; // '='
                    break;
                case "->":
                    kind = Kind.RARROW; // '->'
                    break;
                case "<-":
                    kind = Kind.LARROW; // '<-'
                    break;
                case "void":
                    kind = Kind.KW_VOID; // 'void'
                    break;
                case "^":
                    kind = Kind.RETURN;// '^'
                    break;
                case "EOF":
                    kind = Kind.EOF; // used as sentinal, does not correspond to input
                    break;
                 default:
                    kind = Kind.ERROR; // use to avoid exceptions if scanning all input at once


            }
        }
        this.line = line;
//        System.out.println("input:"+ input);
//        System.out.println("line:"+ this.line);
//        System.out.println("col:"+ this.col);
      System.out.println("token:" + kind + " " + input);

    }
    //returns the token kind
	public Kind getKind(){

        return kind;

    };

	//returns the characters in the source code that correspond to this token
	//if the token is a STRING_LIT, this returns the raw characters, including delimiting "s and unhandled escape sequences.
	public String getText(){
        return input;
    };
	
	//returns the location in the source code of the first character of the token.
	public SourceLocation getSourceLocation(){
        return new SourceLocation(line,col);
    };

	//returns the int value represented by the characters of this token if kind is INT_LIT
	public int getIntValue(){
        return returnInt;
    };

	//returns the float value represented by the characters of this token if kind is FLOAT_LIT
	public float getFloatValue(){
        return returnFloat;
    };

	//returns the boolean value represented by the characters of this token if kind is BOOLEAN_LIT
	public boolean getBooleanValue(){
        return returnBoolean;
    };
	
	//returns the String represented by the characters of this token if kind is STRING_LIT
	//The delimiters should be removed and escape sequences replaced by the characters they represent.  
	public String getStringValue(){
        return returnString;
    };

    public boolean checkError(){//Used by Lexer.  Only Lexer can throw errors, so it needs to check if the token is an error
        if(kind == Kind.ERROR){
            return true;
        }
        else
            return false;
    }

    String clearWhiteSpace(String candidate){//This function erases whitespace from the beginning of a string and updates col
        //String newString = candidate;

        while(candidate.charAt(0) == ' ')
        {
            col += 1;
            candidate = candidate.substring(1);
        }
        return candidate;
    }

    void populateProtected(){//Add protected words to the 'protected' array
        protectedWords.add("EOF");
        protectedWords.add("true");
        protectedWords.add("false");
        protectedWords.add("void");
        protectedWords.add("console");
        protectedWords.add("if");
        protectedWords.add("fi");
        protectedWords.add("write");
        protectedWords.add("else");

    }

    boolean checkProtected(String candidate){//Use this function to check if a string is a protected word
        for(int i = 0; i < protectedWords.size(); ++i)
        {
            if(candidate.equals(protectedWords.get(i)) == true){
                return true;
            }
        }
        return false;
    }

}