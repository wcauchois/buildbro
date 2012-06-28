Introduction
============

BuildBro is a simple build tool, much like Make, that manages the dependencies between sets of executable commands called _targets_. Targets are specified using a symbolic expression-based language in a file called `build.bro`. To illustrate, here is a simple brofile:

    {project hello-world "Description goes here"
      [target compile (depends "main.c") (creates foo)
        (! "gcc -o foo main.c")
      ]
      [target run (depends compile) (creates)
        (! "./foo")
      ]
    }

Note that \[\] and \{\} may also be used to delimit lists for improved readability.

Syntax
======

The syntax for brofiles is as follows:

    <project>             ::= (project <project-name> <project-description> <targets>)
    <name>                ::= <ATOM>
    <project-description> ::= <STRING>
    <targets>             ::= <target> <targets> | ε
    <target>              ::= (target <target-name> <depends-clause> <creates-clause> <commands>)
    <target-name>         ::= <ATOM>
    <depends-clause>      ::= (depends <depends-list>)
    <depends-list>        ::= <depends-target> <depends-list> | <depends-file> <depends-list> | ε
    <depends-target>      ::= <ATOM>
    <depends-file>        ::= <STRING>
    <creates-clause>      ::= (creates <creates-list>)
    <creates-list>        ::= <creates-file> <creates-list> | ε
    <creates-file>        ::= <STRING>
    <commands>            ::= <command> <commands> | ε
    <command>             ::= (<command-name> <command-args>)
    <command-name>        ::= ! | mv
    <command-args>        ::= <command-arg> <command-args> | ε
    <command-arg>         ::= <STRING> | <ATOM>

