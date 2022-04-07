package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Dimension;
import edu.ufl.cise.plc.runtime.ConsoleIO;

import java.util.*;
import java.nio.charset.StandardCharsets;

public class CodeGenVisitor implements ASTVisitor {

    String Package;
    String returnCode;

    CodeGenVisitor(String pack)
    {
        this.Package = pack;
    }

    String formatParams(List<NameDef> params, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        int listSize = params.size();

        if(listSize == 0)
        {
            return "";
        }

        for(int i = 0; i < params.size(); ++i)
        {
            str.append(params.get(i).visit(this, arg));
            if(i != params.size()-1)
            {
                str.append(", ");
            }

        }


        return str.toString();
    }

    String handleDecsAndStatements(List<ASTNode> decState, Object arg) throws Exception {

        String returnString = "";
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < decState.size(); ++i)
        {
            System.out.println("decState: " + decState.get(i).getText());
            str.append(decState.get(i).visit(this, arg));
        }

        returnString = str.toString();

        return returnString;
    }

    String formatType(String type)
    {
        if(type == "STRING")
        {
            return "String";
        }
        else {
            return type.toLowerCase(Locale.ROOT);
        }
    }



    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
//        str.append("\"").append(booleanLitExpr.getValue()).append("\"");
        str.append(booleanLitExpr.getValue());


        return str.toString();
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();

        str.append("\"").append(stringLitExpr.getValue()).append("\"");

        return str.toString();
    }

    @Override
    public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {

        StringBuilder str= new StringBuilder();

        if(intLitExpr.getCoerceTo() != null && intLitExpr.getCoerceTo() != Types.Type.INT)
        {
            str.append("(").append(intLitExpr.getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")").append(intLitExpr.getText());
            return str.toString();
        }
        else
        {
            return intLitExpr.getText();
        }
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(floatLitExpr.getValue()).append("f");

        if(floatLitExpr.getCoerceTo() != null && floatLitExpr.getCoerceTo() != Types.Type.FLOAT) {
            str.append("(").append(floatLitExpr.getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")").append(floatLitExpr.getText());
        }
        return str.toString();

    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();
        StringBuilder prompt = new StringBuilder();


        String type = consoleExpr.getCoerceTo().toString().toLowerCase(Locale.ROOT);
        System.out.println("Type: " + type);

        if(type.equals("int"))
        {
            type = "Integer";
        }

        String boxType = type.substring(0,1).toUpperCase(Locale.ROOT) + type.substring(1);
        prompt.append("Enter ").append(boxType).append(": ");



        str.append("(").append(type).append(") ");
        str.append(ConsoleIO.readValueFromConsole(consoleExpr.getCoerceTo().toString(), prompt.toString()));


        return str.toString();
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append("(").append(unaryExpression.getOp().getText()).append(unaryExpression.getExpr().visit(this,arg)).append(")");
        return str.toString();
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();

        if(binaryExpr.getOp().getKind() == IToken.Kind.EQUALS && binaryExpr.getLeft().getType() == Types.Type.STRING && binaryExpr.getRight().getType() == Types.Type.STRING){
           str.append("(").append(binaryExpr.getLeft().visit(this, arg)).append(".equals(").append(binaryExpr.getRight().visit(this, arg)).append(")").append(")");
        }
        else {
            str.append("(").append(binaryExpr.getLeft().visit(this, arg)).append(" ").append(binaryExpr.getOp().getText()).append(" ").append(binaryExpr.getRight().visit(this, arg)).append(")");
        }

        return str.toString();
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {

        StringBuilder str= new StringBuilder();

        if(identExpr.getCoerceTo() != null && identExpr.getCoerceTo() != identExpr.getType())
        {
            str.append("(").append(identExpr.getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")").append(identExpr.getText());
            return str.toString();
        }
        else
        {
            return identExpr.getText();
        }

    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append("(").append(conditionalExpr.getCondition().visit(this, arg)).append(" ? ");
        str.append(conditionalExpr.getTrueCase().visit(this, arg)).append(" : ").append(conditionalExpr.getFalseCase().visit(this, arg)).append(")");
        return str.toString();
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();



        str.append(assignmentStatement.getName()).append(" = ");
        System.out.println("getcoerceto: "+(assignmentStatement.getExpr().getCoerceTo()));
        System.out.println("targetdectype: "+ assignmentStatement.getTargetDec().getType());

        if(assignmentStatement.getExpr().getCoerceTo() != null) {
            str.append("(").append(assignmentStatement.getExpr().getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")");
        }
        str.append(assignmentStatement.getExpr().visit(this, arg)).append(";\n");


        return str.toString();
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();


       ConsoleIO.console.println(writeStatement.getSource().visit(this, arg));

        //str.append(writeStatement.getSource().visit(this, arg)).append(";");

        return "";
    }

    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(readStatement.getName()).append(" = ").append(readStatement.getSource().visit(this, arg)).append(";\n");
        return str.toString();
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {

        StringBuilder code = new StringBuilder();

        code.append("package ").append(Package).append(";\n");
        code.append("public class ").append(program.getName()).append(" {");
        code.append("public static ");


        code.append(formatType(program.getReturnType().toString())).append(" apply (");


        code.append(formatParams(program.getParams(), arg)).append("){\n");
        code.append(handleDecsAndStatements(program.getDecsAndStatements(), arg)).append("\n}\n}");



        returnCode = code.toString();

        return(returnCode);
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();

        str.append(formatType(nameDef.getType().toString())).append(" ").append(nameDef.getName());

        return str.toString();
    }

    @Override
    public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();

        str.append("return ").append(returnStatement.getExpr().visit(this, arg)).append(";");


        String returnString = str.toString();

        return returnString;
    }

    @Override
    public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();
        str.append(declaration.getNameDef().visit(this, arg));

        if(declaration.getExpr() != null)
        {
            str.append(" = ");
            if(declaration.getExpr().getCoerceTo() != null){
                str.append("(").append(declaration.getExpr().getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")");
            }
            str.append(declaration.getExpr().visit(this, arg));

        }
            str.append(";\n");


        return str.toString();
    }

    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
        return null;
    }
}
