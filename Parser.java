package edu.ufl.cise.plc;



import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.IToken.Kind;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;


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
        //Expr returnExpr = expr();
        Program returnProgram = program();

        if(errorLex){
            throw new LexicalException("Lexical");
        }
        if(errorSyn){
            throw new SyntaxException("Syntax");
        }

        return returnProgram;

    }
    public Program program() throws LexicalException {

        IToken firstToken = currToken;
        Types.Type returnType = null;
        String name = "";
        List<NameDef> params = new ArrayList<>();
        List<ASTNode> decsAndStatements = new ArrayList<>();

        if(isKind(Kind.TYPE) || isKind(Kind.KW_VOID)){

            returnType = Types.Type.toType(currToken.getText());
            System.out.println("returnType: " + returnType);
            consume();

            name = currToken.getText();
            match(Kind.IDENT);



            System.out.println("name: " + name);

            //consume();

            match(Kind.LPAREN);
            System.out.println("matched: LParen");

            if(errorSyn == true){return null;}

            while(!isKind(Kind.RPAREN)) {
                print("entered here");
               params.add(nameDef());
               consume();
                print("here here");


                if(isKind(Kind.COMMA)){
                   consume();
               }
           }
            print("matching RParen");
            match(Kind.RPAREN);

           while(isKind(Kind.EOF) == false && errorSyn == false) {
               System.out.println("here now");

               if (isKind(Kind.TYPE)) {
                   decsAndStatements.add(declaration());
                   //consume();
                   System.out.println("Just consumed declaration. Currtoken is now: " + currToken.getKind());
                   match(Kind.SEMI);
                   System.out.println("added Declaration" + currToken.getText());

               }
               else //TO DO: Change to else if to specifically check for declarations
               {
                   if(isKind(Kind.IDENT) || isKind(Kind.KW_WRITE) || isKind(Kind.RETURN))
                   {
                       decsAndStatements.add(statement());
                       match(Kind.SEMI);
                       System.out.println("added statement" + currToken.getText());
                   }
                   else {
                       errorSyn = true;
                   }

               }
           }
        }
        else{errorSyn = true;}
        return new Program(firstToken, returnType, name, params, decsAndStatements);
    }
    public NameDef nameDef(){
        IToken firstToken = currToken;
        IToken type = currToken;

        match(Kind.TYPE);

        IToken name;
        Dimension dim;
        NameDef nameDefinition;


        if(isKind(Kind.IDENT)){
            name = currToken;
            nameDefinition = new NameDef(firstToken, type, name);
        }
        else{
            match(Kind.LSQUARE);
            dim = dimension();
            name = currToken;
            nameDefinition = new NameDefWithDim(firstToken,type,name,dim);
        }

        return nameDefinition;
    }

    public Declaration declaration(){
        NameDef namedefinition = nameDef();
        IToken firstToken = currToken;
        consume();
        IToken op = null;
        Expr expr = null;

        if(isKind(Kind.ASSIGN) || isKind(Kind.LARROW)){
            op = currToken;
            consume();
            expr = expr();
        }
        return new VarDeclaration(firstToken,namedefinition,op,expr);
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
        IToken firstToken = currToken;

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
            Expr red = expr();
            match(Kind.COMMA);
            Expr green = expr();
            match(Kind.COMMA);
            Expr blue = expr();

            match(Kind.RANGLE);

            currentExpr = new ColorExpr(firstToken, red, blue, green);
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
        IToken firstToken = currToken;
        Expr x = expr();
        match(Kind.COMMA);
        Expr y = expr();
        Dimension dimen = new Dimension(firstToken,x ,y);
        match(Kind.RSQUARE);
        return dimen;
    }
    public Statement statement() {
        IToken firstToken = currToken;
        PixelSelector pixel = null;
        Statement statement = null;

        if(isKind(Kind.IDENT)){
            String name = currToken.getText();
            consume();
            //Check fo a PixelSelector
            if(isKind(Kind.LSQUARE)){
                consume();
                pixel = pixelSelector();
            }
                if(isKind(Kind.ASSIGN))
                {
                  consume();
                  Expr expression = expr();
                  statement = new AssignmentStatement(firstToken, name, pixel, expression );
                }
                else if(isKind(Kind.LARROW)){
                    consume();
                    Expr expression = expr();
                    statement = new ReadStatement(firstToken, name, pixel, expression );
                }
                else {
                    errorSyn = true;
                }
        }
        else if(isKind(Kind.KW_WRITE)){
           consume();
           Expr source = expr();
           match(Kind.RARROW);
           Expr dest = expr();
           statement = new WriteStatement(firstToken, source, dest);
        }
        else if(isKind(Kind.RETURN)){
            consume();
            Expr expression = expr();
            statement = new ReturnStatement(firstToken, expression);
        }
//        else{
//            print("Statement error");
//            errorSyn = true;
//        }

        return  statement;
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
        System.out.println("Trying to consume: " + currToken.getText());

        try {

            if(currToken.getKind()==Kind.EOF){

                print("EOF error");
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
            print("Match error: Should be: " + kind + ", but is " + currToken.getKind());
            errorSyn=true;
            return false;
        }

    }

    public void print(String toPrint)
    {
        System.out.println(toPrint);
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
