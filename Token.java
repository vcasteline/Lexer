package edu.ufl.cise.plc;

import edu.ufl.cise.plc.IToken;

public class Token implements IToken {
    Kind kind;
    char token;
    String input;
    public Token(String input){
        
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
            case "false":
                kind = Kind.BOOLEAN_LIT;
            case "(":
                kind = Kind.LPAREN; // '('
            case ")":
                kind = Kind.RPAREN; // ')'
            case "[":
                kind = Kind.LSQUARE; // '['
            case "]":
                kind = Kind.RSQUARE; // ']'
            case "<<":
                kind = Kind.LANGLE;  // '<<'
            case ">>":
                kind = Kind.RANGLE;  // '>>'
            case "+":
                kind = Kind.PLUS;  // '+'
            case "-":
                kind = Kind.MINUS; // '-'
            case "*":
                kind = Kind.TIMES;  // '*'
            case "/":
                kind = Kind.DIV;  //  '/'
            case "%":
                kind = Kind.MOD;  // '%'
            // case "COLOR_CONST":
            //     kind = Kind.COLOR_CONST; // 'BLACK','BLUE','CYAN','DARK_GRAY','GRAY','GREEN','LIGHT_GRAY','MAGENTA','ORANGE','PINK',
            //             // 'RED','WHITE','YELLOW'
            case "if":
                kind = Kind.KW_IF; // 'if'
            case "fi":
                kind = Kind.KW_FI; //'fi'
            case "else":
                kind = Kind.KW_ELSE; //'else'
            case "write":
                kind = Kind.KW_WRITE; // 'write'
            case "console":
                kind = Kind.KW_CONSOLE; // 'console'
            case "&":
                kind = Kind.AND; // '&'
            case "|":
                kind = Kind.OR;  // '|'
            case "!":
                kind = Kind.BANG; // '!'
            case "<":
                kind = Kind.LT;  // '<'
            case ">":
                kind = Kind.GT;  // '>'
            case "==":
                kind = Kind.EQUALS; // '=='
            case "!=":
                kind = Kind.NOT_EQUALS; // '!='
            case "<=":
                kind = Kind.LE; //  '<='
            case ">=":
                kind = Kind.GE; //  '>='
            // case "TYPE":
            //     kind = Kind.TYPE; //int, float, string, boolean, color, image
            // case COLOR_OP:
            //     kind = Kind.COLOR_OP; //getRed, getGreen, getBlue
            // case IMAGE_OP:
            //     kind = Kind.IMAGE_OP; //getWidth, getHeight
            case ";":
                kind = Kind.SEMI; // ';'
            case ",":
                kind = Kind.COMMA; // ','
            case "=":
                kind = Kind.ASSIGN; // '='
            case "->":
                kind = Kind.RARROW; // '->'
            case "<-":
                kind = Kind.LARROW; // '<-'
            case "void":
                kind = Kind.KW_VOID; // 'void'
            case "^":
                kind = Kind.RETURN;// '^'
            // case EOF:
            //     kind = Kind.EOF; // used as sentinal, does not correspond to input
            // case ERROR:
            //     kind = Kind.ERROR; // use to avoid exceptions if scanning all input at once
        }
    }
    //returns the token kind
	public Kind getKind(){
        Kind ourKind = Kind.IDENT;
        
        
        return kind;

    };

	//returns the characters in the source code that correspond to this token
	//if the token is a STRING_LIT, this returns the raw characters, including delimiting "s and unhandled escape sequences.
	public String getText(){
        return input;
    };
	
	//returns the location in the source code of the first character of the token.
	public SourceLocation getSourceLocation(){

    };

	//returns the int value represented by the characters of this token if kind is INT_LIT
	public int getIntValue(){

    };

	//returns the float value represented by the characters of this token if kind is FLOAT_LIT
	public float getFloatValue(){

    };

	//returns the boolean value represented by the characters of this token if kind is BOOLEAN_LIT
	public boolean getBooleanValue(){

    };
	
	//returns the String represented by the characters of this token if kind is STRING_LIT
	//The delimiters should be removed and escape sequences replaced by the characters they represent.  
	public String getStringValue(){

    };

}