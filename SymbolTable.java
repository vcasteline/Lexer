package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.Declaration;

import java.util.HashMap;

public class SymbolTable {

//TODO:  Implement a symbol table class that is appropriate for this language. 
public static HashMap<String, Declaration> map = new HashMap <String, Declaration>();

public static Declaration Search(String key)
{
    return map.get(key);
}
}
