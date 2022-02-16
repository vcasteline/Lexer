package edu.ufl.cise.plc;



import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.IToken.Kind;


public class Parser implements IParser{

    ASTNode currentNode;
    ILexer lexer;
    //Kind tokenKind;
    IToken currToken;

    Parser(String input){

        lexer = getLexer(input);
        try {
            currToken = lexer.next();
            System.out.println("this is the initial currToken "+ currToken.getKind());
        } catch (LexicalException e) {
            e.printStackTrace();
        }
        //SOS();
    }



    public Expr expr() //::=ConditionalExpr | LogicalOrExpr
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

            if(isKind(Kind.LPAREN)){
                consume();
                condition = expr();
                match(Kind.RPAREN);
                trueExp = expr();

                if(isKind(Kind.KW_ELSE)){

                    consume();
                    falseExp = expr();
                    match(Kind.KW_FI);
                    currExpr = new ConditionalExpr(firstToken, condition, trueExp, falseExp);
                }
            }
        }
        return currExpr;

    }
    public Expr LogicalOrExpr()//LogicalAndExpr ( '|' LogicalAndExpr)*
    {
        Expr currExpr = logicalAndExpr();

        while(isKind(Kind.OR))
        {
            consume();
            logicalAndExpr();
        }
        return currExpr;
    }
    public Expr logicalAndExpr()
    {
        Expr currExpr = comparisonExpr();

        while(isKind(Kind.AND))
        {
            consume();
            comparisonExpr();
        }

        return currExpr;
    }

    public Expr comparisonExpr()
    {
        Expr currExpr = additiveExpr();

        while(isKind(Kind.LT) || isKind(Kind.GT) || isKind(Kind.EQUALS) || isKind(Kind.NOT_EQUALS) || isKind(Kind.LE) || isKind(Kind.GE))
        {
            consume();
            additiveExpr();
        }
        return currExpr;
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
            System.out.println("kind is "+firstToken.getKind());
            currExpr = new UnaryExpr(firstToken, firstToken, unaryExpr() );

        }
        else
        {

            currExpr = unaryExprPostfix();
        }
        System.out.println("CurrExpr: "+currExpr);
        return currExpr;
    }

    public Expr unaryExprPostfix()
    {
        Expr currExpr = primaryExpr();
        if(isKind(Kind.LSQUARE))
        {
            consume();
            //pixelSelector();
        }

        return currExpr;
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
        }

        //CONSUME
        //expr();
        //System.out.println("primary expression "+ currentExpr);
        //System.out.println("primary expression token before: "+ currToken.getKind() +" "+ currToken.getText());
        consume();
        //System.out.println("primary expression token after consume(): "+ currToken.getKind() +" "+ currToken.getText());
        return currentExpr;
    }





//    public void expr()  // expr ::= term ( ( + | - )  term )*
//    {
//        IToken firstToken = currToken;
//        Expr left = null;
//        Expr right = null;
//        left = term();
//        while (isKind(PLUS) || isKind(MINUS))
//        {
//            IToken op = t;
//            consume();
//            right = term();
//            left = new BinaryExpr(firstToken, left, op, right);
//        }
//        return left;
//    }


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


    public ASTNode parse() throws PLCException{
       // System.out.println(currentNode);

        return expr();
    }
    ILexer getLexer(String input){
        return CompilerComponentFactory.getLexer(input);
    }
    void lex(){

        try {
            System.out.println(lexer.next().getKind());
        } catch (LexicalException e) {
            e.printStackTrace();
        }
    }

    /////////////////////////PARSING HELPER FUNCTIONS///////////////////////////////
    boolean isKind(Kind kind)
    {
        return currToken.getKind() == kind;
    }

    void consume()
    {
        try {

            currToken = lexer.next();

        } catch (LexicalException e) {
            e.printStackTrace();
        }
    }
    boolean match(Kind kind)
    {
        if(currToken.getKind() == kind)
        {
            System.out.println("matched "+ currToken.getKind());
            consume();
            System.out.println("matched and consumed "+ currToken.getKind()+" "+currToken.getText());
            return true;
        }
        else
        {
            return false;
        }
    }
    void SOS() {
        //while (true){
            try {
                while ((lexer.peek().getKind() != Kind.EOF))
                    System.out.println("SOS "+ currToken.getKind()+" "+ currToken.getText());
                    consume();

            } catch (LexicalException e) {
                e.printStackTrace();
            }

        //}
    }
}
