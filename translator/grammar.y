%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dynamicarray.h"
#include "Symbol_Table/symbol_table.h"

int yylex(void);
void yyerror(char *);
extern int ppropid;
extern int cpropid;

%}

%start domain_description_or_query

%token CAUSESONEOF
%token INITIALLYONEOF
%token PERFORMEDAT
%token TAKESVALUES
%token WITHPROB
%token IFHOLDS
%token HOLDSWITHPROB
%token CONDITIONEDON
%token VARIABLE
%token '@'
%token '(' ')'
%token '{' '}'
%token ',' ';'
%token '='
%token '1'

%token <string> OBJECT
%token <string> FRACTION
%token <int_val> INTEGER

%union {
	TwoDimensionalArray pairs_array;
	Array string_array;
	int int_val;
	double double_val;
	char *string;
};

%type <int_val> instant
%type <string> prob
%type <pairs_array> list_pairs;
%type <string_array> pair;
%type <string_array> list_objects;
%type <string_array> list_assignments;
%type <string_array> nonempty_list_assignments;
%type <string> assignment;

%%

domain_description_or_query:
	query
	|
	domain_description
;

domain_description:
	/*empty*/
	|
	statement domain_description
;

query:
	hprop {
		printf("(anglican.emit/defm conditioning-i-formula [w] true)\n");
	}
	|
	hprop condition
	|
	hprop query
;

condition:
	CONDITIONEDON '{' nonempty_list_assignments '}' '@' INTEGER {
		printf("(anglican.emit/defm conditioning-i-formula [w] (and ");
		for (int i=0; i<$3.used; i++) {
			printf("(= (list (get-in (nth w %d) (keys %s))) (vals %s)) ", $6, $3.array[i], $3.array[i]);
		}
		printf("))\n");
	}

hprop:
	'{' nonempty_list_assignments '}' '@' INTEGER HOLDSWITHPROB VARIABLE {
			printf("(anglican.emit/defm i-formula [w] (and ");
			for (int i=0; i<$2.used; i++) {
				printf("(= (list (get-in (nth w %d) (keys %s))) (vals %s)) ", $5, $2.array[i], $2.array[i]);
			}
			printf("))\n");
	}
	|
	'{' nonempty_list_assignments '}' '@' '1' HOLDSWITHPROB VARIABLE {
			printf("(anglican.emit/defm i-formula [w] (and ");
			for (int i=0; i<$2.used; i++) {
				printf("(= (list (get-in (nth w 1) (keys %s))) (vals %s)) ", $2.array[i], $2.array[i]);
			}
			printf("))");
	}

instant:
	'1' { $$ = 1; }
	|
	INTEGER { $$ = $1; }
;

prob:
	'1' {
		$$ = (char *)malloc(9*sizeof(char));
		sprintf($$, "1");
	}
	|
	FRACTION {
		$$ = (char *)malloc(sizeof(char)*strlen($1));
		$$ = strdup($1);
	}
;

list_objects:
	list_objects ',' OBJECT {
								$$=$1;
								insertArray(&$$, $3);
							}
	|
	OBJECT 	{
				initArray(&$$,0);
				insertArray(&$$, $1);
	}

;

statement:
	takesvalues
	|
	performed
	|
	causes
	|
	initially
;

takesvalues:
	OBJECT TAKESVALUES '{' list_objects '}' {

												if (!isValidPosition(lookup($1))) {
													addSymbol($1,'f');
												}
												else
												{
													setType(lookup($1),'f');
												}

												printf("(alter-var-root #\'domain-language #(assoc %% :fluents (conj (:fluents domain-language) :%s)))\n",$1);

												// Don't care about values fluents may take
												//for (int i=0; i<$4.used; i++) {
												//	printf("possVal(%s, %s). ", $1, $4.array[i] );
												//}
												//printf("\n");
											}
;

performed:
	performed_full_form | performed_shorthand1 | performed_shorthand2 | performed_shorthand3
;

performed_full_form:
	OBJECT PERFORMEDAT instant WITHPROB prob IFHOLDS '{' nonempty_list_assignments '}'
	{
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :subject] :%s))\n", ppropid, $1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :instant] %d))\n", ppropid, $3);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"true\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) %s))))\n",ppropid,ppropid,$5);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"false\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) (- 1 %s)))))\n",ppropid,ppropid,$5);

		// PRECONDITION
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :precondition] (fn [state] (and ", ppropid);

		for (int i=0; i<$8.used; i++) {
			printf("(= (list (get-in state (keys %s))) (vals %s)) ", $8.array[i], $8.array[i]);
		}
		printf("))))\n");

		ppropid++;
	}
;

performed_shorthand1:
	OBJECT PERFORMEDAT instant WITHPROB prob {
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :subject] :%s))\n", ppropid, $1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :instant] %d))\n", ppropid, $3);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"true\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) %s))))\n",ppropid,ppropid,$5);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"false\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) (- 1 %s)))))\n",ppropid,ppropid,$5);

		// PRECONDITION
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :precondition] (fn [state] true)))\n",ppropid);

		ppropid++;
	}
