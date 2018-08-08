%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "dynamicarray.h"
#include "grammar.tab.h" //Bison generated header file
#include "Symbol_Table/symbol_table.h" //Symbol table

extern YYSTYPE yylval;
int cpropid=0;
int ppropid=0;
int maxinstant=0;
int line=0;

int yyparse (void);
%}

%option noyywrap
%x comment

%%

"//"				BEGIN(comment);
<comment>[^\n]*		//Eats comments
<comment>[\n]		{ line++; BEGIN(INITIAL); }

conditioned-on return CONDITIONEDON;
holds-with-prob	return HOLDSWITHPROB;
causes-one-of		return CAUSESONEOF;
initially-one-of	return INITIALLYONEOF;
performed-at		return PERFORMEDAT;
takes-values		return TAKESVALUES;
with-prob			return WITHPROB;
if-holds			return IFHOLDS;
[1{}(),;=@]			return *yytext;

[0-9]+			{
							yylval.int_val = atoi(yytext);

							if (maxinstant < yylval.int_val)
							{
								maxinstant = yylval.int_val;
							}
							return INTEGER;
						}

[A-Z] {
	return VARIABLE;
}

[a-z][a-zA-Z0-9]*	{
						yylval.string = strdup(yytext);

						return OBJECT;
}

[0-9]+"/"[0-9]+			{
							char *aux;
							int i=0;

							aux=malloc(sizeof(char)*(4+strlen(yytext)));
							sprintf(aux, "(/ %s)", yytext);

							while (aux[i] != '/')
								i++;

							i++;

							while (aux[i] != '/')
								i++;

							aux[i] = ' ';

							yylval.string = strdup(aux);

							free(aux);

							return FRACTION;
						}

[ \t\r]				;
\n 					line++;

%%

void yyerror (char *string) { printf("LEXER ERROR ON LINE %d: %s.\n",line,string ); }

int main(int argc, char **argv) {

	// If option -q or --query is specified, then
	// translator does not add preamble
	if ((argc>1)&&((strcmp(argv[1],"-q")==0)||(strcmp(argv[1],"--query")==0))) {
		yyparse();
	}
	else {
		// Preamble
		printf("(def domain-language {})\n");
		printf("(def domain-description {})\n");
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:cprops] []))\n");
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops] []))\n");

		yyparse();

		// Looks for actions
		for (int i=0; isValidPosition(i); i++) {
			if (getType(i)=='a') {
				printf("(alter-var-root #\'domain-language #(assoc %% :actions (conj (:actions domain-language) :%s)))\n", getName(i));
			}
		}

		printf("(alter-var-root #\'domain-language #(assoc %% :maxinst %d))\n", maxinstant+1);
	}

	return 0;
}
