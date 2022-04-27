package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Dimension;
import edu.ufl.cise.plc.runtime.ColorTuple;
import edu.ufl.cise.plc.runtime.ConsoleIO;
import edu.ufl.cise.plc.runtime.ImageOps;

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
        StringBuilder str = new StringBuilder();
        str.append("(ColorTuple.unpack(Color.").append(colorConstExpr.getText()).append(".getRGB()))");
        return str.toString();
    }

    @Override
    public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();
        StringBuilder prompt = new StringBuilder();


        String type = consoleExpr.getCoerceTo().toString().toLowerCase(Locale.ROOT);


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
        StringBuilder str = new StringBuilder();

//        String redStr = "";
//        if(red instanceof UnaryExpr){
//           if(((UnaryExpr) red).getOp().getKind() == IToken.Kind.COLOR_OP){
//               redStr = "ImageOps.getColorTuple(" + red + "," + ((UnaryExpr) red).getExpr().
//           }
//        }
        str.append("new ColorTuple(").append(colorExpr.getRed().visit(this, arg)).append(", ").append(colorExpr.getGreen().visit(this, arg)).append(", ").append(colorExpr.getBlue().visit(this, arg));
        str.append(")");
        return str.toString();
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpression, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        System.out.println("NORMAL UNARY" + unaryExpression.getOp().getText());
        System.out.println("hey this is unaryexpr:" + unaryExpression);
        if(unaryExpression.getExpr() instanceof UnaryExprPostfix){

            str.append("ColorTuple.").append(unaryExpression.getOp().getText());
            str.append("(").append(unaryExpression.getExpr().visit(this,arg)).append(")");
        }
        else {
            if (unaryExpression.getOp().getKind() == IToken.Kind.IMAGE_OP) {
                str.append("(").append(unaryExpression.getExpr().visit(this, arg)).append(".").append(unaryExpression.getOp().getText()).append("()").append(")");
            } else if (unaryExpression.getOp().getKind() == IToken.Kind.COLOR_OP) {
                str.append("ImageOps.extract");
                switch (unaryExpression.getOp().getText()) {
                    case "getRed" -> str.append("Red");
                    case "getGreen" -> str.append("Green");
                    case "getBlue" -> str.append("Blue");
                }
                str.append("(").append(unaryExpression.getExpr().visit(this, arg)).append(")");
            } else {
                str.append("(").append(unaryExpression.getOp().getText()).append(unaryExpression.getExpr().visit(this, arg)).append(")");
            }
        }
        return str.toString();
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {

        StringBuilder str = new StringBuilder();
        System.out.println("type left: "+ binaryExpr.getLeft().getType());
        System.out.println("type right: "+ binaryExpr.getRight().getType());
        System.out.println("coerced: "+ binaryExpr.getLeft().getCoerceTo());

        if(binaryExpr.getLeft().getType() == Types.Type.COLOR && binaryExpr.getRight().getType() == Types.Type.COLOR ){
            str.append("(").append("ImageOps.binaryTupleOp(").append(binaryExpr.getOp().getKind());
            str.append(", ").append(binaryExpr.getLeft().visit(this, arg)).append(", ").append(binaryExpr.getRight().visit(this, arg)).append("))");
        }
        else if(binaryExpr.getLeft().getCoerceTo() == Types.Type.COLOR && binaryExpr.getRight().getCoerceTo() == Types.Type.COLOR ){ //BE CAREFUL IDK IF THIS CORRECT
            str.append("(").append("ImageOps.binaryImageImageOp(").append(binaryExpr.getOp().getKind()).append(", ").append(binaryExpr.getLeft().getText()).append(", ").append(binaryExpr.getRight().getText()).append("))");
        }
        else if(binaryExpr.getOp().getKind() == IToken.Kind.EQUALS && binaryExpr.getLeft().getType() == Types.Type.STRING && binaryExpr.getRight().getType() == Types.Type.STRING){
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
        System.out.println("IDENT!! " + identExpr.getText());
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
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();




        if(assignmentStatement.getTargetDec().getType() == Types.Type.IMAGE ){
            if(assignmentStatement.getTargetDec().getDim() != null && assignmentStatement.getExpr().getType() == Types.Type.IMAGE){
                str.append(assignmentStatement.getName()).append(" = ");
                str.append("ImageOps.resize(").append(assignmentStatement.getExpr().visit(this, arg)).append(", ").append(assignmentStatement.getTargetDec().getDim().getWidth().visit(this, arg)).append(", ");
                str.append(assignmentStatement.getTargetDec().getDim().getWidth().visit(this, arg)).append(");\n");
            }
            else if(assignmentStatement.getTargetDec().getDim() == null && assignmentStatement.getExpr().getType() == Types.Type.IMAGE){
                str.append(assignmentStatement.getName()).append(" = ");
                str.append("ImageOps.clone(").append(assignmentStatement.getExpr().visit(this, arg)).append(");\n");
            }

            else if(assignmentStatement.getExpr().getCoerceTo() == Types.Type.COLOR && assignmentStatement.getExpr() instanceof BinaryExpr){
                str.append(assignmentStatement.getName()).append(" = ");
                str.append(assignmentStatement.getExpr().visit(this, arg)).append(";\n");
            }
            else if(assignmentStatement.getExpr().getCoerceTo() == Types.Type.COLOR){
                str.append("for(int x=0; x<").append(assignmentStatement.getName()).append(".getWidth(); x++)\n");
                str.append("for(int y=0; y<").append(assignmentStatement.getName()).append(".getHeight(); y++)\n");
                str.append("ImageOps.setColor(").append(assignmentStatement.getName()).append(", x, y, ");
                str.append(assignmentStatement.getExpr().visit(this, arg)).append(");\n");

            }
            else if(assignmentStatement.getExpr().getCoerceTo() == Types.Type.INT){
                str.append("for(int x=0; x<").append(assignmentStatement.getName()).append(".getWidth(); x++)\n");
                str.append("for(int y=0; y<").append(assignmentStatement.getName()).append(".getHeight(); y++)\n");
                str.append("ImageOps.setColor(").append(assignmentStatement.getName()).append(", x, y, ");
                str.append(assignmentStatement.getExpr().visit(this, arg)).append(");\n");
            }
        }
        else {
            str.append(assignmentStatement.getName()).append(" = ");
            if (assignmentStatement.getExpr().getCoerceTo() != null) {
                str.append("(").append(assignmentStatement.getExpr().getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")");
            }
            str.append(assignmentStatement.getExpr().visit(this, arg)).append(";\n");
        }

        return str.toString();
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        System.out.println("before if");

        if(writeStatement.getSource().getType() == Types.Type.IMAGE && writeStatement.getDest().getType() == Types.Type.CONSOLE){
            System.out.println("before line");

            str.append("ConsoleIO.displayImageOnScreen(").append(writeStatement.getSource().visit(this, arg)).append(");\n");
            System.out.println("here  now");
        }
        System.out.println("before consolevisit");

        ConsoleIO.console.println(writeStatement.getSource().visit(this, arg));
        System.out.println("here outside now");

        //str.append(writeStatement.getSource().visit(this, arg)).append(";");

        return str.toString();
    }

    @Override
    public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        String w = readStatement.getTargetDec().getDim().getWidth().getText();
        String h = readStatement.getTargetDec().getDim().getHeight().getText();

        if(readStatement.getTargetDec().getType() == Types.Type.IMAGE){
            str.append(readStatement.getName()).append(" = ").append("FileURLIO.readImage(").append(readStatement.getSource().visit(this, arg)).append(", ");
            str.append(w).append(", ").append(h).append(");\n");
        }
        else{
            str.append(readStatement.getName()).append(" = ").append(readStatement.getSource().visit(this, arg)).append(";\n");

        }
        return str.toString();
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {

        StringBuilder code = new StringBuilder();

        code.append("package ").append(Package).append(";\n");
        //if(program.getReturnType() == Types.Type.IMAGE){
            code.append("import ").append("java.awt.image.BufferedImage").append(";\n");
            code.append("import ").append("edu.ufl.cise.plc.runtime.FileURLIO").append(";\n");
            code.append("import ").append("edu.ufl.cise.plc.runtime.ImageOps;\n");
            code.append("import ").append("edu.ufl.cise.plc.runtime.ColorTuple;\n");
            code.append("import ").append("java.awt.Color").append(";\n");
            code.append("import ").append("static edu.ufl.cise.plc.runtime.ImageOps.BoolOP.*;\n");
            code.append("import ").append("static edu.ufl.cise.plc.runtime.ImageOps.OP.*;\n");

            code.append("import ").append("edu.ufl.cise.plc.runtime.ConsoleIO;\n");

        //}
        code.append("public class ").append(program.getName()).append(" {");
        code.append("public static ");

        if(program.getReturnType() == Types.Type.IMAGE){
            code.append("BufferedImage").append(" apply (");

        }else if(program.getReturnType() == Types.Type.COLOR){
            code.append("ColorTuple").append(" apply (");
        }
        else{
            code.append(formatType(program.getReturnType().toString())).append(" apply (");

        }

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
        throw new UnsupportedOperationException("Not yet implemented");

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
        if(declaration.getNameDef().getType() == Types.Type.IMAGE){
            str.append("BufferedImage ").append(declaration.getName());
        }else if(declaration.getNameDef().getType() == Types.Type.COLOR){
            str.append("ColorTuple ").append(declaration.getName());

        }
        else{
            str.append(declaration.getNameDef().visit(this, arg));
        }

        if(declaration.getExpr() != null) {
            str.append(" = ");
            if (declaration.getNameDef().getType() == Types.Type.IMAGE) { //if is an image with an expression
                if(declaration.getExpr().getType() == Types.Type.IMAGE){
                    if(declaration.getNameDef().getDim() != null){

                        str.append("ImageOps.resize(").append(declaration.getExpr().visit(this, arg)).append(", ").append(declaration.getNameDef().getDim().getWidth().visit(this, arg)).append(", ");
                        str.append(declaration.getNameDef().getDim().getHeight().visit(this, arg)).append(")");
                    }else{
                        str.append("ImageOps.clone(").append(declaration.getExpr().visit(this, arg)).append(");\n");
                    }

                }else if(declaration.getNameDef().getDim() == null && declaration.getExpr() instanceof BinaryExpr){
                    str.append("ImageOps.binaryImageScalarOp(").append(((BinaryExpr) declaration.getExpr()).getOp().getKind().toString()).append(", ").append(((BinaryExpr) declaration.getExpr()).getLeft().getText()).append(", ").append(((BinaryExpr) declaration.getExpr()).getRight().getText());
                    str.append(")");
                }
                else if (declaration.getNameDef().getDim() != null) { //if image has an expression and has a dim
                str.append("FileURLIO.readImage(").append(declaration.getExpr().visit(this, arg)).append(", ").append(declaration.getNameDef().getDim().getWidth().visit(this, arg)).append(", ").append(declaration.getNameDef().getDim().getHeight().visit(this, arg)).append(");\n");
                str.append("FileURLIO.closeFiles()");
                }
                else if (declaration.getNameDef().getDim() == null) { //if image has an expression and does not have a dim
                    str.append("FileURLIO.readImage(").append(declaration.getExpr().visit(this, arg)).append(")");
                }
            }
           else {
                if (declaration.getExpr().getCoerceTo() != null) {
                    str.append("(").append(declaration.getExpr().getCoerceTo().toString().toLowerCase(Locale.ROOT)).append(")");
                }
                str.append(declaration.getExpr().visit(this, arg));
            }
            str.append(";\n");
        }else{
            if (declaration.getNameDef().getType() == Types.Type.IMAGE) { //if is an image without an expression
                if (declaration.getNameDef().getDim() != null) { // if is an image without an expression and with dim
                    System.out.println("idk what this is: "+declaration.getNameDef().getDim().getWidth());

                    str.append(" = new BufferedImage(").append(declaration.getNameDef().getDim().getWidth().visit(this, arg)).append(", ").append(declaration.getNameDef().getDim().getHeight().visit(this, arg)).append(", ").append("BufferedImage.TYPE_INT_RGB)");
                }else{
                    //error
                }
            }
            str.append(";\n");
        }

        return str.toString();
    }

    @Override
    public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
        StringBuilder str = new StringBuilder();
        System.out.println("UNARY!!");

//        str.append(unaryExprPostfix.get).append("(");
    str.append(unaryExprPostfix.getExpr().visit(this, arg)).append(".getRGB(").append(unaryExprPostfix.getSelector().getX().visit(this,arg)).append(", ").append(unaryExprPostfix.getSelector().getY().visit(this, arg)).append(")");
//        str.append("ImageOps.getColorTuple(").append(unaryExprPostfix.getExpr().getText()).append(", ").append(unaryExprPostfix.getSelector().getX().visit(this, arg)).append(", ").append(unaryExprPostfix.getSelector().getY().visit(this, arg)).append(")");
        return str.toString();
    }
}
