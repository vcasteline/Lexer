package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ASTVisitor;

public class Parser implements IParser{
    ILexer lexer;
    Parser(String input){
        lexer = getLexer(input);
        try {
            ASTNode currentNode = new ASTNode(lexer.next()) {
                @Override
                public Object visit(ASTVisitor v, Object arg) throws Exception {
                    return null;
                }
            };
        } catch (LexicalException e) {
            e.printStackTrace();
        }
        //System.out.println(lexer.getLexer().next().getKind());
    }
    public ASTNode parse() throws PLCException{
        System.out.println();
        return null;
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
}
