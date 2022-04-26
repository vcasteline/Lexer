package edu.ufl.cise.plc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.ast.ASTNode;
import edu.ufl.cise.plc.ast.ASTVisitor;
import edu.ufl.cise.plc.ast.AssignmentStatement;
import edu.ufl.cise.plc.ast.BinaryExpr;
import edu.ufl.cise.plc.ast.BooleanLitExpr;
import edu.ufl.cise.plc.ast.ColorConstExpr;
import edu.ufl.cise.plc.ast.ColorExpr;
import edu.ufl.cise.plc.ast.ConditionalExpr;
import edu.ufl.cise.plc.ast.ConsoleExpr;
import edu.ufl.cise.plc.ast.Declaration;
import edu.ufl.cise.plc.ast.Dimension;
import edu.ufl.cise.plc.ast.Expr;
import edu.ufl.cise.plc.ast.FloatLitExpr;
import edu.ufl.cise.plc.ast.IdentExpr;
import edu.ufl.cise.plc.ast.IntLitExpr;
import edu.ufl.cise.plc.ast.NameDef;
import edu.ufl.cise.plc.ast.NameDefWithDim;
import edu.ufl.cise.plc.ast.PixelSelector;
import edu.ufl.cise.plc.ast.Program;
import edu.ufl.cise.plc.ast.ReadStatement;
import edu.ufl.cise.plc.ast.ReturnStatement;
import edu.ufl.cise.plc.ast.StringLitExpr;
import edu.ufl.cise.plc.ast.Types.Type;
import edu.ufl.cise.plc.ast.UnaryExpr;
import edu.ufl.cise.plc.ast.UnaryExprPostfix;
import edu.ufl.cise.plc.ast.VarDeclaration;
import edu.ufl.cise.plc.ast.WriteStatement;

import static edu.ufl.cise.plc.ast.Types.Type.*;

public class TypeCheckVisitor implements ASTVisitor {

	SymbolTable symbolTable = new SymbolTable();
	Program root;
	
	record Pair<T0,T1>(T0 t0, T1 t1){};  //may be useful for constructing lookup tables.
	
	private void check(boolean condition, ASTNode node, String message) throws TypeCheckException {
		if (!condition) {
			throw new TypeCheckException(message, node.getSourceLoc());
		}
	}
	
	//The type of a BooleanLitExpr is always BOOLEAN.  
	//Set the type in AST Node for later passes (code generation)
	//Return the type for convenience in this visitor.  
	@Override
	public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception {
		booleanLitExpr.setType(Type.BOOLEAN);
//		System.out.println("Visited Bool: " + booleanLitExpr.getText());

		return Type.BOOLEAN;
	}

	@Override
	public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception {

		stringLitExpr.setType(Type.STRING);
//		System.out.println("Visited string");
		return Type.STRING;

	}

	@Override
	public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {
		intLitExpr.setType(Type.INT);
//		System.out.println("Visited Int: " + intLitExpr.getValue());

		return INT;
		//throw new UnsupportedOperationException("Unimplemented Int visit method.");
	}

