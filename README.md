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

Here within each target the `!` command is used to execute a shell command. Note that \[\] and \{\} may also be used to delimit lists for improved readability.

Additionally, BuildBro provides basic support for Lisp-style macros.

Syntax
======

The syntax for brofiles (modulo macros) is as follows:

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

Macros
======

Syntax and Example
------------------

Macros must be defined at the beginning of the brofile, immediately before any targets. The syntax for a macro directive is as follows:

    <macro>         ::= (macro (<macro-name> <macro-formals>) <macro-body>)
    <macro-name>    ::= <ATOM>
    <macro-formals> ::= <macro-formal> <macro-formals> | ε
    <macro-formal>  ::= <ATOM>
    <macro-body>    ::= <EXPR>

Here is an example of a simple macro:

    {project macro-sample "Example usage of a simple macro"
      [macro (object-file name)
        `(target ,(string->symbol (string-append "compile-" name))
           (depends ,(string-append name ".c")) (creates ,(string-append name ".o"))
           (! ,(string-append "gcc -c " name ".c")))
      ]
      (object-file "main")
    }

After macro expansion, the brofile looks like this:

    {project macro-sample "Example usage of a simple macro"
      (target compile-main
        (depends "main.c") (creates "main.o")
        (! "gcc -c main.c"))
    }

Expansion Semantics
-------------------

A macro represents a set of rules for transforming some arguments into a symbolic expression that will be substituted for each instantiation of the macro within the brofile. In the grammar above, `<EXPR>` stands for some expression that must evaluate to an atom, string, number, or list. Before BuildBro attempts to parse the project file, the macros are extracted and a process known as macro expansion occurs. Each instance of `(<macro> <args>)` will be replaced by the result of evaluating that macro with those arguments.

Evaluation Semantics
--------------------

Macro expressions in BuildBro consist of either self-evaluating expressions, standard forms, and function applications.

 + **Self-evaluating expressions**, such as strings and numbers, will always evaluate to themselves.

        "A string" => "A string"
        22.0 => 22.0

 + **Standard forms**, such as `let` and `quasiquote`, allow for control flow, variable binding, and quotation (which prevents the evaluation of a list or other expression).

        (let ((x "Hi") (y "World")) (string-append x y)) => "HiWorld"
        (quasiquote (this is a list)) => (this is a list)
        (let ((the-answer 42)) (quasiquote (the answer is (unquote the-answer)) => (the answer is 42)

 + **Function applications** allow builtin functions to be invoked with some (evaluated) arguments.

        (string-append "Hi" "World") => "HiWorld"
        (symbol->string "hi-world") => hi-world

Reference
---------

### Standard Forms

 + `(quasiquote expr)` will prevent the evaluation of a list or other expression. Additionally, each instance of `(unquote expr)` within a quasiquotation will be replaced with the result of evaluating the expression in the current environment.

 + `(let ((name1 value1) (name2 value2) ...) expr)` will bind `name1` to `value1` and `name2` to `value2` (et cetera) while evaluating the specified expression.

### Builtin Functions

 + `(string-append . args)` will append all of its string arguments together.

 + `(string->symbol str)` will convert a string into an atom.

