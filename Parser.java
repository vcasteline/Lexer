package edu.ufl.cise.plc;



import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.IToken.Kind;


public class Parser implements IParser{

    ASTNode currentNode;
    ILexer lexer;
    //Kind tokenKind;
    IToken currToken;
    boolean errorSyn = false;
    boolean errorLex = false;

    Parser(String input){

        if(input == "" || isAllWhitespace(input)){
        errorSyn = true;
        }
        lexer = getLexer(input);

        try {
            currToken = lexer.next();
        } catch (LexicalException e) {
            e.printStackTrace();
        }
        //SOS();
    }

    public ASTNode parse() throws PLCException{
        Expr returnExpr = expr();

        if(errorLex){
            throw new LexicalException("Lexical");
        }
        if(errorSyn){
            throw new SyntaxException("Syntax");
        }

        return returnExpr;

    }
    public Program program(){
        if(isKind(Kind.TYPE) || isKind(Kind.OR)){
            consume();
            make ident;
            match(Kind.LPAREN);
            //check for something here
            match(Kind.RPAREN);
            if(is declaration){
                declaration();
                match(Kind.SEMI);

            }else if(is statement){
                statement();
                match(Kind.SEMI);
            }
        }
    }
    public NameDef nameDef(){
        match(Kind.TYPE);
        if(isKind(Kind.IDENT)){
            NameDef
        }else{
            dimension();
            make ident;
            NameDefWithDim
        }
    }
    public Declaration declaration(){
        nameDef()
        if(isKind(Kind.EQUALS)){
            consume();
            //expr()
        }
        else if(isKind(Kind.LARROW)){
            consume();
            //expr();
        }
    }
    public Expr expr()//::=ConditionalExpr | LogicalOrExpr
    {
        Expr currExpr = null;
        if(isKind(Kind.KW_IF)){
            currExpr = conditionalExpr();
        }
        else{
            currExpr =  LogicalOrExpr();
        }

        return currExpr;
    }
    public Expr conditionalExpr(){
        IToken firstToken = currToken;
        Expr currExpr = null;
        Expr condition = null;
        Expr trueExp = null;
        Expr falseExp = null;

        if(isKind(Kind.KW_IF)){
            consume();
            //if(isKind(Kind.LPAREN)){
                //consume();
                match(Kind.LPAREN);
                condition = expr();
                match(Kind.RPAREN);
                trueExp = expr();
                    match(Kind.KW_ELSE);
                    falseExp = expr();
                    match(Kind.KW_FI);
                    currExpr = new ConditionalExpr(firstToken, condition, trueExp, falseExp);

            //}
        }
        return currExpr;

    }
    public Expr LogicalOrExpr()//LogicalAndExpr ( '|' LogicalAndExpr)*
    {
        IToken firstToken = currToken;

        Expr left = null;
        Expr right = null;

        left = logicalAndExpr();
        while(isKind(Kind.OR))
        {
            IToken op = currToken;
            consume();
            right = logicalAndExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }
        return left;

    }
    public Expr logicalAndExpr()
    {
        IToken firstToken = currToken;

        Expr left = null;
        Expr right = null;

        left = comparisonExpr();
        while(isKind(Kind.AND))
        {
            IToken op = currToken;
            consume();
            right = comparisonExpr();

            left = new BinaryExpr(firstToken, left, op, right);
        }
        return left;

    }

    public Expr comparisonExpr()
    {
        IToken firstToken = currToken;

        Expr left = null;
        Expr right = null;

        left = additiveExpr();
        while(isKind(Kind.LT) || isKind(Kind.GT) || isKind(Kind.EQUALS) || isKind(Kind.NOT_EQUALS) || isKind(Kind.LE) || isKind(Kind.GE))
        {
            IToken op = currToken;
            consume();
            right = additiveExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }
        return left;

    }

    public Expr additiveExpr()
    {
        //Expr currExpr = multiplicativeExpr();
        IToken firstToken = currToken;

        Expr left = null;
        Expr right = null;

        left = multiplicativeExpr();
        while(isKind(Kind.PLUS) || isKind(Kind.MINUS))
        {
            IToken op = currToken;

            consume();
            right = multiplicativeExpr();
            //if(right)
            if(right==null){
                errorSyn =true;
                //throw new LexicalException("lexical error");
            }

            left = new BinaryExpr(firstToken, left, op, right);
        }
        return left;
    }

    public Expr multiplicativeExpr()
    {   IToken firstToken = currToken;

        Expr left = null;
        Expr right = null;

        left = unaryExpr();
        while(isKind(Kind.TIMES) || isKind(Kind.DIV) || isKind(Kind.MOD))
        {   IToken op = currToken;
            consume();
            right = unaryExpr();
            left = new BinaryExpr(firstToken, left, op, right);
        }
        return left;
    }