	@Override
	public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
		floatLitExpr.setType(Type.FLOAT);
		return Type.FLOAT;
	}

	@Override
	public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {

		colorConstExpr.setType(Type.COLOR);
//		System.out.println("Visited colorConst");
		return COLOR;
	}

	@Override
	public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
		consoleExpr.setType(Type.CONSOLE);
		return Type.CONSOLE;
	}
	
	//Visits the child expressions to get their type (and ensure they are correctly typed)
	//then checks the given conditions.
	@Override
	public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
		Type redType = (Type) colorExpr.getRed().visit(this, arg);
		System.out.println("redtype: "+ redType);
		System.out.println("getRed: "+ colorExpr.getRed());

		Type greenType = (Type) colorExpr.getGreen().visit(this, arg);

		Type blueType = (Type) colorExpr.getBlue().visit(this, arg);

		check(redType == greenType && redType == blueType, colorExpr, "color components must have same type");
		check(redType == Type.INT || redType == Type.FLOAT, colorExpr, "color component type must be int or float");
		Type exprType = (redType == Type.INT) ? Type.COLOR : Type.COLORFLOAT;
		colorExpr.setType(exprType);
		return exprType;
	}	

	
	
	//Maps forms a lookup table that maps an operator expression pair into result type.  
	//This more convenient than a long chain of if-else statements. 
	//Given combinations are legal; if the operator expression pair is not in the map, it is an error. 
	Map<Pair<Kind,Type>, Type> unaryExprs = Map.of(
			new Pair<Kind,Type>(Kind.BANG,BOOLEAN), BOOLEAN,
			new Pair<Kind,Type>(Kind.MINUS, FLOAT), FLOAT,
			new Pair<Kind,Type>(Kind.MINUS, INT),INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,INT), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,COLOR), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,IMAGE), IMAGE,
			new Pair<Kind,Type>(Kind.IMAGE_OP,IMAGE), INT
			);
	
	//Visits the child expression to get the type, then uses the above table to determine the result type
	//and check that this node represents a legal combination of operator and expression type. 
	@Override
	public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws Exception {
		// !, -, getRed, getGreen, getBlue
		Kind op = unaryExpr.getOp().getKind();
		Type exprType = (Type) unaryExpr.getExpr().visit(this, arg);
		//Use the lookup table above to both check for a legal combination of operator and expression, and to get result type.
		Type resultType = unaryExprs.get(new Pair<Kind,Type>(op,exprType));
		check(resultType != null, unaryExpr, "incompatible types for unaryExpr");
		//Save the type of the unary expression in the AST node for use in code generation later. 
		unaryExpr.setType(resultType);
		//return the type for convenience in this visitor.
		return resultType;
	}


	//This method has several cases. Work incrementally and test as you go. 
	@Override
	public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {
		
		Kind op = binaryExpr.getOp().getKind();

		Type leftType = (Type) binaryExpr.getLeft().visit(this, arg);
		Type rightType = (Type) binaryExpr.getRight().visit(this, arg);

		if(op == Kind.AND || op == Kind.OR || op == Kind.EQUALS || op == Kind.NOT_EQUALS || op == Kind.LT
				|| op == Kind.GT || op == Kind.LE || op == Kind.GE)
		{
			binaryExpr.setType(BOOLEAN);
			return BOOLEAN;
		}
		else {
			Type returnType = symbolTable.checkMap(leftType, rightType);
			if(leftType==INT && rightType==COLOR){
				binaryExpr.getLeft().setCoerceTo(COLOR);
			}
			else if(leftType==COLOR && rightType==INT){
				binaryExpr.getRight().setCoerceTo(COLOR);
			}
			else if(leftType==FLOAT && rightType==COLOR || leftType==COLOR && rightType==FLOAT){
				binaryExpr.getLeft().setCoerceTo(COLORFLOAT);
				binaryExpr.getRight().setCoerceTo(COLORFLOAT);
			}
			else if(leftType==INT && rightType==FLOAT){
				binaryExpr.getLeft().setCoerceTo(FLOAT);
			}
			else if(leftType==FLOAT && rightType==INT){
				binaryExpr.getRight().setCoerceTo(FLOAT);
			}
			else if(leftType==COLORFLOAT && rightType==COLOR){
				binaryExpr.getRight().setCoerceTo(COLORFLOAT);
			}
			else if(leftType==COLOR && rightType==COLORFLOAT){
				binaryExpr.getLeft().setCoerceTo(COLORFLOAT);
			}
			check(returnType != null, binaryExpr, "incompatible types for BinaryExpr");
			binaryExpr.setType(returnType);
			return returnType;
		}
	}

	@Override
	public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {
		//TODO:  implement this method
		String name = identExpr.getText();
		//identExpr.setType(Type.);
		//throw new UnsupportedOperationException("Unimplemented Ident visit method.");
//		System.out.println("IDENT!!!");
//		System.out.println("ident: " + name);

		//Check if map contains the Ident
		if(symbolTable.map.containsKey(name) == true)
		{
//			System.out.println("Entered");
			identExpr.setType(symbolTable.Search(name).getType());
			identExpr.setDec(symbolTable.Search(name));

			if(identExpr.getDec().isInitialized() == false)
			{
				throw new TypeCheckException("Ident has not been initialized");
			}
//			System.out.println(symbolTable.Search(name).getType());
			return symbolTable.Search(name).getType();
		}


		//throw new UnsupportedOperationException("Unimplemented Ident visit method.");
		return identExpr.getType();
	}

	@Override
	public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
		//TODO  implement this method
		Type conditionType = (Type) conditionalExpr.getCondition().visit(this, arg);
		Type trueCaseType = (Type) conditionalExpr.getTrueCase().visit(this, arg);
		Type falseCaseType = (Type) conditionalExpr.getFalseCase().visit(this, arg);;
		check(conditionType==BOOLEAN, conditionalExpr, "Condition type must be BOOLEAN");
		check(trueCaseType == falseCaseType, conditionalExpr, "Condition Types do not match");
		conditionalExpr.setType(trueCaseType);

		return trueCaseType;
	}

	@Override
	public Object visitDimension(Dimension dimension, Object arg) throws Exception {
		//TODO  implement this method
		Type widthType = (Type)dimension.getWidth().visit(this, arg);
		Type heightType = (Type)dimension.getHeight().visit(this, arg);
		check(widthType == INT && heightType == INT, dimension, "Both arguments in a Dimension must be of type INT" );

		return null;
	}

	@Override
	//This method can only be used to check PixelSelector objects on the right hand side of an assignment. 
	//Either modify to pass in context info and add code to handle both cases, or when on left side
	//of assignment, check fields from parent assignment statement.
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		Type xType = (Type) pixelSelector.getX().visit(this, arg);
		check(xType == Type.INT, pixelSelector.getX(), "only ints as pixel selector components");
		Type yType = (Type) pixelSelector.getY().visit(this, arg);
		check(yType == Type.INT, pixelSelector.getY(), "only ints as pixel selector components");
		return null;
	}

	@Override
	//This method several cases--you don't have to implement them all at once.
	//Work incrementally and systematically, testing as you go.  
	public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
		//TODO:  implement this method
		Declaration targetDec = symbolTable.Search(assignmentStatement.getName());

		System.out.println("assignment state: "+ assignmentStatement);

		Type targetType = symbolTable.Search(assignmentStatement.getName()).getType();
		System.out.println("targeType: " + targetType);

		assignmentStatement.setTargetDec(targetDec);
		assignmentStatement.getTargetDec().setInitialized(true);
