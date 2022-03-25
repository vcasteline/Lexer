package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.Declaration;
import edu.ufl.cise.plc.ast.Types;

import java.util.HashMap;

import static edu.ufl.cise.plc.ast.Types.Type.*;
import static edu.ufl.cise.plc.ast.Types.Type.BOOLEAN;

public class SymbolTable {

    SymbolTable(){
        makeMap();
    }

//TODO:  Implement a symbol table class that is appropriate for this language. 
    HashMap<String, Declaration> map = new HashMap <String, Declaration>();
    HashMap<TypeCheckVisitor.Pair<Types.Type, Types.Type>, Types.Type> BinaryExprs = new HashMap<TypeCheckVisitor.Pair<Types.Type, Types.Type>, Types.Type>();


    public Declaration Search(String key)
{
    return map.get(key);
}

void makeMap() {
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(BOOLEAN, BOOLEAN), BOOLEAN);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(FLOAT, FLOAT), FLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(INT, INT), INT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(INT, FLOAT), FLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(FLOAT, INT), FLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLOR, COLOR), COLOR);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLORFLOAT, COLORFLOAT), COLORFLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLORFLOAT, COLOR), COLORFLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLOR, COLORFLOAT), COLORFLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(IMAGE, IMAGE), IMAGE);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(IMAGE, INT), IMAGE);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(IMAGE, FLOAT), IMAGE);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLOR, INT), COLOR);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(FLOAT, COLOR), COLORFLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(COLOR, FLOAT), COLORFLOAT);
    BinaryExprs.put(new TypeCheckVisitor.Pair<Types.Type, Types.Type>(INT, BOOLEAN), BOOLEAN);
}

Types.Type checkMap (Types.Type left, Types.Type right)
{
    return BinaryExprs.get(new TypeCheckVisitor.Pair<Types.Type,Types.Type>(left,right));
}
}
