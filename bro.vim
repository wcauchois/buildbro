" Vim Syntax File

if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

syntax case match

syn keyword xTopLevel project
syn keyword xMacro macro
syn keyword xTarget target
syn keyword xCommands ! mv
syn keyword xForms let quasiquote unquote
syn keyword xFunctions string->symbol string-append

syntax region xString start=/"/ skip=/\\./ end=/"/
syntax region xString start=/"""/ skip=/\\./ end=/"""/

let b:current_syntax = "bro"

hi def link xTopLevel Type
hi def link xMacro Keyword
hi def link xTarget Keyword
hi def link xForms Statement
hi def link xFunctions Statement
hi def link xCommands PreProc
hi def link xString String