//		if(targetType == IMAGE && assignmentStatement.getSelector() !=null){
//			assignmentStatement.getSelector().getX().getText()
//		}
		//System.out.println("targetDec " + assignmentStatement.getTargetDec());

		if(targetType!=IMAGE){
			assignmentStatement.getExpr().visit(this, arg);

			check(assignmentStatement.getSelector() == null, assignmentStatement, "Cannot have pixel selector if target type is IMAGE" );
			if(targetType==INT && (assignmentStatement.getExpr().getType() == FLOAT || assignmentStatement.getExpr().getType() == COLOR)  ||
					targetType==FLOAT && assignmentStatement.getExpr().getType() == INT||
					targetType==COLOR && assignmentStatement.getExpr().getType() == INT){

				assignmentStatement.getExpr().setCoerceTo(targetType);
			}
			check(targetType == assignmentStatement.getExpr().getType() || targetType == assignmentStatement.getExpr().getCoerceTo(), assignmentStatement, "Expression must be assignment compatible with target");
		}
		else if(targetType == IMAGE && assignmentStatement.getSelector() ==null){
			System.out.println("i dont have a pixel selector");
			assignmentStatement.getExpr().visit(this, arg);

//			System.out.println(assignmentStatement.getExpr().getType());
			if(assignmentStatement.getExpr().getType() == INT){
//				System.out.println("should get in here");
				assignmentStatement.getExpr().setCoerceTo(COLOR);
			}
			else if(assignmentStatement.getExpr().getType() == FLOAT){
				assignmentStatement.getExpr().setCoerceTo(COLORFLOAT);
			}
//			else if(assignmentStatement.getExpr().getType() == COLOR ||assignmentStatement.getExpr().getType() == COLORFLOAT){
//				assignmentStatement.getExpr().setCoerceTo(IMAGE);
//			}
			check(targetType == assignmentStatement.getExpr().getType() ||  assignmentStatement.getExpr().getCoerceTo() == COLOR || assignmentStatement.getExpr().getCoerceTo() == COLORFLOAT || assignmentStatement.getExpr().getType() == COLOR || assignmentStatement.getExpr().getType() == COLORFLOAT, assignmentStatement, "Expression must be assignment compatible with target");

		}
		else if(targetType == IMAGE && assignmentStatement.getSelector() !=null){


			Expr x = assignmentStatement.getSelector().getX();
			Expr y = assignmentStatement.getSelector().getY();
			System.out.println("this is xtext: "+ x.getText());

			check(symbolTable.map.get(assignmentStatement.getName()).getDim() != null, assignmentStatement, "No dimension provided for image");
			//Sets width and height to the value of assignmentStatement Dim in the table
			System.out.println("widthtype: "+symbolTable.map.get(assignmentStatement.getName()).getDim().getHeight().getType());
			String widthName = symbolTable.map.get(assignmentStatement.getName()).getDim().getWidth().getText();
			String heightName = symbolTable.map.get(assignmentStatement.getName()).getDim().getHeight().getText();
			check(symbolTable.Search(x.getText()) == null, assignmentStatement,"Name already declared for x, use another name");
			check(symbolTable.Search(y.getText()) == null, assignmentStatement,"Name already declared for y, use another name");
			//BREAKS TEST 19
			Expr expr = symbolTable.map.get(assignmentStatement.getName()).getDim().getWidth();
			VarDeclaration varDec = new VarDeclaration(assignmentStatement.getFirstToken(), new NameDef(assignmentStatement.getFirstToken(), "int", "x"),assignmentStatement.getFirstToken(), expr);
			varDec.setInitialized(true);
//			symbolTable.map.put(x.getText(), symbolTable.map.get(widthName));
//			symbolTable.map.put(y.getText(), symbolTable.map.get(heightName));
			symbolTable.map.put(x.getText(), varDec);
			symbolTable.map.put(y.getText(), varDec);
			Type xType = (Type) assignmentStatement.getSelector().getX().visit(this, arg);
			Type yType = (Type) assignmentStatement.getSelector().getY().visit(this, arg);
//			Type xType = symbolTable.map.get(assignmentStatement.getName()).getDim().getWidth().getType();
//			Type yType = symbolTable.map.get(assignmentStatement.getName()).getDim().getHeight().getType();
			//System.out.println("xtype is" + x.getType());

			assignmentStatement.getExpr().visit(this, arg);

//			Type xTypefromTable = symbolTable.map.get(widthName).getType();
//			Type yTypefromTable = symbolTable.map.get(heightName).getType();


			check( xType == INT && yType == INT, assignmentStatement, "x and y values must be type INT");
			check(x instanceof IdentExpr && y instanceof IdentExpr, assignmentStatement,"x and y expressions must be IdentExpressions");
			//BREAKS TEST 19


			if(assignmentStatement.getExpr().getType() == COLOR || assignmentStatement.getExpr().getType() == COLORFLOAT || assignmentStatement.getExpr().getType() == FLOAT
					|| assignmentStatement.getExpr().getType() == INT){
				assignmentStatement.getExpr().setCoerceTo(COLOR);

			}
			check(assignmentStatement.getExpr().getType() == COLOR || assignmentStatement.getExpr().getCoerceTo() == COLOR, assignmentStatement, "Right hand side must be of type color, colorfloat, int, or float");

			symbolTable.map.remove(x.getText());
			symbolTable.map.remove(y.getText());
		}

		return null;
	}


	@Override
	public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
		Type sourceType = (Type) writeStatement.getSource().visit(this, arg);
		Type destType = (Type) writeStatement.getDest().visit(this, arg);
		check(destType == Type.STRING || destType == Type.CONSOLE, writeStatement,
				"illegal destination type for write");
		check(sourceType != Type.CONSOLE, writeStatement, "illegal source type for write");
		return null;
	}

	@Override
	public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
		//TODO:  implement this method
		Type targetType = symbolTable.Search(readStatement.getName()).getType();
		check(readStatement.getSelector() == null, readStatement, "Read statement cannot have pixel selector");
		Type rightHand = (Type) readStatement.getSource().visit(this, arg);
		check(rightHand == CONSOLE || rightHand == STRING, readStatement, "Right hand side must be Type CONSOLE or STRING" );
		if(targetType!=null){
			readStatement.setTargetDec(symbolTable.Search(readStatement.getName()));

			readStatement.getTargetDec().setInitialized(true);

			if(rightHand == CONSOLE) {
				readStatement.getSource().setCoerceTo(targetType);
			}
		}
		return null;
	}

	@Override
	public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
		//TODO:  implement this method
		boolean coerced = false;
		String name = declaration.getName();
		boolean readStatement = false;
		Type exprType = null;


		//Check if Var is in the Map
		if(symbolTable.map.containsKey(name) == true)
		{
			throw new TypeCheckException("Cannot have two vars of the same name");
		}


		//Add Var to the map
		symbolTable.map.put(name, declaration);

		//Check if declaration expr() is null.  If it is, the var has not been initialized
		if(declaration.getExpr() != null)
		{
			declaration.setInitialized(true);
			exprType = (Type) declaration.getExpr().visit(this, arg);

		}

		if(declaration.getType() == IMAGE)
		{
			check(declaration.getDim() !=null || exprType == IMAGE || exprType == STRING || exprType == CONSOLE, declaration, "Must have a Dimension or initializer expression of type IMAGE");
			if(declaration.getDim() != null) {
				declaration.getDim().getHeight().setType((Type) declaration.getDim().getHeight().visit(this, arg));


				declaration.getDim().getWidth().setType((Type) declaration.getDim().getWidth().visit(this, arg));


				Type widthType = declaration.getDim().getWidth().getType();
				Type heightType = declaration.getDim().getHeight().getType();
				check(widthType == INT && heightType == INT, declaration, "Image dimensions must be of type INT");
			}



		}

		//Check if type in the symbol table matches with type of RHS of declaration
		if(declaration.isInitialized() &&  symbolTable.Search(name).getType() != declaration.getExpr().visit(this, arg))
		{

			Type targetType = symbolTable.Search(name).getType();
			Type decType = declaration.getExpr().getType();
			if(targetType!=IMAGE){
				if(targetType==INT && (decType == FLOAT || decType == COLOR)  ||
						targetType==FLOAT && decType == INT||
						targetType==COLOR && decType == INT){

					declaration.getExpr().setCoerceTo(targetType);
				}
				if(declaration.getOp().getKind() == Kind.LARROW){
					System.out.println("dectype is "+decType);
					if(decType == STRING){
						declaration.getExpr().setCoerceTo(decType);
						readStatement =true;
					}
					else if(decType == CONSOLE){
						declaration.getExpr().setCoerceTo(targetType);
						readStatement =true;
					}
				}
				check(targetType == decType || targetType == declaration.getExpr().getCoerceTo() || readStatement, declaration, "Expression must be assignment compatible with target");
			}

//			if(coerced == false) {
//				throw new TypeCheckException("mismatched types");
//			}
		}


		return declaration.getType();
	}


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {		
		//TODO:  this method is incomplete, finish it.  

		//Save root of AST so return type can be accessed in return statements
		root = program;

		//Check Params
		List<NameDef> params = program.getParams();
		for (NameDef node : params) {

			node.visit(this, arg);


		}


		//Check declarations and statements
		List<ASTNode> decsAndStatements = program.getDecsAndStatements();

		for (ASTNode node : decsAndStatements) {

			node.visit(this, arg);

		}


		return program;
	}

	@Override
	public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
		
		String name = nameDef.getName();

