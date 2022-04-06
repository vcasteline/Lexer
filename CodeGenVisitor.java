package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Dimension;

import java.util.*;
import java.nio.charset.StandardCharsets;

public class CodeGenVisitor implements ASTVisitor {

    String Package;
    String returnCode;

    CodeGenVisitor(String pack)
    {
        this.Package = pack;
    }

    String formatParams(List<NameDef> params, Object arg)
    {
        String returnString = "";
        int listSize = params.size();

        if(listSize == 0)
        {
            returnString = "";
            return returnString;
        }


        return returnString;
    }

    String handleDecsAndStatements(List<ASTNode> decState, Object arg) throws Exception {
        String returnString = "";
        StringBuilder str = new StringBuilder();

        for(int i = 0; i < decState.size(); ++i)
        {
            str.append(decState.get(i).visit(this, arg));
        }

        returnString = str.toString();

        return returnString;
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


        return intLitExpr.getValue();
    }

    @Override
    public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(floatLitExpr.getValue()).append("f");

        return str.toString();
    }

    @Override
    public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
        return null;
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
        return null;
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {

        StringBuilder code = new StringBuilder();

        code.append("package ").append(Package).append(";\n");
        code.append("public class ").append(program.getName()).append(" {");
        code.append("public static ").append(program.getReturnType().toString().toLowerCase(Locale.ROOT)).append(" apply (");
        code.append(formatParams(program.getParams(), arg)).append("){\n");
        code.append(handleDecsAndStatements(program.getDecsAndStatements(), arg)).append("\n}\n}");



        returnCode = code.toString();


//                "package " + Package + ";\n" +
//                //"<imports>\n" +
//                "public class y {\n" +
//                "   public static int apply(){\n" +
//                "        return 42;\n" +
//                "   }\n" +
//                "}\n";
        return(returnCode);
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
        return null;
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
        return null;
    }

    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
        return null;
    }
}
