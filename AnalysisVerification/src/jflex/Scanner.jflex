/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;



/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

    /*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine()    { return yyline + 1; }
	public int getCharPos() { return yycolumn;   }
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/

WhiteSpace = [\t ]
ID = [a-zA-Z][a-zA-Z0-9_]*
INTEGER	= 0 | [1-9][0-9]*

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {
    "("				{ return symbol(sym.LPAREN); }
    ")"				{ return symbol(sym.RPAREN); }
    "?"             { return symbol(sym.ANY_INT); }
    ":="			{ return symbol(sym.ASSIGN); }
    "="             { return symbol(sym.EQUALS); }
    "!="            { return symbol(sym.NEQUALS); }
    "+ 1"           { return symbol(sym.INCREMENT); }
    "- 1"           { return symbol(sym.DECREMENT); }
    "assume"		{ return symbol(sym.ASSUME); }
    "assert"		{ return symbol(sym.ASSERT); }
    "skip"		    { return symbol(sym.SKIP); }
    "TRUE"			{ return symbol(sym.TRUE); }
    "FALSE"			{ return symbol(sym.FALSE); }
    "ODD"           { return symbol(sym.ODD); }
    "EVEN"          { return symbol(sym.EVEN); }
    "SUM"			{ return symbol(sym.SUM); }
    {WhiteSpace}+   {/* do nothing */}
    {ID}			{ return symbol(sym.ID, new String(yytext())); }
    {INTEGER}		{ return symbol(sym.NUMBER, Integer.parseInt(yytext())); }
    .               { throw new java.lang.Error(); }
}