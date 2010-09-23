grammar TAP;

options {
     language = Java;     
}

tokens 
{
  VERSION='TAP version';
  PLAN_SKIP_PREFIX='1..0';
  TODO='todo';
  SKIP='skip';
  OK='ok';
  NOT_OK='not ok';
  SKIP_DIRECTIVE='SKIP';
  TODO_DIRECTIVE='TODO';
  BAIL_OUT='Bail out!';
  SHARP='#';
  SEMI=';';
  PLAN_PREFIX='1..';
  ONE='1';
  DOT='.';
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
	header? plan EOF
	;

// HEADER

header
  :
  (comment)* version? NEWLINE
  ;
 
version
  :
  VERSION tapProtocolVersion=POSITIVE_INTEGER (comment)*
  ; 

comment
  :
  SHARP (SHARP|STRING)* NEWLINE
  ;

// PLAN

plan
  :
  (comment)* ( planSimple | planTodo | planSkipAll )
  ;

planSimple
  :
  STRING+
  ;

planSkipAll
  :
  PLAN_SKIP_PREFIX SKIP reason (comment)*
  ;

reason
  :
  STRING+
  ;
// !!! OBSOLETE
planTodo
  :
  planSimple TODO (comment)*
  ;

// EXTRA TOKENS

POSITIVE_INTEGER
  :
  ('1'..'9') ('0'..'9')*
  ;

INTEGER
  :
  ('0'..'9')+
  ;
  
STRING
  :
  ('a'..'z'|'A'..'Z'|'0'..'9'|'.')+
  ;

NEWLINE
    : '\r' | '\n' {skip();}
    ;

WS
    : (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;} 
    ;


