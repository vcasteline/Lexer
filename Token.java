package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken;

public class Token implements IToken {
    Kind kind;
    char token;
    String input;
    int line;
    int col;
    public Token(String input, int line, int col){
        
        switch(input){
            // case "  ":
            //     kind = Kind.IDENT;
            // case INT_LIT:
            //     kind = Kind.INT_LIT;
            // case FLOAT_LIT:
            //     kind =  Kind.FLOAT_LIT;
            // case STRING_LIT:
            //     kind = Kind.STRING_LIT;
            case "true":
                kind = Kind.BOOLEAN_LIT;// 'true','false'
                break;
            case "false":
                kind = Kind.BOOLEAN_LIT;
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
            // case "COLOR_CONST":
            //     kind = Kind.COLOR_CONST; // 'BLACK','BLUE','CYAN','DARK_GRAY','GRAY','GREEN','LIGHT_GRAY','MAGENTA','ORANGE','PINK',
            //             // 'RED','WHITE','YELLOW'
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
            // case "TYPE":
            //     kind = Kind.TYPE; //int, float, string, boolean, color, image
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
            // case ERROR:
            //     kind = Kind.ERROR; // use to avoid exceptions if scanning all input at once

        }
        this.line = line;
        this.col = col;
        System.out.println("input:"+ input);
        System.out.println("line"+ line);
        System.out.println("col"+ col);

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
        return 0;
    };

	//returns the float value represented by the characters of this token if kind is FLOAT_LIT
	public float getFloatValue(){
        return 0;
    };

	//returns the boolean value represented by the characters of this token if kind is BOOLEAN_LIT
	public boolean getBooleanValue(){
        return false;
    };
	
	//returns the String represented by the characters of this token if kind is STRING_LIT
	//The delimiters should be removed and escape sequences replaced by the characters they represent.  
	public String getStringValue(){
        return "hello";
    };

}