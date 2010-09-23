grammar TAP;

options {
     language = Java;
}

@header {
     package br.eti.kinoshita.tap4j;
     
     import java.util.List;
     import java.util.ArrayList;
}

@lexer::header
{
	package br.eti.kinoshita.tap4j;
}

@members
{
	protected final List<String> commentsList = new ArrayList<String>();
}

tapFile
	:
	header
	;

header
	:
		(comment|version)* 
	;

comment
	:
	'#' value=STRING { this.commentsList.add($value.getText()); }
	;

version : 'TAP' {System.out.println("Achou a versao!");};

STRING 	: ('a'..'z' | 'A'..'Z' | ' ')+ ;
NUMBER  : ('0'..'9')+;
//WS : (' ' |'\t' | '\r' | '\n' | '\f')+ {skip();} ;
WS
  : ( ' '
    | '\t'
    | '\f'

    // handle newlines
    | ( '\r\n'  // DOS/Windows
      | '\r'    // Macintosh
      | '\n'    // Unix
      )
      // increment the line count in the scanner
      { System.out.println("Whoopie!");  }
    )
    { $channel=HIDDEN; }
  ;