//		System.out.println("namedef: " + name + " | Type: " + nameDef.getType());
//		System.out.println(symbolTable.map.size());


		if(symbolTable.map.containsKey(nameDef.getName()) == true)
		{
			throw new TypeCheckException("Cannot have two parameters of the same name");
		}

		//Since its a parameter, it is initialized with an incoming value
		nameDef.setInitialized(true);
		symbolTable.map.put(nameDef.getName(), nameDef);

		//throw new UnsupportedOperationException();

		return nameDef.getType();
	}

	@Override
	public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
		//TODO:  implement this method
		throw new UnsupportedOperationException();
	}
 
	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {
//		System.out.println("returnging");
		Type returnType = root.getReturnType();  //This is why we save program in visitProgram.
		Type expressionType = (Type) returnStatement.getExpr().visit(this, arg);

//		System.out.println("expression type: " + expressionType);
//
//		System.out.println("returnStatement.getExpr().getType: " + returnStatement.getExpr().getType());
//		System.out.println("returnExpr: " + returnStatement.getExpr());
//		System.out.println("returnType: " + returnType);

		check(returnType == expressionType, returnStatement, "return statement with invalid type");
		//////////////////////////

		/////////////////////////
		return null;
	}

	@Override
	public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
		Type expType = (Type) unaryExprPostfix.getExpr().visit(this, arg);
		check(expType == Type.IMAGE, unaryExprPostfix, "pixel selector can only be applied to image");
		unaryExprPostfix.getSelector().visit(this, arg);
		unaryExprPostfix.setType(Type.INT);
		unaryExprPostfix.setCoerceTo(COLOR);
		return Type.COLOR;
	}

}