;

performed_shorthand2:
	OBJECT PERFORMEDAT instant IFHOLDS '{' nonempty_list_assignments '}' {
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :subject] :%s))\n", ppropid, $1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :instant] %d))\n", ppropid, $3);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"true\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) 1))))\n",ppropid,ppropid);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"false\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) 0))))\n",ppropid,ppropid);

		// PRECONDITION
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :precondition] (fn [state] (and ", ppropid);

		for (int i=0; i<$6.used; i++) {
			printf("(= (list (get-in state (keys %s))) (vals %s)) ", $6.array[i], $6.array[i]);
		}
		printf("))))\n");

		ppropid++;
	}
;

performed_shorthand3:
	OBJECT PERFORMEDAT instant {
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :subject] :%s))\n", ppropid, $1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :instant] %d))\n", ppropid, $3);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"true\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) 1))))\n",ppropid,ppropid);

		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :outcomes] (vec (conj (get-in %% [:pprops %d :outcomes]) {:%s \"false\"}))))\n",ppropid,ppropid,$1);
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :probs] (vec (conj (get-in %% [:pprops %d :probs]) 0))))\n",ppropid,ppropid);

		// PRECONDITION
		printf("(alter-var-root #\'domain-description #(assoc-in %% [:pprops %d :precondition] (fn [state] true)))\n",ppropid);

		ppropid++;
	}
;

causes:
	'{' nonempty_list_assignments '}' CAUSESONEOF '{' list_pairs '}'
									{
									// Initialise iprop
									printf("(alter-var-root #\'domain-description #(assoc-in %% [:cprops %d :outcomes] []))\n",cpropid);

									//PRECONDITION
									printf("(alter-var-root #\'domain-description #(assoc-in %% [:cprops %d :precondition] (fn [state] (and ", cpropid);

									for (int k=0; k<$2.used; k++) {
										printf("(= (list (get-in state (keys %s))) (vals %s)) ", $2.array[k], $2.array[k]);
									}
									printf("))))\n");

									//OUTCOMES
									for (int i=0; i<$6.used; i++) {
											printf("(alter-var-root #\'domain-description #(assoc-in %% [:cprops %d :outcomes %d] (merge ",cpropid,i);
											for (int j=0; j<$6.array[i].used; j++) {
												printf("%s ",$6.array[i].array[j]);
											}
											printf(")))\n");

											printf("(alter-var-root #\'domain-description #(assoc-in %% [:cprops %d :probs] (vec (conj (get-in %% [:cprops %d :probs]) %s))))\n", cpropid, cpropid, $6.array[i].probability);

									}

										cpropid++;
									}
;

initially:
	INITIALLYONEOF '{' list_pairs '}'	{

										// Initialise iprop
										printf("(alter-var-root #\'domain-description #(assoc-in %% [:iprop :outcomes] []))\n");

										for (int i=0; i<$3.used; i++) {
											printf("(alter-var-root #\'domain-description #(assoc-in %% [:iprop :outcomes %d] (merge ",i);
											for (int j=0; j<$3.array[i].used; j++) {
												printf("%s ",$3.array[i].array[j]);
											}
											printf(")))\n");

											printf("(alter-var-root #\'domain-description #(assoc-in %% [:iprop :probs] (vec (conj (get-in %% [:iprop :probs]) %s))))\n", $3.array[i].probability);
										}
									}
;

list_pairs:
	pair ',' list_pairs { $$=$3; insert2DArray(&$$, $1); }
	|
	pair { init2DArray(&$$,0); insert2DArray(&$$, $1); }
;

pair:
	'(' '{' list_assignments '}' ',' prob ')' 	{
														$$=$3;
														$$.probability = $6;
													}
;

list_assignments:
	/* empty */	{
								initArray(&$$,0);
								char *aux;
								aux = (char *) malloc(sizeof(char) * 2);
								sprintf(aux, "{}");
								insertArray(&$$,strdup(aux));
								free(aux);
							}
	|
	nonempty_list_assignments	{
											$$=$1;
										}
;

nonempty_list_assignments:
	assignment	{
								initArray(&$$,0);
								insertArray(&$$, $1);
							}
	|
	assignment ',' nonempty_list_assignments {
												$$=$3;
												insertArray( &$$, $1 );
											}
;

assignment:
	OBJECT '=' OBJECT	{
							if (!isValidPosition(lookup($1))) {
								addSymbol($1,'a');
							}

							char *aux;
							aux = (char *) malloc(sizeof(char) * (strlen($1) + strlen($3) + 6));
							sprintf(aux, "{:%s \"%s\"}", $1, $3);
							$$ = strdup(aux);
							free(aux);
						}
;

%%
