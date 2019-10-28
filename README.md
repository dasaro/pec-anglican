# PEC-Ang

Implementation of PEC+ in [Anglican](http://www.robots.ox.ac.uk/~fwood/anglican/language/).

## Installation

Needs jvm and leiningen installed to be run. To compile the translator Flex and Bison are also needed. Use the following command to start the compilation:

```sh
$ gcc dynamicarray.c lex.yy.c grammar.tab.c Symbol_Table/symbol_table.c -o linux_bin/translator
```

## Usage

First translate the domain description to a *.clj* file, **making sure you use the `-q` flag when translating the query**, e.g.:

```sh
$ ./translator/linux_bin/translator < Examples/antibiotic/antibiotic.pec > Examples/antibiotic/antibiotic.clj
$ /translator/linux_bin/translator -q < Examples/antibiotic/query.pec > Examples/antibiotic/query.clj
```

Then run using e.g.:

```sh
$ lein run Examples/antibiotic/antibiotic.clj -q Examples/antibiotic/query.pec -n 500
```