    public Expr unaryExpr()
    {
        Expr currExpr = null;
        IToken firstToken = currToken;
        if(isKind(Kind.BANG) || isKind(Kind.MINUS) || isKind(Kind.COLOR_OP) || isKind(Kind.IMAGE_OP))
        {
            consume();
           // currExpr = unaryExpr();
            currExpr = new UnaryExpr(firstToken, firstToken, unaryExpr() );

        }
        else
        {

            currExpr = unaryExprPostfix();
        }
        return currExpr;
    }

    public Expr unaryExprPostfix()
    {   IToken firstToken = currToken;
        //Expr currExpr = primaryExpr();
        PixelSelector pixel = null;
        Expr unaryPost = primaryExpr();
        if(isKind(Kind.LSQUARE))
        {
            consume();
            pixel = pixelSelector();
            unaryPost = new UnaryExprPostfix(firstToken, unaryPost, pixel);
        }

        return unaryPost;
    }
    public Expr primaryExpr() //PrimaryExpr ::=// BOOLEAN_LIT |STRING_LIT |INT_LIT |FLOAT_LIT |IDENT |'(' Expr ')'

    {
        Expr currentExpr = null;
        Kind tokenKind = currToken.getKind();

        switch (tokenKind) {
            case BOOLEAN_LIT -> currentExpr = new BooleanLitExpr(currToken);
            case STRING_LIT -> currentExpr = new StringLitExpr(currToken);
            case FLOAT_LIT -> currentExpr = new FloatLitExpr(currToken);
            case INT_LIT -> currentExpr = new IntLitExpr(currToken);
            case IDENT -> currentExpr = new IdentExpr(currToken);
            case COLOR_CONST -> currentExpr = new ColorConstExpr(currToken);
            case KW_CONSOLE -> currentExpr = new ConsoleExpr(currToken);
        }

        //CONSUME
        //expr();
        if(isKind(Kind.LPAREN)){
            consume();
            currentExpr = expr();
            match(Kind.RPAREN);

        } else if (isKind(Kind.LANGLE)){
            consume();
            //expressions for Red blue and green
            match(Kind.RANGLE);
        }
        else{
            consume();
        }



        return currentExpr;
    }
    public PixelSelector pixelSelector(){

        IToken firstToken = currToken;
        Expr x = expr();
        match(Kind.COMMA);
        Expr y = expr();
        PixelSelector pixel = new PixelSelector(firstToken, x, y);
        match(Kind.RSQUARE);
        return pixel;
    }
    public Dimension dimension(){
       // same as pixelselector
    }
    public Statement statement() {
        if(Statement contains =){
            AssignmentStatement
        }
        else if(Statement <-){
            ReadStatement
        }
        else if(->){
            WriteStatement
        }
        else if(^){
            ReturnStatement
        }
        else{
            error
        }
    }


    void term()  // term ::= factor ( ( * | / )  factor )*
     {
         factor();
         while (isKind(Kind.TIMES) || isKind(Kind.DIV))
         {
             consume();
             factor();
         }
         return;
     }

   public void factor()  //factor ::= int_lit | ( expr )
     {
         if (isKind(Kind.INT_LIT))
         {
             consume();
         }
         else if (isKind(Kind.LPAREN))
         {
             consume();
             expr();
             match(Kind.RPAREN);
         }
         else {
            System.out.println("Illegal structure");
         }
         return;
     }

     public void readInput()
     {

     }



    ILexer getLexer(String input){
        return CompilerComponentFactory.getLexer(input);
    }

    /////////////////////////PARSING HELPER FUNCTIONS///////////////////////////////
    boolean isKind(Kind kind)
    {
        return currToken.getKind() == kind;
    }

    void consume()
    {

        try {

            if(currToken.getKind()==Kind.EOF){


                throw new SyntaxException("syntax exception");

            }

            try{
                currToken = lexer.next();
            }
            catch (LexicalException l){
                errorLex = true;
            }


            if(currToken.getKind() == Kind.ERROR){

                errorLex = true;
            }

        } catch (PLCException e) {
            e.printStackTrace();
        }
    }
    boolean match(Kind kind)  {
        if(currToken.getKind() == kind)
        {
            consume();
            return true;
        }
        else{
            errorSyn=true;
            return false;
        }

    }

    public boolean isAllWhitespace(String input)
    {
        boolean allSpaces = true;
        for(int i = 0; i < input.length(); ++i)
        {
            int currChar = input.charAt((i));
            if(currChar != ' ' && currChar != 8 && currChar != 9 && currChar != 10 && currChar != 11 && currChar != 12 && currChar != 13)
            {
                allSpaces = false;
            }
        }
        return allSpaces;
    }

}
