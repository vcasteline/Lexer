package edu.ufl.cise.plc;

public class StringHelper {

    StringBuilder str;

    StringHelper()
    {
        str = new StringBuilder();
    }

    StringBuilder comma()
    {
        return str.append(',');
    }

}
