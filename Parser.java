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
        } catch (LexicalException e) {
            e.printStackTrace();
        }

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
            expr();
        return currentExpr;
    }


    public Expr expr() //::=ConditionalExpr | LogicalOrExpr
    {
        Expr currExpr = null;
        currExpr =  LogicalOrExpr();
        return currExpr;
    }
    public Expr LogicalOrExpr()//LogicalAndExpr ( '|' LogicalAndExpr)*
    {
        IToken firstToken = currToken;
        Expr left = null;
        Expr right = null;

        left =  LogicalAndExpr();

        while(isKind(Kind.OR))
        {
            consume();
            right =  LogicalAndExpr();
        }
        return left;
    }
    public Expr logicalAndExpr()
    {
        comparisonExpr();

        while(isKind(Kind.AND))
        {
            consume();
            comparisonExpr();
        }

        return null;
    }

    public Expr comparisonExpr()
    {
        additiveExpr();

        while(isKind(Kind.LT) || isKind(Kind.GT) || isKind(Kind.EQUALS) || isKind(Kind.NOT_EQUALS) || isKind(Kind.LE) || isKind(Kind.GE))
        {
            consume();
            additiveExpr();
        }
        return null;
    }

    public Expr additiveExpr()
    {
        multiplicitaveExpr();

        while(isKind(Kind.PLUS) || isKind(Kind.MINUS)
        {
            consume();
            multiplicitaveExpr();
        }
        return null;
    }

    public Expr multiplicitaveExpr()
    {
        unaryExpr();

        while(isKind(Kind.TIMES) || isKind(Kind.DIV) || isKind(Kind.MOD))
        {
            consume();
            unaryExpr();
        }
        return null;
    }

    public Expr unaryExpr()
    {
        if(isKind(Kind.BANG) || isKind(Kind.MINUS) || isKind(Kind.COLOR_OP) || isKind(Kind.IMAGE_OP))
        {
            consume();
            unaryExpr();
        }
        else
        {
            unaryExprPostfix();
        }
        return null;
    }

    public Expr unaryExprPostfix()
    {
        primaryExpr();
        if(isKind(Kind.LSQUARE))
        {
            consume();
            //pixelSelector();
        }

        return null;
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

        return primaryExpr();
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
            consume();
            return true;
        }
        else
        {
            return false;
        }
    }
}
