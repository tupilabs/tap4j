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
	header?  plan body footer EOF
	;

// HEADER

header
  :
  (comment)* version? NEWLINE
  ;
 
version
  :
  VERSION tapProtocolVersion=POSITIVE_INTEGER (comment)* EOF
  ; 

comment
  :
  SHARP (SHARP|STRING)* (NEWLINE | EOF)
  ;

// PLAN

plan
  :
  //(comment)* ( planSimple | planTodo | planSkipAll )
  (comment)* ( planSimple | planSkipAll ) EOF
  ;

planSimple
  :
  STRING+ ( NEWLINE | EOF )
  ;

planSkipAll
  :
  PLAN_SKIP_PREFIX SKIP reason (comment)* EOF
  ;

reason
  :
  STRING+ EOF
  ;
// !!! OBSOLETE
//planTodo
//  :
//  planSimple TODO (comment)* NEWLINE
//  ;

// BODY
body 
	:	
	(comment)* (testResult)? NEWLINE
	;
	
testResult
	:	status POSITIVE_INTEGER? description? EOF;
	
description
	:	STRING+ EOF;
	
status 
	:	(OK | NOT_OK) EOF;
	
footer
	:	comment? EOF;

// EXTRA TOKENS

POSITIVE_INTEGER
  :
  '1'..'9'+ ('.' '0'..'9'+)? EOF
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
    : '\r' | '\n' {$channel=HIDDEN;}
    ;

WS
    : (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;} 
    ;


