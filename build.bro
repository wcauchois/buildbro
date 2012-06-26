{project sample "A sample project"
  [macro (object-file name)
    (let ((target-name (string->symbol (string-append "compile-" name "-o")))
          (source-name (string-append name ".c"))
          (object-name (string-append name ".o")))
      `(target ,target-name
         (depends ,source-name) (creates ,object-name)
         (! ,(string-append "gcc -c " source-name))))
  ]
  [macro (object-file-target name)
    (string->symbol (string-append "compile-" name "-o"))
  ]
  (object-file "main")
  [target compile (depends (object-file-target "main")) (creates "foo")
    (! "gcc -o foo main.o")
  ]
  [target run (depends compile) (creates)
    (! "./foo")
  ]
}
