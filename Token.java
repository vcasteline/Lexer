class Token implements IToken{
    Kind kind;
    char token;

    Token(){
        switch(kind)
        case IDENT:
            kind = Kind.IDENT;
        case INT_LIT:
            kind = Kind.INT_LIT;
        case FLOAT_LIT:
            kind =  Kind.FLOAT_LIT;
		case STRING_LIT:
            kind = Kind.STRING_LIT;
        case BOOLEAN_LIT:
            kind = Kind.BOOLEAN_LIT;// 'true','false'
		case LPAREN:
            kind = Kind.LPAREN; // '('
		case RPAREN:
            kind = Kind.RPAREN; // ')'
		case LSQUARE:
            kind = Kind.LSQUARE; // '['
		case RSQUARE:
            kind = Kind.RSQUARE; // ']'
		case LANGLE:
            kind = Kind.LANGLE;  // '<<'
		case RANGLE:
            kind = Kind.RANGLE;  // '>>'
		case PLUS:
            kind = Kind.PLUS;  // '+'
		case MINUS:
            kind = Kind.MINUS; // '-'
		case TIMES:
            kind = Kind.TIMES;  // '*'
		DIV,  //  '/'
		MOD,  // '%'
		COLOR_CONST, // 'BLACK','BLUE','CYAN','DARK_GRAY','GRAY','GREEN','LIGHT_GRAY','MAGENTA','ORANGE','PINK',
					// 'RED','WHITE','YELLOW'
		KW_IF, // 'if'
		KW_FI, //'fi'
		KW_ELSE, //'else'
		KW_WRITE, // 'write'
		KW_CONSOLE, // 'console'
		AND, // '&'
		OR,  // '|'
		BANG, // '!'
		LT,  // '<'
		GT,  // '>'
		EQUALS, // '=='
		NOT_EQUALS, // '!='
		LE, //  '<='
		GE, //  '>='
		TYPE, //int, float, string, boolean, color, image
		COLOR_OP, //getRed, getGreen, getBlue
		IMAGE_OP, //getWidth, getHeight
		SEMI, // ';'
		COMMA, // ','
		ASSIGN, // '='
		RARROW, // '->'
		LARROW, // '<-'
		KW_VOID, // 'void'
		RETURN,// '^'
		EOF, // used as sentinal, does not correspond to input
		ERROR, // use to avoid exceptions if scanning all input at once
    }
    //returns the token kind
	public Kind getKind(){
        Kind ourKind = Kind.IDENT;
        
        
        return kind;

    };

	//returns the characters in the source code that correspond to this token
	//if the token is a STRING_LIT, this returns the raw characters, including delimiting "s and unhandled escape sequences.
	public String getText(){

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